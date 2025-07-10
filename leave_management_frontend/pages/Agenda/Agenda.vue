<template>
  <MainTemplate>
    <template #header>
      <Breadcrumb>
        <BreadcrumbList>
          <BreadcrumbItem class="hidden md:block">
            <BreadcrumbLink href="/">Trang chủ</BreadcrumbLink>
          </BreadcrumbItem>
          <BreadcrumbSeparator class="hidden md:block" />
          <BreadcrumbItem>
            <BreadcrumbPage>Lịch công tác</BreadcrumbPage>
          </BreadcrumbItem>
        </BreadcrumbList>
      </Breadcrumb>
    </template>
    <div class="flex flex-1 flex-col gap-4 p-4 pt-0">
      <h1 class="text-2xl font-bold">Lịch công tác</h1>
      <!-- Nội dung chính của Agenda sẽ đặt ở đây -->
      <div class="flex flex-col md:flex-row gap-4 items-center mb-4">
        <div>
          <label class="block mb-1 font-semibold text-gray-700">Start Date</label>
          <input type="date" v-model="startDate" class="px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-400 focus:outline-none text-gray-700" />
        </div>
        <div>
          <label class="block mb-1 font-semibold text-gray-700">End Date</label>
          <input type="date" v-model="endDate" class="px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-400 focus:outline-none text-gray-700" />
        </div>
        <button @click="fetchAgenda" class="mt-6 md:mt-0 px-6 py-2 bg-blue-500 hover:bg-blue-600 text-white font-bold rounded-lg shadow-md transition">Tìm kiếm</button>
      </div>
      <div v-if="loading" class="text-center text-gray-500">Đang tải dữ liệu...</div>
      <div v-else>
        <table class="min-w-full bg-white border border-gray-300 rounded-xl overflow-hidden shadow">
          <thead class="bg-gray-100">
            <tr>
              <th class="px-4 py-2 text-left font-bold">Tên nhân viên</th>
              <th v-for="date in dateColumns" :key="date" class="px-4 py-2 text-center font-bold">{{ date }}</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="row in userRows" :key="row.user?.userId || row.user?.id || row.user?.username || row.user?.fullName" class="hover:bg-gray-50 transition">
              <td class="px-4 py-2 font-semibold border-r border-gray-200">{{ row.user?.fullName || 'Không rõ' }}</td>
              <td v-for="date in dateColumns" :key="date" class="px-2 py-2 text-center border-r border-gray-100">
                <template v-if="isWeekend(date)">
                  <div class="rounded-lg bg-blue-100 text-blue-700 font-semibold py-1 cursor-pointer group relative" :title="'Nghỉ cuối tuần'">
                    Nghỉ + Cuối tuần
                    <span class="absolute left-1/2 -translate-x-1/2 bottom-full mb-1 w-max max-w-xs bg-white text-xs text-gray-700 border border-gray-300 rounded shadow-lg px-2 py-1 opacity-0 group-hover:opacity-100 pointer-events-none z-10">Nghỉ cuối tuần</span>
                  </div>
                </template>
                <template v-else-if="getLeaveStatusForDate(row.requests, date)">
                  <div
                    class="rounded-lg bg-red-100 text-red-700 font-semibold flex flex-col items-center justify-center py-1 px-1 cursor-pointer group relative"
                    :title="getLeaveReasonForDate(row.requests, date)"
                  >
                    Nghỉ +
                    <span v-if="getLeaveStatusForDate(row.requests, date) === 'Approved'" class="text-green-600 font-semibold">Duyệt</span>
                    <span v-else-if="getLeaveStatusForDate(row.requests, date) === 'Inprogress'" class="text-yellow-600 font-semibold">Chưa duyệt</span>
                    <span v-else class="text-red-600 font-semibold">Không duyệt</span>
                    <span v-if="getLeaveReasonForDate(row.requests, date)" class="absolute left-1/2 -translate-x-1/2 bottom-full mb-1 w-max max-w-xs bg-white text-xs text-gray-700 border border-gray-300 rounded shadow-lg px-2 py-1 opacity-0 group-hover:opacity-100 pointer-events-none z-10">{{ getLeaveReasonForDate(row.requests, date) }}</span>
                  </div>
                </template>
                <template v-else>
                  <div class="rounded-lg bg-green-100 text-green-700 font-semibold py-1">Đi làm</div>
                </template>
              </td>
            </tr>
            <tr v-if="userRows.length === 0">
              <td :colspan="1 + dateColumns.length" class="text-center text-gray-500 py-4">Không có dữ liệu phù hợp</td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
  </MainTemplate>
