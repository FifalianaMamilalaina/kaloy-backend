<template>
  <div class="card bg-base-100">
    <div class="card-body">
      <div v-if="album">
        <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
          <div>
            <label for="albumId" class="block text-sm font-medium mb-1">Id</label>
            <div id="albumId" class="input w-full bg-base-100 cursor-default">
              <span>{{ album.id ? $n(album.id, 'decimal') : '--' }}</span>
            </div>
          </div>
          <div>
            <label for="albumArtistidArtists" class="block text-sm font-medium mb-1">Artist</label>
            <div id="albumArtistidArtists" class="input w-full bg-base-100 cursor-default">
              <span>{{ album.artistidArtists?.getKeyValue() ?? '' }}</span>
            </div>
          </div>
          <div>
            <label for="albumTitle" class="block text-sm font-medium mb-1">Title</label>
            <div id="albumTitle" class="input w-full bg-base-100 cursor-default">
              <span>{{ album.title ?? '' }}</span>
            </div>
          </div>
          <div>
            <label for="albumCoverUrl" class="block text-sm font-medium mb-1">Cover url</label>
            <div id="albumCoverUrl" class="input w-full bg-base-100 cursor-default">
              <span>{{ album.coverUrl ?? '' }}</span>
            </div>
          </div>
          <div>
            <label for="albumReleaseDate" class="block text-sm font-medium mb-1"
              >Release date</label
            >
            <div id="albumReleaseDate" class="input w-full bg-base-100 cursor-default">
              <span>{{ album.releaseDate ? $d(album.releaseDate, 'long') : '--/--/--' }}</span>
            </div>
          </div>
          <div>
            <label for="albumCreatedAt" class="block text-sm font-medium mb-1">Created at</label>
            <div id="albumCreatedAt" class="input w-full bg-base-100 cursor-default">
              <span>{{ album.createdAt ?? '' }}</span>
            </div>
          </div>
        </div>
        <div class="flex justify-end gap-3 mt-6">
          <GenesisButton @click="goToUpdateFormView(album)" class="btn btn-outline btn-neutral">
            <EditIcon class="mr-2" />
            <span>{{ $t('button.update') }}</span>
          </GenesisButton>
          <GenesisButton
            @click="openDeletePopup(album)"
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
    :message="`Êtes-vous sûr de vouloir supprimer Album album?.id ?`"
    subMessage="Cette action est irréversible."
    @confirm="confirmDelete"
    @cancel="closePopup"
  />
</template>

<script setup lang="ts">
import type { PropType } from 'vue'

import type { Album } from '@/models/AlbumModel'
import GenesisButton from '@/components/button/GenesisButton.vue'
import { usePopup } from '@/composables/usePopup'
import { useAlbums } from '@/composables/useAlbums'
import DeleteConfirmationPopup from '@/components/popup/DeleteConfirmationPopup.vue'
import TrashIcon from '@/components/icons/TrashIcon.vue'
import EditIcon from '@/components/icons/EditIcon.vue'
import { useDateFormat } from '@/composables/useDateFormat'

const props = defineProps({
  album: {
    type: Object as PropType<Album>,
    required: true,
  },
})
// Stores
const { closePopup, openPopup, visible: deletePopup } = usePopup(false)
const { deleteAlbum, goToListView, goToUpdateFormView } = useAlbums()

// Methods
const openDeletePopup = (entity: Album) => {
  if (!entity) return
  openPopup()
}

const confirmDelete = async () => {
  const { success, error } = await deleteAlbum(props.album)
  if (!success) console.error(error)
  closePopup()
  goToListView()
}

const { formatDate } = useDateFormat()
</script>
<style scoped></style>
