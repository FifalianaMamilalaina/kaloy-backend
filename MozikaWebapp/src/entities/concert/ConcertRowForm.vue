<script setup lang="ts">
import GenesisSelectSearchCriteria from '@/components/form/GenesisSelectSearchCriteria.vue'
import GenesisInput from '@/components/form/GenesisInput.vue'
import { ref, onMounted } from 'vue'
import { Concert, ConcertFormDTO } from '@/models/ConcertModel'
import TrashIcon from '@/components/icons/TrashIcon.vue'

const props = defineProps<{
  internalId: number
  concert: Concert
  violations?: Record<string, string> | null
}>()

const emit = defineEmits<{
  (e: 'request:remove'): void
  (e: 'udpate:model-value', value: ConcertFormDTO): void
}>()

const formModel = ref<ConcertFormDTO>(ConcertFormDTO.parse(props.concert))
const statusidParticipationStatusesSearchField = Concert.getSearchFieldByKey(
  'statusidParticipationStatuses',
)
const statusidParticipationStatusesDefaultValue = props.concert
  ? (props.concert?.statusidParticipationStatuses?.getReferenceValue?.() ?? undefined)
  : undefined
const createdbyartistidArtistsSearchField = Concert.getSearchFieldByKey('createdbyartistidArtists')
const createdbyartistidArtistsDefaultValue = props.concert
  ? (props.concert?.createdbyartistidArtists?.getReferenceValue?.() ?? undefined)
  : undefined
const moderationstatusidEventModerationStatusesSearchField = Concert.getSearchFieldByKey(
  'moderationstatusidEventModerationStatuses',
)
const moderationstatusidEventModerationStatusesDefaultValue = props.concert
  ? (props.concert?.moderationstatusidEventModerationStatuses?.getReferenceValue?.() ?? undefined)
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
        :violation="violations ? violations['title'] : undefined"
        placeholder="Enter Title"
        type="text"
        v-model="formModel.title"
        :value="formModel.title"
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
        :violation="violations ? violations['startTime'] : undefined"
        placeholder="Enter Start time"
        type="datetime-local"
        v-model="formModel.startTime"
        :value="formModel.startTime"
        @update:model-value="(newVal) => updateModel()"
      />
    </td>

    <td>
      <GenesisInput
        :violation="violations ? violations['endTime'] : undefined"
        placeholder="Enter End time"
        type="datetime-local"
        v-model="formModel.endTime"
        :value="formModel.endTime"
        @update:model-value="(newVal) => updateModel()"
      />
    </td>

    <td>
      <GenesisSelectSearchCriteria
        v-if="statusidParticipationStatusesSearchField?.multicriteriaSelect"
        :violation="violations ? violations['statusidParticipationStatuses'] : undefined"
        placeholder="Select Statusid participation statuses"
        key="ConcertStatusidParticipationStatuses"
        :search-function="
          statusidParticipationStatusesSearchField?.multicriteriaSelect.searchFunction
        "
        :filters="statusidParticipationStatusesSearchField?.multicriteriaSelect.filters"
        :default-value="statusidParticipationStatusesDefaultValue"
        @option-selected="
          (selectedValue) => {
            formModel.statusidParticipationStatuses = String(selectedValue)
            updateModel()
          }
        "
      />
    </td>

    <td>
      <GenesisInput
        :violation="violations ? violations['respondedAt'] : undefined"
        placeholder="Enter Responded at"
        type="datetime-local"
        v-model="formModel.respondedAt"
        :value="formModel.respondedAt"
        @update:model-value="(newVal) => updateModel()"
      />
    </td>

    <td>
      <GenesisSelectSearchCriteria
        v-if="createdbyartistidArtistsSearchField?.multicriteriaSelect"
        :violation="violations ? violations['createdbyartistidArtists'] : undefined"
        placeholder="Select Createdbyartistid artists"
        key="ConcertCreatedbyartistidArtists"
        :search-function="createdbyartistidArtistsSearchField?.multicriteriaSelect.searchFunction"
        :filters="createdbyartistidArtistsSearchField?.multicriteriaSelect.filters"
        :default-value="createdbyartistidArtistsDefaultValue"
        @option-selected="
          (selectedValue) => {
            formModel.createdbyartistidArtists = String(selectedValue)
            updateModel()
          }
        "
      />
    </td>

    <td>
      <GenesisSelectSearchCriteria
        v-if="moderationstatusidEventModerationStatusesSearchField?.multicriteriaSelect"
        :violation="
          violations ? violations['moderationstatusidEventModerationStatuses'] : undefined
        "
        placeholder="Select Moderationstatusid event moderation statuses"
        key="ConcertModerationstatusidEventModerationStatuses"
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
