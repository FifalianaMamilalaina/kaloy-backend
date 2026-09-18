<template>
  <div class="w-full">
    <!-- Header -->
    <div class="flex items-center justify-between mb-6">
      <h3 class="text-xl font-semibold text-base-content">
        Album /
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
    <div v-if="entity && songLoaded">
      <album-form
        :album="entity"
        :songsData="songs"
        :violations="violations"
        :submit-label="$t('entity.update.submitLabel', { entity: 'Album' })"
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
import AlbumForm from '@/entities/album/AlbumForm.vue'
import AlertPopup from '@/components/popup/AlertPopup.vue'
import { useAlbums } from '@/composables/useAlbums'
import { Album, AlbumFormDTO } from '@/models/AlbumModel'
import { usePopup } from '@/composables/usePopup'
import GenesisButton from '@/components/button/GenesisButton.vue'
import LeftArrowIcon from '@/components/icons/LeftArrowIcon.vue'
import { useFreezeScreenStore } from '@/stores/useFreezeScreenStore.ts'
import { useSongs } from '@/composables/useSongs'
import { SongFormDTO } from '@/models/SongModel'

import { PaginationRequestParameter } from '@/models/api/RequestModel'

const route = useRoute()
const pathId = Number(route.params.id)
const { closePopup, openPopup, visible: alertPopup } = usePopup()
const { getAlbumById, goToListView, updateAlbum, viewAlbum, message } = useAlbums()
const entity = ref<Album | null>(null)
const freezeStore = useFreezeScreenStore()

const { songs, getAllSongsByAlbumId } = useSongs()
const songLoaded = ref(false)
const loadSongs = async () => {
  try {
    await getAllSongsByAlbumId(pathId, true, new PaginationRequestParameter(), [])
    songLoaded.value = true
  } catch (error) {
    console.error('Error loading album with details:', error)
    songLoaded.value = true // Continuer même en cas d'erreur
  }
}

const violations = ref<
  | {
      album: Record<string, string>
      songs: Record<string, string>[]
    }
  | undefined
>(undefined)

const updateHandler = async (payload: { album: Partial<AlbumFormDTO>; songs: SongFormDTO[] }) => {
  freezeStore.freeze('Updating album ' + pathId + ' ...')
  message.value = null
  violations.value = {
    album: {},
    songs: [],
  }
  try {
    const { data, errors } = await updateAlbum(pathId, payload)

    if (errors && Object.keys(errors).length > 0) {
      // Gérer les erreurs de validation
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
    console.error(error)
  } finally {
    freezeStore.unfreeze()
  }
}

const cancelHandler = () => {
  if (entity.value) {
    viewAlbum(entity.value)
  }
}

onMounted(async () => {
  const result = await getAlbumById(pathId)
  if (result.data) {
    entity.value = result.data
    await loadSongs()
  } else {
    openPopup()
  }
})
</script>
<style scoped></style>
