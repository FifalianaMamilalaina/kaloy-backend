<template>
  <div class="w-full">
    <div class="flex items-center justify-between mb-6">
      <h3 class="text-xl font-semibold text-base-content">
        {{ $t('entity.nav', { entity: 'User' }) }}
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

    <user-form
      :submit-label="$t('entity.create.submitLabel', { entity: 'User' })"
      :artistsData="initialArtists"
      :clientsData="initialClients"
      :commentsData="initialComments"
      :downloadsData="initialDownloads"
      :eventMediasData="initialEventMedias"
      :followsData="initialFollows"
      :likesData="initialLikes"
      :listeningHistorysData="initialListeningHistorys"
      :notificationPreferencesData="initialNotificationPreferences"
      :notificationsData="initialNotifications"
      :playlistsData="initialPlaylists"
      :reportsData="initialReports"
      :searchHistorysData="initialSearchHistorys"
      :upNextQueuesData="initialUpNextQueues"
      :userStatusHistorysData="initialUserStatusHistorys"
      :verificationCodesData="initialVerificationCodes"
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
import UserForm from '@/entities/user/UserForm.vue'
import AlertPopup from '@/components/popup/AlertPopup.vue'
import GenesisButton from '@/components/button/GenesisButton.vue'
import LeftArrowIcon from '@/components/icons/LeftArrowIcon.vue'
import { UserFormDTO } from '@/models/UserModel'
import { useUsers } from '@/composables/useUsers'
import { usePopup } from '@/composables/usePopup'
import { useFreezeScreenStore } from '@/stores/useFreezeScreenStore'
import { ref } from 'vue'
import type { Artist, ArtistFormDTO } from '@/models/ArtistModel'
import type { Client, ClientFormDTO } from '@/models/ClientModel'
import type { Comment, CommentFormDTO } from '@/models/CommentModel'
import type { Download, DownloadFormDTO } from '@/models/DownloadModel'
import type { EventMedia, EventMediaFormDTO } from '@/models/EventMediaModel'
import type { Follow, FollowFormDTO } from '@/models/FollowModel'
import type { Like, LikeFormDTO } from '@/models/LikeModel'
import type { ListeningHistory, ListeningHistoryFormDTO } from '@/models/ListeningHistoryModel'
import type {
  NotificationPreference,
  NotificationPreferenceFormDTO,
} from '@/models/NotificationPreferenceModel'
import type { Notification, NotificationFormDTO } from '@/models/NotificationModel'
import type { Playlist, PlaylistFormDTO } from '@/models/PlaylistModel'
import type { Report, ReportFormDTO } from '@/models/ReportModel'
import type { SearchHistory, SearchHistoryFormDTO } from '@/models/SearchHistoryModel'
import type { UpNextQueue, UpNextQueueFormDTO } from '@/models/UpNextQueueModel'
import type { UserStatusHistory, UserStatusHistoryFormDTO } from '@/models/UserStatusHistoryModel'
import type { VerificationCode, VerificationCodeFormDTO } from '@/models/VerificationCodeModel'

const { createUser, goToListView, message, viewUser } = useUsers()
const { openPopup, closePopup, visible: alertPopup } = usePopup()
const freezeStore = useFreezeScreenStore()
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
const initialArtists = ref<Artist[]>([])
const initialClients = ref<Client[]>([])
const initialComments = ref<Comment[]>([])
const initialDownloads = ref<Download[]>([])
const initialEventMedias = ref<EventMedia[]>([])
const initialFollows = ref<Follow[]>([])
const initialLikes = ref<Like[]>([])
const initialListeningHistorys = ref<ListeningHistory[]>([])
const initialNotificationPreferences = ref<NotificationPreference[]>([])
const initialNotifications = ref<Notification[]>([])
const initialPlaylists = ref<Playlist[]>([])
const initialReports = ref<Report[]>([])
const initialSearchHistorys = ref<SearchHistory[]>([])
const initialUpNextQueues = ref<UpNextQueue[]>([])
const initialUserStatusHistorys = ref<UserStatusHistory[]>([])
const initialVerificationCodes = ref<VerificationCode[]>([])

const createHandler = async (payload: {
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
  freezeStore.freeze('Creating a new User ...')
  violations.value = undefined
  message.value = null
  try {
    const { data, errors } = await createUser(payload)
    if (errors && Object.keys(errors).length > 0) {
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
    console.error('Error creating user:', error)
    openPopup()
  } finally {
    freezeStore.unfreeze()
  }
}
</script>
<style scoped></style>
