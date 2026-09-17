<template>
  <div class="card bg-base-100">
    <div class="card-body">
      <div v-if="interactionTarget">
        <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
          <div>
            <label for="interactionTargetId" class="block text-sm font-medium mb-1">Id</label>
            <div id="interactionTargetId" class="input w-full bg-base-100 cursor-default">
              <span>{{ interactionTarget.id ? $n(interactionTarget.id, 'decimal') : '--' }}</span>
            </div>
          </div>
          <div>
            <label for="interactionTargetName" class="block text-sm font-medium mb-1">Name</label>
            <div id="interactionTargetName" class="input w-full bg-base-100 cursor-default">
              <span>{{ interactionTarget.name ?? '' }}</span>
            </div>
          </div>
        </div>
        <div class="flex justify-end gap-3 mt-6">
          <GenesisButton
            @click="goToUpdateFormView(interactionTarget)"
            class="btn btn-outline btn-neutral"
          >
            <EditIcon class="mr-2" />
            <span>{{ $t('button.update') }}</span>
          </GenesisButton>
          <GenesisButton
            @click="openDeletePopup(interactionTarget)"
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
    :message="`Êtes-vous sûr de vouloir supprimer InteractionTarget interactionTarget?.id ?`"
    subMessage="Cette action est irréversible."
    @confirm="confirmDelete"
    @cancel="closePopup"
  />
</template>

<script setup lang="ts">
import type { PropType } from 'vue'

import type { InteractionTarget } from '@/models/InteractionTargetModel'
import GenesisButton from '@/components/button/GenesisButton.vue'
import { usePopup } from '@/composables/usePopup'
import { useInteractionTargets } from '@/composables/useInteractionTargets'
import DeleteConfirmationPopup from '@/components/popup/DeleteConfirmationPopup.vue'
import TrashIcon from '@/components/icons/TrashIcon.vue'
import EditIcon from '@/components/icons/EditIcon.vue'
import { useDateFormat } from '@/composables/useDateFormat'

const props = defineProps({
  interactionTarget: {
    type: Object as PropType<InteractionTarget>,
    required: true,
  },
})
// Stores
const { closePopup, openPopup, visible: deletePopup } = usePopup(false)
const { deleteInteractionTarget, goToListView, goToUpdateFormView } = useInteractionTargets()

// Methods
const openDeletePopup = (entity: InteractionTarget) => {
  if (!entity) return
  openPopup()
}

const confirmDelete = async () => {
  const { success, error } = await deleteInteractionTarget(props.interactionTarget)
  if (!success) console.error(error)
  closePopup()
  goToListView()
}

const { formatDate } = useDateFormat()
</script>
<style scoped></style>
