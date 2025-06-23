<template>
  <div class="container mx-auto p-4">
    <h1 class="text-2xl font-bold mb-4">Danh sách nhân viên</h1>
    
    <div class="bg-white shadow-md rounded-lg overflow-hidden">
      <table class="min-w-full divide-y divide-gray-200">
        <thead class="bg-gray-50">
          <tr>
            <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">ID</th>
            <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Tên đăng nhập</th>
            <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Họ và tên</th>
            <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Email</th>
            <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Phòng ban</th>
            <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Vai trò</th>
            <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Thao tác</th>
          </tr>
        </thead>
        <tbody class="bg-white divide-y divide-gray-200">
          <tr v-for="user in users" :key="user.userId" class="hover:bg-gray-50">
            <td class="px-6 py-4 whitespace-nowrap">{{ user.userId }}</td>
            <td class="px-6 py-4 whitespace-nowrap">{{ user.username }}</td>
            <td class="px-6 py-4 whitespace-nowrap">{{ user.fullName }}</td>
            <td class="px-6 py-4 whitespace-nowrap">{{ user.email }}</td>
            <td class="px-6 py-4 whitespace-nowrap">{{ user.department.departmentName }}</td>
            <td class="px-6 py-4 whitespace-nowrap">{{ user.role.roleName }}</td>
            <td class="px-6 py-4 whitespace-nowrap">
              <button @click="editUser(user)" class="text-indigo-600 hover:text-indigo-900 mr-2">Sửa</button>
              <button @click="deleteUser(user.userId)" class="text-red-600 hover:text-red-900">Xóa</button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { ref, onMounted } from 'vue'

interface Department {
  departmentId: number
  departmentName: string
  idManager: number
}

interface Role {
  roleId: number
  roleName: string
}

interface User {
  userId: number
  username: string
  password: string
  fullName: string
  department: Department
  role: Role
  email: string
}

const users = ref<User[]>([])

const fetchUsers = async () => {
  try {
    const response = await fetch('http://localhost:8080/api/list/user')
    if (!response.ok) {
      throw new Error('Network response was not ok')
    }
    users.value = await response.json()
  } catch (error) {
    console.error('Error fetching users:', error)
  }
}

const editUser = (user: User) => {
  // Implement edit functionality
  console.log('Edit user:', user)
}

const deleteUser = async (id: number) => {
  if (confirm('Bạn có chắc chắn muốn xóa người dùng này?')) {
    try {
      const response = await fetch(`http://localhost:8080/api/users/${id}`, {
        method: 'DELETE'
      })
      if (!response.ok) {
        throw new Error('Network response was not ok')
      }
      users.value = users.value.filter(user => user.userId !== id)
    } catch (error) {
      console.error('Error deleting user:', error)
    }
  }
}

onMounted(() => {
  fetchUsers()
})
</script>

<style scoped>
.container {
  max-width: 1200px;
}
</style>