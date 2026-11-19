<template>
  <div class="song-details-wrapper">
    <div v-if="song">
      <!-- Hero : Cover + infos principales -->
      <div class="song-hero">
        <div class="song-hero-icon">
          <svg
            width="38"
            height="38"
            viewBox="0 0 24 24"
            fill="none"
            stroke="currentColor"
            stroke-width="1.5"
          >
            <path d="M9 18V5l12-2v13" />
            <circle cx="6" cy="18" r="3" />
            <circle cx="18" cy="16" r="3" />
          </svg>
        </div>
        <div class="song-hero-meta">
          <h2 class="song-hero-title">{{ song.title ?? 'Sans titre' }}</h2>
          <div class="song-hero-badges">
            <span class="mz-badge mz-badge-primary" v-if="song.language">
              {{ song.language }}
            </span>
            <span class="mz-badge mz-badge-success" v-if="song.isDownloadable">
              Téléchargeable
            </span>
            <span
              class="mz-badge"
              style="background: #f1f5f9; color: #64748b; border: 1px solid #e2e8f0"
              v-if="song.durationSeconds"
            >
              {{ formatDuration(song.durationSeconds) }}
            </span>
          </div>
        </div>
      </div>

      <!-- Sections de détail -->
      <div class="song-sections">
        <!-- Identité -->
        <div class="mz-card detail-section">
          <h3 class="section-title">Identité</h3>
          <div class="detail-grid">
            <div class="detail-item">
              <span class="detail-label">ID</span>
              <span class="detail-value">#{{ song.id }}</span>
            </div>
            <div class="detail-item">
              <span class="detail-label">Artiste</span>
              <span class="detail-value">{{ song.artistidArtists?.getKeyValue() ?? '--' }}</span>
            </div>
            <div class="detail-item">
              <span class="detail-label">Album</span>
              <span class="detail-value">{{ song.albumidAlbums?.getKeyValue() ?? '--' }}</span>
            </div>
            <div class="detail-item">
              <span class="detail-label">Date de sortie</span>
              <span class="detail-value">{{
                song.releaseDate ? $d(song.releaseDate, 'long') : '--'
              }}</span>
            </div>
            <div class="detail-item">
              <span class="detail-label">Langue</span>
              <span class="detail-value">{{ song.language ?? '--' }}</span>
            </div>
            <div class="detail-item">
              <span class="detail-label">Durée</span>
              <span class="detail-value">{{
                song.durationSeconds ? formatDuration(song.durationSeconds) : '--'
              }}</span>
            </div>
          </div>
        </div>

        <!-- Crédits -->
        <div class="mz-card detail-section">
          <h3 class="section-title">Crédits</h3>
          <div class="detail-grid">
            <div class="detail-item">
              <span class="detail-label">Auteur / Compositeur</span>
              <span class="detail-value">{{ song.authorComposer ?? '--' }}</span>
            </div>
            <div class="detail-item">
              <span class="detail-label">Arrangeur musical</span>
              <span class="detail-value">{{ song.musicalArranger ?? '--' }}</span>
            </div>
            <div class="detail-item">
              <span class="detail-label">Lieu d'enregistrement</span>
              <span class="detail-value">{{ song.recordingLocation ?? '--' }}</span>
            </div>
            <div class="detail-item">
              <span class="detail-label">Date d'enregistrement</span>
              <span class="detail-value">{{
                song.recordingDate ? $d(song.recordingDate, 'long') : '--'
              }}</span>
            </div>
          </div>
        </div>

        <!-- Contenu audio -->
        <div class="mz-card detail-section">
          <h3 class="section-title">Contenu Audio & Écoute</h3>

          <!-- Lecteur audio intégré -->
          <div v-if="song.audioUrl" class="player-box" style="margin-bottom: 16px; padding: 12px; background: rgba(255,255,255,0.04); border-radius: 8px;">
            <span class="detail-label" style="font-weight: 600; color: var(--mz-primary, #ff7b00); margin-bottom: 8px; display: block;">
              ▶ Écouter le titre
            </span>
            <audio :src="normalizeMediaUrl(song.audioUrl)" controls style="width: 100%; border-radius: 8px;"></audio>
          </div>

          <!-- Lecteur vidéo intégré -->
          <div v-if="song.videoUrl" class="player-box" style="margin-bottom: 16px; padding: 12px; background: rgba(255,255,255,0.04); border-radius: 8px;">
            <span class="detail-label" style="font-weight: 600; color: var(--mz-primary, #ff7b00); margin-bottom: 8px; display: block;">
              🎬 Clip vidéo
            </span>
            <video :src="normalizeMediaUrl(song.videoUrl)" controls style="width: 100%; max-height: 320px; border-radius: 8px; background: #000;"></video>
          </div>

          <div class="detail-grid">
            <div class="detail-item">
              <span class="detail-label">Type stockage</span>
              <span class="detail-value">{{
                song.storagetypeidAudioStorageTypes?.getKeyValue() ?? '--'
              }}</span>
            </div>
            <div class="detail-item">
              <span class="detail-label">URL Audio</span>
              <span class="detail-value url-value">{{ song.audioUrl ?? '--' }}</span>
            </div>
            <div class="detail-item">
              <span class="detail-label">URL Vidéo</span>
              <span class="detail-value url-value">{{ song.videoUrl ?? '--' }}</span>
            </div>
            <div class="detail-item">
              <span class="detail-label">Téléchargeable</span>
              <span class="detail-value">
                <span
                  class="mz-badge"
                  :class="song.isDownloadable ? 'mz-badge-success' : 'mz-badge-danger'"
                >
                  {{ song.isDownloadable ? 'Oui' : 'Non' }}
                </span>
              </span>
            </div>
          </div>
        </div>

        <!-- Paroles -->
        <div class="mz-card detail-section" v-if="song.lyrics">
          <h3 class="section-title">Paroles</h3>
          <div class="lyrics-block">
            <pre class="lyrics-text">{{ song.lyrics }}</pre>
          </div>
        </div>
      </div>

      <!-- Actions -->
      <div class="song-actions">
        <GenesisButton @click="goToUpdateFormView(song)" class="mz-btn mz-btn-outline">
          <EditIcon class="action-icon" />
          <span>{{ $t('button.update') }}</span>
        </GenesisButton>
        <GenesisButton
          @click="openDeletePopup(song)"
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
      :message="`Êtes-vous sûr de vouloir supprimer cette chanson ?`"
      subMessage="Cette action est irréversible."
      @confirm="confirmDelete"
      @cancel="closePopup"
    />
  </div>
