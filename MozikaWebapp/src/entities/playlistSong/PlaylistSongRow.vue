<template>
  <tr class="hover:bg-base-200">
    <template v-for="field in PlaylistSong.getAllSearchFieldsMetadata()" :key="field.key">
      <td
        v-show="field.showInTable && visibleFields.includes(field.key)"
        class="px-3 py-2"
        :class="{
          'sticky left-0 bg-base-200 font-semibold': field.identifier,
        }"
      >
        <template v-if="field.type === 'date'">
          {{ formatDate(playlistSongData[field.key]) }}
        </template>

        <template v-else-if="field.type === 'number'">
          {{
            playlistSongData[field.key] != null ? $n(playlistSongData[field.key], 'decimal') : '--'
          }}
        </template>

        <template v-else-if="field.type === 'select'">
          {{
            playlistSongData[field.key] != null ? playlistSongData[field.key].getKeyValue() : '--'
          }}
        </template>

        <template v-else-if="field.type === 'file'">
          <img
            v-if="playlistSongData[field.key]"
            :src="getUrl(playlistSongData[field.key])"
            :alt="field.key"
            class="max-h-10 max-w-10"
          />
        </template>

        <template v-else>
          {{ playlistSongData[field.key] ?? '--' }}
        </template>
      </td>
    </template>

    <!-- Actions -->
    <td class="px-3 py-2 bg-base-200 sticky right-0 text-end">
      <div class="rounded-md inline-flex">
        <EntityRowActions
          :view="viewAction"
          :remove="removeAction"
          :edit="editAction"
          @delete-row="deleteRow"
          @view-row="viewRow"
          @edit-row="updateRow"
        />
      </div>
    </td>
  </tr>
</template>

<script setup lang="ts">
import { computed, type PropType } from 'vue'
import { PlaylistSong } from '@/models/PlaylistSongModel'
import EntityRowActions from '@/components/table/EntityRowActions.vue'
import { useDateFormat } from '@/composables/useDateFormat'

const props = defineProps({
  playlistSong: {
    type: Object as PropType<PlaylistSong>,
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

const playlistSongData = computed(() => {
  return props.playlistSong as Record<string, any>
})

const emit = defineEmits<{
  (e: 'request-delete', playlistSong: PlaylistSong): void
  (e: 'request-view', playlistSong: PlaylistSong): void
  (e: 'request-update', playlistSong: PlaylistSong): void
}>()

const deleteRow = () => emit('request-delete', props.playlistSong)
const viewRow = () => emit('request-view', props.playlistSong)
const updateRow = () => emit('request-update', props.playlistSong)
const { formatDate } = useDateFormat()
</script>
<style scoped></style>
