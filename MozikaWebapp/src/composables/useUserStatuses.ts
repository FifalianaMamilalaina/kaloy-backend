import { ref, watch } from 'vue'
import { UserStatuse, UserStatuseFormDTO } from '@/models/UserStatuseModel'
import * as userStatuseService from '@/services/UserStatuseService'
import * as columnConfigService from '@/services/ColumnConfigService'
import { useLoading } from './useLoading'
import { PaginationRequestParameter, SortFieldParameter } from '@/models/api/RequestModel'
import { PaginationData } from '@/models/api/PageResponseModel'
import { useRouter } from 'vue-router'

export const DEFAULT_LIST_VIEW_FIELDS = ['id']

/**
 * Composable to manage UserStatuse entities including CRUD operations,
 * search, pagination, foreign key loading, and navigation.
 */
export function useUserStatuses() {
  /** List of all userStatuses */
  const userStatuses = ref<UserStatuse[]>([])

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
  const savedLayoutMode = localStorage.getItem('layoutMode_userStatuse') as 'list' | 'card' | null
  const layoutMode = ref<'list' | 'card'>(savedLayoutMode || 'list')

  watch(layoutMode, (newValue) => {
    localStorage.setItem('layoutMode_userStatuse', newValue)
  })

  /**
   * Handles API response by updating the userStatuses and pagination
   * @param data Array of UserStatuse returned by the API
   * @param err Optional error message
   * @param pagination Optional pagination data
   */
  const handleResponse = (
    data: UserStatuse[],
    err: string | undefined,
    pagination?: PaginationData,
    concatData: boolean = false,
  ) => {
    if (err) {
      message.value = err
    }
    if (concatData) {
      userStatuses.value = userStatuses.value.concat(data)
    } else {
      userStatuses.value = data
    }
    if (pagination !== undefined) {
      paginationData.value = new PaginationData(pagination)
    }
  }

  const loadColumnConfig = async () => {
    try {
      const response = await columnConfigService.getVisibleFields('userStatuse', 'list')
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
      const response = await columnConfigService.updateVisibleFields('userStatuse', 'list', fields)
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
  /** Navigate to userStatuse detail view */
  const viewUserStatuse = (userStatuse: UserStatuse) => {
    router.push({ name: 'userStatusedetailsview', params: { id: userStatuse.getKeyValue() } })
  }

  /** Navigate to userStatuse list view */
  const goToListView = () => {
    router.push({ path: '/userStatuses' })
  }

  /** Navigate to userStatuse create form */
  const goToCreateFormView = () => {
    router.push({ path: '/userStatuses/create' })
  }

  /** Navigate to userStatuse update form */
  const goToUpdateFormView = (userStatuse: UserStatuse) => {
    router.push({ name: 'userStatuseupdateview', params: { id: userStatuse.getKeyValue() } })
  }

  /**
   * Delete a userStatuse by id
   * @param userStatuse UserStatuse object to delete
   */
  const deleteUserStatuse = async (userStatuse: UserStatuse) => {
    const result = await userStatuseService.remove(userStatuse.getKeyValue())
    if (result.error) {
      message.value = result.error
    }
    return result
  }

  /**
   * Create a new userStatuse
   * @param userStatuseFormDTO UserStatuse object to create
   */
  const createUserStatuse = async (userStatuseFormDTO: Partial<UserStatuseFormDTO>) => {
    const dto = new UserStatuseFormDTO(userStatuseFormDTO)
    const userStatuse: Partial<UserStatuse> = await dto.toEntity()
    const { data, error, errors } = await userStatuseService.create(userStatuse)
    if (error) {
      message.value = error
    }
    return { data, errors }
  }

  /**
   * Update an existing userStatuse
   * @param userStatuseFormDTO UserStatuse object to update
   */
  const updateUserStatuse = async (
    id: number | string,
    userStatuseFormDTO: Partial<UserStatuseFormDTO>,
  ) => {
    const dto = new UserStatuseFormDTO(userStatuseFormDTO)
    const userStatuse: Partial<UserStatuse> = await dto.toEntity()
    const { data, error } = await userStatuseService.update(id, userStatuse)
    if (error) {
      message.value = error
    }
    return data
  }

  // API Requests
  /** Load all userStatuses from the API */
  const loadUserStatuses = async (
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
    } = await userStatuseService.getAll(pagination, sortFields)
    handleResponse(data, err, paginationresult)

    if (unpagined && userStatuses.value.length > 0) {
      while (paginationData.value.hasNext()) {
        paginationData.value.nextPage()
        const {
          data: nextData,
          error: nextErr,
          pagination: nextPagination,
        } = await userStatuseService.getAll(paginationData.value.toParameter(), sortFields)
        handleResponse(nextData, nextErr, nextPagination, true)
      }
    }
    stopLoading()
  }

  /**
   * Get a single userStatuse by id
   * @param id UserStatuse ID
   */
  const getUserStatuseById = async (id: number | string) => {
    const result = await userStatuseService.getById(id)
    if (result.error) {
      message.value = result.error
    }
    return result
  }

  /**
   * Search userStatuses with filters, pagination, and sorting
   * @param filters Partial UserStatuse object containing search filters
   * @param pagination Pagination request parameters
   * @param sortFields Array of fields to sort
   */
  const searchUserStatuses = async (
    unpagined: boolean = false,
    filters: Partial<UserStatuse>,
    pagination: PaginationRequestParameter,
    sortFields: SortFieldParameter[],
  ) => {
    message.value = null
    startLoading()
    const {
      data,
      error: err,
      pagination: paginationresult,
    } = await userStatuseService.search(filters, pagination, sortFields)
    handleResponse(data, err, paginationresult)

    if (unpagined) {
      while (paginationData.value.hasNext()) {
        paginationData.value.nextPage()
        const {
          data: nextData,
          error: nextErr,
          pagination: nextPagination,
        } = await userStatuseService.search(filters, paginationData.value.toParameter(), sortFields)
        handleResponse(nextData, nextErr, nextPagination, true)
      }
    }
    stopLoading()
  }
  const exportUserStatusesToCsv = async (data: Partial<UserStatuse[]>) => {
    console.log('Exporting UserStatuse to CSV', data)
    await userStatuseService.exportCsv(data)
  }

  /** Getter for pagination data */
  const getPaginationData = () => paginationData.value

  return {
    // state
    userStatuses,
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
    exportUserStatusesToCsv,
    loadUserStatuses,
    searchUserStatuses,
    getUserStatuseById,
    viewUserStatuse,
    goToListView,
    goToCreateFormView,
    goToUpdateFormView,
    deleteUserStatuse,
    createUserStatuse,
    updateUserStatuse,
  }
}
