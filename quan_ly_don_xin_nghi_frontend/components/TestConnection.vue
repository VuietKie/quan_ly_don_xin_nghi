<template>
  <div class="p-4 border rounded-lg shadow-sm">
    <h2 class="text-xl font-bold mb-4">Test API Connection</h2>
    
    <div class="space-y-4">
      <!-- Test GET request -->
      <div class="p-4 border rounded">
        <h3 class="font-semibold mb-2">Test GET Request</h3>
        <button 
          @click="testGet" 
          class="px-4 py-2 bg-blue-500 text-white rounded hover:bg-blue-600"
          :disabled="loading"
        >
          {{ loading ? 'Loading...' : 'Test GET' }}
        </button>
        <div v-if="getResponse" class="mt-2 p-2 bg-gray-100 rounded">
          <pre>{{ JSON.stringify(getResponse, null, 2) }}</pre>
        </div>
      </div>

      <!-- Test POST request -->
      <div class="p-4 border rounded">
        <h3 class="font-semibold mb-2">Test POST Request</h3>
        <div class="mb-2">
          <input 
            v-model="postData" 
            type="text" 
            placeholder="Enter test data"
            class="w-full p-2 border rounded"
          />
        </div>
        <button 
          @click="testPost" 
          class="px-4 py-2 bg-green-500 text-white rounded hover:bg-green-600"
          :disabled="loading"
        >
          {{ loading ? 'Loading...' : 'Test POST' }}
        </button>
        <div v-if="postResponse" class="mt-2 p-2 bg-gray-100 rounded">
          <pre>{{ JSON.stringify(postResponse, null, 2) }}</pre>
        </div>
      </div>

      <!-- Error message -->
      <div v-if="error" class="p-4 bg-red-100 text-red-700 rounded">
        {{ error }}
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useApi } from '~/composables/useApi'

const api = useApi()
const loading = ref(false)
const error = ref('')
const getResponse = ref(null)
const postResponse = ref(null)
const postData = ref('')

const testGet = async () => {
  loading.value = true
  error.value = ''
  try {
    // Thay đổi endpoint này theo API của bạn
    const response = await api.get('/test')
    getResponse.value = response
  } catch (err: any) {
    error.value = err.message || 'An error occurred'
  } finally {
    loading.value = false
  }
}

const testPost = async () => {
  loading.value = true
  error.value = ''
  try {
    // Thay đổi endpoint này theo API của bạn
    const response = await api.post('/test', { message: postData.value })
    postResponse.value = response
  } catch (err: any) {
    error.value = err.message || 'An error occurred'
  } finally {
    loading.value = false
  }
}
</script> 