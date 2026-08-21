<template>
  <div class="card bg-base-100">
    <div class="card-body">
      <form @submit.prevent="handleSubmit" class="grid grid-cols-1 w-7/12 gap-4 mx-auto">
        <!-- <form @submit.prevent="handleSubmit" class=""> -->
        <div class="grid grid-cols-1 gap-4">
          <!-- Owneruserid users FK -->
          <GenesisSelectSearchCriteria
            v-if="owneruseridUsersSearchField?.multicriteriaSelect"
            label="Owneruserid users"
            key="playlistOwneruseridUsers"
            :search-function="owneruseridUsersSearchField?.multicriteriaSelect.searchFunction"
            :filters="owneruseridUsersSearchField?.multicriteriaSelect.filters"
            :default-value="owneruseridUsersDefaultValue"
            :violation="playlistViolations ? playlistViolations['owneruseridUsers'] : undefined"
            @option-selected="
              (selectedValue) => (formModel.owneruseridUsers = String(selectedValue))
            "
          />

          <!-- Name -->
          <GenesisInput
            label="Name"
            :violation="playlistViolations ? playlistViolations['name'] : undefined"
            placeholder="Enter Name"
            type="text"
            v-model="formModel.name"
            :value="formModel.name"
          />

          <!-- Visibilityid playlist visibilities FK -->
          <GenesisSelectSearchCriteria
            v-if="visibilityidPlaylistVisibilitiesSearchField?.multicriteriaSelect"
            label="Visibilityid playlist visibilities"
            key="playlistVisibilityidPlaylistVisibilities"
            :search-function="
              visibilityidPlaylistVisibilitiesSearchField?.multicriteriaSelect.searchFunction
            "
            :filters="visibilityidPlaylistVisibilitiesSearchField?.multicriteriaSelect.filters"
            :default-value="visibilityidPlaylistVisibilitiesDefaultValue"
            :violation="
              playlistViolations
                ? playlistViolations['visibilityidPlaylistVisibilities']
                : undefined
            "
            @option-selected="
              (selectedValue) =>
                (formModel.visibilityidPlaylistVisibilities = String(selectedValue))
            "
          />

          <!-- Share token -->
          <GenesisInput
            label="Share token"
            :violation="playlistViolations ? playlistViolations['shareToken'] : undefined"
            placeholder="Enter Share token"
            type="text"
            v-model="formModel.shareToken"
            :value="formModel.shareToken"
          />

          <!-- Created at -->
          <GenesisInput
            label="Created at"
            :violation="playlistViolations ? playlistViolations['createdAt'] : undefined"
            placeholder="Enter Created at"
            type="datetime-local"
            v-model="formModel.createdAt"
            :value="formModel.createdAt"
          />

          <!-- Updated at -->
          <GenesisInput
            label="Updated at"
            :violation="playlistViolations ? playlistViolations['updatedAt'] : undefined"
            placeholder="Enter Updated at"
            type="datetime-local"
            v-model="formModel.updatedAt"
            :value="formModel.updatedAt"
          />
        </div>
        <DownloadTableForm
          :initial-data="downloadsData"
          :get-table-data-callback="setDownloads"
          :violations="downloadViolations"
          class="w-full"
        />
        <PlaylistSongTableForm
          :initial-data="playlistSongsData"
          :get-table-data-callback="setPlaylistSongs"
          :violations="playlistSongViolations"
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
import { Playlist, PlaylistFormDTO } from '@/models/PlaylistModel.ts'
import GenesisButton from '@/components/button/GenesisButton.vue'
import GenesisInput from '@/components/form/GenesisInput.vue'
import GenesisSelectSearchCriteria from '@/components/form/GenesisSelectSearchCriteria.vue'
import { DownloadFormDTO, type Download } from '@/models/DownloadModel.ts'
import DownloadTableForm from '@/entities/download/DownloadTableForm.vue'
import { PlaylistSongFormDTO, type PlaylistSong } from '@/models/PlaylistSongModel.ts'
import PlaylistSongTableForm from '@/entities/playlistSong/PlaylistSongTableForm.vue'

const props = defineProps<{
  playlist?: Playlist

  downloadsData?: Download[]
  playlistSongsData?: PlaylistSong[]

  violations?: {
    playlist: Record<string, string>
    downloads: Record<string, string>[]
    playlistSongs: Record<string, string>[]
  }

  submitLabel?: string
}>()

const emit = defineEmits<{
  (
    e: 'submit',
    payload: {
      playlist: Partial<PlaylistFormDTO>
      downloads: DownloadFormDTO[]
      playlistSongs: PlaylistSongFormDTO[]
    },
  ): void
  (e: 'cancel', payload: Partial<PlaylistFormDTO>): void
}>()

const playlistViolations = computed(() => (props.violations ? props.violations.playlist : {}))
const downloadViolations = computed(() => (props.violations ? props.violations.downloads : []))
const playlistSongViolations = computed(() =>
  props.violations ? props.violations.playlistSongs : [],
)

const formModel = ref<Partial<PlaylistFormDTO>>({ ...PlaylistFormDTO.parse(props.playlist) })
const owneruseridUsersSearchField = Playlist.getSearchFieldByKey('owneruseridUsers')
const owneruseridUsersDefaultValue = props.playlist
  ? (props.playlist?.owneruseridUsers?.getKeyValue?.() ?? undefined)
  : undefined
const visibilityidPlaylistVisibilitiesSearchField = Playlist.getSearchFieldByKey(
  'visibilityidPlaylistVisibilities',
)
const visibilityidPlaylistVisibilitiesDefaultValue = props.playlist
  ? (props.playlist?.visibilityidPlaylistVisibilities?.getKeyValue?.() ?? undefined)
  : undefined

const downloads = ref<DownloadFormDTO[]>([])
const setDownloads = (data: DownloadFormDTO[]) => {
  downloads.value = data
}
const playlistSongs = ref<PlaylistSongFormDTO[]>([])
const setPlaylistSongs = (data: PlaylistSongFormDTO[]) => {
  playlistSongs.value = data
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

  emit('submit', { playlist: data, downloads: downloads.value, playlistSongs: playlistSongs.value })
}

function cancelForm() {
  emit('cancel', formModel.value)
}
onMounted(async () => {
  if (Array.isArray(props.downloadsData) && props.downloadsData.length > 0) {
    setDownloads(DownloadFormDTO.parseList(props.downloadsData))
  }
  if (Array.isArray(props.playlistSongsData) && props.playlistSongsData.length > 0) {
    setPlaylistSongs(PlaylistSongFormDTO.parseList(props.playlistSongsData))
  }
})
</script>
<style scoped></style>
