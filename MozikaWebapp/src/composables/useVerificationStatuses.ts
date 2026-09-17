import { ref, watch } from 'vue'
import { VerificationStatuse, VerificationStatuseFormDTO } from '@/models/VerificationStatuseModel'
import * as verificationStatuseService from '@/services/VerificationStatuseService'
import * as columnConfigService from '@/services/ColumnConfigService'
import { useLoading } from './useLoading'
import { PaginationRequestParameter, SortFieldParameter } from '@/models/api/RequestModel'
import { PaginationData } from '@/models/api/PageResponseModel'
import { useRouter } from 'vue-router'

export const DEFAULT_LIST_VIEW_FIELDS = ['id']

/**
 * Composable to manage VerificationStatuse entities including CRUD operations,
 * search, pagination, foreign key loading, and navigation.
 */
export function useVerificationStatuses() {
  /** List of all verificationStatuses */
  const verificationStatuses = ref<VerificationStatuse[]>([])

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
  const savedLayoutMode = localStorage.getItem('layoutMode_verificationStatuse') as
    | 'list'
    | 'card'
    | null
  const layoutMode = ref<'list' | 'card'>(savedLayoutMode || 'list')

  watch(layoutMode, (newValue) => {
    localStorage.setItem('layoutMode_verificationStatuse', newValue)
  })

  /**
   * Handles API response by updating the verificationStatuses and pagination
   * @param data Array of VerificationStatuse returned by the API
   * @param err Optional error message
   * @param pagination Optional pagination data
   */
  const handleResponse = (
    data: VerificationStatuse[],
    err: string | undefined,
    pagination?: PaginationData,
    concatData: boolean = false,
  ) => {
    if (err) {
      message.value = err
    }
    if (concatData) {
      verificationStatuses.value = verificationStatuses.value.concat(data)
    } else {
      verificationStatuses.value = data
    }
    if (pagination !== undefined) {
      paginationData.value = new PaginationData(pagination)
    }
  }

  const loadColumnConfig = async () => {
    try {
      const response = await columnConfigService.getVisibleFields('verificationStatuse', 'list')
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
        'verificationStatuse',
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
  /** Navigate to verificationStatuse detail view */
  const viewVerificationStatuse = (verificationStatuse: VerificationStatuse) => {
    router.push({
      name: 'verificationStatusedetailsview',
      params: { id: verificationStatuse.getKeyValue() },
    })
  }

  /** Navigate to verificationStatuse list view */
  const goToListView = () => {
    router.push({ path: '/verificationStatuses' })
  }

  /** Navigate to verificationStatuse create form */
  const goToCreateFormView = () => {
    router.push({ path: '/verificationStatuses/create' })
  }

  /** Navigate to verificationStatuse update form */
  const goToUpdateFormView = (verificationStatuse: VerificationStatuse) => {
    router.push({
      name: 'verificationStatuseupdateview',
      params: { id: verificationStatuse.getKeyValue() },
    })
  }

  /**
   * Delete a verificationStatuse by id
   * @param verificationStatuse VerificationStatuse object to delete
   */
  const deleteVerificationStatuse = async (verificationStatuse: VerificationStatuse) => {
    const result = await verificationStatuseService.remove(verificationStatuse.getKeyValue())
    if (result.error) {
      message.value = result.error
    }
    return result
  }

  /**
   * Create a new verificationStatuse
   * @param verificationStatuseFormDTO VerificationStatuse object to create
   */
  const createVerificationStatuse = async (
    verificationStatuseFormDTO: Partial<VerificationStatuseFormDTO>,
  ) => {
    const dto = new VerificationStatuseFormDTO(verificationStatuseFormDTO)
    const verificationStatuse: Partial<VerificationStatuse> = await dto.toEntity()
    const { data, error, errors } = await verificationStatuseService.create(verificationStatuse)
    if (error) {
      message.value = error
    }
    return { data, errors }
  }

  /**
   * Update an existing verificationStatuse
   * @param verificationStatuseFormDTO VerificationStatuse object to update
   */
  const updateVerificationStatuse = async (
    id: number | string,
    verificationStatuseFormDTO: Partial<VerificationStatuseFormDTO>,
  ) => {
    const dto = new VerificationStatuseFormDTO(verificationStatuseFormDTO)
    const verificationStatuse: Partial<VerificationStatuse> = await dto.toEntity()
    const { data, error } = await verificationStatuseService.update(id, verificationStatuse)
    if (error) {
      message.value = error
    }
    return data
  }

  // API Requests
  /** Load all verificationStatuses from the API */
  const loadVerificationStatuses = async (
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
    } = await verificationStatuseService.getAll(pagination, sortFields)
    handleResponse(data, err, paginationresult)

    if (unpagined && verificationStatuses.value.length > 0) {
      while (paginationData.value.hasNext()) {
        paginationData.value.nextPage()
        const {
          data: nextData,
          error: nextErr,
          pagination: nextPagination,
        } = await verificationStatuseService.getAll(paginationData.value.toParameter(), sortFields)
        handleResponse(nextData, nextErr, nextPagination, true)
      }
    }
    stopLoading()
  }

  /**
   * Get a single verificationStatuse by id
   * @param id VerificationStatuse ID
   */
  const getVerificationStatuseById = async (id: number | string) => {
    const result = await verificationStatuseService.getById(id)
    if (result.error) {
      message.value = result.error
    }
    return result
  }

  /**
   * Search verificationStatuses with filters, pagination, and sorting
   * @param filters Partial VerificationStatuse object containing search filters
   * @param pagination Pagination request parameters
   * @param sortFields Array of fields to sort
   */
  const searchVerificationStatuses = async (
    unpagined: boolean = false,
    filters: Partial<VerificationStatuse>,
    pagination: PaginationRequestParameter,
    sortFields: SortFieldParameter[],
  ) => {
    message.value = null
    startLoading()
    const {
      data,
      error: err,
      pagination: paginationresult,
    } = await verificationStatuseService.search(filters, pagination, sortFields)
    handleResponse(data, err, paginationresult)

    if (unpagined) {
      while (paginationData.value.hasNext()) {
        paginationData.value.nextPage()
        const {
          data: nextData,
          error: nextErr,
          pagination: nextPagination,
        } = await verificationStatuseService.search(
          filters,
          paginationData.value.toParameter(),
          sortFields,
        )
        handleResponse(nextData, nextErr, nextPagination, true)
      }
    }
    stopLoading()
  }
  const exportVerificationStatusesToCsv = async (data: Partial<VerificationStatuse[]>) => {
    console.log('Exporting VerificationStatuse to CSV', data)
    await verificationStatuseService.exportCsv(data)
  }

  /** Getter for pagination data */
  const getPaginationData = () => paginationData.value

  return {
    // state
    verificationStatuses,
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
    exportVerificationStatusesToCsv,
    loadVerificationStatuses,
    searchVerificationStatuses,
    getVerificationStatuseById,
    viewVerificationStatuse,
    goToListView,
    goToCreateFormView,
    goToUpdateFormView,
    deleteVerificationStatuse,
    createVerificationStatuse,
    updateVerificationStatuse,
  }
}
