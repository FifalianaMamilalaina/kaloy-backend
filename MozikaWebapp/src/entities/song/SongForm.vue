<template>
  <div class="card bg-base-100">
    <div class="card-body">
      <form @submit.prevent="handleSubmit" class="grid grid-cols-1 w-7/12 gap-4 mx-auto">
        <!-- <form @submit.prevent="handleSubmit" class=""> -->
        <div class="grid grid-cols-1 gap-4">
          <!-- Artistid artists FK -->
          <GenesisSelectSearchCriteria
            v-if="artistidArtistsSearchField?.multicriteriaSelect"
            label="Artistid artists"
            key="songArtistidArtists"
            :search-function="artistidArtistsSearchField?.multicriteriaSelect.searchFunction"
            :filters="artistidArtistsSearchField?.multicriteriaSelect.filters"
            :default-value="artistidArtistsDefaultValue"
            :violation="songViolations ? songViolations['artistidArtists'] : undefined"
            @option-selected="
              (selectedValue) => (formModel.artistidArtists = String(selectedValue))
            "
          />

          <!-- Albumid albums FK -->
          <GenesisSelectSearchCriteria
            v-if="albumidAlbumsSearchField?.multicriteriaSelect"
            label="Albumid albums"
            key="songAlbumidAlbums"
            :search-function="albumidAlbumsSearchField?.multicriteriaSelect.searchFunction"
            :filters="albumidAlbumsSearchField?.multicriteriaSelect.filters"
            :default-value="albumidAlbumsDefaultValue"
            :violation="songViolations ? songViolations['albumidAlbums'] : undefined"
            @option-selected="(selectedValue) => (formModel.albumidAlbums = String(selectedValue))"
          />

          <!-- Title -->
          <GenesisInput
            label="Title"
            :violation="songViolations ? songViolations['title'] : undefined"
            placeholder="Enter Title"
            type="text"
            v-model="formModel.title"
            :value="formModel.title"
          />

          <!-- Duration seconds -->
          <GenesisInput
            label="Duration seconds"
            :violation="songViolations ? songViolations['durationSeconds'] : undefined"
            placeholder="Enter Duration seconds"
            type="number"
            v-model="formModel.durationSeconds"
            :value="formModel.durationSeconds"
          />

          <!-- Release date -->
          <GenesisInput
            label="Release date"
            :violation="songViolations ? songViolations['releaseDate'] : undefined"
            placeholder="Enter Release date"
            type="date"
            v-model="formModel.releaseDate"
            :value="formModel.releaseDate"
          />

          <!-- Language -->
          <GenesisInput
            label="Language"
            :violation="songViolations ? songViolations['language'] : undefined"
            placeholder="Enter Language"
            type="text"
            v-model="formModel.language"
            :value="formModel.language"
          />

          <!-- Author composer -->
          <GenesisInput
            label="Author composer"
            :violation="songViolations ? songViolations['authorComposer'] : undefined"
            placeholder="Enter Author composer"
            type="text"
            v-model="formModel.authorComposer"
            :value="formModel.authorComposer"
          />

          <!-- Musical arranger -->
          <GenesisInput
            label="Musical arranger"
            :violation="songViolations ? songViolations['musicalArranger'] : undefined"
            placeholder="Enter Musical arranger"
            type="text"
            v-model="formModel.musicalArranger"
            :value="formModel.musicalArranger"
          />

          <!-- Recording location -->
          <GenesisInput
            label="Recording location"
            :violation="songViolations ? songViolations['recordingLocation'] : undefined"
            placeholder="Enter Recording location"
            type="text"
            v-model="formModel.recordingLocation"
            :value="formModel.recordingLocation"
          />

          <!-- Recording date -->
          <GenesisInput
            label="Recording date"
            :violation="songViolations ? songViolations['recordingDate'] : undefined"
            placeholder="Enter Recording date"
            type="date"
            v-model="formModel.recordingDate"
            :value="formModel.recordingDate"
          />

          <!-- Storagetypeid audio storage types FK -->
          <GenesisSelectSearchCriteria
            v-if="storagetypeidAudioStorageTypesSearchField?.multicriteriaSelect"
            label="Storagetypeid audio storage types"
            key="songStoragetypeidAudioStorageTypes"
            :search-function="
              storagetypeidAudioStorageTypesSearchField?.multicriteriaSelect.searchFunction
            "
            :filters="storagetypeidAudioStorageTypesSearchField?.multicriteriaSelect.filters"
            :default-value="storagetypeidAudioStorageTypesDefaultValue"
            :violation="
              songViolations ? songViolations['storagetypeidAudioStorageTypes'] : undefined
            "
            @option-selected="
              (selectedValue) => (formModel.storagetypeidAudioStorageTypes = String(selectedValue))
            "
          />

          <!-- Audio url -->
          <GenesisInput
            label="Audio url"
            :violation="songViolations ? songViolations['audioUrl'] : undefined"
            placeholder="Enter Audio url"
            type="text"
            v-model="formModel.audioUrl"
            :value="formModel.audioUrl"
          />

          <!-- Audio file -->
          <GenesisInput
            label="Audio file"
            :violation="songViolations ? songViolations['audioFile'] : undefined"
            placeholder="Enter Audio file"
            type="file"
            v-model="formModel.audioFile"
          />
          <img :src="audioFileUrl" alt="No image" />

          <!-- Video url -->
          <GenesisInput
            label="Video url"
            :violation="songViolations ? songViolations['videoUrl'] : undefined"
            placeholder="Enter Video url"
            type="text"
            v-model="formModel.videoUrl"
            :value="formModel.videoUrl"
          />

          <!-- Karaoke audio -->
          <GenesisInput
            label="Karaoke audio"
            :violation="songViolations ? songViolations['karaokeAudio'] : undefined"
            placeholder="Enter Karaoke audio"
            type="file"
            v-model="formModel.karaokeAudio"
          />
          <img :src="karaokeAudioUrl" alt="No image" />

          <!-- Lyrics -->
          <GenesisInput
            label="Lyrics"
            :violation="songViolations ? songViolations['lyrics'] : undefined"
            placeholder="Enter Lyrics"
            type="text"
            v-model="formModel.lyrics"
            :value="formModel.lyrics"
          />

          <!-- Lyrics sync data -->
          <GenesisInput
            label="Lyrics sync data"
            :violation="songViolations ? songViolations['lyricsSyncData'] : undefined"
            placeholder="Enter Lyrics sync data"
            type="text"
            v-model="formModel.lyricsSyncData"
            :value="formModel.lyricsSyncData"
          />

          <!-- Solfa -->
          <GenesisInput
            label="Solfa"
            :violation="songViolations ? songViolations['solfa'] : undefined"
            placeholder="Enter Solfa"
            type="text"
            v-model="formModel.solfa"
            :value="formModel.solfa"
          />

          <!-- Playback -->
          <GenesisInput
            label="Playback"
            :violation="songViolations ? songViolations['playback'] : undefined"
            placeholder="Enter Playback"
            type="file"
            v-model="formModel.playback"
          />
          <img :src="playbackUrl" alt="No image" />

          <!-- Is downloadable -->
          <label class="flex items-center gap-2">
            <input type="checkbox" class="checkbox" v-model="formModel.isDownloadable" />
            <span>Is downloadable</span>
          </label>

          <!-- Created at -->
          <GenesisInput
            label="Created at"
            :violation="songViolations ? songViolations['createdAt'] : undefined"
            placeholder="Enter Created at"
            type="datetime-local"
            v-model="formModel.createdAt"
            :value="formModel.createdAt"
          />
        </div>
        <EditorialPlaylistSongTableForm
          :initial-data="editorialPlaylistSongsData"
          :get-table-data-callback="setEditorialPlaylistSongs"
          :violations="editorialPlaylistSongViolations"
          class="w-full"
        />
        <ListeningHistoryTableForm
          :initial-data="listeningHistorysData"
          :get-table-data-callback="setListeningHistorys"
          :violations="listeningHistoryViolations"
          class="w-full"
        />
        <PlaylistSongTableForm
          :initial-data="playlistSongsData"
          :get-table-data-callback="setPlaylistSongs"
          :violations="playlistSongViolations"
          class="w-full"
        />
        <SongGenreTableForm
          :initial-data="songGenresData"
          :get-table-data-callback="setSongGenres"
          :violations="songGenreViolations"
          class="w-full"
        />
        <UpNextQueueTableForm
          :initial-data="upNextQueuesData"
          :get-table-data-callback="setUpNextQueues"
          :violations="upNextQueueViolations"
          class="w-full"
        />

        <!-- Action buttons -->
        <div class="flex justify-end gap-3 mt-6">
          <GenesisButton
            type="submit"
            class="btn btn-primary text-primary-content"
            :label="submitLabel"
          />
          <GenesisButton
            @click="cancelForm"
            class="btn btn-outline btn-error"
            :label="$t('button.cancel')"
          />
        </div>
      </form>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { Song, SongFormDTO } from '@/models/SongModel.ts'
