<template>
  <transition name="mz-popup-fade">
    <div
      v-if="visible"
      class="mz-popup-overlay"
      @click.self="close"
      role="dialog"
      aria-modal="true"
    >
      <div class="mz-popup-panel">
        <!-- Header -->
        <div class="mz-popup-header" v-if="title">
          <h5 class="mz-popup-title">{{ title }}</h5>
          <button type="button" class="mz-popup-close-btn" @click="close" aria-label="Fermer">
            <XIcon />
          </button>
        </div>
        <button
          v-else
          type="button"
          class="mz-popup-close-btn mz-popup-close-floating"
          @click="close"
          aria-label="Fermer"
        >
          <XIcon />
        </button>

        <!-- Body -->
        <div class="mz-popup-body">
          <slot />
        </div>

        <!-- Footer -->
        <div class="mz-popup-footer">
          <slot name="footer" />
        </div>
      </div>
    </div>
  </transition>
</template>

<script setup lang="ts">
import XIcon from '../icons/XIcon.vue'

const props = defineProps<{
  title?: string
  visible: boolean
}>()

const emit = defineEmits<{
  (e: 'close', value: boolean): void
}>()

const close = () => emit('close', false)
</script>

<style>
/* Overlay */
.mz-popup-overlay {
  position: fixed;
  inset: 0;
  background: rgba(14, 9, 34, 0.55);
  backdrop-filter: blur(6px);
  -webkit-backdrop-filter: blur(6px);
  z-index: 9000;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 16px;
}

/* Panel */
.mz-popup-panel {
  background: #ffffff;
  border-radius: 20px;
  box-shadow:
    0 24px 60px rgba(14, 9, 34, 0.24),
    0 4px 16px rgba(14, 9, 34, 0.1);
  width: 100%;
  max-width: 520px;
  max-height: 90vh;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  position: relative;
}

/* Header */
.mz-popup-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 20px 24px 16px;
  border-bottom: 1px solid #f1f5f9;
}

.mz-popup-title {
  font-size: 17px;
  font-weight: 700;
  color: #0f172a;
  margin: 0;
}

/* Bouton fermer */
.mz-popup-close-btn {
  width: 32px;
  height: 32px;
  border-radius: 8px;
  border: none;
  background: transparent;
  color: #94a3b8;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.15s ease;
  flex-shrink: 0;
}

.mz-popup-close-btn:hover {
  background: #f1f5f9;
  color: #334155;
}

.mz-popup-close-floating {
  position: absolute;
  top: 16px;
  right: 16px;
  z-index: 1;
}

/* Body */
.mz-popup-body {
  flex: 1;
  overflow-y: auto;
  padding: 0;
}

.mz-popup-body::-webkit-scrollbar {
  width: 4px;
}
.mz-popup-body::-webkit-scrollbar-thumb {
  background: #e2e8f0;
  border-radius: 99px;
}

/* Footer */
.mz-popup-footer {
  padding: 16px 24px;
  border-top: 1px solid #f1f5f9;
  display: flex;
  justify-content: flex-end;
  gap: 8px;
  min-height: 0;
}

.mz-popup-footer:empty {
  display: none;
}

/* Animation */
.mz-popup-fade-enter-active,
.mz-popup-fade-leave-active {
  transition: opacity 0.2s ease;
}
.mz-popup-fade-enter-active .mz-popup-panel,
.mz-popup-fade-leave-active .mz-popup-panel {
  transition:
    opacity 0.2s ease,
    transform 0.22s cubic-bezier(0.34, 1.56, 0.64, 1);
}
.mz-popup-fade-enter-from,
.mz-popup-fade-leave-to {
  opacity: 0;
}
.mz-popup-fade-enter-from .mz-popup-panel,
.mz-popup-fade-leave-to .mz-popup-panel {
  opacity: 0;
  transform: scale(0.94) translateY(12px);
}
</style>
