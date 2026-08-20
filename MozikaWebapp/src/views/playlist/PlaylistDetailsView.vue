<template>
  <div class="w-full">
    <!-- Header -->
    <div class="flex items-center justify-between mb-6">
      <h3 class="text-xl font-semibold text-base-content">
        {{ $t('entity.nav', { entity: 'Playlist' }) }}
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
    <PlaylistDetails v-if="entity" :playlist="entity" />

    <div class="tabs tabs-border">
      <input
        type="radio"
        name="details_tab"
        class="tab"
        :aria-label="$t('entities.download.nav.title')"
        checked
      />
      <div class="tab-content card bg-base-100 border border-base-300">
        <div class="card-body">
          <div class="mt-4">
            <EntityTable
              :entity-model="downloadEntity"
              :entity-search-fields="downloadSearchFields"
              :default-active-filters="['playlistidPlaylists']"
              :searchFn="getDownloadsData"
              :getPaginationData="downloadGetPaginationData"
              :listComponent="DownloadList"
              :entities="downloads"
              :message="downloadMessage"
              :show-filters="false"
              :edit-action="false"
              :remove-action="false"
            />
          </div>
        </div>
      </div>
    </div>
    <div class="tabs tabs-border">
      <input
        type="radio"
        name="details_tab"
        class="tab"
        :aria-label="$t('entities.playlistSong.nav.title')"
        checked
      />
      <div class="tab-content card bg-base-100 border border-base-300">
        <div class="card-body">
          <div class="mt-4">
            <EntityTable
              :entity-model="playlistSongEntity"
              :entity-search-fields="playlistSongSearchFields"
              :default-active-filters="['playlistidPlaylists']"
              :searchFn="getPlaylistSongsData"
              :getPaginationData="playlistSongGetPaginationData"
              :listComponent="PlaylistSongList"
              :entities="playlistSongs"
              :message="playlistSongMessage"
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
import PlaylistDetails from '@/entities/playlist/PlaylistDetails.vue'
import { usePlaylists } from '@/composables/usePlaylists'
import { Playlist } from '@/models/PlaylistModel'
import GenesisButton from '@/components/button/GenesisButton.vue'
import LeftArrowIcon from '@/components/icons/LeftArrowIcon.vue'
import EntityTable from '@/components/table/EntityTable.vue'
import type { EntitySearchField } from '@/models/EntityModel'
import type { PaginationRequestParameter, SortFieldParameter } from '@/models/api/RequestModel'
import { useDownloads } from '@/composables/useDownloads'
import { Download } from '@/models/DownloadModel'
import DownloadList from '@/entities/download/DownloadList.vue'
import { usePlaylistSongs } from '@/composables/usePlaylistSongs'
import { PlaylistSong } from '@/models/PlaylistSongModel'
import PlaylistSongList from '@/entities/playlistSong/PlaylistSongList.vue'

const route = useRoute()
const pathId = Number(route.params.id)
const { getPlaylistById, goToListView } = usePlaylists()
const entity = ref<Playlist | null>(null)

const {
  downloads,
  getAllDownloadsByPlaylistId,
  message: downloadMessage,
  getPaginationData: downloadGetPaginationData,
} = useDownloads()
const getDownloadsData = async (
  unpagined: boolean,
  filters: Record<string, unknown>,
  pagination: PaginationRequestParameter,
  sortFields: SortFieldParameter[],
) => {
  return await getAllDownloadsByPlaylistId(pathId, unpagined, pagination, sortFields)
}
const downloadSearchFields = ref<EntitySearchField[]>(Download.getAllSearchFieldsMetadata())
const downloadEntity = ref<Record<string, unknown>>({ playlistidPlaylists: pathId })
const {
  playlistSongs,
  getAllPlaylistSongsByPlaylistId,
  message: playlistSongMessage,
  getPaginationData: playlistSongGetPaginationData,
} = usePlaylistSongs()
const getPlaylistSongsData = async (
  unpagined: boolean,
  filters: Record<string, unknown>,
  pagination: PaginationRequestParameter,
  sortFields: SortFieldParameter[],
) => {
  return await getAllPlaylistSongsByPlaylistId(pathId, unpagined, pagination, sortFields)
}
const playlistSongSearchFields = ref<EntitySearchField[]>(PlaylistSong.getAllSearchFieldsMetadata())
const playlistSongEntity = ref<Record<string, unknown>>({ playlistidPlaylists: pathId })

onMounted(async () => {
  const result = await getPlaylistById(pathId)
  entity.value = result.data || null
})
</script>
<style scoped></style>
