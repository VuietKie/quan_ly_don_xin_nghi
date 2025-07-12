<template>
  <div :class="compact ? 'w-full' : 'max-w-4xl mx-auto p-6'">
    <!-- Header -->
    <div v-if="!compact" class="mb-8">
      <h1 class="text-3xl font-bold text-gray-900 mb-2">Thông tin cá nhân</h1>
      <p class="text-gray-600">Quản lý thông tin tài khoản của bạn</p>
    </div>

    <!-- Loading State -->
    <div v-if="loading" class="flex justify-center items-center py-12">
      <div class="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600"></div>
      <span class="ml-3 text-gray-600">Đang tải thông tin...</span>
    </div>

    <!-- Compact Profile for Dashboard -->
    <div v-else-if="user && compact" class="space-y-4">
      <!-- User Avatar and Basic Info -->
      <div class="flex items-center space-x-4">
        <div class="w-16 h-16 bg-gradient-to-r from-blue-600 to-purple-600 rounded-full flex items-center justify-center">
          <span class="text-xl font-bold text-white">
            {{ userInitials }}
          </span>
        </div>
        <div class="flex-1">
          <h3 class="text-lg font-semibold text-gray-900">{{ user?.fullName || 'Chưa có tên' }}</h3>
          <p class="text-sm text-gray-600">{{ user?.username || 'Chưa có username' }}</p>
          <div class="flex items-center space-x-2 mt-1">
            <Badge variant="secondary" class="text-xs">
              {{ user?.role?.roleName || 'Chưa có vai trò' }}
            </Badge>
            <Badge variant="secondary" class="text-xs">
              {{ user?.department?.departmentName || 'Chưa có phòng ban' }}
            </Badge>
          </div>
        </div>
      </div>

      <!-- Quick Info -->
      <div class="grid grid-cols-2 gap-4 text-sm">
        <div>
          <span class="text-gray-500">Email:</span>
          <p class="font-medium">{{ user?.email || 'Chưa có thông tin' }}</p>
        </div>
        <div>
          <span class="text-gray-500">ID:</span>
          <p class="font-medium">{{ user?.userId || 'Chưa có thông tin' }}</p>
        </div>
      </div>

      <!-- Quick Actions -->
      <div class="flex justify-center pt-2">
        <Button size="sm" variant="outline" @click="handleEditProfile">
          <svg class="w-4 h-4 mr-1" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z"></path>
          </svg>
          Chỉnh sửa thông tin
        </Button>
      </div>
    </div>

    <!-- Full Profile Card -->
    <div v-else-if="user && !compact" class="bg-white rounded-lg shadow-lg overflow-hidden">
      <!-- Profile Header -->
      <div class="bg-gradient-to-r from-blue-600 to-purple-600 px-6 py-8 text-white">
        <div class="flex items-center space-x-4">
          <div class="w-20 h-20 bg-white/20 rounded-full flex items-center justify-center">
            <span class="text-2xl font-bold text-white">
              {{ userInitials }}
            </span>
          </div>
          <div>
            <h2 class="text-2xl font-semibold">{{ user?.fullName || 'Chưa có tên' }}</h2>
            <p class="text-blue-100">{{ user?.username || 'Chưa có username' }}</p>
            <div class="flex items-center space-x-2 mt-2">
              <Badge variant="secondary" class="bg-white/20 text-white border-white/30">
                {{ user?.role?.roleName || 'Chưa có vai trò' }}
              </Badge>
              <Badge variant="secondary" class="bg-white/20 text-white border-white/30">
                {{ user?.department?.departmentName || 'Chưa có phòng ban' }}
              </Badge>
            </div>
          </div>
        </div>
      </div>

      <!-- Profile Content -->
      <div class="p-6">
        <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
          <!-- Personal Information -->
          <Card>
            <CardHeader>
              <CardTitle class="flex items-center space-x-2">
                <svg class="w-5 h-5 text-blue-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z"></path>
                </svg>
                <span>Thông tin cá nhân</span>
              </CardTitle>
            </CardHeader>
            <CardContent class="space-y-4">
              <div class="grid grid-cols-1 gap-4">
                <div>
                  <Label class="text-sm font-medium text-gray-600">Họ và tên</Label>
                  <p class="text-gray-900 font-medium">{{ user?.fullName || 'Chưa có thông tin' }}</p>
                </div>
                <div>
                  <Label class="text-sm font-medium text-gray-600">Tên đăng nhập</Label>
                  <p class="text-gray-900 font-medium">{{ user?.username || 'Chưa có thông tin' }}</p>
                </div>
                <div>
                  <Label class="text-sm font-medium text-gray-600">Email</Label>
                  <p class="text-gray-900 font-medium">{{ user?.email || 'Chưa có thông tin' }}</p>
                </div>
              </div>
            </CardContent>
          </Card>

          <!-- Work Information -->
          <Card>
            <CardHeader>
              <CardTitle class="flex items-center space-x-2">
                <svg class="w-5 h-5 text-green-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 13.255A23.931 23.931 0 0112 15c-3.183 0-6.22-.62-9-1.745M16 6V4a2 2 0 00-2-2h-4a2 2 0 00-2-2v2m8 0V6a2 2 0 012 2v6a2 2 0 01-2 2H8a2 2 0 01-2-2V8a2 2 0 012-2V6"></path>
                </svg>
                <span>Thông tin công việc</span>
              </CardTitle>
            </CardHeader>
            <CardContent class="space-y-4">
              <div class="grid grid-cols-1 gap-4">
                <div>
                  <Label class="text-sm font-medium text-gray-600">Vai trò</Label>
                  <p class="text-gray-900 font-medium">{{ user?.role?.roleName || 'Chưa có thông tin' }}</p>
                </div>
                <div>
                  <Label class="text-sm font-medium text-gray-600">Phòng ban</Label>
                  <p class="text-gray-900 font-medium">{{ user?.department?.departmentName || 'Chưa có thông tin' }}</p>
                </div>
                <div>
                  <Label class="text-sm font-medium text-gray-600">ID Người dùng</Label>
                  <p class="text-gray-900 font-medium">{{ user?.userId || 'Chưa có thông tin' }}</p>
                </div>
              </div>
            </CardContent>
          </Card>
        </div>

        <!-- Account Actions -->
        <div class="mt-8 pt-6 border-t border-gray-200">
          <h3 class="text-lg font-semibold text-gray-900 mb-4">Tùy chọn tài khoản</h3>
          <div class="flex justify-center">
            <Button variant="outline" class="flex items-center space-x-2" @click="handleEditProfile">
              <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z"></path>
              </svg>
              <span>Chỉnh sửa thông tin</span>
            </Button>
          </div>
        </div>
      </div>
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

