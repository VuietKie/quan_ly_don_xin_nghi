<template>
  <MainTemplate>
    <template #header>
      <Breadcrumb>
        <BreadcrumbList>
          <BreadcrumbItem class="hidden md:block">
            <BreadcrumbLink href="/">Trang chủ</BreadcrumbLink>
          </BreadcrumbItem>
          <BreadcrumbSeparator class="hidden md:block" />
          <BreadcrumbItem>
            <BreadcrumbPage>Trang liên hệ</BreadcrumbPage>
          </BreadcrumbItem>
        </BreadcrumbList>
      </Breadcrumb>
    </template>
    <!-- Nội dung chính của trang -->
    <div class="flex flex-1 flex-col gap-4 p-4 pt-0">
      <!-- Nội dung của bạn ở đây -->
      <div class="flex w-full">
        <div class="w-1/4 border-r overflow-y-auto">
          <div class="p-4">
            <input v-model="searchQuery" type="text" placeholder="Tìm kiếm email hoặc nội dung..."
              class="w-full p-2 border rounded" />
          </div>
          <ul class="p-4 space-y-4">
            <div v-for="email in filteredEmails" :key="email.id" @click="selectEmail(email.id)"
              :class="['cursor-pointer', 'rounded-xl', 'shadow', 'p-4', 'transition', 'hover:shadow-md', 'border', email.id === selectedEmail?.id ? 'bg-gray-100' : 'bg-white']">
              <div class="flex justify-between items-start">
                <div>
                  <p class="text-lg font-semibold">{{ email.subject }}</p>
                  <p class="text-sm text-gray-600">{{ email.email }}</p>
                </div>
                <div class="text-right">
                  <p class="text-xs text-gray-500">{{ formatDate(email.created_at) }}</p>
                  <p class="text-xs font-semibold" :class="email.is_replied ? 'text-green-600' : 'text-red-600'">
                    {{ email.is_replied ? 'Đã trả lời' : 'Chưa trả lời' }}
                  </p>
                </div>
              </div>
            </div>
          </ul>

        </div>

        <div class="w-3/4 p-4">
          <div v-if="selectedEmail" class="mt-4">
            <h2 class="text-xl font-bold">{{ selectedEmail.subject }}</h2>
            <div class="flex justify-between items-center">
              <p class="text-gray-600">Từ: {{ selectedEmail.email }}</p>
              <p class="text-gray-500 text-sm">Gửi ngày: {{ formatDate(selectedEmail.created_at) }}</p>
            </div>
            <p v-if="selectedEmail.is_replied" class="text-green-600 font-semibold">Trạng thái: Đã trả lời</p>
            <p v-else class="text-red-600 font-semibold">Trạng thái: Chưa trả lời</p>
            <div class="border p-4 mt-2 h-64 overflow-y-auto bg-gray-50 rounded mb-10">
              {{ selectedEmail.content }}
            </div>

            <h3 class="text-lg font-bold">Reply:</h3>

            <ClientOnly>
              <Editor v-model="editorData" />
            </ClientOnly>

            <div class="mt-2 text-right">
              <button @click="sendReply" class="p-2 bg-blue-500 text-white rounded">
                <span>Send</span>
              </button>
            </div>

          </div>

          <p v-else>Chọn tin nhắn để xem nội dung</p>
        </div>
      </div>
    </div>
  </MainTemplate>
</template>
<script lang="ts" setup>
import MainTemplate from '~/components/layouts/MainTemplate.vue'
import Breadcrumb from '@/components/ui/breadcrumb/Breadcrumb.vue'
import BreadcrumbList from '@/components/ui/breadcrumb/BreadcrumbList.vue'
import BreadcrumbItem from '@/components/ui/breadcrumb/BreadcrumbItem.vue'
import BreadcrumbLink from '@/components/ui/breadcrumb/BreadcrumbLink.vue'
import BreadcrumbSeparator from '@/components/ui/breadcrumb/BreadcrumbSeparator.vue'
import BreadcrumbPage from '@/components/ui/breadcrumb/BreadcrumbPage.vue'
import { defineAsyncComponent } from 'vue';
const Editor = defineAsyncComponent(() =>
  import("~/components/molecules/editor.client.vue")
);


interface Email {
  id: number;
  name: string;
  email: string;
  subject: string;
  content: string;
  created_at: string;
  is_replied: boolean;
}
const editorData = ref<string>('');
const emails = ref<Email[]>([]);
const searchQuery = ref<string>('');
const selectedEmail = ref<Email | null>(null);
const config = useRuntimeConfig();
const token = useCookie("access_token");

const filteredEmails = computed(() => {
  return emails.value.filter(email =>
    email.email.toLowerCase().includes(searchQuery.value.toLowerCase()) ||
    email.subject.toLowerCase().includes(searchQuery.value.toLowerCase()) ||
    email.content.toLowerCase().includes(searchQuery.value.toLowerCase())
  );
});

const fetchContactList = async () => {
  try {
    const response = await fetch(`${config.public.apiBase}/contact/contacts`, {
      method: 'GET',
      headers: {
        Authorization: `Bearer ${token.value}`,
        'Content-Type': 'application/json',
      },
    });
    if (!response.ok) throw new Error('Failed to fetch emails');
    emails.value = await response.json();
  } catch (error) {
    console.error('Error fetching emails:', error);
  }
};

const selectEmail = async (id: number) => {
  try {
    const response = await fetch(`${config.public.apiBase}/contact/contacts/${id}/`, {
      method: 'GET',
      headers: {
        Authorization: `Bearer ${token.value}`,
        'Content-Type': 'application/json',
      },
    });
    if (!response.ok) throw new Error('Failed to fetch email details');
    selectedEmail.value = await response.json();
  } catch (error) {
    console.error('Error fetching email details:', error);
  }
};

const sendReply = async () => {
  if (!editorData.value.trim() || !selectedEmail.value) {
    alert('Reply content cannot be empty');
    return;
  }
  try {
    const response = await fetch(`${config.public.apiBase}/contact/reply/`, {
      method: 'POST',
      headers: {
        Authorization: `Bearer ${token.value}`,
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({
        contact_id: selectedEmail.value.id,
        subject: `Re: ${selectedEmail.value.subject}`,
        message: editorData.value,
      }),
    });
    if (!response.ok) throw new Error('Failed to send reply');

    alert('Reply sent successfully!');

    await fetchContactList();
    await selectEmail(selectedEmail.value.id);
    editorData.value = '';
  } catch (error) {
    console.error('Error sending reply:', error);
    alert('Failed to send reply.');
  }
};

const formatDate = (dateString: string) => {
  const options: Intl.DateTimeFormatOptions = {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
    second: '2-digit',
  };
  return new Date(dateString).toLocaleDateString('vi-VN', options);
};

onMounted(() => {
  fetchContactList();
});






</script>