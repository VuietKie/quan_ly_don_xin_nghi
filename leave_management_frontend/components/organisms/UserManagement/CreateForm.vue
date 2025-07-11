<template>
  <div class="flex justify-center items-center min-h-[60vh] bg-gray-50">
    <div class="bg-white shadow-xl rounded-2xl p-8 w-full max-w-lg">
      <div class="flex items-center mb-6 gap-2">
        <svg xmlns="http://www.w3.org/2000/svg" class="h-8 w-8 text-blue-500" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v16m8-8H4" /></svg>
        <h2 class="text-2xl font-bold text-gray-800">Thêm người dùng mới</h2>
      </div>
      <form @submit.prevent="handleSubmit" class="space-y-6">
        <div>
          <label class="block mb-1 font-semibold text-gray-700">Tên đăng nhập</label>
          <input v-model="form.username" type="text" required class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-400 focus:outline-none text-gray-700" placeholder="Tên đăng nhập" />
        </div>
        <div>
          <label class="block mb-1 font-semibold text-gray-700">Mật khẩu</label>
          <input v-model="form.password" type="password" required class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-400 focus:outline-none text-gray-700" placeholder="Mật khẩu" />
        </div>
        <div>
          <label class="block mb-1 font-semibold text-gray-700">Họ tên</label>
          <input v-model="form.fullName" type="text" required class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-400 focus:outline-none text-gray-700" placeholder="Họ tên" />
        </div>
        <div>
          <label class="block mb-1 font-semibold text-gray-700">Email</label>
          <input v-model="form.email" type="email" class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-400 focus:outline-none text-gray-700" placeholder="Email" />
        </div>
        <div>
          <label class="block mb-1 font-semibold text-gray-700">Vai trò</label>
          <select v-model="form.roleId" required class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-400 focus:outline-none text-gray-700">
            <option value="" disabled>Chọn vai trò...</option>
            <option v-for="role in roles" :key="role.roleId" :value="role.roleId">{{ role.roleName }}</option>
          </select>
        </div>
        <div class="flex justify-end gap-2 mt-6">
          <button type="button" class="px-4 py-2 rounded-lg bg-gray-200 hover:bg-gray-300 text-gray-700 font-semibold" @click="$emit('close')">Thoát</button>
          <button type="submit" class="px-4 py-2 rounded-lg bg-blue-600 hover:bg-blue-700 text-white font-semibold">Thêm</button>
        </div>
      </form>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRuntimeConfig, useCookie } from '#app'
import { useToast } from 'vue-toastification'

const emit = defineEmits(['close', 'created'])
const config = useRuntimeConfig()
const token = useCookie('access_token')
const toast = useToast()

const roles = ref<any[]>([])
const form = ref({
  username: '',
  password: '',
  fullName: '',
  email: '',
  roleId: ''
})

async function fetchRoles() {
  try {
    const res = await $fetch<any[]>(`${config.public.apiBase}/roles/all`, {
      headers: { Authorization: `Bearer ${token.value}` },
    })
    roles.value = res
  } catch (e) {
    toast.error('Không thể tải danh sách vai trò!')
  }
}

onMounted(fetchRoles)

async function handleSubmit() {
  if (!form.value.username || !form.value.password || !form.value.fullName || !form.value.roleId) {
    toast.warning('Vui lòng nhập đầy đủ thông tin!')
    return
  }
  try {
    await $fetch(`${config.public.apiBase}/auth/add`, {
      method: 'POST',
      headers: { Authorization: `Bearer ${token.value}` },
      body: {
        username: form.value.username,
        password: form.value.password,
        fullName: form.value.fullName,
        email: form.value.email,
        role: { roleId: form.value.roleId }
      },
    })
    toast.success('Thêm người dùng thành công!')
    emit('created')
    emit('close')
  } catch (e) {
    toast.error('Thêm người dùng thất bại!')
  }
}
</script>

<style scoped>
</style>