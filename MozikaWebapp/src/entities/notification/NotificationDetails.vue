<template>
  <div class="card bg-base-100">
    <div class="card-body">
      <div v-if="notification">
        <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
          <div>
            <label for="notificationId" class="block text-sm font-medium mb-1">Id</label>
            <div id="notificationId" class="input w-full bg-base-100 cursor-default">
              <span>{{ notification.id ? $n(notification.id, 'decimal') : '--' }}</span>
            </div>
          </div>
          <div>
            <label for="notificationUseridUsers" class="block text-sm font-medium mb-1">User</label>
            <div id="notificationUseridUsers" class="input w-full bg-base-100 cursor-default">
              <span>{{ notification.useridUsers?.getKeyValue() ?? '' }}</span>
            </div>
          </div>
          <div>
            <label for="notificationTypeidNotificationTypes" class="block text-sm font-medium mb-1"
              >Notification type</label
            >
            <div
              id="notificationTypeidNotificationTypes"
              class="input w-full bg-base-100 cursor-default"
            >
              <span>{{ notification.typeidNotificationTypes?.getKeyValue() ?? '' }}</span>
            </div>
          </div>
          <div>
            <label for="notificationContent" class="block text-sm font-medium mb-1">Content</label>
            <div id="notificationContent" class="input w-full bg-base-100 cursor-default">
              <span>{{ notification.content ?? '' }}</span>
            </div>
          </div>
          <div>
            <label
              for="notificationRelatedentitytypeidInteractionTargets"
              class="block text-sm font-medium mb-1"
              >Interaction target</label
            >
            <div
              id="notificationRelatedentitytypeidInteractionTargets"
              class="input w-full bg-base-100 cursor-default"
            >
              <span>{{
                notification.relatedentitytypeidInteractionTargets?.getKeyValue() ?? ''
              }}</span>
            </div>
          </div>
          <div>
            <label for="notificationRelatedEntityId" class="block text-sm font-medium mb-1"
              >Related entity id</label
            >
            <div id="notificationRelatedEntityId" class="input w-full bg-base-100 cursor-default">
              <span>{{
                notification.relatedEntityId ? $n(notification.relatedEntityId, 'decimal') : '--'
              }}</span>
            </div>
          </div>
          <div>
            <label for="notificationIsRead" class="block text-sm font-medium mb-1">Is read</label>
            <div id="notificationIsRead" class="input w-full bg-base-100 cursor-default">
              <span>{{ notification.isRead ?? '' }}</span>
            </div>
          </div>
          <div>
            <label for="notificationCreatedAt" class="block text-sm font-medium mb-1"
              >Created at</label
            >
            <div id="notificationCreatedAt" class="input w-full bg-base-100 cursor-default">
              <span>{{ notification.createdAt ?? '' }}</span>
            </div>
          </div>
        </div>
        <div class="flex justify-end gap-3 mt-6">
          <GenesisButton
            @click="goToUpdateFormView(notification)"
            class="btn btn-outline btn-neutral"
          >
            <EditIcon class="mr-2" />
            <span>{{ $t('button.update') }}</span>
          </GenesisButton>
          <GenesisButton
            @click="openDeletePopup(notification)"
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
    :message="`Êtes-vous sûr de vouloir supprimer Notification notification?.id ?`"
    subMessage="Cette action est irréversible."
    @confirm="confirmDelete"
    @cancel="closePopup"
  />
</template>

<script setup lang="ts">
import type { PropType } from 'vue'

import type { Notification } from '@/models/NotificationModel'
import GenesisButton from '@/components/button/GenesisButton.vue'
import { usePopup } from '@/composables/usePopup'
import { useNotifications } from '@/composables/useNotifications'
import DeleteConfirmationPopup from '@/components/popup/DeleteConfirmationPopup.vue'
import TrashIcon from '@/components/icons/TrashIcon.vue'
import EditIcon from '@/components/icons/EditIcon.vue'
import { useDateFormat } from '@/composables/useDateFormat'

const props = defineProps({
  notification: {
    type: Object as PropType<Notification>,
    required: true,
  },
})
// Stores
const { closePopup, openPopup, visible: deletePopup } = usePopup(false)
const { deleteNotification, goToListView, goToUpdateFormView } = useNotifications()

// Methods
const openDeletePopup = (entity: Notification) => {
  if (!entity) return
  openPopup()
}

const confirmDelete = async () => {
  const { success, error } = await deleteNotification(props.notification)
  if (!success) console.error(error)
  closePopup()
  goToListView()
}

const { formatDate } = useDateFormat()
</script>
<style scoped></style>
