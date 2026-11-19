<template>
  <div class="submission-details-wrapper">
    <div v-if="contentSubmission" class="space-y-6">
      <!-- Hero de la soumission -->
      <div class="submission-hero">
        <div class="submission-hero-icon" :class="heroStatusClass">
          <svg v-if="statusName === 'SUCCESS'" width="36" height="36" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5">
            <polyline points="20 6 9 17 4 12"/>
          </svg>
          <svg v-else-if="statusName === 'ERROR'" width="36" height="36" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5">
            <circle cx="12" cy="12" r="10"/>
            <line x1="15" y1="9" x2="9" y2="15"/>
            <line x1="9" y1="9" x2="15" y2="15"/>
          </svg>
          <svg v-else width="36" height="36" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/>
            <polyline points="14 2 14 8 20 8"/>
            <line x1="12" y1="18" x2="12" y2="12"/>
            <line x1="9" y1="15" x2="15" y2="15"/>
          </svg>
        </div>
        <div class="submission-hero-meta">
          <span class="submission-hero-kicker">Fiche de Soumission Pipeline</span>
          <h2 class="submission-hero-title">
            {{ contentSubmission.sourceFilename ?? `Soumission #${contentSubmission.id}` }}
          </h2>
          <div class="submission-hero-badges">
            <span class="mz-badge" :class="badgeClass">
              {{ statusLabel }}
            </span>
            <span class="mz-badge mz-badge-accent" v-if="contentSubmission.fileType">
              {{ contentSubmission.fileType }}
            </span>
            <span class="mz-badge" style="background:#f1f5f9;color:#64748b;" v-if="contentSubmission.id">
              ID #{{ contentSubmission.id }}
            </span>
          </div>
        </div>
      </div>

      <!-- Erreur détaillée si présente -->
      <div v-if="contentSubmission.errorMessage" class="error-callout-card">
        <div class="error-callout-header">
          <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <circle cx="12" cy="12" r="10"/>
            <line x1="12" y1="8" x2="12" y2="12"/>
            <line x1="12" y1="16" x2="12.01" y2="16"/>
          </svg>
          <strong>Détail de l'erreur d'ingestion</strong>
        </div>
        <p class="error-callout-body">{{ contentSubmission.errorMessage }}</p>
      </div>

      <!-- Détails et Horodatages -->
      <div class="mz-card detail-section">
        <h3 class="section-title">Informations de Traitement</h3>
        <div class="detail-grid">
          <div class="detail-item">
            <span class="detail-label">Fichier source</span>
            <span class="detail-value filename-value">{{ contentSubmission.sourceFilename ?? '--' }}</span>
          </div>

          <div class="detail-item">
            <span class="detail-label">Artiste associé</span>
            <span class="detail-value">{{ contentSubmission.artistidArtists?.getKeyValue?.() ?? '--' }}</span>
          </div>

          <div class="detail-item">
            <span class="detail-label">Type de fichier</span>
            <span class="detail-value">{{ contentSubmission.fileType ?? '--' }}</span>
          </div>

          <div class="detail-item">
            <span class="detail-label">Statut du pipeline</span>
            <span class="detail-value">
              <span class="mz-badge" :class="badgeClass">{{ statusLabel }}</span>
            </span>
          </div>

          <div class="detail-item" v-if="contentSubmission.submittedAt">
            <span class="detail-label">Reçu par le pipeline le</span>
            <span class="detail-value">{{ formatDate(contentSubmission.submittedAt) }}</span>
          </div>

          <div class="detail-item" v-if="contentSubmission.processedAt">
            <span class="detail-label">Traité le</span>
            <span class="detail-value">{{ formatDate(contentSubmission.processedAt) }}</span>
          </div>
        </div>
      </div>

      <!-- Actions -->
      <div class="detail-actions">
        <GenesisButton
          @click="goToUpdateFormView(contentSubmission)"
          class="mz-btn mz-btn-secondary"
        >
          <EditIcon class="action-icon" />
          <span>{{ $t('button.update') }}</span>
        </GenesisButton>

        <GenesisButton
          @click="openDeletePopup(contentSubmission)"
          class="mz-btn"
          style="background:#fee2e2;color:#ef4444;border:1.5px solid #fecaca;"
        >
          <TrashIcon class="action-icon" />
          <span>{{ $t('button.delete') }}</span>
        </GenesisButton>
      </div>
    </div>

    <DeleteConfirmationPopup
      :visible="deletePopup"
      :message="`Êtes-vous sûr de vouloir supprimer la soumission #${contentSubmission?.id} ?`"
      subMessage="Cette action est irréversible."
      @confirm="confirmDelete"
      @cancel="closePopup"
    />
  </div>
</template>

<script setup lang="ts">
import { computed, type PropType } from 'vue'

import type { ContentSubmission } from '@/models/ContentSubmissionModel'
import GenesisButton from '@/components/button/GenesisButton.vue'
import { usePopup } from '@/composables/usePopup'
import { useContentSubmissions } from '@/composables/useContentSubmissions'
import DeleteConfirmationPopup from '@/components/popup/DeleteConfirmationPopup.vue'
import TrashIcon from '@/components/icons/TrashIcon.vue'
import EditIcon from '@/components/icons/EditIcon.vue'
import { useDateFormat } from '@/composables/useDateFormat'

