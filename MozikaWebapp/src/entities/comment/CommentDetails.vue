<template>
  <div class="card bg-base-100">
    <div class="card-body">
      <div v-if="comment">
        <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
          <div>
            <label for="commentId" class="block text-sm font-medium mb-1">Id</label>
            <div id="commentId" class="input w-full bg-base-100 cursor-default">
              <span>{{ comment.id ? $n(comment.id, 'decimal') : '--' }}</span>
            </div>
          </div>
          <div>
            <label for="commentAuthoruseridUsers" class="block text-sm font-medium mb-1"
              >User</label
            >
            <div id="commentAuthoruseridUsers" class="input w-full bg-base-100 cursor-default">
              <span>{{ comment.authoruseridUsers?.getKeyValue() ?? '' }}</span>
            </div>
          </div>
          <div>
            <label
              for="commentTargettypeidInteractionTargets"
              class="block text-sm font-medium mb-1"
              >Interaction target</label
            >
            <div
              id="commentTargettypeidInteractionTargets"
              class="input w-full bg-base-100 cursor-default"
            >
              <span>{{ comment.targettypeidInteractionTargets?.getKeyValue() ?? '' }}</span>
            </div>
          </div>
          <div>
            <label for="commentTargetId" class="block text-sm font-medium mb-1">Target id</label>
            <div id="commentTargetId" class="input w-full bg-base-100 cursor-default">
              <span>{{ comment.targetId ? $n(comment.targetId, 'decimal') : '--' }}</span>
            </div>
          </div>
          <div>
            <label for="commentContent" class="block text-sm font-medium mb-1">Content</label>
            <div id="commentContent" class="input w-full bg-base-100 cursor-default">
              <span>{{ comment.content ?? '' }}</span>
            </div>
          </div>
          <div>
            <label for="commentIsHidden" class="block text-sm font-medium mb-1">Is hidden</label>
            <div id="commentIsHidden" class="input w-full bg-base-100 cursor-default">
              <span>{{ comment.isHidden ?? '' }}</span>
            </div>
          </div>
          <div>
            <label for="commentCreatedAt" class="block text-sm font-medium mb-1">Created at</label>
            <div id="commentCreatedAt" class="input w-full bg-base-100 cursor-default">
              <span>{{ comment.createdAt ?? '' }}</span>
            </div>
          </div>
        </div>
        <div class="flex justify-end gap-3 mt-6">
          <GenesisButton @click="goToUpdateFormView(comment)" class="btn btn-outline btn-neutral">
            <EditIcon class="mr-2" />
            <span>{{ $t('button.update') }}</span>
          </GenesisButton>
          <GenesisButton
            @click="openDeletePopup(comment)"
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
    :message="`Êtes-vous sûr de vouloir supprimer Comment comment?.id ?`"
    subMessage="Cette action est irréversible."
    @confirm="confirmDelete"
    @cancel="closePopup"
  />
</template>

<script setup lang="ts">
import type { PropType } from 'vue'

import type { Comment } from '@/models/CommentModel'
import GenesisButton from '@/components/button/GenesisButton.vue'
import { usePopup } from '@/composables/usePopup'
import { useComments } from '@/composables/useComments'
import DeleteConfirmationPopup from '@/components/popup/DeleteConfirmationPopup.vue'
import TrashIcon from '@/components/icons/TrashIcon.vue'
import EditIcon from '@/components/icons/EditIcon.vue'
import { useDateFormat } from '@/composables/useDateFormat'

const props = defineProps({
  comment: {
    type: Object as PropType<Comment>,
    required: true,
  },
})
// Stores
const { closePopup, openPopup, visible: deletePopup } = usePopup(false)
const { deleteComment, goToListView, goToUpdateFormView } = useComments()

// Methods
const openDeletePopup = (entity: Comment) => {
  if (!entity) return
  openPopup()
}

const confirmDelete = async () => {
  const { success, error } = await deleteComment(props.comment)
  if (!success) console.error(error)
  closePopup()
  goToListView()
}

const { formatDate } = useDateFormat()
</script>
<style scoped></style>
