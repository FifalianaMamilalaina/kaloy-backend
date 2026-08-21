import { defineStore } from 'pinia'
import { ref, watch } from 'vue'

export const useLayoutStore = defineStore('layout', () => {
  const currentLayout = ref(localStorage.getItem('layoutMode') || 'sidebar')

  function setLayout(mode: 'sidebar' | 'topbar') {
    currentLayout.value = mode
    localStorage.setItem('layoutMode', mode)
  }

  watch(
    currentLayout,
    (newVal) => {
      document.body.dataset.layout = newVal
    },
    { immediate: true },
  )

  return { currentLayout, setLayout }
})
