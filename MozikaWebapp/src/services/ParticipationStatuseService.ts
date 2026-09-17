import api from '@/services/api'
import { getFilenameFromHeaders, triggerFileDownload } from '@/utils/download.ts'
import RestResponse from '@/models/api/RestResponseModel'
import { type IPageResponse } from '@/models/api/PageResponseModel'
import {
  PaginationRequestParameter,
  RequestModel,
  SortFieldParameter,
} from '@/models/api/RequestModel'
import { ParticipationStatuse } from '@/models/ParticipationStatuseModel'
import type { DataResponse, PagedDataResponse } from '@/models/api/DataResponseModel'

const restApiWithPage = api<IPageResponse<ParticipationStatuse>>()
const restApi = api<ParticipationStatuse>()
const BASE_URL = '/participationstatuses'

/** Helper to wrap response data in ParticipationStatuse instances */
function wrapParticipationStatuse(
  response: DataResponse<ParticipationStatuse> | PagedDataResponse<ParticipationStatuse>,
) {
  if (Array.isArray(response.data)) {
    response.data = response.data.map((r) => new ParticipationStatuse(r))
  } else if (response.data && typeof response.data === 'object') {
    response.data = new ParticipationStatuse(response.data)
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
  return wrapParticipationStatuse(
    RestResponse.handlePagedResponse<ParticipationStatuse>(response),
  ) as PagedDataResponse<ParticipationStatuse>
}

/** POST search with pagination */
export async function search(
  data: Partial<ParticipationStatuse>,
  pagination?: PaginationRequestParameter,
  sortFields?: SortFieldParameter[],
) {
  const request = new RequestModel(BASE_URL + '/search', pagination, sortFields)
  const response = await restApiWithPage.POST(request.buildRequestUrl(), data)
  return wrapParticipationStatuse(
    RestResponse.handlePagedResponse<ParticipationStatuse>(response),
  ) as PagedDataResponse<ParticipationStatuse>
}

/** GET by ID */
export async function getById(id: number | string | undefined) {
  if (!id) throw new Error('getById: id is required')
  const response = await restApi.GET(BASE_URL + '/' + id)
  return wrapParticipationStatuse(
    RestResponse.handleDataResponse<ParticipationStatuse>(response),
  ) as DataResponse<ParticipationStatuse>
}

export async function create(data: Partial<ParticipationStatuse>) {
  const response = await restApi.POST(BASE_URL, data)
  return wrapParticipationStatuse(
    RestResponse.handleDataResponse<ParticipationStatuse>(response),
  ) as DataResponse<ParticipationStatuse>
}

/** PUT update */
export async function update(id: number | string | undefined, data: Partial<ParticipationStatuse>) {
  if (!id) throw new Error('update: id is required')
  const response = await restApi.PUT(BASE_URL + '/' + id, data)
  return wrapParticipationStatuse(
    RestResponse.handleDataResponse<ParticipationStatuse>(response),
  ) as DataResponse<ParticipationStatuse>
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
  participationStatuses: Partial<ParticipationStatuse[]>,
): Promise<{ success: boolean; error: string | null }> {
  const url = import.meta.env.VITE_APP_API_URL + BASE_URL + '/export/csv'

  try {
    const response = await fetch(url, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(participationStatuses),
    })
    if (!response.ok) {
      const errorText = await response.text()
      console.error('CSV Export Failed - Server Error:', response.status, errorText)
      throw new Error(`CSV export failed: . Status: .`)
    }
    const blob = await response.blob()
    const contentDisposition = response.headers.get('content-disposition')
    triggerFileDownload(
      blob,
      getFilenameFromHeaders(contentDisposition, 'ParticipationStatuse.csv'),
    )
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
