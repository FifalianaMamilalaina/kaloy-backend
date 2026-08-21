<template>
  <tr class="hover:bg-base-200">
    <template v-for="field in ArtistType.getAllSearchFieldsMetadata()" :key="field.key">
      <td
        v-show="field.showInTable && visibleFields.includes(field.key)"
        class="px-3 py-2"
        :class="{
          'sticky left-0 bg-base-200 font-semibold': field.identifier,
        }"
      >
        <template v-if="field.type === 'date'">
          {{ formatDate(artistTypeData[field.key]) }}
        </template>

        <template v-else-if="field.type === 'number'">
          {{ artistTypeData[field.key] != null ? $n(artistTypeData[field.key], 'decimal') : '--' }}
        </template>

        <template v-else-if="field.type === 'select'">
          {{ artistTypeData[field.key] != null ? artistTypeData[field.key].getKeyValue() : '--' }}
        </template>

        <template v-else-if="field.type === 'file'">
          <img
            v-if="artistTypeData[field.key]"
            :src="getUrl(artistTypeData[field.key])"
            :alt="field.key"
            class="max-h-10 max-w-10"
          />
        </template>

        <template v-else>
          {{ artistTypeData[field.key] ?? '--' }}
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
import { ArtistType } from '@/models/ArtistTypeModel'
import EntityRowActions from '@/components/table/EntityRowActions.vue'
import { useDateFormat } from '@/composables/useDateFormat'

const props = defineProps({
  artistType: {
    type: Object as PropType<ArtistType>,
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

const artistTypeData = computed(() => {
  return props.artistType as Record<string, any>
})

const emit = defineEmits<{
  (e: 'request-delete', artistType: ArtistType): void
  (e: 'request-view', artistType: ArtistType): void
  (e: 'request-update', artistType: ArtistType): void
}>()

const deleteRow = () => emit('request-delete', props.artistType)
const viewRow = () => emit('request-view', props.artistType)
const updateRow = () => emit('request-update', props.artistType)
const { formatDate } = useDateFormat()
</script>
<style scoped></style>
