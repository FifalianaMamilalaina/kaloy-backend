<script setup lang="ts">
import GenesisSelectSearchCriteria from '@/components/form/GenesisSelectSearchCriteria.vue'
import GenesisInput from '@/components/form/GenesisInput.vue'
import { ref, onMounted } from 'vue'
import { ContentSubmission, ContentSubmissionFormDTO } from '@/models/ContentSubmissionModel'
import TrashIcon from '@/components/icons/TrashIcon.vue'

const props = defineProps<{
  internalId: number
  contentSubmission: ContentSubmission
  violations?: Record<string, string> | null
}>()

const emit = defineEmits<{
  (e: 'request:remove'): void
  (e: 'udpate:model-value', value: ContentSubmissionFormDTO): void
}>()

const formModel = ref<ContentSubmissionFormDTO>(
  ContentSubmissionFormDTO.parse(props.contentSubmission),
)
const statusidSubmissionStatusesSearchField = ContentSubmission.getSearchFieldByKey(
  'statusidSubmissionStatuses',
)
const statusidSubmissionStatusesDefaultValue = props.contentSubmission
  ? (props.contentSubmission?.statusidSubmissionStatuses?.getReferenceValue?.() ?? undefined)
  : undefined

function removeRow() {
  emit('request:remove')
}

function updateModel() {
  emit('udpate:model-value', formModel.value)
}

onMounted(() => {
  updateModel()
})
</script>

<template>
  <tr>
    <td>
      {{ internalId }}
    </td>
    <td>
      <GenesisInput
        :violation="violations ? violations['sourceFilename'] : undefined"
        placeholder="Enter Source filename"
        type="text"
        v-model="formModel.sourceFilename"
        :value="formModel.sourceFilename"
        @update:model-value="(newVal) => updateModel()"
      />
    </td>

    <td>
      <GenesisInput
        :violation="violations ? violations['fileType'] : undefined"
        placeholder="Enter File type"
        type="text"
        v-model="formModel.fileType"
        :value="formModel.fileType"
        @update:model-value="(newVal) => updateModel()"
      />
    </td>

    <td>
      <GenesisSelectSearchCriteria
        v-if="statusidSubmissionStatusesSearchField?.multicriteriaSelect"
        :violation="violations ? violations['statusidSubmissionStatuses'] : undefined"
        placeholder="Select Statusid submission statuses"
        key="ContentSubmissionStatusidSubmissionStatuses"
        :search-function="statusidSubmissionStatusesSearchField?.multicriteriaSelect.searchFunction"
        :filters="statusidSubmissionStatusesSearchField?.multicriteriaSelect.filters"
        :default-value="statusidSubmissionStatusesDefaultValue"
        @option-selected="
          (selectedValue) => {
            formModel.statusidSubmissionStatuses = String(selectedValue)
            updateModel()
          }
        "
      />
    </td>

    <td>
      <GenesisInput
        :violation="violations ? violations['errorMessage'] : undefined"
        placeholder="Enter Error message"
        type="text"
        v-model="formModel.errorMessage"
        :value="formModel.errorMessage"
        @update:model-value="(newVal) => updateModel()"
      />
    </td>

    <td>
      <GenesisInput
        :violation="violations ? violations['submittedAt'] : undefined"
        placeholder="Enter Submitted at"
        type="datetime-local"
        v-model="formModel.submittedAt"
        :value="formModel.submittedAt"
        @update:model-value="(newVal) => updateModel()"
      />
    </td>

    <td>
      <GenesisInput
        :violation="violations ? violations['processedAt'] : undefined"
        placeholder="Enter Processed at"
        type="datetime-local"
        v-model="formModel.processedAt"
        :value="formModel.processedAt"
        @update:model-value="(newVal) => updateModel()"
      />
    </td>

    <td class="text-center">
      <button type="button" title="delete row" @click="removeRow" class="btn btn-error btn-outline">
        <TrashIcon />
      </button>
    </td>
  </tr>
</template>

<style scoped></style>
