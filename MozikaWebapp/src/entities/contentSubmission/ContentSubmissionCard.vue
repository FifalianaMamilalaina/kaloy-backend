<template>
  <div class="mz-card mz-card-interactive submission-card">
    <!-- Header : Icône de soumission + Nom du fichier + Statut -->
    <div class="submission-card-header">
      <div class="submission-icon-wrap" :class="statusClass">
        <svg v-if="statusName === 'SUCCESS'" width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5">
          <polyline points="20 6 9 17 4 12"/>
        </svg>
        <svg v-else-if="statusName === 'ERROR'" width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5">
          <circle cx="12" cy="12" r="10"/>
          <line x1="15" y1="9" x2="9" y2="15"/>
          <line x1="9" y1="9" x2="15" y2="15"/>
        </svg>
        <svg v-else-if="statusName === 'PROCESSING'" width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
          <path d="M21 12a9 9 0 1 1-6.219-8.56"/>
        </svg>
        <svg v-else width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
          <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/>
          <polyline points="14 2 14 8 20 8"/>
          <line x1="12" y1="18" x2="12" y2="12"/>
          <line x1="9" y1="15" x2="15" y2="15"/>
        </svg>
      </div>

      <div class="submission-meta">
        <h3 class="submission-filename" :title="contentSubmissionData['sourceFilename'] ?? 'Soumission'">
          {{ contentSubmissionData['sourceFilename'] ?? `Soumission #${contentSubmissionData['id']}` }}
        </h3>
        <div class="submission-submeta">
          <span class="submission-id">#{{ contentSubmissionData['id'] }}</span>
          <span class="submission-type" v-if="contentSubmissionData['fileType']">
            {{ contentSubmissionData['fileType'] }}
          </span>
        </div>
      </div>

      <span class="mz-badge submission-badge" :class="badgeClass">
        {{ statusLabel }}
      </span>
    </div>

    <!-- Message d'erreur éventuel -->
    <div v-if="contentSubmissionData['errorMessage']" class="submission-error-banner" :title="contentSubmissionData['errorMessage']">
      ⚠️ {{ contentSubmissionData['errorMessage'] }}
    </div>

    <!-- Corps des champs visibles -->
    <div class="submission-fields">
      <template v-for="field in ContentSubmission.getAllSearchFieldsMetadata()" :key="field.key">
        <div
          v-if="field.showInTable && visibleFields.includes(field.key) && !['sourceFilename', 'errorMessage', 'fileType'].includes(field.key)"
          class="submission-field-row"
        >
          <span class="field-label">{{ field.label }}</span>
          <span class="field-value">
            <template v-if="field.type === 'date'">
              {{ formatDate(contentSubmissionData[field.key]) }}
            </template>
            <template v-else-if="field.type === 'number'">
              {{ contentSubmissionData[field.key] != null ? $n(contentSubmissionData[field.key], 'decimal') : '--' }}
            </template>
            <template v-else-if="field.type === 'select'">
              {{ contentSubmissionData[field.key] != null ? (contentSubmissionData[field.key].getKeyValue?.() ?? contentSubmissionData[field.key]) : '--' }}
            </template>
            <template v-else>
              {{ contentSubmissionData[field.key] ?? '--' }}
            </template>
          </span>
        </div>
      </template>
    </div>

    <!-- Actions -->
    <div class="submission-card-footer">
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
import { ContentSubmission } from '@/models/ContentSubmissionModel'
import EntityRowActions from '@/components/table/EntityRowActions.vue'
import { useDateFormat } from '@/composables/useDateFormat'

const props = defineProps({
  contentSubmission: {
    type: Object as PropType<ContentSubmission>,
    required: true,
  },
  viewAction: {
    type: Boolean,
    default: true,
  },
  editAction: {
    type: Boolean,
    default: true,
  },
  removeAction: {
    type: Boolean,
    default: true,
  },
  visibleFields: {
    type: Array as PropType<string[]>,
    default: () => [],
  },
})

const contentSubmissionData = computed(() => {
  return props.contentSubmission as Record<string, any>
})

const statusObj = computed(() => {
  return contentSubmissionData.value['statusidSubmissionStatuses']
})

const statusLabel = computed(() => {
  if (!statusObj.value) return 'En attente'
  if (typeof statusObj.value === 'object') {
    return statusObj.value.getKeyValue?.() ?? statusObj.value.name ?? 'Inconnu'
  }
  return String(statusObj.value)
})

const statusName = computed(() => {
  const lbl = statusLabel.value.toUpperCase()
  if (lbl.includes('SUCC') || lbl.includes('PUBLI')) return 'SUCCESS'
  if (lbl.includes('ERR') || lbl.includes('ECHEC')) return 'ERROR'
  if (lbl.includes('COURS') || lbl.includes('PROCESS')) return 'PROCESSING'
  return 'PENDING'
})

