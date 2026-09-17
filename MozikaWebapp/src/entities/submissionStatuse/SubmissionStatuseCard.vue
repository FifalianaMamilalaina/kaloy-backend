<template>
  <div
    class="card bg-base-100 shadow-md border border-base-300 hover:shadow-lg transition-shadow duration-200"
  >
    <div class="card-body p-4">
      <!-- Titre de la carte -->
      <h2 class="card-title text-base font-bold text-primary mb-2 truncate">
        <span> #{{ submissionStatuseData['id'] }} - SubmissionStatuse </span>
      </h2>

      <!-- Corps de la carte : affiche UNIQUEMENT les champs visibles -->
      <div class="space-y-1 text-sm flex-1">
        <template v-for="field in SubmissionStatuse.getAllSearchFieldsMetadata()" :key="field.key">
          <div
            v-if="field.showInTable && visibleFields.includes(field.key)"
            class="flex justify-between gap-2 border-b border-base-200 py-1 last:border-0"
          >
            <span class="font-semibold text-base-content/70 text-xs uppercase tracking-wide">
              {{ field.label }}
            </span>
            <span class="text-right text-base-content truncate max-w-[60%]">
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
            </span>
          </div>
        </template>
      </div>

      <!-- Actions en bas de carte -->
      <div class="card-actions justify-end mt-3 pt-2 border-t border-base-200">
        <EntityRowActions
          :view="viewAction"
          :remove="removeAction"
          :edit="editAction"
          @delete-row="deleteRow"
          @view-row="viewRow"
          @edit-row="updateRow"
        />
      </div>
    </div>
  </div>
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
