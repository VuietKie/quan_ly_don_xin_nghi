<template>
  <Sidebar v-bind="props">
    <SidebarHeader>
      <Logo />
    </SidebarHeader>
    <SidebarContent>
      <NavMain :items="navMain" />
    </SidebarContent>
    <SidebarFooter>
      <NavUser v-if="user" :user="user" @logout="logout" />
    </SidebarFooter>
    <SidebarRail />
  </Sidebar>
</template>
<script setup lang="ts">
import { ref, computed, onMounted } from 'vue';
import { useCookie, useRuntimeConfig } from '#app';
import type { SidebarProps } from '@/components/ui/sidebar'
import {
    Sidebar,
    SidebarContent,
    SidebarFooter,
    SidebarHeader,
    SidebarRail,
} from '@/components/ui/sidebar'
import Logo from '~/components/organisms/sidebar/Logo.vue'
import NavMain from '~/components/organisms/sidebar/NavMain.vue'
import NavUser from '~/components/organisms/sidebar/NavUser.vue'
import { useAuthStore } from '@/stores/auth';

const props = withDefaults(defineProps<SidebarProps>(), {
  collapsible: 'icon',
})

// Mapping featureId -> menu item
const featureMenuMap: Record<number, { key: string; title: string; url: string }> = {
  1: { key: 'create_leave_request', title: 'Tạo đơn xin nghỉ', url: '/leave-request/create' },
  2: { key: 'show_my_leave_request', title: 'Xem đơn của tôi', url: '/leave-request/my-request' },
  3: { key: 'process_leave_request', title: 'Xử lý đơn', url: '/leave-request/process' },
  4: { key: 'view_agenda', title: 'Lịch công tác', url: '/agenda' },
  5: { key: 'manage_permissions', title: 'Phân quyền', url: '/permissions' },
  6: { key: 'user_management', title: 'Quản lý người dùng', url: '/user-management' },
  // Thêm các feature khác nếu có
};

const config = useRuntimeConfig();
const token = useCookie('access_token', { sameSite: 'lax', path: '/' });
const user = ref<any>(null);
const roleID = ref<number|null>(null);
const authStore = useAuthStore();
const menuFeatures = ref<Array<{ key: string; title: string; url: string; featureId: number; featureName: string }>>([]); // Danh sách menu động

function parseJwt (token: string) {
  try {
    const decoded = JSON.parse(atob(token.split('.')[1]));
    return decoded;
  } catch (e) {
    return null;
  }
}

async function fetchMenuFeatures(roleId: number) {
  try {
    const res = await $fetch<any[]>(`${config.public.apiBase}/role-features/by-role/${roleId}`, {
      headers: token.value ? { Authorization: `Bearer ${token.value}` } : {},
    });
    // Lọc active và map sang menu
    menuFeatures.value = (Array.isArray(res) ? res : [])
      .filter((item) => item.active && item.feature && featureMenuMap[(item.feature.featureId as number)])
      .map((item) => ({
        ...featureMenuMap[item.feature.featureId as number],
        featureId: item.feature.featureId,
        featureName: item.feature.featureName,
      }));
  } catch (e) {
    menuFeatures.value = [];
  }
}

onMounted(async () => {
  if (token.value) {
    const payload = parseJwt(token.value);
    if (payload) {
      // Map string roles to numeric roleIDs
      const roleMapping: { [key: string]: number } = {
        'Employee': 1,
        'Manager': 2,
        'Department Manager': 3,
        'admin': 4,
      };
      const roleString = payload.role || payload.roleId || payload.roleID || payload.role_id;
      roleID.value = roleMapping[roleString] || null;
      user.value = { fullName: payload.sub || '', roleID: roleID.value };
      if (roleID.value) {
        await fetchMenuFeatures(roleID.value);
      }
    }
  }
});

const navMain = computed(() => {
  // Trả về menu động theo thứ tự featureId
  return menuFeatures.value.sort((a, b) => a.featureId - b.featureId);
});

const logout = async () => {
  try {
    authStore.logout();
    user.value = null;
    roleID.value = null;
    const refreshToken = useCookie('refresh_token');
    const userData = useCookie('user_data');
    if (refreshToken.value) refreshToken.value = null;
    if (userData.value) userData.value = null;
    await navigateTo('/login', { replace: true });
  } catch (error) {
    await navigateTo('/login', { replace: true });
  }
};
</script>
