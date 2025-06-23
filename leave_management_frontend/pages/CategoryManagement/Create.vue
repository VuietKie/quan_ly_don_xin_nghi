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
                      <BreadcrumbPage>Thêm danh mục</BreadcrumbPage>
                  </BreadcrumbItem>
              </BreadcrumbList>
          </Breadcrumb>
      </template>
      
      <div class="flex flex-1 flex-col gap-4 p-4 pt-0">
          <h1 class="text-2xl font-bold">Thêm danh mục</h1>
          <CreateForm @create-category="handleCreateCategory" />
      </div>
  </MainTemplate>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useToast } from 'vue-toastification'
import { useRuntimeConfig, useCookie, navigateTo, useRouter } from '#app'
import MainTemplate from '~/components/layouts/MainTemplate.vue'
import Breadcrumb from '@/components/ui/breadcrumb/Breadcrumb.vue'
import BreadcrumbList from '@/components/ui/breadcrumb/BreadcrumbList.vue'
import BreadcrumbItem from '@/components/ui/breadcrumb/BreadcrumbItem.vue'
import BreadcrumbLink from '@/components/ui/breadcrumb/BreadcrumbLink.vue'
import BreadcrumbSeparator from '@/components/ui/breadcrumb/BreadcrumbSeparator.vue'
import BreadcrumbPage from '@/components/ui/breadcrumb/BreadcrumbPage.vue'
import CreateForm from '@/components/organisms/CategoryManagement/CreateForm.vue'

const toast = useToast();
const router = useRouter();
const config = useRuntimeConfig();

const handleCreateCategory = async (form: { name: string; description: string; is_active: boolean }) => {
  try {
    const token = useCookie("access_token");
    const response = await $fetch(`${config.public.apiBase}/catalogue/categories/`, {
      method: "POST",
      body: form,
      headers: {
        "Content-Type": "application/json",
        Authorization: `Bearer ${token.value}`
      }
    });

    console.log("Kết quả tạo danh mục:", response);
    toast.success("Danh mục đã được tạo thành công!");
    router.push("/category-management");
  } catch (error) {
    console.error("Lỗi khi tạo danh mục:", error);
    toast.error("Không thể tạo danh mục. Vui lòng kiểm tra lại thông tin.");
  }
};
</script>

<style>

</style>