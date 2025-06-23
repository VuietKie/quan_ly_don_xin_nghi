<template>
  <SidebarMenu>
    <SidebarMenuItem>
      <DropdownMenu>
        <DropdownMenuTrigger as-child>
          <SidebarMenuButton
            size="lg"
            class="data-[state=open]:bg-sidebar-accent data-[state=open]:text-sidebar-accent-foreground"
          >
            <Avatar class="h-8 w-8 rounded-lg">
              <AvatarImage src="/avatars/vietnam.png" alt="Ảnh đại diện" />
              <AvatarFallback class="rounded-lg">CN</AvatarFallback>
            </Avatar>
            <div class="grid flex-1 text-left text-sm leading-tight">
              <span class="truncate font-medium">{{ fullName }}</span>
              <span class="truncate text-xs text-muted-foreground">Admin</span>
            </div>
            <ChevronsUpDown class="ml-auto size-4" />
          </SidebarMenuButton>
        </DropdownMenuTrigger>

        <DropdownMenuContent
          class="w-[--reka-dropdown-menu-trigger-width] min-w-56 rounded-lg"
          :side="isMobile ? 'bottom' : 'right'"
          align="end"
          :side-offset="4"
        >
          <DropdownMenuLabel class="p-0 font-normal">
            <div class="flex items-center gap-2 px-1 py-1.5 text-left text-sm">
              <Avatar class="h-8 w-8 rounded-lg">
                <AvatarImage src="/avatars/vietnam.png" alt="Ảnh đại diện" />
                <AvatarFallback class="rounded-lg">CN</AvatarFallback>
              </Avatar>
              <div class="grid flex-1 text-left text-sm leading-tight">
                <span class="truncate font-semibold">{{ fullName }}</span>
                <span class="truncate text-xs text-muted-foreground">Admin</span>
              </div>
            </div>
          </DropdownMenuLabel>

          <DropdownMenuSeparator />

          <DropdownMenuGroup>
            <NuxtLink to="/profile">
              <DropdownMenuItem>
                <UserRound />
                Tài khoản
              </DropdownMenuItem>
            </NuxtLink>
          </DropdownMenuGroup>

          <DropdownMenuSeparator />

          <DropdownMenuItem @click="logout">
            <LogOut />
            Đăng xuất
          </DropdownMenuItem>
        </DropdownMenuContent>
      </DropdownMenu>
    </SidebarMenuItem>
  </SidebarMenu>
</template>

<script setup lang="ts">
import {
  Avatar,
  AvatarFallback,
  AvatarImage,
} from '@/components/ui/avatar'

import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuGroup,
  DropdownMenuItem,
  DropdownMenuLabel,
  DropdownMenuSeparator,
  DropdownMenuTrigger,
} from '@/components/ui/dropdown-menu'
import {
  SidebarMenu,
  SidebarMenuButton,
  SidebarMenuItem,
  useSidebar,
} from '@/components/ui/sidebar'
import {
  LogOut,
  UserRound,
  ChevronsUpDown,
} from 'lucide-vue-next'

import { computed } from 'vue';
import { useAuthStore } from '@/stores/auth';
const authStore = useAuthStore();
const props = defineProps<{
  user: {
    id: number;
    username: string;
    first_name: string;
    last_name: string;
    email: string;
  }
}>()

const { isMobile } = useSidebar()

// Logout Function
const logout = () => {
  console.log('Logout clicked');
  authStore.logout();
};

// 👇 computed fullName từ first_name + last_name
const fullName = computed(() => {
  return `${props.user.first_name} ${props.user.last_name}`.trim();
});
</script>
