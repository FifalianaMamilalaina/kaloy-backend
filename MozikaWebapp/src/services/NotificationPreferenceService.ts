import api from '@/services/api'
import { getFilenameFromHeaders, triggerFileDownload } from '@/utils/download.ts'
import RestResponse from '@/models/api/RestResponseModel'
import { type IPageResponse } from '@/models/api/PageResponseModel'
import {
  PaginationRequestParameter,
  RequestModel,
  SortFieldParameter,
} from '@/models/api/RequestModel'
import { NotificationPreference } from '@/models/NotificationPreferenceModel'
import type { DataResponse, PagedDataResponse } from '@/models/api/DataResponseModel'

const restApiWithPage = api<IPageResponse<NotificationPreference>>()
const restApi = api<NotificationPreference>()
const BASE_URL = '/notificationpreferences'

/** Helper to wrap response data in NotificationPreference instances */
function wrapNotificationPreference(
  response: DataResponse<NotificationPreference> | PagedDataResponse<NotificationPreference>,
) {
  if (Array.isArray(response.data)) {
    response.data = response.data.map((r) => new NotificationPreference(r))
  } else if (response.data && typeof response.data === 'object') {
    response.data = new NotificationPreference(response.data)
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
  return wrapNotificationPreference(
    RestResponse.handlePagedResponse<NotificationPreference>(response),
  ) as PagedDataResponse<NotificationPreference>
}

/** POST search with pagination */
export async function search(
  data: Partial<NotificationPreference>,
  pagination?: PaginationRequestParameter,
  sortFields?: SortFieldParameter[],
) {
  const request = new RequestModel(BASE_URL + '/search', pagination, sortFields)
  const response = await restApiWithPage.POST(request.buildRequestUrl(), data)
  return wrapNotificationPreference(
    RestResponse.handlePagedResponse<NotificationPreference>(response),
  ) as PagedDataResponse<NotificationPreference>
}

/** GET by ID */
export async function getById(id: number | string | undefined) {
  if (!id) throw new Error('getById: id is required')
  const response = await restApi.GET(BASE_URL + '/' + id)
  return wrapNotificationPreference(
    RestResponse.handleDataResponse<NotificationPreference>(response),
  ) as DataResponse<NotificationPreference>
}

export async function create(data: Partial<NotificationPreference>) {
  const response = await restApi.POST(BASE_URL, data)
  return wrapNotificationPreference(
    RestResponse.handleDataResponse<NotificationPreference>(response),
  ) as DataResponse<NotificationPreference>
}

/** PUT update */
export async function update(
  id: number | string | undefined,
  data: Partial<NotificationPreference>,
) {
  if (!id) throw new Error('update: id is required')
  const response = await restApi.PUT(BASE_URL + '/' + id, data)
  return wrapNotificationPreference(
    RestResponse.handleDataResponse<NotificationPreference>(response),
  ) as DataResponse<NotificationPreference>
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
  notificationPreferences: Partial<NotificationPreference[]>,
): Promise<{ success: boolean; error: string | null }> {
  const url = import.meta.env.VITE_APP_API_URL + BASE_URL + '/export/csv'

  try {
    const response = await fetch(url, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(notificationPreferences),
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
      getFilenameFromHeaders(contentDisposition, 'NotificationPreference.csv'),
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
export async function getAllByUserId(
  id: number | string | undefined,
  pagination?: PaginationRequestParameter,
  sortFields?: SortFieldParameter[],
) {
  if (!id) throw new Error('getAllByUserId: id of user is required')
  const request = new RequestModel('/users/' + id + BASE_URL, pagination, sortFields)
  const response = await restApiWithPage.GET(request.buildRequestUrl())
  return wrapNotificationPreference(
    RestResponse.handlePagedResponse<NotificationPreference>(response),
  ) as PagedDataResponse<NotificationPreference>
}
