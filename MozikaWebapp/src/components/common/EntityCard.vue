<template>
  <div class="mz-card mz-card-interactive entity-card">
    <!-- En-tête -->
    <div class="entity-card-header">
      <div
        class="entity-icon-wrap"
        :style="{ background: iconBg, borderColor: iconBorder, color: iconColor }"
      >
        <slot name="icon">
          <svg
            width="16"
            height="16"
            viewBox="0 0 24 24"
            fill="none"
            stroke="currentColor"
            stroke-width="2"
          >
            <rect x="3" y="3" width="18" height="18" rx="3" />
          </svg>
        </slot>
      </div>
      <div class="entity-title-block">
        <h3 class="entity-title">{{ title }}</h3>
        <span class="entity-subtitle" v-if="subtitle">{{ subtitle }}</span>
      </div>
      <!-- Badge statut optionnel -->
      <slot name="badge" />
    </div>

    <!-- Champs -->
    <div class="entity-fields">
      <slot name="fields" />
    </div>

    <!-- Actions -->
    <div class="entity-card-footer">
      <slot name="actions" />
    </div>
  </div>
</template>

<script setup lang="ts">
defineProps<{
  title: string
  subtitle?: string
  iconBg?: string
  iconBorder?: string
  iconColor?: string
}>()
</script>

<style scoped>
.entity-card {
  display: flex;
  flex-direction: column;
  gap: var(--mz-space-3);
  padding: var(--mz-space-4);
}

.entity-card-header {
  display: flex;
  align-items: center;
  gap: var(--mz-space-3);
}

.entity-icon-wrap {
  width: 38px;
  height: 38px;
  border-radius: var(--mz-radius-sm);
  background: var(--mz-brand-accent-light);
  border: 1px solid var(--mz-border-accent);
  color: var(--mz-brand-accent);
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.entity-title-block {
  display: flex;
  flex-direction: column;
  gap: 2px;
  min-width: 0;
  flex: 1;
}

.entity-title {
  font-size: var(--mz-font-size-sm);
  font-weight: 700;
  color: var(--mz-text-on-light-high);
  margin: 0;
  overflow: hidden;
  white-space: nowrap;
  text-overflow: ellipsis;
}

.entity-subtitle {
  font-size: var(--mz-font-size-xs);
  color: var(--mz-text-on-light-muted);
  overflow: hidden;
  white-space: nowrap;
  text-overflow: ellipsis;
}

.entity-fields {
  display: flex;
  flex-direction: column;
  gap: 0;
  flex: 1;
}

.entity-card-footer {
  display: flex;
  justify-content: flex-end;
  padding-top: var(--mz-space-3);
  border-top: 1px solid var(--mz-border-subtle-light);
}
</style>
