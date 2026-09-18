<template>
  <div class="card bg-base-100">
    <div class="card-body">
      <div v-if="notificationPreference">
        <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
          <div>
            <label for="notificationPreferenceId" class="block text-sm font-medium mb-1">Id</label>
            <div id="notificationPreferenceId" class="input w-full bg-base-100 cursor-default">
              <span>{{
                notificationPreference.id ? $n(notificationPreference.id, 'decimal') : '--'
              }}</span>
            </div>
          </div>
          <div>
            <label for="notificationPreferenceUseridUsers" class="block text-sm font-medium mb-1"
              >User</label
            >
            <div
              id="notificationPreferenceUseridUsers"
              class="input w-full bg-base-100 cursor-default"
            >
              <span>{{ notificationPreference.useridUsers?.getKeyValue() ?? '' }}</span>
            </div>
          </div>
          <div>
            <label
              for="notificationPreferenceNotificationtypeidNotificationTypes"
              class="block text-sm font-medium mb-1"
              >Notification type</label
            >
            <div
              id="notificationPreferenceNotificationtypeidNotificationTypes"
              class="input w-full bg-base-100 cursor-default"
            >
              <span>{{
                notificationPreference.notificationtypeidNotificationTypes?.getKeyValue() ?? ''
              }}</span>
            </div>
          </div>
          <div>
            <label for="notificationPreferenceIsEnabled" class="block text-sm font-medium mb-1"
              >Is enabled</label
            >
            <div
              id="notificationPreferenceIsEnabled"
              class="input w-full bg-base-100 cursor-default"
            >
              <span>{{ notificationPreference.isEnabled ?? '' }}</span>
            </div>
          </div>
        </div>
        <div class="flex justify-end gap-3 mt-6">
          <GenesisButton
            @click="goToUpdateFormView(notificationPreference)"
            class="btn btn-outline btn-neutral"
          >
            <EditIcon class="mr-2" />
            <span>{{ $t('button.update') }}</span>
          </GenesisButton>
          <GenesisButton
            @click="openDeletePopup(notificationPreference)"
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
    :message="`Êtes-vous sûr de vouloir supprimer NotificationPreference notificationPreference?.id ?`"
    subMessage="Cette action est irréversible."
    @confirm="confirmDelete"
    @cancel="closePopup"
  />
</template>

<script setup lang="ts">
import type { PropType } from 'vue'

import type { NotificationPreference } from '@/models/NotificationPreferenceModel'
import GenesisButton from '@/components/button/GenesisButton.vue'
import { usePopup } from '@/composables/usePopup'
import { useNotificationPreferences } from '@/composables/useNotificationPreferences'
import DeleteConfirmationPopup from '@/components/popup/DeleteConfirmationPopup.vue'
import TrashIcon from '@/components/icons/TrashIcon.vue'
import EditIcon from '@/components/icons/EditIcon.vue'
import { useDateFormat } from '@/composables/useDateFormat'

const props = defineProps({
  notificationPreference: {
    type: Object as PropType<NotificationPreference>,
    required: true,
  },
})
// Stores
const { closePopup, openPopup, visible: deletePopup } = usePopup(false)
const { deleteNotificationPreference, goToListView, goToUpdateFormView } =
  useNotificationPreferences()

// Methods
const openDeletePopup = (entity: NotificationPreference) => {
  if (!entity) return
  openPopup()
}

const confirmDelete = async () => {
  const { success, error } = await deleteNotificationPreference(props.notificationPreference)
  if (!success) console.error(error)
  closePopup()
  goToListView()
}

const { formatDate } = useDateFormat()
</script>
<style scoped></style>
