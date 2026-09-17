<template>
  <div class="w-full">
    <!-- Header -->
    <div class="flex items-center justify-between mb-6">
      <h3 class="text-xl font-semibold text-base-content">
        User /
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
    <div
      v-if="
        entity &&
        artistLoaded &&
        clientLoaded &&
        commentLoaded &&
        downloadLoaded &&
        eventMediaLoaded &&
        followLoaded &&
        likeLoaded &&
        listeningHistoryLoaded &&
        notificationPreferenceLoaded &&
        notificationLoaded &&
        playlistLoaded &&
        reportLoaded &&
        searchHistoryLoaded &&
        upNextQueueLoaded &&
        userStatusHistoryLoaded &&
        verificationCodeLoaded
      "
    >
      <user-form
        :user="entity"
        :artistsData="artists"
        :clientsData="clients"
        :commentsData="comments"
        :downloadsData="downloads"
        :eventMediasData="eventMedias"
        :followsData="follows"
        :likesData="likes"
        :listeningHistorysData="listeningHistorys"
        :notificationPreferencesData="notificationPreferences"
        :notificationsData="notifications"
        :playlistsData="playlists"
        :reportsData="reports"
        :searchHistorysData="searchHistorys"
        :upNextQueuesData="upNextQueues"
        :userStatusHistorysData="userStatusHistorys"
        :verificationCodesData="verificationCodes"
        :violations="violations"
        :submit-label="$t('entity.update.submitLabel', { entity: 'User' })"
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
import UserForm from '@/entities/user/UserForm.vue'
import AlertPopup from '@/components/popup/AlertPopup.vue'
import { useUsers } from '@/composables/useUsers'
import { User, UserFormDTO } from '@/models/UserModel'
import { usePopup } from '@/composables/usePopup'
import GenesisButton from '@/components/button/GenesisButton.vue'
import LeftArrowIcon from '@/components/icons/LeftArrowIcon.vue'
import { useFreezeScreenStore } from '@/stores/useFreezeScreenStore.ts'
import { useArtists } from '@/composables/useArtists'
import { ArtistFormDTO } from '@/models/ArtistModel'
import { useClients } from '@/composables/useClients'
import { ClientFormDTO } from '@/models/ClientModel'
import { useComments } from '@/composables/useComments'
import { CommentFormDTO } from '@/models/CommentModel'
import { useDownloads } from '@/composables/useDownloads'
import { DownloadFormDTO } from '@/models/DownloadModel'
import { useEventMedias } from '@/composables/useEventMedias'
import { EventMediaFormDTO } from '@/models/EventMediaModel'
import { useFollows } from '@/composables/useFollows'
import { FollowFormDTO } from '@/models/FollowModel'
import { useLikes } from '@/composables/useLikes'
import { LikeFormDTO } from '@/models/LikeModel'
import { useListeningHistorys } from '@/composables/useListeningHistorys'
import { ListeningHistoryFormDTO } from '@/models/ListeningHistoryModel'
import { useNotificationPreferences } from '@/composables/useNotificationPreferences'
import { NotificationPreferenceFormDTO } from '@/models/NotificationPreferenceModel'
import { useNotifications } from '@/composables/useNotifications'
import { NotificationFormDTO } from '@/models/NotificationModel'
import { usePlaylists } from '@/composables/usePlaylists'
import { PlaylistFormDTO } from '@/models/PlaylistModel'
import { useReports } from '@/composables/useReports'
import { ReportFormDTO } from '@/models/ReportModel'
import { useSearchHistorys } from '@/composables/useSearchHistorys'
import { SearchHistoryFormDTO } from '@/models/SearchHistoryModel'
import { useUpNextQueues } from '@/composables/useUpNextQueues'
import { UpNextQueueFormDTO } from '@/models/UpNextQueueModel'
import { useUserStatusHistorys } from '@/composables/useUserStatusHistorys'
import { UserStatusHistoryFormDTO } from '@/models/UserStatusHistoryModel'
import { useVerificationCodes } from '@/composables/useVerificationCodes'
import { VerificationCodeFormDTO } from '@/models/VerificationCodeModel'

import { PaginationRequestParameter } from '@/models/api/RequestModel'

const route = useRoute()
const pathId = Number(route.params.id)
const { closePopup, openPopup, visible: alertPopup } = usePopup()
const { getUserById, goToListView, updateUser, viewUser, message } = useUsers()
const entity = ref<User | null>(null)
const freezeStore = useFreezeScreenStore()

