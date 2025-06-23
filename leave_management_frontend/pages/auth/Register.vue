<template>
    <RegisterForm @register="register" />
</template>

<script setup lang="ts">
import { ref } from 'vue';
import RegisterForm from '~/components/organisms/auth/RegisterForm.vue';
import { useToast } from "vue-toastification";

const toast = useToast(); // ✅ Sử dụng Vue Toastification
const errorMessage = ref<string | null>(null);
const loading = ref(false);
const config = useRuntimeConfig();
// API call to register user
const register = async (formData: { username: string; password: string; fullname: string; email: string }) => {
    errorMessage.value = null;
    loading.value = true;


    try {
        const data = await $fetch(`${config.public.apiBase}/auth/register`, {
            method: 'POST',
            body: formData
        });

        toast.success("Đăng ký thành công!"); // Hiển thị thông báo thành công
        return navigateTo('/login');
    } catch (error: any) {
        if (Array.isArray(error?.data?.message)) {
            errorMessage.value = error.data.message.join(", "); // Join array into a string
        } else {
            errorMessage.value = error?.data?.message || "Đăng ký thất bại!";
        }

        console.error('Lỗi khi đăng ký:', error);
        toast.error('Lỗi khi đăng ký'); // Show the error in toast
    } finally {
        loading.value = false;
    }
};

</script>