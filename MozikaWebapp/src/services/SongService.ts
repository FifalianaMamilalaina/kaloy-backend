import api from '@/services/api'
import { getFilenameFromHeaders, triggerFileDownload } from '@/utils/download.ts'
import RestResponse from '@/models/api/RestResponseModel'
import { type IPageResponse } from '@/models/api/PageResponseModel'
import {
  PaginationRequestParameter,
  RequestModel,
  SortFieldParameter,
} from '@/models/api/RequestModel'
import { Song } from '@/models/SongModel'
import type { DataResponse, PagedDataResponse } from '@/models/api/DataResponseModel'
import type { EditorialPlaylistSong } from '@/models/EditorialPlaylistSongModel'
import type { ListeningHistory } from '@/models/ListeningHistoryModel'
import type { PlaylistSong } from '@/models/PlaylistSongModel'
import type { SongGenre } from '@/models/SongGenreModel'
import type { UpNextQueue } from '@/models/UpNextQueueModel'

const restApiWithPage = api<IPageResponse<Song>>()
const restApi = api<Song>()
const BASE_URL = '/songs'

/** Helper to wrap response data in Song instances */
function wrapSong(response: DataResponse<Song> | PagedDataResponse<Song>) {
  if (Array.isArray(response.data)) {
    response.data = response.data.map((r) => new Song(r))
  } else if (response.data && typeof response.data === 'object') {
    response.data = new Song(response.data)
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
  return wrapSong(RestResponse.handlePagedResponse<Song>(response)) as PagedDataResponse<Song>
}

/** POST search with pagination */
export async function search(
  data: Partial<Song>,
  pagination?: PaginationRequestParameter,
  sortFields?: SortFieldParameter[],
) {
  const request = new RequestModel(BASE_URL + '/search', pagination, sortFields)
  const response = await restApiWithPage.POST(request.buildRequestUrl(), data)
  return wrapSong(RestResponse.handlePagedResponse<Song>(response)) as PagedDataResponse<Song>
}

/** GET by ID */
export async function getById(id: number | string | undefined) {
  if (!id) throw new Error('getById: id is required')
  const response = await restApi.GET(BASE_URL + '/' + id)
  return wrapSong(RestResponse.handleDataResponse<Song>(response)) as DataResponse<Song>
}

/** POST create */
export async function create(data: {
  song: Partial<Song>
  editorialPlaylistSongs: Partial<EditorialPlaylistSong>[]
  listeningHistorys: Partial<ListeningHistory>[]
  playlistSongs: Partial<PlaylistSong>[]
  songGenres: Partial<SongGenre>[]
  upNextQueues: Partial<UpNextQueue>[]
}) {
  const response = await restApi.POST(BASE_URL, data)
  return wrapSong(RestResponse.handleDataResponse<Song>(response)) as DataResponse<Song>
}

/** PUT update */
export async function update(
  id: number | string | undefined,
  data: {
    song: Partial<Song>
    editorialPlaylistSongs: Partial<EditorialPlaylistSong>[]
    listeningHistorys: Partial<ListeningHistory>[]
    playlistSongs: Partial<PlaylistSong>[]
    songGenres: Partial<SongGenre>[]
    upNextQueues: Partial<UpNextQueue>[]
  },
) {
  if (!id) throw new Error('update: id is required')
  const response = await restApi.PUT(BASE_URL + '/' + id, data)
  return wrapSong(RestResponse.handleDataResponse<Song>(response)) as DataResponse<Song>
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
  songs: Partial<Song[]>,
): Promise<{ success: boolean; error: string | null }> {
  const url = import.meta.env.VITE_APP_API_URL + BASE_URL + '/export/csv'

  try {
    const response = await fetch(url, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(songs),
    })
    if (!response.ok) {
      const errorText = await response.text()
      console.error('CSV Export Failed - Server Error:', response.status, errorText)
      throw new Error(`CSV export failed: . Status: .`)
    }
    const blob = await response.blob()
    const contentDisposition = response.headers.get('content-disposition')
    triggerFileDownload(blob, getFilenameFromHeaders(contentDisposition, 'Song.csv'))
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
export async function getAllByArtistId(
  id: number | string | undefined,
  pagination?: PaginationRequestParameter,
  sortFields?: SortFieldParameter[],
) {
  if (!id) throw new Error('getAllByArtistId: id of artist is required')
  const request = new RequestModel('/artists/' + id + BASE_URL, pagination, sortFields)
  const response = await restApiWithPage.GET(request.buildRequestUrl())
  return wrapSong(RestResponse.handlePagedResponse<Song>(response)) as PagedDataResponse<Song>
}
export async function getAllByAlbumId(
  id: number | string | undefined,
  pagination?: PaginationRequestParameter,
  sortFields?: SortFieldParameter[],
) {
  if (!id) throw new Error('getAllByAlbumId: id of album is required')
  const request = new RequestModel('/albums/' + id + BASE_URL, pagination, sortFields)
  const response = await restApiWithPage.GET(request.buildRequestUrl())
  return wrapSong(RestResponse.handlePagedResponse<Song>(response)) as PagedDataResponse<Song>
}
