<template>
  <teleport to="#freeze-screen">
    <FreezeScreen />
  </teleport>
  <router-view />
</template>

<script setup lang="ts">
import FreezeScreen from '@/components/loading/FreezeScreen.vue'
import { useGlobalStore } from '@/stores/useGlobalStore.ts'
useGlobalStore()
</script>

<style scoped></style>
