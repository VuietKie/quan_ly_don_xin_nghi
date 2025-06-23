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
                      <BreadcrumbLink href="/product-management">Quản lý sản phẩm</BreadcrumbLink>
                  </BreadcrumbItem>
                  <BreadcrumbSeparator class="hidden md:block" />
                  <BreadcrumbItem>
                      <BreadcrumbPage>Thêm sản phẩm</BreadcrumbPage>
                  </BreadcrumbItem>
              </BreadcrumbList>
          </Breadcrumb>
      </template>
      
      <div class="flex flex-1 flex-col gap-4 p-4 pt-0">
          <h1 class="text-2xl font-bold">Thêm sản phẩm</h1>
          <CreateForm @create-product="handleCreateProduct" />
      </div>
  </MainTemplate>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useToast } from 'vue-toastification'
import { useRuntimeConfig, useCookie, navigateTo } from '#app'
import CreateForm from '~/components/organisms/ProductManagement/CreateForm.vue'
import MainTemplate from '~/components/layouts/MainTemplate.vue'
import Breadcrumb from '@/components/ui/breadcrumb/Breadcrumb.vue'
import BreadcrumbList from '@/components/ui/breadcrumb/BreadcrumbList.vue'
import BreadcrumbItem from '@/components/ui/breadcrumb/BreadcrumbItem.vue'
import BreadcrumbLink from '@/components/ui/breadcrumb/BreadcrumbLink.vue'
import BreadcrumbSeparator from '@/components/ui/breadcrumb/BreadcrumbSeparator.vue'
import BreadcrumbPage from '@/components/ui/breadcrumb/BreadcrumbPage.vue'

const toast = useToast();
const router = useRouter();
const config = useRuntimeConfig();
const token = useCookie("access_token");
const handleCreateProduct = async (formData: FormData) => {
  console.log("Nhận dữ liệu từ form:", formData);

  try {
    const response = await $fetch(`${config.public.apiBase}/catalogue/products/`, {
      method: "POST",
      headers: {
        Authorization: `Bearer ${token.value}`,
      },
      body: formData,
    });

    console.log("Kết quả tạo sản phẩm:", response);
    toast.success("Sản phẩm đã được tạo thành công!");
    router.push("/product-management");
  } catch (error) {
    console.error("Lỗi khi tạo sản phẩm:", error);
    toast.error("Không thể tạo sản phẩm. Vui lòng kiểm tra lại thông tin.");
  }
};

</script>
