<template>
  <div class="card bg-base-100">
    <div class="card-body">
      <form @submit.prevent="handleSubmit" class="grid grid-cols-1 w-7/12 gap-4 mx-auto">
        <!-- <form @submit.prevent="handleSubmit" class=""> -->
        <div class="grid grid-cols-1 gap-4">
          <!-- Artistid artists FK -->
          <GenesisSelectSearchCriteria
            v-if="artistidArtistsSearchField?.multicriteriaSelect"
            label="Artistid artists"
            key="contentSubmissionArtistidArtists"
            :search-function="artistidArtistsSearchField?.multicriteriaSelect.searchFunction"
            :filters="artistidArtistsSearchField?.multicriteriaSelect.filters"
            :default-value="artistidArtistsDefaultValue"
            :violation="violations ? violations['artistidArtists'] : undefined"
            @option-selected="
              (selectedValue) => (formModel.artistidArtists = String(selectedValue))
            "
          />

          <!-- Source filename -->
          <GenesisInput
            label="Source filename"
            :violation="violations ? violations['sourceFilename'] : undefined"
            placeholder="Enter Source filename"
            type="text"
            v-model="formModel.sourceFilename"
            :value="formModel.sourceFilename"
          />

          <!-- File type -->
          <GenesisInput
            label="File type"
            :violation="violations ? violations['fileType'] : undefined"
            placeholder="Enter File type"
            type="text"
            v-model="formModel.fileType"
            :value="formModel.fileType"
          />

          <!-- Statusid submission statuses FK -->
          <GenesisSelectSearchCriteria
            v-if="statusidSubmissionStatusesSearchField?.multicriteriaSelect"
            label="Statusid submission statuses"
            key="contentSubmissionStatusidSubmissionStatuses"
            :search-function="
              statusidSubmissionStatusesSearchField?.multicriteriaSelect.searchFunction
            "
            :filters="statusidSubmissionStatusesSearchField?.multicriteriaSelect.filters"
            :default-value="statusidSubmissionStatusesDefaultValue"
            :violation="violations ? violations['statusidSubmissionStatuses'] : undefined"
            @option-selected="
              (selectedValue) => (formModel.statusidSubmissionStatuses = String(selectedValue))
            "
          />

          <!-- Error message -->
          <GenesisInput
            label="Error message"
            :violation="violations ? violations['errorMessage'] : undefined"
            placeholder="Enter Error message"
            type="text"
            v-model="formModel.errorMessage"
            :value="formModel.errorMessage"
          />

          <!-- Submitted at -->
          <GenesisInput
            label="Submitted at"
            :violation="violations ? violations['submittedAt'] : undefined"
            placeholder="Enter Submitted at"
            type="datetime-local"
            v-model="formModel.submittedAt"
            :value="formModel.submittedAt"
          />

          <!-- Processed at -->
          <GenesisInput
            label="Processed at"
            :violation="violations ? violations['processedAt'] : undefined"
            placeholder="Enter Processed at"
            type="datetime-local"
            v-model="formModel.processedAt"
            :value="formModel.processedAt"
          />
        </div>

        <!-- Action buttons -->
        <div class="flex justify-end gap-3 mt-6">
          <GenesisButton
            type="submit"
            class="btn btn-primary text-primary-content"
            :label="submitLabel"
          />
          <GenesisButton
            @click="cancelForm"
            class="btn btn-outline btn-error"
            :label="$t('button.cancel')"
          />
        </div>
      </form>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { ContentSubmission, ContentSubmissionFormDTO } from '@/models/ContentSubmissionModel.ts'
import GenesisButton from '@/components/button/GenesisButton.vue'
import GenesisInput from '@/components/form/GenesisInput.vue'
import GenesisSelectSearchCriteria from '@/components/form/GenesisSelectSearchCriteria.vue'

const props = defineProps<{
  contentSubmission?: ContentSubmission
  violations?: Record<string, string> | null
  submitLabel?: string
}>()

const emit = defineEmits<{
  (e: 'submit', payload: Partial<ContentSubmissionFormDTO>): void
  (e: 'cancel', payload: Partial<ContentSubmissionFormDTO>): void
}>()

const formModel = ref<Partial<ContentSubmissionFormDTO>>({
  ...ContentSubmissionFormDTO.parse(props.contentSubmission),
})
const artistidArtistsSearchField = ContentSubmission.getSearchFieldByKey('artistidArtists')
const artistidArtistsDefaultValue = props.contentSubmission
  ? (props.contentSubmission?.artistidArtists?.getKeyValue?.() ?? undefined)
  : undefined
const statusidSubmissionStatusesSearchField = ContentSubmission.getSearchFieldByKey(
  'statusidSubmissionStatuses',
)
const statusidSubmissionStatusesDefaultValue = props.contentSubmission
  ? (props.contentSubmission?.statusidSubmissionStatuses?.getKeyValue?.() ?? undefined)
  : undefined

const fileToBase64 = (file: File): Promise<string> => {
  return new Promise((resolve, reject) => {
    const reader = new FileReader()
    reader.onload = () => {
      if (typeof reader.result !== 'string') {
        reject(new Error('Unable to read the selected file'))
        return
      }
      const commaIndex = reader.result.indexOf(',')
      resolve(commaIndex >= 0 ? reader.result.substring(commaIndex + 1) : reader.result)
    }
    reader.onerror = () => {
      reject(reader.error ?? new Error('Unable to read the selected file'))
    }
    reader.readAsDataURL(file)
  })
}
async function handleSubmit() {
  const data: any = { ...formModel.value }
  Object.keys(data).forEach((key) => {
    if (data[key] === '' || data[key] === null || data[key] === undefined) {
      delete data[key]
    }
  })

  emit('submit', data)
}

function cancelForm() {
  emit('cancel', formModel.value)
}
</script>
<style scoped></style>
