<template>
  <SidebarProvider>
    <AppSidebar />
    <SidebarInset>
      <!-- HEADER -->
      <header
        class="flex h-12 shrink-0 items-center gap-2 transition-[width,height] ease-linear group-has-data-[collapsible=icon]/sidebar-wrapper:h-12">
        <div class="flex items-center gap-2 px-4">
          <SidebarTrigger class="-ml-1" />
          <Separator orientation="vertical" class="mr-2 data-[orientation=vertical]:h-4" />
          <!-- Slot header tùy biến -->
          <slot name="header">
            <!-- Mặc định nếu không truyền slot -->
            <Breadcrumb>
              <BreadcrumbList>
                <BreadcrumbItem class="hidden md:block">
                  <BreadcrumbLink href="/">Trang chủ</BreadcrumbLink>
                </BreadcrumbItem>
                <BreadcrumbSeparator class="hidden md:block" />
                <BreadcrumbItem>
                  <BreadcrumbPage>Trang hiện tại</BreadcrumbPage>
                </BreadcrumbItem>
              </BreadcrumbList>
            </Breadcrumb>
          </slot>
        </div>
      </header>

      <!-- CONTENT -->
      <div class="flex flex-1 h-[calc(100vh-12rem)] flex-col gap-4 p-4 pt-0">
        <slot />
      </div>
    </SidebarInset>
  </SidebarProvider>
</template>

<script setup lang="ts">
import AppSidebar from '~/components/organisms/sidebar/AppSidebar.vue'
import {
  Breadcrumb,
  BreadcrumbItem,
  BreadcrumbLink,
  BreadcrumbList,
  BreadcrumbPage,
  BreadcrumbSeparator,
} from '@/components/ui/breadcrumb'
import { Separator } from '@/components/ui/separator'
import {
  SidebarInset,
  SidebarProvider,
  SidebarTrigger,
} from '@/components/ui/sidebar'
import { computed } from 'vue'
import { useRoute } from 'vue-router'

const route = useRoute()

// const defaultOpen = computed(() => !autoCloseRoutes.includes(route.path))

</script>