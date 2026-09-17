<template>
  <tr class="hover:bg-base-200">
    <template v-for="field in Album.getAllSearchFieldsMetadata()" :key="field.key">
      <td
        v-show="field.showInTable && visibleFields.includes(field.key)"
        class="px-3 py-2"
        :class="{
          'sticky left-0 bg-base-200 font-semibold': field.identifier,
        }"
      >
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
            class="max-h-10 max-w-10"
          />
        </template>

        <template v-else>
          {{ albumData[field.key] ?? '--' }}
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
import { Album } from '@/models/AlbumModel'
import EntityRowActions from '@/components/table/EntityRowActions.vue'
import { useDateFormat } from '@/composables/useDateFormat'

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

const albumData = computed(() => {
  return props.album as Record<string, any>
})

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
<style scoped></style>