const { artists, getAllArtistsByUserId } = useArtists()
const artistLoaded = ref(false)
const loadArtists = async () => {
  try {
    await getAllArtistsByUserId(pathId, true, new PaginationRequestParameter(), [])
    artistLoaded.value = true
  } catch (error) {
    console.error('Error loading user with details:', error)
    artistLoaded.value = true // Continuer même en cas d'erreur
  }
}
const { clients, getAllClientsByUserId } = useClients()
const clientLoaded = ref(false)
const loadClients = async () => {
  try {
    await getAllClientsByUserId(pathId, true, new PaginationRequestParameter(), [])
    clientLoaded.value = true
  } catch (error) {
    console.error('Error loading user with details:', error)
    clientLoaded.value = true // Continuer même en cas d'erreur
  }
}
const { comments, getAllCommentsByUserId } = useComments()
const commentLoaded = ref(false)
const loadComments = async () => {
  try {
    await getAllCommentsByUserId(pathId, true, new PaginationRequestParameter(), [])
    commentLoaded.value = true
  } catch (error) {
    console.error('Error loading user with details:', error)
    commentLoaded.value = true // Continuer même en cas d'erreur
  }
}
const { downloads, getAllDownloadsByUserId } = useDownloads()
const downloadLoaded = ref(false)
const loadDownloads = async () => {
  try {
    await getAllDownloadsByUserId(pathId, true, new PaginationRequestParameter(), [])
    downloadLoaded.value = true
  } catch (error) {
    console.error('Error loading user with details:', error)
    downloadLoaded.value = true // Continuer même en cas d'erreur
  }
}
const { eventMedias, getAllEventMediasByUserId } = useEventMedias()
const eventMediaLoaded = ref(false)
const loadEventMedias = async () => {
  try {
    await getAllEventMediasByUserId(pathId, true, new PaginationRequestParameter(), [])
    eventMediaLoaded.value = true
  } catch (error) {
    console.error('Error loading user with details:', error)
    eventMediaLoaded.value = true // Continuer même en cas d'erreur
  }
}
const { follows, getAllFollowsByUserId } = useFollows()
const followLoaded = ref(false)
const loadFollows = async () => {
  try {
    await getAllFollowsByUserId(pathId, true, new PaginationRequestParameter(), [])
    followLoaded.value = true
  } catch (error) {
    console.error('Error loading user with details:', error)
    followLoaded.value = true // Continuer même en cas d'erreur
  }
}
const { likes, getAllLikesByUserId } = useLikes()
const likeLoaded = ref(false)
const loadLikes = async () => {
  try {
    await getAllLikesByUserId(pathId, true, new PaginationRequestParameter(), [])
    likeLoaded.value = true
  } catch (error) {
    console.error('Error loading user with details:', error)
    likeLoaded.value = true // Continuer même en cas d'erreur
  }
}
const { listeningHistorys, getAllListeningHistorysByUserId } = useListeningHistorys()
const listeningHistoryLoaded = ref(false)
const loadListeningHistorys = async () => {
  try {
    await getAllListeningHistorysByUserId(pathId, true, new PaginationRequestParameter(), [])
    listeningHistoryLoaded.value = true
  } catch (error) {
    console.error('Error loading user with details:', error)
    listeningHistoryLoaded.value = true // Continuer même en cas d'erreur
  }
}
const { notificationPreferences, getAllNotificationPreferencesByUserId } =
  useNotificationPreferences()
