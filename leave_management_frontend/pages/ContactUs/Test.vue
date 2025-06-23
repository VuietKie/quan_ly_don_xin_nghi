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
            <BreadcrumbPage>Quản lý liên hệ</BreadcrumbPage>
          </BreadcrumbItem>
        </BreadcrumbList>
      </Breadcrumb>
    </template>
    <!-- Nội dung chính của trang -->
    <div class="flex flex-1 flex-col h-full">
      <div class="flex-1 h-full p-4 md:p-6">
        <div class="bg-white rounded-lg shadow-sm h-full">
          <Mail 
            :mails="emails" 
            @send-reply="sendReply"
          />
        </div>
      </div>
    </div>
  </MainTemplate>
</template>

<script lang="ts" setup>
import { ref, onMounted } from 'vue'
import { useRuntimeConfig, useCookie } from '#app'
import MainTemplate from '~/components/layouts/MainTemplate.vue'
import Breadcrumb from '@/components/ui/breadcrumb/Breadcrumb.vue'
import BreadcrumbList from '@/components/ui/breadcrumb/BreadcrumbList.vue'
import BreadcrumbItem from '@/components/ui/breadcrumb/BreadcrumbItem.vue'
import BreadcrumbLink from '@/components/ui/breadcrumb/BreadcrumbLink.vue'
import BreadcrumbSeparator from '@/components/ui/breadcrumb/BreadcrumbSeparator.vue'
import BreadcrumbPage from '@/components/ui/breadcrumb/BreadcrumbPage.vue'
import Mail from '~/components/organisms/mail/Mail.vue'

interface Email {
  id: number;
  name: string;
  email: string;
  subject: string;
  content: string;
  created_at: string;
  is_replied: boolean;
}

const emails = ref<Email[]>([]);
const config = useRuntimeConfig();
const token = useCookie("access_token");

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

const sendReply = async (replyData: { contactId: number; subject: string; message: string }) => {
  try {
    const response = await fetch(`${config.public.apiBase}/contact/reply/`, {
      method: 'POST',
      headers: {
        Authorization: `Bearer ${token.value}`,
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({
        contact_id: replyData.contactId,
        subject: replyData.subject,
        message: replyData.message,
      }),
    });
    if (!response.ok) throw new Error('Failed to send reply');
    await fetchContactList();
  } catch (error) {
    console.error('Error sending reply:', error);
    throw error;
  }
};

onMounted(() => {
  fetchContactList();
});
</script>
