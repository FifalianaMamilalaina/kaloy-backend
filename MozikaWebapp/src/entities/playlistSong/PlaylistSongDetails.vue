<template>
  <div class="card bg-base-100">
    <div class="card-body">
      <div v-if="playlistSong">
        <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
          <div>
            <label for="playlistSongId" class="block text-sm font-medium mb-1">Id</label>
            <div id="playlistSongId" class="input w-full bg-base-100 cursor-default">
              <span>{{ playlistSong.id ? $n(playlistSong.id, 'decimal') : '--' }}</span>
            </div>
          </div>
          <div>
            <label for="playlistSongPlaylistidPlaylists" class="block text-sm font-medium mb-1"
              >Playlist</label
            >
            <div
              id="playlistSongPlaylistidPlaylists"
              class="input w-full bg-base-100 cursor-default"
            >
              <span>{{ playlistSong.playlistidPlaylists?.getKeyValue() ?? '' }}</span>
            </div>
          </div>
          <div>
            <label for="playlistSongSongidSongs" class="block text-sm font-medium mb-1">Song</label>
            <div id="playlistSongSongidSongs" class="input w-full bg-base-100 cursor-default">
              <span>{{ playlistSong.songidSongs?.getKeyValue() ?? '' }}</span>
            </div>
          </div>
          <div>
            <label for="playlistSongPosition" class="block text-sm font-medium mb-1"
              >Position</label
            >
            <div id="playlistSongPosition" class="input w-full bg-base-100 cursor-default">
              <span>{{ playlistSong.position ? $n(playlistSong.position, 'decimal') : '--' }}</span>
            </div>
          </div>
          <div>
            <label for="playlistSongAddedAt" class="block text-sm font-medium mb-1">Added at</label>
            <div id="playlistSongAddedAt" class="input w-full bg-base-100 cursor-default">
              <span>{{ playlistSong.addedAt ?? '' }}</span>
            </div>
          </div>
        </div>
        <div class="flex justify-end gap-3 mt-6">
          <GenesisButton
            @click="goToUpdateFormView(playlistSong)"
            class="btn btn-outline btn-neutral"
          >
            <EditIcon class="mr-2" />
            <span>{{ $t('button.update') }}</span>
          </GenesisButton>
          <GenesisButton
            @click="openDeletePopup(playlistSong)"
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
    :message="`Êtes-vous sûr de vouloir supprimer PlaylistSong playlistSong?.id ?`"
    subMessage="Cette action est irréversible."
    @confirm="confirmDelete"
    @cancel="closePopup"
  />
</template>

<script setup lang="ts">
import type { PropType } from 'vue'

import type { PlaylistSong } from '@/models/PlaylistSongModel'
import GenesisButton from '@/components/button/GenesisButton.vue'
import { usePopup } from '@/composables/usePopup'
import { usePlaylistSongs } from '@/composables/usePlaylistSongs'
import DeleteConfirmationPopup from '@/components/popup/DeleteConfirmationPopup.vue'
import TrashIcon from '@/components/icons/TrashIcon.vue'
import EditIcon from '@/components/icons/EditIcon.vue'
import { useDateFormat } from '@/composables/useDateFormat'

const props = defineProps({
  playlistSong: {
    type: Object as PropType<PlaylistSong>,
    required: true,
  },
})
// Stores
const { closePopup, openPopup, visible: deletePopup } = usePopup(false)
const { deletePlaylistSong, goToListView, goToUpdateFormView } = usePlaylistSongs()

// Methods
const openDeletePopup = (entity: PlaylistSong) => {
  if (!entity) return
  openPopup()
}

const confirmDelete = async () => {
  const { success, error } = await deletePlaylistSong(props.playlistSong)
  if (!success) console.error(error)
  closePopup()
  goToListView()
}

const { formatDate } = useDateFormat()
</script>
<style scoped></style>
