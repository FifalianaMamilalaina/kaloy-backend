<template>
  <div class="playlist-details-wrapper">
    <div v-if="playlist" class="space-y-6">
      <!-- Hero de la playlist -->
      <div class="playlist-hero">
        <div class="playlist-hero-icon">
          <svg
            width="40"
            height="40"
            viewBox="0 0 24 24"
            fill="none"
            stroke="currentColor"
            stroke-width="2"
            stroke-linecap="round"
            stroke-linejoin="round"
          >
            <line x1="8" y1="6" x2="21" y2="6" />
            <line x1="8" y1="12" x2="21" y2="12" />
            <line x1="8" y1="18" x2="16" y2="18" />
            <line x1="3" y1="6" x2="3.01" y2="6" />
            <line x1="3" y1="12" x2="3.01" y2="12" />
            <line x1="3" y1="18" x2="3.01" y2="18" />
          </svg>
        </div>
        <div class="playlist-hero-meta">
          <span class="playlist-hero-kicker">Playlist Mozika</span>
          <h2 class="playlist-hero-title">{{ playlist.name ?? 'Playlist sans titre' }}</h2>
          <div class="playlist-hero-badges">
            <span v-if="playlist.visibilityidPlaylistVisibilities" class="mz-badge mz-badge-accent">
              {{
                playlist.visibilityidPlaylistVisibilities.getKeyValue?.() ??
                playlist.visibilityidPlaylistVisibilities
              }}
            </span>
            <span class="mz-badge" style="background: #f1f5f9; color: #64748b" v-if="playlist.id">
              ID #{{ playlist.id }}
            </span>
          </div>
        </div>
      </div>

      <!-- Informations de la Playlist -->
      <div class="mz-card detail-section">
        <h3 class="section-title">Informations de la Playlist</h3>
        <div class="detail-grid">
          <div class="detail-item">
            <span class="detail-label">Nom de la playlist</span>
            <span class="detail-value">{{ playlist.name ?? '--' }}</span>
          </div>

          <div class="detail-item">
            <span class="detail-label">Créateur / Propriétaire</span>
            <span class="detail-value">{{
              playlist.owneruseridUsers?.getKeyValue?.() ?? '--'
            }}</span>
          </div>

          <div class="detail-item">
            <span class="detail-label">Visibilité</span>
            <span class="detail-value">
              <span
                v-if="playlist.visibilityidPlaylistVisibilities"
                class="mz-badge mz-badge-primary"
              >
                {{
                  playlist.visibilityidPlaylistVisibilities?.getKeyValue?.() ??
                  playlist.visibilityidPlaylistVisibilities
                }}
              </span>
              <span v-else>--</span>
            </span>
          </div>

          <div class="detail-item" v-if="playlist.shareToken">
            <span class="detail-label">Jeton de partage</span>
            <span class="detail-value token-value">{{ playlist.shareToken }}</span>
          </div>

          <div class="detail-item" v-if="playlist.createdAt">
            <span class="detail-label">Créée le</span>
            <span class="detail-value">{{ formatDate(playlist.createdAt) }}</span>
          </div>

          <div class="detail-item" v-if="playlist.updatedAt">
            <span class="detail-label">Dernière modification</span>
            <span class="detail-value">{{ formatDate(playlist.updatedAt) }}</span>
          </div>
        </div>
      </div>

      <!-- Actions -->
      <div class="detail-actions">
        <GenesisButton @click="goToUpdateFormView(playlist)" class="mz-btn mz-btn-secondary">
          <EditIcon class="action-icon" />
          <span>{{ $t('button.update') }}</span>
        </GenesisButton>

        <GenesisButton
          @click="openDeletePopup(playlist)"
          class="mz-btn"
          style="background: #fee2e2; color: #ef4444; border: 1.5px solid #fecaca"
        >
          <TrashIcon class="action-icon" />
          <span>{{ $t('button.delete') }}</span>
        </GenesisButton>
      </div>
    </div>

    <DeleteConfirmationPopup
      :visible="deletePopup"
      :message="`Êtes-vous sûr de vouloir supprimer la playlist ${playlist?.name ?? playlist?.id} ?`"
      subMessage="Cette action est irréversible."
      @confirm="confirmDelete"
      @cancel="closePopup"
    />
  </div>
</template>

<script setup lang="ts">
import type { PropType } from 'vue'

import type { Playlist } from '@/models/PlaylistModel'
import GenesisButton from '@/components/button/GenesisButton.vue'
import { usePopup } from '@/composables/usePopup'
import { usePlaylists } from '@/composables/usePlaylists'
import DeleteConfirmationPopup from '@/components/popup/DeleteConfirmationPopup.vue'
import TrashIcon from '@/components/icons/TrashIcon.vue'
import EditIcon from '@/components/icons/EditIcon.vue'
import { useDateFormat } from '@/composables/useDateFormat'

const props = defineProps({
  playlist: {
    type: Object as PropType<Playlist>,
    required: true,
  },
})

// Stores
const { closePopup, openPopup, visible: deletePopup } = usePopup(false)
const { deletePlaylist, goToListView, goToUpdateFormView } = usePlaylists()

// Methods
const openDeletePopup = (entity: Playlist) => {
  if (!entity) return
  openPopup()
}

const confirmDelete = async () => {
  const { success, error } = await deletePlaylist(props.playlist)
  if (!success) console.error(error)
  closePopup()
  goToListView()
}

const { formatDate } = useDateFormat()
</script>

<style scoped>
.playlist-details-wrapper {
  max-width: 880px;
  margin: 0 auto;
  padding: var(--mz-space-5);
}

/* ── Hero ── */
.playlist-hero {
  display: flex;
  align-items: center;
  gap: var(--mz-space-5);
  padding: var(--mz-space-6);
  background: linear-gradient(135deg, rgba(16, 185, 129, 0.08) 0%, rgba(5, 150, 105, 0.12) 100%);
  border: 1px solid rgba(16, 185, 129, 0.2);
  border-radius: var(--mz-radius-xl);
}

.playlist-hero-icon {
  width: 72px;
  height: 72px;
  border-radius: var(--mz-radius-lg);
  background: linear-gradient(135deg, #10b981 0%, #059669 100%);
  color: #ffffff;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  box-shadow: 0 4px 14px rgba(16, 185, 129, 0.3);
}

.playlist-hero-meta {
  display: flex;
  flex-direction: column;
  gap: var(--mz-space-2);
  min-width: 0;
}

.playlist-hero-kicker {
  font-size: 11px;
  font-weight: 700;
  text-transform: uppercase;
  letter-spacing: 0.08em;
  color: #059669;
}

.playlist-hero-title {
  font-size: var(--mz-font-size-2xl);
  font-weight: 800;
  color: var(--mz-text-on-light-high);
  margin: 0;
  letter-spacing: -0.02em;
}

.playlist-hero-badges {
  display: flex;
  align-items: center;
  gap: var(--mz-space-2);
  flex-wrap: wrap;
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

.token-value {
  font-family: monospace;
  font-size: 12px;
  background: #f8fafc;
  padding: 2px 6px;
  border-radius: 4px;
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
