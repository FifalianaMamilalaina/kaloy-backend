<template>
  <div class="w-full">
    <!-- Header -->
    <div class="flex items-center justify-between mb-6">
      <h3 class="text-xl font-semibold text-base-content">
        {{ $t('entity.nav', { entity: 'Song' }) }}
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
    <SongDetails v-if="entity" :song="entity" />

    <div class="tabs tabs-border">
      <input
        type="radio"
        name="details_tab"
        class="tab"
        :aria-label="$t('entities.editorialPlaylistSong.nav.title')"
        checked
      />
      <div class="tab-content card bg-base-100 border border-base-300">
        <div class="card-body">
          <div class="mt-4">
            <EntityTable
              :entity-model="editorialPlaylistSongEntity"
              :entity-search-fields="editorialPlaylistSongSearchFields"
              :default-active-filters="['songidSongs']"
              :searchFn="getEditorialPlaylistSongsData"
              :getPaginationData="editorialPlaylistSongGetPaginationData"
              :listComponent="EditorialPlaylistSongList"
              :entities="editorialPlaylistSongs"
              :message="editorialPlaylistSongMessage"
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
        :aria-label="$t('entities.listeningHistory.nav.title')"
        checked
      />
      <div class="tab-content card bg-base-100 border border-base-300">
        <div class="card-body">
          <div class="mt-4">
            <EntityTable
              :entity-model="listeningHistoryEntity"
              :entity-search-fields="listeningHistorySearchFields"
              :default-active-filters="['songidSongs']"
              :searchFn="getListeningHistorysData"
              :getPaginationData="listeningHistoryGetPaginationData"
              :listComponent="ListeningHistoryList"
              :entities="listeningHistorys"
              :message="listeningHistoryMessage"
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
              :default-active-filters="['songidSongs']"
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
              :default-active-filters="['songidSongs']"
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
    <div class="tabs tabs-border">
      <input
        type="radio"
        name="details_tab"
        class="tab"
        :aria-label="$t('entities.upNextQueue.nav.title')"
        checked
      />
      <div class="tab-content card bg-base-100 border border-base-300">
        <div class="card-body">
          <div class="mt-4">
            <EntityTable
              :entity-model="upNextQueueEntity"
              :entity-search-fields="upNextQueueSearchFields"
              :default-active-filters="['songidSongs']"
              :searchFn="getUpNextQueuesData"
              :getPaginationData="upNextQueueGetPaginationData"
              :listComponent="UpNextQueueList"
              :entities="upNextQueues"
              :message="upNextQueueMessage"
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
import SongDetails from '@/entities/song/SongDetails.vue'
import { useSongs } from '@/composables/useSongs'
import { Song } from '@/models/SongModel'
import GenesisButton from '@/components/button/GenesisButton.vue'
import LeftArrowIcon from '@/components/icons/LeftArrowIcon.vue'
import EntityTable from '@/components/table/EntityTable.vue'
import type { EntitySearchField } from '@/models/EntityModel'
import type { PaginationRequestParameter, SortFieldParameter } from '@/models/api/RequestModel'
import { useEditorialPlaylistSongs } from '@/composables/useEditorialPlaylistSongs'
import { EditorialPlaylistSong } from '@/models/EditorialPlaylistSongModel'
import EditorialPlaylistSongList from '@/entities/editorialPlaylistSong/EditorialPlaylistSongList.vue'
import { useListeningHistorys } from '@/composables/useListeningHistorys'
import { ListeningHistory } from '@/models/ListeningHistoryModel'
import ListeningHistoryList from '@/entities/listeningHistory/ListeningHistoryList.vue'
import { usePlaylistSongs } from '@/composables/usePlaylistSongs'
import { PlaylistSong } from '@/models/PlaylistSongModel'
import PlaylistSongList from '@/entities/playlistSong/PlaylistSongList.vue'
import { useSongGenres } from '@/composables/useSongGenres'
import { SongGenre } from '@/models/SongGenreModel'
import SongGenreList from '@/entities/songGenre/SongGenreList.vue'
import { useUpNextQueues } from '@/composables/useUpNextQueues'
import { UpNextQueue } from '@/models/UpNextQueueModel'
import UpNextQueueList from '@/entities/upNextQueue/UpNextQueueList.vue'

