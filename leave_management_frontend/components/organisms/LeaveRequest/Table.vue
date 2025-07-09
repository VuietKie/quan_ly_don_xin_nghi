<template>
  <div class="flex flex-col gap-4">
    <h1 class="text-2xl font-bold">{{ title }}</h1>
    <div class="flex flex-col sm:flex-row sm:justify-between sm:items-center gap-2">
      <slot name="toolbar"></slot>
    </div>
    <EasyDataTable
      theme-color="#000000"
      :headers="headers"
      :items="items"
      :server-items-length="totalItems"
      :rows-items="[5, 10, 20]"
      v-model:server-options="serverOptions"
      :loading="loading"
      buttons-pagination
      border-cell
      class="w-full"
      rowsPerPageMessage="Số lượng mỗi trang:"
      rowsOfPageSeparatorMessage="/"
    >
      <template #item-actions="slotProps">
        <slot name="actions" v-bind="slotProps" />
      </template>
    </EasyDataTable>
  </div>
</template>

<script setup lang="ts">
import { ref, watch } from 'vue'
import { useToast } from 'vue-toastification'
import { useCookie, useRuntimeConfig } from '#app'
// EasyDataTable đã là global component

const props = defineProps<{
  apiUrl: string,
  headers: any[],
  title?: string
}>()

const toast = useToast()
const token = useCookie('access_token')
const config = useRuntimeConfig()
const items = ref<any[]>([])
const totalItems = ref(0)
const loading = ref(false)
const serverOptions = ref({
  page: 1,
  rowsPerPage: 10,
})

const fetchData = async () => {
  if (!token.value) return
  loading.value = true
  try {
    const response = await $fetch<any>(props.apiUrl, {
      headers: { Authorization: `Bearer ${token.value}` },
    })
    console.log('[LeaveRequestTable] Raw response from server:', response)
    if (!Array.isArray(response)) {
      toast.error('Lỗi dữ liệu: Server trả về không phải danh sách!')
      items.value = []
      totalItems.value = 0
      return
    }
    // Map dữ liệu về đúng format cho bảng
    const filtered = response.map((item: any) => ({
      id: item.requestId,
      user_id: item.user?.userId,
      start_date: item.startDate,
      end_date: item.endDate,
      reason: item.reason,
      status: item.status,
    }))
    console.log('[LeaveRequestTable] Data after mapping:', filtered)
    // Phân trang client
    const startIndex = (serverOptions.value.page - 1) * serverOptions.value.rowsPerPage
    const endIndex = startIndex + serverOptions.value.rowsPerPage
    const paginated = filtered.slice(startIndex, endIndex)
    items.value = paginated
    totalItems.value = filtered.length
  } catch (error) {
    console.error('[LeaveRequestTable] Error fetching data:', error)
    toast.error('Không thể tải dữ liệu bảng')
    items.value = []
    totalItems.value = 0
  } finally {
    loading.value = false
  }
}

watch(serverOptions, fetchData, { deep: true, immediate: true })
</script>

<style scoped>
</style> 