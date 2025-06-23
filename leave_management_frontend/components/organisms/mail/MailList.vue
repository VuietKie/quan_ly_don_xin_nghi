<template>
  <div class="h-full overflow-y-auto px-1">
    <div v-for="mail in items" :key="mail.id" 
      @click="onSelect(mail.id)"
      :class="[
        'flex flex-col items-start gap-2 rounded-lg border p-3 text-left text-sm transition-all hover:bg-accent mb-2',
        selectedMail === mail.id && 'bg-muted'
      ]">
      <div class="flex w-full flex-col gap-1">
        <div class="flex items-center">
          <div class="flex items-center gap-2 max-w-[70%]">
            <div class="font-semibold truncate">{{ mail.name }}</div>
            <div v-if="!mail.is_replied"
              class="flex-shrink-0 h-2 w-2 rounded-full bg-blue-600"
            />
          </div>
          <div class="ml-auto text-xs text-muted-foreground whitespace-nowrap">
            {{ formatDate(mail.created_at) }}
          </div>
        </div>
        <div class="text-xs font-medium truncate">{{ mail.subject }}</div>
        <div class="text-xs text-muted-foreground line-clamp-2">
          {{ mail.content }}
        </div>
      </div>
    </div>
  </div>
</template>

<script lang="ts" setup>
interface Mail {
  id: number
  name: string
  email: string
  subject: string
  content: string
  created_at: string
  is_replied: boolean
}

const props = defineProps<{
  items: Mail[]
  selectedMail: number | undefined
}>()

const emit = defineEmits<{
  (e: 'update:selectedMail', id: number): void
  (e: 'select'): void
}>()

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

const onSelect = (id: number) => {
  emit('update:selectedMail', id)
  emit('select')
}

const truncateText = (text: string, maxLength = 150) => {
  if (text.length <= maxLength) return text
  return text.substring(0, maxLength) + '...'
}
</script>

<style>

</style>