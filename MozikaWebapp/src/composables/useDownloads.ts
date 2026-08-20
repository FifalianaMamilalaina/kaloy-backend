import { ref, watch } from 'vue'
import { Download, DownloadFormDTO } from '@/models/DownloadModel'
import * as downloadService from '@/services/DownloadService'
import * as columnConfigService from '@/services/ColumnConfigService'
import { useLoading } from './useLoading'
import { PaginationRequestParameter, SortFieldParameter } from '@/models/api/RequestModel'
import { PaginationData } from '@/models/api/PageResponseModel'
import { useRouter } from 'vue-router'

export const DEFAULT_LIST_VIEW_FIELDS = ['id']

/**
 * Composable to manage Download entities including CRUD operations,
 * search, pagination, foreign key loading, and navigation.
 */
export function useDownloads() {
  /** List of all downloads */
  const downloads = ref<Download[]>([])

  /** Loading state management */
  const { loading, startLoading, stopLoading } = useLoading()

  /** Pagination information returned by API */
  const paginationData = ref(new PaginationData())

  /** Message for errors or notifications */
  const message = ref<string | null>(null)

  /** Vue router instance for navigation */
  const router = useRouter()

  const visibleListFields = ref<string[]>([...DEFAULT_LIST_VIEW_FIELDS])

  // On utilise une clé spécifique à l'entité pour plus de flexibilité future
  const savedLayoutMode = localStorage.getItem('layoutMode_download') as 'list' | 'card' | null
  const layoutMode = ref<'list' | 'card'>(savedLayoutMode || 'list')

  watch(layoutMode, (newValue) => {
    localStorage.setItem('layoutMode_download', newValue)
  })

  /**
   * Handles API response by updating the downloads and pagination
   * @param data Array of Download returned by the API
   * @param err Optional error message
   * @param pagination Optional pagination data
   */
  const handleResponse = (
    data: Download[],
    err: string | undefined,
    pagination?: PaginationData,
    concatData: boolean = false,
  ) => {
    if (err) {
      message.value = err
    }
    if (concatData) {
      downloads.value = downloads.value.concat(data)
    } else {
      downloads.value = data
    }
    if (pagination !== undefined) {
      paginationData.value = new PaginationData(pagination)
    }
  }

  const loadColumnConfig = async () => {
    try {
      const response = await columnConfigService.getVisibleFields('download', 'list')
      if (response.returnCode === 1 && response.data && response.data.length > 0) {
        visibleListFields.value = response.data
      }
    } catch (error) {
      console.warn(
        'Impossible de charger la config des colonnes, utilisation des valeurs par défaut.',
        error,
      )
    }
  }

  /** Sauvegarde la nouvelle configuration vers le serveur */
  const saveColumnConfig = async (fields: string[]) => {
    try {
      const response = await columnConfigService.updateVisibleFields('download', 'list', fields)
      if (response.returnCode === 1) {
        visibleListFields.value = fields
        return { success: true, error: null }
      } else {
        return { success: false, error: response.message || 'Erreur lors de la sauvegarde' }
      }
    } catch (error) {
      return { success: false, error: 'Erreur réseau lors de la sauvegarde' }
    }
  }

  // Navigation functions
  /** Navigate to download detail view */
  const viewDownload = (download: Download) => {
    router.push({ name: 'downloaddetailsview', params: { id: download.getKeyValue() } })
  }

  /** Navigate to download list view */
  const goToListView = () => {
    router.push({ path: '/downloads' })
  }

  /** Navigate to download create form */
  const goToCreateFormView = () => {
    router.push({ path: '/downloads/create' })
  }

  /** Navigate to download update form */
  const goToUpdateFormView = (download: Download) => {
    router.push({ name: 'downloadupdateview', params: { id: download.getKeyValue() } })
  }

  /**
   * Delete a download by id
   * @param download Download object to delete
   */
  const deleteDownload = async (download: Download) => {
    const result = await downloadService.remove(download.getKeyValue())
    if (result.error) {
      message.value = result.error
    }
    return result
  }

  /**
   * Create a new download
   * @param downloadFormDTO Download object to create
   */
  const createDownload = async (downloadFormDTO: Partial<DownloadFormDTO>) => {
    const dto = new DownloadFormDTO(downloadFormDTO)
    const download: Partial<Download> = await dto.toEntity()
    const { data, error, errors } = await downloadService.create(download)
    if (error) {
      message.value = error
    }
    return { data, errors }
  }

  /**
   * Update an existing download
   * @param downloadFormDTO Download object to update
   */
  const updateDownload = async (id: number | string, downloadFormDTO: Partial<DownloadFormDTO>) => {
    const dto = new DownloadFormDTO(downloadFormDTO)
    const download: Partial<Download> = await dto.toEntity()
    const { data, error } = await downloadService.update(id, download)
    if (error) {
      message.value = error
    }
    return data
  }

  // API Requests
  /** Load all downloads from the API */
  const loadDownloads = async (
    unpagined: boolean = false,
    pagination?: PaginationRequestParameter,
    sortFields?: SortFieldParameter[],
  ): Promise<void> => {
    message.value = null
    startLoading()
    const {
      data,
      error: err,
      pagination: paginationresult,
    } = await downloadService.getAll(pagination, sortFields)
    handleResponse(data, err, paginationresult)

    if (unpagined && downloads.value.length > 0) {
      while (paginationData.value.hasNext()) {
        paginationData.value.nextPage()
        const {
          data: nextData,
          error: nextErr,
          pagination: nextPagination,
        } = await downloadService.getAll(paginationData.value.toParameter(), sortFields)
        handleResponse(nextData, nextErr, nextPagination, true)
      }
    }
    stopLoading()
  }

  /**
   * Get a single download by id
   * @param id Download ID
   */
  const getDownloadById = async (id: number | string) => {
    const result = await downloadService.getById(id)
    if (result.error) {
      message.value = result.error
    }
    return result
  }

  /**
   * Search downloads with filters, pagination, and sorting
   * @param filters Partial Download object containing search filters
   * @param pagination Pagination request parameters
   * @param sortFields Array of fields to sort
   */
  const searchDownloads = async (
    unpagined: boolean = false,
    filters: Partial<Download>,
    pagination: PaginationRequestParameter,
    sortFields: SortFieldParameter[],
  ) => {
    message.value = null
    startLoading()
    const {
      data,
      error: err,
      pagination: paginationresult,
    } = await downloadService.search(filters, pagination, sortFields)
    handleResponse(data, err, paginationresult)

    if (unpagined) {
      while (paginationData.value.hasNext()) {
        paginationData.value.nextPage()
        const {
          data: nextData,
          error: nextErr,
          pagination: nextPagination,
        } = await downloadService.search(filters, paginationData.value.toParameter(), sortFields)
        handleResponse(nextData, nextErr, nextPagination, true)
      }
    }
    stopLoading()
  }
  const exportDownloadsToCsv = async (data: Partial<Download[]>) => {
    console.log('Exporting Download to CSV', data)
    await downloadService.exportCsv(data)
  }

  const getAllDownloadsByUserId = async (
    userId: number | string | undefined,
    unpagined: boolean = false,
    pagination: PaginationRequestParameter,
    sortFields: SortFieldParameter[],
  ) => {
    message.value = null
    startLoading()
    const {
      data,
      error: err,
      pagination: paginationresult,
    } = await downloadService.getAllByUserId(userId, pagination, sortFields)
    handleResponse(data, err, paginationresult)

    if (unpagined) {
      while (paginationData.value.hasNext() && downloads.value.length > 0) {
        paginationData.value.nextPage()
        const {
          data: nextData,
          error: nextErr,
          pagination: nextPagination,
        } = await downloadService.getAllByUserId(
          userId,
          paginationData.value.toParameter(),
          sortFields,
        )
        handleResponse(nextData, nextErr, nextPagination, true)
      }
    }
    stopLoading()
  }
  const getAllDownloadsByPlaylistId = async (
    playlistId: number | string | undefined,
    unpagined: boolean = false,
    pagination: PaginationRequestParameter,
    sortFields: SortFieldParameter[],
  ) => {
    message.value = null
    startLoading()
    const {
      data,
      error: err,
      pagination: paginationresult,
    } = await downloadService.getAllByPlaylistId(playlistId, pagination, sortFields)
    handleResponse(data, err, paginationresult)

    if (unpagined) {
      while (paginationData.value.hasNext() && downloads.value.length > 0) {
        paginationData.value.nextPage()
        const {
          data: nextData,
          error: nextErr,
          pagination: nextPagination,
        } = await downloadService.getAllByPlaylistId(
          playlistId,
          paginationData.value.toParameter(),
          sortFields,
        )
        handleResponse(nextData, nextErr, nextPagination, true)
      }
    }
    stopLoading()
  }

  /** Getter for pagination data */
  const getPaginationData = () => paginationData.value

  return {
    // state
    downloads,
    loading,
    message,
    paginationData,
    layoutMode,

    // State & function of column configuration
    visibleListFields,
    loadColumnConfig,
    saveColumnConfig,

    // Actions
    getPaginationData,
    exportDownloadsToCsv,
    loadDownloads,
    searchDownloads,
    getDownloadById,
    viewDownload,
    goToListView,
    goToCreateFormView,
    goToUpdateFormView,
    deleteDownload,
    createDownload,
    updateDownload,
    getAllDownloadsByUserId,
    getAllDownloadsByPlaylistId,
  }
}