const props = defineProps({
  contentSubmission: {
    type: Object as PropType<ContentSubmission>,
    required: true,
  },
})

const statusObj = computed(() => {
  return props.contentSubmission?.statusidSubmissionStatuses as any
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

const heroStatusClass = computed(() => {
  switch (statusName.value) {
    case 'SUCCESS': return 'hero-success'
    case 'ERROR': return 'hero-error'
    case 'PROCESSING': return 'hero-processing'
    default: return 'hero-pending'
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

// Stores
const { closePopup, openPopup, visible: deletePopup } = usePopup(false)
const { deleteContentSubmission, goToListView, goToUpdateFormView } = useContentSubmissions()

// Methods
const openDeletePopup = (entity: ContentSubmission) => {
  if (!entity) return
  openPopup()
}

const confirmDelete = async () => {
  const { success, error } = await deleteContentSubmission(props.contentSubmission)
  if (!success) console.error(error)
  closePopup()
  goToListView()
}

const { formatDate } = useDateFormat()
</script>

<style scoped>
.submission-details-wrapper {
  max-width: 880px;
  margin: 0 auto;
  padding: var(--mz-space-5);
}

/* ── Hero ── */
.submission-hero {
  display: flex;
  align-items: center;
  gap: var(--mz-space-5);
  padding: var(--mz-space-6);
  background: linear-gradient(135deg, rgba(139, 92, 246, 0.06) 0%, rgba(99, 102, 241, 0.08) 100%);
  border: 1px solid var(--mz-border-subtle-light);
  border-radius: var(--mz-radius-xl);
}

.submission-hero-icon {
  width: 72px;
  height: 72px;
  border-radius: var(--mz-radius-lg);
  color: #ffffff;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.hero-success {
  background: linear-gradient(135deg, #10b981 0%, #059669 100%);
  box-shadow: 0 4px 14px rgba(16, 185, 129, 0.3);
}

.hero-error {
  background: linear-gradient(135deg, #ef4444 0%, #dc2626 100%);
  box-shadow: 0 4px 14px rgba(239, 68, 68, 0.3);
}

.hero-processing {
  background: linear-gradient(135deg, #8b5cf6 0%, #6366f1 100%);
  box-shadow: 0 4px 14px rgba(139, 92, 246, 0.3);
}

.hero-pending {
  background: linear-gradient(135deg, #64748b 0%, #475569 100%);
  box-shadow: 0 4px 14px rgba(100, 116, 139, 0.3);
}

.submission-hero-meta {
  display: flex;
  flex-direction: column;
  gap: var(--mz-space-2);
  min-width: 0;
}

.submission-hero-kicker {
  font-size: 11px;
  font-weight: 700;
  text-transform: uppercase;
  letter-spacing: 0.08em;
  color: var(--mz-color-primary);
}

.submission-hero-title {
  font-size: var(--mz-font-size-2xl);
  font-weight: 800;
  color: var(--mz-text-on-light-high);
  margin: 0;
  letter-spacing: -0.02em;
  word-break: break-word;
}

.submission-hero-badges {
  display: flex;
  align-items: center;
  gap: var(--mz-space-2);
  flex-wrap: wrap;
}

/* ── Erreur Callout ── */
.error-callout-card {
  padding: var(--mz-space-4) var(--mz-space-5);
  background: #fef2f2;
  border: 1px solid #fecaca;
  border-radius: var(--mz-radius-lg);
  color: #991b1b;
}

.error-callout-header {
  display: flex;
  align-items: center;
  gap: var(--mz-space-2);
  font-size: var(--mz-font-size-sm);
  margin-bottom: 4px;
}

.error-callout-body {
  margin: 0;
  font-size: var(--mz-font-size-xs);
  line-height: 1.5;
}

/* ── Section & Grid ── */
.detail-section {
  padding: var(--mz-space-5);
  display: flex;
  flex-direction: column;
  gap: var(--mz-space-4);
}

.section-title {
  font-size: var(--mz-font-size-sm);
  font-weight: 700;
  text-transform: uppercase;
  letter-spacing: 0.06em;
  color: var(--mz-text-on-light-muted);
  margin: 0;
  padding-bottom: var(--mz-space-3);
  border-bottom: 1px solid var(--mz-border-subtle-light);
}

.detail-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(220px, 1fr));
  gap: var(--mz-space-4);
}

.detail-item {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.detail-label {
  font-size: 11px;
  font-weight: 600;
  text-transform: uppercase;
  letter-spacing: 0.05em;
  color: var(--mz-text-on-light-muted);
}

.detail-value {
  font-size: var(--mz-font-size-sm);
  font-weight: 600;
  color: var(--mz-text-on-light-high);
  word-break: break-word;
}

.filename-value {
  font-family: monospace;
  font-size: 13px;
  color: var(--mz-color-primary);
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

.detail-actions {
  display: flex;
  justify-content: flex-end;
  gap: var(--mz-space-3);
  margin-top: var(--mz-space-6);
  padding-top: var(--mz-space-4);
  border-top: 1px solid var(--mz-border-subtle-light);
}

.action-icon {
  width: 15px;
  height: 15px;
  margin-right: 6px;
}
</style>
