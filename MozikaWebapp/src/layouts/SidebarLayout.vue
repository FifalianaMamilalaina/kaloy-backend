<template>
  <div class="sidebar-layout">
    <GenesisSideNavbar />
    <!-- Main content -->
    <main class="sidebar-layout-main">
      <router-view />
    </main>
  </div>
</template>

<script setup lang="ts">
import GenesisSideNavbar from '@/components/navbar/GenesisSideNavbar.vue'
</script>

<style scoped>
.sidebar-layout {
  display: flex;
  width: 100%;
  height: 100vh;
  overflow: hidden;
  background: var(--mz-surface-light-soft);
}

.sidebar-layout-main {
  flex: 1;
  height: 100%;
  overflow-y: auto;
  background: var(--mz-surface-light-soft);
  padding: var(--mz-space-6);
}

/* Scrollbar style */
.sidebar-layout-main::-webkit-scrollbar {
  width: 6px;
}
.sidebar-layout-main::-webkit-scrollbar-track {
  background: transparent;
}
.sidebar-layout-main::-webkit-scrollbar-thumb {
  background: #cbd5e1;
  border-radius: 99px;
}
.sidebar-layout-main::-webkit-scrollbar-thumb:hover {
  background: #94a3b8;
}
</style>
