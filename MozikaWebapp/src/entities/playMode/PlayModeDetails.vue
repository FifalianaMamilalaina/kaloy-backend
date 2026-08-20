<template>
  <div class="card bg-base-100">
    <div class="card-body">
      <div v-if="playMode">
        <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
          <div>
            <label for="playModeId" class="block text-sm font-medium mb-1">Id</label>
            <div id="playModeId" class="input w-full bg-base-100 cursor-default">
              <span>{{ playMode.id ? $n(playMode.id, 'decimal') : '--' }}</span>
            </div>
          </div>
          <div>
            <label for="playModeName" class="block text-sm font-medium mb-1">Name</label>
            <div id="playModeName" class="input w-full bg-base-100 cursor-default">
              <span>{{ playMode.name ?? '' }}</span>
            </div>
          </div>
        </div>
        <div class="flex justify-end gap-3 mt-6">
          <GenesisButton @click="goToUpdateFormView(playMode)" class="btn btn-outline btn-neutral">
            <EditIcon class="mr-2" />
            <span>{{ $t('button.update') }}</span>
          </GenesisButton>
          <GenesisButton
            @click="openDeletePopup(playMode)"
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
    :message="`Êtes-vous sûr de vouloir supprimer PlayMode playMode?.id ?`"
    subMessage="Cette action est irréversible."
    @confirm="confirmDelete"
    @cancel="closePopup"
  />
</template>

<script setup lang="ts">
import type { PropType } from 'vue'

import type { PlayMode } from '@/models/PlayModeModel'
import GenesisButton from '@/components/button/GenesisButton.vue'
import { usePopup } from '@/composables/usePopup'
import { usePlayModes } from '@/composables/usePlayModes'
import DeleteConfirmationPopup from '@/components/popup/DeleteConfirmationPopup.vue'
import TrashIcon from '@/components/icons/TrashIcon.vue'
import EditIcon from '@/components/icons/EditIcon.vue'
import { useDateFormat } from '@/composables/useDateFormat'

const props = defineProps({
  playMode: {
    type: Object as PropType<PlayMode>,
    required: true,
  },
})
// Stores
const { closePopup, openPopup, visible: deletePopup } = usePopup(false)
const { deletePlayMode, goToListView, goToUpdateFormView } = usePlayModes()

// Methods
const openDeletePopup = (entity: PlayMode) => {
  if (!entity) return
  openPopup()
}

const confirmDelete = async () => {
  const { success, error } = await deletePlayMode(props.playMode)
  if (!success) console.error(error)
  closePopup()
  goToListView()
}

const { formatDate } = useDateFormat()
</script>
<style scoped></style>
