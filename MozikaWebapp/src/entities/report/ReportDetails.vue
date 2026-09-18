<template>
  <div class="card bg-base-100">
    <div class="card-body">
      <div v-if="report">
        <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
          <div>
            <label for="reportId" class="block text-sm font-medium mb-1">Id</label>
            <div id="reportId" class="input w-full bg-base-100 cursor-default">
              <span>{{ report.id ? $n(report.id, 'decimal') : '--' }}</span>
            </div>
          </div>
          <div>
            <label for="reportReporteruseridUsers" class="block text-sm font-medium mb-1"
              >User</label
            >
            <div id="reportReporteruseridUsers" class="input w-full bg-base-100 cursor-default">
              <span>{{ report.reporteruseridUsers?.getKeyValue() ?? '' }}</span>
            </div>
          </div>
          <div>
            <label for="reportTargettypeidInteractionTargets" class="block text-sm font-medium mb-1"
              >Interaction target</label
            >
            <div
              id="reportTargettypeidInteractionTargets"
              class="input w-full bg-base-100 cursor-default"
            >
              <span>{{ report.targettypeidInteractionTargets?.getKeyValue() ?? '' }}</span>
            </div>
          </div>
          <div>
            <label for="reportTargetId" class="block text-sm font-medium mb-1">Target id</label>
            <div id="reportTargetId" class="input w-full bg-base-100 cursor-default">
              <span>{{ report.targetId ? $n(report.targetId, 'decimal') : '--' }}</span>
            </div>
          </div>
          <div>
            <label for="reportReason" class="block text-sm font-medium mb-1">Reason</label>
            <div id="reportReason" class="input w-full bg-base-100 cursor-default">
              <span>{{ report.reason ?? '' }}</span>
            </div>
          </div>
          <div>
            <label for="reportStatusidReportStatuses" class="block text-sm font-medium mb-1"
              >Report statuse</label
            >
            <div id="reportStatusidReportStatuses" class="input w-full bg-base-100 cursor-default">
              <span>{{ report.statusidReportStatuses?.getKeyValue() ?? '' }}</span>
            </div>
          </div>
          <div>
            <label for="reportReviewedAt" class="block text-sm font-medium mb-1">Reviewed at</label>
            <div id="reportReviewedAt" class="input w-full bg-base-100 cursor-default">
              <span>{{ report.reviewedAt ?? '' }}</span>
            </div>
          </div>
          <div>
            <label for="reportCreatedAt" class="block text-sm font-medium mb-1">Created at</label>
            <div id="reportCreatedAt" class="input w-full bg-base-100 cursor-default">
              <span>{{ report.createdAt ?? '' }}</span>
            </div>
          </div>
        </div>
        <div class="flex justify-end gap-3 mt-6">
          <GenesisButton @click="goToUpdateFormView(report)" class="btn btn-outline btn-neutral">
            <EditIcon class="mr-2" />
            <span>{{ $t('button.update') }}</span>
          </GenesisButton>
          <GenesisButton
            @click="openDeletePopup(report)"
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
    :message="`Êtes-vous sûr de vouloir supprimer Report report?.id ?`"
    subMessage="Cette action est irréversible."
    @confirm="confirmDelete"
    @cancel="closePopup"
  />
</template>

<script setup lang="ts">
import type { PropType } from 'vue'

import type { Report } from '@/models/ReportModel'
import GenesisButton from '@/components/button/GenesisButton.vue'
import { usePopup } from '@/composables/usePopup'
import { useReports } from '@/composables/useReports'
import DeleteConfirmationPopup from '@/components/popup/DeleteConfirmationPopup.vue'
import TrashIcon from '@/components/icons/TrashIcon.vue'
import EditIcon from '@/components/icons/EditIcon.vue'
import { useDateFormat } from '@/composables/useDateFormat'

const props = defineProps({
  report: {
    type: Object as PropType<Report>,
    required: true,
  },
})
// Stores
const { closePopup, openPopup, visible: deletePopup } = usePopup(false)
const { deleteReport, goToListView, goToUpdateFormView } = useReports()

// Methods
const openDeletePopup = (entity: Report) => {
  if (!entity) return
  openPopup()
}

const confirmDelete = async () => {
  const { success, error } = await deleteReport(props.report)
  if (!success) console.error(error)
  closePopup()
  goToListView()
}

const { formatDate } = useDateFormat()
</script>
<style scoped></style>
