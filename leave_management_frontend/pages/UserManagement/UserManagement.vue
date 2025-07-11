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
            <BreadcrumbPage>Quản lý người dùng</BreadcrumbPage>
          </BreadcrumbItem>
        </BreadcrumbList>
      </Breadcrumb>
    </template>
    <div class="flex flex-col gap-4 p-4 pt-0">
      <h1 class="text-2xl font-bold">Danh sách người dùng</h1>
      <div class="flex justify-end mb-2">
        <button
          class="px-6 py-2 bg-blue-500 hover:bg-blue-600 text-white font-bold rounded-lg shadow-md transition"
          @click="goToAddUser"
        >
          Thêm người dùng
        </button>
      </div>
      <EasyDataTable
        theme-color="#000000"
        :headers="headers"
        :items="users"
        :loading="loading"
        border-cell
        class="w-full"
        rowsPerPageMessage="Số lượng mỗi trang:"
        rowsOfPageSeparatorMessage="/"
      >
        <template #item-actions="{ userId }">
          <button class="px-3 py-1 bg-yellow-400 hover:bg-yellow-500 text-white rounded-lg font-semibold mr-2" @click="goToEditUser(userId)">Chỉnh sửa</button>
        </template>
      </EasyDataTable>
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
import { ref, onMounted } from 'vue'
import { useRuntimeConfig, useCookie } from '#app'
import { useRouter } from 'vue-router'

const config = useRuntimeConfig()
const token = useCookie('access_token')
const router = useRouter()

const users = ref<any[]>([])
const loading = ref(false)

const headers = [
  { text: 'ID', value: 'userId' },
  { text: 'Tên đăng nhập', value: 'username' },
  { text: 'Họ tên', value: 'fullName' },
  { text: 'Email', value: 'email' },
  { text: 'Role', value: 'roleName' },
  { text: 'Actions', value: 'actions', sortable: false },
]

async function fetchUsers() {
  loading.value = true
  try {
    const res = await $fetch<any[]>(`${config.public.apiBase}/auth/all`, {
      headers: { Authorization: `Bearer ${token.value}` },
    })
    users.value = res.map(u => ({
      ...u,
      roleName: u.role?.roleName || '',
    }))
  } catch (e) {
    users.value = []
  } finally {
    loading.value = false
  }
}

onMounted(fetchUsers)

function goToAddUser() {
  router.push('/user-management/create')
}

function goToEditUser(userId: number) {
  router.push(`/user-management/edit/${userId}`)
}
</script>

<style scoped>
</style>