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
            <BreadcrumbLink href="/leave-request/my-request">Đơn xin nghỉ của tôi</BreadcrumbLink>
          </BreadcrumbItem>
          <BreadcrumbSeparator class="hidden md:block" />
          <BreadcrumbItem>
            <BreadcrumbPage>Đơn xin nghỉ của tôi</BreadcrumbPage>
          </BreadcrumbItem>
        </BreadcrumbList>
      </Breadcrumb>
    </template>
    <div class="flex flex-col gap-4 p-4 pt-0">
      <LeaveRequestTable
        v-if="apiUrl"
        :apiUrl="apiUrl"
        :headers="headers"
        title="Đơn xin nghỉ của tôi"
      >
        <template #actions="{ id }">
          <NuxtLink :to="`/leave-request/${id}`">
            <button class="h-5 w-5 px-2 py-1 text-blue-600 underline" title="Xem chi tiết">
              Xem
            </button>
          </NuxtLink>
        </template>
      </LeaveRequestTable>
    </div>
  </MainTemplate>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, watch } from 'vue'
import { useRuntimeConfig, useCookie } from '#app'
import MainTemplate from '~/components/layouts/MainTemplate.vue'
import Breadcrumb from '@/components/ui/breadcrumb/Breadcrumb.vue'
import BreadcrumbList from '@/components/ui/breadcrumb/BreadcrumbList.vue'
import BreadcrumbItem from '@/components/ui/breadcrumb/BreadcrumbItem.vue'
import BreadcrumbLink from '@/components/ui/breadcrumb/BreadcrumbLink.vue'
import BreadcrumbSeparator from '@/components/ui/breadcrumb/BreadcrumbSeparator.vue'
import BreadcrumbPage from '@/components/ui/breadcrumb/BreadcrumbPage.vue'
import LeaveRequestTable from '~/components/organisms/LeaveRequest/Table.vue'

const config = useRuntimeConfig()
const token = useCookie('access_token')
const userId = ref<number | null>(null)

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
})

const apiUrl = computed(() =>
  userId.value
    ? `${config.public.apiBase}/leave-request/my-request?userId=${userId.value}`
    : ''
)

watch(userId, () => {
  console.log('[MyRequest] userId:', userId.value)
  console.log('[MyRequest] apiUrl:', apiUrl.value)
})

const headers = [
  { text: 'ID', value: 'id' },
  { text: 'Mã nhân viên', value: 'user_id' },
  { text: 'Ngày bắt đầu', value: 'start_date' },
  { text: 'Ngày kết thúc', value: 'end_date' },
  { text: 'Lý do', value: 'reason' },
  { text: 'Trạng thái', value: 'status' },
  { text: 'Actions', value: 'actions', sortable: false },
]
</script> 