import { ref, watch } from 'vue'
import { UserRole, UserRoleFormDTO } from '@/models/UserRoleModel'
import * as userRoleService from '@/services/UserRoleService'
import * as columnConfigService from '@/services/ColumnConfigService'
import { useLoading } from './useLoading'
import { PaginationRequestParameter, SortFieldParameter } from '@/models/api/RequestModel'
import { PaginationData } from '@/models/api/PageResponseModel'
import { useRouter } from 'vue-router'

export const DEFAULT_LIST_VIEW_FIELDS = ['id']

/**
 * Composable to manage UserRole entities including CRUD operations,
 * search, pagination, foreign key loading, and navigation.
 */
export function useUserRoles() {
  /** List of all userRoles */
  const userRoles = ref<UserRole[]>([])

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
  const savedLayoutMode = localStorage.getItem('layoutMode_userRole') as 'list' | 'card' | null
  const layoutMode = ref<'list' | 'card'>(savedLayoutMode || 'list')

  watch(layoutMode, (newValue) => {
    localStorage.setItem('layoutMode_userRole', newValue)
  })

  /**
   * Handles API response by updating the userRoles and pagination
   * @param data Array of UserRole returned by the API
   * @param err Optional error message
   * @param pagination Optional pagination data
   */
  const handleResponse = (
    data: UserRole[],
    err: string | undefined,
    pagination?: PaginationData,
    concatData: boolean = false,
  ) => {
    if (err) {
      message.value = err
    }
    if (concatData) {
      userRoles.value = userRoles.value.concat(data)
    } else {
      userRoles.value = data
    }
    if (pagination !== undefined) {
      paginationData.value = new PaginationData(pagination)
    }
  }

  const loadColumnConfig = async () => {
    try {
      const response = await columnConfigService.getVisibleFields('userRole', 'list')
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
      const response = await columnConfigService.updateVisibleFields('userRole', 'list', fields)
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
  /** Navigate to userRole detail view */
  const viewUserRole = (userRole: UserRole) => {
    router.push({ name: 'userRoledetailsview', params: { id: userRole.getKeyValue() } })
  }

  /** Navigate to userRole list view */
  const goToListView = () => {
    router.push({ path: '/userRoles' })
  }

  /** Navigate to userRole create form */
  const goToCreateFormView = () => {
    router.push({ path: '/userRoles/create' })
  }

  /** Navigate to userRole update form */
  const goToUpdateFormView = (userRole: UserRole) => {
    router.push({ name: 'userRoleupdateview', params: { id: userRole.getKeyValue() } })
  }

  /**
   * Delete a userRole by id
   * @param userRole UserRole object to delete
   */
  const deleteUserRole = async (userRole: UserRole) => {
    const result = await userRoleService.remove(userRole.getKeyValue())
    if (result.error) {
      message.value = result.error
    }
    return result
  }

  /**
   * Create a new userRole
   * @param userRoleFormDTO UserRole object to create
   */
  const createUserRole = async (userRoleFormDTO: Partial<UserRoleFormDTO>) => {
    const dto = new UserRoleFormDTO(userRoleFormDTO)
    const userRole: Partial<UserRole> = await dto.toEntity()
    const { data, error, errors } = await userRoleService.create(userRole)
    if (error) {
      message.value = error
    }
    return { data, errors }
  }

  /**
   * Update an existing userRole
   * @param userRoleFormDTO UserRole object to update
   */
  const updateUserRole = async (id: number | string, userRoleFormDTO: Partial<UserRoleFormDTO>) => {
    const dto = new UserRoleFormDTO(userRoleFormDTO)
    const userRole: Partial<UserRole> = await dto.toEntity()
    const { data, error } = await userRoleService.update(id, userRole)
    if (error) {
      message.value = error
    }
    return data
  }

  // API Requests
  /** Load all userRoles from the API */
  const loadUserRoles = async (
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
    } = await userRoleService.getAll(pagination, sortFields)
    handleResponse(data, err, paginationresult)

    if (unpagined && userRoles.value.length > 0) {
      while (paginationData.value.hasNext()) {
        paginationData.value.nextPage()
        const {
          data: nextData,
          error: nextErr,
          pagination: nextPagination,
        } = await userRoleService.getAll(paginationData.value.toParameter(), sortFields)
        handleResponse(nextData, nextErr, nextPagination, true)
      }
    }
    stopLoading()
  }

  /**
   * Get a single userRole by id
   * @param id UserRole ID
   */
  const getUserRoleById = async (id: number | string) => {
    const result = await userRoleService.getById(id)
    if (result.error) {
      message.value = result.error
    }
    return result
  }

  /**
   * Search userRoles with filters, pagination, and sorting
   * @param filters Partial UserRole object containing search filters
   * @param pagination Pagination request parameters
   * @param sortFields Array of fields to sort
   */
  const searchUserRoles = async (
    unpagined: boolean = false,
    filters: Partial<UserRole>,
    pagination: PaginationRequestParameter,
    sortFields: SortFieldParameter[],
  ) => {
    message.value = null
    startLoading()
    const {
      data,
      error: err,
      pagination: paginationresult,
    } = await userRoleService.search(filters, pagination, sortFields)
    handleResponse(data, err, paginationresult)

    if (unpagined) {
      while (paginationData.value.hasNext()) {
        paginationData.value.nextPage()
        const {
          data: nextData,
          error: nextErr,
          pagination: nextPagination,
        } = await userRoleService.search(filters, paginationData.value.toParameter(), sortFields)
        handleResponse(nextData, nextErr, nextPagination, true)
      }
    }
    stopLoading()
  }
  const exportUserRolesToCsv = async (data: Partial<UserRole[]>) => {
    console.log('Exporting UserRole to CSV', data)
    await userRoleService.exportCsv(data)
  }

  /** Getter for pagination data */
  const getPaginationData = () => paginationData.value

  return {
    // state
    userRoles,
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
    exportUserRolesToCsv,
    loadUserRoles,
    searchUserRoles,
    getUserRoleById,
    viewUserRole,
    goToListView,
    goToCreateFormView,
    goToUpdateFormView,
    deleteUserRole,
    createUserRole,
    updateUserRole,
  }
}
