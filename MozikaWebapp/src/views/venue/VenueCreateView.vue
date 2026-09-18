<template>
  <div class="w-full">
    <div class="flex items-center justify-between mb-6">
      <h3 class="text-xl font-semibold text-base-content">
        {{ $t('entity.nav', { entity: 'Venue' }) }}
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

    <venue-form
      :submit-label="$t('entity.create.submitLabel', { entity: 'Venue' })"
      :concertsData="initialConcerts"
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
import VenueForm from '@/entities/venue/VenueForm.vue'
import AlertPopup from '@/components/popup/AlertPopup.vue'
import GenesisButton from '@/components/button/GenesisButton.vue'
import LeftArrowIcon from '@/components/icons/LeftArrowIcon.vue'
import { VenueFormDTO } from '@/models/VenueModel'
import { useVenues } from '@/composables/useVenues'
import { usePopup } from '@/composables/usePopup'
import { useFreezeScreenStore } from '@/stores/useFreezeScreenStore'
import { ref } from 'vue'
import type { Concert, ConcertFormDTO } from '@/models/ConcertModel'

const { createVenue, goToListView, message, viewVenue } = useVenues()
const { openPopup, closePopup, visible: alertPopup } = usePopup()
const freezeStore = useFreezeScreenStore()
const violations = ref<
  | {
      venue: Record<string, string>
      concerts: Record<string, string>[]
    }
  | undefined
>(undefined)
const initialConcerts = ref<Concert[]>([])

const createHandler = async (payload: {
  venue: Partial<VenueFormDTO>
  concerts: ConcertFormDTO[]
}) => {
  freezeStore.freeze('Creating a new Venue ...')
  violations.value = undefined
  message.value = null
  try {
    const { data, errors } = await createVenue(payload)
    if (errors && Object.keys(errors).length > 0) {
      violations.value = errors as {
        venue: Record<string, string>
        concerts: Record<string, string>[]
      }
    }
    if (data && !message.value) {
      viewVenue(data)
    } else {
      throw new Error(String(message.value))
    }
  } catch (error: unknown) {
    console.error('Error creating venue:', error)
    openPopup()
  } finally {
    freezeStore.unfreeze()
  }
}
</script>
<style scoped></style>
