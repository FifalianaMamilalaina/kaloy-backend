<template>
  <tr class="hover:bg-base-200">
    <template v-for="field in SubmissionStatuse.getAllSearchFieldsMetadata()" :key="field.key">
      <td
        v-show="field.showInTable && visibleFields.includes(field.key)"
        class="px-3 py-2"
        :class="{
          'sticky left-0 bg-base-200 font-semibold': field.identifier,
        }"
      >
        <template v-if="field.type === 'date'">
          {{ formatDate(submissionStatuseData[field.key]) }}
        </template>

        <template v-else-if="field.type === 'number'">
          {{
            submissionStatuseData[field.key] != null
              ? $n(submissionStatuseData[field.key], 'decimal')
              : '--'
          }}
        </template>

        <template v-else-if="field.type === 'select'">
          {{
            submissionStatuseData[field.key] != null
              ? submissionStatuseData[field.key].getKeyValue()
              : '--'
          }}
        </template>

        <template v-else-if="field.type === 'file'">
          <img
            v-if="submissionStatuseData[field.key]"
            :src="getUrl(submissionStatuseData[field.key])"
            :alt="field.key"
            class="max-h-10 max-w-10"
          />
        </template>

        <template v-else>
          {{ submissionStatuseData[field.key] ?? '--' }}
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
import { SubmissionStatuse } from '@/models/SubmissionStatuseModel'
import EntityRowActions from '@/components/table/EntityRowActions.vue'
import { useDateFormat } from '@/composables/useDateFormat'

const props = defineProps({
  submissionStatuse: {
    type: Object as PropType<SubmissionStatuse>,
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

const submissionStatuseData = computed(() => {
  return props.submissionStatuse as Record<string, any>
})

const emit = defineEmits<{
  (e: 'request-delete', submissionStatuse: SubmissionStatuse): void
  (e: 'request-view', submissionStatuse: SubmissionStatuse): void
  (e: 'request-update', submissionStatuse: SubmissionStatuse): void
}>()

const deleteRow = () => emit('request-delete', props.submissionStatuse)
const viewRow = () => emit('request-view', props.submissionStatuse)
const updateRow = () => emit('request-update', props.submissionStatuse)
const { formatDate } = useDateFormat()
</script>
<style scoped></style>
