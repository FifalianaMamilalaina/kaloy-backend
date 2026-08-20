<template>
  <div class="card bg-base-100">
    <div class="card-body">
      <div v-if="notificationType">
        <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
          <div>
            <label for="notificationTypeId" class="block text-sm font-medium mb-1">Id</label>
            <div id="notificationTypeId" class="input w-full bg-base-100 cursor-default">
              <span>{{ notificationType.id ? $n(notificationType.id, 'decimal') : '--' }}</span>
            </div>
          </div>
          <div>
            <label for="notificationTypeName" class="block text-sm font-medium mb-1">Name</label>
            <div id="notificationTypeName" class="input w-full bg-base-100 cursor-default">
              <span>{{ notificationType.name ?? '' }}</span>
            </div>
          </div>
        </div>
        <div class="flex justify-end gap-3 mt-6">
          <GenesisButton
            @click="goToUpdateFormView(notificationType)"
            class="btn btn-outline btn-neutral"
          >
            <EditIcon class="mr-2" />
            <span>{{ $t('button.update') }}</span>
          </GenesisButton>
          <GenesisButton
            @click="openDeletePopup(notificationType)"
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
    :message="`Êtes-vous sûr de vouloir supprimer NotificationType notificationType?.id ?`"
    subMessage="Cette action est irréversible."
    @confirm="confirmDelete"
    @cancel="closePopup"
  />
</template>

<script setup lang="ts">
import type { PropType } from 'vue'

import type { NotificationType } from '@/models/NotificationTypeModel'
import GenesisButton from '@/components/button/GenesisButton.vue'
import { usePopup } from '@/composables/usePopup'
import { useNotificationTypes } from '@/composables/useNotificationTypes'
import DeleteConfirmationPopup from '@/components/popup/DeleteConfirmationPopup.vue'
import TrashIcon from '@/components/icons/TrashIcon.vue'
import EditIcon from '@/components/icons/EditIcon.vue'
import { useDateFormat } from '@/composables/useDateFormat'

const props = defineProps({
  notificationType: {
    type: Object as PropType<NotificationType>,
    required: true,
  },
})
// Stores
const { closePopup, openPopup, visible: deletePopup } = usePopup(false)
const { deleteNotificationType, goToListView, goToUpdateFormView } = useNotificationTypes()

// Methods
const openDeletePopup = (entity: NotificationType) => {
  if (!entity) return
  openPopup()
}

const confirmDelete = async () => {
  const { success, error } = await deleteNotificationType(props.notificationType)
  if (!success) console.error(error)
  closePopup()
  goToListView()
}

const { formatDate } = useDateFormat()
</script>
<style scoped></style>
