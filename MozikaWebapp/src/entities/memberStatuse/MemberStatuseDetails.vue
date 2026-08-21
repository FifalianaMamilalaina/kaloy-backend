<template>
  <div class="card bg-base-100">
    <div class="card-body">
      <div v-if="memberStatuse">
        <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
          <div>
            <label for="memberStatuseId" class="block text-sm font-medium mb-1">Id</label>
            <div id="memberStatuseId" class="input w-full bg-base-100 cursor-default">
              <span>{{ memberStatuse.id ? $n(memberStatuse.id, 'decimal') : '--' }}</span>
            </div>
          </div>
          <div>
            <label for="memberStatuseName" class="block text-sm font-medium mb-1">Name</label>
            <div id="memberStatuseName" class="input w-full bg-base-100 cursor-default">
              <span>{{ memberStatuse.name ?? '' }}</span>
            </div>
          </div>
        </div>
        <div class="flex justify-end gap-3 mt-6">
          <GenesisButton
            @click="goToUpdateFormView(memberStatuse)"
            class="btn btn-outline btn-neutral"
          >
            <EditIcon class="mr-2" />
            <span>{{ $t('button.update') }}</span>
          </GenesisButton>
          <GenesisButton
            @click="openDeletePopup(memberStatuse)"
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
    :message="`Êtes-vous sûr de vouloir supprimer MemberStatuse memberStatuse?.id ?`"
    subMessage="Cette action est irréversible."
    @confirm="confirmDelete"
    @cancel="closePopup"
  />
</template>

<script setup lang="ts">
import type { PropType } from 'vue'

import type { MemberStatuse } from '@/models/MemberStatuseModel'
import GenesisButton from '@/components/button/GenesisButton.vue'
import { usePopup } from '@/composables/usePopup'
import { useMemberStatuses } from '@/composables/useMemberStatuses'
import DeleteConfirmationPopup from '@/components/popup/DeleteConfirmationPopup.vue'
import TrashIcon from '@/components/icons/TrashIcon.vue'
import EditIcon from '@/components/icons/EditIcon.vue'
import { useDateFormat } from '@/composables/useDateFormat'

const props = defineProps({
  memberStatuse: {
    type: Object as PropType<MemberStatuse>,
    required: true,
  },
})
// Stores
const { closePopup, openPopup, visible: deletePopup } = usePopup(false)
const { deleteMemberStatuse, goToListView, goToUpdateFormView } = useMemberStatuses()

// Methods
const openDeletePopup = (entity: MemberStatuse) => {
  if (!entity) return
  openPopup()
}

const confirmDelete = async () => {
  const { success, error } = await deleteMemberStatuse(props.memberStatuse)
  if (!success) console.error(error)
  closePopup()
  goToListView()
}

const { formatDate } = useDateFormat()
</script>
<style scoped></style>
