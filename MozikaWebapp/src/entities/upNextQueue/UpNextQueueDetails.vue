<template>
  <div class="card bg-base-100">
    <div class="card-body">
      <div v-if="upNextQueue">
        <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
          <div>
            <label for="upNextQueueId" class="block text-sm font-medium mb-1">Id</label>
            <div id="upNextQueueId" class="input w-full bg-base-100 cursor-default">
              <span>{{ upNextQueue.id ? $n(upNextQueue.id, 'decimal') : '--' }}</span>
            </div>
          </div>
          <div>
            <label for="upNextQueueUseridUsers" class="block text-sm font-medium mb-1">User</label>
            <div id="upNextQueueUseridUsers" class="input w-full bg-base-100 cursor-default">
              <span>{{ upNextQueue.useridUsers?.getKeyValue() ?? '' }}</span>
            </div>
          </div>
          <div>
            <label for="upNextQueueSongidSongs" class="block text-sm font-medium mb-1">Song</label>
            <div id="upNextQueueSongidSongs" class="input w-full bg-base-100 cursor-default">
              <span>{{ upNextQueue.songidSongs?.getKeyValue() ?? '' }}</span>
            </div>
          </div>
          <div>
            <label for="upNextQueuePosition" class="block text-sm font-medium mb-1">Position</label>
            <div id="upNextQueuePosition" class="input w-full bg-base-100 cursor-default">
              <span>{{ upNextQueue.position ? $n(upNextQueue.position, 'decimal') : '--' }}</span>
            </div>
          </div>
          <div>
            <label for="upNextQueueAddedAt" class="block text-sm font-medium mb-1">Added at</label>
            <div id="upNextQueueAddedAt" class="input w-full bg-base-100 cursor-default">
              <span>{{ upNextQueue.addedAt ?? '' }}</span>
            </div>
          </div>
        </div>
        <div class="flex justify-end gap-3 mt-6">
          <GenesisButton
            @click="goToUpdateFormView(upNextQueue)"
            class="btn btn-outline btn-neutral"
          >
            <EditIcon class="mr-2" />
            <span>{{ $t('button.update') }}</span>
          </GenesisButton>
          <GenesisButton
            @click="openDeletePopup(upNextQueue)"
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
    :message="`Êtes-vous sûr de vouloir supprimer UpNextQueue upNextQueue?.id ?`"
    subMessage="Cette action est irréversible."
    @confirm="confirmDelete"
    @cancel="closePopup"
  />
</template>

<script setup lang="ts">
import type { PropType } from 'vue'

import type { UpNextQueue } from '@/models/UpNextQueueModel'
import GenesisButton from '@/components/button/GenesisButton.vue'
import { usePopup } from '@/composables/usePopup'
import { useUpNextQueues } from '@/composables/useUpNextQueues'
import DeleteConfirmationPopup from '@/components/popup/DeleteConfirmationPopup.vue'
import TrashIcon from '@/components/icons/TrashIcon.vue'
import EditIcon from '@/components/icons/EditIcon.vue'
import { useDateFormat } from '@/composables/useDateFormat'

const props = defineProps({
  upNextQueue: {
    type: Object as PropType<UpNextQueue>,
    required: true,
  },
})
// Stores
const { closePopup, openPopup, visible: deletePopup } = usePopup(false)
const { deleteUpNextQueue, goToListView, goToUpdateFormView } = useUpNextQueues()

// Methods
const openDeletePopup = (entity: UpNextQueue) => {
  if (!entity) return
  openPopup()
}

const confirmDelete = async () => {
  const { success, error } = await deleteUpNextQueue(props.upNextQueue)
  if (!success) console.error(error)
  closePopup()
  goToListView()
}

const { formatDate } = useDateFormat()
</script>
<style scoped></style>
