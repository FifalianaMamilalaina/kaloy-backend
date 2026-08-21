<template>
  <tr class="hover:bg-base-200">
    <template v-for="field in Artist.getAllSearchFieldsMetadata()" :key="field.key">
      <td
        v-show="field.showInTable && visibleFields.includes(field.key)"
        class="px-3 py-2"
        :class="{
          'sticky left-0 bg-base-200 font-semibold': field.identifier,
        }"
      >
        <template v-if="field.type === 'date'">
          {{ formatDate(artistData[field.key]) }}
        </template>

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
            class="max-h-10 max-w-10"
          />
        </template>

        <template v-else>
          {{ artistData[field.key] ?? '--' }}
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
import { Artist } from '@/models/ArtistModel'
import EntityRowActions from '@/components/table/EntityRowActions.vue'
import { useDateFormat } from '@/composables/useDateFormat'

const props = defineProps({
  artist: {
    type: Object as PropType<Artist>,
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

const artistData = computed(() => {
  return props.artist as Record<string, any>
})

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
<style scoped></style>
