<template>
  <SidebarMenu>
    <SidebarMenuItem>
      <SidebarMenuButton size="lg" class="data-[state=open]:bg-sidebar-accent data-[state=open]:text-sidebar-accent-foreground w-full">
        <Avatar class="h-8 w-8 rounded-lg">
          <AvatarImage src="/avatars/vietnam.png" alt="Ảnh đại diện" />
          <AvatarFallback class="rounded-lg">CN</AvatarFallback>
        </Avatar>
        <div class="flex-1 grid text-left text-sm leading-tight">
          <span class="truncate font-medium">{{ displayName }}</span>
          <span class="truncate text-xs text-muted-foreground">{{ roleLabel }}</span>
        </div>
        <button
          @click.stop="logout"
          class="ml-auto flex items-center justify-center p-1 rounded-full transition-colors hover:bg-red-100 group"
          style="height: 32px; width: 32px;"
        >
          <LogOut class="text-red-500 group-hover:text-red-700 transition-colors" />
        </button>
      </SidebarMenuButton>
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
    fullName?: string;
    role: string;
  }
}>()

const { isMobile } = useSidebar()

const emit = defineEmits(['logout']);

// Logout Function
const logout = () => {
  console.log('Logout clicked from NavUser');
  // Emit logout event to parent component
  emit('logout');
};

// 👇 computed hiển thị roleID
const roleLabel = computed(() => {
  return props.user.role ? `Role: ${props.user.role}` : '';
});

const displayName = computed(() => {
  return props.user.fullName && props.user.fullName.trim() !== '' ? props.user.fullName : 'Tài khoản';
});

// expose to template
defineExpose({ roleLabel, displayName });
</script>
