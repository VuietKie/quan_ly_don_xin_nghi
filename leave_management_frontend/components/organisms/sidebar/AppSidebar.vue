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

const menuByFeature = [
  {
    key: 'create_leave_request',
    title: 'Create Leave Request',
    url: '/leave-request/create',
  },
  {
    key: 'show_my_leave_request',
    title: 'Show My Leave Request',
    url: '/leave-request/my-request',
  },
  {
    key: 'process_leave_request',
    title: 'Process Leave Request',
    url: '/leave-request/process',
  },
  {
    key: 'view_agenda',
    title: 'View Agenda',
    url: '/agenda',
  },
  {
    key: 'manage_permissions',
    title: 'Manage Permissions',
    url: '/permissions',
  },
];

const config = useRuntimeConfig();
const token = useCookie('access_token', { sameSite: 'lax', path: '/' });
const user = ref<any>(null);
const roleID = ref<number|null>(null);
const authStore = useAuthStore();

function parseJwt (token: string) {
  try {
    const decoded = JSON.parse(atob(token.split('.')[1]));
    console.log('🔍 Parsed JWT payload:', decoded);
    return decoded;
  } catch (e) {
    console.error('❌ Error parsing JWT:', e);
    return null;
  }
}

onMounted(() => {
  console.log('🚀 AppSidebar mounted');
  console.log('🍪 Token from cookie:', token.value);
  
  if (token.value) {
    const payload = parseJwt(token.value);
    console.log('📦 JWT payload:', payload);
    
    if (payload) {
      // Map string roles to numeric roleIDs
      const roleMapping: { [key: string]: number } = {
        'Employee': 1,
        'Manager': 2,
        'Department Manager': 3,
        'Director': 4,
        'admin': 5
      };
      
      const roleString = payload.role || payload.roleId || payload.roleID || payload.role_id;
      console.log('🎭 Role string from JWT:', roleString);
      
      roleID.value = roleMapping[roleString] || null;
      console.log('🎭 Mapped roleID:', roleID.value);
      console.log('🔍 Available payload keys:', Object.keys(payload));
      
      user.value = { fullName: payload.sub || '', roleID: roleID.value };
      console.log('👤 User object:', user.value);
    } else {
      console.warn('⚠️ Could not parse JWT payload');
    }
  } else {
    console.warn('⚠️ No token found in cookies');
  }
});

const navMain = computed(() => {
  console.log('🔄 Computing navMain, roleID:', roleID.value);
  
  if (!roleID.value) {
    console.log('❌ No roleID, returning empty array');
    return [];
  }
  
  if (roleID.value === 1) {
    console.log('✅ Role 1 - Employee, showing:', menuByFeature.slice(0, 1));
    return menuByFeature.slice(0, 2);
  } else if ([2, 3, 4].includes(roleID.value)) {
    console.log('✅ Role 2,3,4 - Manager/Lead, showing:', menuByFeature.slice(0, 4));
    return menuByFeature.slice(0, 4);
  } else if (roleID.value === 5) {
    console.log('✅ Role 5 - Admin, showing all features:', menuByFeature);
    return menuByFeature;
  }
  
  console.log('❌ Unknown roleID:', roleID.value);
  return [];
});

const logout = async () => {
  try {
    console.log('🚪 Logging out user...');
    
    // Use authStore logout for consistency
    authStore.logout();
    
    // Clear local state
    user.value = null;
    roleID.value = null;
    
    // Clear other auth-related cookies if they exist
    const refreshToken = useCookie('refresh_token');
    const userData = useCookie('user_data');
    
    if (refreshToken.value) {
      refreshToken.value = null;
    }
    if (userData.value) {
      userData.value = null;
    }
    
    console.log('✅ Logout successful, redirecting to login...');
    
    // Redirect to login page
    await navigateTo('/login', { replace: true });
    
  } catch (error) {
    console.error('❌ Error during logout:', error);
    // Even if there's an error, still try to redirect to login
    await navigateTo('/login', { replace: true });
  }
};
</script>
