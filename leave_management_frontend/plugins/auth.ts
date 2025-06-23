import { defineNuxtPlugin, useCookie } from "#app";
import { useAuthStore } from "@/stores/auth";

export default defineNuxtPlugin(async () => {
  const authStore = useAuthStore();

  // Lấy tokens từ cookies
  const accessToken = useCookie<string | null>("access_token");
  const refreshToken = useCookie<string | null>("refresh_token");

  try {
    if (refreshToken.value) {
      // ✅ Tự động refresh token
      await authStore.refreshAccessToken();
    }

    if (accessToken.value) {
      // ✅ Lấy thông tin user nếu có access token
      await authStore.fetchUser();
    }
  } catch (error) {
    console.error("Lỗi khi khởi tạo xác thực:", error);
    authStore.logout();
  }

  // Cung cấp authStore cho toàn bộ app
  return {
    provide: {
      auth: authStore,
    },
  };
});
