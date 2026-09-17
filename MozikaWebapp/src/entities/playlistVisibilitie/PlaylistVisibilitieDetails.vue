<template>
  <div class="card bg-base-100">
    <div class="card-body">
      <div v-if="playlistVisibilitie">
        <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
          <div>
            <label for="playlistVisibilitieId" class="block text-sm font-medium mb-1">Id</label>
            <div id="playlistVisibilitieId" class="input w-full bg-base-100 cursor-default">
              <span>{{
                playlistVisibilitie.id ? $n(playlistVisibilitie.id, 'decimal') : '--'
              }}</span>
            </div>
          </div>
          <div>
            <label for="playlistVisibilitieName" class="block text-sm font-medium mb-1">Name</label>
            <div id="playlistVisibilitieName" class="input w-full bg-base-100 cursor-default">
              <span>{{ playlistVisibilitie.name ?? '' }}</span>
            </div>
          </div>
        </div>
        <div class="flex justify-end gap-3 mt-6">
          <GenesisButton
            @click="goToUpdateFormView(playlistVisibilitie)"
            class="btn btn-outline btn-neutral"
          >
            <EditIcon class="mr-2" />
            <span>{{ $t('button.update') }}</span>
          </GenesisButton>
          <GenesisButton
            @click="openDeletePopup(playlistVisibilitie)"
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
    :message="`Êtes-vous sûr de vouloir supprimer PlaylistVisibilitie playlistVisibilitie?.id ?`"
    subMessage="Cette action est irréversible."
    @confirm="confirmDelete"
    @cancel="closePopup"
  />
</template>

<script setup lang="ts">
import type { PropType } from 'vue'

import type { PlaylistVisibilitie } from '@/models/PlaylistVisibilitieModel'
import GenesisButton from '@/components/button/GenesisButton.vue'
import { usePopup } from '@/composables/usePopup'
import { usePlaylistVisibilities } from '@/composables/usePlaylistVisibilities'
import DeleteConfirmationPopup from '@/components/popup/DeleteConfirmationPopup.vue'
import TrashIcon from '@/components/icons/TrashIcon.vue'
import EditIcon from '@/components/icons/EditIcon.vue'
import { useDateFormat } from '@/composables/useDateFormat'

const props = defineProps({
  playlistVisibilitie: {
    type: Object as PropType<PlaylistVisibilitie>,
    required: true,
  },
})
// Stores
const { closePopup, openPopup, visible: deletePopup } = usePopup(false)
const { deletePlaylistVisibilitie, goToListView, goToUpdateFormView } = usePlaylistVisibilities()

// Methods
const openDeletePopup = (entity: PlaylistVisibilitie) => {
  if (!entity) return
  openPopup()
}

const confirmDelete = async () => {
  const { success, error } = await deletePlaylistVisibilitie(props.playlistVisibilitie)
  if (!success) console.error(error)
  closePopup()
  goToListView()
}

const { formatDate } = useDateFormat()
</script>
<style scoped></style>
