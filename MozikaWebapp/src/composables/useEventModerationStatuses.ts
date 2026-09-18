import { ref, watch } from 'vue'
import {
  EventModerationStatuse,
  EventModerationStatuseFormDTO,
} from '@/models/EventModerationStatuseModel'
import * as eventModerationStatuseService from '@/services/EventModerationStatuseService'
import * as columnConfigService from '@/services/ColumnConfigService'
import { useLoading } from './useLoading'
import { PaginationRequestParameter, SortFieldParameter } from '@/models/api/RequestModel'
import { PaginationData } from '@/models/api/PageResponseModel'
import { useRouter } from 'vue-router'

export const DEFAULT_LIST_VIEW_FIELDS = ['id']

/**
 * Composable to manage EventModerationStatuse entities including CRUD operations,
 * search, pagination, foreign key loading, and navigation.
 */
export function useEventModerationStatuses() {
  /** List of all eventModerationStatuses */
  const eventModerationStatuses = ref<EventModerationStatuse[]>([])

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
  const savedLayoutMode = localStorage.getItem('layoutMode_eventModerationStatuse') as
    | 'list'
    | 'card'
    | null
  const layoutMode = ref<'list' | 'card'>(savedLayoutMode || 'list')

  watch(layoutMode, (newValue) => {
    localStorage.setItem('layoutMode_eventModerationStatuse', newValue)
  })

  /**
   * Handles API response by updating the eventModerationStatuses and pagination
   * @param data Array of EventModerationStatuse returned by the API
   * @param err Optional error message
   * @param pagination Optional pagination data
   */
  const handleResponse = (
    data: EventModerationStatuse[],
    err: string | undefined,
    pagination?: PaginationData,
    concatData: boolean = false,
  ) => {
    if (err) {
      message.value = err
    }
    if (concatData) {
      eventModerationStatuses.value = eventModerationStatuses.value.concat(data)
    } else {
      eventModerationStatuses.value = data
    }
    if (pagination !== undefined) {
      paginationData.value = new PaginationData(pagination)
    }
  }

  const loadColumnConfig = async () => {
    try {
      const response = await columnConfigService.getVisibleFields('eventModerationStatuse', 'list')
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
        'eventModerationStatuse',
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
  /** Navigate to eventModerationStatuse detail view */
  const viewEventModerationStatuse = (eventModerationStatuse: EventModerationStatuse) => {
    router.push({
      name: 'eventModerationStatusedetailsview',
      params: { id: eventModerationStatuse.getKeyValue() },
    })
  }

  /** Navigate to eventModerationStatuse list view */
  const goToListView = () => {
    router.push({ path: '/eventModerationStatuses' })
  }

  /** Navigate to eventModerationStatuse create form */
  const goToCreateFormView = () => {
    router.push({ path: '/eventModerationStatuses/create' })
  }

  /** Navigate to eventModerationStatuse update form */
  const goToUpdateFormView = (eventModerationStatuse: EventModerationStatuse) => {
    router.push({
      name: 'eventModerationStatuseupdateview',
      params: { id: eventModerationStatuse.getKeyValue() },
    })
  }

  /**
   * Delete a eventModerationStatuse by id
   * @param eventModerationStatuse EventModerationStatuse object to delete
   */
  const deleteEventModerationStatuse = async (eventModerationStatuse: EventModerationStatuse) => {
    const result = await eventModerationStatuseService.remove(eventModerationStatuse.getKeyValue())
    if (result.error) {
      message.value = result.error
    }
    return result
  }

  /**
   * Create a new eventModerationStatuse
   * @param eventModerationStatuseFormDTO EventModerationStatuse object to create
   */
  const createEventModerationStatuse = async (
    eventModerationStatuseFormDTO: Partial<EventModerationStatuseFormDTO>,
  ) => {
    const dto = new EventModerationStatuseFormDTO(eventModerationStatuseFormDTO)
    const eventModerationStatuse: Partial<EventModerationStatuse> = await dto.toEntity()
    const { data, error, errors } =
      await eventModerationStatuseService.create(eventModerationStatuse)
    if (error) {
      message.value = error
    }
    return { data, errors }
  }

  /**
   * Update an existing eventModerationStatuse
   * @param eventModerationStatuseFormDTO EventModerationStatuse object to update
   */
  const updateEventModerationStatuse = async (
    id: number | string,
    eventModerationStatuseFormDTO: Partial<EventModerationStatuseFormDTO>,
  ) => {
    const dto = new EventModerationStatuseFormDTO(eventModerationStatuseFormDTO)
    const eventModerationStatuse: Partial<EventModerationStatuse> = await dto.toEntity()
    const { data, error } = await eventModerationStatuseService.update(id, eventModerationStatuse)
    if (error) {
      message.value = error
    }
    return data
  }

  // API Requests
  /** Load all eventModerationStatuses from the API */
  const loadEventModerationStatuses = async (
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
    } = await eventModerationStatuseService.getAll(pagination, sortFields)
    handleResponse(data, err, paginationresult)

    if (unpagined && eventModerationStatuses.value.length > 0) {
      while (paginationData.value.hasNext()) {
        paginationData.value.nextPage()
        const {
          data: nextData,
          error: nextErr,
          pagination: nextPagination,
        } = await eventModerationStatuseService.getAll(
          paginationData.value.toParameter(),
          sortFields,
        )
        handleResponse(nextData, nextErr, nextPagination, true)
      }
    }
    stopLoading()
  }

  /**
   * Get a single eventModerationStatuse by id
   * @param id EventModerationStatuse ID
   */
  const getEventModerationStatuseById = async (id: number | string) => {
    const result = await eventModerationStatuseService.getById(id)
    if (result.error) {
      message.value = result.error
    }
    return result
  }

  /**
   * Search eventModerationStatuses with filters, pagination, and sorting
   * @param filters Partial EventModerationStatuse object containing search filters
   * @param pagination Pagination request parameters
   * @param sortFields Array of fields to sort
   */
  const searchEventModerationStatuses = async (
    unpagined: boolean = false,
    filters: Partial<EventModerationStatuse>,
    pagination: PaginationRequestParameter,
    sortFields: SortFieldParameter[],
  ) => {
    message.value = null
    startLoading()
    const {
      data,
      error: err,
      pagination: paginationresult,
    } = await eventModerationStatuseService.search(filters, pagination, sortFields)
    handleResponse(data, err, paginationresult)

    if (unpagined) {
      while (paginationData.value.hasNext()) {
        paginationData.value.nextPage()
        const {
          data: nextData,
          error: nextErr,
          pagination: nextPagination,
        } = await eventModerationStatuseService.search(
          filters,
          paginationData.value.toParameter(),
          sortFields,
        )
        handleResponse(nextData, nextErr, nextPagination, true)
      }
    }
    stopLoading()
  }
  const exportEventModerationStatusesToCsv = async (data: Partial<EventModerationStatuse[]>) => {
    console.log('Exporting EventModerationStatuse to CSV', data)
    await eventModerationStatuseService.exportCsv(data)
  }

  /** Getter for pagination data */
  const getPaginationData = () => paginationData.value

  return {
    // state
    eventModerationStatuses,
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
    exportEventModerationStatusesToCsv,
    loadEventModerationStatuses,
    searchEventModerationStatuses,
    getEventModerationStatuseById,
    viewEventModerationStatuse,
    goToListView,
    goToCreateFormView,
    goToUpdateFormView,
    deleteEventModerationStatuse,
    createEventModerationStatuse,
    updateEventModerationStatuse,
  }
}
