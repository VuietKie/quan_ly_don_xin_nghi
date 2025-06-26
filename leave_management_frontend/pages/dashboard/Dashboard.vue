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
            <BreadcrumbPage>Trang dashboard</BreadcrumbPage>
          </BreadcrumbItem>
        </BreadcrumbList>
      </Breadcrumb>
    </template>
  </MainTemplate>
</template>

<script lang="ts" setup>
import { ref, onMounted } from 'vue'
import { useRuntimeConfig, useCookie } from '#app'
import MainTemplate from '~/components/layouts/MainTemplate.vue'
import Breadcrumb from '@/components/ui/breadcrumb/Breadcrumb.vue'
import BreadcrumbList from '@/components/ui/breadcrumb/BreadcrumbList.vue'
import BreadcrumbItem from '@/components/ui/breadcrumb/BreadcrumbItem.vue'
import BreadcrumbLink from '@/components/ui/breadcrumb/BreadcrumbLink.vue'
import BreadcrumbSeparator from '@/components/ui/breadcrumb/BreadcrumbSeparator.vue'
import BreadcrumbPage from '@/components/ui/breadcrumb/BreadcrumbPage.vue'


interface MonthlySales {
  total_quantity: number
}

interface MonthlyRevenue {
  revenue_this_month: number
  revenue_last_month: number
  percentage_change: number | null
}

interface FlashSaleResponse {
  count: number
  total_pages: number
  next: string | null
  previous: string | null
  results: Array<any>
}

const monthlySales = ref<MonthlySales>({ total_quantity: 0 })
const monthlyRevenue = ref<MonthlyRevenue>({
  revenue_this_month: 0,
  revenue_last_month: 0,
  percentage_change: null
})
const flashSaleCount = ref<number>(0)

const config = useRuntimeConfig()
const token = useCookie('access_token', { sameSite: 'lax', path: '/' })
console.log('Dashboard access_token:', token.value)

const fetchMonthlySales = async () => {
  try {
    if (!token.value) return
    const response = await $fetch<MonthlySales>(
      `${config.public.apiBase}/order/admin/monthly-sales/`,
      {
        headers: { Authorization: `Bearer ${token.value}` }
      }
    )
    monthlySales.value = response
  } catch (error) {
    console.error('Error fetching monthly sales:', error)
  }
}

const fetchMonthlyRevenue = async () => {
  try {
    if (!token.value) return
    const response = await $fetch<MonthlyRevenue>(
      `${config.public.apiBase}/order/admin/monthly-revenue/`,
      {
        headers: { Authorization: `Bearer ${token.value}` }
      }
    )
    monthlyRevenue.value = response
  } catch (error) {
    console.error('Error fetching monthly revenue:', error)
  }
}

const fetchFlashSaleProducts = async () => {
  try {
    if (!token.value) return
    const response = await $fetch<FlashSaleResponse>(
      `${config.public.apiBase}/catalogue/products/flash-sale/`,
      {
        headers: { Authorization: `Bearer ${token.value}` }
      }
    )
    flashSaleCount.value = response.count
  } catch (error) {
    console.error('Error fetching flash sale products:', error)
  }
}

const formatCurrency = (amount: number): string => {
  return new Intl.NumberFormat('vi-VN', {
    style: 'currency',
    currency: 'VND'
  }).format(amount)
}

onMounted(() => {
  fetchMonthlySales()
  fetchMonthlyRevenue()
  fetchFlashSaleProducts()
})
</script>