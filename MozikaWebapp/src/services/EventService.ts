import api from '@/services/api'
import { getFilenameFromHeaders, triggerFileDownload } from '@/utils/download.ts'
import RestResponse from '@/models/api/RestResponseModel'
import { type IPageResponse } from '@/models/api/PageResponseModel'
import {
  PaginationRequestParameter,
  RequestModel,
  SortFieldParameter,
} from '@/models/api/RequestModel'
import { Event } from '@/models/EventModel'
import type { DataResponse, PagedDataResponse } from '@/models/api/DataResponseModel'
import type { Concert } from '@/models/ConcertModel'
import type { EventMedia } from '@/models/EventMediaModel'

const restApiWithPage = api<IPageResponse<Event>>()
const restApi = api<Event>()
const BASE_URL = '/events'

/** Helper to wrap response data in Event instances */
function wrapEvent(response: DataResponse<Event> | PagedDataResponse<Event>) {
  if (Array.isArray(response.data)) {
    response.data = response.data.map((r) => new Event(r))
  } else if (response.data && typeof response.data === 'object') {
    response.data = new Event(response.data)
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
  return wrapEvent(RestResponse.handlePagedResponse<Event>(response)) as PagedDataResponse<Event>
}

/** POST search with pagination */
export async function search(
  data: Partial<Event>,
  pagination?: PaginationRequestParameter,
  sortFields?: SortFieldParameter[],
) {
  const request = new RequestModel(BASE_URL + '/search', pagination, sortFields)
  const response = await restApiWithPage.POST(request.buildRequestUrl(), data)
  return wrapEvent(RestResponse.handlePagedResponse<Event>(response)) as PagedDataResponse<Event>
}

/** GET by ID */
export async function getById(id: number | string | undefined) {
  if (!id) throw new Error('getById: id is required')
  const response = await restApi.GET(BASE_URL + '/' + id)
  return wrapEvent(RestResponse.handleDataResponse<Event>(response)) as DataResponse<Event>
}

/** POST create */
export async function create(data: {
  event: Partial<Event>
  concerts: Partial<Concert>[]
  eventMedias: Partial<EventMedia>[]
}) {
  const response = await restApi.POST(BASE_URL, data)
  return wrapEvent(RestResponse.handleDataResponse<Event>(response)) as DataResponse<Event>
}

/** PUT update */
export async function update(
  id: number | string | undefined,
  data: {
    event: Partial<Event>
    concerts: Partial<Concert>[]
    eventMedias: Partial<EventMedia>[]
  },
) {
  if (!id) throw new Error('update: id is required')
  const response = await restApi.PUT(BASE_URL + '/' + id, data)
  return wrapEvent(RestResponse.handleDataResponse<Event>(response)) as DataResponse<Event>
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
  events: Partial<Event[]>,
): Promise<{ success: boolean; error: string | null }> {
  const url = import.meta.env.VITE_APP_API_URL + BASE_URL + '/export/csv'

  try {
    const response = await fetch(url, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(events),
    })
    if (!response.ok) {
      const errorText = await response.text()
      console.error('CSV Export Failed - Server Error:', response.status, errorText)
      throw new Error(`CSV export failed: . Status: .`)
    }
    const blob = await response.blob()
    const contentDisposition = response.headers.get('content-disposition')
    triggerFileDownload(blob, getFilenameFromHeaders(contentDisposition, 'Event.csv'))
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
  return wrapEvent(RestResponse.handlePagedResponse<Event>(response)) as PagedDataResponse<Event>
}
