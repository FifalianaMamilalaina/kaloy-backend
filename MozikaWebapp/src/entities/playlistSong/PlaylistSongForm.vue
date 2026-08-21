<template>
  <div class="card bg-base-100">
    <div class="card-body">
      <form @submit.prevent="handleSubmit" class="grid grid-cols-1 w-7/12 gap-4 mx-auto">
        <!-- <form @submit.prevent="handleSubmit" class=""> -->
        <div class="grid grid-cols-1 gap-4">
          <!-- Playlistid playlists FK -->
          <GenesisSelectSearchCriteria
            v-if="playlistidPlaylistsSearchField?.multicriteriaSelect"
            label="Playlistid playlists"
            key="playlistSongPlaylistidPlaylists"
            :search-function="playlistidPlaylistsSearchField?.multicriteriaSelect.searchFunction"
            :filters="playlistidPlaylistsSearchField?.multicriteriaSelect.filters"
            :default-value="playlistidPlaylistsDefaultValue"
            :violation="violations ? violations['playlistidPlaylists'] : undefined"
            @option-selected="
              (selectedValue) => (formModel.playlistidPlaylists = String(selectedValue))
            "
          />

          <!-- Songid songs FK -->
          <GenesisSelectSearchCriteria
            v-if="songidSongsSearchField?.multicriteriaSelect"
            label="Songid songs"
            key="playlistSongSongidSongs"
            :search-function="songidSongsSearchField?.multicriteriaSelect.searchFunction"
            :filters="songidSongsSearchField?.multicriteriaSelect.filters"
            :default-value="songidSongsDefaultValue"
            :violation="violations ? violations['songidSongs'] : undefined"
            @option-selected="(selectedValue) => (formModel.songidSongs = String(selectedValue))"
          />

          <!-- Position -->
          <GenesisInput
            label="Position"
            :violation="violations ? violations['position'] : undefined"
            placeholder="Enter Position"
            type="number"
            v-model="formModel.position"
            :value="formModel.position"
          />

          <!-- Added at -->
          <GenesisInput
            label="Added at"
            :violation="violations ? violations['addedAt'] : undefined"
            placeholder="Enter Added at"
            type="datetime-local"
            v-model="formModel.addedAt"
            :value="formModel.addedAt"
          />
        </div>

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
import { ref } from 'vue'
import { PlaylistSong, PlaylistSongFormDTO } from '@/models/PlaylistSongModel.ts'
import GenesisButton from '@/components/button/GenesisButton.vue'
import GenesisInput from '@/components/form/GenesisInput.vue'
import GenesisSelectSearchCriteria from '@/components/form/GenesisSelectSearchCriteria.vue'

const props = defineProps<{
  playlistSong?: PlaylistSong
  violations?: Record<string, string> | null
  submitLabel?: string
}>()

const emit = defineEmits<{
  (e: 'submit', payload: Partial<PlaylistSongFormDTO>): void
  (e: 'cancel', payload: Partial<PlaylistSongFormDTO>): void
}>()

const formModel = ref<Partial<PlaylistSongFormDTO>>({
  ...PlaylistSongFormDTO.parse(props.playlistSong),
})
const playlistidPlaylistsSearchField = PlaylistSong.getSearchFieldByKey('playlistidPlaylists')
const playlistidPlaylistsDefaultValue = props.playlistSong
  ? (props.playlistSong?.playlistidPlaylists?.getKeyValue?.() ?? undefined)
  : undefined
const songidSongsSearchField = PlaylistSong.getSearchFieldByKey('songidSongs')
const songidSongsDefaultValue = props.playlistSong
  ? (props.playlistSong?.songidSongs?.getKeyValue?.() ?? undefined)
  : undefined

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

  emit('submit', data)
}

function cancelForm() {
  emit('cancel', formModel.value)
}
</script>
<style scoped></style>
