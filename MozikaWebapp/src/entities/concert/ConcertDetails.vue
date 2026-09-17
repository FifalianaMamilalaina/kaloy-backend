<template>
  <div class="card bg-base-100">
    <div class="card-body">
      <div v-if="concert">
        <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
          <div>
            <label for="concertId" class="block text-sm font-medium mb-1">Id</label>
            <div id="concertId" class="input w-full bg-base-100 cursor-default">
              <span>{{ concert.id ? $n(concert.id, 'decimal') : '--' }}</span>
            </div>
          </div>
          <div>
            <label for="concertEventidEvents" class="block text-sm font-medium mb-1">Event</label>
            <div id="concertEventidEvents" class="input w-full bg-base-100 cursor-default">
              <span>{{ concert.eventidEvents?.getKeyValue() ?? '' }}</span>
            </div>
          </div>
          <div>
            <label for="concertTitle" class="block text-sm font-medium mb-1">Title</label>
            <div id="concertTitle" class="input w-full bg-base-100 cursor-default">
              <span>{{ concert.title ?? '' }}</span>
            </div>
          </div>
          <div>
            <label for="concertDescription" class="block text-sm font-medium mb-1"
              >Description</label
            >
            <div id="concertDescription" class="input w-full bg-base-100 cursor-default">
              <span>{{ concert.description ?? '' }}</span>
            </div>
          </div>
          <div>
            <label for="concertArtistidArtists" class="block text-sm font-medium mb-1"
              >Artist</label
            >
            <div id="concertArtistidArtists" class="input w-full bg-base-100 cursor-default">
              <span>{{ concert.artistidArtists?.getKeyValue() ?? '' }}</span>
            </div>
          </div>
          <div>
            <label for="concertVenueidVenues" class="block text-sm font-medium mb-1">Venue</label>
            <div id="concertVenueidVenues" class="input w-full bg-base-100 cursor-default">
              <span>{{ concert.venueidVenues?.getKeyValue() ?? '' }}</span>
            </div>
          </div>
          <div>
            <label for="concertStartTime" class="block text-sm font-medium mb-1">Start time</label>
            <div id="concertStartTime" class="input w-full bg-base-100 cursor-default">
              <span>{{ concert.startTime ?? '' }}</span>
            </div>
          </div>
          <div>
            <label for="concertEndTime" class="block text-sm font-medium mb-1">End time</label>
            <div id="concertEndTime" class="input w-full bg-base-100 cursor-default">
              <span>{{ concert.endTime ?? '' }}</span>
            </div>
          </div>
          <div>
            <label for="concertStatusidParticipationStatuses" class="block text-sm font-medium mb-1"
              >Participation statuse</label
            >
            <div
              id="concertStatusidParticipationStatuses"
              class="input w-full bg-base-100 cursor-default"
            >
              <span>{{ concert.statusidParticipationStatuses?.getKeyValue() ?? '' }}</span>
            </div>
          </div>
          <div>
            <label for="concertRespondedAt" class="block text-sm font-medium mb-1"
              >Responded at</label
            >
            <div id="concertRespondedAt" class="input w-full bg-base-100 cursor-default">
              <span>{{ concert.respondedAt ?? '' }}</span>
            </div>
          </div>
          <div>
            <label for="concertCreatedbyartistidArtists" class="block text-sm font-medium mb-1"
              >Artist</label
            >
            <div
              id="concertCreatedbyartistidArtists"
              class="input w-full bg-base-100 cursor-default"
            >
              <span>{{ concert.createdbyartistidArtists?.getKeyValue() ?? '' }}</span>
            </div>
          </div>
          <div>
            <label
              for="concertModerationstatusidEventModerationStatuses"
              class="block text-sm font-medium mb-1"
              >Event moderation statuse</label
            >
            <div
              id="concertModerationstatusidEventModerationStatuses"
              class="input w-full bg-base-100 cursor-default"
            >
              <span>{{
                concert.moderationstatusidEventModerationStatuses?.getKeyValue() ?? ''
              }}</span>
            </div>
          </div>
          <div>
            <label for="concertReviewedAt" class="block text-sm font-medium mb-1"
              >Reviewed at</label
            >
            <div id="concertReviewedAt" class="input w-full bg-base-100 cursor-default">
              <span>{{ concert.reviewedAt ?? '' }}</span>
            </div>
          </div>
          <div>
            <label for="concertCreatedAt" class="block text-sm font-medium mb-1">Created at</label>
            <div id="concertCreatedAt" class="input w-full bg-base-100 cursor-default">
              <span>{{ concert.createdAt ?? '' }}</span>
            </div>
          </div>
        </div>
        <div class="flex justify-end gap-3 mt-6">
          <GenesisButton @click="goToUpdateFormView(concert)" class="btn btn-outline btn-neutral">
            <EditIcon class="mr-2" />
            <span>{{ $t('button.update') }}</span>
          </GenesisButton>
          <GenesisButton
            @click="openDeletePopup(concert)"
            class="btn-outline btn-error hover:text-white"
          >
            <TrashIcon class="mr-2" />
            <span>{{ $t('button.delete') }}</span>
          </GenesisButton>
        </div>
      </div>
    </div>
  </div>
  <DeleteConfirmationPopup
    :visible="deletePopup"
    :message="`Êtes-vous sûr de vouloir supprimer Concert concert?.id ?`"
    subMessage="Cette action est irréversible."
    @confirm="confirmDelete"
    @cancel="closePopup"
  />
</template>

<script setup lang="ts">
import type { PropType } from 'vue'

import type { Concert } from '@/models/ConcertModel'
import GenesisButton from '@/components/button/GenesisButton.vue'
import { usePopup } from '@/composables/usePopup'
import { useConcerts } from '@/composables/useConcerts'
import DeleteConfirmationPopup from '@/components/popup/DeleteConfirmationPopup.vue'
import TrashIcon from '@/components/icons/TrashIcon.vue'
import EditIcon from '@/components/icons/EditIcon.vue'
import { useDateFormat } from '@/composables/useDateFormat'

const props = defineProps({
  concert: {
    type: Object as PropType<Concert>,
    required: true,
  },
})
// Stores
const { closePopup, openPopup, visible: deletePopup } = usePopup(false)
const { deleteConcert, goToListView, goToUpdateFormView } = useConcerts()

// Methods
const openDeletePopup = (entity: Concert) => {
  if (!entity) return
  openPopup()
}

const confirmDelete = async () => {
  const { success, error } = await deleteConcert(props.concert)
  if (!success) console.error(error)
  closePopup()
  goToListView()
}

const { formatDate } = useDateFormat()
</script>
<style scoped></style>
