<template>
  <div class="comment-details-wrapper">
    <div v-if="comment">
      <!-- Hero -->
      <div class="comment-hero">
        <!-- Avatar -->
        <div class="comment-hero-avatar mz-avatar mz-avatar-xl">
          {{ authorInitials }}
        </div>
        <div class="comment-hero-meta">
          <h2 class="comment-hero-author">{{ authorName }}</h2>
          <p class="comment-hero-sub">Commentaire #{{ comment.id }}</p>
          <div class="comment-hero-badges">
            <span
              class="mz-badge"
              :class="comment.isHidden ? 'mz-badge-danger' : 'mz-badge-success'"
            >
              {{ comment.isHidden ? '🚫 Masqué' : '✓ Visible' }}
            </span>
            <span class="mz-badge mz-badge-glass" v-if="comment.targettypeidInteractionTargets">
              {{ comment.targettypeidInteractionTargets.getKeyValue() }}
            </span>
          </div>
        </div>
      </div>

      <!-- Contenu principal du commentaire -->
      <div class="mz-card comment-content-card" v-if="comment.content">
        <div class="comment-quote-bar"></div>
        <div class="comment-content-body">
          <span class="section-eyebrow">Contenu du commentaire</span>
          <p class="comment-content-text">{{ comment.content }}</p>
        </div>
      </div>

      <!-- Infos détaillées -->
      <div class="comment-sections">
        <div class="mz-card detail-section">
          <h3 class="section-title">Informations</h3>
          <div class="detail-grid">
            <div class="detail-item">
              <span class="detail-label">ID</span>
              <span class="detail-value">#{{ comment.id }}</span>
            </div>
            <div class="detail-item">
              <span class="detail-label">Auteur</span>
              <span class="detail-value">{{
                comment.authoruseridUsers?.getKeyValue() ?? '--'
              }}</span>
            </div>
            <div class="detail-item">
              <span class="detail-label">Cible</span>
              <span class="detail-value">{{
                comment.targettypeidInteractionTargets?.getKeyValue() ?? '--'
              }}</span>
            </div>
            <div class="detail-item">
              <span class="detail-label">ID cible</span>
              <span class="detail-value">{{ comment.targetId ?? '--' }}</span>
            </div>
            <div class="detail-item">
              <span class="detail-label">Statut</span>
              <span class="detail-value">
                <span
                  class="mz-badge"
                  :class="comment.isHidden ? 'mz-badge-danger' : 'mz-badge-success'"
                >
                  {{ comment.isHidden ? 'Masqué' : 'Visible' }}
                </span>
              </span>
            </div>
            <div class="detail-item">
              <span class="detail-label">Créé le</span>
              <span class="detail-value">{{ comment.createdAt ?? '--' }}</span>
            </div>
          </div>
        </div>
      </div>

      <!-- Actions -->
      <div class="comment-actions">
        <GenesisButton @click="goToUpdateFormView(comment)" class="mz-btn mz-btn-outline">
          <EditIcon class="action-icon" />
          <span>{{ $t('button.update') }}</span>
        </GenesisButton>
        <GenesisButton
          @click="openDeletePopup(comment)"
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
      message="Êtes-vous sûr de vouloir supprimer ce commentaire ?"
      subMessage="Cette action est irréversible."
      @confirm="confirmDelete"
      @cancel="closePopup"
    />
  </div>
</template>

<script setup lang="ts">
import { computed, type PropType } from 'vue'
import type { Comment } from '@/models/CommentModel'
import GenesisButton from '@/components/button/GenesisButton.vue'
import { usePopup } from '@/composables/usePopup'
import { useComments } from '@/composables/useComments'
import DeleteConfirmationPopup from '@/components/popup/DeleteConfirmationPopup.vue'
import TrashIcon from '@/components/icons/TrashIcon.vue'
import EditIcon from '@/components/icons/EditIcon.vue'
import { useDateFormat } from '@/composables/useDateFormat'

const props = defineProps({
  comment: { type: Object as PropType<Comment>, required: true },
})

const authorName = computed(() => {
  return (props.comment as any).authoruseridUsers?.getKeyValue() ?? 'Utilisateur inconnu'
})

const authorInitials = computed(() => {
  return authorName.value
    .split(' ')
    .slice(0, 2)
    .map((w: string) => w[0]?.toUpperCase() ?? '')
    .join('')
})

const { closePopup, openPopup, visible: deletePopup } = usePopup(false)
const { deleteComment, goToListView, goToUpdateFormView } = useComments()

const openDeletePopup = (entity: Comment) => {
  if (!entity) return
  openPopup()
}

const confirmDelete = async () => {
  const { success, error } = await deleteComment(props.comment)
  if (!success) console.error(error)
  closePopup()
  goToListView()
}

const { formatDate } = useDateFormat()
</script>

<style scoped>
.comment-details-wrapper {
  display: flex;
  flex-direction: column;
  gap: var(--mz-space-5);
  max-width: 880px;
  margin: 0 auto;
  padding: var(--mz-space-5);
}

/* ── Hero ── */
.comment-hero {
  display: flex;
  align-items: center;
  gap: var(--mz-space-5);
  padding: var(--mz-space-5);
  background: linear-gradient(135deg, #0e0922 0%, #211747 100%);
  border-radius: var(--mz-radius-xl);
}

.comment-hero-avatar {
  flex-shrink: 0;
  border: 3px solid rgba(255, 255, 255, 0.25);
}

.comment-hero-meta {
  display: flex;
  flex-direction: column;
  gap: var(--mz-space-2);
  flex: 1;
  min-width: 0;
}

.comment-hero-author {
  font-size: var(--mz-font-size-xl);
  font-weight: 800;
  color: var(--mz-text-on-dark-high);
  margin: 0;
}

.comment-hero-sub {
  font-size: var(--mz-font-size-sm);
  color: var(--mz-text-on-dark-muted);
  margin: 0;
}

.comment-hero-badges {
  display: flex;
  flex-wrap: wrap;
  gap: var(--mz-space-2);
}

/* ── Contenu ── */
.comment-content-card {
  display: flex;
  gap: var(--mz-space-4);
  padding: var(--mz-space-5);
  align-items: flex-start;
}

.comment-quote-bar {
  width: 4px;
  min-height: 60px;
  background: var(--mz-brand-accent);
  border-radius: 4px;
  flex-shrink: 0;
  align-self: stretch;
}

.comment-content-body {
  display: flex;
  flex-direction: column;
  gap: var(--mz-space-2);
  flex: 1;
}

.section-eyebrow {
  font-size: var(--mz-font-size-xs);
  font-weight: 700;
  text-transform: uppercase;
  letter-spacing: 0.06em;
  color: var(--mz-brand-accent);
}

.comment-content-text {
  font-size: var(--mz-font-size-base);
  color: var(--mz-text-on-light-medium);
  line-height: 1.75;
  margin: 0;
}

/* ── Sections ── */
.comment-sections {
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

/* ── Actions ── */
.comment-actions {
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
