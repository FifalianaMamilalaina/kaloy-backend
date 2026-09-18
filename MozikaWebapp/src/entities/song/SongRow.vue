<template>
  <tr class="hover:bg-base-200">
    <template v-for="field in Song.getAllSearchFieldsMetadata()" :key="field.key">
      <td
        v-show="field.showInTable && visibleFields.includes(field.key)"
        class="px-3 py-2"
        :class="{
          'sticky left-0 bg-base-200 font-semibold': field.identifier,
        }"
      >
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
            class="max-h-10 max-w-10"
          />
        </template>

        <template v-else>
          {{ songData[field.key] ?? '--' }}
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

const songData = computed(() => {
  return props.song as Record<string, any>
})

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
<style scoped></style>
