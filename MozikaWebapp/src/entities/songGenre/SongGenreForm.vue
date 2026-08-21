<template>
  <div class="card bg-base-100">
    <div class="card-body">
      <form @submit.prevent="handleSubmit" class="grid grid-cols-1 w-7/12 gap-4 mx-auto">
        <!-- <form @submit.prevent="handleSubmit" class=""> -->
        <div class="grid grid-cols-1 gap-4">
          <!-- Songid songs FK -->
          <GenesisSelectSearchCriteria
            v-if="songidSongsSearchField?.multicriteriaSelect"
            label="Songid songs"
            key="songGenreSongidSongs"
            :search-function="songidSongsSearchField?.multicriteriaSelect.searchFunction"
            :filters="songidSongsSearchField?.multicriteriaSelect.filters"
            :default-value="songidSongsDefaultValue"
            :violation="violations ? violations['songidSongs'] : undefined"
            @option-selected="(selectedValue) => (formModel.songidSongs = String(selectedValue))"
          />

          <!-- Genreid genres FK -->
          <GenesisSelectSearchCriteria
            v-if="genreidGenresSearchField?.multicriteriaSelect"
            label="Genreid genres"
            key="songGenreGenreidGenres"
            :search-function="genreidGenresSearchField?.multicriteriaSelect.searchFunction"
            :filters="genreidGenresSearchField?.multicriteriaSelect.filters"
            :default-value="genreidGenresDefaultValue"
            :violation="violations ? violations['genreidGenres'] : undefined"
            @option-selected="(selectedValue) => (formModel.genreidGenres = String(selectedValue))"
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
import { SongGenre, SongGenreFormDTO } from '@/models/SongGenreModel.ts'
import GenesisButton from '@/components/button/GenesisButton.vue'
import GenesisInput from '@/components/form/GenesisInput.vue'
import GenesisSelectSearchCriteria from '@/components/form/GenesisSelectSearchCriteria.vue'

const props = defineProps<{
  songGenre?: SongGenre
  violations?: Record<string, string> | null
  submitLabel?: string
}>()

const emit = defineEmits<{
  (e: 'submit', payload: Partial<SongGenreFormDTO>): void
  (e: 'cancel', payload: Partial<SongGenreFormDTO>): void
}>()

const formModel = ref<Partial<SongGenreFormDTO>>({ ...SongGenreFormDTO.parse(props.songGenre) })
const songidSongsSearchField = SongGenre.getSearchFieldByKey('songidSongs')
const songidSongsDefaultValue = props.songGenre
  ? (props.songGenre?.songidSongs?.getKeyValue?.() ?? undefined)
  : undefined
const genreidGenresSearchField = SongGenre.getSearchFieldByKey('genreidGenres')
const genreidGenresDefaultValue = props.songGenre
  ? (props.songGenre?.genreidGenres?.getKeyValue?.() ?? undefined)
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
