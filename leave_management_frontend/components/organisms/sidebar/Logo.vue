<template>
      <SidebarMenu>
        <SidebarMenuItem>
          <SidebarMenuButton as-child
          size="lg"
          class="data-[state=open]:bg-sidebar-accent data-[state=open]:text-sidebar-accent-foreground"
          >
          <a href="/">
            <Avatar class="h-8 w-8 rounded-lg">
              
              <AvatarImage src="/images/bag.svg" alt="Logo" />
              
              <AvatarFallback class="rounded-lg">
                CN
              </AvatarFallback>
            </Avatar>
            <div class="grid flex-1 text-left text-sm leading-tight">
              <span class="truncate font-medium">LEAVE MANAGEMENT</span>
              <span class="truncate text-xs">Trang Quản Lý</span>
            </div>
          </a>
        </SidebarMenuButton>
      </SidebarMenuItem>
    </SidebarMenu>
</template>

<script setup lang="ts">
import {
    SidebarMenu,
    SidebarMenuButton,
    SidebarMenuItem,
    useSidebar,
} from '@/components/ui/sidebar';

const { isMobile } = useSidebar()
</script>