// Props
interface Props {
  compact?: boolean
}

const props = withDefaults(defineProps<Props>(), {
  compact: false
})

// Lấy thông tin user từ auth store
const authStore = useAuthStore()
const user = computed(() => authStore.user)

// Type assertion để TypeScript hiểu đúng type
const currentUser = computed(() => user.value as any)

// State management
const loading = ref(false)
const error = ref<string | null>(null)

// Tính toán chữ cái đầu của tên người dùng
const userInitials = computed(() => {
  if (!user.value?.fullName) return 'U'
  return user.value.fullName
    .split(' ')
    .map(name => name.charAt(0))
    .join('')
    .toUpperCase()
    .slice(0, 2)
})

// Fetch user profile from API
const fetchUserProfile = async () => {
  loading.value = true
  error.value = null
  
  try {
    const config = useRuntimeConfig()
    const token = useCookie('access_token')
    
    if (!token.value) {
      throw new Error('Không tìm thấy token đăng nhập')
    }
    
    // Decode token để lấy userID
    let userId: number | undefined = undefined
    
    try {
      // Decode JWT token để lấy userID
      const tokenParts = token.value.split('.')
      if (tokenParts.length === 3) {
        const payload = JSON.parse(atob(tokenParts[1]))
        userId = payload.userId || payload.sub || payload.id
        console.log('Decoded token payload:', payload)
        console.log('Extracted userId:', userId)
      }
    } catch (decodeError) {
      console.warn('Không thể decode token:', decodeError)
      throw new Error('Token không hợp lệ hoặc không thể decode')
    }
    
    if (!userId) {
      throw new Error('Không tìm thấy userID trong token')
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
    } else {
      throw new Error('Không nhận được dữ liệu từ API')
    }
  } catch (err: any) {
    console.error('Lỗi khi lấy thông tin user:', err)
    error.value = err.message || 'Không thể tải thông tin người dùng'
  } finally {
    loading.value = false
  }
}

// Handle edit profile
const handleEditProfile = () => {
  navigateTo('/auth/edit')
}

// Auto fetch user profile on mount if not available
onMounted(async () => {
  // Only fetch if we have a token but no user data
  if (authStore.accessToken && !user.value) {
    await fetchUserProfile()
  }
})

// Watch for token changes and fetch user data
watch(() => authStore.accessToken, async (newToken) => {
  if (newToken) {
    await fetchUserProfile()
  }
}, { immediate: true })
</script>

<style scoped>
/* Custom styles if needed */
</style>