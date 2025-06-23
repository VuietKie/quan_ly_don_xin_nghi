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
            <BreadcrumbLink href="/combo-management">Quản lý combo</BreadcrumbLink>
          </BreadcrumbItem>
          <BreadcrumbSeparator class="hidden md:block" />
          <BreadcrumbItem>
            <BreadcrumbPage>Chỉnh sửa combo</BreadcrumbPage>
          </BreadcrumbItem>
        </BreadcrumbList>
      </Breadcrumb>
    </template>

    <div class="flex flex-1 flex-col gap-4 p-4 pt-0">
      <h1 class="text-2xl font-bold">Chỉnh sửa combo</h1>
      <div v-if="combo" class="grid gap-4">
        <div class="grid gap-2">
          <Label for="name">Tên combo</Label>
          <Input id="name" v-model="form.name" type="text" placeholder="Tên combo" required />
        </div>

        <div class="grid gap-2">
          <Label for="description">Mô tả</Label>
          <Input id="description" v-model="form.description" type="text" placeholder="Mô tả" />
        </div>

        <div class="grid gap-2">
          <Label for="discount_amount">Giảm giá (VNĐ)</Label>
          <Input id="discount_amount" v-model="form.discount_amount" type="number" placeholder="Giảm giá" />
        </div>

        <div class="mb-6">
          <label class="block text-sm font-medium text-gray-700 mb-1">Trạng thái</label>
          <div class="flex items-center gap-2">
            <input type="checkbox" v-model="form.is_active" class="w-4 h-4" />
            <span class="text-sm text-gray-700">Kích hoạt combo</span>
          </div>
        </div>

        <!-- Combo Items -->
        <div class="mb-6">
          <h3 class="text-lg font-medium text-gray-900 mb-4">Sản phẩm trong Combo</h3>
          
          <!-- Add New Item -->
          <div class="flex gap-4">
            <div class="flex-1">
              <label class="block text-sm font-medium text-gray-700 mb-1">ID sản phẩm</label>
              <Input
                v-model="newItem.product"
                placeholder="Nhập ID sản phẩm"
              />
            </div>
            <div class="w-32">
              <label class="block text-sm font-medium text-gray-700 mb-1">Số lượng</label>
              <Input
                v-model="newItem.quantity"
                type="text"
                placeholder="Nhập số lượng"
              />
            </div>
            <div class="flex items-end">
              <Button @click="addItem" class="bg-primary text-white">
                Thêm
              </Button>
            </div>
          </div>

          <!-- Items List -->
          <div class="mt-4">
            <div v-for="(item, index) in form.combo_items" :key="index" class="flex items-center gap-4 mb-2">
              <div class="flex-1">
                <Input
                  v-model="item.product"
                  placeholder="ID sản phẩm"
                  class="w-full"
                />
              </div>
              <div class="w-32">
                <Input
                  v-model="item.quantity"
                  type="text"
                  placeholder="Số lượng"
                  class="w-full"
                />
              </div>
              <Button @click="removeItem(index)" class="text-red-600">
                <i class="fas fa-trash"></i>
              </Button>
            </div>
          </div>
        </div>

        <div class="flex flex-col gap-2 sm:flex-row sm:justify-end">
          <Button type="submit" class="w-full sm:w-60" @click="saveChanges">
            Lưu thay đổi
          </Button>
          <Button variant="destructive" type="button" class="w-full sm:w-60" @click="cancelChanges">
            Thoát
          </Button>
        </div>
      </div>
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
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '@/components/ui/select'

interface ComboItem {
  product: number;
  quantity: number;
}

interface Combo {
  id: number;
  name: string;
  description: string;
  discount_amount: number;
  is_active: boolean;
  combo_items: ComboItem[];
}

interface Product {
  id: number;
  name: string;
}

const route = useRoute()
const router = useRouter()
const toast = useToast()
const config = useRuntimeConfig()
const token = useCookie('access_token')

const combo = ref<Combo | null>(null)
const availableProducts = ref<Product[]>([])
const newItem = ref<ComboItem>({ product: 0, quantity: 1 })

const form = ref({
  name: '',
  description: '',
  discount_amount: 0,
  is_active: true,
  combo_items: [] as ComboItem[]
})

const fetchCombo = async () => {
  try {
    if (!token.value) return;
    
    const response = await $fetch<Combo>(`${config.public.apiBase}/catalogue/combos/${route.params.id}/`, {
      headers: { 
        Authorization: `Bearer ${token.value}`,
        'Content-Type': 'application/json'
      },
    })
    combo.value = response
    form.value = {
      name: response.name,
      description: response.description,
      discount_amount: response.discount_amount,
      is_active: response.is_active,
      combo_items: Array.isArray(response.combo_items) ? [...response.combo_items] : []
    }
  } catch (error) {
    console.error('Lỗi khi lấy thông tin combo:', error)
    toast.error('Không tìm thấy thông tin combo!')
  }
}

const fetchProducts = async () => {
  try {
    if (!token.value) return;

    const response = await $fetch<Product[]>(`${config.public.apiBase}/catalogue/products/`, {
      headers: { 
        Authorization: `Bearer ${token.value}`,
        'Content-Type': 'application/json'
      },
    })
    availableProducts.value = Array.isArray(response) ? response : []
  } catch (error) {
    console.error('Lỗi khi lấy danh sách sản phẩm:', error)
    toast.error('Không thể tải danh sách sản phẩm!')
    availableProducts.value = []
  }
}

const getProductName = (productId: number) => {
  const product = availableProducts.value.find(p => p.id === productId)
  return product ? product.name : 'Không tìm thấy sản phẩm'
}

const addItem = () => {
  if (newItem.value.product && newItem.value.quantity) {
    form.value.combo_items.push({ 
      product: Number(newItem.value.product), 
      quantity: Number(newItem.value.quantity) 
    })
    newItem.value = { product: 0, quantity: 1 }
  }
}

const removeItem = (index: number) => {
  form.value.combo_items.splice(index, 1)
}

const saveChanges = async () => {
  try {
    if (!token.value) return;

    await $fetch(`${config.public.apiBase}/catalogue/combos/${route.params.id}/`, {
      method: 'PATCH',
      headers: { 
        Authorization: `Bearer ${token.value}`,
        'Content-Type': 'application/json'
      },
      body: JSON.stringify(form.value),
    })

    toast.success('Cập nhật thành công!')
    await fetchCombo()
  } catch (error) {
    console.error('Lỗi khi cập nhật combo:', error)
    toast.error('Cập nhật thất bại!')
  }
}

const cancelChanges = () => {
  toast.warning(
    h('div', { class: 'space-y-2' }, [
      h('p', 'Bạn có chắc chắn muốn thoát không? Dữ liệu chưa lưu sẽ bị mất.'),
      h(
        'button',
        {
          onClick: () => {
            toast.clear()
            router.push('/combo-management')
          },
          class: 'px-4 py-2 bg-red-500 text-white rounded hover:bg-red-700 transition',
        },
        'Thoát'
      ),
    ]),
    { timeout: false, closeOnClick: false }
  )
}

onMounted(() => {
  fetchCombo()
  fetchProducts()
})
</script>

<style>

</style>