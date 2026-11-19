<template>
  <div class="notif-details-wrapper">
    <div v-if="notification">
      <!-- Hero -->
      <div class="notif-hero" :class="notification.isRead ? 'hero-read' : 'hero-unread'">
        <div class="notif-hero-icon">
          <svg
            width="36"
            height="36"
            viewBox="0 0 24 24"
            fill="none"
            stroke="currentColor"
            stroke-width="1.5"
          >
            <path d="M18 8A6 6 0 0 0 6 8c0 7-3 9-3 9h18s-3-2-3-9" />
            <path d="M13.73 21a2 2 0 0 1-3.46 0" />
          </svg>
        </div>
        <div class="notif-hero-meta">
          <div class="notif-hero-top">
            <span
              class="mz-badge"
              :class="notification.isRead ? 'mz-badge-success' : 'mz-badge-warning'"
            >
              {{ notification.isRead ? '✓ Lu' : '● Non lu' }}
            </span>
            <span class="mz-badge mz-badge-glass" v-if="notification.typeidNotificationTypes">
              {{ notification.typeidNotificationTypes.getKeyValue() }}
            </span>
          </div>
          <p class="notif-hero-content">{{ notification.content ?? 'Aucun contenu' }}</p>
        </div>
      </div>

      <!-- Détails -->
      <div class="notif-sections">
        <div class="mz-card detail-section">
          <h3 class="section-title">Informations</h3>
          <div class="detail-grid">
            <div class="detail-item">
              <span class="detail-label">ID</span>
              <span class="detail-value">#{{ notification.id }}</span>
            </div>
            <div class="detail-item">
              <span class="detail-label">Destinataire</span>
              <span class="detail-value">{{
                notification.useridUsers?.getKeyValue() ?? '--'
              }}</span>
            </div>
            <div class="detail-item">
              <span class="detail-label">Type</span>
              <span class="detail-value">{{
                notification.typeidNotificationTypes?.getKeyValue() ?? '--'
              }}</span>
            </div>
            <div class="detail-item">
              <span class="detail-label">Cible</span>
              <span class="detail-value">{{
                notification.relatedentitytypeidInteractionTargets?.getKeyValue() ?? '--'
              }}</span>
            </div>
            <div class="detail-item">
              <span class="detail-label">ID entité liée</span>
              <span class="detail-value">{{ notification.relatedEntityId ?? '--' }}</span>
            </div>
            <div class="detail-item">
              <span class="detail-label">Statut</span>
              <span class="detail-value">
                <span
                  class="mz-badge"
                  :class="notification.isRead ? 'mz-badge-success' : 'mz-badge-warning'"
                >
                  {{ notification.isRead ? 'Lu' : 'Non lu' }}
                </span>
              </span>
            </div>
            <div class="detail-item">
              <span class="detail-label">Créé le</span>
              <span class="detail-value">{{ notification.createdAt ?? '--' }}</span>
            </div>
          </div>
        </div>

        <!-- Contenu -->
        <div class="mz-card detail-section" v-if="notification.content">
          <h3 class="section-title">Contenu du message</h3>
          <div class="content-block">
            <p class="content-text">{{ notification.content }}</p>
          </div>
        </div>
      </div>

      <!-- Actions -->
      <div class="notif-actions">
        <GenesisButton @click="goToUpdateFormView(notification)" class="mz-btn mz-btn-outline">
          <EditIcon class="action-icon" />
          <span>{{ $t('button.update') }}</span>
        </GenesisButton>
        <GenesisButton
          @click="openDeletePopup(notification)"
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
      message="Êtes-vous sûr de vouloir supprimer cette notification ?"
      subMessage="Cette action est irréversible."
      @confirm="confirmDelete"
      @cancel="closePopup"
    />
  </div>
</template>

<script setup lang="ts">
import type { PropType } from 'vue'
import type { Notification } from '@/models/NotificationModel'
import GenesisButton from '@/components/button/GenesisButton.vue'
import { usePopup } from '@/composables/usePopup'
import { useNotifications } from '@/composables/useNotifications'
import DeleteConfirmationPopup from '@/components/popup/DeleteConfirmationPopup.vue'
import TrashIcon from '@/components/icons/TrashIcon.vue'
import EditIcon from '@/components/icons/EditIcon.vue'
import { useDateFormat } from '@/composables/useDateFormat'

const props = defineProps({
  notification: { type: Object as PropType<Notification>, required: true },
})

const { closePopup, openPopup, visible: deletePopup } = usePopup(false)
const { deleteNotification, goToListView, goToUpdateFormView } = useNotifications()

const openDeletePopup = (entity: Notification) => {
  if (!entity) return
  openPopup()
}

const confirmDelete = async () => {
  const { success, error } = await deleteNotification(props.notification)
  if (!success) console.error(error)
  closePopup()
  goToListView()
}

const { formatDate } = useDateFormat()
</script>

<style scoped>
.notif-details-wrapper {
  display: flex;
  flex-direction: column;
  gap: var(--mz-space-5);
  max-width: 880px;
  margin: 0 auto;
  padding: var(--mz-space-5);
}

/* ── Hero ── */
.notif-hero {
  display: flex;
  align-items: flex-start;
  gap: var(--mz-space-4);
  padding: var(--mz-space-5);
  border-radius: var(--mz-radius-xl);
}

.hero-unread {
  background: linear-gradient(135deg, #78350f 0%, #b45309 100%);
}

.hero-read {
  background: linear-gradient(135deg, #064e3b 0%, #047857 100%);
}

.notif-hero-icon {
  width: 72px;
  height: 72px;
  border-radius: var(--mz-radius-lg);
  background: rgba(255, 255, 255, 0.15);
  border: 1.5px solid rgba(255, 255, 255, 0.25);
  color: #ffffff;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.notif-hero-meta {
  display: flex;
  flex-direction: column;
  gap: var(--mz-space-3);
  flex: 1;
}

.notif-hero-top {
  display: flex;
  gap: var(--mz-space-2);
  flex-wrap: wrap;
}

.notif-hero-content {
  font-size: var(--mz-font-size-md);
  color: rgba(255, 255, 255, 0.92);
  line-height: 1.6;
  margin: 0;
  font-weight: 500;
}

/* ── Sections ── */
.notif-sections {
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
  grid-template-columns: repeat(auto-fill, minmax(240px, 1fr));
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

/* Contenu -->  */
.content-block {
  background: var(--mz-surface-light-soft);
  border: 1px solid var(--mz-border-subtle-light);
  border-radius: var(--mz-radius-md);
  padding: var(--mz-space-4);
}

.content-text {
  font-size: var(--mz-font-size-base);
  color: var(--mz-text-on-light-medium);
  line-height: 1.7;
  margin: 0;
}

/* ── Actions ── */
.notif-actions {
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
