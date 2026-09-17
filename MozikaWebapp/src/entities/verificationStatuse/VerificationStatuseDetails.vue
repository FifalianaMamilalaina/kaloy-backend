<template>
  <div class="card bg-base-100">
    <div class="card-body">
      <div v-if="verificationStatuse">
        <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
          <div>
            <label for="verificationStatuseId" class="block text-sm font-medium mb-1">Id</label>
            <div id="verificationStatuseId" class="input w-full bg-base-100 cursor-default">
              <span>{{
                verificationStatuse.id ? $n(verificationStatuse.id, 'decimal') : '--'
              }}</span>
            </div>
          </div>
          <div>
            <label for="verificationStatuseName" class="block text-sm font-medium mb-1">Name</label>
            <div id="verificationStatuseName" class="input w-full bg-base-100 cursor-default">
              <span>{{ verificationStatuse.name ?? '' }}</span>
            </div>
          </div>
        </div>
        <div class="flex justify-end gap-3 mt-6">
          <GenesisButton
            @click="goToUpdateFormView(verificationStatuse)"
            class="btn btn-outline btn-neutral"
          >
            <EditIcon class="mr-2" />
            <span>{{ $t('button.update') }}</span>
          </GenesisButton>
          <GenesisButton
            @click="openDeletePopup(verificationStatuse)"
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
    :message="`Êtes-vous sûr de vouloir supprimer VerificationStatuse verificationStatuse?.id ?`"
    subMessage="Cette action est irréversible."
    @confirm="confirmDelete"
    @cancel="closePopup"
  />
</template>

<script setup lang="ts">
import type { PropType } from 'vue'

import type { VerificationStatuse } from '@/models/VerificationStatuseModel'
import GenesisButton from '@/components/button/GenesisButton.vue'
import { usePopup } from '@/composables/usePopup'
import { useVerificationStatuses } from '@/composables/useVerificationStatuses'
import DeleteConfirmationPopup from '@/components/popup/DeleteConfirmationPopup.vue'
import TrashIcon from '@/components/icons/TrashIcon.vue'
import EditIcon from '@/components/icons/EditIcon.vue'
import { useDateFormat } from '@/composables/useDateFormat'

const props = defineProps({
  verificationStatuse: {
    type: Object as PropType<VerificationStatuse>,
    required: true,
  },
})
// Stores
const { closePopup, openPopup, visible: deletePopup } = usePopup(false)
const { deleteVerificationStatuse, goToListView, goToUpdateFormView } = useVerificationStatuses()

// Methods
const openDeletePopup = (entity: VerificationStatuse) => {
  if (!entity) return
  openPopup()
}

const confirmDelete = async () => {
  const { success, error } = await deleteVerificationStatuse(props.verificationStatuse)
  if (!success) console.error(error)
  closePopup()
  goToListView()
}

const { formatDate } = useDateFormat()
</script>
<style scoped></style>
