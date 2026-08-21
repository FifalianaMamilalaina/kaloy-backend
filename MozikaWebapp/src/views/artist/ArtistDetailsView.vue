<template>
  <div class="w-full">
    <!-- Header -->
    <div class="flex items-center justify-between mb-6">
      <h3 class="text-xl font-semibold text-base-content">
        {{ $t('entity.nav', { entity: 'Artist' }) }}
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
    <ArtistDetails v-if="entity" :artist="entity" />

    <div class="tabs tabs-border">
      <input
        type="radio"
        name="details_tab"
        class="tab"
        :aria-label="$t('entities.album.nav.title')"
        checked
      />
      <div class="tab-content card bg-base-100 border border-base-300">
        <div class="card-body">
          <div class="mt-4">
            <EntityTable
              :entity-model="albumEntity"
              :entity-search-fields="albumSearchFields"
              :default-active-filters="['artistidArtists']"
              :searchFn="getAlbumsData"
              :getPaginationData="albumGetPaginationData"
              :listComponent="AlbumList"
              :entities="albums"
              :message="albumMessage"
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
        :aria-label="$t('entities.artistGroupMember.nav.title')"
        checked
      />
      <div class="tab-content card bg-base-100 border border-base-300">
        <div class="card-body">
          <div class="mt-4">
            <EntityTable
              :entity-model="artistGroupMemberEntity"
              :entity-search-fields="artistGroupMemberSearchFields"
              :default-active-filters="['groupartistidArtists']"
              :searchFn="getArtistGroupMembersData"
              :getPaginationData="artistGroupMemberGetPaginationData"
              :listComponent="ArtistGroupMemberList"
              :entities="artistGroupMembers"
              :message="artistGroupMemberMessage"
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
        :aria-label="$t('entities.concert.nav.title')"
        checked
      />
      <div class="tab-content card bg-base-100 border border-base-300">
        <div class="card-body">
          <div class="mt-4">
            <EntityTable
              :entity-model="concertEntity"
              :entity-search-fields="concertSearchFields"
              :default-active-filters="['artistidArtists']"
              :searchFn="getConcertsData"
              :getPaginationData="concertGetPaginationData"
              :listComponent="ConcertList"
              :entities="concerts"
              :message="concertMessage"
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
        :aria-label="$t('entities.contentSubmission.nav.title')"
        checked
      />
      <div class="tab-content card bg-base-100 border border-base-300">
        <div class="card-body">
          <div class="mt-4">
            <EntityTable
              :entity-model="contentSubmissionEntity"
              :entity-search-fields="contentSubmissionSearchFields"
              :default-active-filters="['artistidArtists']"
              :searchFn="getContentSubmissionsData"
              :getPaginationData="contentSubmissionGetPaginationData"
              :listComponent="ContentSubmissionList"
              :entities="contentSubmissions"
              :message="contentSubmissionMessage"
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
        :aria-label="$t('entities.editorialPlaylist.nav.title')"
        checked
      />
      <div class="tab-content card bg-base-100 border border-base-300">
        <div class="card-body">
          <div class="mt-4">
            <EntityTable
              :entity-model="editorialPlaylistEntity"
              :entity-search-fields="editorialPlaylistSearchFields"
              :default-active-filters="['artistidArtists']"
              :searchFn="getEditorialPlaylistsData"
              :getPaginationData="editorialPlaylistGetPaginationData"
              :listComponent="EditorialPlaylistList"
              :entities="editorialPlaylists"
              :message="editorialPlaylistMessage"
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
        :aria-label="$t('entities.event.nav.title')"
        checked
      />
      <div class="tab-content card bg-base-100 border border-base-300">
        <div class="card-body">
          <div class="mt-4">
            <EntityTable
              :entity-model="eventEntity"
              :entity-search-fields="eventSearchFields"
              :default-active-filters="['createdbyartistidArtists']"
              :searchFn="getEventsData"
              :getPaginationData="eventGetPaginationData"
              :listComponent="EventList"
              :entities="events"
              :message="eventMessage"
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
        :aria-label="$t('entities.follow.nav.title')"
        checked
      />
      <div class="tab-content card bg-base-100 border border-base-300">
        <div class="card-body">
          <div class="mt-4">
            <EntityTable
              :entity-model="followEntity"
              :entity-search-fields="followSearchFields"
              :default-active-filters="['artistidArtists']"
              :searchFn="getFollowsData"
              :getPaginationData="followGetPaginationData"
              :listComponent="FollowList"
              :entities="follows"
              :message="followMessage"
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
        :aria-label="$t('entities.song.nav.title')"
        checked
      />
      <div class="tab-content card bg-base-100 border border-base-300">
        <div class="card-body">
          <div class="mt-4">
            <EntityTable
              :entity-model="songEntity"
              :entity-search-fields="songSearchFields"
              :default-active-filters="['artistidArtists']"
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
import ArtistDetails from '@/entities/artist/ArtistDetails.vue'
import { useArtists } from '@/composables/useArtists'
import { Artist } from '@/models/ArtistModel'
import GenesisButton from '@/components/button/GenesisButton.vue'
import LeftArrowIcon from '@/components/icons/LeftArrowIcon.vue'
import EntityTable from '@/components/table/EntityTable.vue'
import type { EntitySearchField } from '@/models/EntityModel'
import type { PaginationRequestParameter, SortFieldParameter } from '@/models/api/RequestModel'
import { useAlbums } from '@/composables/useAlbums'
import { Album } from '@/models/AlbumModel'
import AlbumList from '@/entities/album/AlbumList.vue'
import { useArtistGroupMembers } from '@/composables/useArtistGroupMembers'
import { ArtistGroupMember } from '@/models/ArtistGroupMemberModel'
import ArtistGroupMemberList from '@/entities/artistGroupMember/ArtistGroupMemberList.vue'
import { useConcerts } from '@/composables/useConcerts'
import { Concert } from '@/models/ConcertModel'
import ConcertList from '@/entities/concert/ConcertList.vue'
import { useContentSubmissions } from '@/composables/useContentSubmissions'
import { ContentSubmission } from '@/models/ContentSubmissionModel'
import ContentSubmissionList from '@/entities/contentSubmission/ContentSubmissionList.vue'
import { useEditorialPlaylists } from '@/composables/useEditorialPlaylists'
import { EditorialPlaylist } from '@/models/EditorialPlaylistModel'
import EditorialPlaylistList from '@/entities/editorialPlaylist/EditorialPlaylistList.vue'
import { useEvents } from '@/composables/useEvents'
import { Event } from '@/models/EventModel'
import EventList from '@/entities/event/EventList.vue'
import { useFollows } from '@/composables/useFollows'
import { Follow } from '@/models/FollowModel'
import FollowList from '@/entities/follow/FollowList.vue'
import { useSongs } from '@/composables/useSongs'
import { Song } from '@/models/SongModel'
import SongList from '@/entities/song/SongList.vue'

