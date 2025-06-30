<template>
  <div class="grid gap-4">
    <div class="grid gap-2">
      <Label for="user_id">Mã người dùng</Label>
      <Input id="user_id" v-model="form.user_id" type="number" placeholder="Nhập mã người dùng" required />
    </div>
    <div class="grid gap-2">
      <Label for="start_date">Ngày bắt đầu</Label>
      <Input id="start_date" v-model="form.start_date" type="date" required />
    </div>
    <div class="grid gap-2">
      <Label for="end_date">Ngày kết thúc</Label>
      <Input id="end_date" v-model="form.end_date" type="date" required />
    </div>
    <div class="grid gap-2">
      <Label for="reason">Lý do</Label>
      <Input id="reason" v-model="form.reason" type="text" placeholder="Nhập lý do" required />
    </div>
    <div class="grid gap-2">
      <Label for="status">Trạng thái</Label>
      <select id="status" v-model="form.status" class="input">
        <option value="pending">Chờ duyệt</option>
        <option value="approved">Đã duyệt</option>
        <option value="rejected">Từ chối</option>
      </select>
    </div>
    <div class="grid gap-2">
      <Label for="processed_by">Người xử lý</Label>
      <Input id="processed_by" v-model="form.processed_by" type="number" placeholder="ID người xử lý (nếu có)" />
    </div>
    <div class="grid gap-2">
      <Label for="processed_reason">Lý do xử lý</Label>
      <Input id="processed_reason" v-model="form.processed_reason" type="text" placeholder="Lý do xử lý (nếu có)" />
    </div>
    <div class="flex flex-col gap-2 sm:flex-row sm:justify-end">
      <Button type="submit" class="w-full sm:w-60" @click="submitForm">
        Tạo đơn xin nghỉ
      </Button>
      <Button variant="destructive" type="button" class="w-full sm:w-60" @click="cancelCreate">
        Thoát
      </Button>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()

const form = ref({
  user_id: '',
  start_date: '',
  end_date: '',
  reason: '',
  status: 'pending',
  processed_by: '',
  processed_reason: ''
})

function submitForm() {
  // TODO: Gửi dữ liệu form tới API tạo đơn xin nghỉ
  // Ví dụ: await $fetch('/api/leave-request', { method: 'POST', body: form.value })
  alert('Đã gửi đơn xin nghỉ!')
  // Sau khi gửi thành công, có thể chuyển hướng hoặc reset form
  // router.push('/LeaveRequest/List')
}

function cancelCreate() {
  // Quay lại trang trước hoặc reset form
  router.back()
}
</script>

<style>
</style>