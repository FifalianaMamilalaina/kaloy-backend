<template>
  <div class="card bg-base-100">
    <div class="card-body">
      <form @submit.prevent="handleSubmit" class="grid grid-cols-1 w-7/12 gap-4 mx-auto">
        <!-- <form @submit.prevent="handleSubmit" class=""> -->
        <div class="grid grid-cols-1 gap-4">
          <!-- Eventid events FK -->
          <GenesisSelectSearchCriteria
            v-if="eventidEventsSearchField?.multicriteriaSelect"
            label="Eventid events"
            key="concertEventidEvents"
            :search-function="eventidEventsSearchField?.multicriteriaSelect.searchFunction"
            :filters="eventidEventsSearchField?.multicriteriaSelect.filters"
            :default-value="eventidEventsDefaultValue"
            :violation="violations ? violations['eventidEvents'] : undefined"
            @option-selected="(selectedValue) => (formModel.eventidEvents = String(selectedValue))"
          />

          <!-- Title -->
          <GenesisInput
            label="Title"
            :violation="violations ? violations['title'] : undefined"
            placeholder="Enter Title"
            type="text"
            v-model="formModel.title"
            :value="formModel.title"
          />

          <!-- Description -->
          <GenesisInput
            label="Description"
            :violation="violations ? violations['description'] : undefined"
            placeholder="Enter Description"
            type="text"
            v-model="formModel.description"
            :value="formModel.description"
          />

          <!-- Artistid artists FK -->
          <GenesisSelectSearchCriteria
            v-if="artistidArtistsSearchField?.multicriteriaSelect"
            label="Artistid artists"
            key="concertArtistidArtists"
            :search-function="artistidArtistsSearchField?.multicriteriaSelect.searchFunction"
            :filters="artistidArtistsSearchField?.multicriteriaSelect.filters"
            :default-value="artistidArtistsDefaultValue"
            :violation="violations ? violations['artistidArtists'] : undefined"
            @option-selected="
              (selectedValue) => (formModel.artistidArtists = String(selectedValue))
            "
          />

          <!-- Venueid venues FK -->
          <GenesisSelectSearchCriteria
            v-if="venueidVenuesSearchField?.multicriteriaSelect"
            label="Venueid venues"
            key="concertVenueidVenues"
            :search-function="venueidVenuesSearchField?.multicriteriaSelect.searchFunction"
            :filters="venueidVenuesSearchField?.multicriteriaSelect.filters"
            :default-value="venueidVenuesDefaultValue"
            :violation="violations ? violations['venueidVenues'] : undefined"
            @option-selected="(selectedValue) => (formModel.venueidVenues = String(selectedValue))"
          />

          <!-- Start time -->
          <GenesisInput
            label="Start time"
            :violation="violations ? violations['startTime'] : undefined"
            placeholder="Enter Start time"
            type="datetime-local"
            v-model="formModel.startTime"
            :value="formModel.startTime"
          />

          <!-- End time -->
          <GenesisInput
            label="End time"
            :violation="violations ? violations['endTime'] : undefined"
            placeholder="Enter End time"
            type="datetime-local"
            v-model="formModel.endTime"
            :value="formModel.endTime"
          />

          <!-- Statusid participation statuses FK -->
          <GenesisSelectSearchCriteria
            v-if="statusidParticipationStatusesSearchField?.multicriteriaSelect"
            label="Statusid participation statuses"
            key="concertStatusidParticipationStatuses"
            :search-function="
              statusidParticipationStatusesSearchField?.multicriteriaSelect.searchFunction
            "
            :filters="statusidParticipationStatusesSearchField?.multicriteriaSelect.filters"
            :default-value="statusidParticipationStatusesDefaultValue"
            :violation="violations ? violations['statusidParticipationStatuses'] : undefined"
            @option-selected="
              (selectedValue) => (formModel.statusidParticipationStatuses = String(selectedValue))
            "
          />

          <!-- Responded at -->
          <GenesisInput
            label="Responded at"
            :violation="violations ? violations['respondedAt'] : undefined"
            placeholder="Enter Responded at"
            type="datetime-local"
            v-model="formModel.respondedAt"
            :value="formModel.respondedAt"
          />

          <!-- Createdbyartistid artists FK -->
          <GenesisSelectSearchCriteria
            v-if="createdbyartistidArtistsSearchField?.multicriteriaSelect"
            label="Createdbyartistid artists"
            key="concertCreatedbyartistidArtists"
            :search-function="
              createdbyartistidArtistsSearchField?.multicriteriaSelect.searchFunction
            "
            :filters="createdbyartistidArtistsSearchField?.multicriteriaSelect.filters"
            :default-value="createdbyartistidArtistsDefaultValue"
            :violation="violations ? violations['createdbyartistidArtists'] : undefined"
            @option-selected="
              (selectedValue) => (formModel.createdbyartistidArtists = String(selectedValue))
            "
          />

          <!-- Moderationstatusid event moderation statuses FK -->
          <GenesisSelectSearchCriteria
            v-if="moderationstatusidEventModerationStatusesSearchField?.multicriteriaSelect"
            label="Moderationstatusid event moderation statuses"
            key="concertModerationstatusidEventModerationStatuses"
            :search-function="
              moderationstatusidEventModerationStatusesSearchField?.multicriteriaSelect
                .searchFunction
            "
            :filters="
              moderationstatusidEventModerationStatusesSearchField?.multicriteriaSelect.filters
            "
            :default-value="moderationstatusidEventModerationStatusesDefaultValue"
            :violation="
              violations ? violations['moderationstatusidEventModerationStatuses'] : undefined
            "
            @option-selected="
              (selectedValue) =>
                (formModel.moderationstatusidEventModerationStatuses = String(selectedValue))
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
import { Concert, ConcertFormDTO } from '@/models/ConcertModel.ts'
import GenesisButton from '@/components/button/GenesisButton.vue'
import GenesisInput from '@/components/form/GenesisInput.vue'
import GenesisSelectSearchCriteria from '@/components/form/GenesisSelectSearchCriteria.vue'

