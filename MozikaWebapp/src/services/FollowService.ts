import api from '@/services/api'
import { getFilenameFromHeaders, triggerFileDownload } from '@/utils/download.ts'
import RestResponse from '@/models/api/RestResponseModel'
import { type IPageResponse } from '@/models/api/PageResponseModel'
import {
  PaginationRequestParameter,
  RequestModel,
  SortFieldParameter,
} from '@/models/api/RequestModel'
import { Follow } from '@/models/FollowModel'
import type { DataResponse, PagedDataResponse } from '@/models/api/DataResponseModel'

const restApiWithPage = api<IPageResponse<Follow>>()
const restApi = api<Follow>()
const BASE_URL = '/follows'

/** Helper to wrap response data in Follow instances */
function wrapFollow(response: DataResponse<Follow> | PagedDataResponse<Follow>) {
  if (Array.isArray(response.data)) {
    response.data = response.data.map((r) => new Follow(r))
  } else if (response.data && typeof response.data === 'object') {
    response.data = new Follow(response.data)
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
  return wrapFollow(RestResponse.handlePagedResponse<Follow>(response)) as PagedDataResponse<Follow>
}

/** POST search with pagination */
export async function search(
  data: Partial<Follow>,
  pagination?: PaginationRequestParameter,
  sortFields?: SortFieldParameter[],
) {
  const request = new RequestModel(BASE_URL + '/search', pagination, sortFields)
  const response = await restApiWithPage.POST(request.buildRequestUrl(), data)
  return wrapFollow(RestResponse.handlePagedResponse<Follow>(response)) as PagedDataResponse<Follow>
}

/** GET by ID */
export async function getById(id: number | string | undefined) {
  if (!id) throw new Error('getById: id is required')
  const response = await restApi.GET(BASE_URL + '/' + id)
  return wrapFollow(RestResponse.handleDataResponse<Follow>(response)) as DataResponse<Follow>
}

export async function create(data: Partial<Follow>) {
  const response = await restApi.POST(BASE_URL, data)
  return wrapFollow(RestResponse.handleDataResponse<Follow>(response)) as DataResponse<Follow>
}

/** PUT update */
export async function update(id: number | string | undefined, data: Partial<Follow>) {
  if (!id) throw new Error('update: id is required')
  const response = await restApi.PUT(BASE_URL + '/' + id, data)
  return wrapFollow(RestResponse.handleDataResponse<Follow>(response)) as DataResponse<Follow>
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
  follows: Partial<Follow[]>,
): Promise<{ success: boolean; error: string | null }> {
  const url = import.meta.env.VITE_APP_API_URL + BASE_URL + '/export/csv'

  try {
    const response = await fetch(url, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(follows),
    })
    if (!response.ok) {
      const errorText = await response.text()
      console.error('CSV Export Failed - Server Error:', response.status, errorText)
      throw new Error(`CSV export failed: . Status: .`)
    }
    const blob = await response.blob()
    const contentDisposition = response.headers.get('content-disposition')
    triggerFileDownload(blob, getFilenameFromHeaders(contentDisposition, 'Follow.csv'))
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
  return wrapFollow(RestResponse.handlePagedResponse<Follow>(response)) as PagedDataResponse<Follow>
}
export async function getAllByArtistId(
  id: number | string | undefined,
  pagination?: PaginationRequestParameter,
  sortFields?: SortFieldParameter[],
) {
  if (!id) throw new Error('getAllByArtistId: id of artist is required')
  const request = new RequestModel('/artists/' + id + BASE_URL, pagination, sortFields)
  const response = await restApiWithPage.GET(request.buildRequestUrl())
  return wrapFollow(RestResponse.handlePagedResponse<Follow>(response)) as PagedDataResponse<Follow>
}
