import { ref, watch } from 'vue'
import { ReportStatuse, ReportStatuseFormDTO } from '@/models/ReportStatuseModel'
import * as reportStatuseService from '@/services/ReportStatuseService'
import * as columnConfigService from '@/services/ColumnConfigService'
import { useLoading } from './useLoading'
import { PaginationRequestParameter, SortFieldParameter } from '@/models/api/RequestModel'
import { PaginationData } from '@/models/api/PageResponseModel'
import { useRouter } from 'vue-router'

export const DEFAULT_LIST_VIEW_FIELDS = ['id']

/**
 * Composable to manage ReportStatuse entities including CRUD operations,
 * search, pagination, foreign key loading, and navigation.
 */
export function useReportStatuses() {
  /** List of all reportStatuses */
  const reportStatuses = ref<ReportStatuse[]>([])

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
  const savedLayoutMode = localStorage.getItem('layoutMode_reportStatuse') as 'list' | 'card' | null
  const layoutMode = ref<'list' | 'card'>(savedLayoutMode || 'list')

  watch(layoutMode, (newValue) => {
    localStorage.setItem('layoutMode_reportStatuse', newValue)
  })

  /**
   * Handles API response by updating the reportStatuses and pagination
   * @param data Array of ReportStatuse returned by the API
   * @param err Optional error message
   * @param pagination Optional pagination data
   */
  const handleResponse = (
    data: ReportStatuse[],
    err: string | undefined,
    pagination?: PaginationData,
    concatData: boolean = false,
  ) => {
    if (err) {
      message.value = err
    }
    if (concatData) {
      reportStatuses.value = reportStatuses.value.concat(data)
    } else {
      reportStatuses.value = data
    }
    if (pagination !== undefined) {
      paginationData.value = new PaginationData(pagination)
    }
  }

  const loadColumnConfig = async () => {
    try {
      const response = await columnConfigService.getVisibleFields('reportStatuse', 'list')
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
        'reportStatuse',
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
  /** Navigate to reportStatuse detail view */
  const viewReportStatuse = (reportStatuse: ReportStatuse) => {
    router.push({ name: 'reportStatusedetailsview', params: { id: reportStatuse.getKeyValue() } })
  }

  /** Navigate to reportStatuse list view */
  const goToListView = () => {
    router.push({ path: '/reportStatuses' })
  }

  /** Navigate to reportStatuse create form */
  const goToCreateFormView = () => {
    router.push({ path: '/reportStatuses/create' })
  }

  /** Navigate to reportStatuse update form */
  const goToUpdateFormView = (reportStatuse: ReportStatuse) => {
    router.push({ name: 'reportStatuseupdateview', params: { id: reportStatuse.getKeyValue() } })
  }

  /**
   * Delete a reportStatuse by id
   * @param reportStatuse ReportStatuse object to delete
   */
  const deleteReportStatuse = async (reportStatuse: ReportStatuse) => {
    const result = await reportStatuseService.remove(reportStatuse.getKeyValue())
    if (result.error) {
      message.value = result.error
    }
    return result
  }

  /**
   * Create a new reportStatuse
   * @param reportStatuseFormDTO ReportStatuse object to create
   */
  const createReportStatuse = async (reportStatuseFormDTO: Partial<ReportStatuseFormDTO>) => {
    const dto = new ReportStatuseFormDTO(reportStatuseFormDTO)
    const reportStatuse: Partial<ReportStatuse> = await dto.toEntity()
    const { data, error, errors } = await reportStatuseService.create(reportStatuse)
    if (error) {
      message.value = error
    }
    return { data, errors }
  }

  /**
   * Update an existing reportStatuse
   * @param reportStatuseFormDTO ReportStatuse object to update
   */
  const updateReportStatuse = async (
    id: number | string,
    reportStatuseFormDTO: Partial<ReportStatuseFormDTO>,
  ) => {
    const dto = new ReportStatuseFormDTO(reportStatuseFormDTO)
    const reportStatuse: Partial<ReportStatuse> = await dto.toEntity()
    const { data, error } = await reportStatuseService.update(id, reportStatuse)
    if (error) {
      message.value = error
    }
    return data
  }

  // API Requests
  /** Load all reportStatuses from the API */
  const loadReportStatuses = async (
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
    } = await reportStatuseService.getAll(pagination, sortFields)
    handleResponse(data, err, paginationresult)

    if (unpagined && reportStatuses.value.length > 0) {
      while (paginationData.value.hasNext()) {
        paginationData.value.nextPage()
        const {
          data: nextData,
          error: nextErr,
          pagination: nextPagination,
        } = await reportStatuseService.getAll(paginationData.value.toParameter(), sortFields)
        handleResponse(nextData, nextErr, nextPagination, true)
      }
    }
    stopLoading()
  }

  /**
   * Get a single reportStatuse by id
   * @param id ReportStatuse ID
   */
  const getReportStatuseById = async (id: number | string) => {
    const result = await reportStatuseService.getById(id)
    if (result.error) {
      message.value = result.error
    }
    return result
  }

  /**
   * Search reportStatuses with filters, pagination, and sorting
   * @param filters Partial ReportStatuse object containing search filters
   * @param pagination Pagination request parameters
   * @param sortFields Array of fields to sort
   */
  const searchReportStatuses = async (
    unpagined: boolean = false,
    filters: Partial<ReportStatuse>,
    pagination: PaginationRequestParameter,
    sortFields: SortFieldParameter[],
  ) => {
    message.value = null
    startLoading()
    const {
      data,
      error: err,
      pagination: paginationresult,
    } = await reportStatuseService.search(filters, pagination, sortFields)
    handleResponse(data, err, paginationresult)

    if (unpagined) {
      while (paginationData.value.hasNext()) {
        paginationData.value.nextPage()
        const {
          data: nextData,
          error: nextErr,
          pagination: nextPagination,
        } = await reportStatuseService.search(
          filters,
          paginationData.value.toParameter(),
          sortFields,
        )
        handleResponse(nextData, nextErr, nextPagination, true)
      }
    }
    stopLoading()
  }
  const exportReportStatusesToCsv = async (data: Partial<ReportStatuse[]>) => {
    console.log('Exporting ReportStatuse to CSV', data)
    await reportStatuseService.exportCsv(data)
  }

  /** Getter for pagination data */
  const getPaginationData = () => paginationData.value

  return {
    // state
    reportStatuses,
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
    exportReportStatusesToCsv,
    loadReportStatuses,
    searchReportStatuses,
    getReportStatuseById,
    viewReportStatuse,
    goToListView,
    goToCreateFormView,
    goToUpdateFormView,
    deleteReportStatuse,
    createReportStatuse,
    updateReportStatuse,
  }
}
