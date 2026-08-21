<template>
  <div class="w-full">
    <!-- Header -->
    <div class="flex items-center justify-between mb-6">
      <h3 class="text-xl font-semibold text-base-content">
        {{ $t('entity.nav', { entity: 'Album' }) }}
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
    <AlbumDetails v-if="entity" :album="entity" />

    <div class="tabs tabs-border">
      <input
        type="radio"
        name="details_tab"
        class="tab"
        :aria-label="$t('entities.song.nav.title')"
        checked
      />
      <div class="tab-content card bg-base-100 border border-base-300">
        <div class="card-body">
          <div class="mt-4">
            <EntityTable
              :entity-model="songEntity"
              :entity-search-fields="songSearchFields"
              :default-active-filters="['albumidAlbums']"
              :searchFn="getSongsData"
              :getPaginationData="songGetPaginationData"
              :listComponent="SongList"
              :entities="songs"
              :message="songMessage"
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
import AlbumDetails from '@/entities/album/AlbumDetails.vue'
import { useAlbums } from '@/composables/useAlbums'
import { Album } from '@/models/AlbumModel'
import GenesisButton from '@/components/button/GenesisButton.vue'
import LeftArrowIcon from '@/components/icons/LeftArrowIcon.vue'
import EntityTable from '@/components/table/EntityTable.vue'
import type { EntitySearchField } from '@/models/EntityModel'
import type { PaginationRequestParameter, SortFieldParameter } from '@/models/api/RequestModel'
import { useSongs } from '@/composables/useSongs'
import { Song } from '@/models/SongModel'
import SongList from '@/entities/song/SongList.vue'

const route = useRoute()
const pathId = Number(route.params.id)
const { getAlbumById, goToListView } = useAlbums()
const entity = ref<Album | null>(null)

const {
  songs,
  getAllSongsByAlbumId,
  message: songMessage,
  getPaginationData: songGetPaginationData,
} = useSongs()
const getSongsData = async (
  unpagined: boolean,
  filters: Record<string, unknown>,
  pagination: PaginationRequestParameter,
  sortFields: SortFieldParameter[],
) => {
  return await getAllSongsByAlbumId(pathId, unpagined, pagination, sortFields)
}
const songSearchFields = ref<EntitySearchField[]>(Song.getAllSearchFieldsMetadata())
const songEntity = ref<Record<string, unknown>>({ albumidAlbums: pathId })

onMounted(async () => {
  const result = await getAlbumById(pathId)
  entity.value = result.data || null
})
</script>
<style scoped></style>
