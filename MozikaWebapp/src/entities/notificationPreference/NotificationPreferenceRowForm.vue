<script setup lang="ts">
import GenesisSelectSearchCriteria from '@/components/form/GenesisSelectSearchCriteria.vue'
import GenesisInput from '@/components/form/GenesisInput.vue'
import { ref, onMounted } from 'vue'
import {
  NotificationPreference,
  NotificationPreferenceFormDTO,
} from '@/models/NotificationPreferenceModel'
import TrashIcon from '@/components/icons/TrashIcon.vue'

const props = defineProps<{
  internalId: number
  notificationPreference: NotificationPreference
  violations?: Record<string, string> | null
}>()

const emit = defineEmits<{
  (e: 'request:remove'): void
  (e: 'udpate:model-value', value: NotificationPreferenceFormDTO): void
}>()

const formModel = ref<NotificationPreferenceFormDTO>(
  NotificationPreferenceFormDTO.parse(props.notificationPreference),
)
const notificationtypeidNotificationTypesSearchField = NotificationPreference.getSearchFieldByKey(
  'notificationtypeidNotificationTypes',
)
const notificationtypeidNotificationTypesDefaultValue = props.notificationPreference
  ? (props.notificationPreference?.notificationtypeidNotificationTypes?.getReferenceValue?.() ??
    undefined)
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
        v-if="notificationtypeidNotificationTypesSearchField?.multicriteriaSelect"
        :violation="violations ? violations['notificationtypeidNotificationTypes'] : undefined"
        placeholder="Select Notificationtypeid notification types"
        key="NotificationPreferenceNotificationtypeidNotificationTypes"
        :search-function="
          notificationtypeidNotificationTypesSearchField?.multicriteriaSelect.searchFunction
        "
        :filters="notificationtypeidNotificationTypesSearchField?.multicriteriaSelect.filters"
        :default-value="notificationtypeidNotificationTypesDefaultValue"
        @option-selected="
          (selectedValue) => {
            formModel.notificationtypeidNotificationTypes = String(selectedValue)
            updateModel()
          }
        "
      />
    </td>

    <td>
      <GenesisInput
        :violation="violations ? violations['isEnabled'] : undefined"
        placeholder="Enter Is enabled"
        type="checkbox"
        v-model="formModel.isEnabled"
        :value="formModel.isEnabled"
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