const statusClass = computed(() => {
  switch (statusName.value) {
    case 'SUCCESS': return 'status-icon-success'
    case 'ERROR': return 'status-icon-error'
    case 'PROCESSING': return 'status-icon-processing'
    default: return 'status-icon-pending'
  }
})

const badgeClass = computed(() => {
  switch (statusName.value) {
    case 'SUCCESS': return 'mz-badge-success'
    case 'ERROR': return 'mz-badge-danger'
    case 'PROCESSING': return 'mz-badge-accent'
    default: return 'mz-badge-neutral'
  }
})

const emit = defineEmits<{
  (e: 'request-delete', cs: ContentSubmission): void
  (e: 'request-view', cs: ContentSubmission): void
  (e: 'request-update', cs: ContentSubmission): void
}>()

const deleteRow = () => emit('request-delete', props.contentSubmission)
const viewRow = () => emit('request-view', props.contentSubmission)
const updateRow = () => emit('request-update', props.contentSubmission)
const { formatDate } = useDateFormat()
</script>

<style scoped>
.submission-card {
  display: flex;
  flex-direction: column;
  height: 100%;
  padding: var(--mz-space-4);
  gap: var(--mz-space-3);
  transition: transform var(--mz-transition-base), box-shadow var(--mz-transition-base);
}

.submission-card:hover {
  transform: translateY(-2px);
  box-shadow: var(--mz-shadow-card-hover);
}

.submission-card-header {
  display: flex;
  align-items: center;
  gap: var(--mz-space-3);
  padding-bottom: var(--mz-space-3);
  border-bottom: 1px solid var(--mz-border-subtle-light);
}

.submission-icon-wrap {
  width: 44px;
  height: 44px;
  border-radius: var(--mz-radius-md);
  color: #ffffff;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.status-icon-success {
  background: linear-gradient(135deg, #10b981 0%, #059669 100%);
  box-shadow: 0 3px 8px rgba(16, 185, 129, 0.25);
}

.status-icon-error {
  background: linear-gradient(135deg, #ef4444 0%, #dc2626 100%);
  box-shadow: 0 3px 8px rgba(239, 68, 68, 0.25);
}

.status-icon-processing {
  background: linear-gradient(135deg, #8b5cf6 0%, #6366f1 100%);
  box-shadow: 0 3px 8px rgba(139, 92, 246, 0.25);
  animation: pulse 2s infinite;
}

.status-icon-pending {
  background: linear-gradient(135deg, #64748b 0%, #475569 100%);
  box-shadow: 0 3px 8px rgba(100, 116, 139, 0.25);
}

@keyframes pulse {
  0%, 100% { opacity: 1; }
  50% { opacity: 0.75; }
}

.submission-meta {
  display: flex;
  flex-direction: column;
  min-width: 0;
  flex: 1;
}

.submission-filename {
  font-size: var(--mz-font-size-base);
  font-weight: 700;
  color: var(--mz-text-on-light-high);
  margin: 0;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.submission-submeta {
  display: flex;
  align-items: center;
  gap: var(--mz-space-2);
  margin-top: 2px;
}

.submission-id {
  font-size: 11px;
  color: var(--mz-text-on-light-muted);
  font-weight: 600;
}

.submission-type {
  font-size: 10px;
  background: #f1f5f9;
  color: #475569;
  padding: 1px 6px;
  border-radius: 4px;
  font-weight: 700;
}

.submission-badge {
  flex-shrink: 0;
}

.mz-badge-danger {
  background: #fef2f2;
  color: #ef4444;
  border: 1px solid #fecaca;
}

.mz-badge-neutral {
  background: #f1f5f9;
  color: #64748b;
  border: 1px solid #e2e8f0;
}

.submission-error-banner {
  font-size: 11px;
  color: #dc2626;
  background: #fef2f2;
  padding: 6px 10px;
  border-radius: var(--mz-radius-sm);
  border: 1px solid #fecaca;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.submission-fields {
  display: flex;
  flex-direction: column;
  gap: 6px;
  flex: 1;
  font-size: var(--mz-font-size-xs);
}

.submission-field-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: var(--mz-space-2);
  padding: 4px 0;
  border-bottom: 1px solid var(--mz-border-subtle-light);
}

.submission-field-row:last-child {
  border-bottom: none;
}

.field-label {
  font-weight: 600;
  text-transform: uppercase;
  letter-spacing: 0.04em;
  color: var(--mz-text-on-light-muted);
  font-size: 10px;
}

.field-value {
  color: var(--mz-text-on-light-high);
  font-weight: 500;
  text-align: right;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  max-width: 60%;
}

.submission-card-footer {
  display: flex;
  justify-content: flex-end;
  padding-top: var(--mz-space-2);
  border-top: 1px solid var(--mz-border-subtle-light);
  margin-top: auto;
}
</style>
