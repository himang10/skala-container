import { provide, inject, ref } from 'vue'

const TOAST_KEY = Symbol('toast')

export function createToast() {
  const toastRef = ref(null)

  function show(msg, type = 'success') {
    toastRef.value?.show(msg, type)
  }

  provide(TOAST_KEY, show)

  return { toastRef, show }
}

export function useToast() {
  return inject(TOAST_KEY)
}
