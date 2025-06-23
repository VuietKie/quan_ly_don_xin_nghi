<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRuntimeConfig, useCookie } from '#app'

interface RecentCustomer {
  customer_name: string
  customer_phone: string
  total_price: number
}

const recentCustomers = ref<RecentCustomer[]>([])
const config = useRuntimeConfig()
const token = useCookie('access_token')

const formatPrice = (price: number) => {
  return new Intl.NumberFormat('vi-VN', {
    style: 'currency',
    currency: 'VND'
  }).format(price)
}

const fetchRecentCustomers = async () => {
  try {
    if (!token.value) return
    const response = await $fetch<RecentCustomer[]>(
      `${config.public.apiBase}/order/admin/recent-paid-customers/`,
      {
        headers: { Authorization: `Bearer ${token.value}` }
      }
    )
    recentCustomers.value = response
  } catch (error) {
    console.error('Error fetching recent customers:', error)
  }
}

onMounted(() => {
  fetchRecentCustomers()
})
</script>

<template>
  <div class="space-y-8">
    <div v-for="customer in recentCustomers" :key="customer.customer_phone" class="flex items-center">
      <div class="ml-4 space-y-1">
        <p class="text-sm font-medium leading-none">
          {{ customer.customer_name }}
        </p>
        <p class="text-sm text-muted-foreground">
          {{ customer.customer_phone }}
        </p>
      </div>
      <div class="ml-auto font-medium">
        {{ formatPrice(customer.total_price) }}
      </div>
    </div>
  </div>
</template>