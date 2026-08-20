<template>
  <div class="card bg-base-100">
    <div class="card-body">
      <form @submit.prevent="handleSubmit" class="grid grid-cols-1 w-7/12 gap-4 mx-auto">
        <!-- <form @submit.prevent="handleSubmit" class=""> -->
        <div class="grid grid-cols-1 gap-4">
          <!-- Name -->
          <GenesisInput
            label="Name"
            :violation="eventViolations ? eventViolations['name'] : undefined"
            placeholder="Enter Name"
            type="text"
            v-model="formModel.name"
            :value="formModel.name"
          />

          <!-- Description -->
          <GenesisInput
            label="Description"
            :violation="eventViolations ? eventViolations['description'] : undefined"
            placeholder="Enter Description"
            type="text"
            v-model="formModel.description"
            :value="formModel.description"
          />

          <!-- Start date -->
          <GenesisInput
            label="Start date"
            :violation="eventViolations ? eventViolations['startDate'] : undefined"
            placeholder="Enter Start date"
            type="date"
            v-model="formModel.startDate"
            :value="formModel.startDate"
          />

          <!-- End date -->
          <GenesisInput
            label="End date"
            :violation="eventViolations ? eventViolations['endDate'] : undefined"
            placeholder="Enter End date"
            type="date"
            v-model="formModel.endDate"
            :value="formModel.endDate"
          />

          <!-- Createdbyartistid artists FK -->
          <GenesisSelectSearchCriteria
            v-if="createdbyartistidArtistsSearchField?.multicriteriaSelect"
            label="Createdbyartistid artists"
            key="eventCreatedbyartistidArtists"
            :search-function="
              createdbyartistidArtistsSearchField?.multicriteriaSelect.searchFunction
            "
            :filters="createdbyartistidArtistsSearchField?.multicriteriaSelect.filters"
            :default-value="createdbyartistidArtistsDefaultValue"
            :violation="eventViolations ? eventViolations['createdbyartistidArtists'] : undefined"
            @option-selected="
              (selectedValue) => (formModel.createdbyartistidArtists = String(selectedValue))
            "
          />

          <!-- Moderationstatusid event moderation statuses FK -->
          <GenesisSelectSearchCriteria
            v-if="moderationstatusidEventModerationStatusesSearchField?.multicriteriaSelect"
            label="Moderationstatusid event moderation statuses"
            key="eventModerationstatusidEventModerationStatuses"
            :search-function="
              moderationstatusidEventModerationStatusesSearchField?.multicriteriaSelect
                .searchFunction
            "
            :filters="
              moderationstatusidEventModerationStatusesSearchField?.multicriteriaSelect.filters
            "
            :default-value="moderationstatusidEventModerationStatusesDefaultValue"
            :violation="
              eventViolations
                ? eventViolations['moderationstatusidEventModerationStatuses']
                : undefined
            "
            @option-selected="
              (selectedValue) =>
                (formModel.moderationstatusidEventModerationStatuses = String(selectedValue))
            "
          />

          <!-- Reviewed at -->
          <GenesisInput
            label="Reviewed at"
            :violation="eventViolations ? eventViolations['reviewedAt'] : undefined"
            placeholder="Enter Reviewed at"
            type="datetime-local"
            v-model="formModel.reviewedAt"
            :value="formModel.reviewedAt"
          />

          <!-- Created at -->
          <GenesisInput
            label="Created at"
            :violation="eventViolations ? eventViolations['createdAt'] : undefined"
            placeholder="Enter Created at"
            type="datetime-local"
            v-model="formModel.createdAt"
            :value="formModel.createdAt"
          />
        </div>
        <ConcertTableForm
          :initial-data="concertsData"
          :get-table-data-callback="setConcerts"
          :violations="concertViolations"
          class="w-full"
        />
        <EventMediaTableForm
          :initial-data="eventMediasData"
          :get-table-data-callback="setEventMedias"
          :violations="eventMediaViolations"
          class="w-full"
        />

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
import { ref, computed, onMounted } from 'vue'
import { Event, EventFormDTO } from '@/models/EventModel.ts'
import GenesisButton from '@/components/button/GenesisButton.vue'
import GenesisInput from '@/components/form/GenesisInput.vue'
import GenesisSelectSearchCriteria from '@/components/form/GenesisSelectSearchCriteria.vue'
import { ConcertFormDTO, type Concert } from '@/models/ConcertModel.ts'
import ConcertTableForm from '@/entities/concert/ConcertTableForm.vue'
import { EventMediaFormDTO, type EventMedia } from '@/models/EventMediaModel.ts'
import EventMediaTableForm from '@/entities/eventMedia/EventMediaTableForm.vue'

const props = defineProps<{
  event?: Event

  concertsData?: Concert[]
  eventMediasData?: EventMedia[]

  violations?: {
    event: Record<string, string>
    concerts: Record<string, string>[]
    eventMedias: Record<string, string>[]
  }

  submitLabel?: string
}>()

const emit = defineEmits<{
  (
    e: 'submit',
    payload: {
      event: Partial<EventFormDTO>
      concerts: ConcertFormDTO[]
      eventMedias: EventMediaFormDTO[]
    },
  ): void
  (e: 'cancel', payload: Partial<EventFormDTO>): void
}>()

const eventViolations = computed(() => (props.violations ? props.violations.event : {}))
const concertViolations = computed(() => (props.violations ? props.violations.concerts : []))
const eventMediaViolations = computed(() => (props.violations ? props.violations.eventMedias : []))

const formModel = ref<Partial<EventFormDTO>>({ ...EventFormDTO.parse(props.event) })
const createdbyartistidArtistsSearchField = Event.getSearchFieldByKey('createdbyartistidArtists')
const createdbyartistidArtistsDefaultValue = props.event
  ? (props.event?.createdbyartistidArtists?.getKeyValue?.() ?? undefined)
  : undefined
const moderationstatusidEventModerationStatusesSearchField = Event.getSearchFieldByKey(
  'moderationstatusidEventModerationStatuses',
)
const moderationstatusidEventModerationStatusesDefaultValue = props.event
  ? (props.event?.moderationstatusidEventModerationStatuses?.getKeyValue?.() ?? undefined)
  : undefined

const concerts = ref<ConcertFormDTO[]>([])
const setConcerts = (data: ConcertFormDTO[]) => {
  concerts.value = data
}
const eventMedias = ref<EventMediaFormDTO[]>([])
const setEventMedias = (data: EventMediaFormDTO[]) => {
  eventMedias.value = data
}

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

  emit('submit', { event: data, concerts: concerts.value, eventMedias: eventMedias.value })
}

function cancelForm() {
  emit('cancel', formModel.value)
}
onMounted(async () => {
  if (Array.isArray(props.concertsData) && props.concertsData.length > 0) {
    setConcerts(ConcertFormDTO.parseList(props.concertsData))
  }
  if (Array.isArray(props.eventMediasData) && props.eventMediasData.length > 0) {
    setEventMedias(EventMediaFormDTO.parseList(props.eventMediasData))
  }
})
</script>
<style scoped></style>
