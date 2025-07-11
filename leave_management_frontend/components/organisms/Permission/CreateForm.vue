<template>
  <div class="flex justify-center items-center min-h-[60vh] bg-gray-50">
    <div class="bg-white shadow-xl rounded-2xl p-8 w-full max-w-lg">
      <div class="flex items-center mb-6 gap-2">
        <svg xmlns="http://www.w3.org/2000/svg" class="h-8 w-8 text-blue-500" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z" /></svg>
        <h2 class="text-2xl font-bold text-gray-800">Thêm quyền mới</h2>
      </div>
      <form @submit.prevent="handleSubmit" class="space-y-6">
        <div>
          <label class="block mb-1 font-semibold text-gray-700">Chọn Role</label>
          <Select v-model="selectedRoleId">
            <SelectTrigger class="w-full">{{ selectedRoleName || 'Chọn role...' }}</SelectTrigger>
            <SelectContent>
              <SelectItem v-for="role in roles" :key="role.roleId" :value="role.roleId">
                {{ role.roleName }}
              </SelectItem>
            </SelectContent>
          </Select>
        </div>
        <div>
          <label class="block mb-1 font-semibold text-gray-700">Chọn Feature</label>
          <Select v-model="selectedFeatureId">
            <SelectTrigger class="w-full">{{ selectedFeatureName || 'Chọn feature...' }}</SelectTrigger>
            <SelectContent>
              <SelectItem v-for="feature in features" :key="feature.featureId" :value="feature.featureId">
                {{ feature.featureName }}
              </SelectItem>
            </SelectContent>
          </Select>
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
import { ref, onMounted, computed } from 'vue'
import { useRuntimeConfig, useCookie } from '#app'
import { useToast } from 'vue-toastification'
import Select from '@/components/ui/select/Select.vue'
import SelectTrigger from '@/components/ui/select/SelectTrigger.vue'
import SelectContent from '@/components/ui/select/SelectContent.vue'
import SelectItem from '@/components/ui/select/SelectItem.vue'

const emit = defineEmits(['close', 'created'])
const config = useRuntimeConfig()
const token = useCookie('access_token')
const toast = useToast()

const roles = ref<any[]>([])
const features = ref<any[]>([])
const selectedRoleId = ref<number|null>(null)
const selectedFeatureId = ref<number|null>(null)

const selectedRoleName = computed(() => roles.value.find(r => r.roleId === selectedRoleId.value)?.roleName)
const selectedFeatureName = computed(() => features.value.find(f => f.featureId === selectedFeatureId.value)?.featureName)

async function fetchRoles() {
  try {
    const res = await $fetch<any[]>(`${config.public.apiBase}/roles/all`, {
      headers: { Authorization: `Bearer ${token.value}` },
    })
    roles.value = res
  } catch (e) {
    toast.error('Không thể tải danh sách role!')
  }
}

async function fetchFeatures() {
  try {
    const res = await $fetch<any[]>(`${config.public.apiBase}/features/all`, {
      headers: { Authorization: `Bearer ${token.value}` },
    })
    features.value = res
  } catch (e) {
    toast.error('Không thể tải danh sách feature!')
  }
}

onMounted(() => {
  fetchRoles()
  fetchFeatures()
})

async function handleSubmit() {
  if (!selectedRoleId.value || !selectedFeatureId.value) {
    toast.warning('Vui lòng chọn đủ Role và Feature!')
    return
  }
  try {
    await $fetch(`${config.public.apiBase}/role-features/add`, {
      method: 'POST',
      headers: { Authorization: `Bearer ${token.value}` },
      body: { roleId: selectedRoleId.value, featureId: selectedFeatureId.value },
    })
    toast.success('Thêm quyền thành công!')
    emit('created')
    emit('close')
  } catch (e) {
    toast.error('Thêm quyền thất bại!')
  }
}
</script>

<style scoped>
</style>