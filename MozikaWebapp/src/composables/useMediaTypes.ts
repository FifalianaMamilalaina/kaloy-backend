import { ref, watch } from 'vue'
import { MediaType, MediaTypeFormDTO } from '@/models/MediaTypeModel'
import * as mediaTypeService from '@/services/MediaTypeService'
import * as columnConfigService from '@/services/ColumnConfigService'
import { useLoading } from './useLoading'
import { PaginationRequestParameter, SortFieldParameter } from '@/models/api/RequestModel'
import { PaginationData } from '@/models/api/PageResponseModel'
import { useRouter } from 'vue-router'

export const DEFAULT_LIST_VIEW_FIELDS = ['id']

/**
 * Composable to manage MediaType entities including CRUD operations,
 * search, pagination, foreign key loading, and navigation.
 */
export function useMediaTypes() {
  /** List of all mediaTypes */
  const mediaTypes = ref<MediaType[]>([])

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
  const savedLayoutMode = localStorage.getItem('layoutMode_mediaType') as 'list' | 'card' | null
  const layoutMode = ref<'list' | 'card'>(savedLayoutMode || 'list')

  watch(layoutMode, (newValue) => {
    localStorage.setItem('layoutMode_mediaType', newValue)
  })

  /**
   * Handles API response by updating the mediaTypes and pagination
   * @param data Array of MediaType returned by the API
   * @param err Optional error message
   * @param pagination Optional pagination data
   */
  const handleResponse = (
    data: MediaType[],
    err: string | undefined,
    pagination?: PaginationData,
    concatData: boolean = false,
  ) => {
    if (err) {
      message.value = err
    }
    if (concatData) {
      mediaTypes.value = mediaTypes.value.concat(data)
    } else {
      mediaTypes.value = data
    }
    if (pagination !== undefined) {
      paginationData.value = new PaginationData(pagination)
    }
  }

  const loadColumnConfig = async () => {
    try {
      const response = await columnConfigService.getVisibleFields('mediaType', 'list')
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
      const response = await columnConfigService.updateVisibleFields('mediaType', 'list', fields)
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
  /** Navigate to mediaType detail view */
  const viewMediaType = (mediaType: MediaType) => {
    router.push({ name: 'mediaTypedetailsview', params: { id: mediaType.getKeyValue() } })
  }

  /** Navigate to mediaType list view */
  const goToListView = () => {
    router.push({ path: '/mediaTypes' })
  }

  /** Navigate to mediaType create form */
  const goToCreateFormView = () => {
    router.push({ path: '/mediaTypes/create' })
  }

  /** Navigate to mediaType update form */
  const goToUpdateFormView = (mediaType: MediaType) => {
    router.push({ name: 'mediaTypeupdateview', params: { id: mediaType.getKeyValue() } })
  }

  /**
   * Delete a mediaType by id
   * @param mediaType MediaType object to delete
   */
  const deleteMediaType = async (mediaType: MediaType) => {
    const result = await mediaTypeService.remove(mediaType.getKeyValue())
    if (result.error) {
      message.value = result.error
    }
    return result
  }

  /**
   * Create a new mediaType
   * @param mediaTypeFormDTO MediaType object to create
   */
  const createMediaType = async (mediaTypeFormDTO: Partial<MediaTypeFormDTO>) => {
    const dto = new MediaTypeFormDTO(mediaTypeFormDTO)
    const mediaType: Partial<MediaType> = await dto.toEntity()
    const { data, error, errors } = await mediaTypeService.create(mediaType)
    if (error) {
      message.value = error
    }
    return { data, errors }
  }

  /**
   * Update an existing mediaType
   * @param mediaTypeFormDTO MediaType object to update
   */
  const updateMediaType = async (
    id: number | string,
    mediaTypeFormDTO: Partial<MediaTypeFormDTO>,
  ) => {
    const dto = new MediaTypeFormDTO(mediaTypeFormDTO)
    const mediaType: Partial<MediaType> = await dto.toEntity()
    const { data, error } = await mediaTypeService.update(id, mediaType)
    if (error) {
      message.value = error
    }
    return data
  }

  // API Requests
  /** Load all mediaTypes from the API */
  const loadMediaTypes = async (
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
    } = await mediaTypeService.getAll(pagination, sortFields)
    handleResponse(data, err, paginationresult)

    if (unpagined && mediaTypes.value.length > 0) {
      while (paginationData.value.hasNext()) {
        paginationData.value.nextPage()
        const {
          data: nextData,
          error: nextErr,
          pagination: nextPagination,
        } = await mediaTypeService.getAll(paginationData.value.toParameter(), sortFields)
        handleResponse(nextData, nextErr, nextPagination, true)
      }
    }
    stopLoading()
  }

  /**
   * Get a single mediaType by id
   * @param id MediaType ID
   */
  const getMediaTypeById = async (id: number | string) => {
    const result = await mediaTypeService.getById(id)
    if (result.error) {
      message.value = result.error
    }
    return result
  }

  /**
   * Search mediaTypes with filters, pagination, and sorting
   * @param filters Partial MediaType object containing search filters
   * @param pagination Pagination request parameters
   * @param sortFields Array of fields to sort
   */
  const searchMediaTypes = async (
    unpagined: boolean = false,
    filters: Partial<MediaType>,
    pagination: PaginationRequestParameter,
    sortFields: SortFieldParameter[],
  ) => {
    message.value = null
    startLoading()
    const {
      data,
      error: err,
      pagination: paginationresult,
    } = await mediaTypeService.search(filters, pagination, sortFields)
    handleResponse(data, err, paginationresult)

    if (unpagined) {
      while (paginationData.value.hasNext()) {
        paginationData.value.nextPage()
        const {
          data: nextData,
          error: nextErr,
          pagination: nextPagination,
        } = await mediaTypeService.search(filters, paginationData.value.toParameter(), sortFields)
        handleResponse(nextData, nextErr, nextPagination, true)
      }
    }
    stopLoading()
  }
  const exportMediaTypesToCsv = async (data: Partial<MediaType[]>) => {
    console.log('Exporting MediaType to CSV', data)
    await mediaTypeService.exportCsv(data)
  }

  /** Getter for pagination data */
  const getPaginationData = () => paginationData.value

  return {
    // state
    mediaTypes,
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
    exportMediaTypesToCsv,
    loadMediaTypes,
    searchMediaTypes,
    getMediaTypeById,
    viewMediaType,
    goToListView,
    goToCreateFormView,
    goToUpdateFormView,
    deleteMediaType,
    createMediaType,
    updateMediaType,
  }
}
