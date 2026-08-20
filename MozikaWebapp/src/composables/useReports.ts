import { ref, watch } from 'vue'
import { Report, ReportFormDTO } from '@/models/ReportModel'
import * as reportService from '@/services/ReportService'
import * as columnConfigService from '@/services/ColumnConfigService'
import { useLoading } from './useLoading'
import { PaginationRequestParameter, SortFieldParameter } from '@/models/api/RequestModel'
import { PaginationData } from '@/models/api/PageResponseModel'
import { useRouter } from 'vue-router'

export const DEFAULT_LIST_VIEW_FIELDS = ['id']

/**
 * Composable to manage Report entities including CRUD operations,
 * search, pagination, foreign key loading, and navigation.
 */
export function useReports() {
  /** List of all reports */
  const reports = ref<Report[]>([])

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
  const savedLayoutMode = localStorage.getItem('layoutMode_report') as 'list' | 'card' | null
  const layoutMode = ref<'list' | 'card'>(savedLayoutMode || 'list')

  watch(layoutMode, (newValue) => {
    localStorage.setItem('layoutMode_report', newValue)
  })

  /**
   * Handles API response by updating the reports and pagination
   * @param data Array of Report returned by the API
   * @param err Optional error message
   * @param pagination Optional pagination data
   */
  const handleResponse = (
    data: Report[],
    err: string | undefined,
    pagination?: PaginationData,
    concatData: boolean = false,
  ) => {
    if (err) {
      message.value = err
    }
    if (concatData) {
      reports.value = reports.value.concat(data)
    } else {
      reports.value = data
    }
    if (pagination !== undefined) {
      paginationData.value = new PaginationData(pagination)
    }
  }

  const loadColumnConfig = async () => {
    try {
      const response = await columnConfigService.getVisibleFields('report', 'list')
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
      const response = await columnConfigService.updateVisibleFields('report', 'list', fields)
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
  /** Navigate to report detail view */
  const viewReport = (report: Report) => {
    router.push({ name: 'reportdetailsview', params: { id: report.getKeyValue() } })
  }

  /** Navigate to report list view */
  const goToListView = () => {
    router.push({ path: '/reports' })
  }

  /** Navigate to report create form */
  const goToCreateFormView = () => {
    router.push({ path: '/reports/create' })
  }

  /** Navigate to report update form */
  const goToUpdateFormView = (report: Report) => {
    router.push({ name: 'reportupdateview', params: { id: report.getKeyValue() } })
  }

  /**
   * Delete a report by id
   * @param report Report object to delete
   */
  const deleteReport = async (report: Report) => {
    const result = await reportService.remove(report.getKeyValue())
    if (result.error) {
      message.value = result.error
    }
    return result
  }

  /**
   * Create a new report
   * @param reportFormDTO Report object to create
   */
  const createReport = async (reportFormDTO: Partial<ReportFormDTO>) => {
    const dto = new ReportFormDTO(reportFormDTO)
    const report: Partial<Report> = await dto.toEntity()
    const { data, error, errors } = await reportService.create(report)
    if (error) {
      message.value = error
    }
    return { data, errors }
  }

  /**
   * Update an existing report
   * @param reportFormDTO Report object to update
   */
  const updateReport = async (id: number | string, reportFormDTO: Partial<ReportFormDTO>) => {
    const dto = new ReportFormDTO(reportFormDTO)
    const report: Partial<Report> = await dto.toEntity()
    const { data, error } = await reportService.update(id, report)
    if (error) {
      message.value = error
    }
    return data
  }

  // API Requests
  /** Load all reports from the API */
  const loadReports = async (
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
    } = await reportService.getAll(pagination, sortFields)
    handleResponse(data, err, paginationresult)

    if (unpagined && reports.value.length > 0) {
      while (paginationData.value.hasNext()) {
        paginationData.value.nextPage()
        const {
          data: nextData,
          error: nextErr,
          pagination: nextPagination,
        } = await reportService.getAll(paginationData.value.toParameter(), sortFields)
        handleResponse(nextData, nextErr, nextPagination, true)
      }
    }
    stopLoading()
  }

  /**
   * Get a single report by id
   * @param id Report ID
   */
  const getReportById = async (id: number | string) => {
    const result = await reportService.getById(id)
    if (result.error) {
      message.value = result.error
    }
    return result
  }

  /**
   * Search reports with filters, pagination, and sorting
   * @param filters Partial Report object containing search filters
   * @param pagination Pagination request parameters
   * @param sortFields Array of fields to sort
   */
  const searchReports = async (
    unpagined: boolean = false,
    filters: Partial<Report>,
    pagination: PaginationRequestParameter,
    sortFields: SortFieldParameter[],
  ) => {
    message.value = null
    startLoading()
    const {
      data,
      error: err,
      pagination: paginationresult,
    } = await reportService.search(filters, pagination, sortFields)
    handleResponse(data, err, paginationresult)

    if (unpagined) {
      while (paginationData.value.hasNext()) {
        paginationData.value.nextPage()
        const {
          data: nextData,
          error: nextErr,
          pagination: nextPagination,
        } = await reportService.search(filters, paginationData.value.toParameter(), sortFields)
        handleResponse(nextData, nextErr, nextPagination, true)
      }
    }
    stopLoading()
  }
  const exportReportsToCsv = async (data: Partial<Report[]>) => {
    console.log('Exporting Report to CSV', data)
    await reportService.exportCsv(data)
  }

  const getAllReportsByUserId = async (
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
    } = await reportService.getAllByUserId(userId, pagination, sortFields)
    handleResponse(data, err, paginationresult)

    if (unpagined) {
      while (paginationData.value.hasNext() && reports.value.length > 0) {
        paginationData.value.nextPage()
        const {
          data: nextData,
          error: nextErr,
          pagination: nextPagination,
        } = await reportService.getAllByUserId(
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
    reports,
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
    exportReportsToCsv,
    loadReports,
    searchReports,
    getReportById,
    viewReport,
    goToListView,
    goToCreateFormView,
    goToUpdateFormView,
    deleteReport,
    createReport,
    updateReport,
    getAllReportsByUserId,
  }
}
