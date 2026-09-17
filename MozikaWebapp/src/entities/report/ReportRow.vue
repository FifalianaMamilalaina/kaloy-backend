<template>
  <tr class="hover:bg-base-200">
    <template v-for="field in Report.getAllSearchFieldsMetadata()" :key="field.key">
      <td
        v-show="field.showInTable && visibleFields.includes(field.key)"
        class="px-3 py-2"
        :class="{
          'sticky left-0 bg-base-200 font-semibold': field.identifier,
        }"
      >
        <template v-if="field.type === 'date'">
          {{ formatDate(reportData[field.key]) }}
        </template>

        <template v-else-if="field.type === 'number'">
          {{ reportData[field.key] != null ? $n(reportData[field.key], 'decimal') : '--' }}
        </template>

        <template v-else-if="field.type === 'select'">
          {{ reportData[field.key] != null ? reportData[field.key].getKeyValue() : '--' }}
        </template>

        <template v-else-if="field.type === 'file'">
          <img
            v-if="reportData[field.key]"
            :src="getUrl(reportData[field.key])"
            :alt="field.key"
            class="max-h-10 max-w-10"
          />
        </template>

        <template v-else>
          {{ reportData[field.key] ?? '--' }}
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
import { Report } from '@/models/ReportModel'
import EntityRowActions from '@/components/table/EntityRowActions.vue'
import { useDateFormat } from '@/composables/useDateFormat'

const props = defineProps({
  report: {
    type: Object as PropType<Report>,
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

const reportData = computed(() => {
  return props.report as Record<string, any>
})

const emit = defineEmits<{
  (e: 'request-delete', report: Report): void
  (e: 'request-view', report: Report): void
  (e: 'request-update', report: Report): void
}>()

const deleteRow = () => emit('request-delete', props.report)
const viewRow = () => emit('request-view', props.report)
const updateRow = () => emit('request-update', props.report)
const { formatDate } = useDateFormat()
</script>
<style scoped></style>
