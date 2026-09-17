<template>
  <div class="w-full">
    <!-- Header -->
    <div class="flex items-center justify-between mb-6">
      <h3 class="text-xl font-semibold text-base-content">
        Genre /
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
    <div v-if="entity && songGenreLoaded">
      <genre-form
        :genre="entity"
        :songGenresData="songGenres"
        :violations="violations"
        :submit-label="$t('entity.update.submitLabel', { entity: 'Genre' })"
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
import GenreForm from '@/entities/genre/GenreForm.vue'
import AlertPopup from '@/components/popup/AlertPopup.vue'
import { useGenres } from '@/composables/useGenres'
import { Genre, GenreFormDTO } from '@/models/GenreModel'
import { usePopup } from '@/composables/usePopup'
import GenesisButton from '@/components/button/GenesisButton.vue'
import LeftArrowIcon from '@/components/icons/LeftArrowIcon.vue'
import { useFreezeScreenStore } from '@/stores/useFreezeScreenStore.ts'
import { useSongGenres } from '@/composables/useSongGenres'
import { SongGenreFormDTO } from '@/models/SongGenreModel'

import { PaginationRequestParameter } from '@/models/api/RequestModel'

const route = useRoute()
const pathId = Number(route.params.id)
const { closePopup, openPopup, visible: alertPopup } = usePopup()
const { getGenreById, goToListView, updateGenre, viewGenre, message } = useGenres()
const entity = ref<Genre | null>(null)
const freezeStore = useFreezeScreenStore()

const { songGenres, getAllSongGenresByGenreId } = useSongGenres()
const songGenreLoaded = ref(false)
const loadSongGenres = async () => {
  try {
    await getAllSongGenresByGenreId(pathId, true, new PaginationRequestParameter(), [])
    songGenreLoaded.value = true
  } catch (error) {
    console.error('Error loading genre with details:', error)
    songGenreLoaded.value = true // Continuer même en cas d'erreur
  }
}

const violations = ref<
  | {
      genre: Record<string, string>
      songGenres: Record<string, string>[]
    }
  | undefined
>(undefined)

const updateHandler = async (payload: {
  genre: Partial<GenreFormDTO>
  songGenres: SongGenreFormDTO[]
}) => {
  freezeStore.freeze('Updating genre ' + pathId + ' ...')
  message.value = null
  violations.value = {
    genre: {},
    songGenres: [],
  }
  try {
    const { data, errors } = await updateGenre(pathId, payload)

    if (errors && Object.keys(errors).length > 0) {
      // Gérer les erreurs de validation
      violations.value = errors as {
        genre: Record<string, string>
        songGenres: Record<string, string>[]
      }
    }
    if (data && !message.value) {
      viewGenre(data)
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
    viewGenre(entity.value)
  }
}

onMounted(async () => {
  const result = await getGenreById(pathId)
  if (result.data) {
    entity.value = result.data
    await loadSongGenres()
  } else {
    openPopup()
  }
})
</script>
<style scoped></style>
