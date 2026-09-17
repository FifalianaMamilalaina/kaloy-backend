<template>
  <div class="card bg-base-100">
    <div class="card-body">
      <div v-if="submissionStatuse">
        <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
          <div>
            <label for="submissionStatuseId" class="block text-sm font-medium mb-1">Id</label>
            <div id="submissionStatuseId" class="input w-full bg-base-100 cursor-default">
              <span>{{ submissionStatuse.id ? $n(submissionStatuse.id, 'decimal') : '--' }}</span>
            </div>
          </div>
          <div>
            <label for="submissionStatuseName" class="block text-sm font-medium mb-1">Name</label>
            <div id="submissionStatuseName" class="input w-full bg-base-100 cursor-default">
              <span>{{ submissionStatuse.name ?? '' }}</span>
            </div>
          </div>
        </div>
        <div class="flex justify-end gap-3 mt-6">
          <GenesisButton
            @click="goToUpdateFormView(submissionStatuse)"
            class="btn btn-outline btn-neutral"
          >
            <EditIcon class="mr-2" />
            <span>{{ $t('button.update') }}</span>
          </GenesisButton>
          <GenesisButton
            @click="openDeletePopup(submissionStatuse)"
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
    :message="`Êtes-vous sûr de vouloir supprimer SubmissionStatuse submissionStatuse?.id ?`"
    subMessage="Cette action est irréversible."
    @confirm="confirmDelete"
    @cancel="closePopup"
  />
</template>

<script setup lang="ts">
import type { PropType } from 'vue'

import type { SubmissionStatuse } from '@/models/SubmissionStatuseModel'
import GenesisButton from '@/components/button/GenesisButton.vue'
import { usePopup } from '@/composables/usePopup'
import { useSubmissionStatuses } from '@/composables/useSubmissionStatuses'
import DeleteConfirmationPopup from '@/components/popup/DeleteConfirmationPopup.vue'
import TrashIcon from '@/components/icons/TrashIcon.vue'
import EditIcon from '@/components/icons/EditIcon.vue'
import { useDateFormat } from '@/composables/useDateFormat'

const props = defineProps({
  submissionStatuse: {
    type: Object as PropType<SubmissionStatuse>,
    required: true,
  },
})
// Stores
const { closePopup, openPopup, visible: deletePopup } = usePopup(false)
const { deleteSubmissionStatuse, goToListView, goToUpdateFormView } = useSubmissionStatuses()

// Methods
const openDeletePopup = (entity: SubmissionStatuse) => {
  if (!entity) return
  openPopup()
}

const confirmDelete = async () => {
  const { success, error } = await deleteSubmissionStatuse(props.submissionStatuse)
  if (!success) console.error(error)
  closePopup()
  goToListView()
}

const { formatDate } = useDateFormat()
</script>
<style scoped></style>
