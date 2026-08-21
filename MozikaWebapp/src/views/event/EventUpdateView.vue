<template>
  <div class="w-full">
    <!-- Header -->
    <div class="flex items-center justify-between mb-6">
      <h3 class="text-xl font-semibold text-base-content">
        Event /
        <span class="text-base-content/50 font-normal">{{ $t('entity.update.nav') }}</span>
      </h3>
      <GenesisButton
        :title="$t('button.backToListDescription')"
        @click="goToListView"
        class="btn-secondary"
      >
        <LeftArrowIcon />
        {{ $t('button.backToList') }}
      </GenesisButton>
    </div>

    <!-- Form -->
    <div v-if="entity && concertLoaded && eventMediaLoaded">
      <event-form
        :event="entity"
        :concertsData="concerts"
        :eventMediasData="eventMedias"
        :violations="violations"
        :submit-label="$t('entity.update.submitLabel', { entity: 'Event' })"
        @submit="updateHandler"
        @cancel="cancelHandler"
      />
    </div>

    <!-- Alert -->
    <AlertPopup
      :message="message ?? undefined"
      title="Error 500"
      :visible="alertPopup"
      @close="closePopup"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import EventForm from '@/entities/event/EventForm.vue'
import AlertPopup from '@/components/popup/AlertPopup.vue'
import { useEvents } from '@/composables/useEvents'
import { Event, EventFormDTO } from '@/models/EventModel'
import { usePopup } from '@/composables/usePopup'
import GenesisButton from '@/components/button/GenesisButton.vue'
import LeftArrowIcon from '@/components/icons/LeftArrowIcon.vue'
import { useFreezeScreenStore } from '@/stores/useFreezeScreenStore.ts'
import { useConcerts } from '@/composables/useConcerts'
import { ConcertFormDTO } from '@/models/ConcertModel'
import { useEventMedias } from '@/composables/useEventMedias'
import { EventMediaFormDTO } from '@/models/EventMediaModel'

import { PaginationRequestParameter } from '@/models/api/RequestModel'

const route = useRoute()
const pathId = Number(route.params.id)
const { closePopup, openPopup, visible: alertPopup } = usePopup()
const { getEventById, goToListView, updateEvent, viewEvent, message } = useEvents()
const entity = ref<Event | null>(null)
const freezeStore = useFreezeScreenStore()

const { concerts, getAllConcertsByEventId } = useConcerts()
const concertLoaded = ref(false)
const loadConcerts = async () => {
  try {
    await getAllConcertsByEventId(pathId, true, new PaginationRequestParameter(), [])
    concertLoaded.value = true
  } catch (error) {
    console.error('Error loading event with details:', error)
    concertLoaded.value = true // Continuer même en cas d'erreur
  }
}
const { eventMedias, getAllEventMediasByEventId } = useEventMedias()
const eventMediaLoaded = ref(false)
const loadEventMedias = async () => {
  try {
    await getAllEventMediasByEventId(pathId, true, new PaginationRequestParameter(), [])
    eventMediaLoaded.value = true
  } catch (error) {
    console.error('Error loading event with details:', error)
    eventMediaLoaded.value = true // Continuer même en cas d'erreur
  }
}

const violations = ref<
  | {
      event: Record<string, string>
      concerts: Record<string, string>[]
      eventMedias: Record<string, string>[]
    }
  | undefined
>(undefined)

const updateHandler = async (payload: {
  event: Partial<EventFormDTO>
  concerts: ConcertFormDTO[]
  eventMedias: EventMediaFormDTO[]
}) => {
  freezeStore.freeze('Updating event ' + pathId + ' ...')
  message.value = null
  violations.value = {
    event: {},
    concerts: [],
    eventMedias: [],
  }
  try {
    const { data, errors } = await updateEvent(pathId, payload)

    if (errors && Object.keys(errors).length > 0) {
      // Gérer les erreurs de validation
      violations.value = errors as {
        event: Record<string, string>
        concerts: Record<string, string>[]
        eventMedias: Record<string, string>[]
      }
    }
    if (data && !message.value) {
      viewEvent(data)
    } else {
      throw new Error(String(message.value))
    }
  } catch (error: unknown) {
    console.error(error)
  } finally {
    freezeStore.unfreeze()
  }
}

const cancelHandler = () => {
  if (entity.value) {
    viewEvent(entity.value)
  }
}

onMounted(async () => {
  const result = await getEventById(pathId)
  if (result.data) {
    entity.value = result.data
    await loadConcerts()
    await loadEventMedias()
  } else {
    openPopup()
  }
})
</script>
<style scoped></style>
