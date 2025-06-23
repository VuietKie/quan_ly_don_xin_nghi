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
            <BreadcrumbPage>Danh sách đơn hàng</BreadcrumbPage>
          </BreadcrumbItem>
        </BreadcrumbList>
      </Breadcrumb>
    </template>

    <div class="flex flex-col gap-4 p-4 pt-0">
      <h1 class="text-2xl font-bold">Danh sách đơn hàng</h1>
      <div class="flex flex-col sm:flex-row sm:justify-between sm:items-center gap-2">
        <div class="w-full sm:w-1/3">
          <Input
            v-model="search"
            type="text"
            placeholder="Tìm kiếm..."
            class="w-full border border-gray-300 rounded px-3 py-2"
          />
        </div>
        <div class="flex flex-col sm:flex-row gap-2 w-full sm:w-2/3">
          <Select v-model="selectedStatus" class="w-full sm:w-1/3">
            <SelectTrigger>
              <SelectValue placeholder="Trạng thái đơn hàng" />
            </SelectTrigger>
            <SelectContent>
              <SelectItem :value="null">Trạng thái đơn hàng</SelectItem>
              <SelectItem v-for="(label, value) in statusMapping" :key="value" :value="value">
                {{ label }}
              </SelectItem>
            </SelectContent>
          </Select>

          <Select v-model="selectedPaymentMethod" class="w-full sm:w-1/3">
            <SelectTrigger>
              <SelectValue placeholder="Phương thức thanh toán" />
            </SelectTrigger>
            <SelectContent>
              <SelectItem :value="null">Phương thức thanh toán</SelectItem>
              <SelectItem v-for="(label, value) in paymentMethodMapping" :key="value" :value="value">
                {{ label }}
              </SelectItem>
            </SelectContent>
          </Select>

          <Select v-model="selectedPaymentStatus" class="w-full sm:w-1/3">
            <SelectTrigger>
              <SelectValue placeholder="Trạng thái thanh toán" />
            </SelectTrigger>
            <SelectContent>
              <SelectItem :value="null">Trạng thái thanh toán</SelectItem>
              <SelectItem v-for="(label, value) in paymentStatusMapping" :key="value" :value="value">
                {{ label }}
              </SelectItem>
            </SelectContent>
          </Select>

          <Button
            @click="clearFilters"
            class="px-4 py-2"
          >
            Xóa bộ lọc
          </Button>
        </div>
      </div>

      <EasyDataTable
        theme-color="#000000"
        :headers="headers"
        :items="orders"
        :server-items-length="totalOrders"
        :rows-items="[5, 10, 20]"
        v-model:server-options="serverOptions"
        :loading="loading"
        buttons-pagination
        border-cell
        class="w-full"
        rowsPerPageMessage="Số lượng mỗi trang:"
        rowsOfPageSeparatorMessage="/"
      >
        <template #item-actions_order="{ id }">
          <div class="flex space-x-2">
            <NuxtLink :to="`/order-management/${id}`">
              <button class="h-5 w-5" title="Xem chi tiết">
                <img src="/images/edit_icon_table.png" alt="View" />
              </button>
            </NuxtLink>
          </div>
        </template>
      </EasyDataTable>
    </div>
  </MainTemplate>
</template>

<script setup lang="ts">
import { ref, watch } from 'vue'
import { useToast } from 'vue-toastification'
import { useRuntimeConfig, useCookie, useRoute } from '#app'
import MainTemplate from '~/components/layouts/MainTemplate.vue'
import Breadcrumb from '@/components/ui/breadcrumb/Breadcrumb.vue'
import BreadcrumbList from '@/components/ui/breadcrumb/BreadcrumbList.vue'
import BreadcrumbItem from '@/components/ui/breadcrumb/BreadcrumbItem.vue'
import BreadcrumbLink from '@/components/ui/breadcrumb/BreadcrumbLink.vue'
import BreadcrumbSeparator from '@/components/ui/breadcrumb/BreadcrumbSeparator.vue'
import BreadcrumbPage from '@/components/ui/breadcrumb/BreadcrumbPage.vue'
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '@/components/ui/select'

