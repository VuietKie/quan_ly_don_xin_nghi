<template>
  <LoginForm @login="handleLogin" />
</template>

<script lang="ts" setup>
import { useRouter } from "vue-router";
import { useAuthStore } from "@/stores/auth"; // Import auth store
import { useToast } from "vue-toastification";
import LoginForm from '~/components/organisms/auth/LoginForm.vue'

const loading = ref(false);

const router = useRouter();
const authStore = useAuthStore();
const toast = useToast();
const config = useRuntimeConfig(); // <-- thêm dòng này

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

const fetchUsers = async (): Promise<User[]> => {
  const response = await fetch(`${config.public.apiBase}/list/user`);
  if (!response.ok) throw new Error("Không thể lấy danh sách người dùng");
  return await response.json();
};

const handleLogin = async (form: { username: string; password: string }) => {
  loading.value = true;
  try {
    const response = await fetch(`${config.public.apiBase}/list/login`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify(form),
    });

    if (!response.ok) {
      throw new Error("Đăng nhập thất bại! Hãy kiểm tra lại tài khoản và mật khẩu.");
    }

    const data = await response.json();
    // Lưu token vào localStorage hoặc store
    localStorage.setItem("token", data.token);

    // Gọi hàm lấy thông tin user chi tiết
    await fetchAndStoreUserInfo(data.username);

    toast.success("Đăng nhập thành công!");
    router.push("/");
  } catch (error) {
    console.error("Lỗi đăng nhập:", error);
    toast.error("Đăng nhập thất bại! Hãy kiểm tra lại tài khoản và mật khẩu.");
  } finally {
    loading.value = false;
  }
};

const fetchAndStoreUserInfo = async (username: string) => {
  try {
    const response = await fetch(`${config.public.apiBase}/list/user`);
    if (!response.ok) throw new Error("Không thể lấy danh sách người dùng");
    const users = await response.json();
    const user = users.find((u: User) => u.username === username);
    if (user) {
      // Lưu vào localStorage hoặc store
      localStorage.setItem("user", JSON.stringify(user));
      // Nếu dùng Pinia/Vuex, có thể lưu vào store ở đây
      // authStore.setUser(user);
    }
  } catch (error) {
    console.error("Lỗi lấy thông tin user:", error);
  }
};
</script>
