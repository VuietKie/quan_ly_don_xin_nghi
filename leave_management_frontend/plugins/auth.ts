import { defineNuxtPlugin, useCookie } from "#app";
import { useAuthStore } from "@/stores/auth";

export default defineNuxtPlugin(async () => {
  const authStore = useAuthStore();

  // Lấy tokens từ cookies
  const accessToken = useCookie<string | null>("access_token", {
    maxAge: 60 * 60 * 24 * 7, // 7 ngày
    sameSite: 'lax',
    path: '/',
    secure: process.client && location.protocol === 'https:',
  });
  const refreshToken = useCookie<string | null>("refresh_token", {
    maxAge: 60 * 60 * 24 * 7, // 7 ngày
    sameSite: 'lax',
    path: '/',
    secure: process.client && location.protocol === 'https:',
  });

  try {
    // Đoạn này có thể thêm logic refresh token hoặc fetch user nếu cần
    // Hiện tại, các hàm này chưa được định nghĩa trong store
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