import GenesisButton from '@/components/button/GenesisButton.vue'
import GenesisInput from '@/components/form/GenesisInput.vue'
import GenesisSelectSearchCriteria from '@/components/form/GenesisSelectSearchCriteria.vue'
import {
  EditorialPlaylistSongFormDTO,
  type EditorialPlaylistSong,
} from '@/models/EditorialPlaylistSongModel.ts'
import EditorialPlaylistSongTableForm from '@/entities/editorialPlaylistSong/EditorialPlaylistSongTableForm.vue'
import { ListeningHistoryFormDTO, type ListeningHistory } from '@/models/ListeningHistoryModel.ts'
import ListeningHistoryTableForm from '@/entities/listeningHistory/ListeningHistoryTableForm.vue'
import { PlaylistSongFormDTO, type PlaylistSong } from '@/models/PlaylistSongModel.ts'
import PlaylistSongTableForm from '@/entities/playlistSong/PlaylistSongTableForm.vue'
import { SongGenreFormDTO, type SongGenre } from '@/models/SongGenreModel.ts'
import SongGenreTableForm from '@/entities/songGenre/SongGenreTableForm.vue'
import { UpNextQueueFormDTO, type UpNextQueue } from '@/models/UpNextQueueModel.ts'
import UpNextQueueTableForm from '@/entities/upNextQueue/UpNextQueueTableForm.vue'

