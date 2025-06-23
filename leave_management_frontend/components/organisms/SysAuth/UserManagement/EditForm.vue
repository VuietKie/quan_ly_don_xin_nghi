<template>
  <div class="grid gap-4">
    <div class="grid gap-2">
      <Label for="username">Tên đăng nhập</Label>
      <Input id="username" v-model="form.username" type="text" placeholder="Tên đăng nhập" readonly />
    </div>

    <span class="text-red-500">(*) Chỉ có thể chỉnh sửa các mục bên dưới</span>

    <div class="grid gap-2">
      <Label for="first_name">Họ</Label>
      <Input id="first_name" v-model="form.first_name" type="text" placeholder="Họ" required />
    </div>

    <div class="grid gap-2">
      <Label for="last_name">Tên</Label>
      <Input id="last_name" v-model="form.last_name" type="text" placeholder="Tên" required />
    </div>

    <div class="grid gap-2">
      <Label for="phone_number">Số điện thoại</Label>
      <Input id="phone_number" v-model="form.phone_number" type="text" placeholder="Số điện thoại" />
    </div>

    <div class="grid gap-2">
      <Label for="address">Địa chỉ</Label>
      <Input id="address" v-model="form.address" type="text" placeholder="Địa chỉ" />
    </div>

    <div class="grid gap-2">
      <Label for="newPassword">Mật khẩu mới</Label>
      <Input id="newPassword" v-model="passwords.newPassword" type="password" placeholder="Mật khẩu mới" />
    </div>

    <div class="grid gap-2">
      <Label for="confirmNewPassword">Nhập lại mật khẩu mới</Label>
      <Input id="confirmNewPassword" v-model="passwords.confirmNewPassword" type="password" placeholder="Nhập lại mật khẩu mới" />
    </div>

    <div class="text-red-500 text-sm">(*) Chỉ nhập khi muốn thay đổi mật khẩu</div>

    <div class="flex items-center gap-2">
      <input id="is_active" type="checkbox" v-model="form.is_active" class="w-4 h-4" />
      <Label for="is_active">Kích hoạt tài khoản</Label>
    </div>

    <div class="flex flex-col gap-2 sm:flex-row sm:justify-end">
      <Button type="submit" class="w-full sm:w-60" @click="saveChanges">
        Lưu thay đổi
      </Button>
      <Button variant="destructive" type="button" class="w-full sm:w-60" @click="cancelChanges">
        Thoát
      </Button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, watch } from 'vue'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'
import { useToast } from 'vue-toastification'

const emit = defineEmits(["save", "cancel"])

const props = defineProps({
  user: {
    type: Object,
    required: true,
    default: () => ({
      username: '',
      first_name: '',
      last_name: '',
      phone_number: '',
      address: '',
      is_active: true,
    }),
  },
})

const toast = useToast()

// Dùng reactive để có thể chỉnh sửa dữ liệu props
const form = reactive({ ...props.user })

// Thêm state cho mật khẩu
const passwords = reactive({
  newPassword: '',
  confirmNewPassword: ''
})

// Nếu props.user thay đổi từ bên ngoài → cập nhật lại form
watch(() => props.user, (newUser) => {
  Object.assign(form, newUser)
})

const saveChanges = () => {
  // Kiểm tra mật khẩu nếu được nhập
  if (passwords.newPassword || passwords.confirmNewPassword) {
    if (passwords.newPassword !== passwords.confirmNewPassword) {
      toast.error("Mật khẩu mới và nhập lại mật khẩu mới không khớp!")
      return
    }
  }

  // Gửi form và mật khẩu mới (nếu có) lên component cha
  emit("save", { 
    ...form,
    newPassword: passwords.newPassword || undefined 
  })
}

const cancelChanges = () => {
  // Reset mật khẩu khi hủy
  passwords.newPassword = ''
  passwords.confirmNewPassword = ''
  emit("cancel")
}
</script>
