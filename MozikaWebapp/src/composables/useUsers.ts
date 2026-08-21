import { ref, watch } from 'vue'
import { User, UserFormDTO } from '@/models/UserModel'
import * as userService from '@/services/UserService'
import * as columnConfigService from '@/services/ColumnConfigService'
import { useLoading } from './useLoading'
import { PaginationRequestParameter, SortFieldParameter } from '@/models/api/RequestModel'
import { PaginationData } from '@/models/api/PageResponseModel'
import { useRouter } from 'vue-router'
import { Artist, ArtistFormDTO } from '@/models/ArtistModel'
import { Client, ClientFormDTO } from '@/models/ClientModel'
import { Comment, CommentFormDTO } from '@/models/CommentModel'
import { Download, DownloadFormDTO } from '@/models/DownloadModel'
import { EventMedia, EventMediaFormDTO } from '@/models/EventMediaModel'
import { Follow, FollowFormDTO } from '@/models/FollowModel'
import { Like, LikeFormDTO } from '@/models/LikeModel'
import { ListeningHistory, ListeningHistoryFormDTO } from '@/models/ListeningHistoryModel'
import {
  NotificationPreference,
  NotificationPreferenceFormDTO,
} from '@/models/NotificationPreferenceModel'
import { Notification, NotificationFormDTO } from '@/models/NotificationModel'
import { Playlist, PlaylistFormDTO } from '@/models/PlaylistModel'
import { Report, ReportFormDTO } from '@/models/ReportModel'
import { SearchHistory, SearchHistoryFormDTO } from '@/models/SearchHistoryModel'
import { UpNextQueue, UpNextQueueFormDTO } from '@/models/UpNextQueueModel'
import { UserStatusHistory, UserStatusHistoryFormDTO } from '@/models/UserStatusHistoryModel'
import { VerificationCode, VerificationCodeFormDTO } from '@/models/VerificationCodeModel'

export const DEFAULT_LIST_VIEW_FIELDS = ['id']

/**
 * Composable to manage User entities including CRUD operations,
 * search, pagination, foreign key loading, and navigation.
 */
