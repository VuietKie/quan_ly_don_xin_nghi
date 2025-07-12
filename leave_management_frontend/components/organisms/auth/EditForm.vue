<template>
  <div class="max-w-2xl mx-auto p-6">
    <!-- Header -->
    <div class="mb-8">
      <h1 class="text-3xl font-bold text-gray-900 mb-2">Chỉnh sửa thông tin cá nhân</h1>
      <p class="text-gray-600">Cập nhật thông tin tài khoản của bạn</p>
    </div>

    <!-- Loading State -->
    <div v-if="loading" class="flex justify-center items-center py-12">
      <div class="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600"></div>
      <span class="ml-3 text-gray-600">Đang tải thông tin...</span>
    </div>

    <!-- Edit Form -->
    <div v-else-if="user" class="bg-white rounded-lg shadow-lg p-6">
      <form @submit.prevent="handleSubmit" class="space-y-6">
        <!-- Personal Information -->
        <div class="space-y-4">
          <h3 class="text-lg font-semibold text-gray-900 border-b pb-2">Thông tin cá nhân</h3>
          
          <!-- Full Name -->
          <div class="grid grid-cols-1 gap-2">
            <Label for="fullName" class="text-sm font-medium text-gray-700">Họ và tên</Label>
            <Input 
              id="fullName" 
              v-model="form.fullName" 
              type="text" 
              placeholder="Nhập họ và tên"
              :class="{ 'border-red-500': errors.fullName }"
            />
            <p v-if="errors.fullName" class="text-sm text-red-600">{{ errors.fullName }}</p>
          </div>

          <!-- Email -->
          <div class="grid grid-cols-1 gap-2">
            <Label for="email" class="text-sm font-medium text-gray-700">Email</Label>
            <Input 
              id="email" 
              v-model="form.email" 
              type="email" 
              placeholder="Nhập email"
              :class="{ 'border-red-500': errors.email }"
            />
            <p v-if="errors.email" class="text-sm text-red-600">{{ errors.email }}</p>
          </div>

          <!-- Password -->
          <div class="grid grid-cols-1 gap-2">
            <Label for="password" class="text-sm font-medium text-gray-700">
              Mật khẩu mới 
              <span class="text-gray-500 text-xs">(để trống nếu không đổi)</span>
            </Label>
            <Input 
              id="password" 
              v-model="form.password" 
              type="password" 
              placeholder="Nhập mật khẩu mới"
              :class="{ 'border-red-500': errors.password }"
            />
            <p v-if="errors.password" class="text-sm text-red-600">{{ errors.password }}</p>
          </div>

          <!-- Confirm Password -->
          <div class="grid grid-cols-1 gap-2">
            <Label for="confirmPassword" class="text-sm font-medium text-gray-700">
              Xác nhận mật khẩu mới
              <span class="text-gray-500 text-xs">(nếu có đổi mật khẩu)</span>
            </Label>
            <Input 
              id="confirmPassword" 
              v-model="form.confirmPassword" 
              type="password" 
              placeholder="Nhập lại mật khẩu mới"
              :class="{ 'border-red-500': errors.confirmPassword }"
            />
            <p v-if="errors.confirmPassword" class="text-sm text-red-600">{{ errors.confirmPassword }}</p>
          </div>
        </div>
        <!-- Form Actions -->
        <div class="flex justify-end space-x-4 pt-6 border-t">
          <Button 
            type="button" 
            variant="outline" 
            @click="handleCancel"
            :disabled="submitting"
          >
            Hủy
          </Button>
          <Button 
            type="submit" 
            :disabled="submitting"
            class="min-w-[120px]"
          >
            <div v-if="submitting" class="flex items-center space-x-2">
              <div class="animate-spin rounded-full h-4 w-4 border-b-2 border-white"></div>
              <span>Đang lưu...</span>
            </div>
            <span v-else>Lưu thay đổi</span>
          </Button>
        </div>
      </form>
    </div>

    <!-- Error State -->
    <div v-else-if="error" class="bg-red-50 border border-red-200 rounded-lg p-6 text-center">
      <svg class="w-12 h-12 text-red-400 mx-auto mb-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-2.5L13.732 4c-.77-.833-1.964-.833-2.732 0L3.732 16.5c-.77.833.192 2.5 1.732 2.5z"></path>
      </svg>
      <h3 class="text-lg font-semibold text-red-800 mb-2">Không thể tải thông tin</h3>
      <p class="text-red-600 mb-4">{{ error }}</p>
      <Button @click="fetchUserProfile" variant="outline" class="text-red-600 border-red-300 hover:bg-red-50">
        Thử lại
      </Button>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { useAuthStore } from '~/stores/auth'
import { useToast } from 'vue-toastification'

// Lấy thông tin user từ auth store
const authStore = useAuthStore()
const user = computed(() => authStore.user)
const currentUser = computed(() => user.value as any)
const toast = useToast()
const router = useRouter()

// State management
const loading = ref(false)
const submitting = ref(false)
const error = ref<string | null>(null)

// Form data
const form = ref({
  fullName: '',
  email: '',
  password: '',
  confirmPassword: ''
})

// Form errors
const errors = ref({
  fullName: '',
  email: '',
  password: '',
  confirmPassword: ''
})

