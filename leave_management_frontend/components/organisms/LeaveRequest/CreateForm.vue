<template>
  <div class="flex justify-center items-center min-h-[60vh] bg-gray-50">
    <div class="bg-white shadow-xl rounded-2xl p-8 w-full max-w-lg">
      <div class="flex items-center mb-6 gap-2">
        <svg xmlns="http://www.w3.org/2000/svg" class="h-8 w-8 text-blue-500" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z" /></svg>
        <h2 class="text-2xl font-bold text-gray-800">Tạo đơn xin nghỉ</h2>
      </div>
      <form @submit.prevent="submitForm" class="space-y-6">
        <div>
          <label class="block mb-1 font-semibold text-gray-700">Ngày bắt đầu</label>
          <input type="date" v-model="form.start_date" required class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-400 focus:outline-none text-gray-700" />
        </div>
        <div>
          <label class="block mb-1 font-semibold text-gray-700">Ngày kết thúc</label>
          <input type="date" v-model="form.end_date" required class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-400 focus:outline-none text-gray-700" />
        </div>
        <div>
          <label class="block mb-1 font-semibold text-gray-700">Lý do</label>
          <textarea v-model="form.reason" required rows="4" placeholder="Nhập lý do xin nghỉ..." class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-400 focus:outline-none text-gray-700 resize-none" />
        </div>
        <button type="submit" class="w-full py-3 bg-blue-500 hover:bg-blue-600 text-white font-bold rounded-lg shadow-md transition">Tạo đơn</button>
      </form>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, defineEmits } from 'vue'
import { useCookie } from '#app'

const emit = defineEmits(['submit'])

const form = ref({
  start_date: '',
  end_date: '',
  reason: ''
})

const token = useCookie('access_token')
const userId = ref(null)

function parseJwt(tokenStr) {
  try {
    const base64Url = tokenStr.split('.')[1]
    const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/')
    const jsonPayload = decodeURIComponent(atob(base64).split('').map(function(c) {
      return '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2)
    }).join(''))
    return JSON.parse(jsonPayload)
  } catch (e) {
    return null
  }
}

onMounted(() => {
  if (token.value) {
    const payload = parseJwt(token.value)
    if (payload && (payload.userId || payload.user_id)) {
      userId.value = payload.userId || payload.user_id
    }
  }
})

const submitForm = () => {
  if (!userId.value) {
    alert('Không xác định được user!')
    return
  }
  const payload = {
    userId: userId.value,
    startDate: form.value.start_date,
    endDate: form.value.end_date,
    reason: form.value.reason,
    status: 'Inprogress'
  }
  emit('submit', payload)
}
</script>

<style>
</style>