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
            key="notificationPreferenceUseridUsers"
            :search-function="useridUsersSearchField?.multicriteriaSelect.searchFunction"
            :filters="useridUsersSearchField?.multicriteriaSelect.filters"
            :default-value="useridUsersDefaultValue"
            :violation="violations ? violations['useridUsers'] : undefined"
            @option-selected="(selectedValue) => (formModel.useridUsers = String(selectedValue))"
          />

          <!-- Notificationtypeid notification types FK -->
          <GenesisSelectSearchCriteria
            v-if="notificationtypeidNotificationTypesSearchField?.multicriteriaSelect"
            label="Notificationtypeid notification types"
            key="notificationPreferenceNotificationtypeidNotificationTypes"
            :search-function="
              notificationtypeidNotificationTypesSearchField?.multicriteriaSelect.searchFunction
            "
            :filters="notificationtypeidNotificationTypesSearchField?.multicriteriaSelect.filters"
            :default-value="notificationtypeidNotificationTypesDefaultValue"
            :violation="violations ? violations['notificationtypeidNotificationTypes'] : undefined"
            @option-selected="
              (selectedValue) =>
                (formModel.notificationtypeidNotificationTypes = String(selectedValue))
            "
          />

          <!-- Is enabled -->
          <label class="flex items-center gap-2">
            <input type="checkbox" class="checkbox" v-model="formModel.isEnabled" />
            <span>Is enabled</span>
          </label>
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
import {
  NotificationPreference,
  NotificationPreferenceFormDTO,
} from '@/models/NotificationPreferenceModel.ts'
import GenesisButton from '@/components/button/GenesisButton.vue'
import GenesisInput from '@/components/form/GenesisInput.vue'
import GenesisSelectSearchCriteria from '@/components/form/GenesisSelectSearchCriteria.vue'

const props = defineProps<{
  notificationPreference?: NotificationPreference
  violations?: Record<string, string> | null
  submitLabel?: string
}>()

const emit = defineEmits<{
  (e: 'submit', payload: Partial<NotificationPreferenceFormDTO>): void
  (e: 'cancel', payload: Partial<NotificationPreferenceFormDTO>): void
}>()

const formModel = ref<Partial<NotificationPreferenceFormDTO>>({
  ...NotificationPreferenceFormDTO.parse(props.notificationPreference),
})
const useridUsersSearchField = NotificationPreference.getSearchFieldByKey('useridUsers')
const useridUsersDefaultValue = props.notificationPreference
  ? (props.notificationPreference?.useridUsers?.getKeyValue?.() ?? undefined)
  : undefined
const notificationtypeidNotificationTypesSearchField = NotificationPreference.getSearchFieldByKey(
  'notificationtypeidNotificationTypes',
)
const notificationtypeidNotificationTypesDefaultValue = props.notificationPreference
  ? (props.notificationPreference?.notificationtypeidNotificationTypes?.getKeyValue?.() ??
    undefined)
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
