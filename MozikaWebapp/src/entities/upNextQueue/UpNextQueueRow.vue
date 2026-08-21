<template>
  <tr class="hover:bg-base-200">
    <template v-for="field in UpNextQueue.getAllSearchFieldsMetadata()" :key="field.key">
      <td
        v-show="field.showInTable && visibleFields.includes(field.key)"
        class="px-3 py-2"
        :class="{
          'sticky left-0 bg-base-200 font-semibold': field.identifier,
        }"
      >
        <template v-if="field.type === 'date'">
          {{ formatDate(upNextQueueData[field.key]) }}
        </template>

        <template v-else-if="field.type === 'number'">
          {{
            upNextQueueData[field.key] != null ? $n(upNextQueueData[field.key], 'decimal') : '--'
          }}
        </template>

        <template v-else-if="field.type === 'select'">
          {{ upNextQueueData[field.key] != null ? upNextQueueData[field.key].getKeyValue() : '--' }}
        </template>

        <template v-else-if="field.type === 'file'">
          <img
            v-if="upNextQueueData[field.key]"
            :src="getUrl(upNextQueueData[field.key])"
            :alt="field.key"
            class="max-h-10 max-w-10"
          />
        </template>

        <template v-else>
          {{ upNextQueueData[field.key] ?? '--' }}
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
import { UpNextQueue } from '@/models/UpNextQueueModel'
import EntityRowActions from '@/components/table/EntityRowActions.vue'
import { useDateFormat } from '@/composables/useDateFormat'

const props = defineProps({
  upNextQueue: {
    type: Object as PropType<UpNextQueue>,
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

const upNextQueueData = computed(() => {
  return props.upNextQueue as Record<string, any>
})

const emit = defineEmits<{
  (e: 'request-delete', upNextQueue: UpNextQueue): void
  (e: 'request-view', upNextQueue: UpNextQueue): void
  (e: 'request-update', upNextQueue: UpNextQueue): void
}>()

const deleteRow = () => emit('request-delete', props.upNextQueue)
const viewRow = () => emit('request-view', props.upNextQueue)
const updateRow = () => emit('request-update', props.upNextQueue)
const { formatDate } = useDateFormat()
</script>
<style scoped></style>
