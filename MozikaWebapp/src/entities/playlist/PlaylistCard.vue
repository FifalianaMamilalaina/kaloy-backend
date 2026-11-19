<template>
  <div class="mz-card mz-card-interactive playlist-card">
    <!-- Header : icône playlist + nom & visibilité -->
    <div class="playlist-card-header">
      <div class="playlist-icon-wrap">
        <svg
          width="24"
          height="24"
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
      <div class="playlist-title-wrap">
        <h3 class="playlist-name" :title="playlistData['name'] ?? 'Playlist'">
          {{ playlistData['name'] ?? 'Playlist sans titre' }}
        </h3>
        <span class="playlist-id">#{{ playlistData['id'] }}</span>
      </div>
      <span
        v-if="playlistData['visibilityidPlaylistVisibilities']"
        class="mz-badge mz-badge-accent playlist-badge"
      >
        {{
          playlistData['visibilityidPlaylistVisibilities'].getKeyValue?.() ??
          playlistData['visibilityidPlaylistVisibilities']
        }}
      </span>
    </div>

    <!-- Champs configurés -->
    <div class="playlist-fields">
      <template v-for="field in Playlist.getAllSearchFieldsMetadata()" :key="field.key">
        <div
          v-if="field.showInTable && visibleFields.includes(field.key) && field.key !== 'name'"
          class="playlist-field-row"
        >
          <span class="field-label">{{ field.label }}</span>
          <span class="field-value">
            <template v-if="field.type === 'date'">
              {{ formatDate(playlistData[field.key]) }}
            </template>
            <template v-else-if="field.type === 'number'">
              {{ playlistData[field.key] != null ? $n(playlistData[field.key], 'decimal') : '--' }}
            </template>
            <template v-else-if="field.type === 'select'">
              {{
                playlistData[field.key] != null
                  ? (playlistData[field.key].getKeyValue?.() ?? playlistData[field.key])
                  : '--'
              }}
            </template>
            <template v-else-if="field.type === 'file'">
              <img
                v-if="playlistData[field.key]"
                :src="getUrl(playlistData[field.key])"
                :alt="field.key"
                class="field-img"
              />
            </template>
            <template v-else>
              {{ playlistData[field.key] ?? '--' }}
            </template>
          </span>
        </div>
      </template>
    </div>

    <!-- Actions en bas de carte -->
    <div class="playlist-card-footer">
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
import { Playlist } from '@/models/PlaylistModel'
import EntityRowActions from '@/components/table/EntityRowActions.vue'
import { useDateFormat } from '@/composables/useDateFormat'

const props = defineProps({
  playlist: {
    type: Object as PropType<Playlist>,
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

const playlistData = computed(() => {
  return props.playlist as Record<string, any>
})

const emit = defineEmits<{
  (e: 'request-delete', playlist: Playlist): void
  (e: 'request-view', playlist: Playlist): void
  (e: 'request-update', playlist: Playlist): void
}>()

const deleteRow = () => emit('request-delete', props.playlist)
const viewRow = () => emit('request-view', props.playlist)
const updateRow = () => emit('request-update', props.playlist)
const { formatDate } = useDateFormat()

const getUrl = (val: string) => val
</script>

<style scoped>
.playlist-card {
  display: flex;
  flex-direction: column;
  height: 100%;
  padding: var(--mz-space-4);
  gap: var(--mz-space-3);
  transition:
    transform var(--mz-transition-base),
    box-shadow var(--mz-transition-base);
}

.playlist-card:hover {
  transform: translateY(-2px);
  box-shadow: var(--mz-shadow-card-hover);
}

.playlist-card-header {
  display: flex;
  align-items: center;
  gap: var(--mz-space-3);
  padding-bottom: var(--mz-space-3);
  border-bottom: 1px solid var(--mz-border-subtle-light);
}

.playlist-icon-wrap {
  width: 44px;
  height: 44px;
  border-radius: var(--mz-radius-md);
  background: linear-gradient(135deg, #10b981 0%, #059669 100%);
  color: #ffffff;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  box-shadow: 0 3px 8px rgba(16, 185, 129, 0.25);
}

.playlist-title-wrap {
  display: flex;
  flex-direction: column;
  min-width: 0;
  flex: 1;
}

.playlist-name {
  font-size: var(--mz-font-size-base);
  font-weight: 700;
  color: var(--mz-text-on-light-high);
  margin: 0;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.playlist-id {
  font-size: 11px;
  color: var(--mz-text-on-light-muted);
  font-weight: 600;
}

.playlist-badge {
  flex-shrink: 0;
}

.playlist-fields {
  display: flex;
  flex-direction: column;
  gap: 6px;
  flex: 1;
  font-size: var(--mz-font-size-xs);
}

.playlist-field-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: var(--mz-space-2);
  padding: 4px 0;
  border-bottom: 1px solid var(--mz-border-subtle-light);
}

.playlist-field-row:last-child {
  border-bottom: none;
}

.field-label {
  font-weight: 600;
  text-transform: uppercase;
  letter-spacing: 0.04em;
  color: var(--mz-text-on-light-muted);
  font-size: 10px;
}

.field-value {
  color: var(--mz-text-on-light-high);
  font-weight: 500;
  text-align: right;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  max-width: 60%;
}

.field-img {
  max-height: 28px;
  max-width: 28px;
  border-radius: var(--mz-radius-sm);
  object-fit: cover;
}

.playlist-card-footer {
  display: flex;
  justify-content: flex-end;
  padding-top: var(--mz-space-2);
  border-top: 1px solid var(--mz-border-subtle-light);
  margin-top: auto;
}
</style>
