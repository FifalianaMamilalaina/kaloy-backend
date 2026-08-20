<template>
  <div class="card bg-base-100">
    <div class="card-body">
      <div v-if="editorialPlaylist">
        <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
          <div>
            <label for="editorialPlaylistId" class="block text-sm font-medium mb-1">Id</label>
            <div id="editorialPlaylistId" class="input w-full bg-base-100 cursor-default">
              <span>{{ editorialPlaylist.id ? $n(editorialPlaylist.id, 'decimal') : '--' }}</span>
            </div>
          </div>
          <div>
            <label for="editorialPlaylistArtistidArtists" class="block text-sm font-medium mb-1"
              >Artist</label
            >
            <div
              id="editorialPlaylistArtistidArtists"
              class="input w-full bg-base-100 cursor-default"
            >
              <span>{{ editorialPlaylist.artistidArtists?.getKeyValue() ?? '' }}</span>
            </div>
          </div>
          <div>
            <label for="editorialPlaylistTitle" class="block text-sm font-medium mb-1">Title</label>
            <div id="editorialPlaylistTitle" class="input w-full bg-base-100 cursor-default">
              <span>{{ editorialPlaylist.title ?? '' }}</span>
            </div>
          </div>
          <div>
            <label for="editorialPlaylistDescription" class="block text-sm font-medium mb-1"
              >Description</label
            >
            <div id="editorialPlaylistDescription" class="input w-full bg-base-100 cursor-default">
              <span>{{ editorialPlaylist.description ?? '' }}</span>
            </div>
          </div>
          <div>
            <label for="editorialPlaylistCoverUrl" class="block text-sm font-medium mb-1"
              >Cover url</label
            >
            <div id="editorialPlaylistCoverUrl" class="input w-full bg-base-100 cursor-default">
              <span>{{ editorialPlaylist.coverUrl ?? '' }}</span>
            </div>
          </div>
          <div>
            <label for="editorialPlaylistIsFeatured" class="block text-sm font-medium mb-1"
              >Is featured</label
            >
            <div id="editorialPlaylistIsFeatured" class="input w-full bg-base-100 cursor-default">
              <span>{{ editorialPlaylist.isFeatured ?? '' }}</span>
            </div>
          </div>
          <div>
            <label for="editorialPlaylistCreatedAt" class="block text-sm font-medium mb-1"
              >Created at</label
            >
            <div id="editorialPlaylistCreatedAt" class="input w-full bg-base-100 cursor-default">
              <span>{{ editorialPlaylist.createdAt ?? '' }}</span>
            </div>
          </div>
        </div>
        <div class="flex justify-end gap-3 mt-6">
          <GenesisButton
            @click="goToUpdateFormView(editorialPlaylist)"
            class="btn btn-outline btn-neutral"
          >
            <EditIcon class="mr-2" />
            <span>{{ $t('button.update') }}</span>
          </GenesisButton>
          <GenesisButton
            @click="openDeletePopup(editorialPlaylist)"
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
    :message="`Êtes-vous sûr de vouloir supprimer EditorialPlaylist editorialPlaylist?.id ?`"
    subMessage="Cette action est irréversible."
    @confirm="confirmDelete"
    @cancel="closePopup"
  />
</template>

<script setup lang="ts">
import type { PropType } from 'vue'

import type { EditorialPlaylist } from '@/models/EditorialPlaylistModel'
import GenesisButton from '@/components/button/GenesisButton.vue'
import { usePopup } from '@/composables/usePopup'
import { useEditorialPlaylists } from '@/composables/useEditorialPlaylists'
import DeleteConfirmationPopup from '@/components/popup/DeleteConfirmationPopup.vue'
import TrashIcon from '@/components/icons/TrashIcon.vue'
import EditIcon from '@/components/icons/EditIcon.vue'
import { useDateFormat } from '@/composables/useDateFormat'

const props = defineProps({
  editorialPlaylist: {
    type: Object as PropType<EditorialPlaylist>,
    required: true,
  },
})
// Stores
const { closePopup, openPopup, visible: deletePopup } = usePopup(false)
const { deleteEditorialPlaylist, goToListView, goToUpdateFormView } = useEditorialPlaylists()

// Methods
const openDeletePopup = (entity: EditorialPlaylist) => {
  if (!entity) return
  openPopup()
}

const confirmDelete = async () => {
  const { success, error } = await deleteEditorialPlaylist(props.editorialPlaylist)
  if (!success) console.error(error)
  closePopup()
  goToListView()
}

const { formatDate } = useDateFormat()
</script>
<style scoped></style>
