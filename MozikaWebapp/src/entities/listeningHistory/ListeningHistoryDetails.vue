<template>
  <div class="card bg-base-100">
    <div class="card-body">
      <div v-if="listeningHistory">
        <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
          <div>
            <label for="listeningHistoryId" class="block text-sm font-medium mb-1">Id</label>
            <div id="listeningHistoryId" class="input w-full bg-base-100 cursor-default">
              <span>{{ listeningHistory.id ? $n(listeningHistory.id, 'decimal') : '--' }}</span>
            </div>
          </div>
          <div>
            <label for="listeningHistoryUseridUsers" class="block text-sm font-medium mb-1"
              >User</label
            >
            <div id="listeningHistoryUseridUsers" class="input w-full bg-base-100 cursor-default">
              <span>{{ listeningHistory.useridUsers?.getKeyValue() ?? '' }}</span>
            </div>
          </div>
          <div>
            <label for="listeningHistorySongidSongs" class="block text-sm font-medium mb-1"
              >Song</label
            >
            <div id="listeningHistorySongidSongs" class="input w-full bg-base-100 cursor-default">
              <span>{{ listeningHistory.songidSongs?.getKeyValue() ?? '' }}</span>
            </div>
          </div>
          <div>
            <label for="listeningHistoryPlaymodeidPlayModes" class="block text-sm font-medium mb-1"
              >Play mode</label
            >
            <div
              id="listeningHistoryPlaymodeidPlayModes"
              class="input w-full bg-base-100 cursor-default"
            >
              <span>{{ listeningHistory.playmodeidPlayModes?.getKeyValue() ?? '' }}</span>
            </div>
          </div>
          <div>
            <label for="listeningHistoryListenedAt" class="block text-sm font-medium mb-1"
              >Listened at</label
            >
            <div id="listeningHistoryListenedAt" class="input w-full bg-base-100 cursor-default">
              <span>{{ listeningHistory.listenedAt ?? '' }}</span>
            </div>
          </div>
        </div>
        <div class="flex justify-end gap-3 mt-6">
          <GenesisButton
            @click="goToUpdateFormView(listeningHistory)"
            class="btn btn-outline btn-neutral"
          >
            <EditIcon class="mr-2" />
            <span>{{ $t('button.update') }}</span>
          </GenesisButton>
          <GenesisButton
            @click="openDeletePopup(listeningHistory)"
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
    :message="`Êtes-vous sûr de vouloir supprimer ListeningHistory listeningHistory?.id ?`"
    subMessage="Cette action est irréversible."
    @confirm="confirmDelete"
    @cancel="closePopup"
  />
</template>

<script setup lang="ts">
import type { PropType } from 'vue'

import type { ListeningHistory } from '@/models/ListeningHistoryModel'
import GenesisButton from '@/components/button/GenesisButton.vue'
import { usePopup } from '@/composables/usePopup'
import { useListeningHistorys } from '@/composables/useListeningHistorys'
import DeleteConfirmationPopup from '@/components/popup/DeleteConfirmationPopup.vue'
import TrashIcon from '@/components/icons/TrashIcon.vue'
import EditIcon from '@/components/icons/EditIcon.vue'
import { useDateFormat } from '@/composables/useDateFormat'

const props = defineProps({
  listeningHistory: {
    type: Object as PropType<ListeningHistory>,
    required: true,
  },
})
// Stores
const { closePopup, openPopup, visible: deletePopup } = usePopup(false)
const { deleteListeningHistory, goToListView, goToUpdateFormView } = useListeningHistorys()

// Methods
const openDeletePopup = (entity: ListeningHistory) => {
  if (!entity) return
  openPopup()
}

const confirmDelete = async () => {
  const { success, error } = await deleteListeningHistory(props.listeningHistory)
  if (!success) console.error(error)
  closePopup()
  goToListView()
}

const { formatDate } = useDateFormat()
</script>
<style scoped></style>
