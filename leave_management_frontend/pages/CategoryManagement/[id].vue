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
            <BreadcrumbLink href="/category-management">Quản lý danh mục</BreadcrumbLink>
          </BreadcrumbItem>
          <BreadcrumbSeparator class="hidden md:block" />
          <BreadcrumbItem>
            <BreadcrumbPage>Chỉnh sửa</BreadcrumbPage>
          </BreadcrumbItem>
        </BreadcrumbList>
      </Breadcrumb>
    </template>

    <div class="flex flex-1 flex-col gap-4 p-4 pt-0">
      <h1 class="text-2xl font-bold">Chỉnh sửa danh mục</h1>
      <div v-if="category" class="grid gap-4">
        <div class="grid gap-2">
          <Label for="name">Tên danh mục</Label>
          <Input id="name" v-model="form.name" type="text" placeholder="Tên danh mục" required />
        </div>

        <div class="grid gap-2">
          <Label for="description">Mô tả</Label>
          <Input id="description" v-model="form.description" type="text" placeholder="Mô tả" />
        </div>

        <div class="flex items-center gap-2">
          <input id="is_active" type="checkbox" v-model="form.is_active" class="w-4 h-4" />
          <Label for="is_active">Kích hoạt danh mục</Label>
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
import { ref, reactive, onMounted, h } from 'vue'
import { useToast } from 'vue-toastification'
import { useRuntimeConfig, useCookie, useRoute, useRouter } from '#app'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'
import MainTemplate from '~/components/layouts/MainTemplate.vue'
import Breadcrumb from '@/components/ui/breadcrumb/Breadcrumb.vue'
import BreadcrumbList from '@/components/ui/breadcrumb/BreadcrumbList.vue'
import BreadcrumbItem from '@/components/ui/breadcrumb/BreadcrumbItem.vue'
import BreadcrumbLink from '@/components/ui/breadcrumb/BreadcrumbLink.vue'
import BreadcrumbSeparator from '@/components/ui/breadcrumb/BreadcrumbSeparator.vue'
import BreadcrumbPage from '@/components/ui/breadcrumb/BreadcrumbPage.vue'

interface Category {
  id: number
  name: string
  description: string
  is_active: boolean
}

const route = useRoute()
const router = useRouter()
const toast = useToast()
const category = ref<Category | null>(null)
const form = reactive<Category>({
  id: 0,
  name: '',
  description: '',
  is_active: true
})

const fetchCategory = async () => {
  try {
    const config = useRuntimeConfig()
    const token = useCookie('access_token')

    const response = await $fetch<Category>(`${config.public.apiBase}/catalogue/categories/${route.params.id}/`, {
      headers: { Authorization: `Bearer ${token.value}` },
    })
    category.value = response
    Object.assign(form, response)
  } catch (error) {
    console.error('Lỗi khi lấy thông tin danh mục:', error)
    toast.error('Không tìm thấy thông tin danh mục!')
    router.push('/category-management')
  }
}

const saveChanges = async () => {
  try {
    const config = useRuntimeConfig()
    const token = useCookie('access_token')

    await $fetch(`${config.public.apiBase}/catalogue/categories/${form.id}/`, {
      method: 'PATCH',
      headers: {
        Authorization: `Bearer ${token.value}`,
        'Content-Type': 'application/json',
      },
      body: {
        name: form.name,
        description: form.description,
        is_active: form.is_active,
      },
    })

    toast.success('Cập nhật thành công!')
    router.push('/category-management')
  } catch (error) {
    console.error('Lỗi khi cập nhật danh mục:', error)
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
            router.push('/category-management')
          },
          class: 'px-4 py-2 bg-red-500 text-white rounded hover:bg-red-700 transition',
        },
        'Thoát'
      ),
    ]),
    { timeout: false, closeOnClick: false }
  )
}

onMounted(fetchCategory)
</script>

<style>

</style>