<template>
  <div class="w-full">
    <!-- Header -->
    <div class="flex items-center justify-between mb-6">
      <h3 class="text-xl font-semibold text-base-content">
        Song /
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
    <div
      v-if="
        entity &&
        editorialPlaylistSongLoaded &&
        listeningHistoryLoaded &&
        playlistSongLoaded &&
        songGenreLoaded &&
        upNextQueueLoaded
      "
    >
      <song-form
        :song="entity"
        :editorialPlaylistSongsData="editorialPlaylistSongs"
        :listeningHistorysData="listeningHistorys"
        :playlistSongsData="playlistSongs"
        :songGenresData="songGenres"
        :upNextQueuesData="upNextQueues"
        :violations="violations"
        :submit-label="$t('entity.update.submitLabel', { entity: 'Song' })"
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
import SongForm from '@/entities/song/SongForm.vue'
import AlertPopup from '@/components/popup/AlertPopup.vue'
import { useSongs } from '@/composables/useSongs'
import { Song, SongFormDTO } from '@/models/SongModel'
import { usePopup } from '@/composables/usePopup'
import GenesisButton from '@/components/button/GenesisButton.vue'
import LeftArrowIcon from '@/components/icons/LeftArrowIcon.vue'
import { useFreezeScreenStore } from '@/stores/useFreezeScreenStore.ts'
import { useEditorialPlaylistSongs } from '@/composables/useEditorialPlaylistSongs'
import { EditorialPlaylistSongFormDTO } from '@/models/EditorialPlaylistSongModel'
import { useListeningHistorys } from '@/composables/useListeningHistorys'
import { ListeningHistoryFormDTO } from '@/models/ListeningHistoryModel'
import { usePlaylistSongs } from '@/composables/usePlaylistSongs'
import { PlaylistSongFormDTO } from '@/models/PlaylistSongModel'
import { useSongGenres } from '@/composables/useSongGenres'
import { SongGenreFormDTO } from '@/models/SongGenreModel'
import { useUpNextQueues } from '@/composables/useUpNextQueues'
import { UpNextQueueFormDTO } from '@/models/UpNextQueueModel'

import { PaginationRequestParameter } from '@/models/api/RequestModel'

const route = useRoute()
const pathId = Number(route.params.id)
const { closePopup, openPopup, visible: alertPopup } = usePopup()
const { getSongById, goToListView, updateSong, viewSong, message } = useSongs()
const entity = ref<Song | null>(null)
const freezeStore = useFreezeScreenStore()

const { editorialPlaylistSongs, getAllEditorialPlaylistSongsBySongId } = useEditorialPlaylistSongs()
const editorialPlaylistSongLoaded = ref(false)
const loadEditorialPlaylistSongs = async () => {
  try {
    await getAllEditorialPlaylistSongsBySongId(pathId, true, new PaginationRequestParameter(), [])
    editorialPlaylistSongLoaded.value = true
  } catch (error) {
    console.error('Error loading song with details:', error)
    editorialPlaylistSongLoaded.value = true // Continuer même en cas d'erreur
  }
}
const { listeningHistorys, getAllListeningHistorysBySongId } = useListeningHistorys()
const listeningHistoryLoaded = ref(false)
const loadListeningHistorys = async () => {
  try {
    await getAllListeningHistorysBySongId(pathId, true, new PaginationRequestParameter(), [])
    listeningHistoryLoaded.value = true
  } catch (error) {
    console.error('Error loading song with details:', error)
    listeningHistoryLoaded.value = true // Continuer même en cas d'erreur
  }
}
const { playlistSongs, getAllPlaylistSongsBySongId } = usePlaylistSongs()
const playlistSongLoaded = ref(false)
const loadPlaylistSongs = async () => {
  try {
    await getAllPlaylistSongsBySongId(pathId, true, new PaginationRequestParameter(), [])
    playlistSongLoaded.value = true
  } catch (error) {
    console.error('Error loading song with details:', error)
    playlistSongLoaded.value = true // Continuer même en cas d'erreur
  }
}
const { songGenres, getAllSongGenresBySongId } = useSongGenres()
const songGenreLoaded = ref(false)
const loadSongGenres = async () => {
  try {
    await getAllSongGenresBySongId(pathId, true, new PaginationRequestParameter(), [])
    songGenreLoaded.value = true
  } catch (error) {
    console.error('Error loading song with details:', error)
    songGenreLoaded.value = true // Continuer même en cas d'erreur
  }
}
const { upNextQueues, getAllUpNextQueuesBySongId } = useUpNextQueues()
const upNextQueueLoaded = ref(false)
const loadUpNextQueues = async () => {
  try {
    await getAllUpNextQueuesBySongId(pathId, true, new PaginationRequestParameter(), [])
    upNextQueueLoaded.value = true
  } catch (error) {
    console.error('Error loading song with details:', error)
    upNextQueueLoaded.value = true // Continuer même en cas d'erreur
  }
}

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

const updateHandler = async (payload: {
  song: Partial<SongFormDTO>
  editorialPlaylistSongs: EditorialPlaylistSongFormDTO[]
  listeningHistorys: ListeningHistoryFormDTO[]
  playlistSongs: PlaylistSongFormDTO[]
  songGenres: SongGenreFormDTO[]
  upNextQueues: UpNextQueueFormDTO[]
}) => {
  freezeStore.freeze('Updating song ' + pathId + ' ...')
  message.value = null
  violations.value = {
    song: {},
    editorialPlaylistSongs: [],
    listeningHistorys: [],
    playlistSongs: [],
    songGenres: [],
    upNextQueues: [],
  }
  try {
    const { data, errors } = await updateSong(pathId, payload)

    if (errors && Object.keys(errors).length > 0) {
      // Gérer les erreurs de validation
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
    console.error(error)
  } finally {
    freezeStore.unfreeze()
  }
}

const cancelHandler = () => {
  if (entity.value) {
    viewSong(entity.value)
  }
}

onMounted(async () => {
  const result = await getSongById(pathId)
  if (result.data) {
    entity.value = result.data
    await loadEditorialPlaylistSongs()
    await loadListeningHistorys()
    await loadPlaylistSongs()
    await loadSongGenres()
    await loadUpNextQueues()
  } else {
    openPopup()
  }
})
</script>
<style scoped></style>
