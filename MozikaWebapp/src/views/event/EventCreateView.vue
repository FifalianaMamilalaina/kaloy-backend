<template>
  <div class="w-full">
    <div class="flex items-center justify-between mb-6">
      <h3 class="text-xl font-semibold text-base-content">
        {{ $t('entity.nav', { entity: 'Event' }) }}
        <span class="text-base-content/50 font-normal">{{ $t('entity.create.nav') }}</span>
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

    <event-form
      :submit-label="$t('entity.create.submitLabel', { entity: 'Event' })"
      :concertsData="initialConcerts"
      :eventMediasData="initialEventMedias"
      :violations="violations"
      @submit="createHandler"
      @cancel="goToListView"
    />

    <AlertPopup
      :message="message ?? undefined"
      title="Error"
      :visible="alertPopup"
      @close="closePopup"
    />
  </div>
</template>

<script setup lang="ts">
import EventForm from '@/entities/event/EventForm.vue'
import AlertPopup from '@/components/popup/AlertPopup.vue'
import GenesisButton from '@/components/button/GenesisButton.vue'
import LeftArrowIcon from '@/components/icons/LeftArrowIcon.vue'
import { EventFormDTO } from '@/models/EventModel'
import { useEvents } from '@/composables/useEvents'
import { usePopup } from '@/composables/usePopup'
import { useFreezeScreenStore } from '@/stores/useFreezeScreenStore'
import { ref } from 'vue'
import type { Concert, ConcertFormDTO } from '@/models/ConcertModel'
import type { EventMedia, EventMediaFormDTO } from '@/models/EventMediaModel'

const { createEvent, goToListView, message, viewEvent } = useEvents()
const { openPopup, closePopup, visible: alertPopup } = usePopup()
const freezeStore = useFreezeScreenStore()
const violations = ref<
  | {
      event: Record<string, string>
      concerts: Record<string, string>[]
      eventMedias: Record<string, string>[]
    }
  | undefined
>(undefined)
const initialConcerts = ref<Concert[]>([])
const initialEventMedias = ref<EventMedia[]>([])

const createHandler = async (payload: {
  event: Partial<EventFormDTO>
  concerts: ConcertFormDTO[]
  eventMedias: EventMediaFormDTO[]
}) => {
  freezeStore.freeze('Creating a new Event ...')
  violations.value = undefined
  message.value = null
  try {
    const { data, errors } = await createEvent(payload)
    if (errors && Object.keys(errors).length > 0) {
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
    console.error('Error creating event:', error)
    openPopup()
  } finally {
    freezeStore.unfreeze()
  }
}
</script>
<style scoped></style>
