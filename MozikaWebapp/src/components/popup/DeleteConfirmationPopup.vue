<template>
  <GenesisPopup :visible="visible" @close="cancel">
    <div class="delete-popup-content">
      <!-- Icône danger -->
      <div class="delete-icon-ring">
        <svg
          width="28"
          height="28"
          viewBox="0 0 24 24"
          fill="none"
          stroke="currentColor"
          stroke-width="2"
        >
          <polyline points="3 6 5 6 21 6" />
          <path d="M19 6l-1 14a2 2 0 0 1-2 2H8a2 2 0 0 1-2-2L5 6" />
          <path d="M10 11v6" />
          <path d="M14 11v6" />
          <path d="M9 6V4a1 1 0 0 1 1-1h4a1 1 0 0 1 1 1v2" />
        </svg>
      </div>

      <!-- Texte -->
      <div class="delete-text-block">
        <p class="delete-message">{{ message }}</p>
        <p class="delete-sub" v-if="subMessage">{{ subMessage }}</p>
      </div>

      <!-- Boutons -->
      <div class="delete-actions">
        <button class="mz-btn mz-btn-outline" @click="cancel">
          {{ $t('button.cancel') }}
        </button>
        <button class="mz-btn delete-confirm-btn" @click="confirm">
          <svg
            width="15"
            height="15"
            viewBox="0 0 24 24"
            fill="none"
            stroke="currentColor"
            stroke-width="2"
          >
            <polyline points="3 6 5 6 21 6" />
            <path d="M19 6l-1 14a2 2 0 0 1-2 2H8a2 2 0 0 1-2-2L5 6" />
          </svg>
          {{ $t('button.delete') }}
        </button>
      </div>
    </div>
  </GenesisPopup>
</template>

<script setup lang="ts">
import GenesisPopup from '@/components/popup/GenesisPopup.vue'

const props = defineProps<{
  visible: boolean
  message: string
  subMessage?: string
}>()

const emit = defineEmits<{
  (e: 'confirm'): void
  (e: 'cancel'): void
}>()

const confirm = () => emit('confirm')
const cancel = () => emit('cancel')
</script>

<style scoped>
.delete-popup-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
  gap: var(--mz-space-5);
  padding: var(--mz-space-8) var(--mz-space-6) var(--mz-space-6);
}

/* Icône */
.delete-icon-ring {
  width: 72px;
  height: 72px;
  border-radius: 50%;
  background: var(--mz-color-danger-bg);
  border: 2px solid rgba(239, 68, 68, 0.25);
  color: var(--mz-color-danger);
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

/* Texte */
.delete-text-block {
  display: flex;
  flex-direction: column;
  gap: var(--mz-space-2);
}

.delete-message {
  font-size: var(--mz-font-size-md);
  font-weight: 700;
  color: var(--mz-text-on-light-high);
  margin: 0;
  line-height: 1.4;
}

.delete-sub {
  font-size: var(--mz-font-size-sm);
  color: var(--mz-text-on-light-muted);
  margin: 0;
}

/* Boutons */
.delete-actions {
  display: flex;
  gap: var(--mz-space-3);
  width: 100%;
  justify-content: center;
}

.delete-confirm-btn {
  background: var(--mz-color-danger);
  color: #ffffff;
  border: 1.5px solid var(--mz-color-danger);
  gap: var(--mz-space-2);
  display: inline-flex;
  align-items: center;
}

.delete-confirm-btn:hover {
  background: #dc2626;
  border-color: #dc2626;
  transform: translateY(-1px);
  box-shadow: 0 6px 18px rgba(239, 68, 68, 0.35);
}
</style>
