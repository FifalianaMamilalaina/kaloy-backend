<template>
  <div class="card bg-base-100">
    <div class="card-body">
      <div v-if="audioStorageType">
        <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
          <div>
            <label for="audioStorageTypeId" class="block text-sm font-medium mb-1">Id</label>
            <div id="audioStorageTypeId" class="input w-full bg-base-100 cursor-default">
              <span>{{ audioStorageType.id ? $n(audioStorageType.id, 'decimal') : '--' }}</span>
            </div>
          </div>
          <div>
            <label for="audioStorageTypeName" class="block text-sm font-medium mb-1">Name</label>
            <div id="audioStorageTypeName" class="input w-full bg-base-100 cursor-default">
              <span>{{ audioStorageType.name ?? '' }}</span>
            </div>
          </div>
        </div>
        <div class="flex justify-end gap-3 mt-6">
          <GenesisButton
            @click="goToUpdateFormView(audioStorageType)"
            class="btn btn-outline btn-neutral"
          >
            <EditIcon class="mr-2" />
            <span>{{ $t('button.update') }}</span>
          </GenesisButton>
          <GenesisButton
            @click="openDeletePopup(audioStorageType)"
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
    :message="`Êtes-vous sûr de vouloir supprimer AudioStorageType audioStorageType?.id ?`"
    subMessage="Cette action est irréversible."
    @confirm="confirmDelete"
    @cancel="closePopup"
  />
</template>

<script setup lang="ts">
import type { PropType } from 'vue'

import type { AudioStorageType } from '@/models/AudioStorageTypeModel'
import GenesisButton from '@/components/button/GenesisButton.vue'
import { usePopup } from '@/composables/usePopup'
import { useAudioStorageTypes } from '@/composables/useAudioStorageTypes'
import DeleteConfirmationPopup from '@/components/popup/DeleteConfirmationPopup.vue'
import TrashIcon from '@/components/icons/TrashIcon.vue'
import EditIcon from '@/components/icons/EditIcon.vue'
import { useDateFormat } from '@/composables/useDateFormat'

const props = defineProps({
  audioStorageType: {
    type: Object as PropType<AudioStorageType>,
    required: true,
  },
})
// Stores
const { closePopup, openPopup, visible: deletePopup } = usePopup(false)
const { deleteAudioStorageType, goToListView, goToUpdateFormView } = useAudioStorageTypes()

// Methods
const openDeletePopup = (entity: AudioStorageType) => {
  if (!entity) return
  openPopup()
}

const confirmDelete = async () => {
  const { success, error } = await deleteAudioStorageType(props.audioStorageType)
  if (!success) console.error(error)
  closePopup()
  goToListView()
}

const { formatDate } = useDateFormat()
</script>
<style scoped></style>
