<template>
  <transition name="fade" mode="out-in">
    <component :is="currentLayout" :key="layoutState" />
  </transition>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useLayoutStore } from '@/stores/useLayoutStore.ts'

// Import the available layouts
import SidebarLayout from '@/layouts/SidebarLayout.vue'
import TopbarLayout from '@/layouts/TopbarLayout.vue'

// Access layout store
const layoutStore = useLayoutStore()

// Reactive state for layout
const layoutState = computed(() => layoutStore.currentLayout)

// Dynamically choose layout component
const currentLayout = computed(() => {
  switch (layoutState.value) {
    case 'sidebar':
      return SidebarLayout
    case 'topbar':
      return TopbarLayout
    default:
      return SidebarLayout
  }
})
</script>

<style scoped>
/* Smooth layout switch animation */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}
.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>
