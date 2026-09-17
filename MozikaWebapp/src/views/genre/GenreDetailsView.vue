<template>
  <div class="w-full">
    <!-- Header -->
    <div class="flex items-center justify-between mb-6">
      <h3 class="text-xl font-semibold text-base-content">
        {{ $t('entity.nav', { entity: 'Genre' }) }}
        <span class="text-base-content/50 font-normal">{{ $t('entity.details.nav') }}</span>
      </h3>

      <!-- Back button -->
      <GenesisButton
        :title="$t('button.backToListDescription')"
        @click="goToListView"
        class="btn-secondary"
      >
        <LeftArrowIcon />
        {{ $t('button.backToList') }}
      </GenesisButton>
    </div>
    <!-- Entity details -->
    <GenreDetails v-if="entity" :genre="entity" />

    <div class="tabs tabs-border">
      <input
        type="radio"
        name="details_tab"
        class="tab"
        :aria-label="$t('entities.songGenre.nav.title')"
        checked
      />
      <div class="tab-content card bg-base-100 border border-base-300">
        <div class="card-body">
          <div class="mt-4">
            <EntityTable
              :entity-model="songGenreEntity"
              :entity-search-fields="songGenreSearchFields"
              :default-active-filters="['genreidGenres']"
              :searchFn="getSongGenresData"
              :getPaginationData="songGenreGetPaginationData"
              :listComponent="SongGenreList"
              :entities="songGenres"
              :message="songGenreMessage"
              :show-filters="false"
              :edit-action="false"
              :remove-action="false"
            />
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import GenreDetails from '@/entities/genre/GenreDetails.vue'
import { useGenres } from '@/composables/useGenres'
import { Genre } from '@/models/GenreModel'
import GenesisButton from '@/components/button/GenesisButton.vue'
import LeftArrowIcon from '@/components/icons/LeftArrowIcon.vue'
import EntityTable from '@/components/table/EntityTable.vue'
import type { EntitySearchField } from '@/models/EntityModel'
import type { PaginationRequestParameter, SortFieldParameter } from '@/models/api/RequestModel'
import { useSongGenres } from '@/composables/useSongGenres'
import { SongGenre } from '@/models/SongGenreModel'
import SongGenreList from '@/entities/songGenre/SongGenreList.vue'

const route = useRoute()
const pathId = Number(route.params.id)
const { getGenreById, goToListView } = useGenres()
const entity = ref<Genre | null>(null)

const {
  songGenres,
  getAllSongGenresByGenreId,
  message: songGenreMessage,
  getPaginationData: songGenreGetPaginationData,
} = useSongGenres()
const getSongGenresData = async (
  unpagined: boolean,
  filters: Record<string, unknown>,
  pagination: PaginationRequestParameter,
  sortFields: SortFieldParameter[],
) => {
  return await getAllSongGenresByGenreId(pathId, unpagined, pagination, sortFields)
}
const songGenreSearchFields = ref<EntitySearchField[]>(SongGenre.getAllSearchFieldsMetadata())
const songGenreEntity = ref<Record<string, unknown>>({ genreidGenres: pathId })

onMounted(async () => {
  const result = await getGenreById(pathId)
  entity.value = result.data || null
})
</script>
<style scoped></style>
