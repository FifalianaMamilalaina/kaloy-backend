<template>
  <div class="card bg-base-100">
    <div class="card-body">
      <div v-if="userStatuse">
        <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
          <div>
            <label for="userStatuseId" class="block text-sm font-medium mb-1">Id</label>
            <div id="userStatuseId" class="input w-full bg-base-100 cursor-default">
              <span>{{ userStatuse.id ? $n(userStatuse.id, 'decimal') : '--' }}</span>
            </div>
          </div>
          <div>
            <label for="userStatuseName" class="block text-sm font-medium mb-1">Name</label>
            <div id="userStatuseName" class="input w-full bg-base-100 cursor-default">
              <span>{{ userStatuse.name ?? '' }}</span>
            </div>
          </div>
        </div>
        <div class="flex justify-end gap-3 mt-6">
          <GenesisButton
            @click="goToUpdateFormView(userStatuse)"
            class="btn btn-outline btn-neutral"
          >
            <EditIcon class="mr-2" />
            <span>{{ $t('button.update') }}</span>
          </GenesisButton>
          <GenesisButton
            @click="openDeletePopup(userStatuse)"
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
    :message="`Êtes-vous sûr de vouloir supprimer UserStatuse userStatuse?.id ?`"
    subMessage="Cette action est irréversible."
    @confirm="confirmDelete"
    @cancel="closePopup"
  />
</template>

<script setup lang="ts">
import type { PropType } from 'vue'

import type { UserStatuse } from '@/models/UserStatuseModel'
import GenesisButton from '@/components/button/GenesisButton.vue'
import { usePopup } from '@/composables/usePopup'
import { useUserStatuses } from '@/composables/useUserStatuses'
import DeleteConfirmationPopup from '@/components/popup/DeleteConfirmationPopup.vue'
import TrashIcon from '@/components/icons/TrashIcon.vue'
import EditIcon from '@/components/icons/EditIcon.vue'
import { useDateFormat } from '@/composables/useDateFormat'

const props = defineProps({
  userStatuse: {
    type: Object as PropType<UserStatuse>,
    required: true,
  },
})
// Stores
const { closePopup, openPopup, visible: deletePopup } = usePopup(false)
const { deleteUserStatuse, goToListView, goToUpdateFormView } = useUserStatuses()

// Methods
const openDeletePopup = (entity: UserStatuse) => {
  if (!entity) return
  openPopup()
}

const confirmDelete = async () => {
  const { success, error } = await deleteUserStatuse(props.userStatuse)
  if (!success) console.error(error)
  closePopup()
  goToListView()
}

const { formatDate } = useDateFormat()
</script>
<style scoped></style>
