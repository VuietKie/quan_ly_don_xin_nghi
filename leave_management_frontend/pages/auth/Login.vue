<template>
  <LoginForm @login="handleLogin" />
</template>

<script lang="ts" setup>
import { useRouter } from "vue-router";
import { useAuthStore } from "@/stores/auth"; // Import auth store
import { useToast } from "vue-toastification";
import LoginForm from '~/components/organisms/auth/LoginForm.vue'
import { useCookie } from '#app';

const loading = ref(false);

const router = useRouter();
const toast = useToast();
const config = useRuntimeConfig(); 

interface Department {
  departmentId: number;
  departmentName: string;
  idManager: number;
}

interface Role {
  roleId: number;
  roleName: string;
}

interface User {
  userId: number;
  username: string;
  password: string;
  fullName: string;
  department: Department;
  role: Role;
  email: string;
}

const handleLogin = async ({ username, password }: { username: string; password: string }) => {
  loading.value = true;
  try {
    // 1. Kiểm tra username, password bằng phương thức GET
    const userRes = await $fetch<User>(`${config.public.apiBase}/auth/user`, {
      method: "GET",
      params: { username, password },
    });

    if (!userRes) {
      toast.error("Tên đăng nhập hoặc mật khẩu không đúng.");
      loading.value = false;
      return;
    }

    // 2. Gọi API login để lấy token, username, Role
    const loginRes = await $fetch<{ token: string; username: string; role: string }>(
      `${config.public.apiBase}/auth/login`,
      {
        method: "POST",
        body: { username, password },
      }
    );

    if (!loginRes || !loginRes.token) {
      toast.error("Đăng nhập thất bại. Không nhận được token.");
      loading.value = false;
      return;
    }

    // Lưu token vào cookie
    const accessToken = useCookie<string | null>("access_token", {
      maxAge: 60 * 60 * 24 * 7, // 7 ngày
      sameSite: 'lax',
      path: '/',
      secure: process.client && location.protocol === 'https:',
    });
    accessToken.value = loginRes.token;

    // Lưu thông tin user vào store nếu muốn (nếu có useAuthStore)
    const authStore = useAuthStore();
    authStore.setUser(userRes);
    authStore.setRoles([loginRes.role]);

    toast.success("Đăng nhập thành công!");
    router.push({ path: "/" });
  } catch (err: any) {
    toast.error("Đăng nhập thất bại. " + (err?.message || ""));
  } finally {
    loading.value = false;
  }
};

</script>

<style>

</style>