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
            key="albumArtistidArtists"
            :search-function="artistidArtistsSearchField?.multicriteriaSelect.searchFunction"
            :filters="artistidArtistsSearchField?.multicriteriaSelect.filters"
            :default-value="artistidArtistsDefaultValue"
            :violation="albumViolations ? albumViolations['artistidArtists'] : undefined"
            @option-selected="
              (selectedValue) => (formModel.artistidArtists = String(selectedValue))
            "
          />

          <!-- Title -->
          <GenesisInput
            label="Title"
            :violation="albumViolations ? albumViolations['title'] : undefined"
            placeholder="Enter Title"
            type="text"
            v-model="formModel.title"
            :value="formModel.title"
          />

          <!-- Cover url -->
          <GenesisInput
            label="Cover url"
            :violation="albumViolations ? albumViolations['coverUrl'] : undefined"
            placeholder="Enter Cover url"
            type="text"
            v-model="formModel.coverUrl"
            :value="formModel.coverUrl"
          />

          <!-- Release date -->
          <GenesisInput
            label="Release date"
            :violation="albumViolations ? albumViolations['releaseDate'] : undefined"
            placeholder="Enter Release date"
            type="date"
            v-model="formModel.releaseDate"
            :value="formModel.releaseDate"
          />

          <!-- Created at -->
          <GenesisInput
            label="Created at"
            :violation="albumViolations ? albumViolations['createdAt'] : undefined"
            placeholder="Enter Created at"
            type="datetime-local"
            v-model="formModel.createdAt"
            :value="formModel.createdAt"
          />
        </div>
        <SongTableForm
          :initial-data="songsData"
          :get-table-data-callback="setSongs"
          :violations="songViolations"
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
import { Album, AlbumFormDTO } from '@/models/AlbumModel.ts'
import GenesisButton from '@/components/button/GenesisButton.vue'
import GenesisInput from '@/components/form/GenesisInput.vue'
import GenesisSelectSearchCriteria from '@/components/form/GenesisSelectSearchCriteria.vue'
import { SongFormDTO, type Song } from '@/models/SongModel.ts'
import SongTableForm from '@/entities/song/SongTableForm.vue'

const props = defineProps<{
  album?: Album

  songsData?: Song[]

  violations?: {
    album: Record<string, string>
    songs: Record<string, string>[]
  }

  submitLabel?: string
}>()

const emit = defineEmits<{
  (e: 'submit', payload: { album: Partial<AlbumFormDTO>; songs: SongFormDTO[] }): void
  (e: 'cancel', payload: Partial<AlbumFormDTO>): void
}>()

const albumViolations = computed(() => (props.violations ? props.violations.album : {}))
const songViolations = computed(() => (props.violations ? props.violations.songs : []))

const formModel = ref<Partial<AlbumFormDTO>>({ ...AlbumFormDTO.parse(props.album) })
const artistidArtistsSearchField = Album.getSearchFieldByKey('artistidArtists')
const artistidArtistsDefaultValue = props.album
  ? (props.album?.artistidArtists?.getKeyValue?.() ?? undefined)
  : undefined

const songs = ref<SongFormDTO[]>([])
const setSongs = (data: SongFormDTO[]) => {
  songs.value = data
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

  emit('submit', { album: data, songs: songs.value })
}

function cancelForm() {
  emit('cancel', formModel.value)
}
onMounted(async () => {
  if (Array.isArray(props.songsData) && props.songsData.length > 0) {
    setSongs(SongFormDTO.parseList(props.songsData))
  }
})
</script>
<style scoped></style>
