<template>
  <div class="mz-card mz-card-interactive song-card">
    <!-- En-tête : icône musicale + titre -->
    <div class="song-card-header">
      <div class="song-icon-wrap">
        <svg
          width="18"
          height="18"
          viewBox="0 0 24 24"
          fill="none"
          stroke="currentColor"
          stroke-width="2"
        >
          <path d="M9 18V5l12-2v13" />
          <circle cx="6" cy="18" r="3" />
          <circle cx="18" cy="16" r="3" />
        </svg>
      </div>
      <div class="song-title-block">
        <h3 class="song-title">Song #{{ songData['id'] }}</h3>
        <span class="song-subtitle" v-if="songData['title']">{{ songData['title'] }}</span>
      </div>
    </div>

    <!-- Corps : champs visibles -->
    <div class="song-fields">
      <template v-for="field in Song.getAllSearchFieldsMetadata()" :key="field.key">
        <div v-if="field.showInTable && visibleFields.includes(field.key)" class="song-field-row">
          <span class="field-label">{{ field.label }}</span>
          <span class="field-value">
            <template v-if="field.type === 'date'">
              {{ formatDate(songData[field.key]) }}
            </template>
            <template v-else-if="field.type === 'number'">
              {{ songData[field.key] != null ? $n(songData[field.key], 'decimal') : '--' }}
            </template>
            <template v-else-if="field.type === 'select'">
              {{ songData[field.key] != null ? songData[field.key].getKeyValue() : '--' }}
            </template>
            <template v-else-if="field.type === 'file'">
              <img
                v-if="songData[field.key]"
                :src="getUrl(songData[field.key])"
                :alt="field.key"
                class="field-img"
              />
            </template>
            <template v-else>
              {{ songData[field.key] ?? '--' }}
            </template>
          </span>
        </div>
      </template>
    </div>

    <!-- Actions -->
    <div class="song-card-footer">
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
import { Song } from '@/models/SongModel'
import EntityRowActions from '@/components/table/EntityRowActions.vue'
import { useDateFormat } from '@/composables/useDateFormat'
import { getUrl } from '@/composables/useImage'

const props = defineProps({
  song: {
    type: Object as PropType<Song>,
    required: true,
  },
  viewAction: {
    type: Boolean,
    default: true,
  },
  editAction: {
    type: Boolean,
    default: true,
  },
  removeAction: {
    type: Boolean,
    default: true,
  },
  visibleFields: {
    type: Array as PropType<string[]>,
    default: () => [],
  },
})

const songData = computed(() => props.song as Record<string, any>)

const emit = defineEmits<{
  (e: 'request-delete', song: Song): void
  (e: 'request-view', song: Song): void
  (e: 'request-update', song: Song): void
}>()

const deleteRow = () => emit('request-delete', props.song)
const viewRow = () => emit('request-view', props.song)
const updateRow = () => emit('request-update', props.song)
const { formatDate } = useDateFormat()
</script>

<style scoped>
.song-card {
  display: flex;
  flex-direction: column;
  gap: var(--mz-space-3);
  padding: var(--mz-space-4);
}

/* En-tête */
.song-card-header {
  display: flex;
  align-items: center;
  gap: var(--mz-space-3);
}

.song-icon-wrap {
  width: 38px;
  height: 38px;
  border-radius: var(--mz-radius-sm);
  background: var(--mz-brand-accent-light);
  border: 1px solid var(--mz-border-accent);
  color: var(--mz-brand-accent);
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.song-title-block {
  display: flex;
  flex-direction: column;
  gap: 2px;
  min-width: 0;
}

.song-title {
  font-size: var(--mz-font-size-sm);
  font-weight: 700;
  color: var(--mz-text-on-light-high);
  margin: 0;
  truncate: true;
  overflow: hidden;
  white-space: nowrap;
  text-overflow: ellipsis;
}

.song-subtitle {
  font-size: var(--mz-font-size-xs);
  color: var(--mz-text-on-light-muted);
  overflow: hidden;
  white-space: nowrap;
  text-overflow: ellipsis;
}

/* Corps */
.song-fields {
  display: flex;
  flex-direction: column;
  gap: 0;
  flex: 1;
}

.song-field-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: var(--mz-space-2);
  padding: 6px 0;
  border-bottom: 1px solid var(--mz-border-subtle-light);
}
.song-field-row:last-child {
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
.song-card-footer {
  display: flex;
  justify-content: flex-end;
  padding-top: var(--mz-space-3);
  border-top: 1px solid var(--mz-border-subtle-light);
}
</style>
