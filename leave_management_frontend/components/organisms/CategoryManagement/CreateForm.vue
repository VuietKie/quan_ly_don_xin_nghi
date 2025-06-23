<template>
  <div class="grid gap-4">
    <div class="grid gap-2">
      <Label for="name">Tên danh mục</Label>
      <Input id="name" v-model="form.name" type="text" placeholder="Tên danh mục" required />
    </div>

    <div class="grid gap-2">
      <Label for="description">Mô tả</Label>
      <Input id="description" v-model="form.description" type="text" placeholder="Mô tả" />
    </div>

    <div class="grid gap-2">
      <div class="grid gap-2">
        <Label for="is_active">Trạng thái</Label>
        <input id="is_active" v-model="form.is_active" type="checkbox" class="w-5 h-5" />
      </div>
    </div>

    <div class="flex flex-col gap-2 sm:flex-row sm:justify-end">
      <Button type="submit" class="w-full sm:w-60" @click="submitForm">
        Tạo danh mục
      </Button>
      <Button variant="destructive" type="button" class="w-full sm:w-60" @click="cancelCreate">
        Thoát
      </Button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, h } from 'vue'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'
import { useToast } from 'vue-toastification'
import { useRouter } from '#app'

const toast = useToast()
const router = useRouter()

const emit = defineEmits(['create-category'])

const form = ref({
  name: '',
  description: '',
  is_active: true,
})

const submitForm = () => {
  const { name } = form.value
  if (!name) {
    toast.error('Vui lòng nhập tên danh mục!')
    return
  }

  emit('create-category', form.value)
}

const cancelCreate = () => {
  toast.warning(h('div', [
    'Bạn có chắc chắn muốn thoát không? Dữ liệu chưa lưu sẽ bị mất.',
    h('div', { class: 'flex space-x-2 mt-2' }, [
      h('button', {
        onClick: () => {
          toast.clear()
          router.push('/category-management')
        },
        class: 'px-4 py-1 bg-red-500 text-white rounded',
      }, 'Thoát')
    ])
  ]), {
    timeout: 10000,
    closeOnClick: false,
  })
}
</script> 