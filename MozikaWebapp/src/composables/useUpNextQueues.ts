import { ref, watch } from 'vue'
import { UpNextQueue, UpNextQueueFormDTO } from '@/models/UpNextQueueModel'
import * as upNextQueueService from '@/services/UpNextQueueService'
import * as columnConfigService from '@/services/ColumnConfigService'
import { useLoading } from './useLoading'
import { PaginationRequestParameter, SortFieldParameter } from '@/models/api/RequestModel'
import { PaginationData } from '@/models/api/PageResponseModel'
import { useRouter } from 'vue-router'

export const DEFAULT_LIST_VIEW_FIELDS = ['id']

/**
 * Composable to manage UpNextQueue entities including CRUD operations,
 * search, pagination, foreign key loading, and navigation.
 */
export function useUpNextQueues() {
  /** List of all upNextQueues */
  const upNextQueues = ref<UpNextQueue[]>([])

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
  const savedLayoutMode = localStorage.getItem('layoutMode_upNextQueue') as 'list' | 'card' | null
  const layoutMode = ref<'list' | 'card'>(savedLayoutMode || 'list')

  watch(layoutMode, (newValue) => {
    localStorage.setItem('layoutMode_upNextQueue', newValue)
  })

  /**
   * Handles API response by updating the upNextQueues and pagination
   * @param data Array of UpNextQueue returned by the API
   * @param err Optional error message
   * @param pagination Optional pagination data
   */
  const handleResponse = (
    data: UpNextQueue[],
    err: string | undefined,
    pagination?: PaginationData,
    concatData: boolean = false,
  ) => {
    if (err) {
      message.value = err
    }
    if (concatData) {
      upNextQueues.value = upNextQueues.value.concat(data)
    } else {
      upNextQueues.value = data
    }
    if (pagination !== undefined) {
      paginationData.value = new PaginationData(pagination)
    }
  }

  const loadColumnConfig = async () => {
    try {
      const response = await columnConfigService.getVisibleFields('upNextQueue', 'list')
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
      const response = await columnConfigService.updateVisibleFields('upNextQueue', 'list', fields)
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
  /** Navigate to upNextQueue detail view */
  const viewUpNextQueue = (upNextQueue: UpNextQueue) => {
    router.push({ name: 'upNextQueuedetailsview', params: { id: upNextQueue.getKeyValue() } })
  }

  /** Navigate to upNextQueue list view */
  const goToListView = () => {
    router.push({ path: '/upNextQueues' })
  }

  /** Navigate to upNextQueue create form */
  const goToCreateFormView = () => {
    router.push({ path: '/upNextQueues/create' })
  }

  /** Navigate to upNextQueue update form */
  const goToUpdateFormView = (upNextQueue: UpNextQueue) => {
    router.push({ name: 'upNextQueueupdateview', params: { id: upNextQueue.getKeyValue() } })
  }

  /**
   * Delete a upNextQueue by id
   * @param upNextQueue UpNextQueue object to delete
   */
  const deleteUpNextQueue = async (upNextQueue: UpNextQueue) => {
    const result = await upNextQueueService.remove(upNextQueue.getKeyValue())
    if (result.error) {
      message.value = result.error
    }
    return result
  }

  /**
   * Create a new upNextQueue
   * @param upNextQueueFormDTO UpNextQueue object to create
   */
  const createUpNextQueue = async (upNextQueueFormDTO: Partial<UpNextQueueFormDTO>) => {
    const dto = new UpNextQueueFormDTO(upNextQueueFormDTO)
    const upNextQueue: Partial<UpNextQueue> = await dto.toEntity()
    const { data, error, errors } = await upNextQueueService.create(upNextQueue)
    if (error) {
      message.value = error
    }
    return { data, errors }
  }

  /**
   * Update an existing upNextQueue
   * @param upNextQueueFormDTO UpNextQueue object to update
   */
  const updateUpNextQueue = async (
    id: number | string,
    upNextQueueFormDTO: Partial<UpNextQueueFormDTO>,
  ) => {
    const dto = new UpNextQueueFormDTO(upNextQueueFormDTO)
    const upNextQueue: Partial<UpNextQueue> = await dto.toEntity()
    const { data, error } = await upNextQueueService.update(id, upNextQueue)
    if (error) {
      message.value = error
    }
    return data
  }

  // API Requests
  /** Load all upNextQueues from the API */
  const loadUpNextQueues = async (
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
    } = await upNextQueueService.getAll(pagination, sortFields)
    handleResponse(data, err, paginationresult)

    if (unpagined && upNextQueues.value.length > 0) {
      while (paginationData.value.hasNext()) {
        paginationData.value.nextPage()
        const {
          data: nextData,
          error: nextErr,
          pagination: nextPagination,
        } = await upNextQueueService.getAll(paginationData.value.toParameter(), sortFields)
        handleResponse(nextData, nextErr, nextPagination, true)
      }
    }
    stopLoading()
  }

  /**
   * Get a single upNextQueue by id
   * @param id UpNextQueue ID
   */
  const getUpNextQueueById = async (id: number | string) => {
    const result = await upNextQueueService.getById(id)
    if (result.error) {
      message.value = result.error
    }
    return result
  }

  /**
   * Search upNextQueues with filters, pagination, and sorting
   * @param filters Partial UpNextQueue object containing search filters
   * @param pagination Pagination request parameters
   * @param sortFields Array of fields to sort
   */
  const searchUpNextQueues = async (
    unpagined: boolean = false,
    filters: Partial<UpNextQueue>,
    pagination: PaginationRequestParameter,
    sortFields: SortFieldParameter[],
  ) => {
    message.value = null
    startLoading()
    const {
      data,
      error: err,
      pagination: paginationresult,
    } = await upNextQueueService.search(filters, pagination, sortFields)
    handleResponse(data, err, paginationresult)

    if (unpagined) {
      while (paginationData.value.hasNext()) {
        paginationData.value.nextPage()
        const {
          data: nextData,
          error: nextErr,
          pagination: nextPagination,
        } = await upNextQueueService.search(filters, paginationData.value.toParameter(), sortFields)
        handleResponse(nextData, nextErr, nextPagination, true)
      }
    }
    stopLoading()
  }
  const exportUpNextQueuesToCsv = async (data: Partial<UpNextQueue[]>) => {
    console.log('Exporting UpNextQueue to CSV', data)
    await upNextQueueService.exportCsv(data)
  }

  const getAllUpNextQueuesByUserId = async (
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
    } = await upNextQueueService.getAllByUserId(userId, pagination, sortFields)
    handleResponse(data, err, paginationresult)

    if (unpagined) {
      while (paginationData.value.hasNext() && upNextQueues.value.length > 0) {
        paginationData.value.nextPage()
        const {
          data: nextData,
          error: nextErr,
          pagination: nextPagination,
        } = await upNextQueueService.getAllByUserId(
          userId,
          paginationData.value.toParameter(),
          sortFields,
        )
        handleResponse(nextData, nextErr, nextPagination, true)
      }
    }
    stopLoading()
  }
  const getAllUpNextQueuesBySongId = async (
    songId: number | string | undefined,
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
    } = await upNextQueueService.getAllBySongId(songId, pagination, sortFields)
    handleResponse(data, err, paginationresult)

    if (unpagined) {
      while (paginationData.value.hasNext() && upNextQueues.value.length > 0) {
        paginationData.value.nextPage()
        const {
          data: nextData,
          error: nextErr,
          pagination: nextPagination,
        } = await upNextQueueService.getAllBySongId(
          songId,
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
    upNextQueues,
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
    exportUpNextQueuesToCsv,
    loadUpNextQueues,
    searchUpNextQueues,
    getUpNextQueueById,
    viewUpNextQueue,
    goToListView,
    goToCreateFormView,
    goToUpdateFormView,
    deleteUpNextQueue,
    createUpNextQueue,
    updateUpNextQueue,
    getAllUpNextQueuesByUserId,
    getAllUpNextQueuesBySongId,
  }
}
