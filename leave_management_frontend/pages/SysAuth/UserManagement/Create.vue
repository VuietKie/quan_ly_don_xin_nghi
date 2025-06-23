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
                      <BreadcrumbLink href="/user-management">Quản lý người dùng</BreadcrumbLink>
                  </BreadcrumbItem>
                  <BreadcrumbSeparator class="hidden md:block" />
                  <BreadcrumbItem>
                      <BreadcrumbPage>Thêm người dùng</BreadcrumbPage>
                  </BreadcrumbItem>
              </BreadcrumbList>
          </Breadcrumb>
      </template>
      
      <div class="flex flex-1 flex-col gap-4 p-4 pt-0">
          <h1 class="text-2xl font-bold">Thêm người dùng</h1>
          <CreateForm @create-user="handleCreateUser" />
      </div>
  </MainTemplate>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useToast } from 'vue-toastification'
import { useRuntimeConfig, useCookie, navigateTo } from '#app'
import CreateForm from '~/components/organisms/SysAuth/UserManagement/CreateForm.vue'
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

const handleCreateUser = async (form: { username: string; password: string; first_name: string; last_name: string }) => {
  console.log("Nhận dữ liệu từ form:", form);

  try {
    const response = await $fetch(`${config.public.apiBase}/users/register/`, {
      method: "POST",
      body: form,
      headers: {
        "Content-Type": "application/json"
      }
    });

    console.log("Kết quả đăng ký:", response);
    toast.success("Người dùng đã được tạo thành công!");
    router.push("/users");
  } catch (error) {
    console.error("Lỗi khi đăng ký:", error);
    toast.error("Không thể đăng ký. Vui lòng kiểm tra lại thông tin.");
  }
};
</script>
