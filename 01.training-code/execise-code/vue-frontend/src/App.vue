<template>
  <div class="d-flex" style="min-height: 100vh">
    <AppSidebar />
    <main class="content-wrapper">
      <RouterView />
    </main>
    <ToastNotification ref="toastRef" />
  </div>
</template>

<script setup>
import { ref, provide } from 'vue'
import AppSidebar from '@/components/AppSidebar.vue'
import ToastNotification from '@/components/ToastNotification.vue'

const toastRef = ref(null)

// 하위 컴포넌트에서 useToast()로 접근 가능하도록 제공
provide('toast', (msg, type = 'success') => toastRef.value?.show(msg, type))
</script>