</template>

<script setup lang="ts">
import MainTemplate from '~/components/layouts/MainTemplate.vue'
import Breadcrumb from '@/components/ui/breadcrumb/Breadcrumb.vue'
import BreadcrumbList from '@/components/ui/breadcrumb/BreadcrumbList.vue'
import BreadcrumbItem from '@/components/ui/breadcrumb/BreadcrumbItem.vue'
import BreadcrumbLink from '@/components/ui/breadcrumb/BreadcrumbLink.vue'
import BreadcrumbSeparator from '@/components/ui/breadcrumb/BreadcrumbSeparator.vue'
import BreadcrumbPage from '@/components/ui/breadcrumb/BreadcrumbPage.vue'
import { ref, computed } from 'vue'
import { useRuntimeConfig } from '#app'
import { useToast } from 'vue-toastification'

const startDate = ref('')
const endDate = ref('')
const agendaList = ref<any[]>([])
const loading = ref(false)
const config = useRuntimeConfig()
const toast = useToast()

const fetchAgenda = async () => {
  if (!startDate.value || !endDate.value) {
    toast.warning('Vui lòng chọn đủ ngày bắt đầu và kết thúc!')
    return
  }
  loading.value = true
  try {
    const res = await $fetch<any[]>(`${config.public.apiBase}/leave-request/filter-by-date?start_date=${startDate.value}&end_date=${endDate.value}`)
    agendaList.value = res
  } catch (e) {
    toast.error('Lỗi khi tải dữ liệu lịch công tác!')
    agendaList.value = []
  } finally {
    loading.value = false
  }
}

function getDatesInRange(start: string, end: string) {
  const arr = []
  const dt = new Date(start)
  const endDt = new Date(end)
  while (dt <= endDt) {
    arr.push(dt.toISOString().slice(0, 10))
    dt.setDate(dt.getDate() + 1)
  }
  return arr
}

const dateColumns = computed(() => {
  if (!startDate.value || !endDate.value) return []
  return getDatesInRange(startDate.value, endDate.value)
})

// Nhóm dữ liệu theo userId
const userRows = computed(() => {
  // Map userId => { user, requests: [LeaveRequest] }
  const map = new Map()
  for (const req of agendaList.value) {
    const userId = req.user?.userId || req.user?.id || req.userId || req.id
    if (!userId) continue
    if (!map.has(userId)) {
      map.set(userId, { user: req.user, requests: [] })
    }
    map.get(userId).requests.push(req)
  }
  return Array.from(map.values())
})

// Trả về status nếu có đơn xin nghỉ trùng ngày, ngược lại trả về null
function getLeaveStatusForDate(requests: any[], date: string) {
  for (const req of requests) {
    if (!req.startDate || !req.endDate) continue
    if (date >= req.startDate && date <= req.endDate) {
      return req.status
    }
  }
  return null
}

// Lấy lý do nghỉ cho ngày đó (nếu có)
function getLeaveReasonForDate(requests: any[], date: string) {
  for (const req of requests) {
    if (!req.startDate || !req.endDate) continue
    if (date >= req.startDate && date <= req.endDate) {
      return req.reason || ''
    }
  }
  return ''
}

// Kiểm tra ngày có phải thứ 7 hoặc Chủ nhật không
function isWeekend(dateStr: string) {
  const d = new Date(dateStr)
  const day = d.getDay()
  return day === 0 || day === 6 // 0: Chủ nhật, 6: Thứ 7
}
</script>

<style>
</style>