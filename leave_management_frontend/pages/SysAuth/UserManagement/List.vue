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
            <BreadcrumbLink href="/">Quản lý người dùng</BreadcrumbLink>
          </BreadcrumbItem>
          <BreadcrumbSeparator class="hidden md:block" />
          <BreadcrumbItem>
            <BreadcrumbPage>Danh sách người dùng</BreadcrumbPage>
          </BreadcrumbItem>
        </BreadcrumbList>
      </Breadcrumb>
    </template>

    <div class="flex flex-col gap-4 p-4 pt-0">
      <h1 class="text-2xl font-bold">Danh sách người dùng</h1>
      <div class="flex justify-end">
        <NuxtLink to="/user-management/create">
          <Button class="btn btn-primary">Thêm người dùng</Button>
        </NuxtLink>
      </div>
      <PaginatedTable 
        :headers="headers" 
        :items="users" 
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

interface User {
  id: number;
  username: string;
  first_name: string;
  last_name: string;
  fullname: string;
  is_active: boolean | string;
}

const toast = useToast();

const users = ref<User[]>([]);
const totalUsers = ref(0);
const router = useRouter();
const limit = ref(10);
const entity = "user-management";

const headers = [
  { text: "ID", value: "id" },
  { text: "Username", value: "username" },
  { text: "Full Name", value: "fullname" },
  { text: "Active", value: "is_active" },
  { text: "Actions", value: "actions", sortable: false },
];

const fetchUsers = async () => {
  try {
    const config = useRuntimeConfig();
    const token = useCookie("access_token");

    if (!token.value) return;

    const response = await $fetch<User[]>(`${config.public.apiBase}/users/admin/users`, {
      headers: { Authorization: `Bearer ${token.value}` },
    });

    users.value = response.map((user) => ({
      id: user.id,
      username: user.username,
      first_name: user.first_name,
      last_name: user.last_name,
      fullname: `${user.first_name} ${user.last_name}`.trim(),
      is_active: user.is_active ? "✅ Active" : "❌ Inactive",
    }));

    totalUsers.value = users.value.length;
  } catch (error) {
    console.error("Error fetching users:", error);
  }
};
const confirmDelete = (id: number) => {
  toast.warning(h('div', [
    'Bạn có chắc chắn muốn xóa tài khoản này?',
    h('div', { class: 'flex space-x-2 mt-2' }, [
      h('button', {
        onClick: async () => {
          try {
            await deleteUser(id); // Perform the deletion
            toast.clear(); // Clear the toast after the action is complete
          } catch (error) {
            console.error('Error deleting user:', error);
            toast.error('Có lỗi xảy ra khi xóa người dùng.');
          }
        },
        class: 'px-4 py-1 bg-red-500 text-white rounded',
      }, 'Xác nhận'),
      h('button', {
        onClick: () => toast.clear(), // Just clear the toast if the user cancels
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
    'Bạn có chắc chắn muốn kích hoạt tài khoản này?',
    h('div', { class: 'flex space-x-2 mt-2' }, [
      h('button', {
        onClick: async () => {
          try {
            await activateUser(id); // Perform the activation
            toast.clear(); // Clear the toast after the action is complete
          } catch (error) {
            console.error('Error activating user:', error);
            toast.error('Có lỗi xảy ra khi kích hoạt người dùng.');
          }
        },
        class: 'px-4 py-1 bg-green-500 text-white rounded',
      }, 'Xác nhận'),
      h('button', {
        onClick: () => toast.clear(), // Just clear the toast if the user cancels
        class: 'px-4 py-1 bg-gray-500 text-white rounded',
      }, 'Hủy')
    ])
  ]), {
    timeout: 10000,
    closeOnClick: false,
  });
};


const deleteUser = async (id: number) => {
  try {
    const config = useRuntimeConfig();
    const token = useCookie("access_token");

    if (!token.value) return;

    await $fetch(`${config.public.apiBase}/users/admin/user/${id}/`, {
      method: "PATCH",
      headers: { Authorization: `Bearer ${token.value}` },
      body: { is_active: false },
    });

    fetchUsers();
  } catch (error) {
    console.error("Error deleting user:", error);
  }
};

const activateUser = async (id: number) => {
  try {
    const config = useRuntimeConfig();
    const token = useCookie("access_token");

    if (!token.value) return;

    await $fetch(`${config.public.apiBase}/users/admin/user/${id}/`, {
      method: "PATCH",
      headers: { Authorization: `Bearer ${token.value}` },
      body: { is_active: true },
    });

    fetchUsers();
  } catch (error) {
    console.error("Error activating user:", error);
  }
};

onMounted(() => {
  fetchUsers();
});

</script>
