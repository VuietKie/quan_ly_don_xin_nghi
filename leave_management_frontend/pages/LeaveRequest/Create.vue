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
            <BreadcrumbLink href="/leave-request/list">Quản lý đơn xin nghỉ</BreadcrumbLink>
          </BreadcrumbItem>
          <BreadcrumbSeparator class="hidden md:block" />
          <BreadcrumbItem>
            <BreadcrumbPage>Thêm đơn xin nghỉ</BreadcrumbPage>
          </BreadcrumbItem>
        </BreadcrumbList>
      </Breadcrumb>
    </template>
    <div class="flex flex-1 flex-col gap-4 p-4 pt-0">
      <h1 class="text-2xl font-bold">Thêm đơn xin nghỉ</h1>
      <CreateForm @submit="handleCreateLeaveRequest" />
    </div>
  </MainTemplate>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useToast } from 'vue-toastification'
import { useRuntimeConfig, useCookie, useRouter } from '#app'
import CreateForm from '~/components/organisms/LeaveRequest/CreateForm.vue'
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

const handleCreateLeaveRequest = async (formData: any) => {
  try {
    const response = await $fetch(`${config.public.apiBase}/leave-request/create`, {
      method: "POST",
      headers: {
        'Content-Type': 'application/json',
        Authorization: `Bearer ${token.value}`,
      },
      body: formData,
    });
    toast.success("Đơn xin nghỉ đã được tạo thành công!");
    router.push("/leave-request/my-request");
  } catch (error) {
    console.error("Lỗi khi tạo đơn xin nghỉ:", error);
    toast.error("Không thể tạo đơn xin nghỉ. Vui lòng kiểm tra lại thông tin.");
  }
};
</script>

<style>

</style>