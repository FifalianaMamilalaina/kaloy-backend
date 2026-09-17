<template>
  <div class="card bg-base-100">
    <div class="card-body">
      <div v-if="participationStatuse">
        <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
          <div>
            <label for="participationStatuseId" class="block text-sm font-medium mb-1">Id</label>
            <div id="participationStatuseId" class="input w-full bg-base-100 cursor-default">
              <span>{{
                participationStatuse.id ? $n(participationStatuse.id, 'decimal') : '--'
              }}</span>
            </div>
          </div>
          <div>
            <label for="participationStatuseName" class="block text-sm font-medium mb-1"
              >Name</label
            >
            <div id="participationStatuseName" class="input w-full bg-base-100 cursor-default">
              <span>{{ participationStatuse.name ?? '' }}</span>
            </div>
          </div>
        </div>
        <div class="flex justify-end gap-3 mt-6">
          <GenesisButton
            @click="goToUpdateFormView(participationStatuse)"
            class="btn btn-outline btn-neutral"
          >
            <EditIcon class="mr-2" />
            <span>{{ $t('button.update') }}</span>
          </GenesisButton>
          <GenesisButton
            @click="openDeletePopup(participationStatuse)"
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
    :message="`Êtes-vous sûr de vouloir supprimer ParticipationStatuse participationStatuse?.id ?`"
    subMessage="Cette action est irréversible."
    @confirm="confirmDelete"
    @cancel="closePopup"
  />
</template>

<script setup lang="ts">
import type { PropType } from 'vue'

import type { ParticipationStatuse } from '@/models/ParticipationStatuseModel'
import GenesisButton from '@/components/button/GenesisButton.vue'
import { usePopup } from '@/composables/usePopup'
import { useParticipationStatuses } from '@/composables/useParticipationStatuses'
import DeleteConfirmationPopup from '@/components/popup/DeleteConfirmationPopup.vue'
import TrashIcon from '@/components/icons/TrashIcon.vue'
import EditIcon from '@/components/icons/EditIcon.vue'
import { useDateFormat } from '@/composables/useDateFormat'

const props = defineProps({
  participationStatuse: {
    type: Object as PropType<ParticipationStatuse>,
    required: true,
  },
})
// Stores
const { closePopup, openPopup, visible: deletePopup } = usePopup(false)
const { deleteParticipationStatuse, goToListView, goToUpdateFormView } = useParticipationStatuses()

// Methods
const openDeletePopup = (entity: ParticipationStatuse) => {
  if (!entity) return
  openPopup()
}

const confirmDelete = async () => {
  const { success, error } = await deleteParticipationStatuse(props.participationStatuse)
  if (!success) console.error(error)
  closePopup()
  goToListView()
}

const { formatDate } = useDateFormat()
</script>
<style scoped></style>
