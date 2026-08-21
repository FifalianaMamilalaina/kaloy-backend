<template>
  <div class="card bg-base-100">
    <div class="card-body">
      <div v-if="userRole">
        <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
          <div>
            <label for="userRoleId" class="block text-sm font-medium mb-1">Id</label>
            <div id="userRoleId" class="input w-full bg-base-100 cursor-default">
              <span>{{ userRole.id ? $n(userRole.id, 'decimal') : '--' }}</span>
            </div>
          </div>
          <div>
            <label for="userRoleName" class="block text-sm font-medium mb-1">Name</label>
            <div id="userRoleName" class="input w-full bg-base-100 cursor-default">
              <span>{{ userRole.name ?? '' }}</span>
            </div>
          </div>
        </div>
        <div class="flex justify-end gap-3 mt-6">
          <GenesisButton @click="goToUpdateFormView(userRole)" class="btn btn-outline btn-neutral">
            <EditIcon class="mr-2" />
            <span>{{ $t('button.update') }}</span>
          </GenesisButton>
          <GenesisButton
            @click="openDeletePopup(userRole)"
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
    :message="`Êtes-vous sûr de vouloir supprimer UserRole userRole?.id ?`"
    subMessage="Cette action est irréversible."
    @confirm="confirmDelete"
    @cancel="closePopup"
  />
</template>

<script setup lang="ts">
import type { PropType } from 'vue'

import type { UserRole } from '@/models/UserRoleModel'
import GenesisButton from '@/components/button/GenesisButton.vue'
import { usePopup } from '@/composables/usePopup'
import { useUserRoles } from '@/composables/useUserRoles'
import DeleteConfirmationPopup from '@/components/popup/DeleteConfirmationPopup.vue'
import TrashIcon from '@/components/icons/TrashIcon.vue'
import EditIcon from '@/components/icons/EditIcon.vue'
import { useDateFormat } from '@/composables/useDateFormat'

const props = defineProps({
  userRole: {
    type: Object as PropType<UserRole>,
    required: true,
  },
})
// Stores
const { closePopup, openPopup, visible: deletePopup } = usePopup(false)
const { deleteUserRole, goToListView, goToUpdateFormView } = useUserRoles()

// Methods
const openDeletePopup = (entity: UserRole) => {
  if (!entity) return
  openPopup()
}

const confirmDelete = async () => {
  const { success, error } = await deleteUserRole(props.userRole)
  if (!success) console.error(error)
  closePopup()
  goToListView()
}

const { formatDate } = useDateFormat()
</script>
<style scoped></style>
