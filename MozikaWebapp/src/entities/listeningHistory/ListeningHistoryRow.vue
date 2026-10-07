<template>
  <tr class="hover:bg-base-200">
    <template v-for="field in ListeningHistory.getAllSearchFieldsMetadata()" :key="field.key">
      <td
        v-show="field.showInTable && visibleFields.includes(field.key)"
        class="px-3 py-2"
        :class="{
          'sticky left-0 bg-base-200 font-semibold': field.identifier,
        }"
      >
        <template v-if="field.type === 'date'">
          {{ formatDate(listeningHistoryData[field.key]) }}
        </template>

        <template v-else-if="field.type === 'number'">
          {{
            listeningHistoryData[field.key] != null
              ? $n(listeningHistoryData[field.key], 'decimal')
              : '--'
          }}
        </template>

        <template v-else-if="field.type === 'select'">
          {{
            listeningHistoryData[field.key] != null
              ? listeningHistoryData[field.key].getKeyValue()
              : '--'
          }}
        </template>

        <template v-else-if="field.type === 'file'">
          <img
            v-if="listeningHistoryData[field.key]"
            :src="getUrl(listeningHistoryData[field.key])"
            :alt="field.key"
            class="max-h-10 max-w-10"
          />
        </template>

        <template v-else>
          {{ listeningHistoryData[field.key] ?? '--' }}
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
import { ListeningHistory } from '@/models/ListeningHistoryModel'
import EntityRowActions from '@/components/table/EntityRowActions.vue'
import { useDateFormat } from '@/composables/useDateFormat'

const props = defineProps({
  listeningHistory: {
    type: Object as PropType<ListeningHistory>,
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

const listeningHistoryData = computed(() => {
  return props.listeningHistory as Record<string, any>
})

const emit = defineEmits<{
  (e: 'request-delete', listeningHistory: ListeningHistory): void
  (e: 'request-view', listeningHistory: ListeningHistory): void
  (e: 'request-update', listeningHistory: ListeningHistory): void
}>()

const deleteRow = () => emit('request-delete', props.listeningHistory)
const viewRow = () => emit('request-view', props.listeningHistory)
const updateRow = () => emit('request-update', props.listeningHistory)
const { formatDate } = useDateFormat()
</script>
<style scoped></style>
