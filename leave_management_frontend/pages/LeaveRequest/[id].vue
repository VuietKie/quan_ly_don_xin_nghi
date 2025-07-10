<template>
  <MainTemplate>
    <template #header>
      <Breadcrumb>
        <BreadcrumbList>
          <BreadcrumbItem class="hidden md:block">
            <BreadcrumbLink href="/">Trang chủ</BreadcrumbLink>
          </BreadcrumbItem>
          <BreadcrumbSeparator class="hidden md:block" />
          <BreadcrumbItem class="hidden md:block">
            <BreadcrumbLink href="/leave-request/list">Quản lý đơn xin nghỉ</BreadcrumbLink>
          </BreadcrumbItem>
          <BreadcrumbSeparator class="hidden md:block" />
          <BreadcrumbItem>
            <BreadcrumbPage>Chỉnh sửa đơn xin nghỉ</BreadcrumbPage>
          </BreadcrumbItem>
        </BreadcrumbList>
      </Breadcrumb>
    </template>
    <div class="flex flex-1 flex-col gap-4 p-4 pt-0">
      <h1 class="text-2xl font-bold">Chỉnh sửa đơn xin nghỉ</h1>
      <div v-if="loading" class="text-center text-gray-500">Đang tải dữ liệu...</div>
      <form v-else @submit.prevent="submitForm" class="flex justify-center items-center min-h-[60vh] bg-gray-50">
        <div class="bg-white shadow-xl rounded-2xl p-8 w-full max-w-lg">
          <div class="flex items-center mb-6 gap-2">
            <svg xmlns="http://www.w3.org/2000/svg" class="h-8 w-8 text-blue-500" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z" /></svg>
            <h2 class="text-2xl font-bold text-gray-800">Chỉnh sửa đơn xin nghỉ</h2>
          </div>
          <div class="space-y-6">
            <div>
              <label class="block mb-1 font-semibold text-gray-700">Ngày bắt đầu</label>
              <input type="date" v-model="form.start_date" required class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-400 focus:outline-none text-gray-700" disabled />
            </div>
            <div>
              <label class="block mb-1 font-semibold text-gray-700">Ngày kết thúc</label>
              <input type="date" v-model="form.end_date" required class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-400 focus:outline-none text-gray-700" disabled />
            </div>
            <div>
              <label class="block mb-1 font-semibold text-gray-700">Lý do</label>
              <textarea v-model="form.reason" required rows="4" placeholder="Nhập lý do xin nghỉ..." class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-400 focus:outline-none text-gray-700 resize-none" disabled />
            </div>
            <div>
              <label class="block mb-1 font-semibold text-gray-700">Trạng thái</label>
              <select v-model="form.status" :disabled="!canEdit" class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-400 focus:outline-none text-gray-700">
                <option value="Inprogress">Chờ duyệt</option>
                <option value="Approved">Đã duyệt</option>
                <option value="Rejected">Từ chối</option>
              </select>
            </div>
            <div v-if="form.status !== 'Inprogress'">
              <label class="block mb-1 font-semibold text-gray-700">Lý do xử lý (nếu từ chối)</label>
              <template v-if="canEdit">
                <textarea v-model="form.processedReason" :required="form.status==='Rejected'" rows="2" placeholder="Nhập lý do xử lý..." class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-400 focus:outline-none text-gray-700 resize-none" />
              </template>
              <template v-else>
                <div class="min-h-[40px] px-4 py-2 border border-gray-200 rounded-lg bg-gray-50 text-gray-700">
                  {{ form.processedReason || 'Không có lý do xử lý' }}
                </div>
              </template>
            </div>
            <div class="flex flex-col gap-2 sm:flex-row sm:justify-end mt-6">
              <button v-if="canEdit" type="submit" class="w-full sm:w-60 py-3 bg-blue-500 hover:bg-blue-600 text-white font-bold rounded-lg shadow-md transition">Lưu thay đổi</button>
              <button type="button" @click="goBack" class="w-full sm:w-60 py-3 bg-red-500 hover:bg-red-600 text-white font-bold rounded-lg shadow-md transition">Thoát</button>
            </div>
          </div>
        </div>
      </form>
    </div>
  </MainTemplate>
</template>

