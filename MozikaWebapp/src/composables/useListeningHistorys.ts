import { ref, watch } from 'vue'
import { ListeningHistory, ListeningHistoryFormDTO } from '@/models/ListeningHistoryModel'
import * as listeningHistoryService from '@/services/ListeningHistoryService'
import * as columnConfigService from '@/services/ColumnConfigService'
import { useLoading } from './useLoading'
import { PaginationRequestParameter, SortFieldParameter } from '@/models/api/RequestModel'
import { PaginationData } from '@/models/api/PageResponseModel'
import { useRouter } from 'vue-router'

export const DEFAULT_LIST_VIEW_FIELDS = ['id']

/**
 * Composable to manage ListeningHistory entities including CRUD operations,
 * search, pagination, foreign key loading, and navigation.
 */
export function useListeningHistorys() {
  /** List of all listeningHistorys */
  const listeningHistorys = ref<ListeningHistory[]>([])

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
  const savedLayoutMode = localStorage.getItem('layoutMode_listeningHistory') as
    | 'list'
    | 'card'
    | null
  const layoutMode = ref<'list' | 'card'>(savedLayoutMode || 'list')

  watch(layoutMode, (newValue) => {
    localStorage.setItem('layoutMode_listeningHistory', newValue)
  })

  /**
   * Handles API response by updating the listeningHistorys and pagination
   * @param data Array of ListeningHistory returned by the API
   * @param err Optional error message
   * @param pagination Optional pagination data
   */
  const handleResponse = (
    data: ListeningHistory[],
    err: string | undefined,
    pagination?: PaginationData,
    concatData: boolean = false,
  ) => {
    if (err) {
      message.value = err
    }
    if (concatData) {
      listeningHistorys.value = listeningHistorys.value.concat(data)
    } else {
      listeningHistorys.value = data
    }
    if (pagination !== undefined) {
      paginationData.value = new PaginationData(pagination)
    }
  }

  const loadColumnConfig = async () => {
    try {
      const response = await columnConfigService.getVisibleFields('listeningHistory', 'list')
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
      const response = await columnConfigService.updateVisibleFields(
        'listeningHistory',
        'list',
        fields,
      )
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
  /** Navigate to listeningHistory detail view */
  const viewListeningHistory = (listeningHistory: ListeningHistory) => {
    router.push({
      name: 'listeningHistorydetailsview',
      params: { id: listeningHistory.getKeyValue() },
    })
  }

  /** Navigate to listeningHistory list view */
  const goToListView = () => {
    router.push({ path: '/listeningHistorys' })
  }

  /** Navigate to listeningHistory create form */
  const goToCreateFormView = () => {
    router.push({ path: '/listeningHistorys/create' })
  }

  /** Navigate to listeningHistory update form */
  const goToUpdateFormView = (listeningHistory: ListeningHistory) => {
    router.push({
      name: 'listeningHistoryupdateview',
      params: { id: listeningHistory.getKeyValue() },
    })
  }

  /**
   * Delete a listeningHistory by id
   * @param listeningHistory ListeningHistory object to delete
   */
  const deleteListeningHistory = async (listeningHistory: ListeningHistory) => {
    const result = await listeningHistoryService.remove(listeningHistory.getKeyValue())
    if (result.error) {
      message.value = result.error
    }
    return result
  }

  /**
   * Create a new listeningHistory
   * @param listeningHistoryFormDTO ListeningHistory object to create
   */
  const createListeningHistory = async (
    listeningHistoryFormDTO: Partial<ListeningHistoryFormDTO>,
  ) => {
    const dto = new ListeningHistoryFormDTO(listeningHistoryFormDTO)
    const listeningHistory: Partial<ListeningHistory> = await dto.toEntity()
    const { data, error, errors } = await listeningHistoryService.create(listeningHistory)
    if (error) {
      message.value = error
    }
    return { data, errors }
  }

  /**
   * Update an existing listeningHistory
   * @param listeningHistoryFormDTO ListeningHistory object to update
   */
  const updateListeningHistory = async (
    id: number | string,
    listeningHistoryFormDTO: Partial<ListeningHistoryFormDTO>,
  ) => {
    const dto = new ListeningHistoryFormDTO(listeningHistoryFormDTO)
    const listeningHistory: Partial<ListeningHistory> = await dto.toEntity()
    const { data, error } = await listeningHistoryService.update(id, listeningHistory)
    if (error) {
      message.value = error
    }
    return data
  }

  // API Requests
  /** Load all listeningHistorys from the API */
  const loadListeningHistorys = async (
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
    } = await listeningHistoryService.getAll(pagination, sortFields)
    handleResponse(data, err, paginationresult)

    if (unpagined && listeningHistorys.value.length > 0) {
      while (paginationData.value.hasNext()) {
        paginationData.value.nextPage()
        const {
          data: nextData,
          error: nextErr,
          pagination: nextPagination,
        } = await listeningHistoryService.getAll(paginationData.value.toParameter(), sortFields)
        handleResponse(nextData, nextErr, nextPagination, true)
      }
    }
    stopLoading()
  }

  /**
   * Get a single listeningHistory by id
   * @param id ListeningHistory ID
   */
  const getListeningHistoryById = async (id: number | string) => {
    const result = await listeningHistoryService.getById(id)
    if (result.error) {
      message.value = result.error
    }
    return result
  }

  /**
   * Search listeningHistorys with filters, pagination, and sorting
   * @param filters Partial ListeningHistory object containing search filters
   * @param pagination Pagination request parameters
   * @param sortFields Array of fields to sort
   */
  const searchListeningHistorys = async (
    unpagined: boolean = false,
    filters: Partial<ListeningHistory>,
    pagination: PaginationRequestParameter,
    sortFields: SortFieldParameter[],
  ) => {
    message.value = null
    startLoading()
    const {
      data,
      error: err,
      pagination: paginationresult,
    } = await listeningHistoryService.search(filters, pagination, sortFields)
    handleResponse(data, err, paginationresult)

    if (unpagined) {
      while (paginationData.value.hasNext()) {
        paginationData.value.nextPage()
        const {
          data: nextData,
          error: nextErr,
          pagination: nextPagination,
        } = await listeningHistoryService.search(
          filters,
          paginationData.value.toParameter(),
          sortFields,
        )
        handleResponse(nextData, nextErr, nextPagination, true)
      }
    }
    stopLoading()
  }
  const exportListeningHistorysToCsv = async (data: Partial<ListeningHistory[]>) => {
    console.log('Exporting ListeningHistory to CSV', data)
    await listeningHistoryService.exportCsv(data)
  }

  const getAllListeningHistorysByUserId = async (
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
    } = await listeningHistoryService.getAllByUserId(userId, pagination, sortFields)
    handleResponse(data, err, paginationresult)

    if (unpagined) {
      while (paginationData.value.hasNext() && listeningHistorys.value.length > 0) {
        paginationData.value.nextPage()
        const {
          data: nextData,
          error: nextErr,
          pagination: nextPagination,
        } = await listeningHistoryService.getAllByUserId(
          userId,
          paginationData.value.toParameter(),
          sortFields,
        )
        handleResponse(nextData, nextErr, nextPagination, true)
      }
    }
    stopLoading()
  }
  const getAllListeningHistorysBySongId = async (
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
    } = await listeningHistoryService.getAllBySongId(songId, pagination, sortFields)
    handleResponse(data, err, paginationresult)

    if (unpagined) {
      while (paginationData.value.hasNext() && listeningHistorys.value.length > 0) {
        paginationData.value.nextPage()
        const {
          data: nextData,
          error: nextErr,
          pagination: nextPagination,
        } = await listeningHistoryService.getAllBySongId(
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
    listeningHistorys,
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
    exportListeningHistorysToCsv,
    loadListeningHistorys,
    searchListeningHistorys,
    getListeningHistoryById,
    viewListeningHistory,
    goToListView,
    goToCreateFormView,
    goToUpdateFormView,
    deleteListeningHistory,
    createListeningHistory,
    updateListeningHistory,
    getAllListeningHistorysByUserId,
    getAllListeningHistorysBySongId,
  }
}
