<template>
  <div class="card bg-base-100">
    <div class="card-body">
      <div v-if="download">
        <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
          <div>
            <label for="downloadId" class="block text-sm font-medium mb-1">Id</label>
            <div id="downloadId" class="input w-full bg-base-100 cursor-default">
              <span>{{ download.id ? $n(download.id, 'decimal') : '--' }}</span>
            </div>
          </div>
          <div>
            <label for="downloadUseridUsers" class="block text-sm font-medium mb-1">User</label>
            <div id="downloadUseridUsers" class="input w-full bg-base-100 cursor-default">
              <span>{{ download.useridUsers?.getKeyValue() ?? '' }}</span>
            </div>
          </div>
          <div>
            <label for="downloadPlaylistidPlaylists" class="block text-sm font-medium mb-1"
              >Playlist</label
            >
            <div id="downloadPlaylistidPlaylists" class="input w-full bg-base-100 cursor-default">
              <span>{{ download.playlistidPlaylists?.getKeyValue() ?? '' }}</span>
            </div>
          </div>
          <div>
            <label for="downloadDownloadedAt" class="block text-sm font-medium mb-1"
              >Downloaded at</label
            >
            <div id="downloadDownloadedAt" class="input w-full bg-base-100 cursor-default">
              <span>{{ download.downloadedAt ?? '' }}</span>
            </div>
          </div>
          <div>
            <label for="downloadExpiresAt" class="block text-sm font-medium mb-1">Expires at</label>
            <div id="downloadExpiresAt" class="input w-full bg-base-100 cursor-default">
              <span>{{ download.expiresAt ?? '' }}</span>
            </div>
          </div>
        </div>
        <div class="flex justify-end gap-3 mt-6">
          <GenesisButton @click="goToUpdateFormView(download)" class="btn btn-outline btn-neutral">
            <EditIcon class="mr-2" />
            <span>{{ $t('button.update') }}</span>
          </GenesisButton>
          <GenesisButton
            @click="openDeletePopup(download)"
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
    :message="`Êtes-vous sûr de vouloir supprimer Download download?.id ?`"
    subMessage="Cette action est irréversible."
    @confirm="confirmDelete"
    @cancel="closePopup"
  />
</template>

<script setup lang="ts">
import type { PropType } from 'vue'

import type { Download } from '@/models/DownloadModel'
import GenesisButton from '@/components/button/GenesisButton.vue'
import { usePopup } from '@/composables/usePopup'
import { useDownloads } from '@/composables/useDownloads'
import DeleteConfirmationPopup from '@/components/popup/DeleteConfirmationPopup.vue'
import TrashIcon from '@/components/icons/TrashIcon.vue'
import EditIcon from '@/components/icons/EditIcon.vue'
import { useDateFormat } from '@/composables/useDateFormat'

const props = defineProps({
  download: {
    type: Object as PropType<Download>,
    required: true,
  },
})
// Stores
const { closePopup, openPopup, visible: deletePopup } = usePopup(false)
const { deleteDownload, goToListView, goToUpdateFormView } = useDownloads()

// Methods
const openDeletePopup = (entity: Download) => {
  if (!entity) return
  openPopup()
}

const confirmDelete = async () => {
  const { success, error } = await deleteDownload(props.download)
  if (!success) console.error(error)
  closePopup()
  goToListView()
}

const { formatDate } = useDateFormat()
</script>
<style scoped></style>
