<template>
  <tr class="hover:bg-base-200">
    <template v-for="field in EventMedia.getAllSearchFieldsMetadata()" :key="field.key">
      <td
        v-show="field.showInTable && visibleFields.includes(field.key)"
        class="px-3 py-2"
        :class="{
          'sticky left-0 bg-base-200 font-semibold': field.identifier,
        }"
      >
        <template v-if="field.type === 'date'">
          {{ formatDate(eventMediaData[field.key]) }}
        </template>

        <template v-else-if="field.type === 'number'">
          {{ eventMediaData[field.key] != null ? $n(eventMediaData[field.key], 'decimal') : '--' }}
        </template>

        <template v-else-if="field.type === 'select'">
          {{ eventMediaData[field.key] != null ? eventMediaData[field.key].getKeyValue() : '--' }}
        </template>

        <template v-else-if="field.type === 'file'">
          <img
            v-if="eventMediaData[field.key]"
            :src="getUrl(eventMediaData[field.key])"
            :alt="field.key"
            class="max-h-10 max-w-10"
          />
        </template>

        <template v-else>
          {{ eventMediaData[field.key] ?? '--' }}
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
import { EventMedia } from '@/models/EventMediaModel'
import EntityRowActions from '@/components/table/EntityRowActions.vue'
import { useDateFormat } from '@/composables/useDateFormat'

const props = defineProps({
  eventMedia: {
    type: Object as PropType<EventMedia>,
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

const eventMediaData = computed(() => {
  return props.eventMedia as Record<string, any>
})

const emit = defineEmits<{
  (e: 'request-delete', eventMedia: EventMedia): void
  (e: 'request-view', eventMedia: EventMedia): void
  (e: 'request-update', eventMedia: EventMedia): void
}>()

const deleteRow = () => emit('request-delete', props.eventMedia)
const viewRow = () => emit('request-view', props.eventMedia)
const updateRow = () => emit('request-update', props.eventMedia)
const { formatDate } = useDateFormat()
</script>
<style scoped></style>
