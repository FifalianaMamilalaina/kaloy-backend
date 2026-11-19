<template>
  <div class="entity-field-row">
    <span class="field-label">{{ label }}</span>
    <span class="field-value">
      <slot>{{ value ?? '--' }}</slot>
    </span>
  </div>
</template>

<script setup lang="ts">
defineProps<{
  label: string
  value?: string | number | null
}>()
</script>

<style scoped>
.entity-field-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: var(--mz-space-2);
  padding: 6px 0;
  border-bottom: 1px solid var(--mz-border-subtle-light);
}
.entity-field-row:last-child {
  border-bottom: none;
}
.field-label {
  font-size: var(--mz-font-size-xs);
  font-weight: 700;
  text-transform: uppercase;
  letter-spacing: 0.04em;
  color: var(--mz-text-on-light-muted);
  flex-shrink: 0;
}
.field-value {
  font-size: var(--mz-font-size-sm);
  color: var(--mz-text-on-light-high);
  text-align: right;
  max-width: 60%;
  overflow: hidden;
  white-space: nowrap;
  text-overflow: ellipsis;
}
</style>
