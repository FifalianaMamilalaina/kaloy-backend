<template>
  <div class="w-full">
    <div class="flex items-center justify-between mb-6">
      <h3 class="text-xl font-semibold text-base-content">
        {{ $t('entity.nav', { entity: 'ArtistGroupMember' }) }}
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

    <artistGroupMember-form
      :submit-label="$t('entity.create.submitLabel', { entity: 'ArtistGroupMember' })"
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
import ArtistGroupMemberForm from '@/entities/artistGroupMember/ArtistGroupMemberForm.vue'
import AlertPopup from '@/components/popup/AlertPopup.vue'
import GenesisButton from '@/components/button/GenesisButton.vue'
import LeftArrowIcon from '@/components/icons/LeftArrowIcon.vue'
import { ArtistGroupMemberFormDTO } from '@/models/ArtistGroupMemberModel'
import { useArtistGroupMembers } from '@/composables/useArtistGroupMembers'
import { usePopup } from '@/composables/usePopup'
import { useFreezeScreenStore } from '@/stores/useFreezeScreenStore'
import { ref } from 'vue'

const { createArtistGroupMember, goToListView, message, viewArtistGroupMember } =
  useArtistGroupMembers()
const { openPopup, closePopup, visible: alertPopup } = usePopup()
const freezeStore = useFreezeScreenStore()
const violations = ref<Record<string, string> | null>(null)
const createHandler = async (formDTO: Partial<ArtistGroupMemberFormDTO>) => {
  freezeStore.freeze('Creating a new ArtistGroupMember ...')
  try {
    const { data, errors } = await createArtistGroupMember(formDTO)
    if (errors) {
      violations.value = errors
    }
    if (data) {
      viewArtistGroupMember(data)
      return
    }
    throw new Error(message.value || 'Creation failed without an error message from the API.')
  } catch (error: unknown) {
    console.error('Error creating artistGroupMember:', error)
    openPopup()
  } finally {
    freezeStore.unfreeze()
  }
}
</script>
<style scoped></style>
