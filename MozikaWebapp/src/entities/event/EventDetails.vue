<template>
  <div class="card bg-base-100">
    <div class="card-body">
      <div v-if="event">
        <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
          <div>
            <label for="eventId" class="block text-sm font-medium mb-1">Id</label>
            <div id="eventId" class="input w-full bg-base-100 cursor-default">
              <span>{{ event.id ? $n(event.id, 'decimal') : '--' }}</span>
            </div>
          </div>
          <div>
            <label for="eventName" class="block text-sm font-medium mb-1">Name</label>
            <div id="eventName" class="input w-full bg-base-100 cursor-default">
              <span>{{ event.name ?? '' }}</span>
            </div>
          </div>
          <div>
            <label for="eventDescription" class="block text-sm font-medium mb-1">Description</label>
            <div id="eventDescription" class="input w-full bg-base-100 cursor-default">
              <span>{{ event.description ?? '' }}</span>
            </div>
          </div>
          <div>
            <label for="eventStartDate" class="block text-sm font-medium mb-1">Start date</label>
            <div id="eventStartDate" class="input w-full bg-base-100 cursor-default">
              <span>{{ event.startDate ? $d(event.startDate, 'long') : '--/--/--' }}</span>
            </div>
          </div>
          <div>
            <label for="eventEndDate" class="block text-sm font-medium mb-1">End date</label>
            <div id="eventEndDate" class="input w-full bg-base-100 cursor-default">
              <span>{{ event.endDate ? $d(event.endDate, 'long') : '--/--/--' }}</span>
            </div>
          </div>
          <div>
            <label for="eventCreatedbyartistidArtists" class="block text-sm font-medium mb-1"
              >Artist</label
            >
            <div id="eventCreatedbyartistidArtists" class="input w-full bg-base-100 cursor-default">
              <span>{{ event.createdbyartistidArtists?.getKeyValue() ?? '' }}</span>
            </div>
          </div>
          <div>
            <label
              for="eventModerationstatusidEventModerationStatuses"
              class="block text-sm font-medium mb-1"
              >Event moderation statuse</label
            >
            <div
              id="eventModerationstatusidEventModerationStatuses"
              class="input w-full bg-base-100 cursor-default"
            >
              <span>{{
                event.moderationstatusidEventModerationStatuses?.getKeyValue() ?? ''
              }}</span>
            </div>
          </div>
          <div>
            <label for="eventReviewedAt" class="block text-sm font-medium mb-1">Reviewed at</label>
            <div id="eventReviewedAt" class="input w-full bg-base-100 cursor-default">
              <span>{{ event.reviewedAt ?? '' }}</span>
            </div>
          </div>
          <div>
            <label for="eventCreatedAt" class="block text-sm font-medium mb-1">Created at</label>
            <div id="eventCreatedAt" class="input w-full bg-base-100 cursor-default">
              <span>{{ event.createdAt ?? '' }}</span>
            </div>
          </div>
        </div>
        <div class="flex justify-end gap-3 mt-6">
          <GenesisButton @click="goToUpdateFormView(event)" class="btn btn-outline btn-neutral">
            <EditIcon class="mr-2" />
            <span>{{ $t('button.update') }}</span>
          </GenesisButton>
          <GenesisButton
            @click="openDeletePopup(event)"
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
    :message="`Êtes-vous sûr de vouloir supprimer Event event?.id ?`"
    subMessage="Cette action est irréversible."
    @confirm="confirmDelete"
    @cancel="closePopup"
  />
</template>

<script setup lang="ts">
import type { PropType } from 'vue'

import type { Event } from '@/models/EventModel'
import GenesisButton from '@/components/button/GenesisButton.vue'
import { usePopup } from '@/composables/usePopup'
import { useEvents } from '@/composables/useEvents'
import DeleteConfirmationPopup from '@/components/popup/DeleteConfirmationPopup.vue'
import TrashIcon from '@/components/icons/TrashIcon.vue'
import EditIcon from '@/components/icons/EditIcon.vue'
import { useDateFormat } from '@/composables/useDateFormat'

const props = defineProps({
  event: {
    type: Object as PropType<Event>,
    required: true,
  },
})
// Stores
const { closePopup, openPopup, visible: deletePopup } = usePopup(false)
const { deleteEvent, goToListView, goToUpdateFormView } = useEvents()

// Methods
const openDeletePopup = (entity: Event) => {
  if (!entity) return
  openPopup()
}

const confirmDelete = async () => {
  const { success, error } = await deleteEvent(props.event)
  if (!success) console.error(error)
  closePopup()
  goToListView()
}

const { formatDate } = useDateFormat()
</script>
<style scoped></style>
