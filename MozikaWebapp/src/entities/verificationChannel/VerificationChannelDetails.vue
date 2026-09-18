<template>
  <div class="card bg-base-100">
    <div class="card-body">
      <div v-if="verificationChannel">
        <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
          <div>
            <label for="verificationChannelId" class="block text-sm font-medium mb-1">Id</label>
            <div id="verificationChannelId" class="input w-full bg-base-100 cursor-default">
              <span>{{
                verificationChannel.id ? $n(verificationChannel.id, 'decimal') : '--'
              }}</span>
            </div>
          </div>
          <div>
            <label for="verificationChannelName" class="block text-sm font-medium mb-1">Name</label>
            <div id="verificationChannelName" class="input w-full bg-base-100 cursor-default">
              <span>{{ verificationChannel.name ?? '' }}</span>
            </div>
          </div>
        </div>
        <div class="flex justify-end gap-3 mt-6">
          <GenesisButton
            @click="goToUpdateFormView(verificationChannel)"
            class="btn btn-outline btn-neutral"
          >
            <EditIcon class="mr-2" />
            <span>{{ $t('button.update') }}</span>
          </GenesisButton>
          <GenesisButton
            @click="openDeletePopup(verificationChannel)"
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
    :message="`Êtes-vous sûr de vouloir supprimer VerificationChannel verificationChannel?.id ?`"
    subMessage="Cette action est irréversible."
    @confirm="confirmDelete"
    @cancel="closePopup"
  />
</template>

<script setup lang="ts">
import type { PropType } from 'vue'

import type { VerificationChannel } from '@/models/VerificationChannelModel'
import GenesisButton from '@/components/button/GenesisButton.vue'
import { usePopup } from '@/composables/usePopup'
import { useVerificationChannels } from '@/composables/useVerificationChannels'
import DeleteConfirmationPopup from '@/components/popup/DeleteConfirmationPopup.vue'
import TrashIcon from '@/components/icons/TrashIcon.vue'
import EditIcon from '@/components/icons/EditIcon.vue'
import { useDateFormat } from '@/composables/useDateFormat'

const props = defineProps({
  verificationChannel: {
    type: Object as PropType<VerificationChannel>,
    required: true,
  },
})
// Stores
const { closePopup, openPopup, visible: deletePopup } = usePopup(false)
const { deleteVerificationChannel, goToListView, goToUpdateFormView } = useVerificationChannels()

// Methods
const openDeletePopup = (entity: VerificationChannel) => {
  if (!entity) return
  openPopup()
}

const confirmDelete = async () => {
  const { success, error } = await deleteVerificationChannel(props.verificationChannel)
  if (!success) console.error(error)
  closePopup()
  goToListView()
}

const { formatDate } = useDateFormat()
</script>
<style scoped></style>
