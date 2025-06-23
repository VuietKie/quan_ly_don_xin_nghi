<template>
  <h1 v-if="!isEditing" class="text-2xl font-bold mb-6">Thông tin cá nhân</h1>
  <h1 v-else class="text-2xl font-bold mb-6">Chỉnh sửa thông tin cá nhân</h1>
  <div class="grid gap-8 sm:grid-cols-2 items-start">
    <div class="grid gap-6">
      <div v-if="!isEditing" class="grid gap-3">
        <Label for="username" class="text-sm font-medium">Tên đăng nhập</Label>
        <Input id="username" v-model="editableUser.username" type="username" class="h-10" disabled/>
      </div>
      <div class="grid gap-3">
        <Label for="first_name" class="text-sm font-medium">Họ</Label>
        <Input id="first_name" v-model="editableUser.first_name" type="text" class="h-10" :disabled="!isEditing"/>
      </div>
      <div class="grid gap-3">
        <Label for="last_name" class="text-sm font-medium">Tên</Label>
        <Input id="last_name" v-model="editableUser.last_name" type="text" class="h-10" :disabled="!isEditing"/>
      </div>
      <div class="grid gap-3">
        <Label for="phone_number" class="text-sm font-medium">Số điện thoại</Label>
        <Input id="phone_number" v-model="editableUser.phone_number" type="tel" class="h-10" :disabled="!isEditing"/>
      </div>
      <div class="grid gap-3">
        <Label for="address" class="text-sm font-medium">Địa chỉ</Label>
        <Input id="address" v-model="editableUser.address" type="text" class="h-10" :disabled="!isEditing"/>
      </div>
    </div>
    <div v-if="isEditing" class="grid gap-6 content-start">
      <div class="grid gap-3">
        <Label for="newPassword" class="text-sm font-medium">Mật khẩu mới</Label>
        <Input id="newPassword" v-model="passwords.newPassword" type="password" class="h-10"/>
      </div>
      <div class="grid gap-3">
        <Label for="confirmNewPassword" class="text-sm font-medium">Nhập lại mật khẩu mới</Label>
        <Input id="confirmNewPassword" v-model="passwords.confirmNewPassword" type="password" class="h-10"/>
      </div>
      <div class="text-red-500 text-sm mb-4">(*) Chỉ nhập khi muốn thay đổi mật khẩu</div>
    </div>
  </div>
  <div v-if="!isEditing" class="flex flex-col gap-2 sm:flex-row sm:justify-end mt-8">
    <Button class="w-full sm:w-60" @click="isEditing = true">
      Chỉnh sửa
    </Button>
  </div>
  <div v-else class="flex flex-col gap-2 sm:flex-row sm:justify-end mt-8">
    <Button type="submit" class="w-full sm:w-60" @click="saveChanges">
      Lưu thay đổi
    </Button>
    <Button variant="destructive" type="button" class="w-full sm:w-60" @click="cancelEdit">
      Thoát
    </Button>
  </div>
</template>

<script lang="ts" setup>
import { ref, reactive } from 'vue';
import { useToast } from 'vue-toastification';
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'

interface User {
  id?: number;
  first_name?: string;
  last_name?: string;
  username?: string;
  phone_number?: string;
  address?: string;
  is_active?: boolean;
}

const props = defineProps<{ user: User }>();
const isEditing = ref(false);
const editableUser = reactive({ ...props.user });
const passwords = reactive({ newPassword: '', confirmNewPassword: '' });
const toast = useToast();

const saveChanges = async () => {
  // Kiểm tra xem có thay đổi thông tin không
  const isUserChanged =
    editableUser.first_name !== props.user.first_name ||
    editableUser.last_name !== props.user.last_name ||
    editableUser.phone_number !== props.user.phone_number ||
    editableUser.address !== props.user.address;

  const isPasswordChanged =
    passwords.newPassword || passwords.confirmNewPassword;

  if (!isUserChanged && !isPasswordChanged) {
    toast.warning(h("div", [
      "Thông tin chưa được thay đổi hoặc nhập mới.",
      h("button", {
        onClick: () => {
          isEditing.value = false;
          toast.clear();
        },
        class: "ml-4 px-2 py-1 bg-red-500 text-white rounded"
      }, "Thoát")
    ]), {
      timeout: 10000,
      closeOnClick: false,
    });
    return;
  }

  try {
    if (passwords.newPassword && passwords.newPassword !== passwords.confirmNewPassword) {
      toast.error("Mật khẩu mới và nhập lại mật khẩu mới không khớp!");
      return;
    }

    const payload = {
      first_name: editableUser.first_name,
      last_name: editableUser.last_name,
      phone_number: editableUser.phone_number,
      address: editableUser.address,
      newPassword: passwords.newPassword || undefined
    };

    const config = useRuntimeConfig();
    const token = useCookie('access_token');

    await $fetch(`${config.public.apiBase}/users/user/profile/`, {
      headers: {
        Authorization: `Bearer ${token.value}`
      },
      method: 'PATCH',
      body: payload
    });

    toast.success("Thông tin đã được cập nhật thành công!");
    isEditing.value = false;
  } catch (error) {
    console.error("Lỗi khi cập nhật thông tin", error);
    toast.error("Có lỗi xảy ra khi cập nhật!");
  }
};

const cancelEdit = () => {
  // Khôi phục dữ liệu từ props ban đầu
  Object.assign(editableUser, { ...props.user });

  // Xóa mật khẩu nhập vào
  Object.assign(passwords, { newPassword: '', confirmNewPassword: '' });

  // Thoát chế độ chỉnh sửa
  isEditing.value = false;
};
</script>