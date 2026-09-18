import api from '@/services/api'
import { getFilenameFromHeaders, triggerFileDownload } from '@/utils/download.ts'
import RestResponse from '@/models/api/RestResponseModel'
import { type IPageResponse } from '@/models/api/PageResponseModel'
import {
  PaginationRequestParameter,
  RequestModel,
  SortFieldParameter,
} from '@/models/api/RequestModel'
import { Artist } from '@/models/ArtistModel'
import type { DataResponse, PagedDataResponse } from '@/models/api/DataResponseModel'
import type { Album } from '@/models/AlbumModel'
import type { ArtistGroupMember } from '@/models/ArtistGroupMemberModel'
import type { Concert } from '@/models/ConcertModel'
import type { ContentSubmission } from '@/models/ContentSubmissionModel'
import type { EditorialPlaylist } from '@/models/EditorialPlaylistModel'
import type { Event } from '@/models/EventModel'
import type { Follow } from '@/models/FollowModel'
import type { Song } from '@/models/SongModel'

const restApiWithPage = api<IPageResponse<Artist>>()
const restApi = api<Artist>()
const BASE_URL = '/artists'

/** Helper to wrap response data in Artist instances */
function wrapArtist(response: DataResponse<Artist> | PagedDataResponse<Artist>) {
  if (Array.isArray(response.data)) {
    response.data = response.data.map((r) => new Artist(r))
  } else if (response.data && typeof response.data === 'object') {
    response.data = new Artist(response.data)
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
  return wrapArtist(RestResponse.handlePagedResponse<Artist>(response)) as PagedDataResponse<Artist>
}

/** POST search with pagination */
export async function search(
  data: Partial<Artist>,
  pagination?: PaginationRequestParameter,
  sortFields?: SortFieldParameter[],
) {
  const request = new RequestModel(BASE_URL + '/search', pagination, sortFields)
  const response = await restApiWithPage.POST(request.buildRequestUrl(), data)
  return wrapArtist(RestResponse.handlePagedResponse<Artist>(response)) as PagedDataResponse<Artist>
}

/** GET by ID */
export async function getById(id: number | string | undefined) {
  if (!id) throw new Error('getById: id is required')
  const response = await restApi.GET(BASE_URL + '/' + id)
  return wrapArtist(RestResponse.handleDataResponse<Artist>(response)) as DataResponse<Artist>
}

/** POST create */
export async function create(data: {
  artist: Partial<Artist>
  albums: Partial<Album>[]
  artistGroupMembers: Partial<ArtistGroupMember>[]
  concerts: Partial<Concert>[]
  contentSubmissions: Partial<ContentSubmission>[]
  editorialPlaylists: Partial<EditorialPlaylist>[]
  events: Partial<Event>[]
  follows: Partial<Follow>[]
  songs: Partial<Song>[]
}) {
  const response = await restApi.POST(BASE_URL, data)
  return wrapArtist(RestResponse.handleDataResponse<Artist>(response)) as DataResponse<Artist>
}

/** PUT update */
export async function update(
  id: number | string | undefined,
  data: {
    artist: Partial<Artist>
    albums: Partial<Album>[]
    artistGroupMembers: Partial<ArtistGroupMember>[]
    concerts: Partial<Concert>[]
    contentSubmissions: Partial<ContentSubmission>[]
    editorialPlaylists: Partial<EditorialPlaylist>[]
    events: Partial<Event>[]
    follows: Partial<Follow>[]
    songs: Partial<Song>[]
  },
) {
  if (!id) throw new Error('update: id is required')
  const response = await restApi.PUT(BASE_URL + '/' + id, data)
  return wrapArtist(RestResponse.handleDataResponse<Artist>(response)) as DataResponse<Artist>
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
  artists: Partial<Artist[]>,
): Promise<{ success: boolean; error: string | null }> {
  const url = import.meta.env.VITE_APP_API_URL + BASE_URL + '/export/csv'

  try {
    const response = await fetch(url, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(artists),
    })
    if (!response.ok) {
      const errorText = await response.text()
      console.error('CSV Export Failed - Server Error:', response.status, errorText)
      throw new Error(`CSV export failed: . Status: .`)
    }
    const blob = await response.blob()
    const contentDisposition = response.headers.get('content-disposition')
    triggerFileDownload(blob, getFilenameFromHeaders(contentDisposition, 'Artist.csv'))
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
export async function getAllByUserId(
  id: number | string | undefined,
  pagination?: PaginationRequestParameter,
  sortFields?: SortFieldParameter[],
) {
  if (!id) throw new Error('getAllByUserId: id of user is required')
  const request = new RequestModel('/users/' + id + BASE_URL, pagination, sortFields)
  const response = await restApiWithPage.GET(request.buildRequestUrl())
  return wrapArtist(RestResponse.handlePagedResponse<Artist>(response)) as PagedDataResponse<Artist>
}
