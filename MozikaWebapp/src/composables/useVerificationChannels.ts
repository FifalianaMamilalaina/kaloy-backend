import { ref, watch } from 'vue'
import { VerificationChannel, VerificationChannelFormDTO } from '@/models/VerificationChannelModel'
import * as verificationChannelService from '@/services/VerificationChannelService'
import * as columnConfigService from '@/services/ColumnConfigService'
import { useLoading } from './useLoading'
import { PaginationRequestParameter, SortFieldParameter } from '@/models/api/RequestModel'
import { PaginationData } from '@/models/api/PageResponseModel'
import { useRouter } from 'vue-router'

export const DEFAULT_LIST_VIEW_FIELDS = ['id']

/**
 * Composable to manage VerificationChannel entities including CRUD operations,
 * search, pagination, foreign key loading, and navigation.
 */
export function useVerificationChannels() {
  /** List of all verificationChannels */
  const verificationChannels = ref<VerificationChannel[]>([])

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
  const savedLayoutMode = localStorage.getItem('layoutMode_verificationChannel') as
    | 'list'
    | 'card'
    | null
  const layoutMode = ref<'list' | 'card'>(savedLayoutMode || 'list')

  watch(layoutMode, (newValue) => {
    localStorage.setItem('layoutMode_verificationChannel', newValue)
  })

  /**
   * Handles API response by updating the verificationChannels and pagination
   * @param data Array of VerificationChannel returned by the API
   * @param err Optional error message
   * @param pagination Optional pagination data
   */
  const handleResponse = (
    data: VerificationChannel[],
    err: string | undefined,
    pagination?: PaginationData,
    concatData: boolean = false,
  ) => {
    if (err) {
      message.value = err
    }
    if (concatData) {
      verificationChannels.value = verificationChannels.value.concat(data)
    } else {
      verificationChannels.value = data
    }
    if (pagination !== undefined) {
      paginationData.value = new PaginationData(pagination)
    }
  }

  const loadColumnConfig = async () => {
    try {
      const response = await columnConfigService.getVisibleFields('verificationChannel', 'list')
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
        'verificationChannel',
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
  /** Navigate to verificationChannel detail view */
  const viewVerificationChannel = (verificationChannel: VerificationChannel) => {
    router.push({
      name: 'verificationChanneldetailsview',
      params: { id: verificationChannel.getKeyValue() },
    })
  }

  /** Navigate to verificationChannel list view */
  const goToListView = () => {
    router.push({ path: '/verificationChannels' })
  }

  /** Navigate to verificationChannel create form */
  const goToCreateFormView = () => {
    router.push({ path: '/verificationChannels/create' })
  }

  /** Navigate to verificationChannel update form */
  const goToUpdateFormView = (verificationChannel: VerificationChannel) => {
    router.push({
      name: 'verificationChannelupdateview',
      params: { id: verificationChannel.getKeyValue() },
    })
  }

  /**
   * Delete a verificationChannel by id
   * @param verificationChannel VerificationChannel object to delete
   */
  const deleteVerificationChannel = async (verificationChannel: VerificationChannel) => {
    const result = await verificationChannelService.remove(verificationChannel.getKeyValue())
    if (result.error) {
      message.value = result.error
    }
    return result
  }

  /**
   * Create a new verificationChannel
   * @param verificationChannelFormDTO VerificationChannel object to create
   */
  const createVerificationChannel = async (
    verificationChannelFormDTO: Partial<VerificationChannelFormDTO>,
  ) => {
    const dto = new VerificationChannelFormDTO(verificationChannelFormDTO)
    const verificationChannel: Partial<VerificationChannel> = await dto.toEntity()
    const { data, error, errors } = await verificationChannelService.create(verificationChannel)
    if (error) {
      message.value = error
    }
    return { data, errors }
  }

  /**
   * Update an existing verificationChannel
   * @param verificationChannelFormDTO VerificationChannel object to update
   */
  const updateVerificationChannel = async (
    id: number | string,
    verificationChannelFormDTO: Partial<VerificationChannelFormDTO>,
  ) => {
    const dto = new VerificationChannelFormDTO(verificationChannelFormDTO)
    const verificationChannel: Partial<VerificationChannel> = await dto.toEntity()
    const { data, error } = await verificationChannelService.update(id, verificationChannel)
    if (error) {
      message.value = error
    }
    return data
  }

  // API Requests
  /** Load all verificationChannels from the API */
  const loadVerificationChannels = async (
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
    } = await verificationChannelService.getAll(pagination, sortFields)
    handleResponse(data, err, paginationresult)

    if (unpagined && verificationChannels.value.length > 0) {
      while (paginationData.value.hasNext()) {
        paginationData.value.nextPage()
        const {
          data: nextData,
          error: nextErr,
          pagination: nextPagination,
        } = await verificationChannelService.getAll(paginationData.value.toParameter(), sortFields)
        handleResponse(nextData, nextErr, nextPagination, true)
      }
    }
    stopLoading()
  }

  /**
   * Get a single verificationChannel by id
   * @param id VerificationChannel ID
   */
  const getVerificationChannelById = async (id: number | string) => {
    const result = await verificationChannelService.getById(id)
    if (result.error) {
      message.value = result.error
    }
    return result
  }

  /**
   * Search verificationChannels with filters, pagination, and sorting
   * @param filters Partial VerificationChannel object containing search filters
   * @param pagination Pagination request parameters
   * @param sortFields Array of fields to sort
   */
  const searchVerificationChannels = async (
    unpagined: boolean = false,
    filters: Partial<VerificationChannel>,
    pagination: PaginationRequestParameter,
    sortFields: SortFieldParameter[],
  ) => {
    message.value = null
    startLoading()
    const {
      data,
      error: err,
      pagination: paginationresult,
    } = await verificationChannelService.search(filters, pagination, sortFields)
    handleResponse(data, err, paginationresult)

    if (unpagined) {
      while (paginationData.value.hasNext()) {
        paginationData.value.nextPage()
        const {
          data: nextData,
          error: nextErr,
          pagination: nextPagination,
        } = await verificationChannelService.search(
          filters,
          paginationData.value.toParameter(),
          sortFields,
        )
        handleResponse(nextData, nextErr, nextPagination, true)
      }
    }
    stopLoading()
  }
  const exportVerificationChannelsToCsv = async (data: Partial<VerificationChannel[]>) => {
    console.log('Exporting VerificationChannel to CSV', data)
    await verificationChannelService.exportCsv(data)
  }

  /** Getter for pagination data */
  const getPaginationData = () => paginationData.value

  return {
    // state
    verificationChannels,
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
    exportVerificationChannelsToCsv,
    loadVerificationChannels,
    searchVerificationChannels,
    getVerificationChannelById,
    viewVerificationChannel,
    goToListView,
    goToCreateFormView,
    goToUpdateFormView,
    deleteVerificationChannel,
    createVerificationChannel,
    updateVerificationChannel,
  }
}
