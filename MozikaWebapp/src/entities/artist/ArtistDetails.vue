<template>
  <div class="card bg-base-100">
    <div class="card-body">
      <div v-if="artist">
        <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
          <div>
            <label for="artistId" class="block text-sm font-medium mb-1">Id</label>
            <div id="artistId" class="input w-full bg-base-100 cursor-default">
              <span>{{ artist.id ? $n(artist.id, 'decimal') : '--' }}</span>
            </div>
          </div>
          <div>
            <label for="artistUseridUsers" class="block text-sm font-medium mb-1">User</label>
            <div id="artistUseridUsers" class="input w-full bg-base-100 cursor-default">
              <span>{{ artist.useridUsers?.getKeyValue() ?? '' }}</span>
            </div>
          </div>
          <div>
            <label for="artistArtisttypeidArtistTypes" class="block text-sm font-medium mb-1"
              >Artist type</label
            >
            <div id="artistArtisttypeidArtistTypes" class="input w-full bg-base-100 cursor-default">
              <span>{{ artist.artisttypeidArtistTypes?.getKeyValue() ?? '' }}</span>
            </div>
          </div>
          <div>
            <label for="artistStageName" class="block text-sm font-medium mb-1">Stage name</label>
            <div id="artistStageName" class="input w-full bg-base-100 cursor-default">
              <span>{{ artist.stageName ?? '' }}</span>
            </div>
          </div>
          <div>
            <label for="artistActiveSinceYear" class="block text-sm font-medium mb-1"
              >Active since year</label
            >
            <div id="artistActiveSinceYear" class="input w-full bg-base-100 cursor-default">
              <span>{{
                artist.activeSinceYear ? $n(artist.activeSinceYear, 'decimal') : '--'
              }}</span>
            </div>
          </div>
          <div>
            <label for="artistPhotoUrl" class="block text-sm font-medium mb-1">Photo url</label>
            <div id="artistPhotoUrl" class="input w-full bg-base-100 cursor-default">
              <span>{{ artist.photoUrl ?? '' }}</span>
            </div>
          </div>
          <div>
            <label for="artistBio" class="block text-sm font-medium mb-1">Bio</label>
            <div id="artistBio" class="input w-full bg-base-100 cursor-default">
              <span>{{ artist.bio ?? '' }}</span>
            </div>
          </div>
          <div>
            <label
              for="artistVerificationstatusidVerificationStatuses"
              class="block text-sm font-medium mb-1"
              >Verification statuse</label
            >
            <div
              id="artistVerificationstatusidVerificationStatuses"
              class="input w-full bg-base-100 cursor-default"
            >
              <span>{{
                artist.verificationstatusidVerificationStatuses?.getKeyValue() ?? ''
              }}</span>
            </div>
          </div>
          <div>
            <label for="artistVerifiedAt" class="block text-sm font-medium mb-1">Verified at</label>
            <div id="artistVerifiedAt" class="input w-full bg-base-100 cursor-default">
              <span>{{ artist.verifiedAt ?? '' }}</span>
            </div>
          </div>
          <div>
            <label for="artistIsCertified" class="block text-sm font-medium mb-1"
              >Is certified</label
            >
            <div id="artistIsCertified" class="input w-full bg-base-100 cursor-default">
              <span>{{ artist.isCertified ?? '' }}</span>
            </div>
          </div>
          <div>
            <label for="artistCreatedAt" class="block text-sm font-medium mb-1">Created at</label>
            <div id="artistCreatedAt" class="input w-full bg-base-100 cursor-default">
              <span>{{ artist.createdAt ?? '' }}</span>
            </div>
          </div>
        </div>
        <div class="flex justify-end gap-3 mt-6">
          <GenesisButton @click="goToUpdateFormView(artist)" class="btn btn-outline btn-neutral">
            <EditIcon class="mr-2" />
            <span>{{ $t('button.update') }}</span>
          </GenesisButton>
          <GenesisButton
            @click="openDeletePopup(artist)"
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
    :message="`Êtes-vous sûr de vouloir supprimer Artist artist?.id ?`"
    subMessage="Cette action est irréversible."
    @confirm="confirmDelete"
    @cancel="closePopup"
  />
</template>

<script setup lang="ts">
import type { PropType } from 'vue'

import type { Artist } from '@/models/ArtistModel'
import GenesisButton from '@/components/button/GenesisButton.vue'
import { usePopup } from '@/composables/usePopup'
import { useArtists } from '@/composables/useArtists'
import DeleteConfirmationPopup from '@/components/popup/DeleteConfirmationPopup.vue'
import TrashIcon from '@/components/icons/TrashIcon.vue'
import EditIcon from '@/components/icons/EditIcon.vue'
import { useDateFormat } from '@/composables/useDateFormat'

const props = defineProps({
  artist: {
    type: Object as PropType<Artist>,
    required: true,
  },
})
// Stores
const { closePopup, openPopup, visible: deletePopup } = usePopup(false)
const { deleteArtist, goToListView, goToUpdateFormView } = useArtists()

// Methods
const openDeletePopup = (entity: Artist) => {
  if (!entity) return
  openPopup()
}

const confirmDelete = async () => {
  const { success, error } = await deleteArtist(props.artist)
  if (!success) console.error(error)
  closePopup()
  goToListView()
}

const { formatDate } = useDateFormat()
</script>
<style scoped></style>
