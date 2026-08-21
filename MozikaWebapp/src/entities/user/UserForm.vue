<template>
  <div class="card bg-base-100">
    <div class="card-body">
      <form @submit.prevent="handleSubmit" class="grid grid-cols-1 w-7/12 gap-4 mx-auto">
        <!-- <form @submit.prevent="handleSubmit" class=""> -->
        <div class="grid grid-cols-1 gap-4">
          <!-- Email -->
          <GenesisInput
            label="Email"
            :violation="userViolations ? userViolations['email'] : undefined"
            placeholder="Enter Email"
            type="text"
            v-model="formModel.email"
            :value="formModel.email"
          />

          <!-- Phone -->
          <GenesisInput
            label="Phone"
            :violation="userViolations ? userViolations['phone'] : undefined"
            placeholder="Enter Phone"
            type="text"
            v-model="formModel.phone"
            :value="formModel.phone"
          />

          <!-- Email verified at -->
          <GenesisInput
            label="Email verified at"
            :violation="userViolations ? userViolations['emailVerifiedAt'] : undefined"
            placeholder="Enter Email verified at"
            type="datetime-local"
            v-model="formModel.emailVerifiedAt"
            :value="formModel.emailVerifiedAt"
          />

          <!-- Phone verified at -->
          <GenesisInput
            label="Phone verified at"
            :violation="userViolations ? userViolations['phoneVerifiedAt'] : undefined"
            placeholder="Enter Phone verified at"
            type="datetime-local"
            v-model="formModel.phoneVerifiedAt"
            :value="formModel.phoneVerifiedAt"
          />

          <!-- Password hash -->
          <GenesisInput
            label="Password hash"
            :violation="userViolations ? userViolations['passwordHash'] : undefined"
            placeholder="Enter Password hash"
            type="text"
            v-model="formModel.passwordHash"
            :value="formModel.passwordHash"
          />

          <!-- Roleid user roles FK -->
          <GenesisSelectSearchCriteria
            v-if="roleidUserRolesSearchField?.multicriteriaSelect"
            label="Roleid user roles"
            key="userRoleidUserRoles"
            :search-function="roleidUserRolesSearchField?.multicriteriaSelect.searchFunction"
            :filters="roleidUserRolesSearchField?.multicriteriaSelect.filters"
            :default-value="roleidUserRolesDefaultValue"
            :violation="userViolations ? userViolations['roleidUserRoles'] : undefined"
            @option-selected="
              (selectedValue) => (formModel.roleidUserRoles = String(selectedValue))
            "
          />

          <!-- Statusid user statuses FK -->
          <GenesisSelectSearchCriteria
            v-if="statusidUserStatusesSearchField?.multicriteriaSelect"
            label="Statusid user statuses"
            key="userStatusidUserStatuses"
            :search-function="statusidUserStatusesSearchField?.multicriteriaSelect.searchFunction"
            :filters="statusidUserStatusesSearchField?.multicriteriaSelect.filters"
            :default-value="statusidUserStatusesDefaultValue"
            :violation="userViolations ? userViolations['statusidUserStatuses'] : undefined"
            @option-selected="
              (selectedValue) => (formModel.statusidUserStatuses = String(selectedValue))
            "
          />

          <!-- Created at -->
          <GenesisInput
            label="Created at"
            :violation="userViolations ? userViolations['createdAt'] : undefined"
            placeholder="Enter Created at"
            type="datetime-local"
            v-model="formModel.createdAt"
            :value="formModel.createdAt"
          />

          <!-- Updated at -->
          <GenesisInput
            label="Updated at"
            :violation="userViolations ? userViolations['updatedAt'] : undefined"
            placeholder="Enter Updated at"
            type="datetime-local"
            v-model="formModel.updatedAt"
            :value="formModel.updatedAt"
          />
        </div>
        <ArtistTableForm
          :initial-data="artistsData"
          :get-table-data-callback="setArtists"
          :violations="artistViolations"
          class="w-full"
        />
        <ClientTableForm
          :initial-data="clientsData"
          :get-table-data-callback="setClients"
          :violations="clientViolations"
          class="w-full"
        />
        <CommentTableForm
          :initial-data="commentsData"
          :get-table-data-callback="setComments"
          :violations="commentViolations"
          class="w-full"
        />
        <DownloadTableForm
          :initial-data="downloadsData"
          :get-table-data-callback="setDownloads"
          :violations="downloadViolations"
          class="w-full"
        />
        <EventMediaTableForm
          :initial-data="eventMediasData"
          :get-table-data-callback="setEventMedias"
          :violations="eventMediaViolations"
          class="w-full"
        />
        <FollowTableForm
          :initial-data="followsData"
          :get-table-data-callback="setFollows"
          :violations="followViolations"
          class="w-full"
        />
        <LikeTableForm
          :initial-data="likesData"
          :get-table-data-callback="setLikes"
          :violations="likeViolations"
          class="w-full"
        />
        <ListeningHistoryTableForm
          :initial-data="listeningHistorysData"
          :get-table-data-callback="setListeningHistorys"
          :violations="listeningHistoryViolations"
          class="w-full"
        />
        <NotificationPreferenceTableForm
          :initial-data="notificationPreferencesData"
          :get-table-data-callback="setNotificationPreferences"
          :violations="notificationPreferenceViolations"
          class="w-full"
        />
        <NotificationTableForm
          :initial-data="notificationsData"
          :get-table-data-callback="setNotifications"
          :violations="notificationViolations"
          class="w-full"
        />
        <PlaylistTableForm
          :initial-data="playlistsData"
          :get-table-data-callback="setPlaylists"
          :violations="playlistViolations"
          class="w-full"
        />
        <ReportTableForm
          :initial-data="reportsData"
          :get-table-data-callback="setReports"
          :violations="reportViolations"
          class="w-full"
        />
        <SearchHistoryTableForm
          :initial-data="searchHistorysData"
          :get-table-data-callback="setSearchHistorys"
          :violations="searchHistoryViolations"
          class="w-full"
        />
        <UpNextQueueTableForm
          :initial-data="upNextQueuesData"
          :get-table-data-callback="setUpNextQueues"
          :violations="upNextQueueViolations"
          class="w-full"
        />
        <UserStatusHistoryTableForm
          :initial-data="userStatusHistorysData"
          :get-table-data-callback="setUserStatusHistorys"
          :violations="userStatusHistoryViolations"
          class="w-full"
        />
        <VerificationCodeTableForm
          :initial-data="verificationCodesData"
          :get-table-data-callback="setVerificationCodes"
          :violations="verificationCodeViolations"
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
import { User, UserFormDTO } from '@/models/UserModel.ts'
import GenesisButton from '@/components/button/GenesisButton.vue'
import GenesisInput from '@/components/form/GenesisInput.vue'
import GenesisSelectSearchCriteria from '@/components/form/GenesisSelectSearchCriteria.vue'
import { ArtistFormDTO, type Artist } from '@/models/ArtistModel.ts'
import ArtistTableForm from '@/entities/artist/ArtistTableForm.vue'
import { ClientFormDTO, type Client } from '@/models/ClientModel.ts'
import ClientTableForm from '@/entities/client/ClientTableForm.vue'
import { CommentFormDTO, type Comment } from '@/models/CommentModel.ts'
import CommentTableForm from '@/entities/comment/CommentTableForm.vue'
import { DownloadFormDTO, type Download } from '@/models/DownloadModel.ts'
import DownloadTableForm from '@/entities/download/DownloadTableForm.vue'
import { EventMediaFormDTO, type EventMedia } from '@/models/EventMediaModel.ts'
import EventMediaTableForm from '@/entities/eventMedia/EventMediaTableForm.vue'
import { FollowFormDTO, type Follow } from '@/models/FollowModel.ts'
import FollowTableForm from '@/entities/follow/FollowTableForm.vue'
import { LikeFormDTO, type Like } from '@/models/LikeModel.ts'
import LikeTableForm from '@/entities/like/LikeTableForm.vue'
import { ListeningHistoryFormDTO, type ListeningHistory } from '@/models/ListeningHistoryModel.ts'
import ListeningHistoryTableForm from '@/entities/listeningHistory/ListeningHistoryTableForm.vue'
import {
  NotificationPreferenceFormDTO,
  type NotificationPreference,
} from '@/models/NotificationPreferenceModel.ts'
import NotificationPreferenceTableForm from '@/entities/notificationPreference/NotificationPreferenceTableForm.vue'
import { NotificationFormDTO, type Notification } from '@/models/NotificationModel.ts'
import NotificationTableForm from '@/entities/notification/NotificationTableForm.vue'
import { PlaylistFormDTO, type Playlist } from '@/models/PlaylistModel.ts'
import PlaylistTableForm from '@/entities/playlist/PlaylistTableForm.vue'
import { ReportFormDTO, type Report } from '@/models/ReportModel.ts'
import ReportTableForm from '@/entities/report/ReportTableForm.vue'
import { SearchHistoryFormDTO, type SearchHistory } from '@/models/SearchHistoryModel.ts'
import SearchHistoryTableForm from '@/entities/searchHistory/SearchHistoryTableForm.vue'
import { UpNextQueueFormDTO, type UpNextQueue } from '@/models/UpNextQueueModel.ts'
import UpNextQueueTableForm from '@/entities/upNextQueue/UpNextQueueTableForm.vue'
import {
  UserStatusHistoryFormDTO,
  type UserStatusHistory,
} from '@/models/UserStatusHistoryModel.ts'
import UserStatusHistoryTableForm from '@/entities/userStatusHistory/UserStatusHistoryTableForm.vue'
import { VerificationCodeFormDTO, type VerificationCode } from '@/models/VerificationCodeModel.ts'
import VerificationCodeTableForm from '@/entities/verificationCode/VerificationCodeTableForm.vue'

