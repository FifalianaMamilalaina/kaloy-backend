import { ref, watch } from 'vue'
import {
  ParticipationStatuse,
  ParticipationStatuseFormDTO,
} from '@/models/ParticipationStatuseModel'
import * as participationStatuseService from '@/services/ParticipationStatuseService'
import * as columnConfigService from '@/services/ColumnConfigService'
import { useLoading } from './useLoading'
import { PaginationRequestParameter, SortFieldParameter } from '@/models/api/RequestModel'
import { PaginationData } from '@/models/api/PageResponseModel'
import { useRouter } from 'vue-router'

export const DEFAULT_LIST_VIEW_FIELDS = ['id']

/**
 * Composable to manage ParticipationStatuse entities including CRUD operations,
 * search, pagination, foreign key loading, and navigation.
 */
export function useParticipationStatuses() {
  /** List of all participationStatuses */
  const participationStatuses = ref<ParticipationStatuse[]>([])

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
  const savedLayoutMode = localStorage.getItem('layoutMode_participationStatuse') as
    | 'list'
    | 'card'
    | null
  const layoutMode = ref<'list' | 'card'>(savedLayoutMode || 'list')

  watch(layoutMode, (newValue) => {
    localStorage.setItem('layoutMode_participationStatuse', newValue)
  })

  /**
   * Handles API response by updating the participationStatuses and pagination
   * @param data Array of ParticipationStatuse returned by the API
   * @param err Optional error message
   * @param pagination Optional pagination data
   */
  const handleResponse = (
    data: ParticipationStatuse[],
    err: string | undefined,
    pagination?: PaginationData,
    concatData: boolean = false,
  ) => {
    if (err) {
      message.value = err
    }
    if (concatData) {
      participationStatuses.value = participationStatuses.value.concat(data)
    } else {
      participationStatuses.value = data
    }
    if (pagination !== undefined) {
      paginationData.value = new PaginationData(pagination)
    }
  }

  const loadColumnConfig = async () => {
    try {
      const response = await columnConfigService.getVisibleFields('participationStatuse', 'list')
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
        'participationStatuse',
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
  /** Navigate to participationStatuse detail view */
  const viewParticipationStatuse = (participationStatuse: ParticipationStatuse) => {
    router.push({
      name: 'participationStatusedetailsview',
      params: { id: participationStatuse.getKeyValue() },
    })
  }

  /** Navigate to participationStatuse list view */
  const goToListView = () => {
    router.push({ path: '/participationStatuses' })
  }

  /** Navigate to participationStatuse create form */
  const goToCreateFormView = () => {
    router.push({ path: '/participationStatuses/create' })
  }

  /** Navigate to participationStatuse update form */
  const goToUpdateFormView = (participationStatuse: ParticipationStatuse) => {
    router.push({
      name: 'participationStatuseupdateview',
      params: { id: participationStatuse.getKeyValue() },
    })
  }

  /**
   * Delete a participationStatuse by id
   * @param participationStatuse ParticipationStatuse object to delete
   */
  const deleteParticipationStatuse = async (participationStatuse: ParticipationStatuse) => {
    const result = await participationStatuseService.remove(participationStatuse.getKeyValue())
    if (result.error) {
      message.value = result.error
    }
    return result
  }

  /**
   * Create a new participationStatuse
   * @param participationStatuseFormDTO ParticipationStatuse object to create
   */
  const createParticipationStatuse = async (
    participationStatuseFormDTO: Partial<ParticipationStatuseFormDTO>,
  ) => {
    const dto = new ParticipationStatuseFormDTO(participationStatuseFormDTO)
    const participationStatuse: Partial<ParticipationStatuse> = await dto.toEntity()
    const { data, error, errors } = await participationStatuseService.create(participationStatuse)
    if (error) {
      message.value = error
    }
    return { data, errors }
  }

  /**
   * Update an existing participationStatuse
   * @param participationStatuseFormDTO ParticipationStatuse object to update
   */
  const updateParticipationStatuse = async (
    id: number | string,
    participationStatuseFormDTO: Partial<ParticipationStatuseFormDTO>,
  ) => {
    const dto = new ParticipationStatuseFormDTO(participationStatuseFormDTO)
    const participationStatuse: Partial<ParticipationStatuse> = await dto.toEntity()
    const { data, error } = await participationStatuseService.update(id, participationStatuse)
    if (error) {
      message.value = error
    }
    return data
  }

  // API Requests
  /** Load all participationStatuses from the API */
  const loadParticipationStatuses = async (
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
    } = await participationStatuseService.getAll(pagination, sortFields)
    handleResponse(data, err, paginationresult)

    if (unpagined && participationStatuses.value.length > 0) {
      while (paginationData.value.hasNext()) {
        paginationData.value.nextPage()
        const {
          data: nextData,
          error: nextErr,
          pagination: nextPagination,
        } = await participationStatuseService.getAll(paginationData.value.toParameter(), sortFields)
        handleResponse(nextData, nextErr, nextPagination, true)
      }
    }
    stopLoading()
  }

  /**
   * Get a single participationStatuse by id
   * @param id ParticipationStatuse ID
   */
  const getParticipationStatuseById = async (id: number | string) => {
    const result = await participationStatuseService.getById(id)
    if (result.error) {
      message.value = result.error
    }
    return result
  }

  /**
   * Search participationStatuses with filters, pagination, and sorting
   * @param filters Partial ParticipationStatuse object containing search filters
   * @param pagination Pagination request parameters
   * @param sortFields Array of fields to sort
   */
  const searchParticipationStatuses = async (
    unpagined: boolean = false,
    filters: Partial<ParticipationStatuse>,
    pagination: PaginationRequestParameter,
    sortFields: SortFieldParameter[],
  ) => {
    message.value = null
    startLoading()
    const {
      data,
      error: err,
      pagination: paginationresult,
    } = await participationStatuseService.search(filters, pagination, sortFields)
    handleResponse(data, err, paginationresult)

    if (unpagined) {
      while (paginationData.value.hasNext()) {
        paginationData.value.nextPage()
        const {
          data: nextData,
          error: nextErr,
          pagination: nextPagination,
        } = await participationStatuseService.search(
          filters,
          paginationData.value.toParameter(),
          sortFields,
        )
        handleResponse(nextData, nextErr, nextPagination, true)
      }
    }
    stopLoading()
  }
  const exportParticipationStatusesToCsv = async (data: Partial<ParticipationStatuse[]>) => {
    console.log('Exporting ParticipationStatuse to CSV', data)
    await participationStatuseService.exportCsv(data)
  }

  /** Getter for pagination data */
  const getPaginationData = () => paginationData.value

  return {
    // state
    participationStatuses,
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
    exportParticipationStatusesToCsv,
    loadParticipationStatuses,
    searchParticipationStatuses,
    getParticipationStatuseById,
    viewParticipationStatuse,
    goToListView,
    goToCreateFormView,
    goToUpdateFormView,
    deleteParticipationStatuse,
    createParticipationStatuse,
    updateParticipationStatuse,
  }
}
