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
            <BreadcrumbLink href="/">Quản lý danh mục</BreadcrumbLink>
          </BreadcrumbItem>
          <BreadcrumbSeparator class="hidden md:block" />
          <BreadcrumbItem>
            <BreadcrumbPage>Danh sách danh mục</BreadcrumbPage>
          </BreadcrumbItem>
        </BreadcrumbList>
      </Breadcrumb>
    </template>

    <div class="flex flex-col gap-4 p-4 pt-0">
      <h1 class="text-2xl font-bold">Danh sách danh mục</h1>
      <div class="flex justify-end">
        <NuxtLink to="/category-management/create">
          <Button class="btn btn-primary">Thêm danh mục</Button>
        </NuxtLink>
      </div>
      <PaginatedTable 
        :headers="headers" 
        :items="categories" 
        :entity="entity"
        @delete="confirmDelete"
        @activate="confirmActivate"
      />
    </div>
  </MainTemplate>
</template>

<script setup lang="ts">
import { ref, onMounted, h } from 'vue';
import { useToast } from 'vue-toastification';
import { useRuntimeConfig, useCookie } from '#app';
import { Button } from '@/components/ui/button';
import MainTemplate from '~/components/layouts/MainTemplate.vue';
import Breadcrumb from '@/components/ui/breadcrumb/Breadcrumb.vue';
import BreadcrumbList from '@/components/ui/breadcrumb/BreadcrumbList.vue';
import BreadcrumbItem from '@/components/ui/breadcrumb/BreadcrumbItem.vue';
import BreadcrumbLink from '@/components/ui/breadcrumb/BreadcrumbLink.vue';
import BreadcrumbSeparator from '@/components/ui/breadcrumb/BreadcrumbSeparator.vue';
import BreadcrumbPage from '@/components/ui/breadcrumb/BreadcrumbPage.vue';
import PaginatedTable from "~/components/molecules/PaginatedTable.vue";

interface Category {
  id: number;
  name: string;
  description: string;
  is_active: boolean | string;
}

const toast = useToast();

const categories = ref<Category[]>([]);
const totalCategories = ref(0);
const router = useRouter();
const limit = ref(10);
const entity = "category-management";

const headers = [
  { text: "ID", value: "id" },
  { text: "Tên danh mục", value: "name" },
  { text: "Mô tả", value: "description" },
  { text: "Trạng thái", value: "is_active" },
  { text: "Thao tác", value: "actions", sortable: false },
];

const fetchCategories = async () => {
  try {
    const config = useRuntimeConfig();
    const token = useCookie("access_token");

    if (!token.value) return;

    const response = await $fetch<Category[]>(`${config.public.apiBase}/catalogue/categories/`, {
      headers: { Authorization: `Bearer ${token.value}` },
    });

    categories.value = response.map((category) => ({
      id: category.id,
      name: category.name,
      description: category.description,
      is_active: category.is_active ? "✅ Active" : "❌ Inactive",
    }));

    totalCategories.value = categories.value.length;
  } catch (error) {
    console.error("Error fetching categories:", error);
  }
};

const confirmDelete = (id: number) => {
  toast.warning(h('div', [
    'Bạn có chắc chắn muốn xóa danh mục này?',
    h('div', { class: 'flex space-x-2 mt-2' }, [
      h('button', {
        onClick: async () => {
          try {
            await deleteCategory(id);
            toast.clear();
          } catch (error) {
            console.error('Error deleting category:', error);
            toast.error('Có lỗi xảy ra khi xóa danh mục.');
          }
        },
        class: 'px-4 py-1 bg-red-500 text-white rounded',
      }, 'Xác nhận'),
      h('button', {
        onClick: () => toast.clear(),
        class: 'px-4 py-1 bg-gray-500 text-white rounded',
      }, 'Hủy')
    ])
  ]), {
    timeout: 10000,
    closeOnClick: false,
  });
};

const confirmActivate = (id: number) => {
  toast.warning(h('div', [
    'Bạn có chắc chắn muốn kích hoạt danh mục này?',
    h('div', { class: 'flex space-x-2 mt-2' }, [
      h('button', {
        onClick: async () => {
          try {
            await activateCategory(id);
            toast.clear();
          } catch (error) {
            console.error('Error activating category:', error);
            toast.error('Có lỗi xảy ra khi kích hoạt danh mục.');
          }
        },
        class: 'px-4 py-1 bg-green-500 text-white rounded',
      }, 'Xác nhận'),
      h('button', {
        onClick: () => toast.clear(),
        class: 'px-4 py-1 bg-gray-500 text-white rounded',
      }, 'Hủy')
    ])
  ]), {
    timeout: 10000,
    closeOnClick: false,
  });
};

const deleteCategory = async (id: number) => {
  try {
    const config = useRuntimeConfig();
    const token = useCookie("access_token");

    if (!token.value) return;

    await $fetch(`${config.public.apiBase}/catalogue/categories/${id}/`, {
      method: "PATCH",
      headers: { Authorization: `Bearer ${token.value}` },
      body: { is_active: false },
    });

    fetchCategories();
  } catch (error) {
    console.error("Error deleting category:", error);
  }
};

const activateCategory = async (id: number) => {
  try {
    const config = useRuntimeConfig();
    const token = useCookie("access_token");

    if (!token.value) return;

    await $fetch(`${config.public.apiBase}/catalogue/categories/${id}/`, {
      method: "PATCH",
      headers: { Authorization: `Bearer ${token.value}` },
      body: { is_active: true },
    });

    fetchCategories();
  } catch (error) {
    console.error("Error activating category:", error);
  }
};

onMounted(() => {
  fetchCategories();
});
</script>
