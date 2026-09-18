import api from '@/services/api'
import { getFilenameFromHeaders, triggerFileDownload } from '@/utils/download.ts'
import RestResponse from '@/models/api/RestResponseModel'
import { type IPageResponse } from '@/models/api/PageResponseModel'
import {
  PaginationRequestParameter,
  RequestModel,
  SortFieldParameter,
} from '@/models/api/RequestModel'
import { EventMedia } from '@/models/EventMediaModel'
import type { DataResponse, PagedDataResponse } from '@/models/api/DataResponseModel'

const restApiWithPage = api<IPageResponse<EventMedia>>()
const restApi = api<EventMedia>()
const BASE_URL = '/eventmedias'

/** Helper to wrap response data in EventMedia instances */
function wrapEventMedia(response: DataResponse<EventMedia> | PagedDataResponse<EventMedia>) {
  if (Array.isArray(response.data)) {
    response.data = response.data.map((r) => new EventMedia(r))
  } else if (response.data && typeof response.data === 'object') {
    response.data = new EventMedia(response.data)
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
  return wrapEventMedia(
    RestResponse.handlePagedResponse<EventMedia>(response),
  ) as PagedDataResponse<EventMedia>
}

/** POST search with pagination */
export async function search(
  data: Partial<EventMedia>,
  pagination?: PaginationRequestParameter,
  sortFields?: SortFieldParameter[],
) {
  const request = new RequestModel(BASE_URL + '/search', pagination, sortFields)
  const response = await restApiWithPage.POST(request.buildRequestUrl(), data)
  return wrapEventMedia(
    RestResponse.handlePagedResponse<EventMedia>(response),
  ) as PagedDataResponse<EventMedia>
}

/** GET by ID */
export async function getById(id: number | string | undefined) {
  if (!id) throw new Error('getById: id is required')
  const response = await restApi.GET(BASE_URL + '/' + id)
  return wrapEventMedia(
    RestResponse.handleDataResponse<EventMedia>(response),
  ) as DataResponse<EventMedia>
}

export async function create(data: Partial<EventMedia>) {
  const response = await restApi.POST(BASE_URL, data)
  return wrapEventMedia(
    RestResponse.handleDataResponse<EventMedia>(response),
  ) as DataResponse<EventMedia>
}

/** PUT update */
export async function update(id: number | string | undefined, data: Partial<EventMedia>) {
  if (!id) throw new Error('update: id is required')
  const response = await restApi.PUT(BASE_URL + '/' + id, data)
  return wrapEventMedia(
    RestResponse.handleDataResponse<EventMedia>(response),
  ) as DataResponse<EventMedia>
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
  eventMedias: Partial<EventMedia[]>,
): Promise<{ success: boolean; error: string | null }> {
  const url = import.meta.env.VITE_APP_API_URL + BASE_URL + '/export/csv'

  try {
    const response = await fetch(url, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(eventMedias),
    })
    if (!response.ok) {
      const errorText = await response.text()
      console.error('CSV Export Failed - Server Error:', response.status, errorText)
      throw new Error(`CSV export failed: . Status: .`)
    }
    const blob = await response.blob()
    const contentDisposition = response.headers.get('content-disposition')
    triggerFileDownload(blob, getFilenameFromHeaders(contentDisposition, 'EventMedia.csv'))
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
export async function getAllByEventId(
  id: number | string | undefined,
  pagination?: PaginationRequestParameter,
  sortFields?: SortFieldParameter[],
) {
  if (!id) throw new Error('getAllByEventId: id of event is required')
  const request = new RequestModel('/events/' + id + BASE_URL, pagination, sortFields)
  const response = await restApiWithPage.GET(request.buildRequestUrl())
  return wrapEventMedia(
    RestResponse.handlePagedResponse<EventMedia>(response),
  ) as PagedDataResponse<EventMedia>
}
export async function getAllByUserId(
  id: number | string | undefined,
  pagination?: PaginationRequestParameter,
  sortFields?: SortFieldParameter[],
) {
  if (!id) throw new Error('getAllByUserId: id of user is required')
  const request = new RequestModel('/users/' + id + BASE_URL, pagination, sortFields)
  const response = await restApiWithPage.GET(request.buildRequestUrl())
  return wrapEventMedia(
    RestResponse.handlePagedResponse<EventMedia>(response),
  ) as PagedDataResponse<EventMedia>
}
