<template>
  <MainTemplate>
    <template #header>
      <Breadcrumb>
        <BreadcrumbList>
          <BreadcrumbItem class="hidden md:block">
            <BreadcrumbLink href="/">Trang chủ</BreadcrumbLink>
          </BreadcrumbItem>
          <BreadcrumbSeparator class="hidden md:block" />
          <BreadcrumbItem>
            <BreadcrumbPage>Trang dashboard</BreadcrumbPage>
          </BreadcrumbItem>
        </BreadcrumbList>
      </Breadcrumb>
    </template>
    <!-- Nội dung chính của trang -->
    <div class="flex flex-1 flex-col gap-4 p-4 pt-0">
      <!-- Nội dung của bạn ở đây -->
       <ProfileForm v-if="user" :user="user"/>
    </div>
  </MainTemplate>
</template>
<script lang="ts" setup>
import MainTemplate from '~/components/layouts/MainTemplate.vue'
import Breadcrumb from '@/components/ui/breadcrumb/Breadcrumb.vue'
import BreadcrumbList from '@/components/ui/breadcrumb/BreadcrumbList.vue'
import BreadcrumbItem from '@/components/ui/breadcrumb/BreadcrumbItem.vue'
import BreadcrumbLink from '@/components/ui/breadcrumb/BreadcrumbLink.vue'
import BreadcrumbSeparator from '@/components/ui/breadcrumb/BreadcrumbSeparator.vue'
import BreadcrumbPage from '@/components/ui/breadcrumb/BreadcrumbPage.vue'

import ProfileForm from '~/components/organisms/auth/ProfileForm.vue'

interface User {
  first_name: string
  last_name: string
  username: string
  phone_number: string
  address: string
}



const user = ref<User | null>(null);
const config = useRuntimeConfig();
const token = useCookie('access_token');
const fetchUserProfile = async () => {
  try {
    const data = await $fetch<User>(`${config.public.apiBase}/users/user/profile`, {headers: {
                Authorization: `Bearer ${token.value}`
            }});

    if (data) {
      user.value = data;
    }
  } catch (error) {
    console.error("Lỗi khi lấy thông tin user:", error);
  }
};


onMounted(fetchUserProfile);
</script>