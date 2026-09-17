import api from '@/services/api'
import { getFilenameFromHeaders, triggerFileDownload } from '@/utils/download.ts'
import RestResponse from '@/models/api/RestResponseModel'
import { type IPageResponse } from '@/models/api/PageResponseModel'
import {
  PaginationRequestParameter,
  RequestModel,
  SortFieldParameter,
} from '@/models/api/RequestModel'
import { User } from '@/models/UserModel'
import type { DataResponse, PagedDataResponse } from '@/models/api/DataResponseModel'
import type { Artist } from '@/models/ArtistModel'
import type { Client } from '@/models/ClientModel'
import type { Comment } from '@/models/CommentModel'
import type { Download } from '@/models/DownloadModel'
import type { EventMedia } from '@/models/EventMediaModel'
import type { Follow } from '@/models/FollowModel'
import type { Like } from '@/models/LikeModel'
import type { ListeningHistory } from '@/models/ListeningHistoryModel'
import type { NotificationPreference } from '@/models/NotificationPreferenceModel'
import type { Notification } from '@/models/NotificationModel'
import type { Playlist } from '@/models/PlaylistModel'
import type { Report } from '@/models/ReportModel'
import type { SearchHistory } from '@/models/SearchHistoryModel'
import type { UpNextQueue } from '@/models/UpNextQueueModel'
import type { UserStatusHistory } from '@/models/UserStatusHistoryModel'
import type { VerificationCode } from '@/models/VerificationCodeModel'

const restApiWithPage = api<IPageResponse<User>>()
const restApi = api<User>()
const BASE_URL = '/users'

/** Helper to wrap response data in User instances */
function wrapUser(response: DataResponse<User> | PagedDataResponse<User>) {
  if (Array.isArray(response.data)) {
    response.data = response.data.map((r) => new User(r))
  } else if (response.data && typeof response.data === 'object') {
    response.data = new User(response.data)
  }
  return response
}

/** GET all with pagination */
export async function getAll(
  pagination?: PaginationRequestParameter,
  sortFields?: SortFieldParameter[],
) {
  const request = new RequestModel(BASE_URL, pagination, sortFields)
  const response = await restApiWithPage.GET(request.buildRequestUrl())
  return wrapUser(RestResponse.handlePagedResponse<User>(response)) as PagedDataResponse<User>
}

/** POST search with pagination */
export async function search(
  data: Partial<User>,
  pagination?: PaginationRequestParameter,
  sortFields?: SortFieldParameter[],
) {
  const request = new RequestModel(BASE_URL + '/search', pagination, sortFields)
  const response = await restApiWithPage.POST(request.buildRequestUrl(), data)
  return wrapUser(RestResponse.handlePagedResponse<User>(response)) as PagedDataResponse<User>
}

/** GET by ID */
export async function getById(id: number | string | undefined) {
  if (!id) throw new Error('getById: id is required')
  const response = await restApi.GET(BASE_URL + '/' + id)
  return wrapUser(RestResponse.handleDataResponse<User>(response)) as DataResponse<User>
}

/** POST create */
export async function create(data: {
  user: Partial<User>
  artists: Partial<Artist>[]
  clients: Partial<Client>[]
  comments: Partial<Comment>[]
  downloads: Partial<Download>[]
  eventMedias: Partial<EventMedia>[]
  follows: Partial<Follow>[]
  likes: Partial<Like>[]
  listeningHistorys: Partial<ListeningHistory>[]
  notificationPreferences: Partial<NotificationPreference>[]
  notifications: Partial<Notification>[]
  playlists: Partial<Playlist>[]
  reports: Partial<Report>[]
  searchHistorys: Partial<SearchHistory>[]
  upNextQueues: Partial<UpNextQueue>[]
  userStatusHistorys: Partial<UserStatusHistory>[]
  verificationCodes: Partial<VerificationCode>[]
}) {
  const response = await restApi.POST(BASE_URL, data)
  return wrapUser(RestResponse.handleDataResponse<User>(response)) as DataResponse<User>
}

/** PUT update */
export async function update(
  id: number | string | undefined,
  data: {
    user: Partial<User>
    artists: Partial<Artist>[]
    clients: Partial<Client>[]
    comments: Partial<Comment>[]
    downloads: Partial<Download>[]
    eventMedias: Partial<EventMedia>[]
    follows: Partial<Follow>[]
    likes: Partial<Like>[]
    listeningHistorys: Partial<ListeningHistory>[]
    notificationPreferences: Partial<NotificationPreference>[]
    notifications: Partial<Notification>[]
    playlists: Partial<Playlist>[]
    reports: Partial<Report>[]
    searchHistorys: Partial<SearchHistory>[]
    upNextQueues: Partial<UpNextQueue>[]
    userStatusHistorys: Partial<UserStatusHistory>[]
    verificationCodes: Partial<VerificationCode>[]
  },
) {
  if (!id) throw new Error('update: id is required')
  const response = await restApi.PUT(BASE_URL + '/' + id, data)
  return wrapUser(RestResponse.handleDataResponse<User>(response)) as DataResponse<User>
}

/** DELETE remove */
export async function remove(id: number | string | undefined) {
  if (!id) throw new Error('remove: id is required')
  const response = await restApi.DELETE(BASE_URL + '/' + id)
  return {
    success: response.returnCode === 1,
    error: response.returnCode !== 1 ? response.message : null,
  }
}
export async function exportCsv(
  users: Partial<User[]>,
): Promise<{ success: boolean; error: string | null }> {
  const url = import.meta.env.VITE_APP_API_URL + BASE_URL + '/export/csv'

  try {
    const response = await fetch(url, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(users),
    })
    if (!response.ok) {
      const errorText = await response.text()
      console.error('CSV Export Failed - Server Error:', response.status, errorText)
      throw new Error(`CSV export failed: . Status: .`)
    }
    const blob = await response.blob()
    const contentDisposition = response.headers.get('content-disposition')
    triggerFileDownload(blob, getFilenameFromHeaders(contentDisposition, 'User.csv'))
    return {
      success: true,
      error: null,
    }
  } catch (error) {
    console.error('Error during CSV export:', error)
    return {
      success: false,
      error: error instanceof Error ? error.message : 'An unknown error occurred during export.',
    }
  }
}