const props = defineProps<{
  user?: User

  artistsData?: Artist[]
  clientsData?: Client[]
  commentsData?: Comment[]
  downloadsData?: Download[]
  eventMediasData?: EventMedia[]
  followsData?: Follow[]
  likesData?: Like[]
  listeningHistorysData?: ListeningHistory[]
  notificationPreferencesData?: NotificationPreference[]
  notificationsData?: Notification[]
  playlistsData?: Playlist[]
  reportsData?: Report[]
  searchHistorysData?: SearchHistory[]
  upNextQueuesData?: UpNextQueue[]
  userStatusHistorysData?: UserStatusHistory[]
  verificationCodesData?: VerificationCode[]

  violations?: {
    user: Record<string, string>
    artists: Record<string, string>[]
    clients: Record<string, string>[]
    comments: Record<string, string>[]
    downloads: Record<string, string>[]
    eventMedias: Record<string, string>[]
    follows: Record<string, string>[]
    likes: Record<string, string>[]
    listeningHistorys: Record<string, string>[]
    notificationPreferences: Record<string, string>[]
    notifications: Record<string, string>[]
    playlists: Record<string, string>[]
    reports: Record<string, string>[]
    searchHistorys: Record<string, string>[]
    upNextQueues: Record<string, string>[]
    userStatusHistorys: Record<string, string>[]
    verificationCodes: Record<string, string>[]
  }

  submitLabel?: string
}>()

