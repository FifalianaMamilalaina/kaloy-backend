<template>
  <Teleport to="body">
    <Transition name="modal-fade">
      <div
        v-if="event"
        class="modal-overlay"
        role="dialog"
        aria-modal="true"
        :aria-label="`Détail : ${event.name}`"
        @click.self="$emit('close')"
      >
        <div class="modal-panel">
          <button class="modal-close-btn" @click="$emit('close')" aria-label="Fermer">
            <svg
              width="20"
              height="20"
              viewBox="0 0 24 24"
              fill="none"
              stroke="currentColor"
              stroke-width="2.5"
              stroke-linecap="round"
            >
              <line x1="18" y1="6" x2="6" y2="18" />
              <line x1="6" y1="6" x2="18" y2="18" />
            </svg>
          </button>

          <div class="modal-header">
            <span class="event-type-badge">
              <svg
                width="14"
                height="14"
                viewBox="0 0 24 24"
                fill="none"
                stroke="currentColor"
                stroke-width="2"
              >
                <rect x="3" y="4" width="18" height="18" rx="2" ry="2" />
                <line x1="16" y1="2" x2="16" y2="6" />
                <line x1="8" y1="2" x2="8" y2="6" />
                <line x1="3" y1="10" x2="21" y2="10" />
              </svg>
              Événement & Annonce
            </span>
            <h2 class="modal-title">{{ event.name }}</h2>
            <div class="modal-dates-row">
              <div class="date-chip">
                <svg
                  width="14"
                  height="14"
                  viewBox="0 0 24 24"
                  fill="none"
                  stroke="currentColor"
                  stroke-width="2"
                >
                  <circle cx="12" cy="12" r="10" />
                  <polyline points="12 6 12 12 16 14" />
                </svg>
                <span>{{ formatDate(event.startDate) }}</span>
                <template v-if="event.endDate && event.endDate !== event.startDate">
                  <span class="date-sep">→</span>
                  <span>{{ formatDate(event.endDate) }}</span>
                </template>
              </div>
              <span v-if="statusName" class="status-badge" :class="statusClass">{{
                statusName
              }}</span>
            </div>
          </div>

          <div class="modal-body">
            <!-- Organisateur / Admin -->
            <div v-if="event.createdbyartistidArtists" class="info-row">
              <div class="info-icon">
                <svg
                  width="18"
                  height="18"
                  viewBox="0 0 24 24"
                  fill="none"
                  stroke="currentColor"
                  stroke-width="2"
                >
                  <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2" />
                  <circle cx="12" cy="7" r="4" />
                </svg>
              </div>
              <div>
                <p class="info-label">Organisateur / Annonceur</p>
                <p class="info-value">
                  {{ event.createdbyartistidArtists.stageName ?? 'Administration' }}
                </p>
              </div>
            </div>

            <!-- Description -->
            <div v-if="event.description" class="info-block">
              <h3 class="block-heading">
                <svg
                  width="16"
                  height="16"
                  viewBox="0 0 24 24"
                  fill="none"
                  stroke="currentColor"
                  stroke-width="2"
                >
                  <line x1="17" y1="10" x2="3" y2="10" />
                  <line x1="21" y1="6" x2="3" y2="6" />
                  <line x1="21" y1="14" x2="3" y2="14" />
                  <line x1="17" y1="18" x2="3" y2="18" />
                </svg>
                Description & Détails
              </h3>
              <p class="description-text">{{ event.description }}</p>
            </div>

            <!-- Durée / Compte à rebours -->
            <div class="duration-block" v-if="durationDays !== null">
              <div class="duration-item">
                <span class="duration-number">{{ durationDays }}</span>
                <span class="duration-label">{{
                  durationDays === 1 ? 'jour d’événement' : 'jours d’événement'
                }}</span>
              </div>
              <div class="duration-sep"></div>
              <div class="duration-item">
                <span class="duration-number">{{ Math.max(0, daysUntil) }}</span>
                <span class="duration-label">{{
                  daysUntil <= 0
                    ? "Aujourd'hui / En cours"
                    : daysUntil === 1
                      ? 'jour restant'
                      : 'jours restants'
                }}</span>
              </div>
            </div>

            <!-- Programme / Concerts associés -->
            <div v-if="loadingConcerts" class="concerts-loading">
              <div class="spinner-sm"></div>
              <span>Chargement du programme...</span>
            </div>

            <div v-else-if="concerts.length > 0" class="info-block">
              <h3 class="block-heading">
                <svg
                  width="16"
                  height="16"
                  viewBox="0 0 24 24"
                  fill="none"
                  stroke="currentColor"
                  stroke-width="2"
                >
                  <path d="M9 18V5l12-2v13"></path>
                  <circle cx="6" cy="18" r="3"></circle>
                  <circle cx="18" cy="16" r="3"></circle>
                </svg>
                Programme & Représentations
              </h3>
              <div class="concerts-list">
                <div v-for="c in concerts" :key="c.id" class="concert-item">
                  <div class="concert-icon">🎵</div>
                  <div class="concert-details">
                    <h4 class="concert-title">{{ c.title }}</h4>
                    <p v-if="c.venueidVenues?.name" class="concert-venue">
                      📍 <strong>{{ c.venueidVenues.name }}</strong>
                      <span v-if="c.venueidVenues?.location">
                        ({{ c.venueidVenues.location }})</span
                      >
                    </p>
                    <p v-if="c.artistidArtists?.stageName" class="concert-artist">
                      Artiste invité : <strong>{{ c.artistidArtists.stageName }}</strong>
                    </p>
                    <p v-if="c.description" class="concert-desc">{{ c.description }}</p>
                  </div>
                </div>
              </div>
            </div>

            <!-- Date de publication -->
            <div v-if="event.createdAt" class="info-row minor">
              <div class="info-icon">
                <svg
                  width="14"
                  height="14"
                  viewBox="0 0 24 24"
                  fill="none"
                  stroke="currentColor"
                  stroke-width="2"
                >
                  <circle cx="12" cy="12" r="10" />
                  <polyline points="12 6 12 12 16 14" />
                </svg>
              </div>
              <p class="info-meta">Publié le {{ formatDateTime(event.createdAt) }}</p>
            </div>
          </div>

          <div class="modal-footer">
            <button class="btn-share" @click="copyLink">
              <svg
                width="16"
                height="16"
                viewBox="0 0 24 24"
                fill="none"
                stroke="currentColor"
                stroke-width="2"
              >
                <circle cx="18" cy="5" r="3" />
                <circle cx="6" cy="12" r="3" />
                <circle cx="18" cy="19" r="3" />
                <line x1="8.59" y1="13.51" x2="15.42" y2="17.49" />
                <line x1="15.41" y1="6.51" x2="8.59" y2="10.49" />
              </svg>
              {{ copied ? 'Lien copié !' : 'Partager' }}
            </button>
            <button class="btn-primary-cta" @click="$emit('close')">Fermer</button>
          </div>
        </div>
      </div>
    </Transition>
  </Teleport>
