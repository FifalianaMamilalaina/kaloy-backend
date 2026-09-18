<template>
  <div class="card bg-base-100">
    <div class="card-body">
      <div v-if="eventMedia">
        <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
          <div>
            <label for="eventMediaId" class="block text-sm font-medium mb-1">Id</label>
            <div id="eventMediaId" class="input w-full bg-base-100 cursor-default">
              <span>{{ eventMedia.id ? $n(eventMedia.id, 'decimal') : '--' }}</span>
            </div>
          </div>
          <div>
            <label for="eventMediaEventidEvents" class="block text-sm font-medium mb-1"
              >Event</label
            >
            <div id="eventMediaEventidEvents" class="input w-full bg-base-100 cursor-default">
              <span>{{ eventMedia.eventidEvents?.getKeyValue() ?? '' }}</span>
            </div>
          </div>
          <div>
            <label for="eventMediaUploaderuseridUsers" class="block text-sm font-medium mb-1"
              >User</label
            >
            <div id="eventMediaUploaderuseridUsers" class="input w-full bg-base-100 cursor-default">
              <span>{{ eventMedia.uploaderuseridUsers?.getKeyValue() ?? '' }}</span>
            </div>
          </div>
          <div>
            <label for="eventMediaMediatypeidMediaTypes" class="block text-sm font-medium mb-1"
              >Media type</label
            >
            <div
              id="eventMediaMediatypeidMediaTypes"
              class="input w-full bg-base-100 cursor-default"
            >
              <span>{{ eventMedia.mediatypeidMediaTypes?.getKeyValue() ?? '' }}</span>
            </div>
          </div>
          <div>
            <label for="eventMediaUrl" class="block text-sm font-medium mb-1">Url</label>
            <div id="eventMediaUrl" class="input w-full bg-base-100 cursor-default">
              <span>{{ eventMedia.url ?? '' }}</span>
            </div>
          </div>
          <div>
            <label for="eventMediaCreatedAt" class="block text-sm font-medium mb-1"
              >Created at</label
            >
            <div id="eventMediaCreatedAt" class="input w-full bg-base-100 cursor-default">
              <span>{{ eventMedia.createdAt ?? '' }}</span>
            </div>
          </div>
        </div>
        <div class="flex justify-end gap-3 mt-6">
          <GenesisButton
            @click="goToUpdateFormView(eventMedia)"
            class="btn btn-outline btn-neutral"
          >
            <EditIcon class="mr-2" />
            <span>{{ $t('button.update') }}</span>
          </GenesisButton>
          <GenesisButton
            @click="openDeletePopup(eventMedia)"
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
    :message="`Êtes-vous sûr de vouloir supprimer EventMedia eventMedia?.id ?`"
    subMessage="Cette action est irréversible."
    @confirm="confirmDelete"
    @cancel="closePopup"
  />
</template>

<script setup lang="ts">
import type { PropType } from 'vue'

import type { EventMedia } from '@/models/EventMediaModel'
import GenesisButton from '@/components/button/GenesisButton.vue'
import { usePopup } from '@/composables/usePopup'
import { useEventMedias } from '@/composables/useEventMedias'
import DeleteConfirmationPopup from '@/components/popup/DeleteConfirmationPopup.vue'
import TrashIcon from '@/components/icons/TrashIcon.vue'
import EditIcon from '@/components/icons/EditIcon.vue'
import { useDateFormat } from '@/composables/useDateFormat'

const props = defineProps({
  eventMedia: {
    type: Object as PropType<EventMedia>,
    required: true,
  },
})
// Stores
const { closePopup, openPopup, visible: deletePopup } = usePopup(false)
const { deleteEventMedia, goToListView, goToUpdateFormView } = useEventMedias()

// Methods
const openDeletePopup = (entity: EventMedia) => {
  if (!entity) return
  openPopup()
}

const confirmDelete = async () => {
  const { success, error } = await deleteEventMedia(props.eventMedia)
  if (!success) console.error(error)
  closePopup()
  goToListView()
}

const { formatDate } = useDateFormat()
</script>
<style scoped></style>
