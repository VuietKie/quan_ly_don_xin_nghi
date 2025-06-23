export default defineNuxtRouteMiddleware(async (to, from) => {
  const authStore = useAuthStore();
  const { fetchUser, checkAdmin, isAuthenticated, isAdmin } = authStore;

  // Nếu chưa đăng nhập, chuyển hướng về trang đăng nhập
  if (!isAuthenticated) {
    if (to.path !== "/login") {
      return navigateTo("/login");
    }
    return; // Tránh vòng lặp nếu đã ở trang login
  }

  // Nếu chưa kiểm tra quyền admin, gọi API để kiểm tra
  if (isAdmin === null) {
    try {
      await fetchUser();
      await checkAdmin();
    } catch (error) {
      console.error("Lỗi kiểm tra admin:", error);
      if (to.path !== "/login") {
        return navigateTo("/login");
      }
      return;
    }
  }

  // Nếu không phải admin, chuyển hướng về đăng nhập
  if (!isAdmin) {
    if (to.path !== "/login") {
      return navigateTo("/login");
    }
    return; // Tránh vòng lặp nếu đã ở đăng nhập
  }
});
