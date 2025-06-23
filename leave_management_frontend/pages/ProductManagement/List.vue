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
            <BreadcrumbLink href="/">Quản lý sản phẩm</BreadcrumbLink>
          </BreadcrumbItem>
          <BreadcrumbSeparator class="hidden md:block" />
          <BreadcrumbItem>
            <BreadcrumbPage>Danh sách sản phẩm</BreadcrumbPage>
          </BreadcrumbItem>
        </BreadcrumbList>
      </Breadcrumb>
    </template>

    <div class="flex flex-col gap-4 p-4 pt-0">
      <h1 class="text-2xl font-bold">Danh sách sản phẩm</h1>
        <div class="flex flex-col sm:flex-row sm:justify-between sm:items-center gap-2">
          <div class="w-full sm:w-1/3">
            <Input
              v-model="search"
              type="text"
              placeholder="Tìm kiếm..."
              class="w-full border border-gray-300 rounded px-3 py-2"
            />
          </div>
          <NuxtLink to="/product-management/create" class="w-full sm:w-auto">
            <Button class="btn btn-primary w-full sm:w-auto">Thêm mới</Button>
          </NuxtLink>
        </div>

      <EasyDataTable theme-color="#000000" :headers="headers" :items="products" :server-items-length="totalProducts"
        :rows-items="[5, 10, 20]" v-model:server-options="serverOptions" :loading="loading" buttons-pagination
        border-cell class="w-full" rowsPerPageMessage="Số lượng mỗi trang:" rowsOfPageSeparatorMessage="/">
        <template #item-mainimage="{ mainimage }">
          <img :src="mainimage" alt="Product Image" class="w-20 h-20 object-cover"
            onerror="this.src='/images/default_product.png'" />
        </template>

        <template #item-actions="{ id, is_active }">
          <div class="flex space-x-2">
            <NuxtLink :to="`/${entity}/${id}`">
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

interface Product {
  id: number;
  name: string;
  description: string;
  mainimage: string;
  is_active: boolean | string;
  quantity: number;
  price: string;
  category: number;
  created_at: string;
}

interface ApiResponse {
  count: number;
  results: Product[];
}

const entity = "product-management";
const toast = useToast()
const config = useRuntimeConfig()
const token = useCookie('access_token')
const products = ref<Product[]>([]);
const totalProducts = ref(0);
const loading = ref(false)
const search = ref('')
const headers = [
  { text: "ID", value: "id" },
  { text: "Tên sản phẩm", value: "name" },
  { text: "Mô tả", value: "description" },
  { text: "Hình ảnh", value: "mainimage", sortable: false },
  { text: "Giá", value: "price" },
  { text: "Trạng thái", value: "is_active" },
  { text: "Actions", value: "actions", sortable: false },
];

const serverOptions = ref({
  page: 1,
  rowsPerPage: 10,
})


const fetchProducts = async () => {
  if (!token.value) return

  loading.value = true
  try {
    const { page, rowsPerPage } = serverOptions.value
    const response = await $fetch<ApiResponse>(`${config.public.apiBase}/catalogue/products/?page=${page}&page_size=${rowsPerPage}&search=${search.value}`, {
      headers: { Authorization: `Bearer ${token.value}` },
    });

    products.value = response.results.map(product => ({
      ...product,
      mainimage: product.mainimage || '/images/default_product.png',
      is_active: product.is_active ? "Đã kích hoạt" : "Chưa kích hoạt",
    }));
    totalProducts.value = response.count;
  } catch (error) {
    console.error("Error fetching products:", error);
  } finally {
    loading.value = false
  }
};


// Watch pagination change
watch(serverOptions, fetchProducts, { deep: true, immediate: true })
watch(search, () => {
  serverOptions.value.page = 1 // reset về page 1
  fetchProducts()
})

const emitAction = (action: 'delete' | 'activate', id: number) => {
  const confirmMsg =
    action === 'delete'
      ? 'Bạn có chắc muốn vô hiệu hóa sản phẩm này?'
      : 'Bạn có chắc muốn kích hoạt sản phẩm này?'
  toast.clear()
  toast.warning(h('div', [
    confirmMsg,
    h('div', { class: 'flex space-x-2 mt-2' }, [
      h('button', {
        onClick: async () => {
          await updateProductStatus(id, action === 'activate')
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

const updateProductStatus = async (id: number, isActive: boolean) => {
  try {
    if (!token.value) return

    await $fetch(`${config.public.apiBase}/catalogue/products/${id}/`, {
      method: 'PATCH',
      headers: { Authorization: `Bearer ${token.value}` },
      body: { is_active: isActive },
    })

    fetchProducts()
  } catch (error) {
    console.error('Error updating product status:', error)
    toast.error("Cập nhật trạng thái sản phẩm thất bại.")
  }
}


</script>
