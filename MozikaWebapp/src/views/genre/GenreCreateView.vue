<template>
  <div class="w-full">
    <div class="flex items-center justify-between mb-6">
      <h3 class="text-xl font-semibold text-base-content">
        {{ $t('entity.nav', { entity: 'Genre' }) }}
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

    <genre-form
      :submit-label="$t('entity.create.submitLabel', { entity: 'Genre' })"
      :songGenresData="initialSongGenres"
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
import GenreForm from '@/entities/genre/GenreForm.vue'
import AlertPopup from '@/components/popup/AlertPopup.vue'
import GenesisButton from '@/components/button/GenesisButton.vue'
import LeftArrowIcon from '@/components/icons/LeftArrowIcon.vue'
import { GenreFormDTO } from '@/models/GenreModel'
import { useGenres } from '@/composables/useGenres'
import { usePopup } from '@/composables/usePopup'
import { useFreezeScreenStore } from '@/stores/useFreezeScreenStore'
import { ref } from 'vue'
import type { SongGenre, SongGenreFormDTO } from '@/models/SongGenreModel'

const { createGenre, goToListView, message, viewGenre } = useGenres()
const { openPopup, closePopup, visible: alertPopup } = usePopup()
const freezeStore = useFreezeScreenStore()
const violations = ref<
  | {
      genre: Record<string, string>
      songGenres: Record<string, string>[]
    }
  | undefined
>(undefined)
const initialSongGenres = ref<SongGenre[]>([])

const createHandler = async (payload: {
  genre: Partial<GenreFormDTO>
  songGenres: SongGenreFormDTO[]
}) => {
  freezeStore.freeze('Creating a new Genre ...')
  violations.value = undefined
  message.value = null
  try {
    const { data, errors } = await createGenre(payload)
    if (errors && Object.keys(errors).length > 0) {
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
    console.error('Error creating genre:', error)
    openPopup()
  } finally {
    freezeStore.unfreeze()
  }
}
</script>
<style scoped></style>
