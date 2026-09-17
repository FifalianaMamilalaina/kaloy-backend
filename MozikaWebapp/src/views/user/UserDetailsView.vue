<template>
  <div class="w-full">
    <!-- Header -->
    <div class="flex items-center justify-between mb-6">
      <h3 class="text-xl font-semibold text-base-content">
        {{ $t('entity.nav', { entity: 'User' }) }}
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
    <UserDetails v-if="entity" :user="entity" />

    <div class="tabs tabs-border">
      <input
        type="radio"
        name="details_tab"
        class="tab"
        :aria-label="$t('entities.artist.nav.title')"
        checked
      />
      <div class="tab-content card bg-base-100 border border-base-300">
        <div class="card-body">
          <div class="mt-4">
            <EntityTable
              :entity-model="artistEntity"
              :entity-search-fields="artistSearchFields"
              :default-active-filters="['useridUsers']"
              :searchFn="getArtistsData"
              :getPaginationData="artistGetPaginationData"
              :listComponent="ArtistList"
              :entities="artists"
              :message="artistMessage"
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
        :aria-label="$t('entities.client.nav.title')"
        checked
      />
      <div class="tab-content card bg-base-100 border border-base-300">
        <div class="card-body">
          <div class="mt-4">
            <EntityTable
              :entity-model="clientEntity"
              :entity-search-fields="clientSearchFields"
              :default-active-filters="['useridUsers']"
              :searchFn="getClientsData"
              :getPaginationData="clientGetPaginationData"
              :listComponent="ClientList"
              :entities="clients"
              :message="clientMessage"
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
        :aria-label="$t('entities.comment.nav.title')"
        checked
      />
      <div class="tab-content card bg-base-100 border border-base-300">
        <div class="card-body">
          <div class="mt-4">
            <EntityTable
              :entity-model="commentEntity"
              :entity-search-fields="commentSearchFields"
              :default-active-filters="['authoruseridUsers']"
              :searchFn="getCommentsData"
              :getPaginationData="commentGetPaginationData"
              :listComponent="CommentList"
              :entities="comments"
              :message="commentMessage"
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
        :aria-label="$t('entities.download.nav.title')"
        checked
      />
      <div class="tab-content card bg-base-100 border border-base-300">
        <div class="card-body">
          <div class="mt-4">
            <EntityTable
              :entity-model="downloadEntity"
              :entity-search-fields="downloadSearchFields"
              :default-active-filters="['useridUsers']"
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
        :aria-label="$t('entities.eventMedia.nav.title')"
        checked
      />
      <div class="tab-content card bg-base-100 border border-base-300">
        <div class="card-body">
          <div class="mt-4">
            <EntityTable
              :entity-model="eventMediaEntity"
              :entity-search-fields="eventMediaSearchFields"
              :default-active-filters="['uploaderuseridUsers']"
              :searchFn="getEventMediasData"
              :getPaginationData="eventMediaGetPaginationData"
              :listComponent="EventMediaList"
              :entities="eventMedias"
              :message="eventMediaMessage"
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
              :default-active-filters="['clientuseridUsers']"
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
        :aria-label="$t('entities.like.nav.title')"
        checked
      />
      <div class="tab-content card bg-base-100 border border-base-300">
        <div class="card-body">
          <div class="mt-4">
            <EntityTable
              :entity-model="likeEntity"
              :entity-search-fields="likeSearchFields"
              :default-active-filters="['useridUsers']"
              :searchFn="getLikesData"
              :getPaginationData="likeGetPaginationData"
              :listComponent="LikeList"
              :entities="likes"
              :message="likeMessage"
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
              :default-active-filters="['useridUsers']"
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
        :aria-label="$t('entities.notificationPreference.nav.title')"
        checked
      />
      <div class="tab-content card bg-base-100 border border-base-300">
        <div class="card-body">
          <div class="mt-4">
            <EntityTable
              :entity-model="notificationPreferenceEntity"
              :entity-search-fields="notificationPreferenceSearchFields"
              :default-active-filters="['useridUsers']"
              :searchFn="getNotificationPreferencesData"
              :getPaginationData="notificationPreferenceGetPaginationData"
              :listComponent="NotificationPreferenceList"
              :entities="notificationPreferences"
              :message="notificationPreferenceMessage"
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
        :aria-label="$t('entities.notification.nav.title')"
        checked
      />
      <div class="tab-content card bg-base-100 border border-base-300">
        <div class="card-body">
          <div class="mt-4">
            <EntityTable
              :entity-model="notificationEntity"
              :entity-search-fields="notificationSearchFields"
              :default-active-filters="['useridUsers']"
              :searchFn="getNotificationsData"
              :getPaginationData="notificationGetPaginationData"
              :listComponent="NotificationList"
              :entities="notifications"
              :message="notificationMessage"
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
        :aria-label="$t('entities.playlist.nav.title')"
        checked
      />
      <div class="tab-content card bg-base-100 border border-base-300">
        <div class="card-body">
          <div class="mt-4">
            <EntityTable
              :entity-model="playlistEntity"
              :entity-search-fields="playlistSearchFields"
              :default-active-filters="['owneruseridUsers']"
              :searchFn="getPlaylistsData"
              :getPaginationData="playlistGetPaginationData"
              :listComponent="PlaylistList"
              :entities="playlists"
              :message="playlistMessage"
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
        :aria-label="$t('entities.report.nav.title')"
        checked
      />
      <div class="tab-content card bg-base-100 border border-base-300">
        <div class="card-body">
          <div class="mt-4">
            <EntityTable
              :entity-model="reportEntity"
              :entity-search-fields="reportSearchFields"
              :default-active-filters="['reporteruseridUsers']"
              :searchFn="getReportsData"
              :getPaginationData="reportGetPaginationData"
              :listComponent="ReportList"
              :entities="reports"
              :message="reportMessage"
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
        :aria-label="$t('entities.searchHistory.nav.title')"
        checked
      />
      <div class="tab-content card bg-base-100 border border-base-300">
        <div class="card-body">
          <div class="mt-4">
            <EntityTable
              :entity-model="searchHistoryEntity"
              :entity-search-fields="searchHistorySearchFields"
              :default-active-filters="['useridUsers']"
              :searchFn="getSearchHistorysData"
              :getPaginationData="searchHistoryGetPaginationData"
              :listComponent="SearchHistoryList"
              :entities="searchHistorys"
              :message="searchHistoryMessage"
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
              :default-active-filters="['useridUsers']"
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
    <div class="tabs tabs-border">
      <input
        type="radio"
        name="details_tab"
        class="tab"
        :aria-label="$t('entities.userStatusHistory.nav.title')"
        checked
      />
      <div class="tab-content card bg-base-100 border border-base-300">
        <div class="card-body">
          <div class="mt-4">
            <EntityTable
              :entity-model="userStatusHistoryEntity"
              :entity-search-fields="userStatusHistorySearchFields"
              :default-active-filters="['useridUsers']"
              :searchFn="getUserStatusHistorysData"
              :getPaginationData="userStatusHistoryGetPaginationData"
              :listComponent="UserStatusHistoryList"
              :entities="userStatusHistorys"
              :message="userStatusHistoryMessage"
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
        :aria-label="$t('entities.verificationCode.nav.title')"
        checked
      />
      <div class="tab-content card bg-base-100 border border-base-300">
        <div class="card-body">
          <div class="mt-4">
            <EntityTable
              :entity-model="verificationCodeEntity"
              :entity-search-fields="verificationCodeSearchFields"
              :default-active-filters="['useridUsers']"
              :searchFn="getVerificationCodesData"
              :getPaginationData="verificationCodeGetPaginationData"
              :listComponent="VerificationCodeList"
              :entities="verificationCodes"
              :message="verificationCodeMessage"
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
import UserDetails from '@/entities/user/UserDetails.vue'
import { useUsers } from '@/composables/useUsers'
import { User } from '@/models/UserModel'
import GenesisButton from '@/components/button/GenesisButton.vue'
import LeftArrowIcon from '@/components/icons/LeftArrowIcon.vue'
import EntityTable from '@/components/table/EntityTable.vue'
import type { EntitySearchField } from '@/models/EntityModel'
import type { PaginationRequestParameter, SortFieldParameter } from '@/models/api/RequestModel'
import { useArtists } from '@/composables/useArtists'
import { Artist } from '@/models/ArtistModel'
import ArtistList from '@/entities/artist/ArtistList.vue'
import { useClients } from '@/composables/useClients'
import { Client } from '@/models/ClientModel'
import ClientList from '@/entities/client/ClientList.vue'
import { useComments } from '@/composables/useComments'
import { Comment } from '@/models/CommentModel'
import CommentList from '@/entities/comment/CommentList.vue'
import { useDownloads } from '@/composables/useDownloads'
import { Download } from '@/models/DownloadModel'
import DownloadList from '@/entities/download/DownloadList.vue'
import { useEventMedias } from '@/composables/useEventMedias'
import { EventMedia } from '@/models/EventMediaModel'
import EventMediaList from '@/entities/eventMedia/EventMediaList.vue'
import { useFollows } from '@/composables/useFollows'
import { Follow } from '@/models/FollowModel'
import FollowList from '@/entities/follow/FollowList.vue'
import { useLikes } from '@/composables/useLikes'
import { Like } from '@/models/LikeModel'
import LikeList from '@/entities/like/LikeList.vue'
import { useListeningHistorys } from '@/composables/useListeningHistorys'
import { ListeningHistory } from '@/models/ListeningHistoryModel'
import ListeningHistoryList from '@/entities/listeningHistory/ListeningHistoryList.vue'
import { useNotificationPreferences } from '@/composables/useNotificationPreferences'
import { NotificationPreference } from '@/models/NotificationPreferenceModel'
import NotificationPreferenceList from '@/entities/notificationPreference/NotificationPreferenceList.vue'
import { useNotifications } from '@/composables/useNotifications'
import { Notification } from '@/models/NotificationModel'
import NotificationList from '@/entities/notification/NotificationList.vue'
import { usePlaylists } from '@/composables/usePlaylists'
import { Playlist } from '@/models/PlaylistModel'
import PlaylistList from '@/entities/playlist/PlaylistList.vue'
import { useReports } from '@/composables/useReports'
import { Report } from '@/models/ReportModel'
import ReportList from '@/entities/report/ReportList.vue'
import { useSearchHistorys } from '@/composables/useSearchHistorys'
import { SearchHistory } from '@/models/SearchHistoryModel'
import SearchHistoryList from '@/entities/searchHistory/SearchHistoryList.vue'
import { useUpNextQueues } from '@/composables/useUpNextQueues'
import { UpNextQueue } from '@/models/UpNextQueueModel'
import UpNextQueueList from '@/entities/upNextQueue/UpNextQueueList.vue'
import { useUserStatusHistorys } from '@/composables/useUserStatusHistorys'
import { UserStatusHistory } from '@/models/UserStatusHistoryModel'
import UserStatusHistoryList from '@/entities/userStatusHistory/UserStatusHistoryList.vue'
import { useVerificationCodes } from '@/composables/useVerificationCodes'
import { VerificationCode } from '@/models/VerificationCodeModel'
import VerificationCodeList from '@/entities/verificationCode/VerificationCodeList.vue'

