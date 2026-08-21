<template>
  <table class="table table-auto relative">
    <thead>
      <tr>
        <template v-for="field in PlaylistSong.getAllSearchFieldsMetadata()" :key="field.key">
          <th
            v-show="field.showInTable && visibleFields.includes(field.key)"
            class="sticky top-0 bg-primary text-primary-content px-3 py-2 font-bold min-w-35 z-1"
            :class="{ 'left-0 z-2': field.identifier }"
          >
            <div class="flex items-center justify-between gap-1">
              <span>{{ field.label }}</span>
              <SortButton
                v-show="field.sortable"
                :field-key="field.key"
                @sort="$emit('sortby', $event)"
              />
            </div>
          </th>
        </template>
        <th
          class="sticky top-0 right-0 bg-primary text-primary-content px-3 py-2 font-bold min-w-35 z-2"
        ></th>
      </tr>
    </thead>
    <tbody class="bg-base-100">
      <tr v-if="!loading && message">
        <td :colspan="visibleColCount + 1" class="text-center py-4 text-gray-500">
          {{ message }}
        </td>
      </tr>
      <component
        v-else
        v-for="playlistSong in data"
        :key="playlistSong.getKeyValue()"
        :is="PlaylistSongRow"
        :playlistSong="playlistSong"
        v-bind="itemProps"
        @request-view="$emit('request-view', $event)"
        @request-delete="$emit('request-delete', $event)"
        @request-update="$emit('request-update', $event)"
      />
    </tbody>
  </table>
</template>

<script setup lang="ts">
import PlaylistSongRow from './PlaylistSongRow.vue'
import { PlaylistSong } from '@/models/PlaylistSongModel'
import { computed, type PropType } from 'vue'
import { SortFieldParameter } from '@/models/api/RequestModel'
import SortButton from '@/components/button/SortButton.vue'

const props = defineProps({
  data: { type: Array as PropType<PlaylistSong[]>, required: true },
  message: String,
  loading: { type: Boolean, default: false },
  viewAction: { type: Boolean, default: true },
  editAction: { type: Boolean, default: true },
  removeAction: { type: Boolean, default: true },
  visibleFields: { type: Array as PropType<string[]>, default: () => [] },
})

defineEmits<{
  (e: 'sortby', sortData: SortFieldParameter): void
  (e: 'request-view', playlistSong: PlaylistSong): void
  (e: 'request-delete', playlistSong: PlaylistSong): void
  (e: 'request-update', playlistSong: PlaylistSong): void
}>()

// Regroupe les props pour éviter la répétition
const itemProps = computed(() => ({
  visibleFields: props.visibleFields,
  viewAction: props.viewAction,
  editAction: props.editAction,
  removeAction: props.removeAction,
}))

// Calcul du colspan isolé ici
const visibleColCount = computed(
  () =>
    props.visibleFields.filter((f) =>
      PlaylistSong.getAllSearchFieldsMetadata().some((m) => m.key === f && m.showInTable),
    ).length,
)
</script>
<style scoped></style>
