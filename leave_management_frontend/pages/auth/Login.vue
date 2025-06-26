<template>
  <LoginForm @login="handleLogin" />
</template>

<script lang="ts" setup>
import { useRouter } from "vue-router";
import { useToast } from "vue-toastification";
import LoginForm from '~/components/organisms/auth/LoginForm.vue'
import { useCookie } from '#app';

const loading = ref(false);

const router = useRouter();
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

const handleLogin = async (form: { username: string; password: string }) => {
  loading.value = true;
  try {
    const response = await fetch(`${config.public.apiBase}/auth/login`, {
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
    // Lưu token, tên và Role vào cookies
    const token = useCookie('access_token', { maxAge: 60 * 60 * 24 * 7, sameSite: 'lax', path: '/' }); // 7 ngày, toàn site
    token.value = data.token;
    const userName = useCookie('user_name');
    userName.value = data.fullName || data.username || '';
    const role = useCookie('role');
    role.value = data.Role || '';

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
    const response = await fetch(`${config.public.apiBase}/auth/user`);
    if (!response.ok) throw new Error("Không thể lấy danh sách người dùng");
    const users = await response.json();
    const user = users.find((u: User) => u.username === username);
    if (user) {
      localStorage.setItem("user", JSON.stringify(user));
    }
  } catch (error) {
    console.error("Lỗi lấy thông tin user:", error);
  }
};
</script>