const notificationPreferenceLoaded = ref(false)
const loadNotificationPreferences = async () => {
  try {
    await getAllNotificationPreferencesByUserId(pathId, true, new PaginationRequestParameter(), [])
    notificationPreferenceLoaded.value = true
  } catch (error) {
    console.error('Error loading user with details:', error)
    notificationPreferenceLoaded.value = true // Continuer même en cas d'erreur
  }
}
const { notifications, getAllNotificationsByUserId } = useNotifications()
const notificationLoaded = ref(false)
const loadNotifications = async () => {
  try {
    await getAllNotificationsByUserId(pathId, true, new PaginationRequestParameter(), [])
    notificationLoaded.value = true
  } catch (error) {
    console.error('Error loading user with details:', error)
    notificationLoaded.value = true // Continuer même en cas d'erreur
  }
}
const { playlists, getAllPlaylistsByUserId } = usePlaylists()
const playlistLoaded = ref(false)
const loadPlaylists = async () => {
  try {
    await getAllPlaylistsByUserId(pathId, true, new PaginationRequestParameter(), [])
    playlistLoaded.value = true
  } catch (error) {
    console.error('Error loading user with details:', error)
    playlistLoaded.value = true // Continuer même en cas d'erreur
  }
}
const { reports, getAllReportsByUserId } = useReports()
const reportLoaded = ref(false)
const loadReports = async () => {
  try {
    await getAllReportsByUserId(pathId, true, new PaginationRequestParameter(), [])
    reportLoaded.value = true
  } catch (error) {
    console.error('Error loading user with details:', error)
    reportLoaded.value = true // Continuer même en cas d'erreur
  }
}
const { searchHistorys, getAllSearchHistorysByUserId } = useSearchHistorys()
const searchHistoryLoaded = ref(false)
const loadSearchHistorys = async () => {
  try {
    await getAllSearchHistorysByUserId(pathId, true, new PaginationRequestParameter(), [])
    searchHistoryLoaded.value = true
  } catch (error) {
    console.error('Error loading user with details:', error)
    searchHistoryLoaded.value = true // Continuer même en cas d'erreur
  }
}
const { upNextQueues, getAllUpNextQueuesByUserId } = useUpNextQueues()
const upNextQueueLoaded = ref(false)
const loadUpNextQueues = async () => {
  try {
    await getAllUpNextQueuesByUserId(pathId, true, new PaginationRequestParameter(), [])
    upNextQueueLoaded.value = true
  } catch (error) {
    console.error('Error loading user with details:', error)
    upNextQueueLoaded.value = true // Continuer même en cas d'erreur
  }
}
const { userStatusHistorys, getAllUserStatusHistorysByUserId } = useUserStatusHistorys()
const userStatusHistoryLoaded = ref(false)
const loadUserStatusHistorys = async () => {
  try {
    await getAllUserStatusHistorysByUserId(pathId, true, new PaginationRequestParameter(), [])
    userStatusHistoryLoaded.value = true
  } catch (error) {
    console.error('Error loading user with details:', error)
    userStatusHistoryLoaded.value = true // Continuer même en cas d'erreur
  }
}
const { verificationCodes, getAllVerificationCodesByUserId } = useVerificationCodes()
const verificationCodeLoaded = ref(false)
const loadVerificationCodes = async () => {
  try {
    await getAllVerificationCodesByUserId(pathId, true, new PaginationRequestParameter(), [])
    verificationCodeLoaded.value = true
  } catch (error) {
    console.error('Error loading user with details:', error)
    verificationCodeLoaded.value = true // Continuer même en cas d'erreur
  }
}

const violations = ref<
  | {
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
  | undefined
>(undefined)

const updateHandler = async (payload: {
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
}) => {
  freezeStore.freeze('Updating user ' + pathId + ' ...')
  message.value = null
  violations.value = {
    user: {},
    artists: [],
    clients: [],
    comments: [],
    downloads: [],
    eventMedias: [],
    follows: [],
    likes: [],
    listeningHistorys: [],
    notificationPreferences: [],
    notifications: [],
    playlists: [],
    reports: [],
    searchHistorys: [],
    upNextQueues: [],
    userStatusHistorys: [],
    verificationCodes: [],
  }
  try {
    const { data, errors } = await updateUser(pathId, payload)

    if (errors && Object.keys(errors).length > 0) {
      // Gérer les erreurs de validation
      violations.value = errors as {
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
    }
    if (data && !message.value) {
      viewUser(data)
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
    viewUser(entity.value)
  }
}

onMounted(async () => {
  const result = await getUserById(pathId)
  if (result.data) {
    entity.value = result.data
    await loadArtists()
    await loadClients()
    await loadComments()
    await loadDownloads()
    await loadEventMedias()
    await loadFollows()
    await loadLikes()
    await loadListeningHistorys()
    await loadNotificationPreferences()
    await loadNotifications()
    await loadPlaylists()
    await loadReports()
    await loadSearchHistorys()
    await loadUpNextQueues()
    await loadUserStatusHistorys()
    await loadVerificationCodes()
  } else {
    openPopup()
  }
})
</script>
<style scoped></style>
