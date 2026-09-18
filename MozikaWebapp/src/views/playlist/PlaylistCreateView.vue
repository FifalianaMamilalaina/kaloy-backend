<template>
  <div class="w-full">
    <div class="flex items-center justify-between mb-6">
      <h3 class="text-xl font-semibold text-base-content">
        {{ $t('entity.nav', { entity: 'Playlist' }) }}
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

    <playlist-form
      :submit-label="$t('entity.create.submitLabel', { entity: 'Playlist' })"
      :downloadsData="initialDownloads"
      :playlistSongsData="initialPlaylistSongs"
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
import PlaylistForm from '@/entities/playlist/PlaylistForm.vue'
import AlertPopup from '@/components/popup/AlertPopup.vue'
import GenesisButton from '@/components/button/GenesisButton.vue'
import LeftArrowIcon from '@/components/icons/LeftArrowIcon.vue'
import { PlaylistFormDTO } from '@/models/PlaylistModel'
import { usePlaylists } from '@/composables/usePlaylists'
import { usePopup } from '@/composables/usePopup'
import { useFreezeScreenStore } from '@/stores/useFreezeScreenStore'
import { ref } from 'vue'
import type { Download, DownloadFormDTO } from '@/models/DownloadModel'
import type { PlaylistSong, PlaylistSongFormDTO } from '@/models/PlaylistSongModel'

const { createPlaylist, goToListView, message, viewPlaylist } = usePlaylists()
const { openPopup, closePopup, visible: alertPopup } = usePopup()
const freezeStore = useFreezeScreenStore()
const violations = ref<
  | {
      playlist: Record<string, string>
      downloads: Record<string, string>[]
      playlistSongs: Record<string, string>[]
    }
  | undefined
>(undefined)
const initialDownloads = ref<Download[]>([])
const initialPlaylistSongs = ref<PlaylistSong[]>([])

const createHandler = async (payload: {
  playlist: Partial<PlaylistFormDTO>
  downloads: DownloadFormDTO[]
  playlistSongs: PlaylistSongFormDTO[]
}) => {
  freezeStore.freeze('Creating a new Playlist ...')
  violations.value = undefined
  message.value = null
  try {
    const { data, errors } = await createPlaylist(payload)
    if (errors && Object.keys(errors).length > 0) {
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
    console.error('Error creating playlist:', error)
    openPopup()
  } finally {
    freezeStore.unfreeze()
  }
}
</script>
<style scoped></style>
