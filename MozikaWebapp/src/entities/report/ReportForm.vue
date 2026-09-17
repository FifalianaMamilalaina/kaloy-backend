<template>
  <div class="card bg-base-100">
    <div class="card-body">
      <form @submit.prevent="handleSubmit" class="grid grid-cols-1 w-7/12 gap-4 mx-auto">
        <!-- <form @submit.prevent="handleSubmit" class=""> -->
        <div class="grid grid-cols-1 gap-4">
          <!-- Reporteruserid users FK -->
          <GenesisSelectSearchCriteria
            v-if="reporteruseridUsersSearchField?.multicriteriaSelect"
            label="Reporteruserid users"
            key="reportReporteruseridUsers"
            :search-function="reporteruseridUsersSearchField?.multicriteriaSelect.searchFunction"
            :filters="reporteruseridUsersSearchField?.multicriteriaSelect.filters"
            :default-value="reporteruseridUsersDefaultValue"
            :violation="violations ? violations['reporteruseridUsers'] : undefined"
            @option-selected="
              (selectedValue) => (formModel.reporteruseridUsers = String(selectedValue))
            "
          />

          <!-- Targettypeid interaction targets FK -->
          <GenesisSelectSearchCriteria
            v-if="targettypeidInteractionTargetsSearchField?.multicriteriaSelect"
            label="Targettypeid interaction targets"
            key="reportTargettypeidInteractionTargets"
            :search-function="
              targettypeidInteractionTargetsSearchField?.multicriteriaSelect.searchFunction
            "
            :filters="targettypeidInteractionTargetsSearchField?.multicriteriaSelect.filters"
            :default-value="targettypeidInteractionTargetsDefaultValue"
            :violation="violations ? violations['targettypeidInteractionTargets'] : undefined"
            @option-selected="
              (selectedValue) => (formModel.targettypeidInteractionTargets = String(selectedValue))
            "
          />

          <!-- Target id -->
          <GenesisInput
            label="Target id"
            :violation="violations ? violations['targetId'] : undefined"
            placeholder="Enter Target id"
            type="number"
            v-model="formModel.targetId"
            :value="formModel.targetId"
          />

          <!-- Reason -->
          <GenesisInput
            label="Reason"
            :violation="violations ? violations['reason'] : undefined"
            placeholder="Enter Reason"
            type="text"
            v-model="formModel.reason"
            :value="formModel.reason"
          />

          <!-- Statusid report statuses FK -->
          <GenesisSelectSearchCriteria
            v-if="statusidReportStatusesSearchField?.multicriteriaSelect"
            label="Statusid report statuses"
            key="reportStatusidReportStatuses"
            :search-function="statusidReportStatusesSearchField?.multicriteriaSelect.searchFunction"
            :filters="statusidReportStatusesSearchField?.multicriteriaSelect.filters"
            :default-value="statusidReportStatusesDefaultValue"
            :violation="violations ? violations['statusidReportStatuses'] : undefined"
            @option-selected="
              (selectedValue) => (formModel.statusidReportStatuses = String(selectedValue))
            "
          />

          <!-- Reviewed at -->
          <GenesisInput
            label="Reviewed at"
            :violation="violations ? violations['reviewedAt'] : undefined"
            placeholder="Enter Reviewed at"
            type="datetime-local"
            v-model="formModel.reviewedAt"
            :value="formModel.reviewedAt"
          />

          <!-- Created at -->
          <GenesisInput
            label="Created at"
            :violation="violations ? violations['createdAt'] : undefined"
            placeholder="Enter Created at"
            type="datetime-local"
            v-model="formModel.createdAt"
            :value="formModel.createdAt"
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
import { Report, ReportFormDTO } from '@/models/ReportModel.ts'
import GenesisButton from '@/components/button/GenesisButton.vue'
import GenesisInput from '@/components/form/GenesisInput.vue'
import GenesisSelectSearchCriteria from '@/components/form/GenesisSelectSearchCriteria.vue'

const props = defineProps<{
  report?: Report
  violations?: Record<string, string> | null
  submitLabel?: string
}>()

const emit = defineEmits<{
  (e: 'submit', payload: Partial<ReportFormDTO>): void
  (e: 'cancel', payload: Partial<ReportFormDTO>): void
}>()

const formModel = ref<Partial<ReportFormDTO>>({ ...ReportFormDTO.parse(props.report) })
const reporteruseridUsersSearchField = Report.getSearchFieldByKey('reporteruseridUsers')
const reporteruseridUsersDefaultValue = props.report
  ? (props.report?.reporteruseridUsers?.getKeyValue?.() ?? undefined)
  : undefined
const targettypeidInteractionTargetsSearchField = Report.getSearchFieldByKey(
  'targettypeidInteractionTargets',
)
const targettypeidInteractionTargetsDefaultValue = props.report
  ? (props.report?.targettypeidInteractionTargets?.getKeyValue?.() ?? undefined)
  : undefined
const statusidReportStatusesSearchField = Report.getSearchFieldByKey('statusidReportStatuses')
const statusidReportStatusesDefaultValue = props.report
  ? (props.report?.statusidReportStatuses?.getKeyValue?.() ?? undefined)
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
