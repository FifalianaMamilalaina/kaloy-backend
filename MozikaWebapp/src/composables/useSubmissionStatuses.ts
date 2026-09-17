import { ref, watch } from 'vue'
import { SubmissionStatuse, SubmissionStatuseFormDTO } from '@/models/SubmissionStatuseModel'
import * as submissionStatuseService from '@/services/SubmissionStatuseService'
import * as columnConfigService from '@/services/ColumnConfigService'
import { useLoading } from './useLoading'
import { PaginationRequestParameter, SortFieldParameter } from '@/models/api/RequestModel'
import { PaginationData } from '@/models/api/PageResponseModel'
import { useRouter } from 'vue-router'

export const DEFAULT_LIST_VIEW_FIELDS = ['id']

/**
 * Composable to manage SubmissionStatuse entities including CRUD operations,
 * search, pagination, foreign key loading, and navigation.
 */
export function useSubmissionStatuses() {
  /** List of all submissionStatuses */
  const submissionStatuses = ref<SubmissionStatuse[]>([])

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
  const savedLayoutMode = localStorage.getItem('layoutMode_submissionStatuse') as
    | 'list'
    | 'card'
    | null
  const layoutMode = ref<'list' | 'card'>(savedLayoutMode || 'list')

  watch(layoutMode, (newValue) => {
    localStorage.setItem('layoutMode_submissionStatuse', newValue)
  })

  /**
   * Handles API response by updating the submissionStatuses and pagination
   * @param data Array of SubmissionStatuse returned by the API
   * @param err Optional error message
   * @param pagination Optional pagination data
   */
  const handleResponse = (
    data: SubmissionStatuse[],
    err: string | undefined,
    pagination?: PaginationData,
    concatData: boolean = false,
  ) => {
    if (err) {
      message.value = err
    }
    if (concatData) {
      submissionStatuses.value = submissionStatuses.value.concat(data)
    } else {
      submissionStatuses.value = data
    }
    if (pagination !== undefined) {
      paginationData.value = new PaginationData(pagination)
    }
  }

  const loadColumnConfig = async () => {
    try {
      const response = await columnConfigService.getVisibleFields('submissionStatuse', 'list')
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
        'submissionStatuse',
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
  /** Navigate to submissionStatuse detail view */
  const viewSubmissionStatuse = (submissionStatuse: SubmissionStatuse) => {
    router.push({
      name: 'submissionStatusedetailsview',
      params: { id: submissionStatuse.getKeyValue() },
    })
  }

  /** Navigate to submissionStatuse list view */
  const goToListView = () => {
    router.push({ path: '/submissionStatuses' })
  }

  /** Navigate to submissionStatuse create form */
  const goToCreateFormView = () => {
    router.push({ path: '/submissionStatuses/create' })
  }

  /** Navigate to submissionStatuse update form */
  const goToUpdateFormView = (submissionStatuse: SubmissionStatuse) => {
    router.push({
      name: 'submissionStatuseupdateview',
      params: { id: submissionStatuse.getKeyValue() },
    })
  }

  /**
   * Delete a submissionStatuse by id
   * @param submissionStatuse SubmissionStatuse object to delete
   */
  const deleteSubmissionStatuse = async (submissionStatuse: SubmissionStatuse) => {
    const result = await submissionStatuseService.remove(submissionStatuse.getKeyValue())
    if (result.error) {
      message.value = result.error
    }
    return result
  }

  /**
   * Create a new submissionStatuse
   * @param submissionStatuseFormDTO SubmissionStatuse object to create
   */
  const createSubmissionStatuse = async (
    submissionStatuseFormDTO: Partial<SubmissionStatuseFormDTO>,
  ) => {
    const dto = new SubmissionStatuseFormDTO(submissionStatuseFormDTO)
    const submissionStatuse: Partial<SubmissionStatuse> = await dto.toEntity()
    const { data, error, errors } = await submissionStatuseService.create(submissionStatuse)
    if (error) {
      message.value = error
    }
    return { data, errors }
  }

  /**
   * Update an existing submissionStatuse
   * @param submissionStatuseFormDTO SubmissionStatuse object to update
   */
  const updateSubmissionStatuse = async (
    id: number | string,
    submissionStatuseFormDTO: Partial<SubmissionStatuseFormDTO>,
  ) => {
    const dto = new SubmissionStatuseFormDTO(submissionStatuseFormDTO)
    const submissionStatuse: Partial<SubmissionStatuse> = await dto.toEntity()
    const { data, error } = await submissionStatuseService.update(id, submissionStatuse)
    if (error) {
      message.value = error
    }
    return data
  }

  // API Requests
  /** Load all submissionStatuses from the API */
  const loadSubmissionStatuses = async (
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
    } = await submissionStatuseService.getAll(pagination, sortFields)
    handleResponse(data, err, paginationresult)

    if (unpagined && submissionStatuses.value.length > 0) {
      while (paginationData.value.hasNext()) {
        paginationData.value.nextPage()
        const {
          data: nextData,
          error: nextErr,
          pagination: nextPagination,
        } = await submissionStatuseService.getAll(paginationData.value.toParameter(), sortFields)
        handleResponse(nextData, nextErr, nextPagination, true)
      }
    }
    stopLoading()
  }

  /**
   * Get a single submissionStatuse by id
   * @param id SubmissionStatuse ID
   */
  const getSubmissionStatuseById = async (id: number | string) => {
    const result = await submissionStatuseService.getById(id)
    if (result.error) {
      message.value = result.error
    }
    return result
  }

  /**
   * Search submissionStatuses with filters, pagination, and sorting
   * @param filters Partial SubmissionStatuse object containing search filters
   * @param pagination Pagination request parameters
   * @param sortFields Array of fields to sort
   */
  const searchSubmissionStatuses = async (
    unpagined: boolean = false,
    filters: Partial<SubmissionStatuse>,
    pagination: PaginationRequestParameter,
    sortFields: SortFieldParameter[],
  ) => {
    message.value = null
    startLoading()
    const {
      data,
      error: err,
      pagination: paginationresult,
    } = await submissionStatuseService.search(filters, pagination, sortFields)
    handleResponse(data, err, paginationresult)

    if (unpagined) {
      while (paginationData.value.hasNext()) {
        paginationData.value.nextPage()
        const {
          data: nextData,
          error: nextErr,
          pagination: nextPagination,
        } = await submissionStatuseService.search(
          filters,
          paginationData.value.toParameter(),
          sortFields,
        )
        handleResponse(nextData, nextErr, nextPagination, true)
      }
    }
    stopLoading()
  }
  const exportSubmissionStatusesToCsv = async (data: Partial<SubmissionStatuse[]>) => {
    console.log('Exporting SubmissionStatuse to CSV', data)
    await submissionStatuseService.exportCsv(data)
  }

  /** Getter for pagination data */
  const getPaginationData = () => paginationData.value

  return {
    // state
    submissionStatuses,
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
    exportSubmissionStatusesToCsv,
    loadSubmissionStatuses,
    searchSubmissionStatuses,
    getSubmissionStatuseById,
    viewSubmissionStatuse,
    goToListView,
    goToCreateFormView,
    goToUpdateFormView,
    deleteSubmissionStatuse,
    createSubmissionStatuse,
    updateSubmissionStatuse,
  }
}
