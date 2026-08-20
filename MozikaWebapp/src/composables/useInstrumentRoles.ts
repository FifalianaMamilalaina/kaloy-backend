import { ref, watch } from 'vue'
import { InstrumentRole, InstrumentRoleFormDTO } from '@/models/InstrumentRoleModel'
import * as instrumentRoleService from '@/services/InstrumentRoleService'
import * as columnConfigService from '@/services/ColumnConfigService'
import { useLoading } from './useLoading'
import { PaginationRequestParameter, SortFieldParameter } from '@/models/api/RequestModel'
import { PaginationData } from '@/models/api/PageResponseModel'
import { useRouter } from 'vue-router'

export const DEFAULT_LIST_VIEW_FIELDS = ['id']

/**
 * Composable to manage InstrumentRole entities including CRUD operations,
 * search, pagination, foreign key loading, and navigation.
 */
export function useInstrumentRoles() {
  /** List of all instrumentRoles */
  const instrumentRoles = ref<InstrumentRole[]>([])

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
  const savedLayoutMode = localStorage.getItem('layoutMode_instrumentRole') as
    | 'list'
    | 'card'
    | null
  const layoutMode = ref<'list' | 'card'>(savedLayoutMode || 'list')

  watch(layoutMode, (newValue) => {
    localStorage.setItem('layoutMode_instrumentRole', newValue)
  })

  /**
   * Handles API response by updating the instrumentRoles and pagination
   * @param data Array of InstrumentRole returned by the API
   * @param err Optional error message
   * @param pagination Optional pagination data
   */
  const handleResponse = (
    data: InstrumentRole[],
    err: string | undefined,
    pagination?: PaginationData,
    concatData: boolean = false,
  ) => {
    if (err) {
      message.value = err
    }
    if (concatData) {
      instrumentRoles.value = instrumentRoles.value.concat(data)
    } else {
      instrumentRoles.value = data
    }
    if (pagination !== undefined) {
      paginationData.value = new PaginationData(pagination)
    }
  }

  const loadColumnConfig = async () => {
    try {
      const response = await columnConfigService.getVisibleFields('instrumentRole', 'list')
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
        'instrumentRole',
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
  /** Navigate to instrumentRole detail view */
  const viewInstrumentRole = (instrumentRole: InstrumentRole) => {
    router.push({ name: 'instrumentRoledetailsview', params: { id: instrumentRole.getKeyValue() } })
  }

  /** Navigate to instrumentRole list view */
  const goToListView = () => {
    router.push({ path: '/instrumentRoles' })
  }

  /** Navigate to instrumentRole create form */
  const goToCreateFormView = () => {
    router.push({ path: '/instrumentRoles/create' })
  }

  /** Navigate to instrumentRole update form */
  const goToUpdateFormView = (instrumentRole: InstrumentRole) => {
    router.push({ name: 'instrumentRoleupdateview', params: { id: instrumentRole.getKeyValue() } })
  }

  /**
   * Delete a instrumentRole by id
   * @param instrumentRole InstrumentRole object to delete
   */
  const deleteInstrumentRole = async (instrumentRole: InstrumentRole) => {
    const result = await instrumentRoleService.remove(instrumentRole.getKeyValue())
    if (result.error) {
      message.value = result.error
    }
    return result
  }

  /**
   * Create a new instrumentRole
   * @param instrumentRoleFormDTO InstrumentRole object to create
   */
  const createInstrumentRole = async (instrumentRoleFormDTO: Partial<InstrumentRoleFormDTO>) => {
    const dto = new InstrumentRoleFormDTO(instrumentRoleFormDTO)
    const instrumentRole: Partial<InstrumentRole> = await dto.toEntity()
    const { data, error, errors } = await instrumentRoleService.create(instrumentRole)
    if (error) {
      message.value = error
    }
    return { data, errors }
  }

  /**
   * Update an existing instrumentRole
   * @param instrumentRoleFormDTO InstrumentRole object to update
   */
  const updateInstrumentRole = async (
    id: number | string,
    instrumentRoleFormDTO: Partial<InstrumentRoleFormDTO>,
  ) => {
    const dto = new InstrumentRoleFormDTO(instrumentRoleFormDTO)
    const instrumentRole: Partial<InstrumentRole> = await dto.toEntity()
    const { data, error } = await instrumentRoleService.update(id, instrumentRole)
    if (error) {
      message.value = error
    }
    return data
  }

  // API Requests
  /** Load all instrumentRoles from the API */
  const loadInstrumentRoles = async (
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
    } = await instrumentRoleService.getAll(pagination, sortFields)
    handleResponse(data, err, paginationresult)

    if (unpagined && instrumentRoles.value.length > 0) {
      while (paginationData.value.hasNext()) {
        paginationData.value.nextPage()
        const {
          data: nextData,
          error: nextErr,
          pagination: nextPagination,
        } = await instrumentRoleService.getAll(paginationData.value.toParameter(), sortFields)
        handleResponse(nextData, nextErr, nextPagination, true)
      }
    }
    stopLoading()
  }

  /**
   * Get a single instrumentRole by id
   * @param id InstrumentRole ID
   */
  const getInstrumentRoleById = async (id: number | string) => {
    const result = await instrumentRoleService.getById(id)
    if (result.error) {
      message.value = result.error
    }
    return result
  }

  /**
   * Search instrumentRoles with filters, pagination, and sorting
   * @param filters Partial InstrumentRole object containing search filters
   * @param pagination Pagination request parameters
   * @param sortFields Array of fields to sort
   */
  const searchInstrumentRoles = async (
    unpagined: boolean = false,
    filters: Partial<InstrumentRole>,
    pagination: PaginationRequestParameter,
    sortFields: SortFieldParameter[],
  ) => {
    message.value = null
    startLoading()
    const {
      data,
      error: err,
      pagination: paginationresult,
    } = await instrumentRoleService.search(filters, pagination, sortFields)
    handleResponse(data, err, paginationresult)

    if (unpagined) {
      while (paginationData.value.hasNext()) {
        paginationData.value.nextPage()
        const {
          data: nextData,
          error: nextErr,
          pagination: nextPagination,
        } = await instrumentRoleService.search(
          filters,
          paginationData.value.toParameter(),
          sortFields,
        )
        handleResponse(nextData, nextErr, nextPagination, true)
      }
    }
    stopLoading()
  }
  const exportInstrumentRolesToCsv = async (data: Partial<InstrumentRole[]>) => {
    console.log('Exporting InstrumentRole to CSV', data)
    await instrumentRoleService.exportCsv(data)
  }

  /** Getter for pagination data */
  const getPaginationData = () => paginationData.value

  return {
    // state
    instrumentRoles,
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
    exportInstrumentRolesToCsv,
    loadInstrumentRoles,
    searchInstrumentRoles,
    getInstrumentRoleById,
    viewInstrumentRole,
    goToListView,
    goToCreateFormView,
    goToUpdateFormView,
    deleteInstrumentRole,
    createInstrumentRole,
    updateInstrumentRole,
  }
}
