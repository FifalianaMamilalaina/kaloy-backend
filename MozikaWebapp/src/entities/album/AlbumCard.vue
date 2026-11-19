<template>
  <div class="mz-card mz-card-interactive album-card">
    <!-- En-tête : icône album + titre -->
    <div class="album-card-header">
      <div class="album-icon-wrap">
        <svg
          width="18"
          height="18"
          viewBox="0 0 24 24"
          fill="none"
          stroke="currentColor"
          stroke-width="2"
        >
          <circle cx="12" cy="12" r="10" />
          <circle cx="12" cy="12" r="3" />
          <line x1="12" y1="2" x2="12" y2="9" />
          <line x1="12" y1="15" x2="12" y2="22" />
          <line x1="2" y1="12" x2="9" y2="12" />
          <line x1="15" y1="12" x2="22" y2="12" />
        </svg>
      </div>
      <div class="album-title-block">
        <h3 class="album-title">Album #{{ albumData['id'] }}</h3>
        <span class="album-subtitle" v-if="albumData['title']">{{ albumData['title'] }}</span>
      </div>
    </div>

    <!-- Corps : champs visibles -->
    <div class="album-fields">
      <template v-for="field in Album.getAllSearchFieldsMetadata()" :key="field.key">
        <div v-if="field.showInTable && visibleFields.includes(field.key)" class="album-field-row">
          <span class="field-label">{{ field.label }}</span>
          <span class="field-value">
            <template v-if="field.type === 'date'">
              {{ formatDate(albumData[field.key]) }}
            </template>
            <template v-else-if="field.type === 'number'">
              {{ albumData[field.key] != null ? $n(albumData[field.key], 'decimal') : '--' }}
            </template>
            <template v-else-if="field.type === 'select'">
              {{ albumData[field.key] != null ? albumData[field.key].getKeyValue() : '--' }}
            </template>
            <template v-else-if="field.type === 'file'">
              <img
                v-if="albumData[field.key]"
                :src="getUrl(albumData[field.key])"
                :alt="field.key"
                class="field-img"
              />
            </template>
            <template v-else>
              {{ albumData[field.key] ?? '--' }}
            </template>
          </span>
        </div>
      </template>
    </div>

    <!-- Actions -->
    <div class="album-card-footer">
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
import { Album } from '@/models/AlbumModel'
import EntityRowActions from '@/components/table/EntityRowActions.vue'
import { useDateFormat } from '@/composables/useDateFormat'
import { getUrl } from '@/composables/useImage'

const props = defineProps({
  album: {
    type: Object as PropType<Album>,
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

const albumData = computed(() => props.album as Record<string, any>)

const emit = defineEmits<{
  (e: 'request-delete', album: Album): void
  (e: 'request-view', album: Album): void
  (e: 'request-update', album: Album): void
}>()

const deleteRow = () => emit('request-delete', props.album)
const viewRow = () => emit('request-view', props.album)
const updateRow = () => emit('request-update', props.album)
const { formatDate } = useDateFormat()
</script>

<style scoped>
.album-card {
  display: flex;
  flex-direction: column;
  gap: var(--mz-space-3);
  padding: var(--mz-space-4);
}

.album-card-header {
  display: flex;
  align-items: center;
  gap: var(--mz-space-3);
}

.album-icon-wrap {
  width: 38px;
  height: 38px;
  border-radius: var(--mz-radius-sm);
  background: rgba(55, 35, 112, 0.1);
  border: 1px solid rgba(55, 35, 112, 0.22);
  color: var(--mz-brand-purple);
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.album-title-block {
  display: flex;
  flex-direction: column;
  gap: 2px;
  min-width: 0;
}

.album-title {
  font-size: var(--mz-font-size-sm);
  font-weight: 700;
  color: var(--mz-text-on-light-high);
  margin: 0;
  overflow: hidden;
  white-space: nowrap;
  text-overflow: ellipsis;
}

.album-subtitle {
  font-size: var(--mz-font-size-xs);
  color: var(--mz-text-on-light-muted);
  overflow: hidden;
  white-space: nowrap;
  text-overflow: ellipsis;
}

.album-fields {
  display: flex;
  flex-direction: column;
  gap: 0;
  flex: 1;
}

.album-field-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: var(--mz-space-2);
  padding: 6px 0;
  border-bottom: 1px solid var(--mz-border-subtle-light);
}
.album-field-row:last-child {
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

.album-card-footer {
  display: flex;
  justify-content: flex-end;
  padding-top: var(--mz-space-3);
  border-top: 1px solid var(--mz-border-subtle-light);
}
</style>
