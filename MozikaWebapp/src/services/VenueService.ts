import api from '@/services/api'
import { getFilenameFromHeaders, triggerFileDownload } from '@/utils/download.ts'
import RestResponse from '@/models/api/RestResponseModel'
import { type IPageResponse } from '@/models/api/PageResponseModel'
import {
  PaginationRequestParameter,
  RequestModel,
  SortFieldParameter,
} from '@/models/api/RequestModel'
import { Venue } from '@/models/VenueModel'
import type { DataResponse, PagedDataResponse } from '@/models/api/DataResponseModel'
import type { Concert } from '@/models/ConcertModel'

const restApiWithPage = api<IPageResponse<Venue>>()
const restApi = api<Venue>()
const BASE_URL = '/venues'

/** Helper to wrap response data in Venue instances */
function wrapVenue(response: DataResponse<Venue> | PagedDataResponse<Venue>) {
  if (Array.isArray(response.data)) {
    response.data = response.data.map((r) => new Venue(r))
  } else if (response.data && typeof response.data === 'object') {
    response.data = new Venue(response.data)
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
  return wrapVenue(RestResponse.handlePagedResponse<Venue>(response)) as PagedDataResponse<Venue>
}

/** POST search with pagination */
export async function search(
  data: Partial<Venue>,
  pagination?: PaginationRequestParameter,
  sortFields?: SortFieldParameter[],
) {
  const request = new RequestModel(BASE_URL + '/search', pagination, sortFields)
  const response = await restApiWithPage.POST(request.buildRequestUrl(), data)
  return wrapVenue(RestResponse.handlePagedResponse<Venue>(response)) as PagedDataResponse<Venue>
}

/** GET by ID */
export async function getById(id: number | string | undefined) {
  if (!id) throw new Error('getById: id is required')
  const response = await restApi.GET(BASE_URL + '/' + id)
  return wrapVenue(RestResponse.handleDataResponse<Venue>(response)) as DataResponse<Venue>
}

/** POST create */
export async function create(data: { venue: Partial<Venue>; concerts: Partial<Concert>[] }) {
  const response = await restApi.POST(BASE_URL, data)
  return wrapVenue(RestResponse.handleDataResponse<Venue>(response)) as DataResponse<Venue>
}

/** PUT update */
export async function update(
  id: number | string | undefined,
  data: {
    venue: Partial<Venue>
    concerts: Partial<Concert>[]
  },
) {
  if (!id) throw new Error('update: id is required')
  const response = await restApi.PUT(BASE_URL + '/' + id, data)
  return wrapVenue(RestResponse.handleDataResponse<Venue>(response)) as DataResponse<Venue>
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
  venues: Partial<Venue[]>,
): Promise<{ success: boolean; error: string | null }> {
  const url = import.meta.env.VITE_APP_API_URL + BASE_URL + '/export/csv'

  try {
    const response = await fetch(url, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(venues),
    })
    if (!response.ok) {
      const errorText = await response.text()
      console.error('CSV Export Failed - Server Error:', response.status, errorText)
      throw new Error(`CSV export failed: . Status: .`)
    }
    const blob = await response.blob()
    const contentDisposition = response.headers.get('content-disposition')
    triggerFileDownload(blob, getFilenameFromHeaders(contentDisposition, 'Venue.csv'))
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
