<template>
  <div class="card bg-base-100">
    <div class="card-body">
      <div v-if="userStatusHistory">
        <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
          <div>
            <label for="userStatusHistoryId" class="block text-sm font-medium mb-1">Id</label>
            <div id="userStatusHistoryId" class="input w-full bg-base-100 cursor-default">
              <span>{{ userStatusHistory.id ? $n(userStatusHistory.id, 'decimal') : '--' }}</span>
            </div>
          </div>
          <div>
            <label for="userStatusHistoryUseridUsers" class="block text-sm font-medium mb-1"
              >User</label
            >
            <div id="userStatusHistoryUseridUsers" class="input w-full bg-base-100 cursor-default">
              <span>{{ userStatusHistory.useridUsers?.getKeyValue() ?? '' }}</span>
            </div>
          </div>
          <div>
            <label
              for="userStatusHistoryPreviousstatusidUserStatuses"
              class="block text-sm font-medium mb-1"
              >User statuse</label
            >
            <div
              id="userStatusHistoryPreviousstatusidUserStatuses"
              class="input w-full bg-base-100 cursor-default"
            >
              <span>{{ userStatusHistory.previousstatusidUserStatuses?.getKeyValue() ?? '' }}</span>
            </div>
          </div>
          <div>
            <label
              for="userStatusHistoryNewstatusidUserStatuses"
              class="block text-sm font-medium mb-1"
              >User statuse</label
            >
            <div
              id="userStatusHistoryNewstatusidUserStatuses"
              class="input w-full bg-base-100 cursor-default"
            >
              <span>{{ userStatusHistory.newstatusidUserStatuses?.getKeyValue() ?? '' }}</span>
            </div>
          </div>
          <div>
            <label for="userStatusHistoryReason" class="block text-sm font-medium mb-1"
              >Reason</label
            >
            <div id="userStatusHistoryReason" class="input w-full bg-base-100 cursor-default">
              <span>{{ userStatusHistory.reason ?? '' }}</span>
            </div>
          </div>
          <div>
            <label
              for="userStatusHistoryChangedbyuseridUsers"
              class="block text-sm font-medium mb-1"
              >User</label
            >
            <div
              id="userStatusHistoryChangedbyuseridUsers"
              class="input w-full bg-base-100 cursor-default"
            >
              <span>{{ userStatusHistory.changedbyuseridUsers?.getKeyValue() ?? '' }}</span>
            </div>
          </div>
          <div>
            <label for="userStatusHistoryCreatedAt" class="block text-sm font-medium mb-1"
              >Created at</label
            >
            <div id="userStatusHistoryCreatedAt" class="input w-full bg-base-100 cursor-default">
              <span>{{ userStatusHistory.createdAt ?? '' }}</span>
            </div>
          </div>
        </div>
        <div class="flex justify-end gap-3 mt-6">
          <GenesisButton
            @click="goToUpdateFormView(userStatusHistory)"
            class="btn btn-outline btn-neutral"
          >
            <EditIcon class="mr-2" />
            <span>{{ $t('button.update') }}</span>
          </GenesisButton>
          <GenesisButton
            @click="openDeletePopup(userStatusHistory)"
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
    :message="`Êtes-vous sûr de vouloir supprimer UserStatusHistory userStatusHistory?.id ?`"
    subMessage="Cette action est irréversible."
    @confirm="confirmDelete"
    @cancel="closePopup"
  />
</template>

<script setup lang="ts">
import type { PropType } from 'vue'

import type { UserStatusHistory } from '@/models/UserStatusHistoryModel'
import GenesisButton from '@/components/button/GenesisButton.vue'
import { usePopup } from '@/composables/usePopup'
import { useUserStatusHistorys } from '@/composables/useUserStatusHistorys'
import DeleteConfirmationPopup from '@/components/popup/DeleteConfirmationPopup.vue'
import TrashIcon from '@/components/icons/TrashIcon.vue'
import EditIcon from '@/components/icons/EditIcon.vue'
import { useDateFormat } from '@/composables/useDateFormat'

const props = defineProps({
  userStatusHistory: {
    type: Object as PropType<UserStatusHistory>,
    required: true,
  },
})
// Stores
const { closePopup, openPopup, visible: deletePopup } = usePopup(false)
const { deleteUserStatusHistory, goToListView, goToUpdateFormView } = useUserStatusHistorys()

// Methods
const openDeletePopup = (entity: UserStatusHistory) => {
  if (!entity) return
  openPopup()
}

const confirmDelete = async () => {
  const { success, error } = await deleteUserStatusHistory(props.userStatusHistory)
  if (!success) console.error(error)
  closePopup()
  goToListView()
}

const { formatDate } = useDateFormat()
</script>
<style scoped></style>
