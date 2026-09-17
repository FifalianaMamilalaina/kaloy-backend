<template>
  <tr class="hover:bg-base-200">
    <template v-for="field in ContentSubmission.getAllSearchFieldsMetadata()" :key="field.key">
      <td
        v-show="field.showInTable && visibleFields.includes(field.key)"
        class="px-3 py-2"
        :class="{
          'sticky left-0 bg-base-200 font-semibold': field.identifier,
        }"
      >
        <template v-if="field.type === 'date'">
          {{ formatDate(contentSubmissionData[field.key]) }}
        </template>

        <template v-else-if="field.type === 'number'">
          {{
            contentSubmissionData[field.key] != null
              ? $n(contentSubmissionData[field.key], 'decimal')
              : '--'
          }}
        </template>

        <template v-else-if="field.type === 'select'">
          {{
            contentSubmissionData[field.key] != null
              ? contentSubmissionData[field.key].getKeyValue()
              : '--'
          }}
        </template>

        <template v-else-if="field.type === 'file'">
          <img
            v-if="contentSubmissionData[field.key]"
            :src="getUrl(contentSubmissionData[field.key])"
            :alt="field.key"
            class="max-h-10 max-w-10"
          />
        </template>

        <template v-else>
          {{ contentSubmissionData[field.key] ?? '--' }}
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
import { ContentSubmission } from '@/models/ContentSubmissionModel'
import EntityRowActions from '@/components/table/EntityRowActions.vue'
import { useDateFormat } from '@/composables/useDateFormat'

const props = defineProps({
  contentSubmission: {
    type: Object as PropType<ContentSubmission>,
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

const contentSubmissionData = computed(() => {
  return props.contentSubmission as Record<string, any>
})

const emit = defineEmits<{
  (e: 'request-delete', contentSubmission: ContentSubmission): void
  (e: 'request-view', contentSubmission: ContentSubmission): void
  (e: 'request-update', contentSubmission: ContentSubmission): void
}>()

const deleteRow = () => emit('request-delete', props.contentSubmission)
const viewRow = () => emit('request-view', props.contentSubmission)
const updateRow = () => emit('request-update', props.contentSubmission)
const { formatDate } = useDateFormat()
</script>
<style scoped></style>
