<script setup lang="ts">
import GenesisSelectSearchCriteria from '@/components/form/GenesisSelectSearchCriteria.vue'
import GenesisInput from '@/components/form/GenesisInput.vue'
import { ref, onMounted } from 'vue'
import { Event, EventFormDTO } from '@/models/EventModel'
import TrashIcon from '@/components/icons/TrashIcon.vue'

const props = defineProps<{
  internalId: number
  event: Event
  violations?: Record<string, string> | null
}>()

const emit = defineEmits<{
  (e: 'request:remove'): void
  (e: 'udpate:model-value', value: EventFormDTO): void
}>()

const formModel = ref<EventFormDTO>(EventFormDTO.parse(props.event))
const moderationstatusidEventModerationStatusesSearchField = Event.getSearchFieldByKey(
  'moderationstatusidEventModerationStatuses',
)
const moderationstatusidEventModerationStatusesDefaultValue = props.event
  ? (props.event?.moderationstatusidEventModerationStatuses?.getReferenceValue?.() ?? undefined)
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
        :violation="violations ? violations['name'] : undefined"
        placeholder="Enter Name"
        type="text"
        v-model="formModel.name"
        :value="formModel.name"
        @update:model-value="(newVal) => updateModel()"
      />
    </td>

    <td>
      <GenesisInput
        :violation="violations ? violations['description'] : undefined"
        placeholder="Enter Description"
        type="text"
        v-model="formModel.description"
        :value="formModel.description"
        @update:model-value="(newVal) => updateModel()"
      />
    </td>

    <td>
      <GenesisInput
        :violation="violations ? violations['startDate'] : undefined"
        placeholder="Enter Start date"
        type="date"
        v-model="formModel.startDate"
        :value="formModel.startDate"
        @update:model-value="(newVal) => updateModel()"
      />
    </td>

    <td>
      <GenesisInput
        :violation="violations ? violations['endDate'] : undefined"
        placeholder="Enter End date"
        type="date"
        v-model="formModel.endDate"
        :value="formModel.endDate"
        @update:model-value="(newVal) => updateModel()"
      />
    </td>

    <td>
      <GenesisSelectSearchCriteria
        v-if="moderationstatusidEventModerationStatusesSearchField?.multicriteriaSelect"
        :violation="
          violations ? violations['moderationstatusidEventModerationStatuses'] : undefined
        "
        placeholder="Select Moderationstatusid event moderation statuses"
        key="EventModerationstatusidEventModerationStatuses"
        :search-function="
          moderationstatusidEventModerationStatusesSearchField?.multicriteriaSelect.searchFunction
        "
        :filters="moderationstatusidEventModerationStatusesSearchField?.multicriteriaSelect.filters"
        :default-value="moderationstatusidEventModerationStatusesDefaultValue"
        @option-selected="
          (selectedValue) => {
            formModel.moderationstatusidEventModerationStatuses = String(selectedValue)
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
