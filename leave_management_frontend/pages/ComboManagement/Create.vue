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
            <BreadcrumbPage>Thêm combo mới</BreadcrumbPage>
          </BreadcrumbItem>
        </BreadcrumbList>
      </Breadcrumb>
    </template>

    <div class="flex flex-1 flex-col gap-4 p-4 pt-0">
      <h1 class="text-2xl font-bold">Thêm combo mới</h1>
      <div class="grid gap-4">
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

        <div class="flex items-center gap-2">
          <input id="is_active" type="checkbox" v-model="form.is_active" class="w-4 h-4" />
          <Label for="is_active">Kích hoạt combo</Label>
        </div>

        <!-- Combo Items Section -->
        <div class="border rounded-lg p-4">
          <h2 class="text-lg font-semibold mb-4">Sản phẩm trong combo</h2>
          
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

          <!-- Combo Items List -->
          <div class="space-y-2">
            <div v-for="(item, index) in form.combo_items" :key="index" class="flex items-center gap-4 p-2 border rounded">
              <span class="flex-1">ID: {{ item.product }}</span>
              <Input 
                v-model="item.quantity" 
                type="number" 
                class="w-32"
                min="1"
              />
              <Button variant="destructive" @click="removeComboItem(index)">
                Xóa
              </Button>
            </div>
          </div>
        </div>

        <div class="flex flex-col gap-2 sm:flex-row sm:justify-end">
          <Button type="submit" class="w-full sm:w-60" @click="createCombo">
            Tạo combo
          </Button>
          <Button variant="destructive" type="button" class="w-full sm:w-60" @click="cancelCreate">
            Hủy
          </Button>
        </div>
      </div>
    </div>
  </MainTemplate>
</template>

<script setup lang="ts">
import { ref, onMounted, h } from 'vue'
import { useToast } from 'vue-toastification'
import { useRuntimeConfig, useCookie, useRouter } from '#app'
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

interface ComboItem {
  product: number;
  quantity: number;
}

const router = useRouter()
const toast = useToast()
const config = useRuntimeConfig()
const token = useCookie('access_token')

const newItem = ref<ComboItem>({ product: 0, quantity: 1 })

const form = ref({
  name: '',
  description: '',
  discount_amount: 0,
  is_active: true,
  combo_items: [] as ComboItem[]
})

const addItem = () => {
  if (newItem.value.product && newItem.value.quantity) {
    form.value.combo_items.push({ 
      product: Number(newItem.value.product), 
      quantity: Number(newItem.value.quantity) 
    })
    newItem.value = { product: 0, quantity: 1 }
  }
}

const removeComboItem = (index: number) => {
  form.value.combo_items.splice(index, 1)
}

const createCombo = async () => {
  try {
    if (!token.value) return;

    if (!form.value.name) {
      toast.error('Vui lòng nhập tên combo!')
      return
    }

    if (form.value.combo_items.length === 0) {
      toast.error('Vui lòng thêm ít nhất một sản phẩm vào combo!')
      return
    }

    await $fetch(`${config.public.apiBase}/catalogue/combos/`, {
      method: 'POST',
      headers: { 
        Authorization: `Bearer ${token.value}`,
        'Content-Type': 'application/json'
      },
      body: JSON.stringify(form.value),
    })

    toast.success('Tạo combo thành công!')
    router.push('/combo-management')
  } catch (error) {
    console.error('Lỗi khi tạo combo:', error)
    toast.error('Tạo combo thất bại!')
  }
}

const cancelCreate = () => {
  toast.warning(
    h('div', { class: 'space-y-2' }, [
      h('p', 'Bạn có chắc chắn muốn hủy không? Dữ liệu đã nhập sẽ bị mất.'),
      h(
        'button',
        {
          onClick: () => {
            toast.clear()
            router.push('/combo-management')
          },
          class: 'px-4 py-2 bg-red-500 text-white rounded hover:bg-red-700 transition',
        },
        'Hủy'
      ),
    ]),
    { timeout: false, closeOnClick: false }
  )
}
</script>

<style>
/* Hide number input spinners */
input[type="number"]::-webkit-inner-spin-button,
input[type="number"]::-webkit-outer-spin-button {
  -webkit-appearance: none;
  margin: 0;
}

input[type="number"] {
  -moz-appearance: textfield;
}
</style>