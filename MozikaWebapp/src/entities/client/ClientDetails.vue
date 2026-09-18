<template>
  <div class="card bg-base-100">
    <div class="card-body">
      <div v-if="client">
        <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
          <div>
            <label for="clientId" class="block text-sm font-medium mb-1">Id</label>
            <div id="clientId" class="input w-full bg-base-100 cursor-default">
              <span>{{ client.id ? $n(client.id, 'decimal') : '--' }}</span>
            </div>
          </div>
          <div>
            <label for="clientUseridUsers" class="block text-sm font-medium mb-1">User</label>
            <div id="clientUseridUsers" class="input w-full bg-base-100 cursor-default">
              <span>{{ client.useridUsers?.getKeyValue() ?? '' }}</span>
            </div>
          </div>
        </div>
        <div class="flex justify-end gap-3 mt-6">
          <GenesisButton @click="goToUpdateFormView(client)" class="btn btn-outline btn-neutral">
            <EditIcon class="mr-2" />
            <span>{{ $t('button.update') }}</span>
          </GenesisButton>
          <GenesisButton
            @click="openDeletePopup(client)"
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
    :message="`Êtes-vous sûr de vouloir supprimer Client client?.id ?`"
    subMessage="Cette action est irréversible."
    @confirm="confirmDelete"
    @cancel="closePopup"
  />
</template>

<script setup lang="ts">
import type { PropType } from 'vue'

import type { Client } from '@/models/ClientModel'
import GenesisButton from '@/components/button/GenesisButton.vue'
import { usePopup } from '@/composables/usePopup'
import { useClients } from '@/composables/useClients'
import DeleteConfirmationPopup from '@/components/popup/DeleteConfirmationPopup.vue'
import TrashIcon from '@/components/icons/TrashIcon.vue'
import EditIcon from '@/components/icons/EditIcon.vue'
import { useDateFormat } from '@/composables/useDateFormat'

const props = defineProps({
  client: {
    type: Object as PropType<Client>,
    required: true,
  },
})
// Stores
const { closePopup, openPopup, visible: deletePopup } = usePopup(false)
const { deleteClient, goToListView, goToUpdateFormView } = useClients()

// Methods
const openDeletePopup = (entity: Client) => {
  if (!entity) return
  openPopup()
}

const confirmDelete = async () => {
  const { success, error } = await deleteClient(props.client)
  if (!success) console.error(error)
  closePopup()
  goToListView()
}

const { formatDate } = useDateFormat()
</script>
<style scoped></style>
