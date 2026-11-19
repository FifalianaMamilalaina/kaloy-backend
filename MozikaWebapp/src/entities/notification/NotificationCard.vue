<template>
  <div class="mz-card mz-card-interactive notif-card">
    <!-- En-tête -->
    <div class="notif-header">
      <div class="notif-icon-wrap" :class="isRead ? 'is-read' : 'is-unread'">
        <svg
          width="16"
          height="16"
          viewBox="0 0 24 24"
          fill="none"
          stroke="currentColor"
          stroke-width="2"
        >
          <path d="M18 8A6 6 0 0 0 6 8c0 7-3 9-3 9h18s-3-2-3-9" />
          <path d="M13.73 21a2 2 0 0 1-3.46 0" />
        </svg>
      </div>
      <div class="notif-title-block">
        <h3 class="notif-title">Notification #{{ notificationData['id'] }}</h3>
        <span class="notif-subtitle" v-if="notificationData['content']">
          {{ notificationData['content'] }}
        </span>
      </div>
      <!-- Badge lu/non lu -->
      <span class="mz-badge" :class="isRead ? 'mz-badge-success' : 'mz-badge-warning'">
        {{ isRead ? 'Lu' : 'Non lu' }}
      </span>
    </div>

    <!-- Champs -->
    <div class="notif-fields">
      <template v-for="field in Notification.getAllSearchFieldsMetadata()" :key="field.key">
        <div v-if="field.showInTable && visibleFields.includes(field.key)" class="notif-field-row">
          <span class="field-label">{{ field.label }}</span>
          <span class="field-value">
            <template v-if="field.type === 'date'">{{
              formatDate(notificationData[field.key])
            }}</template>
            <template v-else-if="field.type === 'number'">
              {{
                notificationData[field.key] != null
                  ? $n(notificationData[field.key], 'decimal')
                  : '--'
              }}
            </template>
            <template v-else-if="field.type === 'select'">
              {{
                notificationData[field.key] != null
                  ? notificationData[field.key].getKeyValue()
                  : '--'
              }}
            </template>
            <template v-else-if="field.type === 'file'">
              <img
                v-if="notificationData[field.key]"
                :src="getUrl(notificationData[field.key])"
                :alt="field.key"
                class="field-img"
              />
            </template>
            <template v-else>{{ notificationData[field.key] ?? '--' }}</template>
          </span>
        </div>
      </template>
    </div>

    <!-- Actions -->
    <div class="notif-footer">
      <EntityRowActions
        :view="viewAction"
        :remove="removeAction"
        :edit="editAction"
        @delete-row="deleteRow"
        @view-row="viewRow"
        @edit-row="updateRow"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, type PropType } from 'vue'
import { Notification } from '@/models/NotificationModel'
import EntityRowActions from '@/components/table/EntityRowActions.vue'
import { useDateFormat } from '@/composables/useDateFormat'
import { getUrl } from '@/composables/useImage'

const props = defineProps({
  notification: { type: Object as PropType<Notification>, required: true },
  viewAction: { type: Boolean, default: true },
  editAction: { type: Boolean, default: true },
  removeAction: { type: Boolean, default: true },
  visibleFields: { type: Array as PropType<string[]>, default: () => [] },
})

const notificationData = computed(() => props.notification as Record<string, any>)
const isRead = computed(() => !!notificationData.value['isRead'])

const emit = defineEmits<{
  (e: 'request-delete', notification: Notification): void
  (e: 'request-view', notification: Notification): void
  (e: 'request-update', notification: Notification): void
}>()

const deleteRow = () => emit('request-delete', props.notification)
const viewRow = () => emit('request-view', props.notification)
const updateRow = () => emit('request-update', props.notification)
const { formatDate } = useDateFormat()
</script>

<style scoped>
.notif-card {
  display: flex;
  flex-direction: column;
  gap: var(--mz-space-3);
  padding: var(--mz-space-4);
}

.notif-header {
  display: flex;
  align-items: center;
  gap: var(--mz-space-3);
}

.notif-icon-wrap {
  width: 38px;
  height: 38px;
  border-radius: var(--mz-radius-sm);
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  transition: all 0.2s ease;
}

.notif-icon-wrap.is-unread {
  background: var(--mz-color-warning-bg);
  border: 1px solid rgba(245, 158, 11, 0.3);
  color: var(--mz-color-warning);
}

.notif-icon-wrap.is-read {
  background: var(--mz-color-success-bg);
  border: 1px solid rgba(16, 185, 129, 0.3);
  color: var(--mz-color-success);
}

.notif-title-block {
  display: flex;
  flex-direction: column;
  gap: 2px;
  min-width: 0;
  flex: 1;
}

.notif-title {
  font-size: var(--mz-font-size-sm);
  font-weight: 700;
  color: var(--mz-text-on-light-high);
  margin: 0;
}

.notif-subtitle {
  font-size: var(--mz-font-size-xs);
  color: var(--mz-text-on-light-muted);
  overflow: hidden;
  white-space: nowrap;
  text-overflow: ellipsis;
}

.notif-fields {
  display: flex;
  flex-direction: column;
  gap: 0;
  flex: 1;
}

.notif-field-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: var(--mz-space-2);
  padding: 6px 0;
  border-bottom: 1px solid var(--mz-border-subtle-light);
}
.notif-field-row:last-child {
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

.field-img {
  max-height: 36px;
  max-width: 36px;
  border-radius: var(--mz-radius-xs);
  object-fit: cover;
}

.notif-footer {
  display: flex;
  justify-content: flex-end;
  padding-top: var(--mz-space-3);
  border-top: 1px solid var(--mz-border-subtle-light);
}
</style>
