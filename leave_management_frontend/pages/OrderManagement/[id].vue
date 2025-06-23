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
            <BreadcrumbLink href="/">Quản lý đơn hàng</BreadcrumbLink>
          </BreadcrumbItem>
          <BreadcrumbSeparator class="hidden md:block" />
          <BreadcrumbItem>
            <BreadcrumbPage>Chỉnh sửa</BreadcrumbPage>
          </BreadcrumbItem>
        </BreadcrumbList>
      </Breadcrumb>
    </template>

    <div class="flex flex-1 flex-col gap-4 p-4 pt-0">
      <h1 class="text-2xl font-bold">Chỉnh sửa đơn hàng</h1>
      <EditForm v-if="order" :order="order" @save="updateOrder" @cancel="goBack" />
      <p v-else class="text-center text-gray-500">Đang tải dữ liệu...</p>
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
import EditForm from '~/components/organisms/OrderManagement/EditForm.vue'

interface Order {
  id: number
  user: number
  phone_number: string
  address: string
  status: string
  payment_method: string
  payment_status: string
  items: Array<{
    product: number
    product_name: string
    product_image: string
    unit_price: number
    quantity: number
    total_price: number
  }>
  total_price: number
}

const route = useRoute()
const router = useRouter()
const toast = useToast()
const order = ref<Order | null>(null)

const fetchOrder = async () => {
  try {
    const config = useRuntimeConfig()
    const token = useCookie('access_token')

    const response = await $fetch<Order>(`${config.public.apiBase}/order/admin/order/${route.params.id}/`, {
      headers: { Authorization: `Bearer ${token.value}` },
    })
    order.value = response || {} // Gán giá trị mặc định nếu response null
  } catch (error) {
    console.error('Lỗi khi lấy thông tin đơn hàng:', error)
    toast.error('Không tìm thấy thông tin đơn hàng!')
    router.push('/order-management')
  }
}

const updateOrder = async (updatedOrder: Partial<Order>) => {
  try {
    const config = useRuntimeConfig()
    const token = useCookie('access_token')

    await $fetch(`${config.public.apiBase}/order/admin/order/${route.params.id}/`, {
      method: 'PATCH',
      headers: {
        Authorization: `Bearer ${token.value}`,
      },
      body: updatedOrder,
    })

    toast.success('Cập nhật đơn hàng thành công!')
    await fetchOrder()
  } catch (error) {
    console.error('Lỗi khi cập nhật đơn hàng:', error)
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
            router.push('/order-management')
          },
          class: 'px-4 py-2 bg-red-500 text-white rounded hover:bg-red-700 transition',
        },
        'Thoát'
      ),
    ]),
    { timeout: false, closeOnClick: false }
  )
}

onMounted(fetchOrder)
</script>

