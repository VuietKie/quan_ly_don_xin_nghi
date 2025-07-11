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
            <BreadcrumbLink href="/user-management">Quản lý người dùng</BreadcrumbLink>
          </BreadcrumbItem>
          <BreadcrumbSeparator class="hidden md:block" />
          <BreadcrumbItem>
            <BreadcrumbPage>Chỉnh sửa người dùng</BreadcrumbPage>
          </BreadcrumbItem>
        </BreadcrumbList>
      </Breadcrumb>
    </template>
    <div class="flex flex-1 flex-col gap-4 p-4 pt-0">
      <h1 class="text-2xl font-bold">Chỉnh sửa người dùng</h1>
      <div v-if="loading" class="text-center text-gray-500">Đang tải dữ liệu...</div>
      <form v-else @submit.prevent="submitForm" class="flex justify-center items-center min-h-[60vh] bg-gray-50">
        <div class="bg-white shadow-xl rounded-2xl p-8 w-full max-w-lg">
          <div class="flex items-center mb-6 gap-2">
            <svg xmlns="http://www.w3.org/2000/svg" class="h-8 w-8 text-blue-500" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v16m8-8H4" /></svg>
            <h2 class="text-2xl font-bold text-gray-800">Chỉnh sửa người dùng</h2>
          </div>
          <div class="space-y-6">
            <div>
              <label class="block mb-1 font-semibold text-gray-700">Tên đăng nhập</label>
              <input v-model="form.username" type="text" class="w-full px-4 py-2 border border-gray-300 rounded-lg bg-gray-100 text-gray-500" readonly />
            </div>
            <div>
              <label class="block mb-1 font-semibold text-gray-700">Họ tên</label>
              <input v-model="form.fullName" type="text" required class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-400 focus:outline-none text-gray-700" />
            </div>
            <div>
              <label class="block mb-1 font-semibold text-gray-700">Email</label>
              <input v-model="form.email" type="email" class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-400 focus:outline-none text-gray-700" />
            </div>
            <div>
              <label class="block mb-1 font-semibold text-gray-700">Mật khẩu mới</label>
              <input v-model="form.password" type="password" class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-400 focus:outline-none text-gray-700" placeholder="Để trống nếu không đổi" />
            </div>
            <div>
              <label class="block mb-1 font-semibold text-gray-700">Vai trò</label>
              <select v-model="form.roleId" required class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-400 focus:outline-none text-gray-700">
                <option value="" disabled>Chọn vai trò...</option>
                <option v-for="role in roles" :key="role.roleId" :value="role.roleId">{{ role.roleName }}</option>
              </select>
            </div>
            <div class="flex flex-col gap-2 sm:flex-row sm:justify-end mt-6">
              <button type="submit" class="w-full sm:w-60 py-3 bg-blue-500 hover:bg-blue-600 text-white font-bold rounded-lg shadow-md transition">Lưu thay đổi</button>
              <button type="button" @click="goBack" class="w-full sm:w-60 py-3 bg-red-500 hover:bg-red-600 text-white font-bold rounded-lg shadow-md transition">Thoát</button>
            </div>
          </div>
        </div>
      </form>
    </div>
  </MainTemplate>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useRuntimeConfig, useCookie } from '#app'
import { useToast } from 'vue-toastification'
import MainTemplate from '~/components/layouts/MainTemplate.vue'
import Breadcrumb from '@/components/ui/breadcrumb/Breadcrumb.vue'
import BreadcrumbList from '@/components/ui/breadcrumb/BreadcrumbList.vue'
import BreadcrumbItem from '@/components/ui/breadcrumb/BreadcrumbItem.vue'
import BreadcrumbLink from '@/components/ui/breadcrumb/BreadcrumbLink.vue'
import BreadcrumbSeparator from '@/components/ui/breadcrumb/BreadcrumbSeparator.vue'
import BreadcrumbPage from '@/components/ui/breadcrumb/BreadcrumbPage.vue'

const route = useRoute()
const router = useRouter()
const config = useRuntimeConfig()
const token = useCookie('access_token')
const toast = useToast()

const loading = ref(true)
const roles = ref<any[]>([])
const form = ref({
  username: '',
  fullName: '',
  email: '',
  password: '',
  roleId: ''
})

async function fetchUser() {
  loading.value = true
  try {
    const res = await $fetch<any>(`${config.public.apiBase}/auth/${route.params.id}`, {
      headers: { Authorization: `Bearer ${token.value}` },
    })
    form.value = {
      username: res.username || '',
      fullName: res.fullName || '',
      email: res.email || '',
      password: '',
      roleId: res.role?.roleId || ''
    }
  } catch (e) {
    toast.error('Không thể tải thông tin người dùng!')
    router.push('/user-management')
  } finally {
    loading.value = false
  }
}

async function fetchRoles() {
  try {
    const res = await $fetch<any[]>(`${config.public.apiBase}/roles/all`, {
      headers: { Authorization: `Bearer ${token.value}` },
    })
    roles.value = res
  } catch (e) {
    toast.error('Không thể tải danh sách vai trò!')
  }
}

onMounted(() => {
  fetchUser()
  fetchRoles()
})

async function submitForm() {
  try {
    const body: any = {
      fullName: form.value.fullName,
      email: form.value.email,
      role: { roleId: form.value.roleId }
    }
    if (form.value.password) {
      body.password = form.value.password
    }
    await $fetch(`${config.public.apiBase}/auth/update/${route.params.id}`, {
      method: 'PATCH',
      headers: { Authorization: `Bearer ${token.value}` },
      body,
    })
    toast.success('Cập nhật người dùng thành công!')
    router.push('/user-management')
  } catch (e) {
    toast.error('Cập nhật người dùng thất bại!')
  }
}

function goBack() {
  router.push('/user-management')
}
</script>

<style scoped>
</style>