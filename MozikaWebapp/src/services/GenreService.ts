import api from '@/services/api'
import { getFilenameFromHeaders, triggerFileDownload } from '@/utils/download.ts'
import RestResponse from '@/models/api/RestResponseModel'
import { type IPageResponse } from '@/models/api/PageResponseModel'
import {
  PaginationRequestParameter,
  RequestModel,
  SortFieldParameter,
} from '@/models/api/RequestModel'
import { Genre } from '@/models/GenreModel'
import type { DataResponse, PagedDataResponse } from '@/models/api/DataResponseModel'
import type { SongGenre } from '@/models/SongGenreModel'

const restApiWithPage = api<IPageResponse<Genre>>()
const restApi = api<Genre>()
const BASE_URL = '/genres'

/** Helper to wrap response data in Genre instances */
function wrapGenre(response: DataResponse<Genre> | PagedDataResponse<Genre>) {
  if (Array.isArray(response.data)) {
    response.data = response.data.map((r) => new Genre(r))
  } else if (response.data && typeof response.data === 'object') {
    response.data = new Genre(response.data)
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
  return wrapGenre(RestResponse.handlePagedResponse<Genre>(response)) as PagedDataResponse<Genre>
}

/** POST search with pagination */
export async function search(
  data: Partial<Genre>,
  pagination?: PaginationRequestParameter,
  sortFields?: SortFieldParameter[],
) {
  const request = new RequestModel(BASE_URL + '/search', pagination, sortFields)
  const response = await restApiWithPage.POST(request.buildRequestUrl(), data)
  return wrapGenre(RestResponse.handlePagedResponse<Genre>(response)) as PagedDataResponse<Genre>
}

/** GET by ID */
export async function getById(id: number | string | undefined) {
  if (!id) throw new Error('getById: id is required')
  const response = await restApi.GET(BASE_URL + '/' + id)
  return wrapGenre(RestResponse.handleDataResponse<Genre>(response)) as DataResponse<Genre>
}

/** POST create */
export async function create(data: { genre: Partial<Genre>; songGenres: Partial<SongGenre>[] }) {
  const response = await restApi.POST(BASE_URL, data)
  return wrapGenre(RestResponse.handleDataResponse<Genre>(response)) as DataResponse<Genre>
}

/** PUT update */
export async function update(
  id: number | string | undefined,
  data: {
    genre: Partial<Genre>
    songGenres: Partial<SongGenre>[]
  },
) {
  if (!id) throw new Error('update: id is required')
  const response = await restApi.PUT(BASE_URL + '/' + id, data)
  return wrapGenre(RestResponse.handleDataResponse<Genre>(response)) as DataResponse<Genre>
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
  genres: Partial<Genre[]>,
): Promise<{ success: boolean; error: string | null }> {
  const url = import.meta.env.VITE_APP_API_URL + BASE_URL + '/export/csv'

  try {
    const response = await fetch(url, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(genres),
    })
    if (!response.ok) {
      const errorText = await response.text()
      console.error('CSV Export Failed - Server Error:', response.status, errorText)
      throw new Error(`CSV export failed: . Status: .`)
    }
    const blob = await response.blob()
    const contentDisposition = response.headers.get('content-disposition')
    triggerFileDownload(blob, getFilenameFromHeaders(contentDisposition, 'Genre.csv'))
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
