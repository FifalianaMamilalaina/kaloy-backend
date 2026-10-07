import { ref, watch } from 'vue'
import { Concert, ConcertFormDTO } from '@/models/ConcertModel'
import * as concertService from '@/services/ConcertService'
import * as columnConfigService from '@/services/ColumnConfigService'
import { useLoading } from './useLoading'
import { PaginationRequestParameter, SortFieldParameter } from '@/models/api/RequestModel'
import { PaginationData } from '@/models/api/PageResponseModel'
import { useRouter } from 'vue-router'

export const DEFAULT_LIST_VIEW_FIELDS = ['id']

/**
 * Composable to manage Concert entities including CRUD operations,
 * search, pagination, foreign key loading, and navigation.
 */
export function useConcerts() {
  /** List of all concerts */
  const concerts = ref<Concert[]>([])

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
  const savedLayoutMode = localStorage.getItem('layoutMode_concert') as 'list' | 'card' | null
  const layoutMode = ref<'list' | 'card'>(savedLayoutMode || 'list')

  watch(layoutMode, (newValue) => {
    localStorage.setItem('layoutMode_concert', newValue)
  })

  /**
   * Handles API response by updating the concerts and pagination
   * @param data Array of Concert returned by the API
   * @param err Optional error message
   * @param pagination Optional pagination data
   */
  const handleResponse = (
    data: Concert[],
    err: string | undefined,
    pagination?: PaginationData,
    concatData: boolean = false,
  ) => {
    if (err) {
      message.value = err
    }
    if (concatData) {
      concerts.value = concerts.value.concat(data)
    } else {
      concerts.value = data
    }
    if (pagination !== undefined) {
      paginationData.value = new PaginationData(pagination)
    }
  }

  const loadColumnConfig = async () => {
    try {
      const response = await columnConfigService.getVisibleFields('concert', 'list')
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
      const response = await columnConfigService.updateVisibleFields('concert', 'list', fields)
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
  /** Navigate to concert detail view */
  const viewConcert = (concert: Concert) => {
    router.push({ name: 'concertdetailsview', params: { id: concert.getKeyValue() } })
  }

  /** Navigate to concert list view */
  const goToListView = () => {
    router.push({ path: '/concerts' })
  }

  /** Navigate to concert create form */
  const goToCreateFormView = () => {
    router.push({ path: '/concerts/create' })
  }

  /** Navigate to concert update form */
  const goToUpdateFormView = (concert: Concert) => {
    router.push({ name: 'concertupdateview', params: { id: concert.getKeyValue() } })
  }

  /**
   * Delete a concert by id
   * @param concert Concert object to delete
   */
  const deleteConcert = async (concert: Concert) => {
    const result = await concertService.remove(concert.getKeyValue())
    if (result.error) {
      message.value = result.error
    }
    return result
  }

  /**
   * Create a new concert
   * @param concertFormDTO Concert object to create
   */
  const createConcert = async (concertFormDTO: Partial<ConcertFormDTO>) => {
    const dto = new ConcertFormDTO(concertFormDTO)
    const concert: Partial<Concert> = await dto.toEntity()
    const { data, error, errors } = await concertService.create(concert)
    if (error) {
      message.value = error
    }
    return { data, errors }
  }

  /**
   * Update an existing concert
   * @param concertFormDTO Concert object to update
   */
  const updateConcert = async (id: number | string, concertFormDTO: Partial<ConcertFormDTO>) => {
    const dto = new ConcertFormDTO(concertFormDTO)
    const concert: Partial<Concert> = await dto.toEntity()
    const { data, error } = await concertService.update(id, concert)
    if (error) {
      message.value = error
    }
    return data
  }

  // API Requests
  /** Load all concerts from the API */
  const loadConcerts = async (
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
    } = await concertService.getAll(pagination, sortFields)
    handleResponse(data, err, paginationresult)

    if (unpagined && concerts.value.length > 0) {
      while (paginationData.value.hasNext()) {
        paginationData.value.nextPage()
        const {
          data: nextData,
          error: nextErr,
          pagination: nextPagination,
        } = await concertService.getAll(paginationData.value.toParameter(), sortFields)
        handleResponse(nextData, nextErr, nextPagination, true)
      }
    }
    stopLoading()
  }

  /**
   * Get a single concert by id
   * @param id Concert ID
   */
  const getConcertById = async (id: number | string) => {
    const result = await concertService.getById(id)
    if (result.error) {
      message.value = result.error
    }
    return result
  }

  /**
   * Search concerts with filters, pagination, and sorting
   * @param filters Partial Concert object containing search filters
   * @param pagination Pagination request parameters
   * @param sortFields Array of fields to sort
   */
  const searchConcerts = async (
    unpagined: boolean = false,
    filters: Partial<Concert>,
    pagination: PaginationRequestParameter,
    sortFields: SortFieldParameter[],
  ) => {
    message.value = null
    startLoading()
    const {
      data,
      error: err,
      pagination: paginationresult,
    } = await concertService.search(filters, pagination, sortFields)
    handleResponse(data, err, paginationresult)

    if (unpagined) {
      while (paginationData.value.hasNext()) {
        paginationData.value.nextPage()
        const {
          data: nextData,
          error: nextErr,
          pagination: nextPagination,
        } = await concertService.search(filters, paginationData.value.toParameter(), sortFields)
        handleResponse(nextData, nextErr, nextPagination, true)
      }
    }
    stopLoading()
  }
  const exportConcertsToCsv = async (data: Partial<Concert[]>) => {
    console.log('Exporting Concert to CSV', data)
    await concertService.exportCsv(data)
  }

  const getAllConcertsByEventId = async (
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
    } = await concertService.getAllByEventId(eventId, pagination, sortFields)
    handleResponse(data, err, paginationresult)

    if (unpagined) {
      while (paginationData.value.hasNext() && concerts.value.length > 0) {
        paginationData.value.nextPage()
        const {
          data: nextData,
          error: nextErr,
          pagination: nextPagination,
        } = await concertService.getAllByEventId(
          eventId,
          paginationData.value.toParameter(),
          sortFields,
        )
        handleResponse(nextData, nextErr, nextPagination, true)
      }
    }
    stopLoading()
  }
  const getAllConcertsByArtistId = async (
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
    } = await concertService.getAllByArtistId(artistId, pagination, sortFields)
    handleResponse(data, err, paginationresult)

    if (unpagined) {
      while (paginationData.value.hasNext() && concerts.value.length > 0) {
        paginationData.value.nextPage()
        const {
          data: nextData,
          error: nextErr,
          pagination: nextPagination,
        } = await concertService.getAllByArtistId(
          artistId,
          paginationData.value.toParameter(),
          sortFields,
        )
        handleResponse(nextData, nextErr, nextPagination, true)
      }
    }
    stopLoading()
  }
  const getAllConcertsByVenueId = async (
    venueId: number | string | undefined,
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
    } = await concertService.getAllByVenueId(venueId, pagination, sortFields)
    handleResponse(data, err, paginationresult)

    if (unpagined) {
      while (paginationData.value.hasNext() && concerts.value.length > 0) {
        paginationData.value.nextPage()
        const {
          data: nextData,
          error: nextErr,
          pagination: nextPagination,
        } = await concertService.getAllByVenueId(
          venueId,
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
    concerts,
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
    exportConcertsToCsv,
    loadConcerts,
    searchConcerts,
    getConcertById,
    viewConcert,
    goToListView,
    goToCreateFormView,
    goToUpdateFormView,
    deleteConcert,
    createConcert,
    updateConcert,
    getAllConcertsByEventId,
    getAllConcertsByArtistId,
    getAllConcertsByVenueId,
  }
}
