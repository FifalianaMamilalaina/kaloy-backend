<template>
  <div class="flex flex-col w-full h-screen overflow-hidden">
    <!-- Top Navigation -->
    <GenesisTopNavbar />

    <!-- Main content -->
    <main :class="['flex-1 overflow-y-auto', isHome ? 'p-0' : 'bg-base-100 p-6']">
      <router-view />
    </main>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import GenesisTopNavbar from '@/components/navbar/GenesisTopNavbar.vue'

const route = useRoute()
const isHome = computed(() => route.name === 'home' || route.path === '/home' || route.path === '/')
</script>
