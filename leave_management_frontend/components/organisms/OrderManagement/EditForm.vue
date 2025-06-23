<template>
  <div class="grid gap-4">
    <div class="grid gap-2">
      <Label for="order_id">Mã đơn hàng</Label>
      <Input id="order_id" :model-value="String(order.id)" type="text" disabled />
    </div>

    <div class="grid gap-2">
      <Label for="user_id">ID Người dùng</Label>
      <Input id="user_id" :model-value="String(order.user)" type="text" disabled />
    </div>

    <div class="grid gap-2">
      <Label for="user_full_name">Tên Người dùng</Label>
      <Input id="user_full_name" :model-value="String(order.full_name)" type="text" disabled />
    </div>

    <div class="grid gap-2">
      <Label for="phone_number">Số điện thoại</Label>
      <Input id="phone_number" v-model="order.phone_number" type="text" placeholder="Số điện thoại" />
    </div>

    <div class="grid gap-2">
      <Label for="address">Địa chỉ giao hàng</Label>
      <Input id="address" v-model="order.address" type="text" placeholder="Địa chỉ giao hàng" />
    </div>

    <div class="grid gap-2">
      <Label for="status">Trạng thái đơn hàng</Label>
      <select id="status" v-model="order.status" class="border p-2 rounded w-full">
        <option v-for="(label, value) in statusChoices" :key="value" :value="value">{{ label }}</option>
      </select>
    </div>

    <div class="grid gap-2">
      <Label for="payment_method">Phương thức thanh toán</Label>
      <select id="payment_method" v-model="order.payment_method" class="border p-2 rounded w-full" disabled>
        <option v-for="(label, value) in paymentMethodChoices" :key="value" :value="value">{{ label }}</option>
      </select>
    </div>

    <div class="grid gap-2">
      <Label for="payment_status">Trạng thái thanh toán</Label>
      <select id="payment_status" v-model="order.payment_status" class="border p-2 rounded w-full">
        <option v-for="(label, value) in paymentStatusChoices" :key="value" :value="value">{{ label }}</option>
      </select>
    </div>

    <h2 class="text-xl font-semibold mt-6">Sản phẩm trong đơn hàng</h2>
    <div class="mt-4 space-y-4">
      <div v-for="item in order?.items" :key="item.product" class="flex border p-4 rounded-lg shadow-md items-center">
        <img :src="item.product_image" alt="Product Image" class="w-24 h-24 object-cover rounded-md">
        <div class="ml-4">
          <router-link :to="`/product/${item.product}`" class="text-lg font-semibold text-yellow-600 hover:underline">
            {{ item.product_name }}
          </router-link>
          <p><strong>Đơn giá:</strong> {{ formatPrice(item.unit_price) }}</p>
          <p><strong>Số lượng:</strong> {{ item.quantity }}</p>
        </div>
        <div class="ml-auto text-right">
          <p><strong>Thành tiền:</strong> {{ formatPrice(item.total_price) }}</p>
        </div>
      </div>
    </div>

    <div class="mt-6 p-4 bg-gray-100 rounded-lg text-lg font-semibold text-right">
      Tổng cộng: {{ formatPrice(order.total_price) }}
    </div>

    <div class="flex justify-end mt-6 space-x-3">
      <button @click="saveChanges" class="bg-blue-500 text-white text-lg px-6 py-3 rounded-lg transition-all duration-200 hover:bg-gray-800">
        Lưu thay đổi
      </button>
      <button @click="cancelChanges" class="bg-red-500 text-white text-lg px-6 py-3 rounded-lg transition-all duration-200 hover:bg-gray-800">
        Thoát
      </button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { defineProps, reactive, watch, onMounted } from 'vue';
import { useRoute, useRouter } from "vue-router";
import { useToast } from "vue-toastification";
import { useRuntimeConfig } from "#imports";
import Input from "~/components/ui/input/Input.vue";

const route = useRoute();
const router = useRouter();
const toast = useToast();
const config = useRuntimeConfig();
const token = useCookie("access_token");

const props = defineProps({
  order: {
    type: Object,
    required: true,
    default: () => ({
      id: 0,
      user: 0,
      full_name: '',
      phone_number: '',
      address: '',
      status: '',
      payment_method: '',
      payment_status: '',
      items: [],
      total_price: 0,
    }),
  },
})

const order = reactive({ ...props.order })

// Đồng bộ dữ liệu khi props.order thay đổi
watch(() => props.order, (newOrder) => {
  Object.assign(order, newOrder)
})

const statusChoices = {
  "cxl": "Chưa Xử Lý",
  "dcbh": "Đang Chuẩn Bị Hàng",
  "dgh": "Đang Giao Hàng",
  "dghh": "Đã Giao Hàng",
  "khh": "Khách Hàng Hủy",
  "adh": "Admin Hủy"
};

const paymentMethodChoices = {
  "cod": "Thanh toán khi giao hàng",
  "ppl": "Thanh toán qua PayPal"
};

const paymentStatusChoices = {
  "pending": "Chờ thanh toán",
  "paid": "Đã thanh toán"
};

const fetchOrder = async () => {
  try {
    const response = await $fetch(`${config.public.apiBase}/order/admin/order/${route.params.id}/`, {
      headers: { Authorization: `Bearer ${token.value}` },
    });
    Object.assign(order, response);
  } catch (error) {
    console.error("Lỗi khi tải đơn hàng:", error);
    toast.error("Không tìm thấy thông tin đơn hàng!");
    router.push("/admin/orders");
  }
};

const saveChanges = async () => {
  try {
    await $fetch(`${config.public.apiBase}/order/admin/order/${route.params.id}/`, {
      method: "PATCH",
      headers: { Authorization: `Bearer ${token.value}` },
      body: {
        phone_number: order.phone_number,
        address: order.address,
        status: order.status,
        payment_method: order.payment_method,
        payment_status: order.payment_status,
      },
    });
    toast.success("Cập nhật đơn hàng thành công!");
    await fetchOrder(); // Refresh order data after successful update
  } catch (error) {
    console.error("Lỗi khi cập nhật đơn hàng:", error);
    toast.error("Cập nhật thất bại!");
  }
};

const cancelChanges = () => {
  router.push("/admin/orders");
};

interface OrderItem {
  product: number;
  product_image: string;
  product_name: string;
  unit_price: number;
  quantity: number;
  total_price: number;
}

interface Order {
  id: number;
  user: number;
  phone_number: string;
  address: string;
  status: string;
  payment_method: string;
  payment_status: string;
  items: OrderItem[];
  total_price: number;
}

const formatPrice = (price: number): string => {
  return new Intl.NumberFormat("vi-VN", { style: "currency", currency: "VND" }).format(price);
};

onMounted(fetchOrder);
</script>