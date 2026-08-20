<template>
  <div class="w-full">
    <div class="flex items-center justify-between mb-6">
      <h3 class="text-xl font-semibold text-base-content">
        {{ $t('entity.nav', { entity: 'EditorialPlaylist' }) }}
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

    <editorialPlaylist-form
      :submit-label="$t('entity.create.submitLabel', { entity: 'EditorialPlaylist' })"
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
import EditorialPlaylistForm from '@/entities/editorialPlaylist/EditorialPlaylistForm.vue'
import AlertPopup from '@/components/popup/AlertPopup.vue'
import GenesisButton from '@/components/button/GenesisButton.vue'
import LeftArrowIcon from '@/components/icons/LeftArrowIcon.vue'
import { EditorialPlaylistFormDTO } from '@/models/EditorialPlaylistModel'
import { useEditorialPlaylists } from '@/composables/useEditorialPlaylists'
import { usePopup } from '@/composables/usePopup'
import { useFreezeScreenStore } from '@/stores/useFreezeScreenStore'
import { ref } from 'vue'

const { createEditorialPlaylist, goToListView, message, viewEditorialPlaylist } =
  useEditorialPlaylists()
const { openPopup, closePopup, visible: alertPopup } = usePopup()
const freezeStore = useFreezeScreenStore()
const violations = ref<Record<string, string> | null>(null)
const createHandler = async (formDTO: Partial<EditorialPlaylistFormDTO>) => {
  freezeStore.freeze('Creating a new EditorialPlaylist ...')
  try {
    const { data, errors } = await createEditorialPlaylist(formDTO)
    if (errors) {
      violations.value = errors
    }
    if (data) {
      viewEditorialPlaylist(data)
      return
    }
    throw new Error(message.value || 'Creation failed without an error message from the API.')
  } catch (error: unknown) {
    console.error('Error creating editorialPlaylist:', error)
    openPopup()
  } finally {
    freezeStore.unfreeze()
  }
}
</script>
<style scoped></style>
