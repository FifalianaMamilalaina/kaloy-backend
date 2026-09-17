<template>
  <div class="w-full">
    <!-- Header -->
    <div class="flex items-center justify-between mb-6">
      <h3 class="text-xl font-semibold text-base-content">
        Venue /
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
    <div v-if="entity && concertLoaded">
      <venue-form
        :venue="entity"
        :concertsData="concerts"
        :violations="violations"
        :submit-label="$t('entity.update.submitLabel', { entity: 'Venue' })"
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
import VenueForm from '@/entities/venue/VenueForm.vue'
import AlertPopup from '@/components/popup/AlertPopup.vue'
import { useVenues } from '@/composables/useVenues'
import { Venue, VenueFormDTO } from '@/models/VenueModel'
import { usePopup } from '@/composables/usePopup'
import GenesisButton from '@/components/button/GenesisButton.vue'
import LeftArrowIcon from '@/components/icons/LeftArrowIcon.vue'
import { useFreezeScreenStore } from '@/stores/useFreezeScreenStore.ts'
import { useConcerts } from '@/composables/useConcerts'
import { ConcertFormDTO } from '@/models/ConcertModel'

import { PaginationRequestParameter } from '@/models/api/RequestModel'

const route = useRoute()
const pathId = Number(route.params.id)
const { closePopup, openPopup, visible: alertPopup } = usePopup()
const { getVenueById, goToListView, updateVenue, viewVenue, message } = useVenues()
const entity = ref<Venue | null>(null)
const freezeStore = useFreezeScreenStore()

const { concerts, getAllConcertsByVenueId } = useConcerts()
const concertLoaded = ref(false)
const loadConcerts = async () => {
  try {
    await getAllConcertsByVenueId(pathId, true, new PaginationRequestParameter(), [])
    concertLoaded.value = true
  } catch (error) {
    console.error('Error loading venue with details:', error)
    concertLoaded.value = true // Continuer même en cas d'erreur
  }
}

const violations = ref<
  | {
      venue: Record<string, string>
      concerts: Record<string, string>[]
    }
  | undefined
>(undefined)

const updateHandler = async (payload: {
  venue: Partial<VenueFormDTO>
  concerts: ConcertFormDTO[]
}) => {
  freezeStore.freeze('Updating venue ' + pathId + ' ...')
  message.value = null
  violations.value = {
    venue: {},
    concerts: [],
  }
  try {
    const { data, errors } = await updateVenue(pathId, payload)

    if (errors && Object.keys(errors).length > 0) {
      // Gérer les erreurs de validation
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
    console.error(error)
  } finally {
    freezeStore.unfreeze()
  }
}

const cancelHandler = () => {
  if (entity.value) {
    viewVenue(entity.value)
  }
}

onMounted(async () => {
  const result = await getVenueById(pathId)
  if (result.data) {
    entity.value = result.data
    await loadConcerts()
  } else {
    openPopup()
  }
})
</script>
<style scoped></style>
