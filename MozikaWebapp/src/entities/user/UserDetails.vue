<template>
  <div class="card bg-base-100">
    <div class="card-body">
      <div v-if="user">
        <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
          <div>
            <label for="userId" class="block text-sm font-medium mb-1">Id</label>
            <div id="userId" class="input w-full bg-base-100 cursor-default">
              <span>{{ user.id ? $n(user.id, 'decimal') : '--' }}</span>
            </div>
          </div>
          <div>
            <label for="userEmail" class="block text-sm font-medium mb-1">Email</label>
            <div id="userEmail" class="input w-full bg-base-100 cursor-default">
              <span>{{ user.email ?? '' }}</span>
            </div>
          </div>
          <div>
            <label for="userPhone" class="block text-sm font-medium mb-1">Phone</label>
            <div id="userPhone" class="input w-full bg-base-100 cursor-default">
              <span>{{ user.phone ?? '' }}</span>
            </div>
          </div>
          <div>
            <label for="userEmailVerifiedAt" class="block text-sm font-medium mb-1"
              >Email verified at</label
            >
            <div id="userEmailVerifiedAt" class="input w-full bg-base-100 cursor-default">
              <span>{{ user.emailVerifiedAt ?? '' }}</span>
            </div>
          </div>
          <div>
            <label for="userPhoneVerifiedAt" class="block text-sm font-medium mb-1"
              >Phone verified at</label
            >
            <div id="userPhoneVerifiedAt" class="input w-full bg-base-100 cursor-default">
              <span>{{ user.phoneVerifiedAt ?? '' }}</span>
            </div>
          </div>
          <div>
            <label for="userPasswordHash" class="block text-sm font-medium mb-1"
              >Password hash</label
            >
            <div id="userPasswordHash" class="input w-full bg-base-100 cursor-default">
              <span>{{ user.passwordHash ?? '' }}</span>
            </div>
          </div>
          <div>
            <label for="userRoleidUserRoles" class="block text-sm font-medium mb-1"
              >User role</label
            >
            <div id="userRoleidUserRoles" class="input w-full bg-base-100 cursor-default">
              <span>{{ user.roleidUserRoles?.getKeyValue() ?? '' }}</span>
            </div>
          </div>
          <div>
            <label for="userStatusidUserStatuses" class="block text-sm font-medium mb-1"
              >User statuse</label
            >
            <div id="userStatusidUserStatuses" class="input w-full bg-base-100 cursor-default">
              <span>{{ user.statusidUserStatuses?.getKeyValue() ?? '' }}</span>
            </div>
          </div>
          <div>
            <label for="userCreatedAt" class="block text-sm font-medium mb-1">Created at</label>
            <div id="userCreatedAt" class="input w-full bg-base-100 cursor-default">
              <span>{{ user.createdAt ?? '' }}</span>
            </div>
          </div>
          <div>
            <label for="userUpdatedAt" class="block text-sm font-medium mb-1">Updated at</label>
            <div id="userUpdatedAt" class="input w-full bg-base-100 cursor-default">
              <span>{{ user.updatedAt ?? '' }}</span>
            </div>
          </div>
        </div>
        <div class="flex justify-end gap-3 mt-6">
          <GenesisButton @click="goToUpdateFormView(user)" class="btn btn-outline btn-neutral">
            <EditIcon class="mr-2" />
            <span>{{ $t('button.update') }}</span>
          </GenesisButton>
          <GenesisButton
            @click="openDeletePopup(user)"
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
    :message="`Êtes-vous sûr de vouloir supprimer User user?.id ?`"
    subMessage="Cette action est irréversible."
    @confirm="confirmDelete"
    @cancel="closePopup"
  />
</template>

<script setup lang="ts">
import type { PropType } from 'vue'

import type { User } from '@/models/UserModel'
import GenesisButton from '@/components/button/GenesisButton.vue'
import { usePopup } from '@/composables/usePopup'
import { useUsers } from '@/composables/useUsers'
import DeleteConfirmationPopup from '@/components/popup/DeleteConfirmationPopup.vue'
import TrashIcon from '@/components/icons/TrashIcon.vue'
import EditIcon from '@/components/icons/EditIcon.vue'
import { useDateFormat } from '@/composables/useDateFormat'

const props = defineProps({
  user: {
    type: Object as PropType<User>,
    required: true,
  },
})
// Stores
const { closePopup, openPopup, visible: deletePopup } = usePopup(false)
const { deleteUser, goToListView, goToUpdateFormView } = useUsers()

// Methods
const openDeletePopup = (entity: User) => {
  if (!entity) return
  openPopup()
}

const confirmDelete = async () => {
  const { success, error } = await deleteUser(props.user)
  if (!success) console.error(error)
  closePopup()
  goToListView()
}

const { formatDate } = useDateFormat()
</script>
<style scoped></style>