</template>

<script setup lang="ts">
import { ref, computed, watch } from 'vue'
import type { Event } from '@/models/EventModel'
import type { Concert } from '@/models/ConcertModel'
import * as ConcertService from '@/services/ConcertService'

const props = defineProps<{ event: Event | null }>()
defineEmits<{ close: [] }>()

const copied = ref(false)
const concerts = ref<Concert[]>([])
const loadingConcerts = ref(false)

watch(
  () => props.event?.id,
  async (newId) => {
    if (!newId) {
      concerts.value = []
      return
    }
    loadingConcerts.value = true
    try {
      const res = await ConcertService.getAllByEventId(newId)
      concerts.value = res.data ?? []
    } catch {
      concerts.value = []
    } finally {
      loadingConcerts.value = false
    }
  },
  { immediate: true },
)

function formatDate(dateStr?: string): string {
  if (!dateStr) return '—'
  const d = new Date(dateStr)
  return d.toLocaleDateString('fr-FR', { day: 'numeric', month: 'long', year: 'numeric' })
}

function formatDateTime(dateVal?: Date | string): string {
  if (!dateVal) return '—'
  const d = new Date(dateVal as string)
  return d.toLocaleDateString('fr-FR', { day: 'numeric', month: 'long', year: 'numeric' })
}

const statusName = computed(
  () => props.event?.moderationstatusidEventModerationStatuses?.name ?? null,
)

const statusClass = computed(() => {
  const s = statusName.value?.toLowerCase() ?? ''
  if (s.includes('approuv')) return 'approved'
  if (s.includes('attente')) return 'pending'
  if (s.includes('refus')) return 'rejected'
  return ''
})

const durationDays = computed(() => {
  if (!props.event?.startDate || !props.event?.endDate) return null
  const start = new Date(props.event.startDate)
  const end = new Date(props.event.endDate)
  return Math.max(1, Math.round((end.getTime() - start.getTime()) / 86400000) + 1)
})