const props = defineProps<{
  song?: Song

  editorialPlaylistSongsData?: EditorialPlaylistSong[]
  listeningHistorysData?: ListeningHistory[]
  playlistSongsData?: PlaylistSong[]
  songGenresData?: SongGenre[]
  upNextQueuesData?: UpNextQueue[]

  violations?: {
    song: Record<string, string>
    editorialPlaylistSongs: Record<string, string>[]
    listeningHistorys: Record<string, string>[]
    playlistSongs: Record<string, string>[]
    songGenres: Record<string, string>[]
    upNextQueues: Record<string, string>[]
  }

  submitLabel?: string
}>()

const emit = defineEmits<{
  (
    e: 'submit',
    payload: {
      song: Partial<SongFormDTO>
      editorialPlaylistSongs: EditorialPlaylistSongFormDTO[]
      listeningHistorys: ListeningHistoryFormDTO[]
      playlistSongs: PlaylistSongFormDTO[]
      songGenres: SongGenreFormDTO[]
      upNextQueues: UpNextQueueFormDTO[]
    },
  ): void
  (e: 'cancel', payload: Partial<SongFormDTO>): void
}>()

const songViolations = computed(() => (props.violations ? props.violations.song : {}))
const editorialPlaylistSongViolations = computed(() =>
  props.violations ? props.violations.editorialPlaylistSongs : [],
)
const listeningHistoryViolations = computed(() =>
  props.violations ? props.violations.listeningHistorys : [],
)
const playlistSongViolations = computed(() =>
  props.violations ? props.violations.playlistSongs : [],
)
const songGenreViolations = computed(() => (props.violations ? props.violations.songGenres : []))
const upNextQueueViolations = computed(() =>
  props.violations ? props.violations.upNextQueues : [],
)

const audioFileUrl = computed(() => {
  const audioFile = formModel.value.audioFile
  if (!audioFile) return ''

  if (audioFile instanceof File) {
    return URL.createObjectURL(audioFile)
  }
  return 'data:image/png;base64,' + audioFile
})

const karaokeAudioUrl = computed(() => {
  const karaokeAudio = formModel.value.karaokeAudio
  if (!karaokeAudio) return ''

  if (karaokeAudio instanceof File) {
    return URL.createObjectURL(karaokeAudio)
  }
  return 'data:image/png;base64,' + karaokeAudio
})

const playbackUrl = computed(() => {
  const playback = formModel.value.playback
  if (!playback) return ''

  if (playback instanceof File) {
    return URL.createObjectURL(playback)
  }
  return 'data:image/png;base64,' + playback
})

