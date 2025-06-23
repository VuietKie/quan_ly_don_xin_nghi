<template>
  <div class="relative">
    <!-- Menu toggle button for mobile -->
    <div class="h-12 px-4 flex items-center border-b md:hidden">
      <Button
        variant="ghost"
        size="icon"
        class="mr-2"
        @click="isMenuOpen = !isMenuOpen"
      >
        <Menu v-if="!isMenuOpen" class="size-4" />
        <X v-else class="size-4" />
      </Button>
      <h1 class="text-lg font-semibold">
        {{ selectedMailData ? selectedMailData.subject : 'Quản lý liên hệ' }}
      </h1>
    </div>

    <div class="grid h-[calc(100vh-168px)] md:h-[calc(100vh-120px)] grid-cols-12 divide-x">
      <!-- Sidebar -->
      <div 
        :class="[
          'col-span-12 md:col-span-4 flex flex-col min-w-[280px] bg-white',
          'fixed md:relative inset-x-0 top-[105px] md:top-0 bottom-0 z-40',
          isMenuOpen ? 'translate-x-0' : '-translate-x-full md:translate-x-0',
          'transition-transform duration-200 ease-in-out'
        ]"
      >
        <Tabs default-value="all" class="h-full flex flex-col">
          <div class="flex items-center px-4 py-2">
            <h1 class="text-xl font-bold md:block">
              Liên hệ
            </h1>
            <TabsList class="ml-auto">
              <TabsTrigger value="all" class="text-zinc-600 dark:text-zinc-200">
                Tất cả
              </TabsTrigger>
              <TabsTrigger value="unread" class="text-zinc-600 dark:text-zinc-200">
                Chưa trả lời
              </TabsTrigger>
            </TabsList>
          </div>
          <Separator />
          <div class="bg-background/95 p-4 backdrop-blur supports-[backdrop-filter]:bg-background/60">
            <form>
              <div class="relative">
                <Search class="absolute left-2 top-2.5 size-4 text-muted-foreground" />
                <Input v-model="searchValue" placeholder="Tìm kiếm..." class="pl-8" />
              </div>
            </form>
          </div>
          <div class="flex-1 overflow-hidden">
            <TabsContent value="all" class="m-0 h-full overflow-hidden">
              <MailList 
                v-model:selected-mail="selectedMail" 
                :items="filteredMailList" 
                @select="onSelectMail"
              />
            </TabsContent>
            <TabsContent value="unread" class="m-0 h-full overflow-hidden">
              <MailList 
                v-model:selected-mail="selectedMail" 
                :items="unreadMailList" 
                @select="onSelectMail"
              />
            </TabsContent>
          </div>
        </Tabs>
      </div>

      <!-- Overlay for mobile -->
      <div 
        v-if="isMenuOpen"
        class="fixed inset-0 bg-black/20 z-30 md:hidden"
        @click="isMenuOpen = false"
      />

      <!-- Main content -->
      <div class="col-span-12 md:col-span-8 h-full">
        <div class="h-full md:pt-0">
          <MailDisplay :mail="selectedMailData" @send-reply="$emit('send-reply', $event)" />
        </div>
      </div>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { refDebounced } from '@vueuse/core'
import { Search, Menu, X } from 'lucide-vue-next'
import { computed, ref } from 'vue'
import { Input } from '~/components/ui/input'
import { Button } from '~/components/ui/button'
import { Separator } from '~/components/ui/separator'
import {
  Tabs,
  TabsContent,
  TabsList,
  TabsTrigger,
} from '~/components/ui/tabs'
import MailDisplay from '~/components/organisms/mail/MailDisplay.vue'
import MailList from '~/components/organisms/mail/MailList.vue'

interface Mail {
  id: number
  name: string
  email: string
  subject: string
  content: string
  created_at: string
  is_replied: boolean
}

interface MailProps {
  mails: Mail[]
}

const props = defineProps<MailProps>()

defineEmits<{
  (e: 'send-reply', data: { contactId: number; subject: string; message: string }): void
}>()

const isMenuOpen = ref(false)
const selectedMail = ref<number | undefined>(props.mails[0]?.id)
const searchValue = ref('')
const debouncedSearch = refDebounced(searchValue, 250)

const filteredMailList = computed(() => {
  const searchValue = debouncedSearch.value?.trim().toLowerCase()
  if (!searchValue) return props.mails

  return props.mails.filter((item) => {
    return item.name.toLowerCase().includes(searchValue) ||
      item.email.toLowerCase().includes(searchValue) ||
      item.subject.toLowerCase().includes(searchValue) ||
      item.content.toLowerCase().includes(searchValue)
  })
})

const unreadMailList = computed(() => 
  filteredMailList.value.filter(item => !item.is_replied)
)

const selectedMailData = computed(() => 
  props.mails.find(item => item.id === selectedMail.value)
)

const onSelectMail = () => {
  isMenuOpen.value = false
}
</script>

