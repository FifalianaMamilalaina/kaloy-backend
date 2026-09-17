<template>
  <div class="card bg-base-100">
    <div class="card-body">
      <div v-if="instrumentRole">
        <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
          <div>
            <label for="instrumentRoleId" class="block text-sm font-medium mb-1">Id</label>
            <div id="instrumentRoleId" class="input w-full bg-base-100 cursor-default">
              <span>{{ instrumentRole.id ? $n(instrumentRole.id, 'decimal') : '--' }}</span>
            </div>
          </div>
          <div>
            <label for="instrumentRoleLabel" class="block text-sm font-medium mb-1">Label</label>
            <div id="instrumentRoleLabel" class="input w-full bg-base-100 cursor-default">
              <span>{{ instrumentRole.label ?? '' }}</span>
            </div>
          </div>
        </div>
        <div class="flex justify-end gap-3 mt-6">
          <GenesisButton
            @click="goToUpdateFormView(instrumentRole)"
            class="btn btn-outline btn-neutral"
          >
            <EditIcon class="mr-2" />
            <span>{{ $t('button.update') }}</span>
          </GenesisButton>
          <GenesisButton
            @click="openDeletePopup(instrumentRole)"
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
    :message="`Êtes-vous sûr de vouloir supprimer InstrumentRole instrumentRole?.id ?`"
    subMessage="Cette action est irréversible."
    @confirm="confirmDelete"
    @cancel="closePopup"
  />
</template>

<script setup lang="ts">
import type { PropType } from 'vue'

import type { InstrumentRole } from '@/models/InstrumentRoleModel'
import GenesisButton from '@/components/button/GenesisButton.vue'
import { usePopup } from '@/composables/usePopup'
import { useInstrumentRoles } from '@/composables/useInstrumentRoles'
import DeleteConfirmationPopup from '@/components/popup/DeleteConfirmationPopup.vue'
import TrashIcon from '@/components/icons/TrashIcon.vue'
import EditIcon from '@/components/icons/EditIcon.vue'
import { useDateFormat } from '@/composables/useDateFormat'

const props = defineProps({
  instrumentRole: {
    type: Object as PropType<InstrumentRole>,
    required: true,
  },
})
// Stores
const { closePopup, openPopup, visible: deletePopup } = usePopup(false)
const { deleteInstrumentRole, goToListView, goToUpdateFormView } = useInstrumentRoles()

// Methods
const openDeletePopup = (entity: InstrumentRole) => {
  if (!entity) return
  openPopup()
}

const confirmDelete = async () => {
  const { success, error } = await deleteInstrumentRole(props.instrumentRole)
  if (!success) console.error(error)
  closePopup()
  goToListView()
}

const { formatDate } = useDateFormat()
</script>
<style scoped></style>
