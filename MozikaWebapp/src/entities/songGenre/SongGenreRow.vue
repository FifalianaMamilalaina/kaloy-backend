<template>
  <tr class="hover:bg-base-200">
    <template v-for="field in SongGenre.getAllSearchFieldsMetadata()" :key="field.key">
      <td
        v-show="field.showInTable && visibleFields.includes(field.key)"
        class="px-3 py-2"
        :class="{
          'sticky left-0 bg-base-200 font-semibold': field.identifier,
        }"
      >
        <template v-if="field.type === 'date'">
          {{ formatDate(songGenreData[field.key]) }}
        </template>

        <template v-else-if="field.type === 'number'">
          {{ songGenreData[field.key] != null ? $n(songGenreData[field.key], 'decimal') : '--' }}
        </template>

        <template v-else-if="field.type === 'select'">
          {{ songGenreData[field.key] != null ? songGenreData[field.key].getKeyValue() : '--' }}
        </template>

        <template v-else-if="field.type === 'file'">
          <img
            v-if="songGenreData[field.key]"
            :src="getUrl(songGenreData[field.key])"
            :alt="field.key"
            class="max-h-10 max-w-10"
          />
        </template>

        <template v-else>
          {{ songGenreData[field.key] ?? '--' }}
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
import { SongGenre } from '@/models/SongGenreModel'
import EntityRowActions from '@/components/table/EntityRowActions.vue'
import { useDateFormat } from '@/composables/useDateFormat'

const props = defineProps({
  songGenre: {
    type: Object as PropType<SongGenre>,
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

const songGenreData = computed(() => {
  return props.songGenre as Record<string, any>
})

const emit = defineEmits<{
  (e: 'request-delete', songGenre: SongGenre): void
  (e: 'request-view', songGenre: SongGenre): void
  (e: 'request-update', songGenre: SongGenre): void
}>()

const deleteRow = () => emit('request-delete', props.songGenre)
const viewRow = () => emit('request-view', props.songGenre)
const updateRow = () => emit('request-update', props.songGenre)
const { formatDate } = useDateFormat()
</script>
<style scoped></style>
