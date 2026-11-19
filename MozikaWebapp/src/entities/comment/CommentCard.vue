<template>
  <div class="mz-card mz-card-interactive comment-card">
    <!-- En-tête -->
    <div class="comment-card-header">
      <!-- Avatar initiales de l'auteur -->
      <div class="comment-avatar mz-avatar mz-avatar-md">
        {{ authorInitials }}
      </div>
      <div class="comment-title-block">
        <h3 class="comment-author">{{ authorName }}</h3>
        <span class="comment-meta">Commentaire #{{ commentData['id'] }}</span>
      </div>
      <!-- Badge statut masqué -->
      <span
        v-if="commentData['isHidden'] !== undefined"
        class="mz-badge"
        :class="commentData['isHidden'] ? 'mz-badge-danger' : 'mz-badge-success'"
      >
        {{ commentData['isHidden'] ? 'Masqué' : 'Visible' }}
      </span>
    </div>

    <!-- Aperçu du contenu -->
    <div class="comment-preview" v-if="commentData['content']">
      <p class="comment-text">{{ commentData['content'] }}</p>
    </div>

    <!-- Champs supplémentaires -->
    <div class="comment-fields">
      <template v-for="field in Comment.getAllSearchFieldsMetadata()" :key="field.key">
        <div
          v-if="field.showInTable && visibleFields.includes(field.key) && field.key !== 'content'"
          class="comment-field-row"
        >
          <span class="field-label">{{ field.label }}</span>
          <span class="field-value">
            <template v-if="field.type === 'date'">{{
              formatDate(commentData[field.key])
            }}</template>
            <template v-else-if="field.type === 'number'">
              {{ commentData[field.key] != null ? $n(commentData[field.key], 'decimal') : '--' }}
            </template>
            <template v-else-if="field.type === 'select'">
              {{ commentData[field.key] != null ? commentData[field.key].getKeyValue() : '--' }}
            </template>
            <template v-else-if="field.type === 'file'">
              <img
                v-if="commentData[field.key]"
                :src="getUrl(commentData[field.key])"
                :alt="field.key"
                class="field-img"
              />
            </template>
            <template v-else>{{ commentData[field.key] ?? '--' }}</template>
          </span>
        </div>
      </template>
    </div>

    <!-- Actions -->
    <div class="comment-card-footer">
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
import { Comment } from '@/models/CommentModel'
import EntityRowActions from '@/components/table/EntityRowActions.vue'
import { useDateFormat } from '@/composables/useDateFormat'
import { getUrl } from '@/composables/useImage'

const props = defineProps({
  comment: { type: Object as PropType<Comment>, required: true },
  viewAction: { type: Boolean, default: true },
  editAction: { type: Boolean, default: true },
  removeAction: { type: Boolean, default: true },
  visibleFields: { type: Array as PropType<string[]>, default: () => [] },
})

const commentData = computed(() => props.comment as Record<string, any>)

const authorName = computed(() => {
  return commentData.value['authoruseridUsers']?.getKeyValue() ?? 'Utilisateur inconnu'
})

const authorInitials = computed(() => {
  const name = authorName.value
  return name
    .split(' ')
    .slice(0, 2)
    .map((w: string) => w[0]?.toUpperCase() ?? '')
    .join('')
})

const emit = defineEmits<{
  (e: 'request-delete', comment: Comment): void
  (e: 'request-view', comment: Comment): void
  (e: 'request-update', comment: Comment): void
}>()

const deleteRow = () => emit('request-delete', props.comment)
const viewRow = () => emit('request-view', props.comment)
const updateRow = () => emit('request-update', props.comment)
const { formatDate } = useDateFormat()
</script>

<style scoped>
.comment-card {
  display: flex;
  flex-direction: column;
  gap: var(--mz-space-3);
  padding: var(--mz-space-4);
}

/* En-tête avec avatar */
.comment-card-header {
  display: flex;
  align-items: center;
  gap: var(--mz-space-3);
}

.comment-avatar {
  flex-shrink: 0;
}

.comment-title-block {
  display: flex;
  flex-direction: column;
  gap: 2px;
  min-width: 0;
  flex: 1;
}

.comment-author {
  font-size: var(--mz-font-size-sm);
  font-weight: 700;
  color: var(--mz-text-on-light-high);
  margin: 0;
  overflow: hidden;
  white-space: nowrap;
  text-overflow: ellipsis;
}

.comment-meta {
  font-size: var(--mz-font-size-xs);
  color: var(--mz-text-on-light-muted);
}

/* Aperçu contenu */
.comment-preview {
  background: var(--mz-surface-light-soft);
  border: 1px solid var(--mz-border-subtle-light);
  border-left: 3px solid var(--mz-brand-accent);
  border-radius: var(--mz-radius-sm);
  padding: var(--mz-space-3) var(--mz-space-4);
}

.comment-text {
  font-size: var(--mz-font-size-sm);
  color: var(--mz-text-on-light-medium);
  line-height: 1.6;
  margin: 0;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

/* Champs -->  */
.comment-fields {
  display: flex;
  flex-direction: column;
  gap: 0;
}

.comment-field-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: var(--mz-space-2);
  padding: 6px 0;
  border-bottom: 1px solid var(--mz-border-subtle-light);
}
.comment-field-row:last-child {
  border-bottom: none;
}

.field-label {
  font-size: var(--mz-font-size-xs);
  font-weight: 700;
  text-transform: uppercase;
  letter-spacing: 0.04em;
  color: var(--mz-text-on-light-muted);
  flex-shrink: 0;
}

.field-value {
  font-size: var(--mz-font-size-sm);
  color: var(--mz-text-on-light-high);
  text-align: right;
  max-width: 60%;
  overflow: hidden;
  white-space: nowrap;
  text-overflow: ellipsis;
}

.field-img {
  max-height: 36px;
  max-width: 36px;
  border-radius: var(--mz-radius-xs);
  object-fit: cover;
}

/* Footer */
.comment-card-footer {
  display: flex;
  justify-content: flex-end;
  padding-top: var(--mz-space-3);
  border-top: 1px solid var(--mz-border-subtle-light);
}
</style>
