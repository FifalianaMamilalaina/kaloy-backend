import { ref, watch } from 'vue'
import { SearchHistory, SearchHistoryFormDTO } from '@/models/SearchHistoryModel'
import * as searchHistoryService from '@/services/SearchHistoryService'
import * as columnConfigService from '@/services/ColumnConfigService'
import { useLoading } from './useLoading'
import { PaginationRequestParameter, SortFieldParameter } from '@/models/api/RequestModel'
import { PaginationData } from '@/models/api/PageResponseModel'
import { useRouter } from 'vue-router'

export const DEFAULT_LIST_VIEW_FIELDS = ['id']

/**
 * Composable to manage SearchHistory entities including CRUD operations,
 * search, pagination, foreign key loading, and navigation.
 */
export function useSearchHistorys() {
  /** List of all searchHistorys */
  const searchHistorys = ref<SearchHistory[]>([])

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
  const savedLayoutMode = localStorage.getItem('layoutMode_searchHistory') as 'list' | 'card' | null
  const layoutMode = ref<'list' | 'card'>(savedLayoutMode || 'list')

  watch(layoutMode, (newValue) => {
    localStorage.setItem('layoutMode_searchHistory', newValue)
  })

  /**
   * Handles API response by updating the searchHistorys and pagination
   * @param data Array of SearchHistory returned by the API
   * @param err Optional error message
   * @param pagination Optional pagination data
   */
  const handleResponse = (
    data: SearchHistory[],
    err: string | undefined,
    pagination?: PaginationData,
    concatData: boolean = false,
  ) => {
    if (err) {
      message.value = err
    }
    if (concatData) {
      searchHistorys.value = searchHistorys.value.concat(data)
    } else {
      searchHistorys.value = data
    }
    if (pagination !== undefined) {
      paginationData.value = new PaginationData(pagination)
    }
  }

  const loadColumnConfig = async () => {
    try {
      const response = await columnConfigService.getVisibleFields('searchHistory', 'list')
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
        'searchHistory',
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
  /** Navigate to searchHistory detail view */
  const viewSearchHistory = (searchHistory: SearchHistory) => {
    router.push({ name: 'searchHistorydetailsview', params: { id: searchHistory.getKeyValue() } })
  }

  /** Navigate to searchHistory list view */
  const goToListView = () => {
    router.push({ path: '/searchHistorys' })
  }

  /** Navigate to searchHistory create form */
  const goToCreateFormView = () => {
    router.push({ path: '/searchHistorys/create' })
  }

  /** Navigate to searchHistory update form */
  const goToUpdateFormView = (searchHistory: SearchHistory) => {
    router.push({ name: 'searchHistoryupdateview', params: { id: searchHistory.getKeyValue() } })
  }

  /**
   * Delete a searchHistory by id
   * @param searchHistory SearchHistory object to delete
   */
  const deleteSearchHistory = async (searchHistory: SearchHistory) => {
    const result = await searchHistoryService.remove(searchHistory.getKeyValue())
    if (result.error) {
      message.value = result.error
    }
    return result
  }

  /**
   * Create a new searchHistory
   * @param searchHistoryFormDTO SearchHistory object to create
   */
  const createSearchHistory = async (searchHistoryFormDTO: Partial<SearchHistoryFormDTO>) => {
    const dto = new SearchHistoryFormDTO(searchHistoryFormDTO)
    const searchHistory: Partial<SearchHistory> = await dto.toEntity()
    const { data, error, errors } = await searchHistoryService.create(searchHistory)
    if (error) {
      message.value = error
    }
    return { data, errors }
  }

  /**
   * Update an existing searchHistory
   * @param searchHistoryFormDTO SearchHistory object to update
   */
  const updateSearchHistory = async (
    id: number | string,
    searchHistoryFormDTO: Partial<SearchHistoryFormDTO>,
  ) => {
    const dto = new SearchHistoryFormDTO(searchHistoryFormDTO)
    const searchHistory: Partial<SearchHistory> = await dto.toEntity()
    const { data, error } = await searchHistoryService.update(id, searchHistory)
    if (error) {
      message.value = error
    }
    return data
  }

  // API Requests
  /** Load all searchHistorys from the API */
  const loadSearchHistorys = async (
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
    } = await searchHistoryService.getAll(pagination, sortFields)
    handleResponse(data, err, paginationresult)

    if (unpagined && searchHistorys.value.length > 0) {
      while (paginationData.value.hasNext()) {
        paginationData.value.nextPage()
        const {
          data: nextData,
          error: nextErr,
          pagination: nextPagination,
        } = await searchHistoryService.getAll(paginationData.value.toParameter(), sortFields)
        handleResponse(nextData, nextErr, nextPagination, true)
      }
    }
    stopLoading()
  }

  /**
   * Get a single searchHistory by id
   * @param id SearchHistory ID
   */
  const getSearchHistoryById = async (id: number | string) => {
    const result = await searchHistoryService.getById(id)
    if (result.error) {
      message.value = result.error
    }
    return result
  }

  /**
   * Search searchHistorys with filters, pagination, and sorting
   * @param filters Partial SearchHistory object containing search filters
   * @param pagination Pagination request parameters
   * @param sortFields Array of fields to sort
   */
  const searchSearchHistorys = async (
    unpagined: boolean = false,
    filters: Partial<SearchHistory>,
    pagination: PaginationRequestParameter,
    sortFields: SortFieldParameter[],
  ) => {
    message.value = null
    startLoading()
    const {
      data,
      error: err,
      pagination: paginationresult,
    } = await searchHistoryService.search(filters, pagination, sortFields)
    handleResponse(data, err, paginationresult)

    if (unpagined) {
      while (paginationData.value.hasNext()) {
        paginationData.value.nextPage()
        const {
          data: nextData,
          error: nextErr,
          pagination: nextPagination,
        } = await searchHistoryService.search(
          filters,
          paginationData.value.toParameter(),
          sortFields,
        )
        handleResponse(nextData, nextErr, nextPagination, true)
      }
    }
    stopLoading()
  }
  const exportSearchHistorysToCsv = async (data: Partial<SearchHistory[]>) => {
    console.log('Exporting SearchHistory to CSV', data)
    await searchHistoryService.exportCsv(data)
  }

  const getAllSearchHistorysByUserId = async (
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
    } = await searchHistoryService.getAllByUserId(userId, pagination, sortFields)
    handleResponse(data, err, paginationresult)

    if (unpagined) {
      while (paginationData.value.hasNext() && searchHistorys.value.length > 0) {
        paginationData.value.nextPage()
        const {
          data: nextData,
          error: nextErr,
          pagination: nextPagination,
        } = await searchHistoryService.getAllByUserId(
          userId,
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
    searchHistorys,
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
    exportSearchHistorysToCsv,
    loadSearchHistorys,
    searchSearchHistorys,
    getSearchHistoryById,
    viewSearchHistory,
    goToListView,
    goToCreateFormView,
    goToUpdateFormView,
    deleteSearchHistory,
    createSearchHistory,
    updateSearchHistory,
    getAllSearchHistorysByUserId,
  }
}
