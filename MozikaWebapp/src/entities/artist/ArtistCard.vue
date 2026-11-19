<template>
  <div class="mz-card mz-card-interactive artist-card">
    <!-- Header : photo / initiales + identité -->
    <div class="artist-card-header">
      <div class="artist-avatar">
        <img v-if="photoUrl" :src="photoUrl" :alt="artistName" class="artist-avatar-img" />
        <span v-else class="artist-avatar-initials">{{ initials }}</span>
      </div>
      <div class="artist-info">
        <h3 class="artist-name">{{ artistName }}</h3>
        <span class="artist-type" v-if="artistData['artisttypeidArtistTypes']">
          {{
            artistData['artisttypeidArtistTypes'].getKeyValue?.() ??
            artistData['artisttypeidArtistTypes']
          }}
        </span>
      </div>
      <!-- Badge certifié -->
      <span
        v-if="artistData['isCertified']"
        class="mz-badge mz-badge-accent artist-certified"
        title="Artiste certifié"
        >✓ Certifié</span
      >
    </div>

    <!-- Champs -->
    <div class="artist-fields">
      <template v-for="field in Artist.getAllSearchFieldsMetadata()" :key="field.key">
        <div v-if="field.showInTable && visibleFields.includes(field.key)" class="artist-field-row">
          <span class="field-label">{{ field.label }}</span>
          <span class="field-value">
            <template v-if="field.type === 'date'">{{
              formatDate(artistData[field.key])
            }}</template>
            <template v-else-if="field.type === 'number'">
              {{ artistData[field.key] != null ? $n(artistData[field.key], 'decimal') : '--' }}
            </template>
            <template v-else-if="field.type === 'select'">
              {{ artistData[field.key] != null ? artistData[field.key].getKeyValue() : '--' }}
            </template>
            <template v-else-if="field.type === 'file'">
              <img
                v-if="artistData[field.key]"
                :src="getUrl(artistData[field.key])"
                :alt="field.key"
                class="field-img"
              />
            </template>
            <template v-else>{{ artistData[field.key] ?? '--' }}</template>
          </span>
        </div>
      </template>
    </div>

    <!-- Actions -->
    <div class="artist-card-footer">
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
import { Artist } from '@/models/ArtistModel'
import EntityRowActions from '@/components/table/EntityRowActions.vue'
import { useDateFormat } from '@/composables/useDateFormat'
import { getUrl } from '@/composables/useImage'

const props = defineProps({
  artist: { type: Object as PropType<Artist>, required: true },
  viewAction: { type: Boolean, default: true },
  editAction: { type: Boolean, default: true },
  removeAction: { type: Boolean, default: true },
  visibleFields: { type: Array as PropType<string[]>, default: () => [] },
})

const artistData = computed(() => props.artist as Record<string, any>)

const artistName = computed(
  () =>
    artistData.value['stageName'] ??
    artistData.value['useridUsers']?.getKeyValue?.() ??
    `Artiste #${artistData.value['id']}`,
)

const photoUrl = computed(() => {
  const url = artistData.value['photoUrl']
  return url ? getUrl(url) : null
})

const initials = computed(() =>
  artistName.value
    .split(' ')
    .slice(0, 2)
    .map((w: string) => w[0]?.toUpperCase() ?? '')
    .join(''),
)

const emit = defineEmits<{
  (e: 'request-delete', artist: Artist): void
  (e: 'request-view', artist: Artist): void
  (e: 'request-update', artist: Artist): void
}>()

const deleteRow = () => emit('request-delete', props.artist)
const viewRow = () => emit('request-view', props.artist)
const updateRow = () => emit('request-update', props.artist)
const { formatDate } = useDateFormat()
</script>

<style scoped>
.artist-card {
  display: flex;
  flex-direction: column;
  gap: var(--mz-space-3);
  padding: var(--mz-space-4);
}

/* ── Header ── */
.artist-card-header {
  display: flex;
  align-items: center;
  gap: var(--mz-space-3);
}

.artist-avatar {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  background: linear-gradient(135deg, var(--mz-brand-primary) 0%, var(--mz-brand-accent) 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  overflow: hidden;
  border: 2px solid rgba(230, 115, 0, 0.2);
}

.artist-avatar-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.artist-avatar-initials {
  font-size: 18px;
  font-weight: 800;
  color: #ffffff;
  letter-spacing: -0.02em;
}

.artist-info {
  display: flex;
  flex-direction: column;
  gap: 3px;
  min-width: 0;
  flex: 1;
}

.artist-name {
  font-size: var(--mz-font-size-sm);
  font-weight: 700;
  color: var(--mz-text-on-light-high);
  margin: 0;
  overflow: hidden;
  white-space: nowrap;
  text-overflow: ellipsis;
}

.artist-type {
  font-size: var(--mz-font-size-xs);
  color: var(--mz-text-on-light-muted);
}

.artist-certified {
  font-size: 11px;
  flex-shrink: 0;
}

/* ── Champs ── */
.artist-fields {
  display: flex;
  flex-direction: column;
  gap: 0;
  flex: 1;
}

.artist-field-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: var(--mz-space-2);
  padding: 6px 0;
  border-bottom: 1px solid var(--mz-border-subtle-light);
}
.artist-field-row:last-child {
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

/* ── Footer ── */
.artist-card-footer {
  display: flex;
  justify-content: flex-end;
  padding-top: var(--mz-space-3);
  border-top: 1px solid var(--mz-border-subtle-light);
}
</style>
