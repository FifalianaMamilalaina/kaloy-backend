import { ref, watch } from 'vue'
import { VerificationCode, VerificationCodeFormDTO } from '@/models/VerificationCodeModel'
import * as verificationCodeService from '@/services/VerificationCodeService'
import * as columnConfigService from '@/services/ColumnConfigService'
import { useLoading } from './useLoading'
import { PaginationRequestParameter, SortFieldParameter } from '@/models/api/RequestModel'
import { PaginationData } from '@/models/api/PageResponseModel'
import { useRouter } from 'vue-router'

export const DEFAULT_LIST_VIEW_FIELDS = ['id']

/**
 * Composable to manage VerificationCode entities including CRUD operations,
 * search, pagination, foreign key loading, and navigation.
 */
export function useVerificationCodes() {
  /** List of all verificationCodes */
  const verificationCodes = ref<VerificationCode[]>([])

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
  const savedLayoutMode = localStorage.getItem('layoutMode_verificationCode') as
    | 'list'
    | 'card'
    | null
  const layoutMode = ref<'list' | 'card'>(savedLayoutMode || 'list')

  watch(layoutMode, (newValue) => {
    localStorage.setItem('layoutMode_verificationCode', newValue)
  })

  /**
   * Handles API response by updating the verificationCodes and pagination
   * @param data Array of VerificationCode returned by the API
   * @param err Optional error message
   * @param pagination Optional pagination data
   */
  const handleResponse = (
    data: VerificationCode[],
    err: string | undefined,
    pagination?: PaginationData,
    concatData: boolean = false,
  ) => {
    if (err) {
      message.value = err
    }
    if (concatData) {
      verificationCodes.value = verificationCodes.value.concat(data)
    } else {
      verificationCodes.value = data
    }
    if (pagination !== undefined) {
      paginationData.value = new PaginationData(pagination)
    }
  }

  const loadColumnConfig = async () => {
    try {
      const response = await columnConfigService.getVisibleFields('verificationCode', 'list')
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
        'verificationCode',
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
  /** Navigate to verificationCode detail view */
  const viewVerificationCode = (verificationCode: VerificationCode) => {
    router.push({
      name: 'verificationCodedetailsview',
      params: { id: verificationCode.getKeyValue() },
    })
  }

  /** Navigate to verificationCode list view */
  const goToListView = () => {
    router.push({ path: '/verificationCodes' })
  }

  /** Navigate to verificationCode create form */
  const goToCreateFormView = () => {
    router.push({ path: '/verificationCodes/create' })
  }

  /** Navigate to verificationCode update form */
  const goToUpdateFormView = (verificationCode: VerificationCode) => {
    router.push({
      name: 'verificationCodeupdateview',
      params: { id: verificationCode.getKeyValue() },
    })
  }

  /**
   * Delete a verificationCode by id
   * @param verificationCode VerificationCode object to delete
   */
  const deleteVerificationCode = async (verificationCode: VerificationCode) => {
    const result = await verificationCodeService.remove(verificationCode.getKeyValue())
    if (result.error) {
      message.value = result.error
    }
    return result
  }

  /**
   * Create a new verificationCode
   * @param verificationCodeFormDTO VerificationCode object to create
   */
  const createVerificationCode = async (
    verificationCodeFormDTO: Partial<VerificationCodeFormDTO>,
  ) => {
    const dto = new VerificationCodeFormDTO(verificationCodeFormDTO)
    const verificationCode: Partial<VerificationCode> = await dto.toEntity()
    const { data, error, errors } = await verificationCodeService.create(verificationCode)
    if (error) {
      message.value = error
    }
    return { data, errors }
  }

  /**
   * Update an existing verificationCode
   * @param verificationCodeFormDTO VerificationCode object to update
   */
  const updateVerificationCode = async (
    id: number | string,
    verificationCodeFormDTO: Partial<VerificationCodeFormDTO>,
  ) => {
    const dto = new VerificationCodeFormDTO(verificationCodeFormDTO)
    const verificationCode: Partial<VerificationCode> = await dto.toEntity()
    const { data, error } = await verificationCodeService.update(id, verificationCode)
    if (error) {
      message.value = error
    }
    return data
  }

  // API Requests
  /** Load all verificationCodes from the API */
  const loadVerificationCodes = async (
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
    } = await verificationCodeService.getAll(pagination, sortFields)
    handleResponse(data, err, paginationresult)

    if (unpagined && verificationCodes.value.length > 0) {
      while (paginationData.value.hasNext()) {
        paginationData.value.nextPage()
        const {
          data: nextData,
          error: nextErr,
          pagination: nextPagination,
        } = await verificationCodeService.getAll(paginationData.value.toParameter(), sortFields)
        handleResponse(nextData, nextErr, nextPagination, true)
      }
    }
    stopLoading()
  }

  /**
   * Get a single verificationCode by id
   * @param id VerificationCode ID
   */
  const getVerificationCodeById = async (id: number | string) => {
    const result = await verificationCodeService.getById(id)
    if (result.error) {
      message.value = result.error
    }
    return result
  }

  /**
   * Search verificationCodes with filters, pagination, and sorting
   * @param filters Partial VerificationCode object containing search filters
   * @param pagination Pagination request parameters
   * @param sortFields Array of fields to sort
   */
  const searchVerificationCodes = async (
    unpagined: boolean = false,
    filters: Partial<VerificationCode>,
    pagination: PaginationRequestParameter,
    sortFields: SortFieldParameter[],
  ) => {
    message.value = null
    startLoading()
    const {
      data,
      error: err,
      pagination: paginationresult,
    } = await verificationCodeService.search(filters, pagination, sortFields)
    handleResponse(data, err, paginationresult)

    if (unpagined) {
      while (paginationData.value.hasNext()) {
        paginationData.value.nextPage()
        const {
          data: nextData,
          error: nextErr,
          pagination: nextPagination,
        } = await verificationCodeService.search(
          filters,
          paginationData.value.toParameter(),
          sortFields,
        )
        handleResponse(nextData, nextErr, nextPagination, true)
      }
    }
    stopLoading()
  }
  const exportVerificationCodesToCsv = async (data: Partial<VerificationCode[]>) => {
    console.log('Exporting VerificationCode to CSV', data)
    await verificationCodeService.exportCsv(data)
  }

  const getAllVerificationCodesByUserId = async (
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
    } = await verificationCodeService.getAllByUserId(userId, pagination, sortFields)
    handleResponse(data, err, paginationresult)

    if (unpagined) {
      while (paginationData.value.hasNext() && verificationCodes.value.length > 0) {
        paginationData.value.nextPage()
        const {
          data: nextData,
          error: nextErr,
          pagination: nextPagination,
        } = await verificationCodeService.getAllByUserId(
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
    verificationCodes,
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
    exportVerificationCodesToCsv,
    loadVerificationCodes,
    searchVerificationCodes,
    getVerificationCodeById,
    viewVerificationCode,
    goToListView,
    goToCreateFormView,
    goToUpdateFormView,
    deleteVerificationCode,
    createVerificationCode,
    updateVerificationCode,
    getAllVerificationCodesByUserId,
  }
}
