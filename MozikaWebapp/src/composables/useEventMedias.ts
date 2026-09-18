import { ref, watch } from 'vue'
import { EventMedia, EventMediaFormDTO } from '@/models/EventMediaModel'
import * as eventMediaService from '@/services/EventMediaService'
import * as columnConfigService from '@/services/ColumnConfigService'
import { useLoading } from './useLoading'
import { PaginationRequestParameter, SortFieldParameter } from '@/models/api/RequestModel'
import { PaginationData } from '@/models/api/PageResponseModel'
import { useRouter } from 'vue-router'

export const DEFAULT_LIST_VIEW_FIELDS = ['id']

/**
 * Composable to manage EventMedia entities including CRUD operations,
 * search, pagination, foreign key loading, and navigation.
 */
export function useEventMedias() {
  /** List of all eventMedias */
  const eventMedias = ref<EventMedia[]>([])

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
  const savedLayoutMode = localStorage.getItem('layoutMode_eventMedia') as 'list' | 'card' | null
  const layoutMode = ref<'list' | 'card'>(savedLayoutMode || 'list')

  watch(layoutMode, (newValue) => {
    localStorage.setItem('layoutMode_eventMedia', newValue)
  })

  /**
   * Handles API response by updating the eventMedias and pagination
   * @param data Array of EventMedia returned by the API
   * @param err Optional error message
   * @param pagination Optional pagination data
   */
  const handleResponse = (
    data: EventMedia[],
    err: string | undefined,
    pagination?: PaginationData,
    concatData: boolean = false,
  ) => {
    if (err) {
      message.value = err
    }
    if (concatData) {
      eventMedias.value = eventMedias.value.concat(data)
    } else {
      eventMedias.value = data
    }
    if (pagination !== undefined) {
      paginationData.value = new PaginationData(pagination)
    }
  }

  const loadColumnConfig = async () => {
    try {
      const response = await columnConfigService.getVisibleFields('eventMedia', 'list')
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
      const response = await columnConfigService.updateVisibleFields('eventMedia', 'list', fields)
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
  /** Navigate to eventMedia detail view */
  const viewEventMedia = (eventMedia: EventMedia) => {
    router.push({ name: 'eventMediadetailsview', params: { id: eventMedia.getKeyValue() } })
  }

  /** Navigate to eventMedia list view */
  const goToListView = () => {
    router.push({ path: '/eventMedias' })
  }

  /** Navigate to eventMedia create form */
  const goToCreateFormView = () => {
    router.push({ path: '/eventMedias/create' })
  }

  /** Navigate to eventMedia update form */
  const goToUpdateFormView = (eventMedia: EventMedia) => {
    router.push({ name: 'eventMediaupdateview', params: { id: eventMedia.getKeyValue() } })
  }

  /**
   * Delete a eventMedia by id
   * @param eventMedia EventMedia object to delete
   */
  const deleteEventMedia = async (eventMedia: EventMedia) => {
    const result = await eventMediaService.remove(eventMedia.getKeyValue())
    if (result.error) {
      message.value = result.error
    }
    return result
  }

  /**
   * Create a new eventMedia
   * @param eventMediaFormDTO EventMedia object to create
   */
  const createEventMedia = async (eventMediaFormDTO: Partial<EventMediaFormDTO>) => {
    const dto = new EventMediaFormDTO(eventMediaFormDTO)
    const eventMedia: Partial<EventMedia> = await dto.toEntity()
    const { data, error, errors } = await eventMediaService.create(eventMedia)
    if (error) {
      message.value = error
    }
    return { data, errors }
  }

  /**
   * Update an existing eventMedia
   * @param eventMediaFormDTO EventMedia object to update
   */
  const updateEventMedia = async (
    id: number | string,
    eventMediaFormDTO: Partial<EventMediaFormDTO>,
  ) => {
    const dto = new EventMediaFormDTO(eventMediaFormDTO)
    const eventMedia: Partial<EventMedia> = await dto.toEntity()
    const { data, error } = await eventMediaService.update(id, eventMedia)
    if (error) {
      message.value = error
    }
    return data
  }

  // API Requests
  /** Load all eventMedias from the API */
  const loadEventMedias = async (
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
    } = await eventMediaService.getAll(pagination, sortFields)
    handleResponse(data, err, paginationresult)

    if (unpagined && eventMedias.value.length > 0) {
      while (paginationData.value.hasNext()) {
        paginationData.value.nextPage()
        const {
          data: nextData,
          error: nextErr,
          pagination: nextPagination,
        } = await eventMediaService.getAll(paginationData.value.toParameter(), sortFields)
        handleResponse(nextData, nextErr, nextPagination, true)
      }
    }
    stopLoading()
  }

  /**
   * Get a single eventMedia by id
   * @param id EventMedia ID
   */
  const getEventMediaById = async (id: number | string) => {
    const result = await eventMediaService.getById(id)
    if (result.error) {
      message.value = result.error
    }
    return result
  }

  /**
   * Search eventMedias with filters, pagination, and sorting
   * @param filters Partial EventMedia object containing search filters
   * @param pagination Pagination request parameters
   * @param sortFields Array of fields to sort
   */
  const searchEventMedias = async (
    unpagined: boolean = false,
    filters: Partial<EventMedia>,
    pagination: PaginationRequestParameter,
    sortFields: SortFieldParameter[],
  ) => {
    message.value = null
    startLoading()
    const {
      data,
      error: err,
      pagination: paginationresult,
    } = await eventMediaService.search(filters, pagination, sortFields)
    handleResponse(data, err, paginationresult)

    if (unpagined) {
      while (paginationData.value.hasNext()) {
        paginationData.value.nextPage()
        const {
          data: nextData,
          error: nextErr,
          pagination: nextPagination,
        } = await eventMediaService.search(filters, paginationData.value.toParameter(), sortFields)
        handleResponse(nextData, nextErr, nextPagination, true)
      }
    }
    stopLoading()
  }
  const exportEventMediasToCsv = async (data: Partial<EventMedia[]>) => {
    console.log('Exporting EventMedia to CSV', data)
    await eventMediaService.exportCsv(data)
  }

  const getAllEventMediasByEventId = async (
    eventId: number | string | undefined,
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
    } = await eventMediaService.getAllByEventId(eventId, pagination, sortFields)
    handleResponse(data, err, paginationresult)

    if (unpagined) {
      while (paginationData.value.hasNext() && eventMedias.value.length > 0) {
        paginationData.value.nextPage()
        const {
          data: nextData,
          error: nextErr,
          pagination: nextPagination,
        } = await eventMediaService.getAllByEventId(
          eventId,
          paginationData.value.toParameter(),
          sortFields,
        )
        handleResponse(nextData, nextErr, nextPagination, true)
      }
    }
    stopLoading()
  }
  const getAllEventMediasByUserId = async (
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
    } = await eventMediaService.getAllByUserId(userId, pagination, sortFields)
    handleResponse(data, err, paginationresult)

    if (unpagined) {
      while (paginationData.value.hasNext() && eventMedias.value.length > 0) {
        paginationData.value.nextPage()
        const {
          data: nextData,
          error: nextErr,
          pagination: nextPagination,
        } = await eventMediaService.getAllByUserId(
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
    eventMedias,
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
    exportEventMediasToCsv,
    loadEventMedias,
    searchEventMedias,
    getEventMediaById,
    viewEventMedia,
    goToListView,
    goToCreateFormView,
    goToUpdateFormView,
    deleteEventMedia,
    createEventMedia,
    updateEventMedia,
    getAllEventMediasByEventId,
    getAllEventMediasByUserId,
  }
}
