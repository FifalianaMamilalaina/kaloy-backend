<template>
  <div class="card bg-base-100">
    <div class="card-body">
      <div v-if="venue">
        <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
          <div>
            <label for="venueId" class="block text-sm font-medium mb-1">Id</label>
            <div id="venueId" class="input w-full bg-base-100 cursor-default">
              <span>{{ venue.id ? $n(venue.id, 'decimal') : '--' }}</span>
            </div>
          </div>
          <div>
            <label for="venueName" class="block text-sm font-medium mb-1">Name</label>
            <div id="venueName" class="input w-full bg-base-100 cursor-default">
              <span>{{ venue.name ?? '' }}</span>
            </div>
          </div>
          <div>
            <label for="venueLocation" class="block text-sm font-medium mb-1">Location</label>
            <div id="venueLocation" class="input w-full bg-base-100 cursor-default">
              <span>{{ venue.location ?? '' }}</span>
            </div>
          </div>
        </div>
        <div class="flex justify-end gap-3 mt-6">
          <GenesisButton @click="goToUpdateFormView(venue)" class="btn btn-outline btn-neutral">
            <EditIcon class="mr-2" />
            <span>{{ $t('button.update') }}</span>
          </GenesisButton>
          <GenesisButton
            @click="openDeletePopup(venue)"
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
    :message="`Êtes-vous sûr de vouloir supprimer Venue venue?.id ?`"
    subMessage="Cette action est irréversible."
    @confirm="confirmDelete"
    @cancel="closePopup"
  />
</template>

<script setup lang="ts">
import type { PropType } from 'vue'

import type { Venue } from '@/models/VenueModel'
import GenesisButton from '@/components/button/GenesisButton.vue'
import { usePopup } from '@/composables/usePopup'
import { useVenues } from '@/composables/useVenues'
import DeleteConfirmationPopup from '@/components/popup/DeleteConfirmationPopup.vue'
import TrashIcon from '@/components/icons/TrashIcon.vue'
import EditIcon from '@/components/icons/EditIcon.vue'
import { useDateFormat } from '@/composables/useDateFormat'

const props = defineProps({
  venue: {
    type: Object as PropType<Venue>,
    required: true,
  },
})
// Stores
const { closePopup, openPopup, visible: deletePopup } = usePopup(false)
const { deleteVenue, goToListView, goToUpdateFormView } = useVenues()

// Methods
const openDeletePopup = (entity: Venue) => {
  if (!entity) return
  openPopup()
}

const confirmDelete = async () => {
  const { success, error } = await deleteVenue(props.venue)
  if (!success) console.error(error)
  closePopup()
  goToListView()
}

const { formatDate } = useDateFormat()
</script>
<style scoped></style>
