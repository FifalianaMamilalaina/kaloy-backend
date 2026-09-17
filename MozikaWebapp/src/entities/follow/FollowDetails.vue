<template>
  <div class="card bg-base-100">
    <div class="card-body">
      <div v-if="follow">
        <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
          <div>
            <label for="followId" class="block text-sm font-medium mb-1">Id</label>
            <div id="followId" class="input w-full bg-base-100 cursor-default">
              <span>{{ follow.id ? $n(follow.id, 'decimal') : '--' }}</span>
            </div>
          </div>
          <div>
            <label for="followClientuseridUsers" class="block text-sm font-medium mb-1">User</label>
            <div id="followClientuseridUsers" class="input w-full bg-base-100 cursor-default">
              <span>{{ follow.clientuseridUsers?.getKeyValue() ?? '' }}</span>
            </div>
          </div>
          <div>
            <label for="followArtistidArtists" class="block text-sm font-medium mb-1">Artist</label>
            <div id="followArtistidArtists" class="input w-full bg-base-100 cursor-default">
              <span>{{ follow.artistidArtists?.getKeyValue() ?? '' }}</span>
            </div>
          </div>
          <div>
            <label for="followCreatedAt" class="block text-sm font-medium mb-1">Created at</label>
            <div id="followCreatedAt" class="input w-full bg-base-100 cursor-default">
              <span>{{ follow.createdAt ?? '' }}</span>
            </div>
          </div>
        </div>
        <div class="flex justify-end gap-3 mt-6">
          <GenesisButton @click="goToUpdateFormView(follow)" class="btn btn-outline btn-neutral">
            <EditIcon class="mr-2" />
            <span>{{ $t('button.update') }}</span>
          </GenesisButton>
          <GenesisButton
            @click="openDeletePopup(follow)"
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
    :message="`Êtes-vous sûr de vouloir supprimer Follow follow?.id ?`"
    subMessage="Cette action est irréversible."
    @confirm="confirmDelete"
    @cancel="closePopup"
  />
</template>

<script setup lang="ts">
import type { PropType } from 'vue'

import type { Follow } from '@/models/FollowModel'
import GenesisButton from '@/components/button/GenesisButton.vue'
import { usePopup } from '@/composables/usePopup'
import { useFollows } from '@/composables/useFollows'
import DeleteConfirmationPopup from '@/components/popup/DeleteConfirmationPopup.vue'
import TrashIcon from '@/components/icons/TrashIcon.vue'
import EditIcon from '@/components/icons/EditIcon.vue'
import { useDateFormat } from '@/composables/useDateFormat'

const props = defineProps({
  follow: {
    type: Object as PropType<Follow>,
    required: true,
  },
})
// Stores
const { closePopup, openPopup, visible: deletePopup } = usePopup(false)
const { deleteFollow, goToListView, goToUpdateFormView } = useFollows()

// Methods
const openDeletePopup = (entity: Follow) => {
  if (!entity) return
  openPopup()
}

const confirmDelete = async () => {
  const { success, error } = await deleteFollow(props.follow)
  if (!success) console.error(error)
  closePopup()
  goToListView()
}

const { formatDate } = useDateFormat()
</script>
<style scoped></style>