const daysUntil = computed(() => {
  if (!props.event?.startDate) return 0
  const start = new Date(props.event.startDate)
  const today = new Date()
  today.setHours(0, 0, 0, 0)
  return Math.round((start.getTime() - today.getTime()) / 86400000)
})

function copyLink() {
  const url = window.location.origin + '/events/' + props.event?.id
  navigator.clipboard.writeText(url).catch(() => {})
  copied.value = true
  setTimeout(() => (copied.value = false), 2000)
}
</script>

<style scoped>
.modal-overlay {
  position: fixed;
  inset: 0;
  z-index: 9000;
  background: rgba(10, 6, 28, 0.72);
  backdrop-filter: blur(8px);
  -webkit-backdrop-filter: blur(8px);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 16px;
}
.modal-panel {
  position: relative;
  width: 100%;
  max-width: 600px;
  max-height: 90vh;
  overflow-y: auto;
  background: #ffffff;
  border-radius: 28px;
  box-shadow:
    0 32px 80px rgba(0, 0, 0, 0.4),
    0 0 0 1px rgba(255, 255, 255, 0.08);
  display: flex;
  flex-direction: column;
  scrollbar-width: thin;
  scrollbar-color: #f0e8d0 transparent;
}
.modal-panel::-webkit-scrollbar {
  width: 6px;
}
.modal-panel::-webkit-scrollbar-thumb {
  background: #f0e8d0;
  border-radius: 3px;
}
.modal-close-btn {
  position: absolute;
  top: 18px;
  right: 18px;
  z-index: 10;
  width: 36px;
  height: 36px;
  border-radius: 50%;
  border: none;
  background: rgba(255, 255, 255, 0.9);
  color: #334155;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.2s;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.12);
}
.modal-close-btn:hover {
  background: #fff;
  color: #e67300;
  transform: scale(1.08);
}
.modal-header {
  background: linear-gradient(135deg, #1b1340 0%, #3d2880 60%, #e67300 100%);
  padding: 36px 32px 28px;
  border-radius: 28px 28px 0 0;
  position: relative;
  overflow: hidden;
}
.event-type-badge {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  font-size: 12px;
  font-weight: 700;
  letter-spacing: 0.08em;
  text-transform: uppercase;
  color: rgba(255, 255, 255, 0.7);
  margin-bottom: 12px;
}
.modal-title {
  font-size: 26px;
  font-weight: 800;
  color: #ffffff;
  margin: 0 0 16px;
  line-height: 1.2;
  letter-spacing: -0.02em;
  text-shadow: 0 2px 12px rgba(0, 0, 0, 0.3);
  padding-right: 40px;
}
.modal-dates-row {
  display: flex;
  align-items: center;
  gap: 12px;
  flex-wrap: wrap;
}
.date-chip {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  font-size: 13.5px;
  font-weight: 600;
  color: rgba(255, 255, 255, 0.9);
  background: rgba(255, 255, 255, 0.15);
  border: 1px solid rgba(255, 255, 255, 0.2);
  padding: 6px 14px;
  border-radius: 999px;
}
.date-sep {
  color: rgba(255, 200, 120, 0.9);
  margin: 0 2px;
}
.status-badge {
  font-size: 12px;
  font-weight: 700;
  padding: 5px 14px;
  border-radius: 999px;
  letter-spacing: 0.04em;
}
.status-badge.approved {
  background: rgba(34, 197, 94, 0.25);
  color: #86efac;
  border: 1px solid rgba(34, 197, 94, 0.35);
}
.status-badge.pending {
  background: rgba(251, 191, 36, 0.25);
  color: #fde68a;
  border: 1px solid rgba(251, 191, 36, 0.35);
}
.status-badge.rejected {
  background: rgba(239, 68, 68, 0.25);
  color: #fca5a5;
  border: 1px solid rgba(239, 68, 68, 0.35);
}
.modal-body {
  padding: 28px 32px;
  display: flex;
  flex-direction: column;
  gap: 20px;
}
.info-row {
  display: flex;
  align-items: flex-start;
  gap: 14px;
}
.info-row.minor {
  opacity: 0.6;
}
.info-icon {
  width: 36px;
  height: 36px;
  flex-shrink: 0;
  border-radius: 10px;
  background: rgba(230, 115, 0, 0.08);
  display: flex;
  align-items: center;
  justify-content: center;
  color: #e67300;
}
.info-label {
  font-size: 11px;
  font-weight: 700;
  text-transform: uppercase;
  letter-spacing: 0.08em;
  color: #94a3b8;
  margin: 0 0 2px;
}
.info-value {
  font-size: 15px;
  font-weight: 600;
  color: #1e293b;
  margin: 0;
}
.info-meta {
  font-size: 13px;
  color: #64748b;
  margin: 0;
  line-height: 36px;
}
.info-block {
  background: #f8f9fb;
  border-radius: 16px;
  padding: 20px 22px;
  border: 1px solid #f1f5f9;
}
.block-heading {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 13px;
  font-weight: 700;
  text-transform: uppercase;
  letter-spacing: 0.08em;
  color: #64748b;
  margin: 0 0 12px;
}
.description-text {
  font-size: 15px;
  line-height: 1.7;
  color: #334155;
  margin: 0;
  white-space: pre-line;
}
.duration-block {
  display: flex;
  align-items: center;
  gap: 0;
  background: linear-gradient(135deg, rgba(27, 19, 64, 0.05) 0%, rgba(230, 115, 0, 0.08) 100%);
  border-radius: 16px;
  padding: 20px 28px;
  border: 1px solid rgba(230, 115, 0, 0.12);
}
.duration-item {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
}
.duration-number {
  font-size: 36px;
  font-weight: 900;
  color: #1b1340;
  line-height: 1;
  letter-spacing: -0.03em;
}
.duration-label {
  font-size: 13px;
  font-weight: 600;
  color: #94a3b8;
  margin-top: 4px;
  text-align: center;
}
.duration-sep {
  width: 1px;
  height: 48px;
  background: rgba(0, 0, 0, 0.1);
  margin: 0 20px;
}

.concerts-loading {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 14px;
  color: #94a3b8;
  padding: 12px 0;
}
.spinner-sm {
  width: 16px;
  height: 16px;
  border: 2px solid #e2e8f0;
  border-top-color: #e67300;
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
}
@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

.concerts-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}
.concert-item {
  display: flex;
  align-items: flex-start;
  gap: 12px;
  background: #ffffff;
  border-radius: 12px;
  padding: 14px 16px;
  border: 1px solid #e2e8f0;
}
.concert-icon {
  font-size: 20px;
  line-height: 1;
  margin-top: 2px;
}
.concert-details {
  flex: 1;
}
.concert-title {
  font-size: 15px;
  font-weight: 700;
  color: #1e293b;
  margin: 0 0 4px;
}
.concert-venue {
  font-size: 13px;
  color: #475569;
  margin: 0 0 2px;
}
.concert-artist {
  font-size: 13px;
  color: #e67300;
  margin: 0 0 4px;
}
.concert-desc {
  font-size: 13px;
  color: #64748b;
  margin: 0;
  line-height: 1.4;
}

