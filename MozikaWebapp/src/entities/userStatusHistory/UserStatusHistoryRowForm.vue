<script setup lang="ts">
import GenesisSelectSearchCriteria from '@/components/form/GenesisSelectSearchCriteria.vue'
import GenesisInput from '@/components/form/GenesisInput.vue'
import { ref, onMounted } from 'vue'
import { UserStatusHistory, UserStatusHistoryFormDTO } from '@/models/UserStatusHistoryModel'
import TrashIcon from '@/components/icons/TrashIcon.vue'

const props = defineProps<{
  internalId: number
  userStatusHistory: UserStatusHistory
  violations?: Record<string, string> | null
}>()

const emit = defineEmits<{
  (e: 'request:remove'): void
  (e: 'udpate:model-value', value: UserStatusHistoryFormDTO): void
}>()

const formModel = ref<UserStatusHistoryFormDTO>(
  UserStatusHistoryFormDTO.parse(props.userStatusHistory),
)
const previousstatusidUserStatusesSearchField = UserStatusHistory.getSearchFieldByKey(
  'previousstatusidUserStatuses',
)
const previousstatusidUserStatusesDefaultValue = props.userStatusHistory
  ? (props.userStatusHistory?.previousstatusidUserStatuses?.getReferenceValue?.() ?? undefined)
  : undefined
const newstatusidUserStatusesSearchField =
  UserStatusHistory.getSearchFieldByKey('newstatusidUserStatuses')
const newstatusidUserStatusesDefaultValue = props.userStatusHistory
  ? (props.userStatusHistory?.newstatusidUserStatuses?.getReferenceValue?.() ?? undefined)
  : undefined
const changedbyuseridUsersSearchField =
  UserStatusHistory.getSearchFieldByKey('changedbyuseridUsers')
const changedbyuseridUsersDefaultValue = props.userStatusHistory
  ? (props.userStatusHistory?.changedbyuseridUsers?.getReferenceValue?.() ?? undefined)
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
        v-if="previousstatusidUserStatusesSearchField?.multicriteriaSelect"
        :violation="violations ? violations['previousstatusidUserStatuses'] : undefined"
        placeholder="Select Previousstatusid user statuses"
        key="UserStatusHistoryPreviousstatusidUserStatuses"
        :search-function="
          previousstatusidUserStatusesSearchField?.multicriteriaSelect.searchFunction
        "
        :filters="previousstatusidUserStatusesSearchField?.multicriteriaSelect.filters"
        :default-value="previousstatusidUserStatusesDefaultValue"
        @option-selected="
          (selectedValue) => {
            formModel.previousstatusidUserStatuses = String(selectedValue)
            updateModel()
          }
        "
      />
    </td>

    <td>
      <GenesisSelectSearchCriteria
        v-if="newstatusidUserStatusesSearchField?.multicriteriaSelect"
        :violation="violations ? violations['newstatusidUserStatuses'] : undefined"
        placeholder="Select Newstatusid user statuses"
        key="UserStatusHistoryNewstatusidUserStatuses"
        :search-function="newstatusidUserStatusesSearchField?.multicriteriaSelect.searchFunction"
        :filters="newstatusidUserStatusesSearchField?.multicriteriaSelect.filters"
        :default-value="newstatusidUserStatusesDefaultValue"
        @option-selected="
          (selectedValue) => {
            formModel.newstatusidUserStatuses = String(selectedValue)
            updateModel()
          }
        "
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
        v-if="changedbyuseridUsersSearchField?.multicriteriaSelect"
        :violation="violations ? violations['changedbyuseridUsers'] : undefined"
        placeholder="Select Changedbyuserid users"
        key="UserStatusHistoryChangedbyuseridUsers"
        :search-function="changedbyuseridUsersSearchField?.multicriteriaSelect.searchFunction"
        :filters="changedbyuseridUsersSearchField?.multicriteriaSelect.filters"
        :default-value="changedbyuseridUsersDefaultValue"
        @option-selected="
          (selectedValue) => {
            formModel.changedbyuseridUsers = String(selectedValue)
            updateModel()
          }
        "
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
