<template>
  <tr class="hover:bg-base-200">
    <template v-for="field in EditorialPlaylist.getAllSearchFieldsMetadata()" :key="field.key">
      <td
        v-show="field.showInTable && visibleFields.includes(field.key)"
        class="px-3 py-2"
        :class="{
          'sticky left-0 bg-base-200 font-semibold': field.identifier,
        }"
      >
        <template v-if="field.type === 'date'">
          {{ formatDate(editorialPlaylistData[field.key]) }}
        </template>

        <template v-else-if="field.type === 'number'">
          {{
            editorialPlaylistData[field.key] != null
              ? $n(editorialPlaylistData[field.key], 'decimal')
              : '--'
          }}
        </template>

        <template v-else-if="field.type === 'select'">
          {{
            editorialPlaylistData[field.key] != null
              ? editorialPlaylistData[field.key].getKeyValue()
              : '--'
          }}
        </template>

        <template v-else-if="field.type === 'file'">
          <img
            v-if="editorialPlaylistData[field.key]"
            :src="getUrl(editorialPlaylistData[field.key])"
            :alt="field.key"
            class="max-h-10 max-w-10"
          />
        </template>

        <template v-else>
          {{ editorialPlaylistData[field.key] ?? '--' }}
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
import { EditorialPlaylist } from '@/models/EditorialPlaylistModel'
import EntityRowActions from '@/components/table/EntityRowActions.vue'
import { useDateFormat } from '@/composables/useDateFormat'

const props = defineProps({
  editorialPlaylist: {
    type: Object as PropType<EditorialPlaylist>,
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

const editorialPlaylistData = computed(() => {
  return props.editorialPlaylist as Record<string, any>
})

const emit = defineEmits<{
  (e: 'request-delete', editorialPlaylist: EditorialPlaylist): void
  (e: 'request-view', editorialPlaylist: EditorialPlaylist): void
  (e: 'request-update', editorialPlaylist: EditorialPlaylist): void
}>()

const deleteRow = () => emit('request-delete', props.editorialPlaylist)
const viewRow = () => emit('request-view', props.editorialPlaylist)
const updateRow = () => emit('request-update', props.editorialPlaylist)
const { formatDate } = useDateFormat()
</script>
<style scoped></style>