export function useUsers() {
  /** List of all users */
  const users = ref<User[]>([])

  /** Loading state management */
  const { loading, startLoading, stopLoading } = useLoading()

  /** Pagination information returned by API */
  const paginationData = ref(new PaginationData())

  /** Message for errors or notifications */
  const message = ref<string | null>(null)

  /** Vue router instance for navigation */
  const router = useRouter()

  const visibleListFields = ref<string[]>([...DEFAULT_LIST_VIEW_FIELDS])

  // On utilise une clé spécifique à l'entité pour plus de flexibilité future
  const savedLayoutMode = localStorage.getItem('layoutMode_user') as 'list' | 'card' | null
  const layoutMode = ref<'list' | 'card'>(savedLayoutMode || 'list')

  watch(layoutMode, (newValue) => {
    localStorage.setItem('layoutMode_user', newValue)
  })

  /**
   * Handles API response by updating the users and pagination
   * @param data Array of User returned by the API
   * @param err Optional error message
   * @param pagination Optional pagination data
   */
  const handleResponse = (
    data: User[],
    err: string | undefined,
    pagination?: PaginationData,
    concatData: boolean = false,
  ) => {
    if (err) {
      message.value = err
    }
    if (concatData) {
      users.value = users.value.concat(data)
    } else {
      users.value = data
    }
    if (pagination !== undefined) {
      paginationData.value = new PaginationData(pagination)
    }
  }

  const loadColumnConfig = async () => {
    try {
      const response = await columnConfigService.getVisibleFields('user', 'list')
      if (response.returnCode === 1 && response.data && response.data.length > 0) {
        visibleListFields.value = response.data
      }
    } catch (error) {
      console.warn(
        'Impossible de charger la config des colonnes, utilisation des valeurs par défaut.',
        error,
      )
    }
  }

  /** Sauvegarde la nouvelle configuration vers le serveur */
  const saveColumnConfig = async (fields: string[]) => {
    try {
      const response = await columnConfigService.updateVisibleFields('user', 'list', fields)
      if (response.returnCode === 1) {
        visibleListFields.value = fields
        return { success: true, error: null }
      } else {
        return { success: false, error: response.message || 'Erreur lors de la sauvegarde' }
      }
    } catch (error) {
      return { success: false, error: 'Erreur réseau lors de la sauvegarde' }
    }
  }

  // Navigation functions
  /** Navigate to user detail view */
  const viewUser = (user: User) => {
    router.push({ name: 'userdetailsview', params: { id: user.getKeyValue() } })
  }

  /** Navigate to user list view */
  const goToListView = () => {
    router.push({ path: '/users' })
  }

  /** Navigate to user create form */
  const goToCreateFormView = () => {
    router.push({ path: '/users/create' })
  }

  /** Navigate to user update form */
  const goToUpdateFormView = (user: User) => {
    router.push({ name: 'userupdateview', params: { id: user.getKeyValue() } })
  }

  /**
   * Delete a user by id
   * @param user User object to delete
   */
  const deleteUser = async (user: User) => {
    const result = await userService.remove(user.getKeyValue())
    if (result.error) {
      message.value = result.error
    }
    return result
  }

  /**
   * Create a new user
   * @param fullData Object containing user and his details
   */
  const createUser = async (fullData: {
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
    const dto = new UserFormDTO(fullData.user)
    const user: Partial<User> = await dto.toEntity()
    const artists: Partial<Artist>[] = await ArtistFormDTO.toEntities(fullData.artists)
    const clients: Partial<Client>[] = await ClientFormDTO.toEntities(fullData.clients)
    const comments: Partial<Comment>[] = await CommentFormDTO.toEntities(fullData.comments)
    const downloads: Partial<Download>[] = await DownloadFormDTO.toEntities(fullData.downloads)
    const eventMedias: Partial<EventMedia>[] = await EventMediaFormDTO.toEntities(
      fullData.eventMedias,
    )
    const follows: Partial<Follow>[] = await FollowFormDTO.toEntities(fullData.follows)
    const likes: Partial<Like>[] = await LikeFormDTO.toEntities(fullData.likes)
    const listeningHistorys: Partial<ListeningHistory>[] = await ListeningHistoryFormDTO.toEntities(
      fullData.listeningHistorys,
    )
    const notificationPreferences: Partial<NotificationPreference>[] =
      await NotificationPreferenceFormDTO.toEntities(fullData.notificationPreferences)
    const notifications: Partial<Notification>[] = await NotificationFormDTO.toEntities(
      fullData.notifications,
    )
    const playlists: Partial<Playlist>[] = await PlaylistFormDTO.toEntities(fullData.playlists)
    const reports: Partial<Report>[] = await ReportFormDTO.toEntities(fullData.reports)
    const searchHistorys: Partial<SearchHistory>[] = await SearchHistoryFormDTO.toEntities(
      fullData.searchHistorys,
    )
    const upNextQueues: Partial<UpNextQueue>[] = await UpNextQueueFormDTO.toEntities(
      fullData.upNextQueues,
    )
    const userStatusHistorys: Partial<UserStatusHistory>[] =
      await UserStatusHistoryFormDTO.toEntities(fullData.userStatusHistorys)
    const verificationCodes: Partial<VerificationCode>[] = await VerificationCodeFormDTO.toEntities(
      fullData.verificationCodes,
    )

    const { data, error, errors } = await userService.create({
      user,
      artists,
      clients,
      comments,
      downloads,
      eventMedias,
      follows,
      likes,
      listeningHistorys,
      notificationPreferences,
      notifications,
      playlists,
      reports,
      searchHistorys,
      upNextQueues,
      userStatusHistorys,
      verificationCodes,
    })
    if (error) {
      message.value = error
    }
    return { data, errors }
  }

  /**
   * Update an existing user with full details
   * @param id User ID to update
   * @param fullData Object containing user and its details
   */
  const updateUser = async (
    id: number | string,
    fullData: {
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
  ) => {
    const dto = new UserFormDTO(fullData.user)
    const user: Partial<User> = await dto.toEntity()

    const artists: Partial<Artist>[] = await ArtistFormDTO.toEntities(fullData.artists)
    const clients: Partial<Client>[] = await ClientFormDTO.toEntities(fullData.clients)
    const comments: Partial<Comment>[] = await CommentFormDTO.toEntities(fullData.comments)
    const downloads: Partial<Download>[] = await DownloadFormDTO.toEntities(fullData.downloads)
    const eventMedias: Partial<EventMedia>[] = await EventMediaFormDTO.toEntities(
      fullData.eventMedias,
    )
    const follows: Partial<Follow>[] = await FollowFormDTO.toEntities(fullData.follows)
    const likes: Partial<Like>[] = await LikeFormDTO.toEntities(fullData.likes)
    const listeningHistorys: Partial<ListeningHistory>[] = await ListeningHistoryFormDTO.toEntities(
      fullData.listeningHistorys,
    )
    const notificationPreferences: Partial<NotificationPreference>[] =
      await NotificationPreferenceFormDTO.toEntities(fullData.notificationPreferences)
    const notifications: Partial<Notification>[] = await NotificationFormDTO.toEntities(
      fullData.notifications,
    )
    const playlists: Partial<Playlist>[] = await PlaylistFormDTO.toEntities(fullData.playlists)
    const reports: Partial<Report>[] = await ReportFormDTO.toEntities(fullData.reports)
    const searchHistorys: Partial<SearchHistory>[] = await SearchHistoryFormDTO.toEntities(
      fullData.searchHistorys,
    )
    const upNextQueues: Partial<UpNextQueue>[] = await UpNextQueueFormDTO.toEntities(
      fullData.upNextQueues,
    )
    const userStatusHistorys: Partial<UserStatusHistory>[] =
      await UserStatusHistoryFormDTO.toEntities(fullData.userStatusHistorys)
    const verificationCodes: Partial<VerificationCode>[] = await VerificationCodeFormDTO.toEntities(
      fullData.verificationCodes,
    )

    const { data, error, errors } = await userService.update(id, {
      user,
      artists,
      clients,
      comments,
      downloads,
      eventMedias,
      follows,
      likes,
      listeningHistorys,
      notificationPreferences,
      notifications,
      playlists,
      reports,
      searchHistorys,
      upNextQueues,
      userStatusHistorys,
      verificationCodes,
    })
    if (error) {
      message.value = error
    }
    return { data, errors }
  }

  // API Requests
  /** Load all users from the API */
  const loadUsers = async (
    unpagined: boolean = false,
    pagination?: PaginationRequestParameter,
    sortFields?: SortFieldParameter[],
  ): Promise<void> => {
    message.value = null
    startLoading()
    const {
      data,
      error: err,
      pagination: paginationresult,
    } = await userService.getAll(pagination, sortFields)
    handleResponse(data, err, paginationresult)

    if (unpagined && users.value.length > 0) {
      while (paginationData.value.hasNext()) {
        paginationData.value.nextPage()
        const {
          data: nextData,
          error: nextErr,
          pagination: nextPagination,
        } = await userService.getAll(paginationData.value.toParameter(), sortFields)
        handleResponse(nextData, nextErr, nextPagination, true)
      }
    }
    stopLoading()
  }

  /**
   * Get a single user by id
   * @param id User ID
   */
  const getUserById = async (id: number | string) => {
    const result = await userService.getById(id)
    if (result.error) {
      message.value = result.error
    }
    return result
  }

  /**
   * Search users with filters, pagination, and sorting
   * @param filters Partial User object containing search filters
   * @param pagination Pagination request parameters
   * @param sortFields Array of fields to sort
   */
  const searchUsers = async (
    unpagined: boolean = false,
    filters: Partial<User>,
    pagination: PaginationRequestParameter,
    sortFields: SortFieldParameter[],
  ) => {
    message.value = null
    startLoading()
    const {
      data,
      error: err,
      pagination: paginationresult,
    } = await userService.search(filters, pagination, sortFields)
    handleResponse(data, err, paginationresult)

    if (unpagined) {
      while (paginationData.value.hasNext()) {
        paginationData.value.nextPage()
        const {
          data: nextData,
          error: nextErr,
          pagination: nextPagination,
        } = await userService.search(filters, paginationData.value.toParameter(), sortFields)
        handleResponse(nextData, nextErr, nextPagination, true)
      }
    }
    stopLoading()
  }
  const exportUsersToCsv = async (data: Partial<User[]>) => {
    console.log('Exporting User to CSV', data)
    await userService.exportCsv(data)
  }

  /** Getter for pagination data */
  const getPaginationData = () => paginationData.value

  return {
    // state
    users,
    loading,
    message,
    paginationData,
    layoutMode,

    // State & function of column configuration
    visibleListFields,
    loadColumnConfig,
    saveColumnConfig,

    // Actions
    getPaginationData,
    exportUsersToCsv,
    loadUsers,
    searchUsers,
    getUserById,
    viewUser,
    goToListView,
    goToCreateFormView,
    goToUpdateFormView,
    deleteUser,
    createUser,
    updateUser,
  }
}