</template>

<script setup lang="ts">
import type { PropType } from 'vue'
import { getUrl } from '@/composables/useImage'
import type { Song } from '@/models/SongModel'
import GenesisButton from '@/components/button/GenesisButton.vue'
import { usePopup } from '@/composables/usePopup'
import { useSongs } from '@/composables/useSongs'
import DeleteConfirmationPopup from '@/components/popup/DeleteConfirmationPopup.vue'
import TrashIcon from '@/components/icons/TrashIcon.vue'
import EditIcon from '@/components/icons/EditIcon.vue'
import { useDateFormat } from '@/composables/useDateFormat'

const props = defineProps({
  song: {
    type: Object as PropType<Song>,
    required: true,
  },
})

const { closePopup, openPopup, visible: deletePopup } = usePopup(false)
const { deleteSong, goToListView, goToUpdateFormView } = useSongs()

const openDeletePopup = (entity: Song) => {
  if (!entity) return
  openPopup()
}

const confirmDelete = async () => {
  const { success, error } = await deleteSong(props.song)
  if (!success) console.error(error)
  closePopup()
  goToListView()
}

const { formatDate } = useDateFormat()

function formatDuration(seconds: number): string {
  const m = Math.floor(seconds / 60)
  const s = seconds % 60
  return `${m}:${String(s).padStart(2, '0')}`
}

function normalizeMediaUrl(url?: string): string {
  if (!url) return ''
  return url.replace('10.0.2.2', window.location.hostname || 'localhost')
}
</script>

<style scoped>
.song-details-wrapper {
  display: flex;
  flex-direction: column;
  gap: var(--mz-space-5);
  max-width: 880px;
  margin: 0 auto;
  padding: var(--mz-space-5);
}

/* ── Hero ── */
.song-hero {
  display: flex;
  align-items: center;
  gap: var(--mz-space-5);
  padding: var(--mz-space-5);
  background: linear-gradient(135deg, #0e0922 0%, #211747 100%);
  border-radius: var(--mz-radius-xl);
  color: var(--mz-text-on-dark-high);
}

.song-hero-icon {
  width: 80px;
  height: 80px;
  border-radius: var(--mz-radius-lg);
  background: var(--mz-brand-accent-light);
  border: 1.5px solid var(--mz-border-accent);
  color: var(--mz-brand-accent);
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.song-hero-meta {
  display: flex;
  flex-direction: column;
  gap: var(--mz-space-3);
  min-width: 0;
}

.song-hero-title {
  font-size: var(--mz-font-size-2xl);
  font-weight: 800;
  color: var(--mz-text-on-dark-high);
  margin: 0;
  line-height: 1.2;
}

.song-hero-badges {
  display: flex;
  flex-wrap: wrap;
  gap: var(--mz-space-2);
}

/* ── Sections ── */
.song-sections {
  display: flex;
  flex-direction: column;
  gap: var(--mz-space-4);
}

.detail-section {
  padding: var(--mz-space-5);
}

.section-title {
  font-size: var(--mz-font-size-sm);
  font-weight: 800;
  text-transform: uppercase;
  letter-spacing: 0.06em;
  color: var(--mz-text-on-light-muted);
  margin: 0 0 var(--mz-space-4) 0;
  padding-bottom: var(--mz-space-3);
  border-bottom: 1px solid var(--mz-border-subtle-light);
}

.detail-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(260px, 1fr));
  gap: var(--mz-space-3);
}

.detail-item {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.detail-label {
  font-size: var(--mz-font-size-xs);
  font-weight: 700;
  text-transform: uppercase;
  letter-spacing: 0.04em;
  color: var(--mz-text-on-light-muted);
}

.detail-value {
  font-size: var(--mz-font-size-base);
  color: var(--mz-text-on-light-high);
  font-weight: 500;
}

.url-value {
  font-size: var(--mz-font-size-xs);
  word-break: break-all;
  color: var(--mz-color-info);
}

/* ── Paroles ── */
.lyrics-block {
  background: var(--mz-surface-light-soft);
  border: 1px solid var(--mz-border-subtle-light);
  border-radius: var(--mz-radius-md);
  padding: var(--mz-space-4);
}

.lyrics-text {
  font-family: var(--mz-font-family);
  font-size: var(--mz-font-size-sm);
  color: var(--mz-text-on-light-medium);
  white-space: pre-wrap;
  line-height: 1.75;
  margin: 0;
}

/* ── Actions ── */
.song-actions {
  display: flex;
  justify-content: flex-end;
  gap: var(--mz-space-3);
  padding-top: var(--mz-space-4);
  border-top: 1px solid var(--mz-border-subtle-light);
}

.action-icon {
  width: 16px;
  height: 16px;
}
</style>
