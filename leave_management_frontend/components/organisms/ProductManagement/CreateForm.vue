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
      <Input id="mainimage" type="file" @change="(e: Event) => handleFileChange(e, 'main')" />
    </div>

    <div class="grid gap-2">
      <Label for="uploaded_images">Ảnh phụ</Label>
      <Input id="uploaded_images" type="file" @change="(e: Event) => handleFileChange(e, 'uploaded_images')" multiple/>
    </div>

    <div class="grid gap-2">
      <div class="grid gap-2">
        <Label for="price">Giá</Label>
        <Input id="price" v-model="form.price" type="number" min="0" placeholder="Giá" />
      </div>
    </div>

    <div class="grid gap-2">
      <div class="grid gap-2">
        <Label for="category">Danh mục</Label>
        <Input id="category" v-model="form.category" type="number" placeholder="ID danh mục" />
      </div>

      <div class="grid gap-2">
        <div class="grid gap-2">
          <Label for="is_active">Hiển thị</Label>
          <input id="is_active" v-model="form.is_active" type="checkbox" class="w-5 h-5" />
        </div>
      </div>
    </div>

    <div class="flex flex-col gap-2 sm:flex-row sm:justify-end">
      <Button type="submit" class="w-full sm:w-60" @click="submitForm">
        Tạo sản phẩm
      </Button>
      <Button variant="destructive" type="button" class="w-full sm:w-60" @click="cancelCreate">
        Thoát
      </Button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'
import { useToast } from 'vue-toastification'
import { useRouter } from '#app'

const toast = useToast()
const router = useRouter()

const emit = defineEmits(['create-product', 'reset-form'])

const form = ref({
  name: '',
  description: '',
  mainimage: null as File | null,
  uploaded_images: [] as File[], // Đổi tên trường
  price: 0,
  category: 0,
  is_active: true,
})

const handleFileChange = (e: Event, type: 'main' | 'uploaded_images' = 'main') => {
  const target = e.target as HTMLInputElement
  if (target.files && target.files.length > 0) {
    if (type === 'main') {
      form.value.mainimage = target.files[0]
    } else {
      form.value.uploaded_images = Array.from(target.files)
      console.log('Selected images:', form.value.uploaded_images)
    }
  }
}

const submitForm = () => {
  const { name, price, category } = form.value
  if (!name || !price || category === null) {
    toast.error('Vui lòng điền đầy đủ thông tin bắt buộc!')
    return
  }

  const formData = new FormData()

  for (const [key, value] of Object.entries(form.value)) {
    if (key === 'uploaded_images' && Array.isArray(value)) {
      value.forEach((file) => {
        formData.append('uploaded_images', file)
      })
    } else if (value !== null && value !== undefined && !(value instanceof Array)) {
      if (value instanceof File) {
        formData.append(key, value)
      } else {
        formData.append(key, String(value))
      }
    }
  }

  emit('create-product', formData)
}


const cancelCreate = () => {
  toast.warning(h("div", [
    "Bạn có chắc chắn muốn thoát không? Dữ liệu chưa lưu sẽ bị mất.",
    h("button", {
      onClick: () => {
        toast.clear()
        router.push('/product-management')
      },
      class: "ml-4 px-3 py-1 bg-red-500 text-white rounded hover:bg-red-700 transition"
    }, "Thoát")
  ]), {
    timeout: 10000,
    closeOnClick: false,
  })
}
</script>
