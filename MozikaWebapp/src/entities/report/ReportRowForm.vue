<script setup lang="ts">
import GenesisSelectSearchCriteria from '@/components/form/GenesisSelectSearchCriteria.vue'
import GenesisInput from '@/components/form/GenesisInput.vue'
import { ref, onMounted } from 'vue'
import { Report, ReportFormDTO } from '@/models/ReportModel'
import TrashIcon from '@/components/icons/TrashIcon.vue'

const props = defineProps<{
  internalId: number
  report: Report
  violations?: Record<string, string> | null
}>()

const emit = defineEmits<{
  (e: 'request:remove'): void
  (e: 'udpate:model-value', value: ReportFormDTO): void
}>()

const formModel = ref<ReportFormDTO>(ReportFormDTO.parse(props.report))
const targettypeidInteractionTargetsSearchField = Report.getSearchFieldByKey(
  'targettypeidInteractionTargets',
)
const targettypeidInteractionTargetsDefaultValue = props.report
  ? (props.report?.targettypeidInteractionTargets?.getReferenceValue?.() ?? undefined)
  : undefined
const statusidReportStatusesSearchField = Report.getSearchFieldByKey('statusidReportStatuses')
const statusidReportStatusesDefaultValue = props.report
  ? (props.report?.statusidReportStatuses?.getReferenceValue?.() ?? undefined)
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
      <GenesisSelectSearchCriteria
        v-if="targettypeidInteractionTargetsSearchField?.multicriteriaSelect"
        :violation="violations ? violations['targettypeidInteractionTargets'] : undefined"
        placeholder="Select Targettypeid interaction targets"
        key="ReportTargettypeidInteractionTargets"
        :search-function="
          targettypeidInteractionTargetsSearchField?.multicriteriaSelect.searchFunction
        "
        :filters="targettypeidInteractionTargetsSearchField?.multicriteriaSelect.filters"
        :default-value="targettypeidInteractionTargetsDefaultValue"
        @option-selected="
          (selectedValue) => {
            formModel.targettypeidInteractionTargets = String(selectedValue)
            updateModel()
          }
        "
      />
    </td>

    <td>
      <GenesisInput
        :violation="violations ? violations['targetId'] : undefined"
        placeholder="Enter Target id"
        type="number"
        v-model="formModel.targetId"
        :value="formModel.targetId"
        @update:model-value="(newVal) => updateModel()"
      />
    </td>

    <td>
      <GenesisInput
        :violation="violations ? violations['reason'] : undefined"
        placeholder="Enter Reason"
        type="text"
        v-model="formModel.reason"
        :value="formModel.reason"
        @update:model-value="(newVal) => updateModel()"
      />
    </td>

    <td>
      <GenesisSelectSearchCriteria
        v-if="statusidReportStatusesSearchField?.multicriteriaSelect"
        :violation="violations ? violations['statusidReportStatuses'] : undefined"
        placeholder="Select Statusid report statuses"
        key="ReportStatusidReportStatuses"
        :search-function="statusidReportStatusesSearchField?.multicriteriaSelect.searchFunction"
        :filters="statusidReportStatusesSearchField?.multicriteriaSelect.filters"
        :default-value="statusidReportStatusesDefaultValue"
        @option-selected="
          (selectedValue) => {
            formModel.statusidReportStatuses = String(selectedValue)
            updateModel()
          }
        "
      />
    </td>

    <td>
      <GenesisInput
        :violation="violations ? violations['reviewedAt'] : undefined"
        placeholder="Enter Reviewed at"
        type="datetime-local"
        v-model="formModel.reviewedAt"
        :value="formModel.reviewedAt"
        @update:model-value="(newVal) => updateModel()"
      />
    </td>

    <td>
      <GenesisInput
        :violation="violations ? violations['createdAt'] : undefined"
        placeholder="Enter Created at"
        type="datetime-local"
        v-model="formModel.createdAt"
        :value="formModel.createdAt"
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
