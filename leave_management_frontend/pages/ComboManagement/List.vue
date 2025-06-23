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
            <BreadcrumbLink href="/">Quản lý combo</BreadcrumbLink>
          </BreadcrumbItem>
          <BreadcrumbSeparator class="hidden md:block" />
          <BreadcrumbItem>
            <BreadcrumbPage>Danh sách combo</BreadcrumbPage>
          </BreadcrumbItem>
        </BreadcrumbList>
      </Breadcrumb>
    </template>

    <div class="flex flex-col gap-4 p-4 pt-0">
      <h1 class="text-2xl font-bold">Danh sách combo</h1>
      <div class="flex flex-col sm:flex-row sm:justify-between sm:items-center gap-2">
        <div class="w-full sm:w-1/3">
          <Input
            v-model="search"
            type="text"
            placeholder="Tìm kiếm..."
            class="w-full border border-gray-300 rounded px-3 py-2"
          />
        </div>
        <NuxtLink to="/combo-management/create" class="w-full sm:w-auto">
          <Button class="btn btn-primary w-full sm:w-auto">Thêm mới</Button>
        </NuxtLink>
      </div>

      <EasyDataTable
        theme-color="#000000"
        :headers="headers"
        :items="combos"
        :server-items-length="totalCombos"
        :rows-items="[5, 10, 20]"
        v-model:server-options="serverOptions"
        :loading="loading"
        buttons-pagination
        border-cell
        class="w-full"
        rowsPerPageMessage="Số lượng mỗi trang:"
        rowsOfPageSeparatorMessage="/"
      >
        <template #item-actions="{ id, is_active }">
          <div class="flex space-x-2">
            <NuxtLink :to="`/combo-management/${id}`">
              <button class="h-5 w-5" title="Chỉnh sửa">
                <img src="/images/edit_icon_table.png" alt="Edit" />
              </button>
            </NuxtLink>

            <button v-if="is_active === 'Đã kích hoạt'" @click="emitAction('delete', id)" class="h-6 w-6"
              title="Vô hiệu hóa hoặc xoá">
              <img src="/images/delete_icon_table.png" alt="Vô hiệu hóa" />
            </button>

            <button v-else @click="emitAction('activate', id)" class="h-6 w-6" title="Kích hoạt">
              <img src="/images/activate_icon.png" alt="Kích hoạt" />
            </button>
          </div>
        </template>
      </EasyDataTable>
    </div>
  </MainTemplate>
</template>

<script setup lang="ts">
import { ref, watch } from 'vue'
import { useToast } from 'vue-toastification'
import { useRuntimeConfig, useCookie } from '#app'
import { Button } from '@/components/ui/button'
import MainTemplate from '~/components/layouts/MainTemplate.vue'
import Breadcrumb from '@/components/ui/breadcrumb/Breadcrumb.vue'
import BreadcrumbList from '@/components/ui/breadcrumb/BreadcrumbList.vue'
import BreadcrumbItem from '@/components/ui/breadcrumb/BreadcrumbItem.vue'
import BreadcrumbLink from '@/components/ui/breadcrumb/BreadcrumbLink.vue'
import BreadcrumbSeparator from '@/components/ui/breadcrumb/BreadcrumbSeparator.vue'
import BreadcrumbPage from '@/components/ui/breadcrumb/BreadcrumbPage.vue'

interface ComboItem {
  product: number;
  quantity: number;
}

interface Combo {
  id: number;
  name: string;
  description: string;
  discount_amount: number;
  is_active: boolean | string;
  combo_items: ComboItem[];
}

interface ApiResponse {
  count: number;
  results: Combo[];
}

const entity = "combo-management";
const toast = useToast()
const config = useRuntimeConfig()
const token = useCookie('access_token')
const combos = ref<Combo[]>([]);
const totalCombos = ref(0);
const loading = ref(false)
const search = ref('')
const headers = [
  { text: "ID", value: "id" },
  { text: "Tên combo", value: "name" },
  { text: "Mô tả", value: "description" },
  { text: "Giảm giá", value: "discount_amount" },
  { text: "Trạng thái", value: "is_active" },
  { text: "Actions", value: "actions", sortable: false },
];

const serverOptions = ref({
  page: 1,
  rowsPerPage: 10,
})

const fetchCombos = async () => {
  if (!token.value) return

  loading.value = true
  try {
    const response = await $fetch<Combo[]>(`${config.public.apiBase}/catalogue/combos/`, {
      headers: { Authorization: `Bearer ${token.value}` },
    });

    // Apply search filter on client side
    const filteredCombos = search.value
      ? response.filter(combo => 
          combo.name.toLowerCase().includes(search.value.toLowerCase()) ||
          combo.description.toLowerCase().includes(search.value.toLowerCase())
        )
      : response;

    // Apply pagination on client side
    const startIndex = (serverOptions.value.page - 1) * serverOptions.value.rowsPerPage;
    const endIndex = startIndex + serverOptions.value.rowsPerPage;
    const paginatedCombos = filteredCombos.slice(startIndex, endIndex);

    combos.value = paginatedCombos.map(combo => ({
      ...combo,
      is_active: combo.is_active ? "Đã kích hoạt" : "Chưa kích hoạt",
    }));
    totalCombos.value = filteredCombos.length;
  } catch (error) {
    console.error("Error fetching combos:", error);
    toast.error("Không thể tải danh sách combo");
  } finally {
    loading.value = false
  }
};

// Watch pagination change
watch(serverOptions, fetchCombos, { deep: true, immediate: true })
watch(search, () => {
  serverOptions.value.page = 1 // reset về page 1
  fetchCombos()
})

const emitAction = (action: 'delete' | 'activate', id: number) => {
  const confirmMsg =
    action === 'delete'
      ? 'Bạn có chắc muốn vô hiệu hóa combo này?'
      : 'Bạn có chắc muốn kích hoạt combo này?'
  toast.clear()
  toast.warning(h('div', [
    confirmMsg,
    h('div', { class: 'flex space-x-2 mt-2' }, [
      h('button', {
        onClick: async () => {
          await updateComboStatus(id, action === 'activate')
          toast.clear()
        },
        class: 'px-4 py-1 bg-red-500 text-white rounded',
      }, 'Xác nhận'),
      h('button', {
        onClick: () => toast.clear(),
        class: 'px-4 py-1 bg-gray-500 text-white rounded',
      }, 'Hủy')
    ])
  ]), {
    timeout: 10000,
    closeOnClick: false,
  })
}

const updateComboStatus = async (id: number, isActive: boolean) => {
  try {
    if (!token.value) return

    await $fetch(`${config.public.apiBase}/catalogue/combos/${id}/`, {
      method: 'PATCH',
      headers: { Authorization: `Bearer ${token.value}` },
      body: { is_active: isActive },
    })

    fetchCombos()
  } catch (error) {
    console.error('Error updating combo status:', error)
    toast.error("Cập nhật trạng thái combo thất bại.")
  }
}
</script>

<style>
</style>