const emit = defineEmits<{
  (
    e: 'submit',
    payload: {
      user: Partial<UserFormDTO>
      artists: ArtistFormDTO[]
      clients: ClientFormDTO[]
      comments: CommentFormDTO[]
      downloads: DownloadFormDTO[]
      eventMedias: EventMediaFormDTO[]
      follows: FollowFormDTO[]
      likes: LikeFormDTO[]
      listeningHistorys: ListeningHistoryFormDTO[]
      notificationPreferences: NotificationPreferenceFormDTO[]
      notifications: NotificationFormDTO[]
      playlists: PlaylistFormDTO[]
      reports: ReportFormDTO[]
      searchHistorys: SearchHistoryFormDTO[]
      upNextQueues: UpNextQueueFormDTO[]
      userStatusHistorys: UserStatusHistoryFormDTO[]
      verificationCodes: VerificationCodeFormDTO[]
    },
  ): void
  (e: 'cancel', payload: Partial<UserFormDTO>): void
}>()

const userViolations = computed(() => (props.violations ? props.violations.user : {}))
const artistViolations = computed(() => (props.violations ? props.violations.artists : []))
const clientViolations = computed(() => (props.violations ? props.violations.clients : []))
const commentViolations = computed(() => (props.violations ? props.violations.comments : []))
const downloadViolations = computed(() => (props.violations ? props.violations.downloads : []))
const eventMediaViolations = computed(() => (props.violations ? props.violations.eventMedias : []))
const followViolations = computed(() => (props.violations ? props.violations.follows : []))
const likeViolations = computed(() => (props.violations ? props.violations.likes : []))
const listeningHistoryViolations = computed(() =>
  props.violations ? props.violations.listeningHistorys : [],
)
const notificationPreferenceViolations = computed(() =>
  props.violations ? props.violations.notificationPreferences : [],
)
const notificationViolations = computed(() =>
  props.violations ? props.violations.notifications : [],
)
const playlistViolations = computed(() => (props.violations ? props.violations.playlists : []))
const reportViolations = computed(() => (props.violations ? props.violations.reports : []))
const searchHistoryViolations = computed(() =>
  props.violations ? props.violations.searchHistorys : [],
)
const upNextQueueViolations = computed(() =>
  props.violations ? props.violations.upNextQueues : [],
)
const userStatusHistoryViolations = computed(() =>
  props.violations ? props.violations.userStatusHistorys : [],
)
const verificationCodeViolations = computed(() =>
  props.violations ? props.violations.verificationCodes : [],
)

