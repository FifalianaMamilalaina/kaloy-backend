<template>
  <div class="card bg-base-100">
    <div class="card-body">
      <div v-if="reportStatuse">
        <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
          <div>
            <label for="reportStatuseId" class="block text-sm font-medium mb-1">Id</label>
            <div id="reportStatuseId" class="input w-full bg-base-100 cursor-default">
              <span>{{ reportStatuse.id ? $n(reportStatuse.id, 'decimal') : '--' }}</span>
            </div>
          </div>
          <div>
            <label for="reportStatuseName" class="block text-sm font-medium mb-1">Name</label>
            <div id="reportStatuseName" class="input w-full bg-base-100 cursor-default">
              <span>{{ reportStatuse.name ?? '' }}</span>
            </div>
          </div>
        </div>
        <div class="flex justify-end gap-3 mt-6">
          <GenesisButton
            @click="goToUpdateFormView(reportStatuse)"
            class="btn btn-outline btn-neutral"
          >
            <EditIcon class="mr-2" />
            <span>{{ $t('button.update') }}</span>
          </GenesisButton>
          <GenesisButton
            @click="openDeletePopup(reportStatuse)"
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
    :message="`Êtes-vous sûr de vouloir supprimer ReportStatuse reportStatuse?.id ?`"
    subMessage="Cette action est irréversible."
    @confirm="confirmDelete"
    @cancel="closePopup"
  />
</template>

<script setup lang="ts">
import type { PropType } from 'vue'

import type { ReportStatuse } from '@/models/ReportStatuseModel'
import GenesisButton from '@/components/button/GenesisButton.vue'
import { usePopup } from '@/composables/usePopup'
import { useReportStatuses } from '@/composables/useReportStatuses'
import DeleteConfirmationPopup from '@/components/popup/DeleteConfirmationPopup.vue'
import TrashIcon from '@/components/icons/TrashIcon.vue'
import EditIcon from '@/components/icons/EditIcon.vue'
import { useDateFormat } from '@/composables/useDateFormat'

const props = defineProps({
  reportStatuse: {
    type: Object as PropType<ReportStatuse>,
    required: true,
  },
})
// Stores
const { closePopup, openPopup, visible: deletePopup } = usePopup(false)
const { deleteReportStatuse, goToListView, goToUpdateFormView } = useReportStatuses()

// Methods
const openDeletePopup = (entity: ReportStatuse) => {
  if (!entity) return
  openPopup()
}

const confirmDelete = async () => {
  const { success, error } = await deleteReportStatuse(props.reportStatuse)
  if (!success) console.error(error)
  closePopup()
  goToListView()
}

const { formatDate } = useDateFormat()
</script>
<style scoped></style>
