<template>
  <div class="w-full">
    <div class="flex items-center justify-between mb-6">
      <h3 class="text-xl font-semibold text-base-content">
        {{ $t('entity.nav', { entity: 'SubmissionStatuse' }) }}
        <span class="text-base-content/50 font-normal">{{ $t('entity.create.nav') }}</span>
      </h3>
      <GenesisButton
        :title="$t('button.backToListDescription')"
        @click="goToListView"
        class="btn-secondary"
      >
        <LeftArrowIcon />
        {{ $t('button.backToList') }}
      </GenesisButton>
    </div>

    <submissionStatuse-form
      :submit-label="$t('entity.create.submitLabel', { entity: 'SubmissionStatuse' })"
      :violations="violations"
      @submit="createHandler"
      @cancel="goToListView"
    />

    <AlertPopup
      :message="message ?? undefined"
      title="Error"
      :visible="alertPopup"
      @close="closePopup"
    />
  </div>
</template>

<script setup lang="ts">
import SubmissionStatuseForm from '@/entities/submissionStatuse/SubmissionStatuseForm.vue'
import AlertPopup from '@/components/popup/AlertPopup.vue'
import GenesisButton from '@/components/button/GenesisButton.vue'
import LeftArrowIcon from '@/components/icons/LeftArrowIcon.vue'
import { SubmissionStatuseFormDTO } from '@/models/SubmissionStatuseModel'
import { useSubmissionStatuses } from '@/composables/useSubmissionStatuses'
import { usePopup } from '@/composables/usePopup'
import { useFreezeScreenStore } from '@/stores/useFreezeScreenStore'
import { ref } from 'vue'

const { createSubmissionStatuse, goToListView, message, viewSubmissionStatuse } =
  useSubmissionStatuses()
const { openPopup, closePopup, visible: alertPopup } = usePopup()
const freezeStore = useFreezeScreenStore()
const violations = ref<Record<string, string> | null>(null)
const createHandler = async (formDTO: Partial<SubmissionStatuseFormDTO>) => {
  freezeStore.freeze('Creating a new SubmissionStatuse ...')
  try {
    const { data, errors } = await createSubmissionStatuse(formDTO)
    if (errors) {
      violations.value = errors
    }
    if (data) {
      viewSubmissionStatuse(data)
      return
    }
    throw new Error(message.value || 'Creation failed without an error message from the API.')
  } catch (error: unknown) {
    console.error('Error creating submissionStatuse:', error)
    openPopup()
  } finally {
    freezeStore.unfreeze()
  }
}
</script>
<style scoped></style>
