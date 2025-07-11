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
            <BreadcrumbPage>Quyền truy cập</BreadcrumbPage>
          </BreadcrumbItem>
        </BreadcrumbList>
      </Breadcrumb>
    </template>
    <div class="flex flex-col gap-4 p-4 pt-0">
      <h1 class="text-2xl font-bold">Danh sách quyền truy cập</h1>
      <div class="flex justify-end mb-2">
        <button
          class="px-6 py-2 bg-blue-500 hover:bg-blue-600 text-white font-bold rounded-lg shadow-md transition"
          @click="goToCreatePermission"
        >
          Thêm quyền
        </button>
      </div>
      <EasyDataTable
        theme-color="#000000"
        :headers="headers"
        :items="items"
        :loading="loading"
        border-cell
        class="w-full"
        rowsPerPageMessage="Số lượng mỗi trang:"
        rowsOfPageSeparatorMessage="/"
      >
        <template #item-active="{ active }">
          <span :class="active ? 'text-green-600 font-semibold' : 'text-gray-400'">
            {{ active ? 'Đang bật' : 'Đang tắt' }}
          </span>
        </template>
        <template #item-actions="{ role, feature, active }">
          <button
            class="px-2 py-1 rounded text-white"
            :class="active ? 'bg-red-500 hover:bg-red-600' : 'bg-green-500 hover:bg-green-600'"
            @click="toggleActive(role.roleId, feature.featureId, !active)"
          >
            {{ active ? 'Tắt' : 'Bật' }}
          </button>
        </template>
      </EasyDataTable>
    </div>
  </MainTemplate>
</template>

<script lang="ts" setup>
import MainTemplate from '~/components/layouts/MainTemplate.vue'
import Breadcrumb from '@/components/ui/breadcrumb/Breadcrumb.vue'
import BreadcrumbList from '@/components/ui/breadcrumb/BreadcrumbList.vue'
import BreadcrumbItem from '@/components/ui/breadcrumb/BreadcrumbItem.vue'
import BreadcrumbLink from '@/components/ui/breadcrumb/BreadcrumbLink.vue'
import BreadcrumbSeparator from '@/components/ui/breadcrumb/BreadcrumbSeparator.vue'
import BreadcrumbPage from '@/components/ui/breadcrumb/BreadcrumbPage.vue'
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useToast } from 'vue-toastification'
import { useRuntimeConfig, useCookie } from '#app'

const toast = useToast()
const config = useRuntimeConfig()
const token = useCookie('access_token')
const items = ref<any[]>([])
const loading = ref(false)
const router = useRouter()
function goToCreatePermission() {
  router.push('/permission/create')
}

const headers = [
  { text: 'Role', value: 'roleName' },
  { text: 'Feature', value: 'featureName' },
  { text: 'Trạng thái', value: 'active', sortable: false },
  { text: 'Thao tác', value: 'actions', sortable: false },
]

async function fetchPermissions() {
  loading.value = true
  try {
    const res = await $fetch<any[]>(`${config.public.apiBase}/role-features/all`, {
      headers: { Authorization: `Bearer ${token.value}` },
    })
    console.log('[Permission] Dữ liệu trả về từ API /role-features/all:', res)
    items.value = res.map(item => ({
      roleId: item.role.roleId,
      roleName: item.role.roleName,
      featureId: item.feature.featureId,
      featureName: item.feature.featureName,
      active: item.active,
      role: item.role,
      feature: item.feature,
    }))
    console.log('[Permission] Dữ liệu đã map cho bảng:', items.value)
  } catch (e) {
    toast.error('Không thể tải danh sách quyền truy cập!')
    items.value = []
  } finally {
    loading.value = false
  }
}

onMounted(fetchPermissions)

async function toggleActive(roleId: number, featureId: number, isActive: boolean) {
  console.log('[Permission] Gửi PATCH /role-features/active', { roleId, featureId, isActive })
  try {
    await $fetch(`${config.public.apiBase}/role-features/active?isActive=${isActive}`, {
      method: 'PATCH',
      headers: { Authorization: `Bearer ${token.value}` },
      body: { roleId, featureId },
    })
    toast.success('Cập nhật trạng thái thành công!')
    fetchPermissions()
  } catch (e) {
    toast.error('Cập nhật trạng thái thất bại!')
  }
}
</script>

<style>
</style>