<script setup lang="ts">
import { ref, onMounted, h } from 'vue'
import { useToast } from 'vue-toastification'
import { useRuntimeConfig, useCookie, useRoute, useRouter } from '#app'
import MainTemplate from '~/components/layouts/MainTemplate.vue'
import Breadcrumb from '@/components/ui/breadcrumb/Breadcrumb.vue'
import BreadcrumbList from '@/components/ui/breadcrumb/BreadcrumbList.vue'
import BreadcrumbItem from '@/components/ui/breadcrumb/BreadcrumbItem.vue'
import BreadcrumbLink from '@/components/ui/breadcrumb/BreadcrumbLink.vue'
import BreadcrumbSeparator from '@/components/ui/breadcrumb/BreadcrumbSeparator.vue'
import BreadcrumbPage from '@/components/ui/breadcrumb/BreadcrumbPage.vue'

const route = useRoute()
const router = useRouter()
const toast = useToast()
const config = useRuntimeConfig()
const token = useCookie('access_token')
const loading = ref(true)

const form = ref({
  start_date: '',
  end_date: '',
  reason: '',
  status: 'Inprogress',
  processedReason: '',
})

const userId = ref<number|null>(null)
const canEdit = ref(false)

function parseJwt(tokenStr: string) {
  try {
    const base64Url = tokenStr.split('.')[1]
    const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/')
    const jsonPayload = decodeURIComponent(atob(base64).split('').map(function(c) {
      return '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2)
    }).join(''))
    return JSON.parse(jsonPayload)
  } catch (e) {
    return null
  }
}

onMounted(() => {
  if (token.value) {
    const payload = parseJwt(token.value)
    if (payload && (payload.userId || payload.user_id)) {
      userId.value = payload.userId || payload.user_id
    }
  }
  fetchLeaveRequest()
})

const fetchLeaveRequest = async () => {
  loading.value = true
  try {
    const response = await $fetch<any>(`${config.public.apiBase}/leave-request/${route.params.id}`, {
      headers: { Authorization: `Bearer ${token.value}` },
    })
    form.value = {
      start_date: response.startDate || '',
      end_date: response.endDate || '',
      reason: response.reason || '',
      status: response.status || 'Inprogress',
      processedReason: response.processedReason || '',
    }
    // Kiểm tra quyền chỉnh sửa
    await checkCanEdit()
  } catch (error) {
    console.error('Lỗi khi lấy thông tin đơn xin nghỉ:', error)
    toast.error('Không tìm thấy thông tin đơn xin nghỉ!')
    router.push('/leave-request/my-request')
  } finally {
    loading.value = false
  }
}

const checkCanEdit = async () => {
  if (!userId.value) {
    canEdit.value = false
    return
  }
  try {
    const myRequests = await $fetch<any[]>(`${config.public.apiBase}/leave-request/my-request?userId=${userId.value}`, {
      headers: { Authorization: `Bearer ${token.value}` },
    })
    // Nếu đơn hiện tại nằm trong danh sách đơn của user => chỉ được xem
    const duplicate = myRequests.some(r => {
      // Có thể là requestId hoặc id tùy backend trả về
      return r.requestId == route.params.id || r.id == route.params.id
    })
    canEdit.value = !duplicate
  } catch (e) {
    canEdit.value = false
  }
}

const submitForm = async () => {
  try {
    const body: any = {
      status: form.value.status,
    }
    if (form.value.processedReason) {
      body.processedReason = form.value.processedReason
    }
    if (userId.value) {
      body.processedById = userId.value
    }
    await $fetch(`${config.public.apiBase}/leave-request/${route.params.id}`, {
      method: 'PATCH',
      headers: {
        'Content-Type': 'application/json',
        Authorization: `Bearer ${token.value}`,
      },
      body,
    })
    toast.success('Cập nhật đơn xin nghỉ thành công!')
    await fetchLeaveRequest()
  } catch (error) {
    console.error('Lỗi khi cập nhật đơn xin nghỉ:', error)
    toast.error('Cập nhật thất bại!')
  }
}

const goBack = () => {
  if (!canEdit.value) {
    router.push('/leave-request/my-request')
    return
  }
  toast.warning(
    h('div', { class: 'space-y-2' }, [
      h('p', 'Bạn có chắc chắn muốn thoát không? Dữ liệu chưa lưu sẽ bị mất.'),
      h(
        'button',
        {
          onClick: () => {
            toast.clear()
            router.push('/leave-request/my-request')
          },
          class: 'px-4 py-2 bg-red-500 text-white rounded hover:bg-red-700 transition',
        },
        'Thoát'
      ),
    ]),
    { timeout: false, closeOnClick: false }
  )
}
</script>

<style>
</style>