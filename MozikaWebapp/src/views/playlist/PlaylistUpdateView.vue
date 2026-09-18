<template>
  <div class="w-full">
    <!-- Header -->
    <div class="flex items-center justify-between mb-6">
      <h3 class="text-xl font-semibold text-base-content">
        Playlist /
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
    <div v-if="entity && downloadLoaded && playlistSongLoaded">
      <playlist-form
        :playlist="entity"
        :downloadsData="downloads"
        :playlistSongsData="playlistSongs"
        :violations="violations"
        :submit-label="$t('entity.update.submitLabel', { entity: 'Playlist' })"
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
import PlaylistForm from '@/entities/playlist/PlaylistForm.vue'
import AlertPopup from '@/components/popup/AlertPopup.vue'
import { usePlaylists } from '@/composables/usePlaylists'
import { Playlist, PlaylistFormDTO } from '@/models/PlaylistModel'
import { usePopup } from '@/composables/usePopup'
import GenesisButton from '@/components/button/GenesisButton.vue'
import LeftArrowIcon from '@/components/icons/LeftArrowIcon.vue'
import { useFreezeScreenStore } from '@/stores/useFreezeScreenStore.ts'
import { useDownloads } from '@/composables/useDownloads'
import { DownloadFormDTO } from '@/models/DownloadModel'
import { usePlaylistSongs } from '@/composables/usePlaylistSongs'
import { PlaylistSongFormDTO } from '@/models/PlaylistSongModel'

import { PaginationRequestParameter } from '@/models/api/RequestModel'

const route = useRoute()
const pathId = Number(route.params.id)
const { closePopup, openPopup, visible: alertPopup } = usePopup()
const { getPlaylistById, goToListView, updatePlaylist, viewPlaylist, message } = usePlaylists()
const entity = ref<Playlist | null>(null)
const freezeStore = useFreezeScreenStore()

const { downloads, getAllDownloadsByPlaylistId } = useDownloads()
const downloadLoaded = ref(false)
const loadDownloads = async () => {
  try {
    await getAllDownloadsByPlaylistId(pathId, true, new PaginationRequestParameter(), [])
    downloadLoaded.value = true
  } catch (error) {
    console.error('Error loading playlist with details:', error)
    downloadLoaded.value = true // Continuer même en cas d'erreur
  }
}
const { playlistSongs, getAllPlaylistSongsByPlaylistId } = usePlaylistSongs()
const playlistSongLoaded = ref(false)
const loadPlaylistSongs = async () => {
  try {
    await getAllPlaylistSongsByPlaylistId(pathId, true, new PaginationRequestParameter(), [])
    playlistSongLoaded.value = true
  } catch (error) {
    console.error('Error loading playlist with details:', error)
    playlistSongLoaded.value = true // Continuer même en cas d'erreur
  }
}

const violations = ref<
  | {
      playlist: Record<string, string>
      downloads: Record<string, string>[]
      playlistSongs: Record<string, string>[]
    }
  | undefined
>(undefined)

const updateHandler = async (payload: {
  playlist: Partial<PlaylistFormDTO>
  downloads: DownloadFormDTO[]
  playlistSongs: PlaylistSongFormDTO[]
}) => {
  freezeStore.freeze('Updating playlist ' + pathId + ' ...')
  message.value = null
  violations.value = {
    playlist: {},
    downloads: [],
    playlistSongs: [],
  }
  try {
    const { data, errors } = await updatePlaylist(pathId, payload)

    if (errors && Object.keys(errors).length > 0) {
      // Gérer les erreurs de validation
      violations.value = errors as {
        playlist: Record<string, string>
        downloads: Record<string, string>[]
        playlistSongs: Record<string, string>[]
      }
    }
    if (data && !message.value) {
      viewPlaylist(data)
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
    viewPlaylist(entity.value)
  }
}

onMounted(async () => {
  const result = await getPlaylistById(pathId)
  if (result.data) {
    entity.value = result.data
    await loadDownloads()
    await loadPlaylistSongs()
  } else {
    openPopup()
  }
})
</script>
<style scoped></style>