// Initialize form with current user data
const initializeForm = () => {
  if (user.value) {
    form.value.fullName = user.value.fullName || ''
    form.value.email = user.value.email || ''
    form.value.password = ''
    form.value.confirmPassword = ''
  }
}

// Validation
const validateForm = () => {
  errors.value = {
    fullName: '',
    email: '',
    password: '',
    confirmPassword: ''
  }

  let isValid = true

  // Validate fullName
  if (!form.value.fullName.trim()) {
    errors.value.fullName = 'Họ và tên không được để trống'
    isValid = false
  } else if (form.value.fullName.trim().length < 2) {
    errors.value.fullName = 'Họ và tên phải có ít nhất 2 ký tự'
    isValid = false
  }

  // Validate email
  if (!form.value.email.trim()) {
    errors.value.email = 'Email không được để trống'
    isValid = false
  } else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(form.value.email)) {
    errors.value.email = 'Email không hợp lệ'
    isValid = false
  }

  // Validate password if provided
  if (form.value.password) {
    if (form.value.password.length < 6) {
      errors.value.password = 'Mật khẩu phải có ít nhất 6 ký tự'
      isValid = false
    }
    
    if (form.value.password !== form.value.confirmPassword) {
      errors.value.confirmPassword = 'Mật khẩu xác nhận không khớp'
      isValid = false
    }
  }

  return isValid
}

// Fetch user profile from API
const fetchUserProfile = async () => {
  if (user.value) {
    initializeForm()
    return
  }
  
  loading.value = true
  error.value = null
  
  try {
    const config = useRuntimeConfig()
    const token = useCookie('access_token')
    
    if (!token.value) {
      throw new Error('Không tìm thấy token đăng nhập')
    }
    
    // Lấy userID từ store hoặc decode token
    let userId: number | undefined = undefined
    
    // Thử lấy từ store trước
    if (currentUser.value && currentUser.value.userId) {
      userId = currentUser.value.userId
    }
    
    // Nếu chưa có userID, thử decode token để lấy
    if (!userId) {
      try {
        // Decode JWT token để lấy userID (giả sử token có format chuẩn)
        const tokenParts = token.value.split('.')
        if (tokenParts.length === 3) {
          const payload = JSON.parse(atob(tokenParts[1]))
          userId = payload.userId || payload.sub || payload.id
        }
      } catch (decodeError) {
        console.warn('Không thể decode token:', decodeError)
      }
    }
    
    if (!userId) {
      throw new Error('Không tìm thấy userID')
    }
    
    const data = await $fetch<any>(`${config.public.apiBase}/auth/${userId}`, {
      headers: {
        Authorization: `Bearer ${token.value}`
      }
    })
    
    if (data) {
      console.log('Raw API response:', data)
      
      // Transform data to match User interface structure
      const transformedData = {
        userId: data.userId,
        username: data.username,
        password: '', // Not provided by API
        fullName: data.fullName,
        email: data.email,
        department: {
          departmentId: data.departmentId,
          departmentName: data.departmentName,
          idManager: data.managerId || 0
        },
        role: {
          roleId: data.roleId,
          roleName: data.roleName
        }
      }
      
      console.log('Transformed data:', transformedData)
      authStore.setUser(transformedData as any)
      initializeForm()
    }
  } catch (err: any) {
    console.error('Lỗi khi lấy thông tin user:', err)
    error.value = err.message || 'Không thể tải thông tin người dùng'
  } finally {
    loading.value = false
  }
}

// Handle form submission
const handleSubmit = async () => {
  if (!validateForm()) {
    return
  }

  submitting.value = true
  
  try {
    const config = useRuntimeConfig()
    const token = useCookie('access_token')
    
    if (!token.value || !user.value) {
      throw new Error('Không tìm thấy token đăng nhập hoặc thông tin người dùng')
    }

    // Prepare update data
    const updateData: any = {
      fullName: form.value.fullName.trim(),
      email: form.value.email.trim()
    }

    // Only include password if provided
    if (form.value.password) {
      updateData.password = form.value.password
    }

    console.log('Sending update data:', updateData)

    // Call API to update user
    const response = await $fetch(`${config.public.apiBase}/auth/update/${user.value.userId}`, {
      method: 'PATCH',
      headers: {
        'Authorization': `Bearer ${token.value}`,
        'Content-Type': 'application/json'
      },
      body: updateData
    })

    console.log('Update response:', response)

    // Update local user data
    if (response) {
      const updatedUser = {
        ...user.value,
        fullName: updateData.fullName,
        email: updateData.email
      }
      authStore.setUser(updatedUser as any)
    }

    toast.success('Cập nhật thông tin thành công!')
    
    // Navigate back to profile
    router.push('/')
    
  } catch (err: any) {
    console.error('Lỗi khi cập nhật thông tin:', err)
    toast.error('Cập nhật thông tin thất bại: ' + (err.message || 'Lỗi không xác định'))
  } finally {
    submitting.value = false
  }
}

// Handle cancel
const handleCancel = () => {
  router.push('/')
}

// Auto fetch user profile on mount if not available
onMounted(async () => {
  if (!user.value) {
    await fetchUserProfile()
  } else {
    initializeForm()
  }
})
</script>

<style scoped>
/* Custom styles if needed */
</style> 