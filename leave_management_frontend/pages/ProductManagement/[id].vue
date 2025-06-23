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
            <BreadcrumbPage>Chỉnh sửa</BreadcrumbPage>
          </BreadcrumbItem>
        </BreadcrumbList>
      </Breadcrumb>
    </template>

    <div class="flex flex-1 flex-col gap-4 p-4 pt-0">
      <h1 class="text-2xl font-bold">Chỉnh sửa sản phẩm</h1>
      <EditForm v-if="product" :product="product" @save="updateProduct" @cancel="goBack" />
      <p v-else class="text-center text-gray-500">Đang tải dữ liệu...</p>
    </div>
  </MainTemplate>
</template>

<script setup lang="ts">
import { ref, onMounted, h } from 'vue'
import { useToast } from 'vue-toastification'
import { useRuntimeConfig, useCookie, useRoute, useRouter, navigateTo } from '#app'

import MainTemplate from '~/components/layouts/MainTemplate.vue'
import Breadcrumb from '@/components/ui/breadcrumb/Breadcrumb.vue'
import BreadcrumbList from '@/components/ui/breadcrumb/BreadcrumbList.vue'
import BreadcrumbItem from '@/components/ui/breadcrumb/BreadcrumbItem.vue'
import BreadcrumbLink from '@/components/ui/breadcrumb/BreadcrumbLink.vue'
import BreadcrumbSeparator from '@/components/ui/breadcrumb/BreadcrumbSeparator.vue'
import BreadcrumbPage from '@/components/ui/breadcrumb/BreadcrumbPage.vue'
import EditForm from '~/components/organisms/ProductManagement/EditForm.vue'

interface Product {
  id: number
  name: string
  description: string
  mainimage: string
  price: number
  flash_sale_price: number | null
  flash_sale_start: string | null
  flash_sale_end: string | null
  category: number
  is_active: boolean
  uploaded_images: string[]
}

const route = useRoute()
const router = useRouter()
const toast = useToast()
const product = ref<Product | null>(null)

const fetchProduct = async () => {
  try {
    const config = useRuntimeConfig()
    const token = useCookie('access_token')

    const response = await $fetch<Product>(`${config.public.apiBase}/catalogue/products/${route.params.id}/`, {
      headers: { Authorization: `Bearer ${token.value}` },
    })
    product.value = response
  } catch (error) {
    console.error('Lỗi khi lấy thông tin sản phẩm:', error)
    toast.error('Không tìm thấy thông tin sản phẩm!')
    router.push('/product-management')
  }
}

const updateProduct = async (formData: FormData) => {
  try {
    const config = useRuntimeConfig()
    const token = useCookie('access_token')

    await $fetch(`${config.public.apiBase}/catalogue/products/${route.params.id}/`, {
      method: 'PATCH',
      headers: {
        Authorization: `Bearer ${token.value}`,
      },
      body: formData,
    })

    toast.success('Cập nhật thành công!')
    await fetchProduct()
  } catch (error) {
    console.error('Lỗi khi cập nhật sản phẩm:', error)
    toast.error('Cập nhật thất bại!')
  }
}

const goBack = () => {
  toast.warning(
    h('div', { class: 'space-y-2' }, [
      h('p', 'Bạn có chắc chắn muốn thoát không? Dữ liệu chưa lưu sẽ bị mất.'),
      h(
        'button',
        {
          onClick: () => {
            toast.clear()
            router.push('/product-management')
          },
          class: 'px-4 py-2 bg-red-500 text-white rounded hover:bg-red-700 transition',
        },
        'Thoát'
      ),
    ]),
    { timeout: false, closeOnClick: false }
  )
}

onMounted(fetchProduct)
</script>
