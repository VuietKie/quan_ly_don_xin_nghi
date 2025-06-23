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
    <!-- Nội dung chính của trang -->
    <div class="flex flex-1 flex-col gap-2 sm:gap-4 p-2 sm:p-4 pt-0">
      <div class="w-full">
        <div class="space-y-2 sm:space-y-4 p-2 sm:p-4 md:p-6">
          <div class="flex items-center justify-between space-y-1 sm:space-y-2">
            <h2 class="text-2xl sm:text-3xl font-bold tracking-tight">
              Dashboard
            </h2>
          </div>

          <div class="space-y-2 sm:space-y-4">
            <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-2 sm:gap-4">
              <Card>
                <CardHeader class="flex flex-row items-center justify-between space-y-0 pb-2">
                  <CardTitle class="text-sm font-medium">
                    Tổng doanh thu
                  </CardTitle>
                  <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor"
                    strokeLinecap="round" strokeLinejoin="round" strokeWidth="2"
                    class="h-4 w-4 text-muted-foreground">
                    <path d="M12 2v20M17 5H9.5a3.5 3.5 0 0 0 0 7h5a3.5 3.5 0 0 1 0 7H6" />
                  </svg>
                </CardHeader>
                <CardContent>
                  <div class="text-xl sm:text-2xl font-bold">
                    {{ formatCurrency(monthlyRevenue.revenue_this_month) }}
                  </div>
                  <p class="text-xs" v-if="monthlyRevenue.percentage_change !== null"
                     :class="{
                       'text-green-600': monthlyRevenue.percentage_change > 0,
                       'text-red-600': monthlyRevenue.percentage_change < 0,
                       'text-muted-foreground': monthlyRevenue.percentage_change === 0
                     }">
                    {{ monthlyRevenue.percentage_change > 0 ? '+' : '' }}{{ monthlyRevenue.percentage_change }}% so với tháng trước
                  </p>
                  <p class="text-xs text-muted-foreground" v-else>
                    Không có dữ liệu tháng trước
                  </p>
                </CardContent>
              </Card>
              <Card>
                <CardHeader class="flex flex-row items-center justify-between space-y-0 pb-2">
                  <CardTitle class="text-sm font-medium">
                    Đăng ký
                  </CardTitle>
                  <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor"
                    strokeLinecap="round" strokeLinejoin="round" strokeWidth="2"
                    class="h-4 w-4 text-muted-foreground">
                    <path d="M16 21v-2a4 4 0 0 0-4-4H6a4 4 0 0 0-4 4v2" />
                    <circle cx="9" cy="7" r="4" />
                    <path d="M22 21v-2a4 4 0 0 0-3-3.87M16 3.13a4 4 0 0 1 0 7.75" />
                  </svg>
                </CardHeader>
                <CardContent>
                  <div class="text-2xl font-bold">
                    +12
                  </div>
                  <p class="text-xs text-muted-foreground">
                    +180.1% so với tháng trước
                  </p>
                </CardContent>
              </Card>
              <Card>
                <CardHeader class="flex flex-row items-center justify-between space-y-0 pb-2">
                  <CardTitle class="text-sm font-medium">
                    Hàng hoá
                  </CardTitle>
                  <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor"
                    strokeLinecap="round" strokeLinejoin="round" strokeWidth="2"
                    class="h-4 w-4 text-muted-foreground">
                    <rect width="20" height="14" x="2" y="5" rx="2" />
                    <path d="M2 10h20" />
                  </svg>
                </CardHeader>
                <CardContent>
                  <div class="text-2xl font-bold">
                    +12
                  </div>
                  <p class="text-xs text-muted-foreground">
                    +19% so với tháng trước
                  </p>
                </CardContent>
              </Card>
              <Card>
                <CardHeader class="flex flex-row items-center justify-between space-y-0 pb-2">
                  <CardTitle class="text-sm font-medium">
                    Đang giảm giá
                  </CardTitle>
                  <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor"
                    strokeLinecap="round" strokeLinejoin="round" strokeWidth="2"
                    class="h-4 w-4 text-muted-foreground">
                    <path d="M22 12h-4l-3 9L9 3l-3 9H2" />
                  </svg>
                </CardHeader>
                <CardContent>
                  <div class="text-2xl font-bold">
                    {{ flashSaleCount }}
                  </div>
                  <p class="text-xs text-muted-foreground">
                    hàng hoá đang được giảm giá
                  </p>
                </CardContent>
              </Card>
            </div>
            <div class="grid grid-cols-1 lg:grid-cols-7 gap-2 sm:gap-4">
              <Card class="lg:col-span-4">
                <CardHeader>
                  <CardTitle>Tổng quan</CardTitle>
                </CardHeader>
                <CardContent class="pl-0 sm:pl-2">
                  <Overview />
                </CardContent>
              </Card>
              <Card class="lg:col-span-3">
                <CardHeader>
                  <CardTitle>Bán gần đây</CardTitle>
                  <CardDescription>
                    Bạn đã bán {{ monthlySales.total_quantity }} đơn hàng tháng này.
                  </CardDescription>
                </CardHeader>
                <CardContent>
                  <RecentSales />
                </CardContent>
              </Card>
            </div>
          </div>

        </div>
      </div>
    </div>
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
import { Button } from '@/components/ui/button'
import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from '@/components/ui/card'
import {
  Tabs,
  TabsContent,
  TabsList,
  TabsTrigger,
} from '@/components/ui/tabs'
import Overview from '~/components/organisms/Dashboard/Overview.vue'
import RecentSales from '~/components/organisms/Dashboard/RecentSales.vue'

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
const token = useCookie('access_token')

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