.modal-footer {
  padding: 20px 32px 28px;
  display: flex;
  align-items: center;
  gap: 12px;
  border-top: 1px solid #f1f5f9;
  margin-top: auto;
}
.btn-share {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 11px 20px;
  font-size: 14px;
  font-weight: 700;
  border-radius: 999px;
  border: 1.5px solid #e2e8f0;
  background: #ffffff;
  color: #475569;
  cursor: pointer;
  transition: all 0.2s;
}
.btn-share:hover {
  border-color: #e67300;
  color: #e67300;
}
.btn-primary-cta {
  flex: 1;
  padding: 12px 20px;
  font-size: 15px;
  font-weight: 800;
  border-radius: 999px;
  border: none;
  background: linear-gradient(135deg, #1b1340 0%, #e67300 100%);
  color: #ffffff;
  cursor: pointer;
  transition: all 0.2s;
  box-shadow: 0 4px 14px rgba(230, 115, 0, 0.3);
}
.btn-primary-cta:hover {
  transform: translateY(-1px);
  box-shadow: 0 6px 20px rgba(230, 115, 0, 0.4);
}
.modal-fade-enter-active,
.modal-fade-leave-active {
  transition: opacity 0.25s ease;
}
.modal-fade-enter-active .modal-panel,
.modal-fade-leave-active .modal-panel {
  transition:
    transform 0.25s cubic-bezier(0.34, 1.56, 0.64, 1),
    opacity 0.2s ease;
}
.modal-fade-enter-from,
.modal-fade-leave-to {
  opacity: 0;
}
.modal-fade-enter-from .modal-panel {
  transform: scale(0.9) translateY(20px);
  opacity: 0;
}
.modal-fade-leave-to .modal-panel {
  transform: scale(0.95) translateY(10px);
  opacity: 0;
}
</style>
