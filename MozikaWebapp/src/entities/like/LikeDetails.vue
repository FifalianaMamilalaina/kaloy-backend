<template>
  <div class="card bg-base-100">
    <div class="card-body">
      <div v-if="like">
        <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
          <div>
            <label for="likeId" class="block text-sm font-medium mb-1">Id</label>
            <div id="likeId" class="input w-full bg-base-100 cursor-default">
              <span>{{ like.id ? $n(like.id, 'decimal') : '--' }}</span>
            </div>
          </div>
          <div>
            <label for="likeUseridUsers" class="block text-sm font-medium mb-1">User</label>
            <div id="likeUseridUsers" class="input w-full bg-base-100 cursor-default">
              <span>{{ like.useridUsers?.getKeyValue() ?? '' }}</span>
            </div>
          </div>
          <div>
            <label for="likeTargettypeidInteractionTargets" class="block text-sm font-medium mb-1"
              >Interaction target</label
            >
            <div
              id="likeTargettypeidInteractionTargets"
              class="input w-full bg-base-100 cursor-default"
            >
              <span>{{ like.targettypeidInteractionTargets?.getKeyValue() ?? '' }}</span>
            </div>
          </div>
          <div>
            <label for="likeTargetId" class="block text-sm font-medium mb-1">Target id</label>
            <div id="likeTargetId" class="input w-full bg-base-100 cursor-default">
              <span>{{ like.targetId ? $n(like.targetId, 'decimal') : '--' }}</span>
            </div>
          </div>
          <div>
            <label for="likeCreatedAt" class="block text-sm font-medium mb-1">Created at</label>
            <div id="likeCreatedAt" class="input w-full bg-base-100 cursor-default">
              <span>{{ like.createdAt ?? '' }}</span>
            </div>
          </div>
        </div>
        <div class="flex justify-end gap-3 mt-6">
          <GenesisButton @click="goToUpdateFormView(like)" class="btn btn-outline btn-neutral">
            <EditIcon class="mr-2" />
            <span>{{ $t('button.update') }}</span>
          </GenesisButton>
          <GenesisButton
            @click="openDeletePopup(like)"
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
    :message="`Êtes-vous sûr de vouloir supprimer Like like?.id ?`"
    subMessage="Cette action est irréversible."
    @confirm="confirmDelete"
    @cancel="closePopup"
  />
</template>

<script setup lang="ts">
import type { PropType } from 'vue'

import type { Like } from '@/models/LikeModel'
import GenesisButton from '@/components/button/GenesisButton.vue'
import { usePopup } from '@/composables/usePopup'
import { useLikes } from '@/composables/useLikes'
import DeleteConfirmationPopup from '@/components/popup/DeleteConfirmationPopup.vue'
import TrashIcon from '@/components/icons/TrashIcon.vue'
import EditIcon from '@/components/icons/EditIcon.vue'
import { useDateFormat } from '@/composables/useDateFormat'

const props = defineProps({
  like: {
    type: Object as PropType<Like>,
    required: true,
  },
})
// Stores
const { closePopup, openPopup, visible: deletePopup } = usePopup(false)
const { deleteLike, goToListView, goToUpdateFormView } = useLikes()

// Methods
const openDeletePopup = (entity: Like) => {
  if (!entity) return
  openPopup()
}

const confirmDelete = async () => {
  const { success, error } = await deleteLike(props.like)
  if (!success) console.error(error)
  closePopup()
  goToListView()
}

const { formatDate } = useDateFormat()
</script>
<style scoped></style>