const formModel = ref<Partial<UserFormDTO>>({ ...UserFormDTO.parse(props.user) })
const roleidUserRolesSearchField = User.getSearchFieldByKey('roleidUserRoles')
const roleidUserRolesDefaultValue = props.user
  ? (props.user?.roleidUserRoles?.getKeyValue?.() ?? undefined)
  : undefined
const statusidUserStatusesSearchField = User.getSearchFieldByKey('statusidUserStatuses')
const statusidUserStatusesDefaultValue = props.user
  ? (props.user?.statusidUserStatuses?.getKeyValue?.() ?? undefined)
  : undefined

const artists = ref<ArtistFormDTO[]>([])
const setArtists = (data: ArtistFormDTO[]) => {
  artists.value = data
}
const clients = ref<ClientFormDTO[]>([])
const setClients = (data: ClientFormDTO[]) => {
  clients.value = data
}
const comments = ref<CommentFormDTO[]>([])
const setComments = (data: CommentFormDTO[]) => {
  comments.value = data
}
const downloads = ref<DownloadFormDTO[]>([])
const setDownloads = (data: DownloadFormDTO[]) => {
  downloads.value = data
}
const eventMedias = ref<EventMediaFormDTO[]>([])
const setEventMedias = (data: EventMediaFormDTO[]) => {
  eventMedias.value = data
}
const follows = ref<FollowFormDTO[]>([])
const setFollows = (data: FollowFormDTO[]) => {
  follows.value = data
}
const likes = ref<LikeFormDTO[]>([])
const setLikes = (data: LikeFormDTO[]) => {
  likes.value = data
}
const listeningHistorys = ref<ListeningHistoryFormDTO[]>([])
const setListeningHistorys = (data: ListeningHistoryFormDTO[]) => {
  listeningHistorys.value = data
}
const notificationPreferences = ref<NotificationPreferenceFormDTO[]>([])
const setNotificationPreferences = (data: NotificationPreferenceFormDTO[]) => {
  notificationPreferences.value = data
}
const notifications = ref<NotificationFormDTO[]>([])
const setNotifications = (data: NotificationFormDTO[]) => {
  notifications.value = data
}
const playlists = ref<PlaylistFormDTO[]>([])
const setPlaylists = (data: PlaylistFormDTO[]) => {
  playlists.value = data
}
const reports = ref<ReportFormDTO[]>([])
const setReports = (data: ReportFormDTO[]) => {
  reports.value = data
}
const searchHistorys = ref<SearchHistoryFormDTO[]>([])
const setSearchHistorys = (data: SearchHistoryFormDTO[]) => {
  searchHistorys.value = data
}
const upNextQueues = ref<UpNextQueueFormDTO[]>([])
const setUpNextQueues = (data: UpNextQueueFormDTO[]) => {
  upNextQueues.value = data
}
const userStatusHistorys = ref<UserStatusHistoryFormDTO[]>([])
const setUserStatusHistorys = (data: UserStatusHistoryFormDTO[]) => {
  userStatusHistorys.value = data
}
const verificationCodes = ref<VerificationCodeFormDTO[]>([])
const setVerificationCodes = (data: VerificationCodeFormDTO[]) => {
  verificationCodes.value = data
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

  emit('submit', {
    user: data,
    artists: artists.value,
    clients: clients.value,
    comments: comments.value,
    downloads: downloads.value,
    eventMedias: eventMedias.value,
    follows: follows.value,
    likes: likes.value,
    listeningHistorys: listeningHistorys.value,
    notificationPreferences: notificationPreferences.value,
    notifications: notifications.value,
    playlists: playlists.value,
    reports: reports.value,
    searchHistorys: searchHistorys.value,
    upNextQueues: upNextQueues.value,
    userStatusHistorys: userStatusHistorys.value,
    verificationCodes: verificationCodes.value,
  })
}

