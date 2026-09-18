import api from '@/services/api'
import { getFilenameFromHeaders, triggerFileDownload } from '@/utils/download.ts'
import RestResponse from '@/models/api/RestResponseModel'
import { type IPageResponse } from '@/models/api/PageResponseModel'
import {
  PaginationRequestParameter,
  RequestModel,
  SortFieldParameter,
} from '@/models/api/RequestModel'
import { Album } from '@/models/AlbumModel'
import type { DataResponse, PagedDataResponse } from '@/models/api/DataResponseModel'
import type { Song } from '@/models/SongModel'

const restApiWithPage = api<IPageResponse<Album>>()
const restApi = api<Album>()
const BASE_URL = '/albums'

/** Helper to wrap response data in Album instances */
function wrapAlbum(response: DataResponse<Album> | PagedDataResponse<Album>) {
  if (Array.isArray(response.data)) {
    response.data = response.data.map((r) => new Album(r))
  } else if (response.data && typeof response.data === 'object') {
    response.data = new Album(response.data)
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
  return wrapAlbum(RestResponse.handlePagedResponse<Album>(response)) as PagedDataResponse<Album>
}

/** POST search with pagination */
export async function search(
  data: Partial<Album>,
  pagination?: PaginationRequestParameter,
  sortFields?: SortFieldParameter[],
) {
  const request = new RequestModel(BASE_URL + '/search', pagination, sortFields)
  const response = await restApiWithPage.POST(request.buildRequestUrl(), data)
  return wrapAlbum(RestResponse.handlePagedResponse<Album>(response)) as PagedDataResponse<Album>
}

/** GET by ID */
export async function getById(id: number | string | undefined) {
  if (!id) throw new Error('getById: id is required')
  const response = await restApi.GET(BASE_URL + '/' + id)
  return wrapAlbum(RestResponse.handleDataResponse<Album>(response)) as DataResponse<Album>
}

/** POST create */
export async function create(data: { album: Partial<Album>; songs: Partial<Song>[] }) {
  const response = await restApi.POST(BASE_URL, data)
  return wrapAlbum(RestResponse.handleDataResponse<Album>(response)) as DataResponse<Album>
}

/** PUT update */
export async function update(
  id: number | string | undefined,
  data: {
    album: Partial<Album>
    songs: Partial<Song>[]
  },
) {
  if (!id) throw new Error('update: id is required')
  const response = await restApi.PUT(BASE_URL + '/' + id, data)
  return wrapAlbum(RestResponse.handleDataResponse<Album>(response)) as DataResponse<Album>
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
  albums: Partial<Album[]>,
): Promise<{ success: boolean; error: string | null }> {
  const url = import.meta.env.VITE_APP_API_URL + BASE_URL + '/export/csv'

  try {
    const response = await fetch(url, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(albums),
    })
    if (!response.ok) {
      const errorText = await response.text()
      console.error('CSV Export Failed - Server Error:', response.status, errorText)
      throw new Error(`CSV export failed: . Status: .`)
    }
    const blob = await response.blob()
    const contentDisposition = response.headers.get('content-disposition')
    triggerFileDownload(blob, getFilenameFromHeaders(contentDisposition, 'Album.csv'))
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
  return wrapAlbum(RestResponse.handlePagedResponse<Album>(response)) as PagedDataResponse<Album>
}
