<template>
  <div class="flex items-center justify-center min-h-screen">
    <div class="mx-auto grid w-[350px] gap-6">
      <div class="grid gap-2 text-center">
        <h1 class="text-3xl font-bold">
          LEAVE MANAGEMENT
        </h1>
        <p class="text-balance text-muted-foreground">
          LOGIN
        </p>
      </div>
      <div class="grid gap-4">
        <div class="grid gap-2">
          <Label for="username">Tên đăng nhập</Label>
          <Input id="username" type="username" v-model=form.username placeholder="Tên đăng nhập" required
            @keyup.enter="submitForm" />
        </div>
        <div class="grid gap-2">
          <div class="flex items-center">
            <Label for="password">Mật khẩu</Label>

          </div>
          <Input id="password" type="password" v-model=form.password placeholder="Mật khẩu" required
            @keyup.enter="submitForm" />
        </div>
        <Button type="submit" class="w-full" @click="submitForm">
          Đăng nhập
        </Button>
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