function cancelForm() {
  emit('cancel', formModel.value)
}
onMounted(async () => {
  if (Array.isArray(props.artistsData) && props.artistsData.length > 0) {
    setArtists(ArtistFormDTO.parseList(props.artistsData))
  }
  if (Array.isArray(props.clientsData) && props.clientsData.length > 0) {
    setClients(ClientFormDTO.parseList(props.clientsData))
  }
  if (Array.isArray(props.commentsData) && props.commentsData.length > 0) {
    setComments(CommentFormDTO.parseList(props.commentsData))
  }
  if (Array.isArray(props.downloadsData) && props.downloadsData.length > 0) {
    setDownloads(DownloadFormDTO.parseList(props.downloadsData))
  }
  if (Array.isArray(props.eventMediasData) && props.eventMediasData.length > 0) {
    setEventMedias(EventMediaFormDTO.parseList(props.eventMediasData))
  }
  if (Array.isArray(props.followsData) && props.followsData.length > 0) {
    setFollows(FollowFormDTO.parseList(props.followsData))
  }
  if (Array.isArray(props.likesData) && props.likesData.length > 0) {
    setLikes(LikeFormDTO.parseList(props.likesData))
  }
  if (Array.isArray(props.listeningHistorysData) && props.listeningHistorysData.length > 0) {
    setListeningHistorys(ListeningHistoryFormDTO.parseList(props.listeningHistorysData))
  }
  if (
    Array.isArray(props.notificationPreferencesData) &&
    props.notificationPreferencesData.length > 0
  ) {
    setNotificationPreferences(
      NotificationPreferenceFormDTO.parseList(props.notificationPreferencesData),
    )
  }
  if (Array.isArray(props.notificationsData) && props.notificationsData.length > 0) {
    setNotifications(NotificationFormDTO.parseList(props.notificationsData))
  }
  if (Array.isArray(props.playlistsData) && props.playlistsData.length > 0) {
    setPlaylists(PlaylistFormDTO.parseList(props.playlistsData))
  }
  if (Array.isArray(props.reportsData) && props.reportsData.length > 0) {
    setReports(ReportFormDTO.parseList(props.reportsData))
  }
  if (Array.isArray(props.searchHistorysData) && props.searchHistorysData.length > 0) {
    setSearchHistorys(SearchHistoryFormDTO.parseList(props.searchHistorysData))
  }
  if (Array.isArray(props.upNextQueuesData) && props.upNextQueuesData.length > 0) {
    setUpNextQueues(UpNextQueueFormDTO.parseList(props.upNextQueuesData))
  }
  if (Array.isArray(props.userStatusHistorysData) && props.userStatusHistorysData.length > 0) {
    setUserStatusHistorys(UserStatusHistoryFormDTO.parseList(props.userStatusHistorysData))
  }
  if (Array.isArray(props.verificationCodesData) && props.verificationCodesData.length > 0) {
    setVerificationCodes(VerificationCodeFormDTO.parseList(props.verificationCodesData))
  }
})
</script>
<style scoped></style>
