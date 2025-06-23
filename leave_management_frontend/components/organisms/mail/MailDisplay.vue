<script lang="ts" setup>
import { Archive, ArchiveX, Clock, Forward, MoreVertical, Reply, ReplyAll, Trash2 } from 'lucide-vue-next'
import { computed, ref } from 'vue'
import { Button } from '~/components/ui/button'
import { DropdownMenu, DropdownMenuContent, DropdownMenuItem, DropdownMenuTrigger } from '~/components/ui/dropdown-menu'
import { Popover, PopoverContent, PopoverTrigger } from '~/components/ui/popover'
import { Separator } from '~/components/ui/separator'
import { Tooltip, TooltipContent, TooltipTrigger } from '~/components/ui/tooltip'
import { Textarea } from '~/components/ui/textarea'
import { Input } from '~/components/ui/input'

interface Mail {
  id: number
  name: string
  email: string
  subject: string
  content: string
  created_at: string
  is_replied: boolean
}

interface MailDisplayProps {
  mail: Mail | undefined
}

const props = defineProps<MailDisplayProps>()
const emit = defineEmits<{
  (e: 'send-reply', data: { contactId: number; subject: string; message: string }): void
}>()

const replySubject = ref('')
const replyMessage = ref('')

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
  if (!props.mail || !replyMessage.value.trim()) return

  emit('send-reply', {
    contactId: props.mail.id,
    subject: replySubject.value || `Re: ${props.mail.subject}`,
    message: replyMessage.value
  })

  replySubject.value = ''
  replyMessage.value = ''
}
</script>

<template>
  <div class="flex h-full flex-col">
    <div v-if="mail" class="flex flex-1 flex-col overflow-hidden">
      <div class="flex items-start p-4">
        <div class="flex items-start gap-4 text-sm w-full">
          <div class="grid gap-1 w-full">
            <div class="flex items-center justify-between flex-wrap gap-2">
              <div class="font-semibold text-base">
                {{ mail.name }}
              </div>
              <div class="text-xs text-muted-foreground whitespace-nowrap">
                {{ formatDate(mail.created_at) }}
              </div>
            </div>
            <div class="text-sm font-medium">
              {{ mail.subject }}
            </div>
            <div class="text-sm">
              <span class="font-medium">Email:</span> {{ mail.email }}
            </div>
            <div class="text-sm">
              <span class="font-medium">Trạng thái: </span>
              <span :class="mail.is_replied ? 'text-green-600' : 'text-red-600'">
                {{ mail.is_replied ? 'Đã trả lời' : 'Chưa trả lời' }}
              </span>
            </div>
          </div>
        </div>
      </div>
      <Separator />
      <div class="flex-1 overflow-auto">
        <div class="whitespace-pre-wrap p-4 text-sm">
          {{ mail.content }}
        </div>
      </div>
      <Separator class="mt-auto" />
      <div v-if="!mail.is_replied" class="p-4">
        <form @submit.prevent="handleSendReply">
          <div class="grid gap-4">
            <Input v-model="replySubject" :placeholder="`Re: ${mail.subject}`" />
            <Textarea v-model="replyMessage" class="min-h-[150px]" :placeholder="`Trả lời ${mail.name}...`" />
            <div class="flex items-center">
              <Button type="submit" size="sm" class="ml-auto" :disabled="!replyMessage.trim()">
                Gửi trả lời
              </Button>
            </div>
          </div>
        </form>
      </div>
    </div>
    <div v-else class="p-8 text-center text-muted-foreground">
      Chưa chọn tin nhắn nào
    </div>
  </div>
</template>