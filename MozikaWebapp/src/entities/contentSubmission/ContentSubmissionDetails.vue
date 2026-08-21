<template>
  <div class="card bg-base-100">
    <div class="card-body">
      <div v-if="contentSubmission">
        <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
          <div>
            <label for="contentSubmissionId" class="block text-sm font-medium mb-1">Id</label>
            <div id="contentSubmissionId" class="input w-full bg-base-100 cursor-default">
              <span>{{ contentSubmission.id ? $n(contentSubmission.id, 'decimal') : '--' }}</span>
            </div>
          </div>
          <div>
            <label for="contentSubmissionArtistidArtists" class="block text-sm font-medium mb-1"
              >Artist</label
            >
            <div
              id="contentSubmissionArtistidArtists"
              class="input w-full bg-base-100 cursor-default"
            >
              <span>{{ contentSubmission.artistidArtists?.getKeyValue() ?? '' }}</span>
            </div>
          </div>
          <div>
            <label for="contentSubmissionSourceFilename" class="block text-sm font-medium mb-1"
              >Source filename</label
            >
            <div
              id="contentSubmissionSourceFilename"
              class="input w-full bg-base-100 cursor-default"
            >
              <span>{{ contentSubmission.sourceFilename ?? '' }}</span>
            </div>
          </div>
          <div>
            <label for="contentSubmissionFileType" class="block text-sm font-medium mb-1"
              >File type</label
            >
            <div id="contentSubmissionFileType" class="input w-full bg-base-100 cursor-default">
              <span>{{ contentSubmission.fileType ?? '' }}</span>
            </div>
          </div>
          <div>
            <label
              for="contentSubmissionStatusidSubmissionStatuses"
              class="block text-sm font-medium mb-1"
              >Submission statuse</label
            >
            <div
              id="contentSubmissionStatusidSubmissionStatuses"
              class="input w-full bg-base-100 cursor-default"
            >
              <span>{{ contentSubmission.statusidSubmissionStatuses?.getKeyValue() ?? '' }}</span>
            </div>
          </div>
          <div>
            <label for="contentSubmissionErrorMessage" class="block text-sm font-medium mb-1"
              >Error message</label
            >
            <div id="contentSubmissionErrorMessage" class="input w-full bg-base-100 cursor-default">
              <span>{{ contentSubmission.errorMessage ?? '' }}</span>
            </div>
          </div>
          <div>
            <label for="contentSubmissionSubmittedAt" class="block text-sm font-medium mb-1"
              >Submitted at</label
            >
            <div id="contentSubmissionSubmittedAt" class="input w-full bg-base-100 cursor-default">
              <span>{{ contentSubmission.submittedAt ?? '' }}</span>
            </div>
          </div>
          <div>
            <label for="contentSubmissionProcessedAt" class="block text-sm font-medium mb-1"
              >Processed at</label
            >
            <div id="contentSubmissionProcessedAt" class="input w-full bg-base-100 cursor-default">
              <span>{{ contentSubmission.processedAt ?? '' }}</span>
            </div>
          </div>
        </div>
        <div class="flex justify-end gap-3 mt-6">
          <GenesisButton
            @click="goToUpdateFormView(contentSubmission)"
            class="btn btn-outline btn-neutral"
          >
            <EditIcon class="mr-2" />
            <span>{{ $t('button.update') }}</span>
          </GenesisButton>
          <GenesisButton
            @click="openDeletePopup(contentSubmission)"
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
    :message="`Êtes-vous sûr de vouloir supprimer ContentSubmission contentSubmission?.id ?`"
    subMessage="Cette action est irréversible."
    @confirm="confirmDelete"
    @cancel="closePopup"
  />
</template>

<script setup lang="ts">
import type { PropType } from 'vue'

import type { ContentSubmission } from '@/models/ContentSubmissionModel'
import GenesisButton from '@/components/button/GenesisButton.vue'
import { usePopup } from '@/composables/usePopup'
import { useContentSubmissions } from '@/composables/useContentSubmissions'
import DeleteConfirmationPopup from '@/components/popup/DeleteConfirmationPopup.vue'
import TrashIcon from '@/components/icons/TrashIcon.vue'
import EditIcon from '@/components/icons/EditIcon.vue'
import { useDateFormat } from '@/composables/useDateFormat'

const props = defineProps({
  contentSubmission: {
    type: Object as PropType<ContentSubmission>,
    required: true,
  },
})
// Stores
const { closePopup, openPopup, visible: deletePopup } = usePopup(false)
const { deleteContentSubmission, goToListView, goToUpdateFormView } = useContentSubmissions()

// Methods
const openDeletePopup = (entity: ContentSubmission) => {
  if (!entity) return
  openPopup()
}

const confirmDelete = async () => {
  const { success, error } = await deleteContentSubmission(props.contentSubmission)
  if (!success) console.error(error)
  closePopup()
  goToListView()
}

const { formatDate } = useDateFormat()
</script>
<style scoped></style>
