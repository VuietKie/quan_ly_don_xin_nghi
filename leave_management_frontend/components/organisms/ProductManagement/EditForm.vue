<template>
  <div class="grid gap-4">
    <div class="grid gap-2">
      <Label for="name">Tên sản phẩm</Label>
      <Input id="name" v-model="form.name" type="text" placeholder="Tên sản phẩm" required />
    </div>

    <div class="grid gap-2">
      <Label for="description">Mô tả</Label>
      <Input id="description" v-model="form.description" type="text" placeholder="Mô tả" />
    </div>

    <div class="grid gap-2">
      <Label for="mainimage">Ảnh chính</Label>
      <div v-if="form.mainimage_url" class="mb-2">
        <img :src="form.mainimage_url" alt="mainimage" class="h-24 rounded" />
      </div>
      <Input id="mainimage" type="file" @change="(e: Event) => handleFileChange(e, 'main')" />
    </div>

    <div class="grid gap-2">
      <Label for="uploaded_images">Ảnh phụ</Label>
      <div class="flex gap-2 mb-2" v-if="form.images && form.images.length">
        <img v-for="img in form.images" :key="img.id" :src="img.image" class="h-16 rounded" />
      </div>
      <Input id="uploaded_images" type="file" @change="(e: Event) => handleFileChange(e, 'uploaded_images')" multiple/>
    </div>

    <div class="grid gap-2">
      <Label for="price">Giá</Label>
      <Input id="price" v-model="form.price" type="number" min="0" placeholder="Giá" />
    </div>

    <div class="grid gap-2">
      <Label for="flash_sale_price">Giá Flash Sale</Label>
      <Input id="flash_sale_price" v-model="flashSalePriceProxy" type="number" min="0" placeholder="Giá Flash Sale" />
    </div>

    <div class="grid gap-2">
      <Label for="flash_sale_start">Bắt đầu Flash Sale</Label>
      <Input id="flash_sale_start" v-model="flashSaleStartProxy" type="datetime-local" />
    </div>

    <div class="grid gap-2">
      <Label for="flash_sale_end">Kết thúc Flash Sale</Label>
      <Input id="flash_sale_end" v-model="flashSaleEndProxy" type="datetime-local" />
    </div>

    <div class="grid gap-2">
      <Label for="category">Danh mục</Label>
      <Select v-model="form.category">
        <SelectTrigger class="w-full">
          <SelectValue placeholder="Chọn danh mục" />
        </SelectTrigger>
        <SelectContent>
          <SelectItem v-for="category in categories" :key="category.id" :value="category.id">
            {{ category.name }}
          </SelectItem>
        </SelectContent>
      </Select>
    </div>

    <div class="grid gap-2">
      <Label for="is_active">Hiển thị</Label>
      <input id="is_active" v-model="form.is_active" type="checkbox" class="w-5 h-5" />
    </div>

    <div class="grid gap-4 p-4 mt-4 border rounded-lg bg-muted">
      <h3 class="font-medium text-lg">Thông tin sản phẩm</h3>
      
      <div class="grid gap-2">
        <Label>Ngày tạo</Label>
        <div class="p-2 bg-background rounded border">
          {{ formatDateTime(form.created_at) }}
        </div>
      </div>

      <div class="grid gap-2">
        <Label>Ngày cập nhật</Label>
        <div class="p-2 bg-background rounded border">
          {{ formatDateTime(form.updated_at) }}
        </div>
      </div>

      <div class="grid gap-2">
        <Label>Trạng thái Flash Sale</Label>
        <div class="p-2 bg-background rounded border flex items-center gap-2">
          <div :class="form.is_flash_sale_active ? 'bg-green-500' : 'bg-red-500'" class="w-2 h-2 rounded-full"></div>
          {{ form.is_flash_sale_active ? 'Đang Flash Sale' : 'Không trong Flash Sale' }}
        </div>
      </div>

      <div class="grid gap-2">
        <Label>Giá hiện tại</Label>
        <div class="p-2 bg-background rounded border font-medium text-primary">
          {{ formatPrice(form.current_price) }} đ
        </div>
      </div>
    </div>



    <div class="flex flex-col gap-2 sm:flex-row sm:justify-end">
      <Button type="submit" class="w-full sm:w-60" @click="submitForm">
        Lưu thay đổi
      </Button>
      <Button variant="destructive" type="button" class="w-full sm:w-60" @click="cancelCreate">
        Thoát
      </Button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed, watch } from 'vue'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select'
import { useToast } from 'vue-toastification'
import { useRouter, useRoute } from '#app'
import { useRuntimeConfig } from '#app'

const toast = useToast()
const router = useRouter()
const route = useRoute()
const config = useRuntimeConfig()

const emit = defineEmits(['save', 'cancel'])

