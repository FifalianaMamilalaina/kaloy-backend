<template>
  <tr class="hover:bg-base-200">
    <template v-for="field in ReportStatuse.getAllSearchFieldsMetadata()" :key="field.key">
      <td
        v-show="field.showInTable && visibleFields.includes(field.key)"
        class="px-3 py-2"
        :class="{
          'sticky left-0 bg-base-200 font-semibold': field.identifier,
        }"
      >
        <template v-if="field.type === 'date'">
          {{ formatDate(reportStatuseData[field.key]) }}
        </template>

        <template v-else-if="field.type === 'number'">
          {{
            reportStatuseData[field.key] != null
              ? $n(reportStatuseData[field.key], 'decimal')
              : '--'
          }}
        </template>

        <template v-else-if="field.type === 'select'">
          {{
            reportStatuseData[field.key] != null ? reportStatuseData[field.key].getKeyValue() : '--'
          }}
        </template>

        <template v-else-if="field.type === 'file'">
          <img
            v-if="reportStatuseData[field.key]"
            :src="getUrl(reportStatuseData[field.key])"
            :alt="field.key"
            class="max-h-10 max-w-10"
          />
        </template>

        <template v-else>
          {{ reportStatuseData[field.key] ?? '--' }}
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
import { ReportStatuse } from '@/models/ReportStatuseModel'
import EntityRowActions from '@/components/table/EntityRowActions.vue'
import { useDateFormat } from '@/composables/useDateFormat'

const props = defineProps({
  reportStatuse: {
    type: Object as PropType<ReportStatuse>,
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

const reportStatuseData = computed(() => {
  return props.reportStatuse as Record<string, any>
})

const emit = defineEmits<{
  (e: 'request-delete', reportStatuse: ReportStatuse): void
  (e: 'request-view', reportStatuse: ReportStatuse): void
  (e: 'request-update', reportStatuse: ReportStatuse): void
}>()

const deleteRow = () => emit('request-delete', props.reportStatuse)
const viewRow = () => emit('request-view', props.reportStatuse)
const updateRow = () => emit('request-update', props.reportStatuse)
const { formatDate } = useDateFormat()
</script>
<style scoped></style>
