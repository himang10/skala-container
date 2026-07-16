<template>
  <div class="position-fixed top-0 end-0 p-3" style="z-index: 9999">
    <TransitionGroup name="toast">
      <div
        v-for="toast in toasts"
        :key="toast.id"
        :class="`toast align-items-center text-bg-${toast.type} border-0 show mb-2`"
        role="alert"
      >
        <div class="d-flex">
          <div class="toast-body">{{ toast.msg }}</div>
          <button
            type="button"
            class="btn-close btn-close-white me-2 m-auto"
            @click="remove(toast.id)"
          ></button>
        </div>
      </div>
    </TransitionGroup>
  </div>
</template>

<script setup>
import { ref } from 'vue'

const toasts = ref([])

// 외부에서 호출 가능하도록 expose
function show(msg, type = 'success') {
  const id = Date.now()
  toasts.value.push({ id, msg, type })
  setTimeout(() => remove(id), 3000)
}

function remove(id) {
  toasts.value = toasts.value.filter((t) => t.id !== id)
}

defineExpose({ show })
</script>

<style scoped>
.toast-enter-active,
.toast-leave-active {
  transition: all 0.3s ease;
}
.toast-enter-from,
.toast-leave-to {
  opacity: 0;
  transform: translateX(30px);
}
</style>
