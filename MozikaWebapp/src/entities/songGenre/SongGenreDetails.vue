<template>
  <div class="card bg-base-100">
    <div class="card-body">
      <div v-if="songGenre">
        <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
          <div>
            <label for="songGenreId" class="block text-sm font-medium mb-1">Id</label>
            <div id="songGenreId" class="input w-full bg-base-100 cursor-default">
              <span>{{ songGenre.id ? $n(songGenre.id, 'decimal') : '--' }}</span>
            </div>
          </div>
          <div>
            <label for="songGenreSongidSongs" class="block text-sm font-medium mb-1">Song</label>
            <div id="songGenreSongidSongs" class="input w-full bg-base-100 cursor-default">
              <span>{{ songGenre.songidSongs?.getKeyValue() ?? '' }}</span>
            </div>
          </div>
          <div>
            <label for="songGenreGenreidGenres" class="block text-sm font-medium mb-1">Genre</label>
            <div id="songGenreGenreidGenres" class="input w-full bg-base-100 cursor-default">
              <span>{{ songGenre.genreidGenres?.getKeyValue() ?? '' }}</span>
            </div>
          </div>
        </div>
        <div class="flex justify-end gap-3 mt-6">
          <GenesisButton @click="goToUpdateFormView(songGenre)" class="btn btn-outline btn-neutral">
            <EditIcon class="mr-2" />
            <span>{{ $t('button.update') }}</span>
          </GenesisButton>
          <GenesisButton
            @click="openDeletePopup(songGenre)"
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
    :message="`Êtes-vous sûr de vouloir supprimer SongGenre songGenre?.id ?`"
    subMessage="Cette action est irréversible."
    @confirm="confirmDelete"
    @cancel="closePopup"
  />
</template>

<script setup lang="ts">
import type { PropType } from 'vue'

import type { SongGenre } from '@/models/SongGenreModel'
import GenesisButton from '@/components/button/GenesisButton.vue'
import { usePopup } from '@/composables/usePopup'
import { useSongGenres } from '@/composables/useSongGenres'
import DeleteConfirmationPopup from '@/components/popup/DeleteConfirmationPopup.vue'
import TrashIcon from '@/components/icons/TrashIcon.vue'
import EditIcon from '@/components/icons/EditIcon.vue'
import { useDateFormat } from '@/composables/useDateFormat'

const props = defineProps({
  songGenre: {
    type: Object as PropType<SongGenre>,
    required: true,
  },
})
// Stores
const { closePopup, openPopup, visible: deletePopup } = usePopup(false)
const { deleteSongGenre, goToListView, goToUpdateFormView } = useSongGenres()

// Methods
const openDeletePopup = (entity: SongGenre) => {
  if (!entity) return
  openPopup()
}

const confirmDelete = async () => {
  const { success, error } = await deleteSongGenre(props.songGenre)
  if (!success) console.error(error)
  closePopup()
  goToListView()
}

const { formatDate } = useDateFormat()
</script>
<style scoped></style>
