<template>
    <div class="grid gap-4">
        <div class="grid gap-2">
            <Label for="username">Tên đăng nhập</Label>
            <Input id="username" v-model="form.username" type="text" placeholder="Tên đăng nhập" required />
        </div>
        <div class="grid gap-2">
            <Label for="password">Mật khẩu</Label>
            <Input id="password" v-model="form.password" type="password" placeholder="Mật khẩu" required />
        </div>
        <div class="grid grid-cols-2 space-x-2">

            <div class="grid gap-2">
                <Label for="last_name">Họ</Label>
                <Input id="last_name" v-model="form.last_name" type="text" placeholder="Họ" required />
            </div>
            <div class="grid gap-2">
                <Label for="first_name">Tên</Label>
                <Input id="first_name" v-model="form.first_name" type="text" placeholder="Tên" required />
            </div>
            
        </div>
        <div class="flex flex-col gap-2 sm:flex-row sm:justify-end">
            <Button type="submit" class="w-full sm:w-60" @click="submitForm">
                Đăng ký
            </Button>
            <Button variant="destructive" type="button" class="w-full sm:w-60" @click="cancelCreate">
                Thoát
            </Button>
        </div>
    </div>
</template>
<script setup lang="ts">
import { ref, watch } from 'vue'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'
import { useToast } from 'vue-toastification'
import { useRouter } from '#app';
// Định nghĩa sự kiện mà component này sẽ phát ra
const emit = defineEmits(['create-user', 'reset-form'])

const form = ref({
    username: '',
    password: '',
    last_name: '',
    first_name: '',
})

const toast = useToast()
const router = useRouter();
const submitForm = () => {
    const { username, password, last_name, first_name } = form.value

    if (!username || !password || !last_name || !first_name) {
        toast.error('Vui lòng nhập đầy đủ thông tin!')
        return
    }

    // Emit event đăng ký
    emit('create-user', form.value)
}

const cancelCreate = () => {
    console.log("Thoát");

    toast.warning(h("div", [
        "Bạn có chắc chắn muốn thoát không? Dữ liệu chưa lưu sẽ bị mất.",
        h("button", {
            onClick: () => {
                toast.clear(); // Xóa thông báo
                router.push('/user-management'); // Điều hướng sau khi xác nhận
            },
            class: "ml-4 px-3 py-1 bg-red-500 text-white rounded hover:bg-red-700 transition"
        }, "Thoát")
    ]), {
        timeout: 10000,
        closeOnClick: false,
    });
}
</script>