const route = useRoute()
const pathId = Number(route.params.id)
const { getArtistById, goToListView } = useArtists()
const entity = ref<Artist | null>(null)

const {
  albums,
  getAllAlbumsByArtistId,
  message: albumMessage,
  getPaginationData: albumGetPaginationData,
} = useAlbums()
const getAlbumsData = async (
  unpagined: boolean,
  filters: Record<string, unknown>,
  pagination: PaginationRequestParameter,
  sortFields: SortFieldParameter[],
) => {
  return await getAllAlbumsByArtistId(pathId, unpagined, pagination, sortFields)
}
const albumSearchFields = ref<EntitySearchField[]>(Album.getAllSearchFieldsMetadata())
const albumEntity = ref<Record<string, unknown>>({ artistidArtists: pathId })
const {
  artistGroupMembers,
  getAllArtistGroupMembersByArtistId,
  message: artistGroupMemberMessage,
  getPaginationData: artistGroupMemberGetPaginationData,
} = useArtistGroupMembers()
const getArtistGroupMembersData = async (
  unpagined: boolean,
  filters: Record<string, unknown>,
  pagination: PaginationRequestParameter,
  sortFields: SortFieldParameter[],
) => {
  return await getAllArtistGroupMembersByArtistId(pathId, unpagined, pagination, sortFields)
}
const artistGroupMemberSearchFields = ref<EntitySearchField[]>(
  ArtistGroupMember.getAllSearchFieldsMetadata(),
)
const artistGroupMemberEntity = ref<Record<string, unknown>>({ groupartistidArtists: pathId })
const {
  concerts,
  getAllConcertsByArtistId,
  message: concertMessage,
  getPaginationData: concertGetPaginationData,
} = useConcerts()
const getConcertsData = async (
  unpagined: boolean,
  filters: Record<string, unknown>,
  pagination: PaginationRequestParameter,
  sortFields: SortFieldParameter[],
) => {
  return await getAllConcertsByArtistId(pathId, unpagined, pagination, sortFields)
}
const concertSearchFields = ref<EntitySearchField[]>(Concert.getAllSearchFieldsMetadata())
const concertEntity = ref<Record<string, unknown>>({ artistidArtists: pathId })
const {
  contentSubmissions,
  getAllContentSubmissionsByArtistId,
  message: contentSubmissionMessage,
  getPaginationData: contentSubmissionGetPaginationData,
} = useContentSubmissions()
const getContentSubmissionsData = async (
  unpagined: boolean,
  filters: Record<string, unknown>,
  pagination: PaginationRequestParameter,
  sortFields: SortFieldParameter[],
) => {
  return await getAllContentSubmissionsByArtistId(pathId, unpagined, pagination, sortFields)
}
const contentSubmissionSearchFields = ref<EntitySearchField[]>(
  ContentSubmission.getAllSearchFieldsMetadata(),
)
const contentSubmissionEntity = ref<Record<string, unknown>>({ artistidArtists: pathId })
const {
  editorialPlaylists,
  getAllEditorialPlaylistsByArtistId,
  message: editorialPlaylistMessage,
  getPaginationData: editorialPlaylistGetPaginationData,
} = useEditorialPlaylists()
const getEditorialPlaylistsData = async (
  unpagined: boolean,
  filters: Record<string, unknown>,
  pagination: PaginationRequestParameter,
  sortFields: SortFieldParameter[],
) => {
  return await getAllEditorialPlaylistsByArtistId(pathId, unpagined, pagination, sortFields)
}
const editorialPlaylistSearchFields = ref<EntitySearchField[]>(
  EditorialPlaylist.getAllSearchFieldsMetadata(),
)
const editorialPlaylistEntity = ref<Record<string, unknown>>({ artistidArtists: pathId })
const {
  events,
  getAllEventsByArtistId,
  message: eventMessage,
  getPaginationData: eventGetPaginationData,
} = useEvents()
const getEventsData = async (
  unpagined: boolean,
  filters: Record<string, unknown>,
  pagination: PaginationRequestParameter,
  sortFields: SortFieldParameter[],
) => {
  return await getAllEventsByArtistId(pathId, unpagined, pagination, sortFields)
}
const eventSearchFields = ref<EntitySearchField[]>(Event.getAllSearchFieldsMetadata())
const eventEntity = ref<Record<string, unknown>>({ createdbyartistidArtists: pathId })
const {
  follows,
  getAllFollowsByArtistId,
  message: followMessage,
  getPaginationData: followGetPaginationData,
} = useFollows()
const getFollowsData = async (
  unpagined: boolean,
  filters: Record<string, unknown>,
  pagination: PaginationRequestParameter,
  sortFields: SortFieldParameter[],
) => {
  return await getAllFollowsByArtistId(pathId, unpagined, pagination, sortFields)
}
const followSearchFields = ref<EntitySearchField[]>(Follow.getAllSearchFieldsMetadata())
const followEntity = ref<Record<string, unknown>>({ artistidArtists: pathId })
const {
  songs,
  getAllSongsByArtistId,
  message: songMessage,
  getPaginationData: songGetPaginationData,
} = useSongs()
const getSongsData = async (
  unpagined: boolean,
  filters: Record<string, unknown>,
  pagination: PaginationRequestParameter,
  sortFields: SortFieldParameter[],
) => {
  return await getAllSongsByArtistId(pathId, unpagined, pagination, sortFields)
}
const songSearchFields = ref<EntitySearchField[]>(Song.getAllSearchFieldsMetadata())
const songEntity = ref<Record<string, unknown>>({ artistidArtists: pathId })

onMounted(async () => {
  const result = await getArtistById(pathId)
  entity.value = result.data || null
})
</script>
<style scoped></style>
