<template>
  <div class="album-details-wrapper">
    <div v-if="album">
      <!-- Hero -->
      <div class="album-hero">
        <!-- Cover image ou placeholder -->
        <div class="album-hero-cover">
          <img v-if="album.coverUrl" :src="album.coverUrl" alt="Cover" class="cover-img" />
          <div v-else class="cover-placeholder">
            <svg
              width="38"
              height="38"
              viewBox="0 0 24 24"
              fill="none"
              stroke="currentColor"
              stroke-width="1.5"
            >
              <circle cx="12" cy="12" r="10" />
              <circle cx="12" cy="12" r="3" />
            </svg>
          </div>
        </div>

        <div class="album-hero-meta">
          <h2 class="album-hero-title">{{ album.title ?? 'Album sans titre' }}</h2>
          <p class="album-hero-artist" v-if="album.artistidArtists">
            par <strong>{{ album.artistidArtists.getKeyValue() }}</strong>
          </p>
          <div class="album-hero-badges">
            <span
              class="mz-badge"
              style="background: #f1f5f9; color: #64748b; border: 1px solid #e2e8f0"
              v-if="album.releaseDate"
            >
              {{ $d(album.releaseDate, 'long') }}
            </span>
          </div>
        </div>
      </div>

      <!-- Détails -->
      <div class="album-sections">
        <div class="mz-card detail-section">
          <h3 class="section-title">Informations</h3>
          <div class="detail-grid">
            <div class="detail-item">
              <span class="detail-label">ID</span>
              <span class="detail-value">#{{ album.id }}</span>
            </div>
            <div class="detail-item">
              <span class="detail-label">Artiste</span>
              <span class="detail-value">{{ album.artistidArtists?.getKeyValue() ?? '--' }}</span>
            </div>
            <div class="detail-item">
              <span class="detail-label">Titre</span>
              <span class="detail-value">{{ album.title ?? '--' }}</span>
            </div>
            <div class="detail-item">
              <span class="detail-label">Date de sortie</span>
              <span class="detail-value">{{
                album.releaseDate ? $d(album.releaseDate, 'long') : '--'
              }}</span>
            </div>
            <div class="detail-item">
              <span class="detail-label">Cover URL</span>
              <span class="detail-value url-value">{{ album.coverUrl ?? '--' }}</span>
            </div>
            <div class="detail-item">
              <span class="detail-label">Créé le</span>
              <span class="detail-value">{{ album.createdAt ?? '--' }}</span>
            </div>
          </div>
        </div>
      </div>

      <!-- Actions -->
      <div class="album-actions">
        <GenesisButton @click="goToUpdateFormView(album)" class="mz-btn mz-btn-outline">
          <EditIcon class="action-icon" />
          <span>{{ $t('button.update') }}</span>
        </GenesisButton>
        <GenesisButton
          @click="openDeletePopup(album)"
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
      :message="`Êtes-vous sûr de vouloir supprimer cet album ?`"
      subMessage="Cette action est irréversible."
      @confirm="confirmDelete"
      @cancel="closePopup"
    />
  </div>
</template>

<script setup lang="ts">
import type { PropType } from 'vue'
import type { Album } from '@/models/AlbumModel'
import GenesisButton from '@/components/button/GenesisButton.vue'
import { usePopup } from '@/composables/usePopup'
import { useAlbums } from '@/composables/useAlbums'
import DeleteConfirmationPopup from '@/components/popup/DeleteConfirmationPopup.vue'
import TrashIcon from '@/components/icons/TrashIcon.vue'
import EditIcon from '@/components/icons/EditIcon.vue'
import { useDateFormat } from '@/composables/useDateFormat'

const props = defineProps({
  album: {
    type: Object as PropType<Album>,
    required: true,
  },
})

const { closePopup, openPopup, visible: deletePopup } = usePopup(false)
const { deleteAlbum, goToListView, goToUpdateFormView } = useAlbums()

const openDeletePopup = (entity: Album) => {
  if (!entity) return
  openPopup()
}

const confirmDelete = async () => {
  const { success, error } = await deleteAlbum(props.album)
  if (!success) console.error(error)
  closePopup()
  goToListView()
}

const { formatDate } = useDateFormat()
</script>

<style scoped>
.album-details-wrapper {
  display: flex;
  flex-direction: column;
  gap: var(--mz-space-5);
  max-width: 880px;
  margin: 0 auto;
  padding: var(--mz-space-5);
}

/* ── Hero ── */
.album-hero {
  display: flex;
  align-items: center;
  gap: var(--mz-space-5);
  padding: var(--mz-space-5);
  background: linear-gradient(135deg, #1b1340 0%, #372370 100%);
  border-radius: var(--mz-radius-xl);
  color: var(--mz-text-on-dark-high);
}

.album-hero-cover {
  width: 90px;
  height: 90px;
  border-radius: var(--mz-radius-lg);
  overflow: hidden;
  flex-shrink: 0;
  border: 2px solid rgba(255, 255, 255, 0.2);
}

.cover-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.cover-placeholder {
  width: 100%;
  height: 100%;
  background: rgba(255, 255, 255, 0.1);
  display: flex;
  align-items: center;
  justify-content: center;
  color: rgba(255, 255, 255, 0.5);
}

.album-hero-meta {
  display: flex;
  flex-direction: column;
  gap: var(--mz-space-2);
  min-width: 0;
}

.album-hero-title {
  font-size: var(--mz-font-size-2xl);
  font-weight: 800;
  color: var(--mz-text-on-dark-high);
  margin: 0;
  line-height: 1.2;
}

.album-hero-artist {
  font-size: var(--mz-font-size-sm);
  color: var(--mz-text-on-dark-medium);
  margin: 0;
}

.album-hero-badges {
  display: flex;
  flex-wrap: wrap;
  gap: var(--mz-space-2);
}

/* ── Sections ── */
.album-sections {
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

/* ── Actions ── */
.album-actions {
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
