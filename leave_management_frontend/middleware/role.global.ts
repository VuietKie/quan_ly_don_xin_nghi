import { defineNuxtRouteMiddleware, useCookie, useRuntimeConfig } from 'nuxt/app'

// Map featureId sang url (cần đồng bộ với backend và sidebar)
const featureUrlMap: Record<number, string> = {
  1: '/leave-request/create',
  2: '/leave-request/my-request',
  3: '/leave-request/process',
  4: '/agenda',
  5: '/permissions',
  6: '/user-management',
  // Thêm các featureId khác nếu có
}

const PUBLIC_PATHS = ['/login', '/unauthorized', '/', '/auth/edit'] // Thêm các trang public

function parseJwt(token: string): any {
  try {
    return JSON.parse(atob(token.split('.')[1]));
  } catch (e) {
    return null;
  }
}

export default defineNuxtRouteMiddleware(async (to) => {
  if (process.client) {
    // Nếu là trang public thì bỏ qua kiểm tra quyền
    if (PUBLIC_PATHS.includes(to.path)) return

    // Lấy token từ cookie, roleId từ localStorage
    const token = useCookie('access_token').value as string | null
    const config = useRuntimeConfig()
    let roleId: string | null = null
    if (token) {
      const payload = parseJwt(token)
      console.log('JWT payload:', payload)
      roleId = payload?.roleId?.toString() || null
    }

    if (!token || !roleId) {
      return navigateTo('/login')
    }

    try {
      // Gọi API lấy danh sách feature cho role hiện tại
      const res = await $fetch<any[]>(`${config.public.apiBase}/role-features/by-role/${roleId}`, {
        headers: { Authorization: `Bearer ${token}` }
      })
      // Lấy danh sách url được phép
      const allowedUrls = (res || [])
        .filter(item => item.active && item.feature && featureUrlMap[item.feature.featureId])
        .map(item => featureUrlMap[item.feature.featureId])
      console.log('allowedUrls:', allowedUrls, 'to.path:', to.path)
      // Nếu route hiện tại không nằm trong allowedUrls thì chuyển hướng
      if (!allowedUrls.includes(to.path)) {
        return navigateTo('/unauthorized')
      }
    } catch (e) {
      // Nếu lỗi API thì chặn luôn
      return navigateTo('/unauthorized')
    }
  }
}) 