<template>


  <Sidebar v-bind="props">
    <SidebarHeader>
      <Logo />
    </SidebarHeader>
    <SidebarContent>
      <NavMain :items="data.navMain" />
    </SidebarContent>
    <SidebarFooter>
      <NavUser v-if="user" :user="user" @logout="logout" />
    </SidebarFooter>
    <SidebarRail />
  </Sidebar>


</template>
<script setup lang="ts">
import { useAuthStore } from '@/stores/auth';
import { useRouter } from 'vue-router';
import { computed } from 'vue';
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

import {
    BookOpen,
    Command,
    Settings2,
    SquareTerminal,
    ChartLine,
    Contact,
    Package
} from 'lucide-vue-next'



const props = withDefaults(defineProps<SidebarProps>(), {
  collapsible: 'icon',
})

// This is sample data.
const data = {
  user: {
    name: 'shadcn',
    email: 'm@example.com',
    avatar: '/avatars/vietnam.png',
  },
  navMain: [
    {
      title: 'Tổng quan',
      url: '#',
      icon: Command,
      items: [
        {
          title: 'Dashboard',
          url: '/',
        },
      ],
    },
    {
      title: 'Quản trị',
      url: '#',
      icon: SquareTerminal,
      items: [
        {
          title: 'Quản lý người dùng',
          url: '/user-management',
        }
      ],
    },
    {
      title: 'Quản lý sản phẩm',
      url: '#',
      icon: BookOpen,
      items: [
        {
          title: 'Danh sách sản phẩm',
          url: '/product-management',
        },
        {
          title: 'Danh mục sản phẩm',
          url: '/category-management',
        },
        {
          title: 'Danh sách combo',
          url: '/combo-management',
        },
      ],
    },
    {
      title: 'Liên hệ',
      url: '#',
      icon: Contact,
      items: [
        {
          title: 'Danh sách liên hệ',
          url: '/contact',
        },
      ],
    },
    {
      title: 'Quản lý đơn hàng',
      url: '#',
      icon: Package,
      items: [
        {
          title: 'Danh sách đơn hàng',
          url: '/order-management',
        },
      ],
    },
  ]
}

const config = useRuntimeConfig();
const token = useCookie('auth_token');
const authStore = useAuthStore();
const user = computed(() => authStore.user);

const logout = () => {
  const token = useCookie("auth_token");
  token.value = null; // Clear the token

  navigateTo("/login");
};

</script>
