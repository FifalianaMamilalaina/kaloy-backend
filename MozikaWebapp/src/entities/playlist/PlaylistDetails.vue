<template>
  <div class="card bg-base-100">
    <div class="card-body">
      <div v-if="playlist">
        <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
          <div>
            <label for="playlistId" class="block text-sm font-medium mb-1">Id</label>
            <div id="playlistId" class="input w-full bg-base-100 cursor-default">
              <span>{{ playlist.id ? $n(playlist.id, 'decimal') : '--' }}</span>
            </div>
          </div>
          <div>
            <label for="playlistOwneruseridUsers" class="block text-sm font-medium mb-1"
              >User</label
            >
            <div id="playlistOwneruseridUsers" class="input w-full bg-base-100 cursor-default">
              <span>{{ playlist.owneruseridUsers?.getKeyValue() ?? '' }}</span>
            </div>
          </div>
          <div>
            <label for="playlistName" class="block text-sm font-medium mb-1">Name</label>
            <div id="playlistName" class="input w-full bg-base-100 cursor-default">
              <span>{{ playlist.name ?? '' }}</span>
            </div>
          </div>
          <div>
            <label
              for="playlistVisibilityidPlaylistVisibilities"
              class="block text-sm font-medium mb-1"
              >Playlist visibilitie</label
            >
            <div
              id="playlistVisibilityidPlaylistVisibilities"
              class="input w-full bg-base-100 cursor-default"
            >
              <span>{{ playlist.visibilityidPlaylistVisibilities?.getKeyValue() ?? '' }}</span>
            </div>
          </div>
          <div>
            <label for="playlistShareToken" class="block text-sm font-medium mb-1"
              >Share token</label
            >
            <div id="playlistShareToken" class="input w-full bg-base-100 cursor-default">
              <span>{{ playlist.shareToken ?? '' }}</span>
            </div>
          </div>
          <div>
            <label for="playlistCreatedAt" class="block text-sm font-medium mb-1">Created at</label>
            <div id="playlistCreatedAt" class="input w-full bg-base-100 cursor-default">
              <span>{{ playlist.createdAt ?? '' }}</span>
            </div>
          </div>
          <div>
            <label for="playlistUpdatedAt" class="block text-sm font-medium mb-1">Updated at</label>
            <div id="playlistUpdatedAt" class="input w-full bg-base-100 cursor-default">
              <span>{{ playlist.updatedAt ?? '' }}</span>
            </div>
          </div>
        </div>
        <div class="flex justify-end gap-3 mt-6">
          <GenesisButton @click="goToUpdateFormView(playlist)" class="btn btn-outline btn-neutral">
            <EditIcon class="mr-2" />
            <span>{{ $t('button.update') }}</span>
          </GenesisButton>
          <GenesisButton
            @click="openDeletePopup(playlist)"
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
    :message="`Êtes-vous sûr de vouloir supprimer Playlist playlist?.id ?`"
    subMessage="Cette action est irréversible."
    @confirm="confirmDelete"
    @cancel="closePopup"
  />
</template>

<script setup lang="ts">
import type { PropType } from 'vue'

import type { Playlist } from '@/models/PlaylistModel'
import GenesisButton from '@/components/button/GenesisButton.vue'
import { usePopup } from '@/composables/usePopup'
import { usePlaylists } from '@/composables/usePlaylists'
import DeleteConfirmationPopup from '@/components/popup/DeleteConfirmationPopup.vue'
import TrashIcon from '@/components/icons/TrashIcon.vue'
import EditIcon from '@/components/icons/EditIcon.vue'
import { useDateFormat } from '@/composables/useDateFormat'

const props = defineProps({
  playlist: {
    type: Object as PropType<Playlist>,
    required: true,
  },
})
// Stores
const { closePopup, openPopup, visible: deletePopup } = usePopup(false)
const { deletePlaylist, goToListView, goToUpdateFormView } = usePlaylists()

// Methods
const openDeletePopup = (entity: Playlist) => {
  if (!entity) return
  openPopup()
}

const confirmDelete = async () => {
  const { success, error } = await deletePlaylist(props.playlist)
  if (!success) console.error(error)
  closePopup()
  goToListView()
}

const { formatDate } = useDateFormat()
</script>
<style scoped></style>
