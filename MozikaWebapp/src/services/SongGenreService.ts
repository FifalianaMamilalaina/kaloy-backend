import api from '@/services/api'
import { getFilenameFromHeaders, triggerFileDownload } from '@/utils/download.ts'
import RestResponse from '@/models/api/RestResponseModel'
import { type IPageResponse } from '@/models/api/PageResponseModel'
import {
  PaginationRequestParameter,
  RequestModel,
  SortFieldParameter,
} from '@/models/api/RequestModel'
import { SongGenre } from '@/models/SongGenreModel'
import type { DataResponse, PagedDataResponse } from '@/models/api/DataResponseModel'

const restApiWithPage = api<IPageResponse<SongGenre>>()
const restApi = api<SongGenre>()
const BASE_URL = '/songgenres'

/** Helper to wrap response data in SongGenre instances */
function wrapSongGenre(response: DataResponse<SongGenre> | PagedDataResponse<SongGenre>) {
  if (Array.isArray(response.data)) {
    response.data = response.data.map((r) => new SongGenre(r))
  } else if (response.data && typeof response.data === 'object') {
    response.data = new SongGenre(response.data)
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
  return wrapSongGenre(
    RestResponse.handlePagedResponse<SongGenre>(response),
  ) as PagedDataResponse<SongGenre>
}

/** POST search with pagination */
export async function search(
  data: Partial<SongGenre>,
  pagination?: PaginationRequestParameter,
  sortFields?: SortFieldParameter[],
) {
  const request = new RequestModel(BASE_URL + '/search', pagination, sortFields)
  const response = await restApiWithPage.POST(request.buildRequestUrl(), data)
  return wrapSongGenre(
    RestResponse.handlePagedResponse<SongGenre>(response),
  ) as PagedDataResponse<SongGenre>
}

/** GET by ID */
export async function getById(id: number | string | undefined) {
  if (!id) throw new Error('getById: id is required')
  const response = await restApi.GET(BASE_URL + '/' + id)
  return wrapSongGenre(
    RestResponse.handleDataResponse<SongGenre>(response),
  ) as DataResponse<SongGenre>
}

export async function create(data: Partial<SongGenre>) {
  const response = await restApi.POST(BASE_URL, data)
  return wrapSongGenre(
    RestResponse.handleDataResponse<SongGenre>(response),
  ) as DataResponse<SongGenre>
}

/** PUT update */
export async function update(id: number | string | undefined, data: Partial<SongGenre>) {
  if (!id) throw new Error('update: id is required')
  const response = await restApi.PUT(BASE_URL + '/' + id, data)
  return wrapSongGenre(
    RestResponse.handleDataResponse<SongGenre>(response),
  ) as DataResponse<SongGenre>
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
  songGenres: Partial<SongGenre[]>,
): Promise<{ success: boolean; error: string | null }> {
  const url = import.meta.env.VITE_APP_API_URL + BASE_URL + '/export/csv'

  try {
    const response = await fetch(url, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(songGenres),
    })
    if (!response.ok) {
      const errorText = await response.text()
      console.error('CSV Export Failed - Server Error:', response.status, errorText)
      throw new Error(`CSV export failed: . Status: .`)
    }
    const blob = await response.blob()
    const contentDisposition = response.headers.get('content-disposition')
    triggerFileDownload(blob, getFilenameFromHeaders(contentDisposition, 'SongGenre.csv'))
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
export async function getAllBySongId(
  id: number | string | undefined,
  pagination?: PaginationRequestParameter,
  sortFields?: SortFieldParameter[],
) {
  if (!id) throw new Error('getAllBySongId: id of song is required')
  const request = new RequestModel('/songs/' + id + BASE_URL, pagination, sortFields)
  const response = await restApiWithPage.GET(request.buildRequestUrl())
  return wrapSongGenre(
    RestResponse.handlePagedResponse<SongGenre>(response),
  ) as PagedDataResponse<SongGenre>
}
export async function getAllByGenreId(
  id: number | string | undefined,
  pagination?: PaginationRequestParameter,
  sortFields?: SortFieldParameter[],
) {
  if (!id) throw new Error('getAllByGenreId: id of genre is required')
  const request = new RequestModel('/genres/' + id + BASE_URL, pagination, sortFields)
  const response = await restApiWithPage.GET(request.buildRequestUrl())
  return wrapSongGenre(
    RestResponse.handlePagedResponse<SongGenre>(response),
  ) as PagedDataResponse<SongGenre>
}
