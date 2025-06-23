import { defineStore } from "pinia";
import { useCookie, useRuntimeConfig } from "#app";

interface User {
  id: number;
  username: string;
  first_name: string;
  last_name: string;
  email: string;
}

export const useAuthStore = defineStore("auth", () => {
  const accessToken = useCookie<string | null>("access_token");
  const refreshToken = useCookie<string | null>("refresh_token");
  const user = ref<User | null>(null);
  const isAdmin = ref<boolean>(false); // ✅ Thêm biến kiểm tra admin
  const isRefreshing = ref<boolean>(false);

  const isAuthenticated = computed(() => !!accessToken.value);
  const config = useRuntimeConfig();

  const login = async (username: string, password: string) => {
    try {
      const data = await $fetch<{ access: string; refresh: string }>(
        `${config.public.apiBase}/users/login/`,
        {
          method: "POST",
          body: { username, password },
        }
      );

      if (!data || !data.access || !data.refresh) {
        throw new Error("Invalid response from server");
      }

      // ✅ Lưu token vào cookies
      accessToken.value = data.access;
      refreshToken.value = data.refresh;

      // ✅ Lấy thông tin user
      await fetchUser();
    } catch (err) {
      console.error("Login failed:", err);
      throw new Error("Đăng nhập thất bại");
    }
  };

  const fetchUser = async () => {
    if (!accessToken.value) return;

    try {
      const data = await $fetch<User>(`${config.public.apiBase}/users/user/profile/`, {
        headers: { Authorization: `Bearer ${accessToken.value}` },
      });

      if (!data) throw new Error("User data not found");

      user.value = data;
      console.log("user value", user.value);

      // ✅ Kiểm tra quyền admin
      await checkAdmin();
    } catch (err) {
      console.error("Failed to fetch user:", err);
      logout();
    }
  };

  const checkAdmin = async () => {
    try {
      const data = await $fetch<{ is_admin: boolean }>(
        `${config.public.apiBase}/users/is-admin/`,
        {
          headers: { Authorization: `Bearer ${accessToken.value}` },
        }
      );

      isAdmin.value = data.is_admin; // ✅ Cập nhật isAdmin từ API
    } catch (err) {
      console.error("Failed to check admin status:", err);
      isAdmin.value = false;
      throw new Error("Không thể xác minh quyền admin");
    }
  };

  const refreshAccessToken = async () => {
    if (isRefreshing.value) return;
    isRefreshing.value = true;

    try {
      if (!refreshToken.value) throw new Error("No refresh token available");

      const data = await $fetch<{ access: string }>(
        `${config.public.apiBase}/users/api/token/refresh/`,
        {
          method: "POST",
          body: { refresh: refreshToken.value },
        }
      );

      if (!data || !data.access) throw new Error("Invalid response from server");

      accessToken.value = data.access;
    } catch (err) {
      console.error("Token refresh failed:", err);
      logout();
    } finally {
      isRefreshing.value = false;
    }
  };

  const logout = async() => {
    accessToken.value = null;
    refreshToken.value = null;
    user.value = null;
    isAdmin.value = false;
    await navigateTo('/login');
  };

  return { login, logout, fetchUser, checkAdmin, refreshAccessToken, accessToken, user, isAuthenticated, isAdmin, isRefreshing };
});