const props = defineProps({
  product: {
    type: Object,
    required: true
  }
})

const form = ref({
  id: null,
  name: '',
  description: '',
  mainimage: null as File | null,
  mainimage_url: '',
  images: [] as Array<{ id: number; image: string; product: number }>,
  uploaded_images: [] as File[],
  price: 0,
  flash_sale_price: null as number | null,
  flash_sale_start: null as string | null,
  flash_sale_end: null as string | null,
  category: 0,
  is_active: true,
  created_at: '',
  updated_at: '',
  is_flash_sale_active: null,
  current_price: 0,
})

// Watch for changes in product prop
watch(() => props.product, (newProduct) => {
  if (newProduct) {
    form.value = {
      ...form.value,
      ...(newProduct && typeof newProduct === 'object'
        ? {
            ...newProduct,
            mainimage: null,
            mainimage_url: newProduct.mainimage,
            images: newProduct.images || [],
            price: Number(newProduct.price),
            flash_sale_price: newProduct.flash_sale_price ? Number(newProduct.flash_sale_price) : null,
            flash_sale_start: newProduct.flash_sale_start ? new Date(newProduct.flash_sale_start).toISOString().slice(0, 16) : null,
            flash_sale_end: newProduct.flash_sale_end ? new Date(newProduct.flash_sale_end).toISOString().slice(0, 16) : null,
          }
        : {}),
    }
  }
}, { immediate: true })

const categories = ref<Array<{ id: number; name: string }>>([])

const fetchCategories = async () => {
  try {
    const res = await $fetch(`${config.public.apiBase}/catalogue/categories/`)
    categories.value = res as Array<{ id: number; name: string }>
  } catch (e) {
    toast.error('Không thể tải danh sách danh mục')
  }
}

// Proxy for flash_sale_price to avoid null assignment to Input
const flashSalePriceProxy = computed({
  get() {
    return form.value.flash_sale_price ?? ''
  },
  set(val: string | number) {
    form.value.flash_sale_price = val === '' ? null : Number(val)
  }
})

// Proxy for flash_sale_start to avoid null assignment to Input
const flashSaleStartProxy = computed({
  get() {
    return form.value.flash_sale_start ?? ''
  },
  set(val: string | number) {
    form.value.flash_sale_start = val === '' ? null : String(val)
  }
})

// Proxy for flash_sale_end to avoid null assignment to Input
const flashSaleEndProxy = computed({
  get() {
    return form.value.flash_sale_end ?? ''
  },
  set(val: string | number) {
    form.value.flash_sale_end = val === '' ? null : String(val)
  }
})


onMounted(() => {
  fetchCategories()
})

const handleFileChange = (e: Event, type: 'main' | 'uploaded_images' = 'main') => {
  const target = e.target as HTMLInputElement
  if (target.files && target.files.length > 0) {
    if (type === 'main') {
      form.value.mainimage = target.files[0]
    } else {
      form.value.uploaded_images = Array.from(target.files)
    }
  }
}

const handleCategoryChange = () => {
  // Handle category change if needed
}

const submitForm = () => {
  const { name, price, category } = form.value
  if (!name || !price || category === null) {
    toast.error('Vui lòng điền đầy đủ thông tin bắt buộc!')
    return
  }

  const formData = new FormData()

  // Thêm các trường cơ bản
  formData.append('name', form.value.name)
  formData.append('description', form.value.description || '')
  formData.append('price', String(form.value.price))
  formData.append('category', String(form.value.category))
  formData.append('is_active', String(form.value.is_active))
  
  // Thêm các trường flash sale
  if (form.value.flash_sale_price !== null) {
    formData.append('flash_sale_price', String(form.value.flash_sale_price))
  }
  if (form.value.flash_sale_start !== null) {
    formData.append('flash_sale_start', form.value.flash_sale_start)
  }
  if (form.value.flash_sale_end !== null) {
    formData.append('flash_sale_end', form.value.flash_sale_end)
  }

  // Thêm ảnh chính nếu có
  if (form.value.mainimage instanceof File) {
    formData.append('mainimage', form.value.mainimage)
  }

  // Thêm các ảnh phụ nếu có
  if (form.value.uploaded_images.length > 0) {
    form.value.uploaded_images.forEach((file) => {
      formData.append('uploaded_images', file)
    })
  }

  emit('save', formData)
}

const cancelCreate = () => {
  emit('cancel')
}

const formatDateTime = (dateString: string) => {
  if (!dateString) return '';
  const date = new Date(dateString);
  return new Intl.DateTimeFormat('vi-VN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
    second: '2-digit',
    hour12: false
  }).format(date);
}

const formatPrice = (price: number) => {
  return new Intl.NumberFormat('vi-VN').format(price);
}
</script>
