import { ref, watch } from 'vue'
import { Venue, VenueFormDTO } from '@/models/VenueModel'
import * as venueService from '@/services/VenueService'
import * as columnConfigService from '@/services/ColumnConfigService'
import { useLoading } from './useLoading'
import { PaginationRequestParameter, SortFieldParameter } from '@/models/api/RequestModel'
import { PaginationData } from '@/models/api/PageResponseModel'
import { useRouter } from 'vue-router'
import { Concert, ConcertFormDTO } from '@/models/ConcertModel'

export const DEFAULT_LIST_VIEW_FIELDS = ['id']

/**
 * Composable to manage Venue entities including CRUD operations,
 * search, pagination, foreign key loading, and navigation.
 */
export function useVenues() {
  /** List of all venues */
  const venues = ref<Venue[]>([])

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
  const savedLayoutMode = localStorage.getItem('layoutMode_venue') as 'list' | 'card' | null
  const layoutMode = ref<'list' | 'card'>(savedLayoutMode || 'list')

  watch(layoutMode, (newValue) => {
    localStorage.setItem('layoutMode_venue', newValue)
  })

  /**
   * Handles API response by updating the venues and pagination
   * @param data Array of Venue returned by the API
   * @param err Optional error message
   * @param pagination Optional pagination data
   */
  const handleResponse = (
    data: Venue[],
    err: string | undefined,
    pagination?: PaginationData,
    concatData: boolean = false,
  ) => {
    if (err) {
      message.value = err
    }
    if (concatData) {
      venues.value = venues.value.concat(data)
    } else {
      venues.value = data
    }
    if (pagination !== undefined) {
      paginationData.value = new PaginationData(pagination)
    }
  }

  const loadColumnConfig = async () => {
    try {
      const response = await columnConfigService.getVisibleFields('venue', 'list')
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
      const response = await columnConfigService.updateVisibleFields('venue', 'list', fields)
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
  /** Navigate to venue detail view */
  const viewVenue = (venue: Venue) => {
    router.push({ name: 'venuedetailsview', params: { id: venue.getKeyValue() } })
  }

  /** Navigate to venue list view */
  const goToListView = () => {
    router.push({ path: '/venues' })
  }

  /** Navigate to venue create form */
  const goToCreateFormView = () => {
    router.push({ path: '/venues/create' })
  }

  /** Navigate to venue update form */
  const goToUpdateFormView = (venue: Venue) => {
    router.push({ name: 'venueupdateview', params: { id: venue.getKeyValue() } })
  }

  /**
   * Delete a venue by id
   * @param venue Venue object to delete
   */
  const deleteVenue = async (venue: Venue) => {
    const result = await venueService.remove(venue.getKeyValue())
    if (result.error) {
      message.value = result.error
    }
    return result
  }

  /**
   * Create a new venue
   * @param fullData Object containing venue and his details
   */
  const createVenue = async (fullData: {
    venue: Partial<VenueFormDTO>
    concerts: ConcertFormDTO[]
  }) => {
    const dto = new VenueFormDTO(fullData.venue)
    const venue: Partial<Venue> = await dto.toEntity()
    const concerts: Partial<Concert>[] = await ConcertFormDTO.toEntities(fullData.concerts)

    const { data, error, errors } = await venueService.create({ venue, concerts })
    if (error) {
      message.value = error
    }
    return { data, errors }
  }

  /**
   * Update an existing venue with full details
   * @param id Venue ID to update
   * @param fullData Object containing venue and its details
   */
  const updateVenue = async (
    id: number | string,
    fullData: {
      venue: Partial<VenueFormDTO>
      concerts: ConcertFormDTO[]
    },
  ) => {
    const dto = new VenueFormDTO(fullData.venue)
    const venue: Partial<Venue> = await dto.toEntity()

    const concerts: Partial<Concert>[] = await ConcertFormDTO.toEntities(fullData.concerts)

    const { data, error, errors } = await venueService.update(id, {
      venue,
      concerts,
    })
    if (error) {
      message.value = error
    }
    return { data, errors }
  }

  // API Requests
  /** Load all venues from the API */
  const loadVenues = async (
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
    } = await venueService.getAll(pagination, sortFields)
    handleResponse(data, err, paginationresult)

    if (unpagined && venues.value.length > 0) {
      while (paginationData.value.hasNext()) {
        paginationData.value.nextPage()
        const {
          data: nextData,
          error: nextErr,
          pagination: nextPagination,
        } = await venueService.getAll(paginationData.value.toParameter(), sortFields)
        handleResponse(nextData, nextErr, nextPagination, true)
      }
    }
    stopLoading()
  }

  /**
   * Get a single venue by id
   * @param id Venue ID
   */
  const getVenueById = async (id: number | string) => {
    const result = await venueService.getById(id)
    if (result.error) {
      message.value = result.error
    }
    return result
  }

  /**
   * Search venues with filters, pagination, and sorting
   * @param filters Partial Venue object containing search filters
   * @param pagination Pagination request parameters
   * @param sortFields Array of fields to sort
   */
  const searchVenues = async (
    unpagined: boolean = false,
    filters: Partial<Venue>,
    pagination: PaginationRequestParameter,
    sortFields: SortFieldParameter[],
  ) => {
    message.value = null
    startLoading()
    const {
      data,
      error: err,
      pagination: paginationresult,
    } = await venueService.search(filters, pagination, sortFields)
    handleResponse(data, err, paginationresult)

    if (unpagined) {
      while (paginationData.value.hasNext()) {
        paginationData.value.nextPage()
        const {
          data: nextData,
          error: nextErr,
          pagination: nextPagination,
        } = await venueService.search(filters, paginationData.value.toParameter(), sortFields)
        handleResponse(nextData, nextErr, nextPagination, true)
      }
    }
    stopLoading()
  }
  const exportVenuesToCsv = async (data: Partial<Venue[]>) => {
    console.log('Exporting Venue to CSV', data)
    await venueService.exportCsv(data)
  }

  /** Getter for pagination data */
  const getPaginationData = () => paginationData.value

  return {
    // state
    venues,
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
    exportVenuesToCsv,
    loadVenues,
    searchVenues,
    getVenueById,
    viewVenue,
    goToListView,
    goToCreateFormView,
    goToUpdateFormView,
    deleteVenue,
    createVenue,
    updateVenue,
  }
}
