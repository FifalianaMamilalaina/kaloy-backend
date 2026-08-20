<script setup lang="ts">
import GenesisSelectSearchCriteria from '@/components/form/GenesisSelectSearchCriteria.vue'
import GenesisInput from '@/components/form/GenesisInput.vue'
import { ref, onMounted } from 'vue'
import { Notification, NotificationFormDTO } from '@/models/NotificationModel'
import TrashIcon from '@/components/icons/TrashIcon.vue'

const props = defineProps<{
  internalId: number
  notification: Notification
  violations?: Record<string, string> | null
}>()

const emit = defineEmits<{
  (e: 'request:remove'): void
  (e: 'udpate:model-value', value: NotificationFormDTO): void
}>()

const formModel = ref<NotificationFormDTO>(NotificationFormDTO.parse(props.notification))
const typeidNotificationTypesSearchField =
  Notification.getSearchFieldByKey('typeidNotificationTypes')
const typeidNotificationTypesDefaultValue = props.notification
  ? (props.notification?.typeidNotificationTypes?.getReferenceValue?.() ?? undefined)
  : undefined
const relatedentitytypeidInteractionTargetsSearchField = Notification.getSearchFieldByKey(
  'relatedentitytypeidInteractionTargets',
)
const relatedentitytypeidInteractionTargetsDefaultValue = props.notification
  ? (props.notification?.relatedentitytypeidInteractionTargets?.getReferenceValue?.() ?? undefined)
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
        v-if="typeidNotificationTypesSearchField?.multicriteriaSelect"
        :violation="violations ? violations['typeidNotificationTypes'] : undefined"
        placeholder="Select Typeid notification types"
        key="NotificationTypeidNotificationTypes"
        :search-function="typeidNotificationTypesSearchField?.multicriteriaSelect.searchFunction"
        :filters="typeidNotificationTypesSearchField?.multicriteriaSelect.filters"
        :default-value="typeidNotificationTypesDefaultValue"
        @option-selected="
          (selectedValue) => {
            formModel.typeidNotificationTypes = String(selectedValue)
            updateModel()
          }
        "
      />
    </td>

    <td>
      <GenesisInput
        :violation="violations ? violations['content'] : undefined"
        placeholder="Enter Content"
        type="text"
        v-model="formModel.content"
        :value="formModel.content"
        @update:model-value="(newVal) => updateModel()"
      />
    </td>

    <td>
      <GenesisSelectSearchCriteria
        v-if="relatedentitytypeidInteractionTargetsSearchField?.multicriteriaSelect"
        :violation="violations ? violations['relatedentitytypeidInteractionTargets'] : undefined"
        placeholder="Select Relatedentitytypeid interaction targets"
        key="NotificationRelatedentitytypeidInteractionTargets"
        :search-function="
          relatedentitytypeidInteractionTargetsSearchField?.multicriteriaSelect.searchFunction
        "
        :filters="relatedentitytypeidInteractionTargetsSearchField?.multicriteriaSelect.filters"
        :default-value="relatedentitytypeidInteractionTargetsDefaultValue"
        @option-selected="
          (selectedValue) => {
            formModel.relatedentitytypeidInteractionTargets = String(selectedValue)
            updateModel()
          }
        "
      />
    </td>

    <td>
      <GenesisInput
        :violation="violations ? violations['relatedEntityId'] : undefined"
        placeholder="Enter Related entity id"
        type="number"
        v-model="formModel.relatedEntityId"
        :value="formModel.relatedEntityId"
        @update:model-value="(newVal) => updateModel()"
      />
    </td>

    <td>
      <GenesisInput
        :violation="violations ? violations['isRead'] : undefined"
        placeholder="Enter Is read"
        type="checkbox"
        v-model="formModel.isRead"
        :value="formModel.isRead"
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
