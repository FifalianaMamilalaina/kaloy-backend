<template>
  <div class="artist-details-wrapper">
    <div v-if="artist" class="space-y-6">
      <!-- Fiche profil unifiée regroupant l'identité de l'artiste -->
      <ProfileCard
        :name="artist.stageName ?? 'Artiste sans nom'"
        :username="artist.useridUsers?.email ? artist.useridUsers.email.split('@')[0] : ''"
        :photo-url="artist.photoUrl"
        :bio="artist.bio ?? 'Aucune biographie renseignée pour le moment.'"
        :role-tag="
          artist.artisttypeidArtistTypes?.getKeyValue?.() ??
          artist.artisttypeidArtistTypes?.name ??
          'Artiste'
        "
        :is-certified="Boolean(artist.isCertified)"
        :show-follow-btn="false"
      />

      <!-- Section Informations techniques & modération -->
      <div class="mz-card detail-section">
        <h3 class="section-title">Informations techniques & modération</h3>
        <div class="detail-grid">
          <div class="detail-item">
            <span class="detail-label">ID Système</span>
            <span class="detail-value">#{{ artist.id ? $n(artist.id, 'decimal') : '--' }}</span>
          </div>

          <div class="detail-item">
            <span class="detail-label">Utilisateur associé</span>
            <span class="detail-value">{{ artist.useridUsers?.getKeyValue?.() ?? '--' }}</span>
          </div>

          <div class="detail-item">
            <span class="detail-label">Type d'artiste</span>
            <span class="detail-value">{{
              artist.artisttypeidArtistTypes?.getKeyValue?.() ?? '--'
            }}</span>
          </div>

          <div class="detail-item">
            <span class="detail-label">Nom de scène</span>
            <span class="detail-value">{{ artist.stageName ?? '--' }}</span>
          </div>

          <div class="detail-item">
            <span class="detail-label">Actif depuis</span>
            <span class="detail-value">{{
              artist.activeSinceYear ? $n(artist.activeSinceYear, 'decimal') : '--'
            }}</span>
          </div>

          <div class="detail-item">
            <span class="detail-label">Statut de vérification</span>
            <span class="detail-value">
              <span
                v-if="artist.verificationstatusidVerificationStatuses"
                class="mz-badge mz-badge-accent"
              >
                {{
                  artist.verificationstatusidVerificationStatuses.getKeyValue?.() ??
                  artist.verificationstatusidVerificationStatuses
                }}
              </span>
              <span v-else class="text-muted">Non vérifié</span>
            </span>
          </div>

          <div class="detail-item" v-if="artist.verifiedAt">
            <span class="detail-label">Date de vérification</span>
            <span class="detail-value">{{ formatDate(artist.verifiedAt) }}</span>
          </div>

          <div class="detail-item">
            <span class="detail-label">Certification</span>
            <span class="detail-value">
              <span v-if="artist.isCertified" class="mz-badge mz-badge-success">✓ Certifié</span>
              <span v-else class="mz-badge" style="background: #f1f5f9; color: #64748b"
                >Non certifié</span
              >
            </span>
          </div>

          <div class="detail-item" v-if="artist.createdAt">
            <span class="detail-label">Date d'inscription</span>
            <span class="detail-value">{{ formatDate(artist.createdAt) }}</span>
          </div>
        </div>
      </div>

      <!-- Actions bar -->
      <div class="detail-actions">
        <GenesisButton @click="goToUpdateFormView(artist)" class="mz-btn mz-btn-secondary">
          <EditIcon class="action-icon" />
          <span>{{ $t('button.update') }}</span>
        </GenesisButton>

        <GenesisButton
          @click="openDeletePopup(artist)"
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
      :message="`Êtes-vous sûr de vouloir supprimer l'artiste ${artist?.stageName ?? artist?.id} ?`"
      subMessage="Cette action est irréversible."
      @confirm="confirmDelete"
      @cancel="closePopup"
    />
  </div>
</template>

<script setup lang="ts">
import type { PropType } from 'vue'

import type { Artist } from '@/models/ArtistModel'
import ProfileCard from '@/components/common/ProfileCard.vue'
import GenesisButton from '@/components/button/GenesisButton.vue'
import { usePopup } from '@/composables/usePopup'
import { useArtists } from '@/composables/useArtists'
import DeleteConfirmationPopup from '@/components/popup/DeleteConfirmationPopup.vue'
import TrashIcon from '@/components/icons/TrashIcon.vue'
import EditIcon from '@/components/icons/EditIcon.vue'
import { useDateFormat } from '@/composables/useDateFormat'

const props = defineProps({
  artist: {
    type: Object as PropType<Artist>,
    required: true,
  },
})

// Stores
const { closePopup, openPopup, visible: deletePopup } = usePopup(false)
const { deleteArtist, goToListView, goToUpdateFormView } = useArtists()

// Methods
const openDeletePopup = (entity: Artist) => {
  if (!entity) return
  openPopup()
}

const confirmDelete = async () => {
  const { success, error } = await deleteArtist(props.artist)
  if (!success) console.error(error)
  closePopup()
  goToListView()
}

const { formatDate } = useDateFormat()
</script>

<style scoped>
.artist-details-wrapper {
  max-width: 880px;
  margin: 0 auto;
  padding: var(--mz-space-5);
}

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

.text-muted {
  color: var(--mz-text-on-light-muted);
  font-weight: normal;
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
