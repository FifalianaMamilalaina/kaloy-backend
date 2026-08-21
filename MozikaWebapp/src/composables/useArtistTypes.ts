import { ref, watch } from 'vue'
import { ArtistType, ArtistTypeFormDTO } from '@/models/ArtistTypeModel'
import * as artistTypeService from '@/services/ArtistTypeService'
import * as columnConfigService from '@/services/ColumnConfigService'
import { useLoading } from './useLoading'
import { PaginationRequestParameter, SortFieldParameter } from '@/models/api/RequestModel'
import { PaginationData } from '@/models/api/PageResponseModel'
import { useRouter } from 'vue-router'

export const DEFAULT_LIST_VIEW_FIELDS = ['id']

/**
 * Composable to manage ArtistType entities including CRUD operations,
 * search, pagination, foreign key loading, and navigation.
 */
export function useArtistTypes() {
  /** List of all artistTypes */
  const artistTypes = ref<ArtistType[]>([])

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
  const savedLayoutMode = localStorage.getItem('layoutMode_artistType') as 'list' | 'card' | null
  const layoutMode = ref<'list' | 'card'>(savedLayoutMode || 'list')

  watch(layoutMode, (newValue) => {
    localStorage.setItem('layoutMode_artistType', newValue)
  })

  /**
   * Handles API response by updating the artistTypes and pagination
   * @param data Array of ArtistType returned by the API
   * @param err Optional error message
   * @param pagination Optional pagination data
   */
  const handleResponse = (
    data: ArtistType[],
    err: string | undefined,
    pagination?: PaginationData,
    concatData: boolean = false,
  ) => {
    if (err) {
      message.value = err
    }
    if (concatData) {
      artistTypes.value = artistTypes.value.concat(data)
    } else {
      artistTypes.value = data
    }
    if (pagination !== undefined) {
      paginationData.value = new PaginationData(pagination)
    }
  }

  const loadColumnConfig = async () => {
    try {
      const response = await columnConfigService.getVisibleFields('artistType', 'list')
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
      const response = await columnConfigService.updateVisibleFields('artistType', 'list', fields)
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
  /** Navigate to artistType detail view */
  const viewArtistType = (artistType: ArtistType) => {
    router.push({ name: 'artistTypedetailsview', params: { id: artistType.getKeyValue() } })
  }

  /** Navigate to artistType list view */
  const goToListView = () => {
    router.push({ path: '/artistTypes' })
  }

  /** Navigate to artistType create form */
  const goToCreateFormView = () => {
    router.push({ path: '/artistTypes/create' })
  }

  /** Navigate to artistType update form */
  const goToUpdateFormView = (artistType: ArtistType) => {
    router.push({ name: 'artistTypeupdateview', params: { id: artistType.getKeyValue() } })
  }

  /**
   * Delete a artistType by id
   * @param artistType ArtistType object to delete
   */
  const deleteArtistType = async (artistType: ArtistType) => {
    const result = await artistTypeService.remove(artistType.getKeyValue())
    if (result.error) {
      message.value = result.error
    }
    return result
  }

  /**
   * Create a new artistType
   * @param artistTypeFormDTO ArtistType object to create
   */
  const createArtistType = async (artistTypeFormDTO: Partial<ArtistTypeFormDTO>) => {
    const dto = new ArtistTypeFormDTO(artistTypeFormDTO)
    const artistType: Partial<ArtistType> = await dto.toEntity()
    const { data, error, errors } = await artistTypeService.create(artistType)
    if (error) {
      message.value = error
    }
    return { data, errors }
  }

  /**
   * Update an existing artistType
   * @param artistTypeFormDTO ArtistType object to update
   */
  const updateArtistType = async (
    id: number | string,
    artistTypeFormDTO: Partial<ArtistTypeFormDTO>,
  ) => {
    const dto = new ArtistTypeFormDTO(artistTypeFormDTO)
    const artistType: Partial<ArtistType> = await dto.toEntity()
    const { data, error } = await artistTypeService.update(id, artistType)
    if (error) {
      message.value = error
    }
    return data
  }

  // API Requests
  /** Load all artistTypes from the API */
  const loadArtistTypes = async (
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
    } = await artistTypeService.getAll(pagination, sortFields)
    handleResponse(data, err, paginationresult)

    if (unpagined && artistTypes.value.length > 0) {
      while (paginationData.value.hasNext()) {
        paginationData.value.nextPage()
        const {
          data: nextData,
          error: nextErr,
          pagination: nextPagination,
        } = await artistTypeService.getAll(paginationData.value.toParameter(), sortFields)
        handleResponse(nextData, nextErr, nextPagination, true)
      }
    }
    stopLoading()
  }

  /**
   * Get a single artistType by id
   * @param id ArtistType ID
   */
  const getArtistTypeById = async (id: number | string) => {
    const result = await artistTypeService.getById(id)
    if (result.error) {
      message.value = result.error
    }
    return result
  }

  /**
   * Search artistTypes with filters, pagination, and sorting
   * @param filters Partial ArtistType object containing search filters
   * @param pagination Pagination request parameters
   * @param sortFields Array of fields to sort
   */
  const searchArtistTypes = async (
    unpagined: boolean = false,
    filters: Partial<ArtistType>,
    pagination: PaginationRequestParameter,
    sortFields: SortFieldParameter[],
  ) => {
    message.value = null
    startLoading()
    const {
      data,
      error: err,
      pagination: paginationresult,
    } = await artistTypeService.search(filters, pagination, sortFields)
    handleResponse(data, err, paginationresult)

    if (unpagined) {
      while (paginationData.value.hasNext()) {
        paginationData.value.nextPage()
        const {
          data: nextData,
          error: nextErr,
          pagination: nextPagination,
        } = await artistTypeService.search(filters, paginationData.value.toParameter(), sortFields)
        handleResponse(nextData, nextErr, nextPagination, true)
      }
    }
    stopLoading()
  }
  const exportArtistTypesToCsv = async (data: Partial<ArtistType[]>) => {
    console.log('Exporting ArtistType to CSV', data)
    await artistTypeService.exportCsv(data)
  }

  /** Getter for pagination data */
  const getPaginationData = () => paginationData.value

  return {
    // state
    artistTypes,
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
    exportArtistTypesToCsv,
    loadArtistTypes,
    searchArtistTypes,
    getArtistTypeById,
    viewArtistType,
    goToListView,
    goToCreateFormView,
    goToUpdateFormView,
    deleteArtistType,
    createArtistType,
    updateArtistType,
  }
}
