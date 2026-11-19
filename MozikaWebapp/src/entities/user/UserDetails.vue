<template>
  <div class="user-details-wrapper">
    <div v-if="user" class="space-y-6">
      <!-- ProfileCard pour l'utilisateur -->
      <ProfileCard
        :name="userDisplayName"
        :username="username"
        :role-tag="
          user.roleidUserRoles?.getKeyValue?.() ?? user.roleidUserRoles?.name ?? 'Utilisateur'
        "
        :bio="user.phone ? `Téléphone : ${user.phone}` : 'Aucune information supplémentaire.'"
        :is-certified="Boolean(user.emailVerifiedAt)"
        :show-follow-btn="false"
      />

      <!-- Informations système & sécurité -->
      <div class="mz-card detail-section">
        <h3 class="section-title">Compte, Statut & Sécurité</h3>
        <div class="detail-grid">
          <div class="detail-item">
            <span class="detail-label">ID Utilisateur</span>
            <span class="detail-value">#{{ user.id ? $n(user.id, 'decimal') : '--' }}</span>
          </div>

          <div class="detail-item">
            <span class="detail-label">Adresse email</span>
            <span class="detail-value">{{ user.email ?? '--' }}</span>
          </div>

          <div class="detail-item">
            <span class="detail-label">Numéro de téléphone</span>
            <span class="detail-value">{{ user.phone ?? '--' }}</span>
          </div>

          <div class="detail-item">
            <span class="detail-label">Rôle du compte</span>
            <span class="detail-value">
              <span v-if="user.roleidUserRoles" class="mz-badge mz-badge-accent">
                {{ user.roleidUserRoles.getKeyValue?.() ?? user.roleidUserRoles }}
              </span>
              <span v-else>--</span>
            </span>
          </div>

          <div class="detail-item">
            <span class="detail-label">Statut du compte</span>
            <span class="detail-value">
              <span v-if="user.statusidUserStatuses" class="mz-badge mz-badge-success">
                {{ user.statusidUserStatuses.getKeyValue?.() ?? user.statusidUserStatuses }}
              </span>
              <span v-else>--</span>
            </span>
          </div>

          <div class="detail-item">
            <span class="detail-label">Vérification Email</span>
            <span class="detail-value">
              <span v-if="user.emailVerifiedAt" class="mz-badge mz-badge-primary">
                Vérifié le {{ formatDate(user.emailVerifiedAt) }}
              </span>
              <span v-else class="text-muted">Non vérifié</span>
            </span>
          </div>

          <div class="detail-item">
            <span class="detail-label">Vérification Téléphone</span>
            <span class="detail-value">
              <span v-if="user.phoneVerifiedAt" class="mz-badge mz-badge-primary">
                Vérifié le {{ formatDate(user.phoneVerifiedAt) }}
              </span>
              <span v-else class="text-muted">Non vérifié</span>
            </span>
          </div>

          <div class="detail-item" v-if="user.createdAt">
            <span class="detail-label">Date d'inscription</span>
            <span class="detail-value">{{ formatDate(user.createdAt) }}</span>
          </div>

          <div class="detail-item" v-if="user.updatedAt">
            <span class="detail-label">Dernière mise à jour</span>
            <span class="detail-value">{{ formatDate(user.updatedAt) }}</span>
          </div>
        </div>
      </div>

      <!-- Actions -->
      <div class="detail-actions">
        <GenesisButton @click="goToUpdateFormView(user)" class="mz-btn mz-btn-secondary">
          <EditIcon class="action-icon" />
          <span>{{ $t('button.update') }}</span>
        </GenesisButton>

        <GenesisButton
          @click="openDeletePopup(user)"
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
      :message="`Êtes-vous sûr de vouloir supprimer l'utilisateur ${user?.email ?? user?.id} ?`"
      subMessage="Cette action est irréversible."
      @confirm="confirmDelete"
      @cancel="closePopup"
    />
  </div>
</template>

<script setup lang="ts">
import { computed, type PropType } from 'vue'

import type { User } from '@/models/UserModel'
import ProfileCard from '@/components/common/ProfileCard.vue'
import GenesisButton from '@/components/button/GenesisButton.vue'
import { usePopup } from '@/composables/usePopup'
import { useUsers } from '@/composables/useUsers'
import DeleteConfirmationPopup from '@/components/popup/DeleteConfirmationPopup.vue'
import TrashIcon from '@/components/icons/TrashIcon.vue'
import EditIcon from '@/components/icons/EditIcon.vue'
import { useDateFormat } from '@/composables/useDateFormat'

const props = defineProps({
  user: {
    type: Object as PropType<User>,
    required: true,
  },
})

const username = computed(() => {
  if (!props.user?.email) return `user_${props.user?.id ?? ''}`
  return props.user.email.split('@')[0]
})

const userDisplayName = computed(() => {
  return username.value
})

// Stores
const { closePopup, openPopup, visible: deletePopup } = usePopup(false)
const { deleteUser, goToListView, goToUpdateFormView } = useUsers()

// Methods
const openDeletePopup = (entity: User) => {
  if (!entity) return
  openPopup()
}

const confirmDelete = async () => {
  const { success, error } = await deleteUser(props.user)
  if (!success) console.error(error)
  closePopup()
  goToListView()
}

const { formatDate } = useDateFormat()
</script>

<style scoped>
.user-details-wrapper {
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
