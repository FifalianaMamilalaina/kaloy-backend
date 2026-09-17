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
            key="notificationUseridUsers"
            :search-function="useridUsersSearchField?.multicriteriaSelect.searchFunction"
            :filters="useridUsersSearchField?.multicriteriaSelect.filters"
            :default-value="useridUsersDefaultValue"
            :violation="violations ? violations['useridUsers'] : undefined"
            @option-selected="(selectedValue) => (formModel.useridUsers = String(selectedValue))"
          />

          <!-- Typeid notification types FK -->
          <GenesisSelectSearchCriteria
            v-if="typeidNotificationTypesSearchField?.multicriteriaSelect"
            label="Typeid notification types"
            key="notificationTypeidNotificationTypes"
            :search-function="
              typeidNotificationTypesSearchField?.multicriteriaSelect.searchFunction
            "
            :filters="typeidNotificationTypesSearchField?.multicriteriaSelect.filters"
            :default-value="typeidNotificationTypesDefaultValue"
            :violation="violations ? violations['typeidNotificationTypes'] : undefined"
            @option-selected="
              (selectedValue) => (formModel.typeidNotificationTypes = String(selectedValue))
            "
          />

          <!-- Content -->
          <GenesisInput
            label="Content"
            :violation="violations ? violations['content'] : undefined"
            placeholder="Enter Content"
            type="text"
            v-model="formModel.content"
            :value="formModel.content"
          />

          <!-- Relatedentitytypeid interaction targets FK -->
          <GenesisSelectSearchCriteria
            v-if="relatedentitytypeidInteractionTargetsSearchField?.multicriteriaSelect"
            label="Relatedentitytypeid interaction targets"
            key="notificationRelatedentitytypeidInteractionTargets"
            :search-function="
              relatedentitytypeidInteractionTargetsSearchField?.multicriteriaSelect.searchFunction
            "
            :filters="relatedentitytypeidInteractionTargetsSearchField?.multicriteriaSelect.filters"
            :default-value="relatedentitytypeidInteractionTargetsDefaultValue"
            :violation="
              violations ? violations['relatedentitytypeidInteractionTargets'] : undefined
            "
            @option-selected="
              (selectedValue) =>
                (formModel.relatedentitytypeidInteractionTargets = String(selectedValue))
            "
          />

          <!-- Related entity id -->
          <GenesisInput
            label="Related entity id"
            :violation="violations ? violations['relatedEntityId'] : undefined"
            placeholder="Enter Related entity id"
            type="number"
            v-model="formModel.relatedEntityId"
            :value="formModel.relatedEntityId"
          />

          <!-- Is read -->
          <label class="flex items-center gap-2">
            <input type="checkbox" class="checkbox" v-model="formModel.isRead" />
            <span>Is read</span>
          </label>

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
import { Notification, NotificationFormDTO } from '@/models/NotificationModel.ts'
import GenesisButton from '@/components/button/GenesisButton.vue'
import GenesisInput from '@/components/form/GenesisInput.vue'
import GenesisSelectSearchCriteria from '@/components/form/GenesisSelectSearchCriteria.vue'

const props = defineProps<{
  notification?: Notification
  violations?: Record<string, string> | null
  submitLabel?: string
}>()

const emit = defineEmits<{
  (e: 'submit', payload: Partial<NotificationFormDTO>): void
  (e: 'cancel', payload: Partial<NotificationFormDTO>): void
}>()

const formModel = ref<Partial<NotificationFormDTO>>({
  ...NotificationFormDTO.parse(props.notification),
})
const useridUsersSearchField = Notification.getSearchFieldByKey('useridUsers')
const useridUsersDefaultValue = props.notification
  ? (props.notification?.useridUsers?.getKeyValue?.() ?? undefined)
  : undefined
const typeidNotificationTypesSearchField =
  Notification.getSearchFieldByKey('typeidNotificationTypes')
const typeidNotificationTypesDefaultValue = props.notification
  ? (props.notification?.typeidNotificationTypes?.getKeyValue?.() ?? undefined)
  : undefined
const relatedentitytypeidInteractionTargetsSearchField = Notification.getSearchFieldByKey(
  'relatedentitytypeidInteractionTargets',
)
const relatedentitytypeidInteractionTargetsDefaultValue = props.notification
  ? (props.notification?.relatedentitytypeidInteractionTargets?.getKeyValue?.() ?? undefined)
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
