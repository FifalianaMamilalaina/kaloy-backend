<template>
  <div class="w-full">
    <div class="flex items-center justify-between mb-6">
      <h3 class="text-xl font-semibold text-base-content">
        {{ $t('entity.nav', { entity: 'EventMedia' }) }}
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

    <eventMedia-form
      :submit-label="$t('entity.create.submitLabel', { entity: 'EventMedia' })"
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
import EventMediaForm from '@/entities/eventMedia/EventMediaForm.vue'
import AlertPopup from '@/components/popup/AlertPopup.vue'
import GenesisButton from '@/components/button/GenesisButton.vue'
import LeftArrowIcon from '@/components/icons/LeftArrowIcon.vue'
import { EventMediaFormDTO } from '@/models/EventMediaModel'
import { useEventMedias } from '@/composables/useEventMedias'
import { usePopup } from '@/composables/usePopup'
import { useFreezeScreenStore } from '@/stores/useFreezeScreenStore'
import { ref } from 'vue'

const { createEventMedia, goToListView, message, viewEventMedia } = useEventMedias()
const { openPopup, closePopup, visible: alertPopup } = usePopup()
const freezeStore = useFreezeScreenStore()
const violations = ref<Record<string, string> | null>(null)
const createHandler = async (formDTO: Partial<EventMediaFormDTO>) => {
  freezeStore.freeze('Creating a new EventMedia ...')
  try {
    const { data, errors } = await createEventMedia(formDTO)
    if (errors) {
      violations.value = errors
    }
    if (data) {
      viewEventMedia(data)
      return
    }
    throw new Error(message.value || 'Creation failed without an error message from the API.')
  } catch (error: unknown) {
    console.error('Error creating eventMedia:', error)
    openPopup()
  } finally {
    freezeStore.unfreeze()
  }
}
</script>
<style scoped></style>
