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

const props = withDefaults(defineProps<SidebarProps>(), {
  collapsible: 'icon',
})

const menuByFeature = [
  {
    key: 'create_leave_request',
    title: 'Create Leave Request',
    url: '/leave-request/create',
  },
  {
    key: 'process_leave_request',
    title: 'Process Leave Request',
    url: '/leave-request/process',
  },
  {
    key: 'view_department_agenda',
    title: 'View Department Agenda',
    url: '/agenda/department',
  },
  {
    key: 'view_company_agenda',
    title: 'View Company Agenda',
    url: '/agenda/company',
  },
  {
    key: 'manage_permissions',
    title: 'Manage Permissions',
    url: '/permissions',
  },
];

const config = useRuntimeConfig();
const token = useCookie('access_token');
const user = ref<any>(null);
const roleID = ref<number|null>(null);

const userName = useCookie('user_name');
const roleCookie = useCookie('role');

function parseJwt (token: string) {
  try {
    return JSON.parse(atob(token.split('.')[1]));
  } catch (e) {
    return null;
  }
}

onMounted(() => {
  // Ưu tiên lấy từ cookies nếu có
  if (roleCookie.value) {
    user.value = {
      fullName: userName.value || '',
      role: roleCookie.value
    };
  } else if (token.value) {
    const payload = parseJwt(token.value);
    if (payload) {
      user.value = { fullName: payload.sub || '', role: payload.role || '' };
    }
  }
});

const navMain = computed(() => {
  if (!user.value || !user.value.role) return [];
  const roleId = Number(user.value.role);
  if (roleId === 1) {
    return [menuByFeature[0]];
  } else if ([2, 3, 4].includes(roleId)) {
    return menuByFeature.slice(0, 3);
  } else if (roleId === 5) {
    return menuByFeature;
  }
  return [];
});

const logout = () => {
  token.value = null;
  navigateTo("/login");
};
</script>
