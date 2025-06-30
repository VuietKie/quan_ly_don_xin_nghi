import { defineStore } from "pinia";
import { useCookie } from "#app";

// Match the User interface from Login.vue
interface Department {
  departmentId: number;
  departmentName: string;
  idManager: number;
}

interface Role {
  roleId: number;
  roleName: string;
}

interface User {
  userId: number;
  username: string;
  password: string;
  fullName: string;
  department: Department;
  role: Role;
  email: string;
}

export const useAuthStore = defineStore("auth", () => {
  // Token is stored in cookie
  const accessToken = useCookie<string | null>("access_token", {
    maxAge: 60 * 60 * 24 * 7, // 7 ngày
    sameSite: 'lax',
    path: '/',
    secure: process.client && location.protocol === 'https:',
  });
  // User info và roles
  const user = ref<User | null>(null);
  const roles = ref<string[]>([]);

  // Simple computed for auth status
  const isAuthenticated = computed(() => !!accessToken.value);

  // Setters for user và roles
  function setUser(newUser: User | null) {
    user.value = newUser;
  }
  function setRoles(newRoles: string[]) {
    roles.value = newRoles;
  }
  function hasRole(role: string) {
    return roles.value.includes(role);
  }
  function logout() {
    console.log('🔄 AuthStore: Clearing authentication data...');
    accessToken.value = null;
    user.value = null;
    roles.value = [];
    
    // Clear other auth-related cookies
    const refreshToken = useCookie('refresh_token');
    const userData = useCookie('user_data');
    
    if (refreshToken.value) {
      refreshToken.value = null;
    }
    if (userData.value) {
      userData.value = null;
    }
    
    console.log('✅ AuthStore: Logout completed');
  }

  return {
    accessToken,
    user,
    roles,
    isAuthenticated,
    setUser,
    setRoles,
    hasRole,
    logout,
  };
});