const formModel = ref<Partial<SongFormDTO>>({ ...SongFormDTO.parse(props.song) })
const artistidArtistsSearchField = Song.getSearchFieldByKey('artistidArtists')
const artistidArtistsDefaultValue = props.song
  ? (props.song?.artistidArtists?.getKeyValue?.() ?? undefined)
  : undefined
const albumidAlbumsSearchField = Song.getSearchFieldByKey('albumidAlbums')
const albumidAlbumsDefaultValue = props.song
  ? (props.song?.albumidAlbums?.getKeyValue?.() ?? undefined)
  : undefined
const storagetypeidAudioStorageTypesSearchField = Song.getSearchFieldByKey(
  'storagetypeidAudioStorageTypes',
)
const storagetypeidAudioStorageTypesDefaultValue = props.song
  ? (props.song?.storagetypeidAudioStorageTypes?.getKeyValue?.() ?? undefined)
  : undefined

const editorialPlaylistSongs = ref<EditorialPlaylistSongFormDTO[]>([])
const setEditorialPlaylistSongs = (data: EditorialPlaylistSongFormDTO[]) => {
  editorialPlaylistSongs.value = data
}
const listeningHistorys = ref<ListeningHistoryFormDTO[]>([])
const setListeningHistorys = (data: ListeningHistoryFormDTO[]) => {
  listeningHistorys.value = data
}
const playlistSongs = ref<PlaylistSongFormDTO[]>([])
const setPlaylistSongs = (data: PlaylistSongFormDTO[]) => {
  playlistSongs.value = data
}
const songGenres = ref<SongGenreFormDTO[]>([])
const setSongGenres = (data: SongGenreFormDTO[]) => {
  songGenres.value = data
}
const upNextQueues = ref<UpNextQueueFormDTO[]>([])
const setUpNextQueues = (data: UpNextQueueFormDTO[]) => {
  upNextQueues.value = data
}

const fileToBase64 = (file: File): Promise<string> => {
  return new Promise((resolve, reject) => {
    const reader = new FileReader()
    reader.onload = () => {
      if (typeof reader.result !== 'string') {
        reject(new Error('Unable to read the selected file'))
        return
      }
      const commaIndex = reader.result.indexOf(',')
      resolve(commaIndex >= 0 ? reader.result.substring(commaIndex + 1) : reader.result)
    }
    reader.onerror = () => {
      reject(reader.error ?? new Error('Unable to read the selected file'))
    }
    reader.readAsDataURL(file)
  })
}
async function handleSubmit() {
  const data: any = { ...formModel.value }
  Object.keys(data).forEach((key) => {
    if (data[key] === '' || data[key] === null || data[key] === undefined) {
      delete data[key]
    }
  })
  if (data.audioFile instanceof File) {
    data.audioFile = await fileToBase64(data.audioFile)
  }
  if (data.karaokeAudio instanceof File) {
    data.karaokeAudio = await fileToBase64(data.karaokeAudio)
  }
  if (data.playback instanceof File) {
    data.playback = await fileToBase64(data.playback)
  }

  emit('submit', {
    song: data,
    editorialPlaylistSongs: editorialPlaylistSongs.value,
    listeningHistorys: listeningHistorys.value,
    playlistSongs: playlistSongs.value,
    songGenres: songGenres.value,
    upNextQueues: upNextQueues.value,
  })
}

function cancelForm() {
  emit('cancel', formModel.value)
}
onMounted(async () => {
  if (
    Array.isArray(props.editorialPlaylistSongsData) &&
    props.editorialPlaylistSongsData.length > 0
  ) {
    setEditorialPlaylistSongs(
      EditorialPlaylistSongFormDTO.parseList(props.editorialPlaylistSongsData),
    )
  }
  if (Array.isArray(props.listeningHistorysData) && props.listeningHistorysData.length > 0) {
    setListeningHistorys(ListeningHistoryFormDTO.parseList(props.listeningHistorysData))
  }
  if (Array.isArray(props.playlistSongsData) && props.playlistSongsData.length > 0) {
    setPlaylistSongs(PlaylistSongFormDTO.parseList(props.playlistSongsData))
  }
  if (Array.isArray(props.songGenresData) && props.songGenresData.length > 0) {
    setSongGenres(SongGenreFormDTO.parseList(props.songGenresData))
  }
  if (Array.isArray(props.upNextQueuesData) && props.upNextQueuesData.length > 0) {
    setUpNextQueues(UpNextQueueFormDTO.parseList(props.upNextQueuesData))
  }
})
</script>
<style scoped></style>