const props = defineProps<{
  concert?: Concert
  violations?: Record<string, string> | null
  submitLabel?: string
}>()

const emit = defineEmits<{
  (e: 'submit', payload: Partial<ConcertFormDTO>): void
  (e: 'cancel', payload: Partial<ConcertFormDTO>): void
}>()

const formModel = ref<Partial<ConcertFormDTO>>({ ...ConcertFormDTO.parse(props.concert) })
const eventidEventsSearchField = Concert.getSearchFieldByKey('eventidEvents')
const eventidEventsDefaultValue = props.concert
  ? (props.concert?.eventidEvents?.getKeyValue?.() ?? undefined)
  : undefined
const artistidArtistsSearchField = Concert.getSearchFieldByKey('artistidArtists')
const artistidArtistsDefaultValue = props.concert
  ? (props.concert?.artistidArtists?.getKeyValue?.() ?? undefined)
  : undefined
const venueidVenuesSearchField = Concert.getSearchFieldByKey('venueidVenues')
const venueidVenuesDefaultValue = props.concert
  ? (props.concert?.venueidVenues?.getKeyValue?.() ?? undefined)
  : undefined
const statusidParticipationStatusesSearchField = Concert.getSearchFieldByKey(
  'statusidParticipationStatuses',
)
const statusidParticipationStatusesDefaultValue = props.concert
  ? (props.concert?.statusidParticipationStatuses?.getKeyValue?.() ?? undefined)
  : undefined
const createdbyartistidArtistsSearchField = Concert.getSearchFieldByKey('createdbyartistidArtists')
const createdbyartistidArtistsDefaultValue = props.concert
  ? (props.concert?.createdbyartistidArtists?.getKeyValue?.() ?? undefined)
  : undefined
const moderationstatusidEventModerationStatusesSearchField = Concert.getSearchFieldByKey(
  'moderationstatusidEventModerationStatuses',
)
const moderationstatusidEventModerationStatusesDefaultValue = props.concert
  ? (props.concert?.moderationstatusidEventModerationStatuses?.getKeyValue?.() ?? undefined)
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
