<template>
  <TooltipProvider :delay-duration="0">
    <ResizablePanelGroup
      direction="horizontal"
      class="h-full items-stretch"
    >
      <ResizablePanel :min-size="30" class="min-w-[300px]">
        <Tabs default-value="all">
          <div class="flex items-center px-4 py-2">
            <h1 class="text-xl font-bold">
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
          <TabsContent value="all" class="m-0">
            <div class="overflow-auto">
              <div v-for="contact in filteredContacts" :key="contact.id" 
                @click="$emit('select-contact', contact.id)"
                :class="['cursor-pointer p-4 border-b hover:bg-gray-50', 
                  selectedContact?.id === contact.id ? 'bg-gray-100' : '']">
                <div class="flex justify-between items-start">
                  <div>
                    <p class="font-medium">{{ contact.subject }}</p>
                    <p class="text-sm text-gray-600">{{ contact.email }}</p>
                  </div>
                  <div class="text-right">
                    <p class="text-xs text-gray-500">{{ formatDate(contact.created_at) }}</p>
                    <p class="text-xs font-semibold" 
                      :class="contact.is_replied ? 'text-green-600' : 'text-red-600'">
                      {{ contact.is_replied ? 'Đã trả lời' : 'Chưa trả lời' }}
                    </p>
                  </div>
                </div>
              </div>
            </div>
          </TabsContent>
          <TabsContent value="unread" class="m-0">
            <div class="overflow-auto">
              <div v-for="contact in unreadContacts" :key="contact.id" 
                @click="$emit('select-contact', contact.id)"
                :class="['cursor-pointer p-4 border-b hover:bg-gray-50', 
                  selectedContact?.id === contact.id ? 'bg-gray-100' : '']">
                <div class="flex justify-between items-start">
                  <div>
                    <p class="font-medium">{{ contact.subject }}</p>
                    <p class="text-sm text-gray-600">{{ contact.email }}</p>
                  </div>
                  <div class="text-right">
                    <p class="text-xs text-gray-500">{{ formatDate(contact.created_at) }}</p>
                    <p class="text-xs font-semibold text-red-600">
                      Chưa trả lời
                    </p>
                  </div>
                </div>
              </div>
            </div>
          </TabsContent>
        </Tabs>
      </ResizablePanel>
      <ResizableHandle with-handle />
      <ResizablePanel>
        <div v-if="selectedContact" class="p-4">
          <div class="mb-4">
            <h2 class="text-xl font-bold">{{ selectedContact.subject }}</h2>
            <div class="flex justify-between items-center mt-2">
              <p class="text-gray-600">Từ: {{ selectedContact.email }}</p>
              <p class="text-gray-500 text-sm">{{ formatDate(selectedContact.created_at) }}</p>
            </div>
            <p :class="[
              'text-sm font-semibold mt-1',
              selectedContact.is_replied ? 'text-green-600' : 'text-red-600'
            ]">
              {{ selectedContact.is_replied ? 'Đã trả lời' : 'Chưa trả lời' }}
            </p>
          </div>
          
          <div class="border p-4 rounded-lg bg-gray-50 mb-6">
            {{ selectedContact.content }}
          </div>

          <div v-if="!selectedContact.is_replied">
            <h3 class="text-lg font-bold mb-2">Trả lời:</h3>
            <div class="mb-4">
              <Input v-model="replySubject" placeholder="Tiêu đề" class="mb-2" />
              <Textarea
                v-model="replyMessage"
                placeholder="Nội dung trả lời..."
                class="min-h-[200px]"
              />
            </div>
            <div class="text-right">
              <Button @click="handleSendReply" :disabled="!replyMessage.trim()">
                Gửi trả lời
              </Button>
            </div>
          </div>
        </div>
        <div v-else class="p-4 text-center text-gray-500">
          Chọn một liên hệ để xem chi tiết
        </div>
      </ResizablePanel>
    </ResizablePanelGroup>
  </TooltipProvider>
</template>

<script lang="ts" setup>
import { computed, ref } from 'vue'
import { Search } from 'lucide-vue-next'
import { Input } from '~/components/ui/input'
import { Button } from '~/components/ui/button'
import { Textarea } from '~/components/ui/textarea'
import { ResizableHandle, ResizablePanel, ResizablePanelGroup } from '~/components/ui/resizable'
import { Separator } from '~/components/ui/separator'
import {
  Tabs,
  TabsContent,
  TabsList,
  TabsTrigger,
} from '~/components/ui/tabs'
import { TooltipProvider } from '~/components/ui/tooltip'

interface Contact {
  id: number
  name: string
  email: string
  subject: string
  content: string
  created_at: string
  is_replied: boolean
}

const props = defineProps<{
  contacts: Contact[]
  selectedContact: Contact | null
}>()

const emit = defineEmits<{
  (e: 'select-contact', id: number): void
  (e: 'send-reply', data: { contactId: number; subject: string; message: string }): void
}>()

const searchValue = ref('')
const replySubject = ref('')
const replyMessage = ref('')

const filteredContacts = computed(() => {
  const search = searchValue.value.toLowerCase().trim()
  if (!search) return props.contacts
  
  return props.contacts.filter(contact => 
    contact.email.toLowerCase().includes(search) ||
    contact.subject.toLowerCase().includes(search) ||
    contact.content.toLowerCase().includes(search)
  )
})

const unreadContacts = computed(() => 
  filteredContacts.value.filter(contact => !contact.is_replied)
)

const formatDate = (dateString: string) => {
  const options: Intl.DateTimeFormatOptions = {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
  }
  return new Date(dateString).toLocaleDateString('vi-VN', options)
}

const handleSendReply = () => {
  if (!props.selectedContact || !replyMessage.value.trim()) return
  
  emit('send-reply', {
    contactId: props.selectedContact.id,
    subject: replySubject.value || `Re: ${props.selectedContact.subject}`,
    message: replyMessage.value
  })
  
  replySubject.value = ''
  replyMessage.value = ''
}
</script> 