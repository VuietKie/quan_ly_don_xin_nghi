<template>
    <div class="w-full lg:grid lg:min-h-screen lg:grid-cols-2 xl:min-h-screen">
      <div class="flex items-center justify-center py-12">
        <div class="mx-auto grid w-[350px] gap-6">
          <div class="grid gap-2 text-center">
            <h1 class="text-3xl font-bold">
                EATSNDRINKS
            </h1>
            <p class="text-balance text-muted-foreground">
                ADMIN
            </p>
          </div>
          <div class="grid gap-4">
            <div class="grid gap-2">
              <Label for="username">Tên đăng nhập</Label>
              <Input
                id="username"
                type="username"
                v-model=form.username
                placeholder="Tên đăng nhập"
                required
                @keyup.enter="submitForm"
              />
            </div>
            <div class="grid gap-2">
              <div class="flex items-center">
                <Label for="password">Mật khẩu</Label>
                
              </div>
              <Input id="password" type="password" v-model=form.password placeholder="Mật khẩu" required @keyup.enter="submitForm"/>
            </div>
            <Button type="submit" class="w-full"  @click="submitForm">
              Đăng nhập
            </Button>
          </div>
        </div>
      </div>
      <div class="relative hidden lg:block h-full">
        <img
            src="/images/background_login.png"
            alt="Image"
            class="h-screen w-full object-cover"
        />
        <div class="absolute inset-0 bg-black/50 z-10"></div>

        <!-- Nội dung chữ -->
        <div class="absolute inset-0 z-20 flex items-center justify-center">
            <div class="text-center text-white px-4">
                <h2 class="text-3xl font-bold mb-2">NỀN TẢNG QUẢN LÝ</h2>
                <h2 class="text-3xl font-bold mb-2">EATSNDRINKS</h2>
                <p class="text-lg text-gray-200">
                    &copy; {{ year }} <a href="#" class="hover:underline" target="_blank">EATSNDRINKS</a>
                </p>
            </div>
        </div>
        </div>
    </div>
  </template>
<script setup lang="ts">
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { useToast } from 'vue-toastification';
const year = new Date().getFullYear();
const form = ref({
    username: '',
    password: ''
});
const emit = defineEmits(['login']);
const toast = useToast();
const submitForm = () => {
    if (!form.value.username || !form.value.password) {
        toast.error("Vui lòng nhập đầy đủ tên đăng nhập và mật khẩu!");
        return;
    }
    emit('login', form.value);
};
</script>

