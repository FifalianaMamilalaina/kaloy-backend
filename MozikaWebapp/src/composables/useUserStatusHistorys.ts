import { ref, watch } from 'vue'
import { UserStatusHistory, UserStatusHistoryFormDTO } from '@/models/UserStatusHistoryModel'
import * as userStatusHistoryService from '@/services/UserStatusHistoryService'
import * as columnConfigService from '@/services/ColumnConfigService'
import { useLoading } from './useLoading'
import { PaginationRequestParameter, SortFieldParameter } from '@/models/api/RequestModel'
import { PaginationData } from '@/models/api/PageResponseModel'
import { useRouter } from 'vue-router'

export const DEFAULT_LIST_VIEW_FIELDS = ['id']

/**
 * Composable to manage UserStatusHistory entities including CRUD operations,
 * search, pagination, foreign key loading, and navigation.
 */
export function useUserStatusHistorys() {
  /** List of all userStatusHistorys */
  const userStatusHistorys = ref<UserStatusHistory[]>([])

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
  const savedLayoutMode = localStorage.getItem('layoutMode_userStatusHistory') as
    | 'list'
    | 'card'
    | null
  const layoutMode = ref<'list' | 'card'>(savedLayoutMode || 'list')

  watch(layoutMode, (newValue) => {
    localStorage.setItem('layoutMode_userStatusHistory', newValue)
  })

  /**
   * Handles API response by updating the userStatusHistorys and pagination
   * @param data Array of UserStatusHistory returned by the API
   * @param err Optional error message
   * @param pagination Optional pagination data
   */
  const handleResponse = (
    data: UserStatusHistory[],
    err: string | undefined,
    pagination?: PaginationData,
    concatData: boolean = false,
  ) => {
    if (err) {
      message.value = err
    }
    if (concatData) {
      userStatusHistorys.value = userStatusHistorys.value.concat(data)
    } else {
      userStatusHistorys.value = data
    }
    if (pagination !== undefined) {
      paginationData.value = new PaginationData(pagination)
    }
  }

  const loadColumnConfig = async () => {
    try {
      const response = await columnConfigService.getVisibleFields('userStatusHistory', 'list')
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
        'userStatusHistory',
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
  /** Navigate to userStatusHistory detail view */
  const viewUserStatusHistory = (userStatusHistory: UserStatusHistory) => {
    router.push({
      name: 'userStatusHistorydetailsview',
      params: { id: userStatusHistory.getKeyValue() },
    })
  }

  /** Navigate to userStatusHistory list view */
  const goToListView = () => {
    router.push({ path: '/userStatusHistorys' })
  }

  /** Navigate to userStatusHistory create form */
  const goToCreateFormView = () => {
    router.push({ path: '/userStatusHistorys/create' })
  }

  /** Navigate to userStatusHistory update form */
  const goToUpdateFormView = (userStatusHistory: UserStatusHistory) => {
    router.push({
      name: 'userStatusHistoryupdateview',
      params: { id: userStatusHistory.getKeyValue() },
    })
  }

  /**
   * Delete a userStatusHistory by id
   * @param userStatusHistory UserStatusHistory object to delete
   */
  const deleteUserStatusHistory = async (userStatusHistory: UserStatusHistory) => {
    const result = await userStatusHistoryService.remove(userStatusHistory.getKeyValue())
    if (result.error) {
      message.value = result.error
    }
    return result
  }

  /**
   * Create a new userStatusHistory
   * @param userStatusHistoryFormDTO UserStatusHistory object to create
   */
  const createUserStatusHistory = async (
    userStatusHistoryFormDTO: Partial<UserStatusHistoryFormDTO>,
  ) => {
    const dto = new UserStatusHistoryFormDTO(userStatusHistoryFormDTO)
    const userStatusHistory: Partial<UserStatusHistory> = await dto.toEntity()
    const { data, error, errors } = await userStatusHistoryService.create(userStatusHistory)
    if (error) {
      message.value = error
    }
    return { data, errors }
  }

  /**
   * Update an existing userStatusHistory
   * @param userStatusHistoryFormDTO UserStatusHistory object to update
   */
  const updateUserStatusHistory = async (
    id: number | string,
    userStatusHistoryFormDTO: Partial<UserStatusHistoryFormDTO>,
  ) => {
    const dto = new UserStatusHistoryFormDTO(userStatusHistoryFormDTO)
    const userStatusHistory: Partial<UserStatusHistory> = await dto.toEntity()
    const { data, error } = await userStatusHistoryService.update(id, userStatusHistory)
    if (error) {
      message.value = error
    }
    return data
  }

  // API Requests
  /** Load all userStatusHistorys from the API */
  const loadUserStatusHistorys = async (
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
    } = await userStatusHistoryService.getAll(pagination, sortFields)
    handleResponse(data, err, paginationresult)

    if (unpagined && userStatusHistorys.value.length > 0) {
      while (paginationData.value.hasNext()) {
        paginationData.value.nextPage()
        const {
          data: nextData,
          error: nextErr,
          pagination: nextPagination,
        } = await userStatusHistoryService.getAll(paginationData.value.toParameter(), sortFields)
        handleResponse(nextData, nextErr, nextPagination, true)
      }
    }
    stopLoading()
  }

  /**
   * Get a single userStatusHistory by id
   * @param id UserStatusHistory ID
   */
  const getUserStatusHistoryById = async (id: number | string) => {
    const result = await userStatusHistoryService.getById(id)
    if (result.error) {
      message.value = result.error
    }
    return result
  }

  /**
   * Search userStatusHistorys with filters, pagination, and sorting
   * @param filters Partial UserStatusHistory object containing search filters
   * @param pagination Pagination request parameters
   * @param sortFields Array of fields to sort
   */
  const searchUserStatusHistorys = async (
    unpagined: boolean = false,
    filters: Partial<UserStatusHistory>,
    pagination: PaginationRequestParameter,
    sortFields: SortFieldParameter[],
  ) => {
    message.value = null
    startLoading()
    const {
      data,
      error: err,
      pagination: paginationresult,
    } = await userStatusHistoryService.search(filters, pagination, sortFields)
    handleResponse(data, err, paginationresult)

    if (unpagined) {
      while (paginationData.value.hasNext()) {
        paginationData.value.nextPage()
        const {
          data: nextData,
          error: nextErr,
          pagination: nextPagination,
        } = await userStatusHistoryService.search(
          filters,
          paginationData.value.toParameter(),
          sortFields,
        )
        handleResponse(nextData, nextErr, nextPagination, true)
      }
    }
    stopLoading()
  }
  const exportUserStatusHistorysToCsv = async (data: Partial<UserStatusHistory[]>) => {
    console.log('Exporting UserStatusHistory to CSV', data)
    await userStatusHistoryService.exportCsv(data)
  }

  const getAllUserStatusHistorysByUserId = async (
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
    } = await userStatusHistoryService.getAllByUserId(userId, pagination, sortFields)
    handleResponse(data, err, paginationresult)

    if (unpagined) {
      while (paginationData.value.hasNext() && userStatusHistorys.value.length > 0) {
        paginationData.value.nextPage()
        const {
          data: nextData,
          error: nextErr,
          pagination: nextPagination,
        } = await userStatusHistoryService.getAllByUserId(
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
    userStatusHistorys,
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
    exportUserStatusHistorysToCsv,
    loadUserStatusHistorys,
    searchUserStatusHistorys,
    getUserStatusHistoryById,
    viewUserStatusHistory,
    goToListView,
    goToCreateFormView,
    goToUpdateFormView,
    deleteUserStatusHistory,
    createUserStatusHistory,
    updateUserStatusHistory,
    getAllUserStatusHistorysByUserId,
  }
}
