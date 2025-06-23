<template>
      <SidebarMenu>
        <SidebarMenuItem>
          <SidebarMenuButton as-child
          size="lg"
          class="data-[state=open]:bg-sidebar-accent data-[state=open]:text-sidebar-accent-foreground"
          >
          <a href="/">
            <Avatar class="h-8 w-8 rounded-lg">
              
              <AvatarImage src="/images/icon_food.png" alt="Logo" />
              
              <AvatarFallback class="rounded-lg">
                CN
              </AvatarFallback>
            </Avatar>
            <div class="grid flex-1 text-left text-sm leading-tight">
              <span class="truncate font-medium">EATSNDRINKS</span>
              <span class="truncate text-xs">Trang Admin</span>
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
