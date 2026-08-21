<template>
  <div class="card bg-base-100">
    <div class="card-body">
      <form @submit.prevent="handleSubmit" class="grid grid-cols-1 w-7/12 gap-4 mx-auto">
        <!-- <form @submit.prevent="handleSubmit" class=""> -->
        <div class="grid grid-cols-1 gap-4">
          <!-- Userid users FK -->
          <GenesisSelectSearchCriteria
            v-if="useridUsersSearchField?.multicriteriaSelect"
            label="Userid users"
            key="userStatusHistoryUseridUsers"
            :search-function="useridUsersSearchField?.multicriteriaSelect.searchFunction"
            :filters="useridUsersSearchField?.multicriteriaSelect.filters"
            :default-value="useridUsersDefaultValue"
            :violation="violations ? violations['useridUsers'] : undefined"
            @option-selected="(selectedValue) => (formModel.useridUsers = String(selectedValue))"
          />

          <!-- Previousstatusid user statuses FK -->
          <GenesisSelectSearchCriteria
            v-if="previousstatusidUserStatusesSearchField?.multicriteriaSelect"
            label="Previousstatusid user statuses"
            key="userStatusHistoryPreviousstatusidUserStatuses"
            :search-function="
              previousstatusidUserStatusesSearchField?.multicriteriaSelect.searchFunction
            "
            :filters="previousstatusidUserStatusesSearchField?.multicriteriaSelect.filters"
            :default-value="previousstatusidUserStatusesDefaultValue"
            :violation="violations ? violations['previousstatusidUserStatuses'] : undefined"
            @option-selected="
              (selectedValue) => (formModel.previousstatusidUserStatuses = String(selectedValue))
            "
          />

          <!-- Newstatusid user statuses FK -->
          <GenesisSelectSearchCriteria
            v-if="newstatusidUserStatusesSearchField?.multicriteriaSelect"
            label="Newstatusid user statuses"
            key="userStatusHistoryNewstatusidUserStatuses"
            :search-function="
              newstatusidUserStatusesSearchField?.multicriteriaSelect.searchFunction
            "
            :filters="newstatusidUserStatusesSearchField?.multicriteriaSelect.filters"
            :default-value="newstatusidUserStatusesDefaultValue"
            :violation="violations ? violations['newstatusidUserStatuses'] : undefined"
            @option-selected="
              (selectedValue) => (formModel.newstatusidUserStatuses = String(selectedValue))
            "
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

          <!-- Changedbyuserid users FK -->
          <GenesisSelectSearchCriteria
            v-if="changedbyuseridUsersSearchField?.multicriteriaSelect"
            label="Changedbyuserid users"
            key="userStatusHistoryChangedbyuseridUsers"
            :search-function="changedbyuseridUsersSearchField?.multicriteriaSelect.searchFunction"
            :filters="changedbyuseridUsersSearchField?.multicriteriaSelect.filters"
            :default-value="changedbyuseridUsersDefaultValue"
            :violation="violations ? violations['changedbyuseridUsers'] : undefined"
            @option-selected="
              (selectedValue) => (formModel.changedbyuseridUsers = String(selectedValue))
            "
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
import { UserStatusHistory, UserStatusHistoryFormDTO } from '@/models/UserStatusHistoryModel.ts'
import GenesisButton from '@/components/button/GenesisButton.vue'
import GenesisInput from '@/components/form/GenesisInput.vue'
import GenesisSelectSearchCriteria from '@/components/form/GenesisSelectSearchCriteria.vue'

const props = defineProps<{
  userStatusHistory?: UserStatusHistory
  violations?: Record<string, string> | null
  submitLabel?: string
}>()

const emit = defineEmits<{
  (e: 'submit', payload: Partial<UserStatusHistoryFormDTO>): void
  (e: 'cancel', payload: Partial<UserStatusHistoryFormDTO>): void
}>()

const formModel = ref<Partial<UserStatusHistoryFormDTO>>({
  ...UserStatusHistoryFormDTO.parse(props.userStatusHistory),
})
const useridUsersSearchField = UserStatusHistory.getSearchFieldByKey('useridUsers')
const useridUsersDefaultValue = props.userStatusHistory
  ? (props.userStatusHistory?.useridUsers?.getKeyValue?.() ?? undefined)
  : undefined
const previousstatusidUserStatusesSearchField = UserStatusHistory.getSearchFieldByKey(
  'previousstatusidUserStatuses',
)
const previousstatusidUserStatusesDefaultValue = props.userStatusHistory
  ? (props.userStatusHistory?.previousstatusidUserStatuses?.getKeyValue?.() ?? undefined)
  : undefined
const newstatusidUserStatusesSearchField =
  UserStatusHistory.getSearchFieldByKey('newstatusidUserStatuses')
const newstatusidUserStatusesDefaultValue = props.userStatusHistory
  ? (props.userStatusHistory?.newstatusidUserStatuses?.getKeyValue?.() ?? undefined)
  : undefined
const changedbyuseridUsersSearchField =
  UserStatusHistory.getSearchFieldByKey('changedbyuseridUsers')
const changedbyuseridUsersDefaultValue = props.userStatusHistory
  ? (props.userStatusHistory?.changedbyuseridUsers?.getKeyValue?.() ?? undefined)
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
