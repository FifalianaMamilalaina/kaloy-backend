<template>
  <div class="mz-page-header">
    <!-- Titre + breadcrumb -->
    <div class="page-header-left">
      <div class="page-header-breadcrumb" v-if="breadcrumb">
        <span class="breadcrumb-parent">{{ breadcrumb }}</span>
        <svg
          width="14"
          height="14"
          viewBox="0 0 24 24"
          fill="none"
          stroke="currentColor"
          stroke-width="2"
        >
          <polyline points="9 18 15 12 9 6" />
        </svg>
        <span class="breadcrumb-current">{{ title }}</span>
      </div>
      <h1 class="page-header-title" v-else>{{ title }}</h1>
      <p class="page-header-subtitle" v-if="subtitle">{{ subtitle }}</p>
    </div>

    <!-- Actions à droite -->
    <div class="page-header-actions" v-if="$slots.actions">
      <slot name="actions" />
    </div>
  </div>
</template>

<script setup lang="ts">
defineProps<{
  title: string
  subtitle?: string
  breadcrumb?: string
}>()
</script>

<style scoped>
.mz-page-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: var(--mz-space-4);
  margin-bottom: var(--mz-space-6);
  padding-bottom: var(--mz-space-5);
  border-bottom: 1px solid var(--mz-border-subtle-light);
}

.page-header-left {
  display: flex;
  flex-direction: column;
  gap: var(--mz-space-1);
  min-width: 0;
}

.page-header-title {
  font-size: var(--mz-font-size-xl);
  font-weight: 800;
  color: var(--mz-text-on-light-high);
  margin: 0;
  letter-spacing: -0.02em;
}

.page-header-subtitle {
  font-size: var(--mz-font-size-sm);
  color: var(--mz-text-on-light-muted);
  margin: 0;
}

/* Breadcrumb mode */
.page-header-breadcrumb {
  display: flex;
  align-items: center;
  gap: var(--mz-space-2);
  color: var(--mz-text-on-light-muted);
  font-size: var(--mz-font-size-sm);
}

.breadcrumb-parent {
  color: var(--mz-text-on-light-muted);
  font-weight: 500;
}

.breadcrumb-current {
  font-weight: 700;
  color: var(--mz-text-on-light-high);
}

/* Actions */
.page-header-actions {
  display: flex;
  align-items: center;
  gap: var(--mz-space-3);
  flex-shrink: 0;
}

/* Responsive */
@media (max-width: 640px) {
  .mz-page-header {
    flex-direction: column;
    align-items: flex-start;
  }

  .page-header-actions {
    width: 100%;
    justify-content: flex-end;
  }
}
</style>
