<template>
  <div class="flex flex-col items-center justify-center min-h-screen bg-gray-50">
    <div class="bg-white shadow-lg rounded-xl p-8 flex flex-col items-center">
      <svg xmlns="http://www.w3.org/2000/svg" class="h-16 w-16 text-red-500 mb-4" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M18.364 5.636l-1.414-1.414A9 9 0 105.636 18.364l1.414 1.414A9 9 0 1018.364 5.636z" /><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 9l6 6m0-6l-6 6" /></svg>
      <h1 class="text-2xl font-bold text-gray-800 mb-2">Không có quyền truy cập</h1>
      <p class="text-gray-600 mb-6">Bạn không được phép truy cập vào trang này.</p>
      <NuxtLink to="/" class="px-4 py-2 bg-blue-500 hover:bg-blue-600 text-white rounded-lg font-semibold">Về trang chủ</NuxtLink>
    </div>
  </div>
</template>

<script setup lang="ts">
</script> 