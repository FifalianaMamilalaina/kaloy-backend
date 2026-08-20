<template>
  <div class="w-full">
    <div class="flex items-center justify-between mb-6">
      <h3 class="text-xl font-semibold text-base-content">
        {{ $t('entity.nav', { entity: 'Album' }) }}
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

    <album-form
      :submit-label="$t('entity.create.submitLabel', { entity: 'Album' })"
      :songsData="initialSongs"
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
import AlbumForm from '@/entities/album/AlbumForm.vue'
import AlertPopup from '@/components/popup/AlertPopup.vue'
import GenesisButton from '@/components/button/GenesisButton.vue'
import LeftArrowIcon from '@/components/icons/LeftArrowIcon.vue'
import { AlbumFormDTO } from '@/models/AlbumModel'
import { useAlbums } from '@/composables/useAlbums'
import { usePopup } from '@/composables/usePopup'
import { useFreezeScreenStore } from '@/stores/useFreezeScreenStore'
import { ref } from 'vue'
import type { Song, SongFormDTO } from '@/models/SongModel'

const { createAlbum, goToListView, message, viewAlbum } = useAlbums()
const { openPopup, closePopup, visible: alertPopup } = usePopup()
const freezeStore = useFreezeScreenStore()
const violations = ref<
  | {
      album: Record<string, string>
      songs: Record<string, string>[]
    }
  | undefined
>(undefined)
const initialSongs = ref<Song[]>([])

const createHandler = async (payload: { album: Partial<AlbumFormDTO>; songs: SongFormDTO[] }) => {
  freezeStore.freeze('Creating a new Album ...')
  violations.value = undefined
  message.value = null
  try {
    const { data, errors } = await createAlbum(payload)
    if (errors && Object.keys(errors).length > 0) {
      violations.value = errors as {
        album: Record<string, string>
        songs: Record<string, string>[]
      }
    }
    if (data && !message.value) {
      viewAlbum(data)
    } else {
      throw new Error(String(message.value))
    }
  } catch (error: unknown) {
    console.error('Error creating album:', error)
    openPopup()
  } finally {
    freezeStore.unfreeze()
  }
}
</script>
<style scoped></style>