const route = useRoute()
const pathId = Number(route.params.id)
const { getSongById, goToListView } = useSongs()
const entity = ref<Song | null>(null)

const {
  editorialPlaylistSongs,
  getAllEditorialPlaylistSongsBySongId,
  message: editorialPlaylistSongMessage,
  getPaginationData: editorialPlaylistSongGetPaginationData,
} = useEditorialPlaylistSongs()
const getEditorialPlaylistSongsData = async (
  unpagined: boolean,
  filters: Record<string, unknown>,
  pagination: PaginationRequestParameter,
  sortFields: SortFieldParameter[],
) => {
  return await getAllEditorialPlaylistSongsBySongId(pathId, unpagined, pagination, sortFields)
}
const editorialPlaylistSongSearchFields = ref<EntitySearchField[]>(
  EditorialPlaylistSong.getAllSearchFieldsMetadata(),
)
const editorialPlaylistSongEntity = ref<Record<string, unknown>>({ songidSongs: pathId })
const {
  listeningHistorys,
  getAllListeningHistorysBySongId,
  message: listeningHistoryMessage,
  getPaginationData: listeningHistoryGetPaginationData,
} = useListeningHistorys()
const getListeningHistorysData = async (
  unpagined: boolean,
  filters: Record<string, unknown>,
  pagination: PaginationRequestParameter,
  sortFields: SortFieldParameter[],
) => {
  return await getAllListeningHistorysBySongId(pathId, unpagined, pagination, sortFields)
}
const listeningHistorySearchFields = ref<EntitySearchField[]>(
  ListeningHistory.getAllSearchFieldsMetadata(),
)
const listeningHistoryEntity = ref<Record<string, unknown>>({ songidSongs: pathId })
const {
  playlistSongs,
  getAllPlaylistSongsBySongId,
  message: playlistSongMessage,
  getPaginationData: playlistSongGetPaginationData,
} = usePlaylistSongs()
const getPlaylistSongsData = async (
  unpagined: boolean,
  filters: Record<string, unknown>,
  pagination: PaginationRequestParameter,
  sortFields: SortFieldParameter[],
) => {
  return await getAllPlaylistSongsBySongId(pathId, unpagined, pagination, sortFields)
}
const playlistSongSearchFields = ref<EntitySearchField[]>(PlaylistSong.getAllSearchFieldsMetadata())
const playlistSongEntity = ref<Record<string, unknown>>({ songidSongs: pathId })
const {
  songGenres,
  getAllSongGenresBySongId,
  message: songGenreMessage,
  getPaginationData: songGenreGetPaginationData,
} = useSongGenres()
const getSongGenresData = async (
  unpagined: boolean,
  filters: Record<string, unknown>,
  pagination: PaginationRequestParameter,
  sortFields: SortFieldParameter[],
) => {
  return await getAllSongGenresBySongId(pathId, unpagined, pagination, sortFields)
}
const songGenreSearchFields = ref<EntitySearchField[]>(SongGenre.getAllSearchFieldsMetadata())
const songGenreEntity = ref<Record<string, unknown>>({ songidSongs: pathId })
const {
  upNextQueues,
  getAllUpNextQueuesBySongId,
  message: upNextQueueMessage,
  getPaginationData: upNextQueueGetPaginationData,
} = useUpNextQueues()
const getUpNextQueuesData = async (
  unpagined: boolean,
  filters: Record<string, unknown>,
  pagination: PaginationRequestParameter,
  sortFields: SortFieldParameter[],
) => {
  return await getAllUpNextQueuesBySongId(pathId, unpagined, pagination, sortFields)
}
const upNextQueueSearchFields = ref<EntitySearchField[]>(UpNextQueue.getAllSearchFieldsMetadata())
const upNextQueueEntity = ref<Record<string, unknown>>({ songidSongs: pathId })

onMounted(async () => {
  const result = await getSongById(pathId)
  entity.value = result.data || null
})
</script>
<style scoped></style>