const route = useRoute()
const pathId = Number(route.params.id)
const { getUserById, goToListView } = useUsers()
const entity = ref<User | null>(null)

const {
  artists,
  getAllArtistsByUserId,
  message: artistMessage,
  getPaginationData: artistGetPaginationData,
} = useArtists()
const getArtistsData = async (
  unpagined: boolean,
  filters: Record<string, unknown>,
  pagination: PaginationRequestParameter,
  sortFields: SortFieldParameter[],
) => {
  return await getAllArtistsByUserId(pathId, unpagined, pagination, sortFields)
}
const artistSearchFields = ref<EntitySearchField[]>(Artist.getAllSearchFieldsMetadata())
const artistEntity = ref<Record<string, unknown>>({ useridUsers: pathId })
const {
  clients,
  getAllClientsByUserId,
  message: clientMessage,
  getPaginationData: clientGetPaginationData,
} = useClients()
const getClientsData = async (
  unpagined: boolean,
  filters: Record<string, unknown>,
  pagination: PaginationRequestParameter,
  sortFields: SortFieldParameter[],
) => {
  return await getAllClientsByUserId(pathId, unpagined, pagination, sortFields)
}
const clientSearchFields = ref<EntitySearchField[]>(Client.getAllSearchFieldsMetadata())
const clientEntity = ref<Record<string, unknown>>({ useridUsers: pathId })
const {
  comments,
  getAllCommentsByUserId,
  message: commentMessage,
  getPaginationData: commentGetPaginationData,
} = useComments()
const getCommentsData = async (
  unpagined: boolean,
  filters: Record<string, unknown>,
  pagination: PaginationRequestParameter,
  sortFields: SortFieldParameter[],
) => {
  return await getAllCommentsByUserId(pathId, unpagined, pagination, sortFields)
}
const commentSearchFields = ref<EntitySearchField[]>(Comment.getAllSearchFieldsMetadata())
const commentEntity = ref<Record<string, unknown>>({ authoruseridUsers: pathId })
const {
  downloads,
  getAllDownloadsByUserId,
  message: downloadMessage,
  getPaginationData: downloadGetPaginationData,
} = useDownloads()
const getDownloadsData = async (
  unpagined: boolean,
  filters: Record<string, unknown>,
  pagination: PaginationRequestParameter,
  sortFields: SortFieldParameter[],
) => {
  return await getAllDownloadsByUserId(pathId, unpagined, pagination, sortFields)
}
const downloadSearchFields = ref<EntitySearchField[]>(Download.getAllSearchFieldsMetadata())
const downloadEntity = ref<Record<string, unknown>>({ useridUsers: pathId })
const {
  eventMedias,
  getAllEventMediasByUserId,
  message: eventMediaMessage,
  getPaginationData: eventMediaGetPaginationData,
} = useEventMedias()
const getEventMediasData = async (
  unpagined: boolean,
  filters: Record<string, unknown>,
  pagination: PaginationRequestParameter,
  sortFields: SortFieldParameter[],
) => {
  return await getAllEventMediasByUserId(pathId, unpagined, pagination, sortFields)
}
const eventMediaSearchFields = ref<EntitySearchField[]>(EventMedia.getAllSearchFieldsMetadata())
const eventMediaEntity = ref<Record<string, unknown>>({ uploaderuseridUsers: pathId })
const {
  follows,
  getAllFollowsByUserId,
  message: followMessage,
  getPaginationData: followGetPaginationData,
} = useFollows()
const getFollowsData = async (
  unpagined: boolean,
  filters: Record<string, unknown>,
  pagination: PaginationRequestParameter,
  sortFields: SortFieldParameter[],
) => {
  return await getAllFollowsByUserId(pathId, unpagined, pagination, sortFields)
}
const followSearchFields = ref<EntitySearchField[]>(Follow.getAllSearchFieldsMetadata())
const followEntity = ref<Record<string, unknown>>({ clientuseridUsers: pathId })
const {
  likes,
  getAllLikesByUserId,
  message: likeMessage,
  getPaginationData: likeGetPaginationData,
} = useLikes()
const getLikesData = async (
  unpagined: boolean,
  filters: Record<string, unknown>,
  pagination: PaginationRequestParameter,
  sortFields: SortFieldParameter[],
) => {
  return await getAllLikesByUserId(pathId, unpagined, pagination, sortFields)
}
const likeSearchFields = ref<EntitySearchField[]>(Like.getAllSearchFieldsMetadata())
const likeEntity = ref<Record<string, unknown>>({ useridUsers: pathId })
const {
  listeningHistorys,
  getAllListeningHistorysByUserId,
  message: listeningHistoryMessage,
  getPaginationData: listeningHistoryGetPaginationData,
} = useListeningHistorys()
const getListeningHistorysData = async (
  unpagined: boolean,
  filters: Record<string, unknown>,
  pagination: PaginationRequestParameter,
  sortFields: SortFieldParameter[],
) => {
  return await getAllListeningHistorysByUserId(pathId, unpagined, pagination, sortFields)
}
const listeningHistorySearchFields = ref<EntitySearchField[]>(
  ListeningHistory.getAllSearchFieldsMetadata(),
)
const listeningHistoryEntity = ref<Record<string, unknown>>({ useridUsers: pathId })
const {
  notificationPreferences,
  getAllNotificationPreferencesByUserId,
  message: notificationPreferenceMessage,
  getPaginationData: notificationPreferenceGetPaginationData,
} = useNotificationPreferences()
const getNotificationPreferencesData = async (
  unpagined: boolean,
  filters: Record<string, unknown>,
  pagination: PaginationRequestParameter,
  sortFields: SortFieldParameter[],
) => {
  return await getAllNotificationPreferencesByUserId(pathId, unpagined, pagination, sortFields)
}
const notificationPreferenceSearchFields = ref<EntitySearchField[]>(
  NotificationPreference.getAllSearchFieldsMetadata(),
)
const notificationPreferenceEntity = ref<Record<string, unknown>>({ useridUsers: pathId })
const {
  notifications,
  getAllNotificationsByUserId,
  message: notificationMessage,
  getPaginationData: notificationGetPaginationData,
} = useNotifications()
const getNotificationsData = async (
  unpagined: boolean,
  filters: Record<string, unknown>,
  pagination: PaginationRequestParameter,
  sortFields: SortFieldParameter[],
) => {
  return await getAllNotificationsByUserId(pathId, unpagined, pagination, sortFields)
}
const notificationSearchFields = ref<EntitySearchField[]>(Notification.getAllSearchFieldsMetadata())
const notificationEntity = ref<Record<string, unknown>>({ useridUsers: pathId })
const {
  playlists,
  getAllPlaylistsByUserId,
  message: playlistMessage,
  getPaginationData: playlistGetPaginationData,
} = usePlaylists()
const getPlaylistsData = async (
  unpagined: boolean,
  filters: Record<string, unknown>,
  pagination: PaginationRequestParameter,
  sortFields: SortFieldParameter[],
) => {
  return await getAllPlaylistsByUserId(pathId, unpagined, pagination, sortFields)
}
const playlistSearchFields = ref<EntitySearchField[]>(Playlist.getAllSearchFieldsMetadata())
const playlistEntity = ref<Record<string, unknown>>({ owneruseridUsers: pathId })
const {
  reports,
  getAllReportsByUserId,
  message: reportMessage,
  getPaginationData: reportGetPaginationData,
} = useReports()
const getReportsData = async (
  unpagined: boolean,
  filters: Record<string, unknown>,
  pagination: PaginationRequestParameter,
  sortFields: SortFieldParameter[],
) => {
  return await getAllReportsByUserId(pathId, unpagined, pagination, sortFields)
}
const reportSearchFields = ref<EntitySearchField[]>(Report.getAllSearchFieldsMetadata())
const reportEntity = ref<Record<string, unknown>>({ reporteruseridUsers: pathId })
const {
  searchHistorys,
  getAllSearchHistorysByUserId,
  message: searchHistoryMessage,
  getPaginationData: searchHistoryGetPaginationData,
} = useSearchHistorys()
const getSearchHistorysData = async (
  unpagined: boolean,
  filters: Record<string, unknown>,
  pagination: PaginationRequestParameter,
  sortFields: SortFieldParameter[],
) => {
  return await getAllSearchHistorysByUserId(pathId, unpagined, pagination, sortFields)
}
const searchHistorySearchFields = ref<EntitySearchField[]>(
  SearchHistory.getAllSearchFieldsMetadata(),
)
const searchHistoryEntity = ref<Record<string, unknown>>({ useridUsers: pathId })
const {
  upNextQueues,
  getAllUpNextQueuesByUserId,
  message: upNextQueueMessage,
  getPaginationData: upNextQueueGetPaginationData,
} = useUpNextQueues()
const getUpNextQueuesData = async (
  unpagined: boolean,
  filters: Record<string, unknown>,
  pagination: PaginationRequestParameter,
  sortFields: SortFieldParameter[],
) => {
  return await getAllUpNextQueuesByUserId(pathId, unpagined, pagination, sortFields)
}
const upNextQueueSearchFields = ref<EntitySearchField[]>(UpNextQueue.getAllSearchFieldsMetadata())
const upNextQueueEntity = ref<Record<string, unknown>>({ useridUsers: pathId })
const {
  userStatusHistorys,
  getAllUserStatusHistorysByUserId,
  message: userStatusHistoryMessage,
  getPaginationData: userStatusHistoryGetPaginationData,
} = useUserStatusHistorys()
const getUserStatusHistorysData = async (
  unpagined: boolean,
  filters: Record<string, unknown>,
  pagination: PaginationRequestParameter,
  sortFields: SortFieldParameter[],
) => {
  return await getAllUserStatusHistorysByUserId(pathId, unpagined, pagination, sortFields)
}
const userStatusHistorySearchFields = ref<EntitySearchField[]>(
  UserStatusHistory.getAllSearchFieldsMetadata(),
)
const userStatusHistoryEntity = ref<Record<string, unknown>>({ useridUsers: pathId })
const {
  verificationCodes,
  getAllVerificationCodesByUserId,
  message: verificationCodeMessage,
  getPaginationData: verificationCodeGetPaginationData,
} = useVerificationCodes()
const getVerificationCodesData = async (
  unpagined: boolean,
  filters: Record<string, unknown>,
  pagination: PaginationRequestParameter,
  sortFields: SortFieldParameter[],
) => {
  return await getAllVerificationCodesByUserId(pathId, unpagined, pagination, sortFields)
}
const verificationCodeSearchFields = ref<EntitySearchField[]>(
  VerificationCode.getAllSearchFieldsMetadata(),
)
const verificationCodeEntity = ref<Record<string, unknown>>({ useridUsers: pathId })

onMounted(async () => {
  const result = await getUserById(pathId)
  entity.value = result.data || null
})
</script>
<style scoped></style>
