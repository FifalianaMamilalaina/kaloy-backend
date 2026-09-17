<template>
  <div class="w-full">
    <div class="flex items-center justify-between mb-6">
      <h3 class="text-xl font-semibold text-base-content">
        {{ $t('entity.nav', { entity: 'Song' }) }}
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

    <song-form
      :submit-label="$t('entity.create.submitLabel', { entity: 'Song' })"
      :editorialPlaylistSongsData="initialEditorialPlaylistSongs"
      :listeningHistorysData="initialListeningHistorys"
      :playlistSongsData="initialPlaylistSongs"
      :songGenresData="initialSongGenres"
      :upNextQueuesData="initialUpNextQueues"
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
import SongForm from '@/entities/song/SongForm.vue'
import AlertPopup from '@/components/popup/AlertPopup.vue'
import GenesisButton from '@/components/button/GenesisButton.vue'
import LeftArrowIcon from '@/components/icons/LeftArrowIcon.vue'
import { SongFormDTO } from '@/models/SongModel'
import { useSongs } from '@/composables/useSongs'
import { usePopup } from '@/composables/usePopup'
import { useFreezeScreenStore } from '@/stores/useFreezeScreenStore'
import { ref } from 'vue'
import type {
  EditorialPlaylistSong,
  EditorialPlaylistSongFormDTO,
} from '@/models/EditorialPlaylistSongModel'
import type { ListeningHistory, ListeningHistoryFormDTO } from '@/models/ListeningHistoryModel'
import type { PlaylistSong, PlaylistSongFormDTO } from '@/models/PlaylistSongModel'
import type { SongGenre, SongGenreFormDTO } from '@/models/SongGenreModel'
import type { UpNextQueue, UpNextQueueFormDTO } from '@/models/UpNextQueueModel'

const { createSong, goToListView, message, viewSong } = useSongs()
const { openPopup, closePopup, visible: alertPopup } = usePopup()
const freezeStore = useFreezeScreenStore()
const violations = ref<
  | {
      song: Record<string, string>
      editorialPlaylistSongs: Record<string, string>[]
      listeningHistorys: Record<string, string>[]
      playlistSongs: Record<string, string>[]
      songGenres: Record<string, string>[]
      upNextQueues: Record<string, string>[]
    }
  | undefined
>(undefined)
const initialEditorialPlaylistSongs = ref<EditorialPlaylistSong[]>([])
const initialListeningHistorys = ref<ListeningHistory[]>([])
const initialPlaylistSongs = ref<PlaylistSong[]>([])
const initialSongGenres = ref<SongGenre[]>([])
const initialUpNextQueues = ref<UpNextQueue[]>([])

const createHandler = async (payload: {
  song: Partial<SongFormDTO>
  editorialPlaylistSongs: EditorialPlaylistSongFormDTO[]
  listeningHistorys: ListeningHistoryFormDTO[]
  playlistSongs: PlaylistSongFormDTO[]
  songGenres: SongGenreFormDTO[]
  upNextQueues: UpNextQueueFormDTO[]
}) => {
  freezeStore.freeze('Creating a new Song ...')
  violations.value = undefined
  message.value = null
  try {
    const { data, errors } = await createSong(payload)
    if (errors && Object.keys(errors).length > 0) {
      violations.value = errors as {
        song: Record<string, string>
        editorialPlaylistSongs: Record<string, string>[]
        listeningHistorys: Record<string, string>[]
        playlistSongs: Record<string, string>[]
        songGenres: Record<string, string>[]
        upNextQueues: Record<string, string>[]
      }
    }
    if (data && !message.value) {
      viewSong(data)
    } else {
      throw new Error(String(message.value))
    }
  } catch (error: unknown) {
    console.error('Error creating song:', error)
    openPopup()
  } finally {
    freezeStore.unfreeze()
  }
}
</script>
<style scoped></style>
