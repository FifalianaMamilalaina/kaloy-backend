<template>
  <div class="card bg-base-100">
    <div class="card-body">
      <form @submit.prevent="handleSubmit" class="grid grid-cols-1 w-7/12 gap-4 mx-auto">
        <!-- <form @submit.prevent="handleSubmit" class=""> -->
        <div class="grid grid-cols-1 gap-4">
          <!-- Editorialplaylistid editorial playlists FK -->
          <GenesisSelectSearchCriteria
            v-if="editorialplaylistidEditorialPlaylistsSearchField?.multicriteriaSelect"
            label="Editorialplaylistid editorial playlists"
            key="editorialPlaylistSongEditorialplaylistidEditorialPlaylists"
            :search-function="
              editorialplaylistidEditorialPlaylistsSearchField?.multicriteriaSelect.searchFunction
            "
            :filters="editorialplaylistidEditorialPlaylistsSearchField?.multicriteriaSelect.filters"
            :default-value="editorialplaylistidEditorialPlaylistsDefaultValue"
            :violation="
              violations ? violations['editorialplaylistidEditorialPlaylists'] : undefined
            "
            @option-selected="
              (selectedValue) =>
                (formModel.editorialplaylistidEditorialPlaylists = String(selectedValue))
            "
          />

          <!-- Songid songs FK -->
          <GenesisSelectSearchCriteria
            v-if="songidSongsSearchField?.multicriteriaSelect"
            label="Songid songs"
            key="editorialPlaylistSongSongidSongs"
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
import {
  EditorialPlaylistSong,
  EditorialPlaylistSongFormDTO,
} from '@/models/EditorialPlaylistSongModel.ts'
import GenesisButton from '@/components/button/GenesisButton.vue'
import GenesisInput from '@/components/form/GenesisInput.vue'
import GenesisSelectSearchCriteria from '@/components/form/GenesisSelectSearchCriteria.vue'

const props = defineProps<{
  editorialPlaylistSong?: EditorialPlaylistSong
  violations?: Record<string, string> | null
  submitLabel?: string
}>()

const emit = defineEmits<{
  (e: 'submit', payload: Partial<EditorialPlaylistSongFormDTO>): void
  (e: 'cancel', payload: Partial<EditorialPlaylistSongFormDTO>): void
}>()

const formModel = ref<Partial<EditorialPlaylistSongFormDTO>>({
  ...EditorialPlaylistSongFormDTO.parse(props.editorialPlaylistSong),
})
const editorialplaylistidEditorialPlaylistsSearchField = EditorialPlaylistSong.getSearchFieldByKey(
  'editorialplaylistidEditorialPlaylists',
)
const editorialplaylistidEditorialPlaylistsDefaultValue = props.editorialPlaylistSong
  ? (props.editorialPlaylistSong?.editorialplaylistidEditorialPlaylists?.getKeyValue?.() ??
    undefined)
  : undefined
const songidSongsSearchField = EditorialPlaylistSong.getSearchFieldByKey('songidSongs')
const songidSongsDefaultValue = props.editorialPlaylistSong
  ? (props.editorialPlaylistSong?.songidSongs?.getKeyValue?.() ?? undefined)
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
