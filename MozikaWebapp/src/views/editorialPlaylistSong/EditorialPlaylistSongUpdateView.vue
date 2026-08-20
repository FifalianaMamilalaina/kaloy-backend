<template>
  <div class="w-full">
    <!-- Header -->
    <div class="flex items-center justify-between mb-6">
      <h3 class="text-xl font-semibold text-base-content">
        EditorialPlaylistSong /
        <span class="text-base-content/50 font-normal">{{ $t('entity.update.nav') }}</span>
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

    <!-- Form -->
    <div v-if="entity">
      <editorialPlaylistSong-form
        :editorialPlaylistSong="entity"
        :violations="violations"
        :submit-label="$t('entity.update.submitLabel', { entity: 'EditorialPlaylistSong' })"
        @submit="updateHandler"
        @cancel="cancelHandler"
      />
    </div>

    <!-- Alert -->
    <AlertPopup
      :message="message ?? undefined"
      title="Error 500"
      :visible="alertPopup"
      @close="closePopup"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import EditorialPlaylistSongForm from '@/entities/editorialPlaylistSong/EditorialPlaylistSongForm.vue'
import AlertPopup from '@/components/popup/AlertPopup.vue'
import { useEditorialPlaylistSongs } from '@/composables/useEditorialPlaylistSongs'
import {
  EditorialPlaylistSong,
  EditorialPlaylistSongFormDTO,
} from '@/models/EditorialPlaylistSongModel'
import { usePopup } from '@/composables/usePopup'
import GenesisButton from '@/components/button/GenesisButton.vue'
import LeftArrowIcon from '@/components/icons/LeftArrowIcon.vue'
import { useFreezeScreenStore } from '@/stores/useFreezeScreenStore.ts'

const route = useRoute()
const pathId = Number(route.params.id)
const { closePopup, openPopup, visible: alertPopup } = usePopup()
const {
  getEditorialPlaylistSongById,
  goToListView,
  updateEditorialPlaylistSong,
  viewEditorialPlaylistSong,
  message,
} = useEditorialPlaylistSongs()
const entity = ref<EditorialPlaylistSong | null>(null)
const freezeStore = useFreezeScreenStore()

const violations = ref<Record<string, object> | null>(null)

const updateHandler = async (formDTO: Partial<EditorialPlaylistSongFormDTO>) => {
  freezeStore.freeze('Updating editorialPlaylistSong ' + pathId + ' ...')
  try {
    const data = await updateEditorialPlaylistSong(pathId, formDTO)
    if (data && !message.value) viewEditorialPlaylistSong(data)
    else throw new Error(String(message.value))
  } catch (error: unknown) {
    console.error(error)
  } finally {
    freezeStore.unfreeze()
  }
}

const cancelHandler = () => {
  if (entity.value) {
    viewEditorialPlaylistSong(entity.value)
  }
}

onMounted(async () => {
  const result = await getEditorialPlaylistSongById(pathId)
  if (result.data) {
    entity.value = result.data
  } else {
    openPopup()
  }
})
</script>
<style scoped></style>