interface Order {
  id: number;
  user: string;
  total_price: string;
  status: string;
  payment_method: string;
  payment_status: string;
}

interface ApiResponse {
  count: number;
  results: Order[];
}

const toast = useToast()
const config = useRuntimeConfig()
const token = useCookie('access_token')
const route = useRoute()
const orders = ref<Order[]>([]);
const totalOrders = ref(0);
const loading = ref(false)
const search = ref('')
const selectedStatus = ref<string | null>(null)
const selectedPaymentMethod = ref<string | null>(null)
const selectedPaymentStatus = ref<string | null>(null)
const headers = [
  { text: "ID", value: "id" },
  { text: "Mã khách hàng", value: "user" },
  { text: "Tên khách hàng", value: "full_name" },
  { text: "Tổng tiền", value: "total_price" },
  { text: "Trạng thái", value: "status" },
  { text: "Phương thức thanh toán", value: "payment_method" },
  { text: "Trạng thái thanh toán", value: "payment_status" },
  { text: "Hành động", value: "actions_order", sortable: false },
];

const statusMapping: Record<string, string> = {
  cxl: "Chưa Xử Lý",
  dcbh: "Đang Chuẩn Bị Hàng",
  dgh: "Đang Giao Hàng",
  dghh: "Đã Giao Hàng",
  khh: "Khách Hàng Hủy",
  adh: "Admin Hủy",
};

const paymentMethodMapping: Record<string, string> = {
  cod: "Thanh toán khi giao hàng",
  ppl: "Thanh toán qua PayPal",
};

const paymentStatusMapping: Record<string, string> = {
  pending: "Chờ thanh toán",
  paid: "Đã thanh toán",
};

const serverOptions = ref({
  page: 1,
  rowsPerPage: 10,
});

const fetchOrders = async () => {
  try {
    if (!token.value) return;

    loading.value = true;
    const { page, rowsPerPage } = serverOptions.value;

    // Build query parameters
    const queryParams = new URLSearchParams({
      page: page.toString(),
      page_size: rowsPerPage.toString(),
      search: search.value
    });

    if (selectedStatus.value !== null) {
      queryParams.append('status', selectedStatus.value);
    }
    if (selectedPaymentMethod.value !== null) {
      queryParams.append('payment_method', selectedPaymentMethod.value);
    }
    if (selectedPaymentStatus.value !== null) {
      queryParams.append('payment_status', selectedPaymentStatus.value);
    }

    const response = await $fetch<ApiResponse>(
      `${config.public.apiBase}/order/admin/orders/?${queryParams.toString()}`,
      {
        headers: { Authorization: `Bearer ${token.value}` },
      }
    );

    orders.value = response.results.map(order => ({
      ...order,
      status: statusMapping[order.status] || order.status,
      payment_method: paymentMethodMapping[order.payment_method] || order.payment_method,
      payment_status: paymentStatusMapping[order.payment_status] || order.payment_status,
    }));
    totalOrders.value = response.count;
  } catch (error) {
    console.error("Error fetching orders:", error);
    toast.error("Không thể tải danh sách đơn hàng.");
  } finally {
    loading.value = false;
  }
};

const clearFilters = () => {
  search.value = '';
  selectedStatus.value = null;
  selectedPaymentMethod.value = null;
  selectedPaymentStatus.value = null;
  serverOptions.value.page = 1;
  fetchOrders();
};

// Watch pagination change
watch(serverOptions, fetchOrders, { deep: true, immediate: true });
watch(search, () => {
  serverOptions.value.page = 1; // Reset về trang đầu tiên khi tìm kiếm
  fetchOrders();
});

// Add watchers for filters
watch([selectedStatus, selectedPaymentMethod, selectedPaymentStatus], () => {
  serverOptions.value.page = 1; // Reset to first page when filters change
  fetchOrders();
});
</script>
