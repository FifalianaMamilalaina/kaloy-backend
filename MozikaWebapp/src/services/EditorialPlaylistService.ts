import api from '@/services/api'
import { getFilenameFromHeaders, triggerFileDownload } from '@/utils/download.ts'
import RestResponse from '@/models/api/RestResponseModel'
import { type IPageResponse } from '@/models/api/PageResponseModel'
import {
  PaginationRequestParameter,
  RequestModel,
  SortFieldParameter,
} from '@/models/api/RequestModel'
import { EditorialPlaylist } from '@/models/EditorialPlaylistModel'
import type { DataResponse, PagedDataResponse } from '@/models/api/DataResponseModel'

const restApiWithPage = api<IPageResponse<EditorialPlaylist>>()
const restApi = api<EditorialPlaylist>()
const BASE_URL = '/editorialplaylists'

/** Helper to wrap response data in EditorialPlaylist instances */
function wrapEditorialPlaylist(
  response: DataResponse<EditorialPlaylist> | PagedDataResponse<EditorialPlaylist>,
) {
  if (Array.isArray(response.data)) {
    response.data = response.data.map((r) => new EditorialPlaylist(r))
  } else if (response.data && typeof response.data === 'object') {
    response.data = new EditorialPlaylist(response.data)
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
  return wrapEditorialPlaylist(
    RestResponse.handlePagedResponse<EditorialPlaylist>(response),
  ) as PagedDataResponse<EditorialPlaylist>
}

/** POST search with pagination */
export async function search(
  data: Partial<EditorialPlaylist>,
  pagination?: PaginationRequestParameter,
  sortFields?: SortFieldParameter[],
) {
  const request = new RequestModel(BASE_URL + '/search', pagination, sortFields)
  const response = await restApiWithPage.POST(request.buildRequestUrl(), data)
  return wrapEditorialPlaylist(
    RestResponse.handlePagedResponse<EditorialPlaylist>(response),
  ) as PagedDataResponse<EditorialPlaylist>
}

/** GET by ID */
export async function getById(id: number | string | undefined) {
  if (!id) throw new Error('getById: id is required')
  const response = await restApi.GET(BASE_URL + '/' + id)
  return wrapEditorialPlaylist(
    RestResponse.handleDataResponse<EditorialPlaylist>(response),
  ) as DataResponse<EditorialPlaylist>
}

export async function create(data: Partial<EditorialPlaylist>) {
  const response = await restApi.POST(BASE_URL, data)
  return wrapEditorialPlaylist(
    RestResponse.handleDataResponse<EditorialPlaylist>(response),
  ) as DataResponse<EditorialPlaylist>
}

/** PUT update */
export async function update(id: number | string | undefined, data: Partial<EditorialPlaylist>) {
  if (!id) throw new Error('update: id is required')
  const response = await restApi.PUT(BASE_URL + '/' + id, data)
  return wrapEditorialPlaylist(
    RestResponse.handleDataResponse<EditorialPlaylist>(response),
  ) as DataResponse<EditorialPlaylist>
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
  editorialPlaylists: Partial<EditorialPlaylist[]>,
): Promise<{ success: boolean; error: string | null }> {
  const url = import.meta.env.VITE_APP_API_URL + BASE_URL + '/export/csv'

  try {
    const response = await fetch(url, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(editorialPlaylists),
    })
    if (!response.ok) {
      const errorText = await response.text()
      console.error('CSV Export Failed - Server Error:', response.status, errorText)
      throw new Error(`CSV export failed: . Status: .`)
    }
    const blob = await response.blob()
    const contentDisposition = response.headers.get('content-disposition')
    triggerFileDownload(blob, getFilenameFromHeaders(contentDisposition, 'EditorialPlaylist.csv'))
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
  return wrapEditorialPlaylist(
    RestResponse.handlePagedResponse<EditorialPlaylist>(response),
  ) as PagedDataResponse<EditorialPlaylist>
}
