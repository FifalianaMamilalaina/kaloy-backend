<template>
  <div class="card bg-base-100">
    <div class="card-body">
      <form @submit.prevent="handleSubmit" class="grid grid-cols-1 w-7/12 gap-4 mx-auto">
        <!-- <form @submit.prevent="handleSubmit" class=""> -->
        <div class="grid grid-cols-1 gap-4">
          <!-- Name -->
          <GenesisInput
            label="Name"
            :violation="genreViolations ? genreViolations['name'] : undefined"
            placeholder="Enter Name"
            type="text"
            v-model="formModel.name"
            :value="formModel.name"
          />
        </div>
        <SongGenreTableForm
          :initial-data="songGenresData"
          :get-table-data-callback="setSongGenres"
          :violations="songGenreViolations"
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
import { Genre, GenreFormDTO } from '@/models/GenreModel.ts'
import GenesisButton from '@/components/button/GenesisButton.vue'
import GenesisInput from '@/components/form/GenesisInput.vue'
import { SongGenreFormDTO, type SongGenre } from '@/models/SongGenreModel.ts'
import SongGenreTableForm from '@/entities/songGenre/SongGenreTableForm.vue'

const props = defineProps<{
  genre?: Genre

  songGenresData?: SongGenre[]

  violations?: {
    genre: Record<string, string>
    songGenres: Record<string, string>[]
  }

  submitLabel?: string
}>()

const emit = defineEmits<{
  (e: 'submit', payload: { genre: Partial<GenreFormDTO>; songGenres: SongGenreFormDTO[] }): void
  (e: 'cancel', payload: Partial<GenreFormDTO>): void
}>()

const genreViolations = computed(() => (props.violations ? props.violations.genre : {}))
const songGenreViolations = computed(() => (props.violations ? props.violations.songGenres : []))

const formModel = ref<Partial<GenreFormDTO>>({ ...GenreFormDTO.parse(props.genre) })

const songGenres = ref<SongGenreFormDTO[]>([])
const setSongGenres = (data: SongGenreFormDTO[]) => {
  songGenres.value = data
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

  emit('submit', { genre: data, songGenres: songGenres.value })
}

function cancelForm() {
  emit('cancel', formModel.value)
}
onMounted(async () => {
  if (Array.isArray(props.songGenresData) && props.songGenresData.length > 0) {
    setSongGenres(SongGenreFormDTO.parseList(props.songGenresData))
  }
})
</script>
<style scoped></style>
