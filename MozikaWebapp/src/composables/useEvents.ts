import { ref, watch } from 'vue'
import { Event, EventFormDTO } from '@/models/EventModel'
import * as eventService from '@/services/EventService'
import * as columnConfigService from '@/services/ColumnConfigService'
import { useLoading } from './useLoading'
import { PaginationRequestParameter, SortFieldParameter } from '@/models/api/RequestModel'
import { PaginationData } from '@/models/api/PageResponseModel'
import { useRouter } from 'vue-router'
import { Concert, ConcertFormDTO } from '@/models/ConcertModel'
import { EventMedia, EventMediaFormDTO } from '@/models/EventMediaModel'

export const DEFAULT_LIST_VIEW_FIELDS = ['id']

/**
 * Composable to manage Event entities including CRUD operations,
 * search, pagination, foreign key loading, and navigation.
 */
export function useEvents() {
  /** List of all events */
  const events = ref<Event[]>([])

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
  const savedLayoutMode = localStorage.getItem('layoutMode_event') as 'list' | 'card' | null
  const layoutMode = ref<'list' | 'card'>(savedLayoutMode || 'list')

  watch(layoutMode, (newValue) => {
    localStorage.setItem('layoutMode_event', newValue)
  })

  /**
   * Handles API response by updating the events and pagination
   * @param data Array of Event returned by the API
   * @param err Optional error message
   * @param pagination Optional pagination data
   */
  const handleResponse = (
    data: Event[],
    err: string | undefined,
    pagination?: PaginationData,
    concatData: boolean = false,
  ) => {
    if (err) {
      message.value = err
    }
    if (concatData) {
      events.value = events.value.concat(data)
    } else {
      events.value = data
    }
    if (pagination !== undefined) {
      paginationData.value = new PaginationData(pagination)
    }
  }

  const loadColumnConfig = async () => {
    try {
      const response = await columnConfigService.getVisibleFields('event', 'list')
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
      const response = await columnConfigService.updateVisibleFields('event', 'list', fields)
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
  /** Navigate to event detail view */
  const viewEvent = (event: Event) => {
    router.push({ name: 'eventdetailsview', params: { id: event.getKeyValue() } })
  }

  /** Navigate to event list view */
  const goToListView = () => {
    router.push({ path: '/events' })
  }

  /** Navigate to event create form */
  const goToCreateFormView = () => {
    router.push({ path: '/events/create' })
  }

  /** Navigate to event update form */
  const goToUpdateFormView = (event: Event) => {
    router.push({ name: 'eventupdateview', params: { id: event.getKeyValue() } })
  }

  /**
   * Delete a event by id
   * @param event Event object to delete
   */
  const deleteEvent = async (event: Event) => {
    const result = await eventService.remove(event.getKeyValue())
    if (result.error) {
      message.value = result.error
    }
    return result
  }

  /**
   * Create a new event
   * @param fullData Object containing event and his details
   */
  const createEvent = async (fullData: {
    event: Partial<EventFormDTO>
    concerts: ConcertFormDTO[]
    eventMedias: EventMediaFormDTO[]
  }) => {
    const dto = new EventFormDTO(fullData.event)
    const event: Partial<Event> = await dto.toEntity()
    const concerts: Partial<Concert>[] = await ConcertFormDTO.toEntities(fullData.concerts)
    const eventMedias: Partial<EventMedia>[] = await EventMediaFormDTO.toEntities(
      fullData.eventMedias,
    )

    const { data, error, errors } = await eventService.create({ event, concerts, eventMedias })
    if (error) {
      message.value = error
    }
    return { data, errors }
  }

  /**
   * Update an existing event with full details
   * @param id Event ID to update
   * @param fullData Object containing event and its details
   */
  const updateEvent = async (
    id: number | string,
    fullData: {
      event: Partial<EventFormDTO>
      concerts: ConcertFormDTO[]
      eventMedias: EventMediaFormDTO[]
    },
  ) => {
    const dto = new EventFormDTO(fullData.event)
    const event: Partial<Event> = await dto.toEntity()

    const concerts: Partial<Concert>[] = await ConcertFormDTO.toEntities(fullData.concerts)
    const eventMedias: Partial<EventMedia>[] = await EventMediaFormDTO.toEntities(
      fullData.eventMedias,
    )

    const { data, error, errors } = await eventService.update(id, {
      event,
      concerts,
      eventMedias,
    })
    if (error) {
      message.value = error
    }
    return { data, errors }
  }

  // API Requests
  /** Load all events from the API */
  const loadEvents = async (
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
    } = await eventService.getAll(pagination, sortFields)
    handleResponse(data, err, paginationresult)

    if (unpagined && events.value.length > 0) {
      while (paginationData.value.hasNext()) {
        paginationData.value.nextPage()
        const {
          data: nextData,
          error: nextErr,
          pagination: nextPagination,
        } = await eventService.getAll(paginationData.value.toParameter(), sortFields)
        handleResponse(nextData, nextErr, nextPagination, true)
      }
    }
    stopLoading()
  }

  /**
   * Get a single event by id
   * @param id Event ID
   */
  const getEventById = async (id: number | string) => {
    const result = await eventService.getById(id)
    if (result.error) {
      message.value = result.error
    }
    return result
  }

  /**
   * Search events with filters, pagination, and sorting
   * @param filters Partial Event object containing search filters
   * @param pagination Pagination request parameters
   * @param sortFields Array of fields to sort
   */
  const searchEvents = async (
    unpagined: boolean = false,
    filters: Partial<Event>,
    pagination: PaginationRequestParameter,
    sortFields: SortFieldParameter[],
  ) => {
    message.value = null
    startLoading()
    const {
      data,
      error: err,
      pagination: paginationresult,
    } = await eventService.search(filters, pagination, sortFields)
    handleResponse(data, err, paginationresult)

    if (unpagined) {
      while (paginationData.value.hasNext()) {
        paginationData.value.nextPage()
        const {
          data: nextData,
          error: nextErr,
          pagination: nextPagination,
        } = await eventService.search(filters, paginationData.value.toParameter(), sortFields)
        handleResponse(nextData, nextErr, nextPagination, true)
      }
    }
    stopLoading()
  }
  const exportEventsToCsv = async (data: Partial<Event[]>) => {
    console.log('Exporting Event to CSV', data)
    await eventService.exportCsv(data)
  }

  const getAllEventsByArtistId = async (
    artistId: number | string | undefined,
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
    } = await eventService.getAllByArtistId(artistId, pagination, sortFields)
    handleResponse(data, err, paginationresult)

    if (unpagined) {
      while (paginationData.value.hasNext() && events.value.length > 0) {
        paginationData.value.nextPage()
        const {
          data: nextData,
          error: nextErr,
          pagination: nextPagination,
        } = await eventService.getAllByArtistId(
          artistId,
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
    events,
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
    exportEventsToCsv,
    loadEvents,
    searchEvents,
    getEventById,
    viewEvent,
    goToListView,
    goToCreateFormView,
    goToUpdateFormView,
    deleteEvent,
    createEvent,
    updateEvent,
    getAllEventsByArtistId,
  }
}
