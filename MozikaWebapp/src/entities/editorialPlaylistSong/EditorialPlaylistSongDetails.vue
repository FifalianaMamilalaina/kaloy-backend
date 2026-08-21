<template>
  <div class="card bg-base-100">
    <div class="card-body">
      <div v-if="editorialPlaylistSong">
        <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
          <div>
            <label for="editorialPlaylistSongId" class="block text-sm font-medium mb-1">Id</label>
            <div id="editorialPlaylistSongId" class="input w-full bg-base-100 cursor-default">
              <span>{{
                editorialPlaylistSong.id ? $n(editorialPlaylistSong.id, 'decimal') : '--'
              }}</span>
            </div>
          </div>
          <div>
            <label
              for="editorialPlaylistSongEditorialplaylistidEditorialPlaylists"
              class="block text-sm font-medium mb-1"
              >Editorial playlist</label
            >
            <div
              id="editorialPlaylistSongEditorialplaylistidEditorialPlaylists"
              class="input w-full bg-base-100 cursor-default"
            >
              <span>{{
                editorialPlaylistSong.editorialplaylistidEditorialPlaylists?.getKeyValue() ?? ''
              }}</span>
            </div>
          </div>
          <div>
            <label for="editorialPlaylistSongSongidSongs" class="block text-sm font-medium mb-1"
              >Song</label
            >
            <div
              id="editorialPlaylistSongSongidSongs"
              class="input w-full bg-base-100 cursor-default"
            >
              <span>{{ editorialPlaylistSong.songidSongs?.getKeyValue() ?? '' }}</span>
            </div>
          </div>
          <div>
            <label for="editorialPlaylistSongPosition" class="block text-sm font-medium mb-1"
              >Position</label
            >
            <div id="editorialPlaylistSongPosition" class="input w-full bg-base-100 cursor-default">
              <span>{{
                editorialPlaylistSong.position
                  ? $n(editorialPlaylistSong.position, 'decimal')
                  : '--'
              }}</span>
            </div>
          </div>
        </div>
        <div class="flex justify-end gap-3 mt-6">
          <GenesisButton
            @click="goToUpdateFormView(editorialPlaylistSong)"
            class="btn btn-outline btn-neutral"
          >
            <EditIcon class="mr-2" />
            <span>{{ $t('button.update') }}</span>
          </GenesisButton>
          <GenesisButton
            @click="openDeletePopup(editorialPlaylistSong)"
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
    :message="`Êtes-vous sûr de vouloir supprimer EditorialPlaylistSong editorialPlaylistSong?.id ?`"
    subMessage="Cette action est irréversible."
    @confirm="confirmDelete"
    @cancel="closePopup"
  />
</template>

<script setup lang="ts">
import type { PropType } from 'vue'

import type { EditorialPlaylistSong } from '@/models/EditorialPlaylistSongModel'
import GenesisButton from '@/components/button/GenesisButton.vue'
import { usePopup } from '@/composables/usePopup'
import { useEditorialPlaylistSongs } from '@/composables/useEditorialPlaylistSongs'
import DeleteConfirmationPopup from '@/components/popup/DeleteConfirmationPopup.vue'
import TrashIcon from '@/components/icons/TrashIcon.vue'
import EditIcon from '@/components/icons/EditIcon.vue'
import { useDateFormat } from '@/composables/useDateFormat'

const props = defineProps({
  editorialPlaylistSong: {
    type: Object as PropType<EditorialPlaylistSong>,
    required: true,
  },
})
// Stores
const { closePopup, openPopup, visible: deletePopup } = usePopup(false)
const { deleteEditorialPlaylistSong, goToListView, goToUpdateFormView } =
  useEditorialPlaylistSongs()

// Methods
const openDeletePopup = (entity: EditorialPlaylistSong) => {
  if (!entity) return
  openPopup()
}

const confirmDelete = async () => {
  const { success, error } = await deleteEditorialPlaylistSong(props.editorialPlaylistSong)
  if (!success) console.error(error)
  closePopup()
  goToListView()
}

const { formatDate } = useDateFormat()
</script>
<style scoped></style>
