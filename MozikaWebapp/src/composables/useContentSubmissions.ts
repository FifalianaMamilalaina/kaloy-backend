import { ref, watch } from 'vue'
import { ContentSubmission, ContentSubmissionFormDTO } from '@/models/ContentSubmissionModel'
import * as contentSubmissionService from '@/services/ContentSubmissionService'
import * as columnConfigService from '@/services/ColumnConfigService'
import { useLoading } from './useLoading'
import { PaginationRequestParameter, SortFieldParameter } from '@/models/api/RequestModel'
import { PaginationData } from '@/models/api/PageResponseModel'
import { useRouter } from 'vue-router'

export const DEFAULT_LIST_VIEW_FIELDS = ['id']

/**
 * Composable to manage ContentSubmission entities including CRUD operations,
 * search, pagination, foreign key loading, and navigation.
 */
export function useContentSubmissions() {
  /** List of all contentSubmissions */
  const contentSubmissions = ref<ContentSubmission[]>([])

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
  const savedLayoutMode = localStorage.getItem('layoutMode_contentSubmission') as
    | 'list'
    | 'card'
    | null
  const layoutMode = ref<'list' | 'card'>(savedLayoutMode || 'list')

  watch(layoutMode, (newValue) => {
    localStorage.setItem('layoutMode_contentSubmission', newValue)
  })

  /**
   * Handles API response by updating the contentSubmissions and pagination
   * @param data Array of ContentSubmission returned by the API
   * @param err Optional error message
   * @param pagination Optional pagination data
   */
  const handleResponse = (
    data: ContentSubmission[],
    err: string | undefined,
    pagination?: PaginationData,
    concatData: boolean = false,
  ) => {
    if (err) {
      message.value = err
    }
    if (concatData) {
      contentSubmissions.value = contentSubmissions.value.concat(data)
    } else {
      contentSubmissions.value = data
    }
    if (pagination !== undefined) {
      paginationData.value = new PaginationData(pagination)
    }
  }

  const loadColumnConfig = async () => {
    try {
      const response = await columnConfigService.getVisibleFields('contentSubmission', 'list')
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
        'contentSubmission',
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
  /** Navigate to contentSubmission detail view */
  const viewContentSubmission = (contentSubmission: ContentSubmission) => {
    router.push({
      name: 'contentSubmissiondetailsview',
      params: { id: contentSubmission.getKeyValue() },
    })
  }

  /** Navigate to contentSubmission list view */
  const goToListView = () => {
    router.push({ path: '/contentSubmissions' })
  }

  /** Navigate to contentSubmission create form */
  const goToCreateFormView = () => {
    router.push({ path: '/contentSubmissions/create' })
  }

  /** Navigate to contentSubmission update form */
  const goToUpdateFormView = (contentSubmission: ContentSubmission) => {
    router.push({
      name: 'contentSubmissionupdateview',
      params: { id: contentSubmission.getKeyValue() },
    })
  }

  /**
   * Delete a contentSubmission by id
   * @param contentSubmission ContentSubmission object to delete
   */
  const deleteContentSubmission = async (contentSubmission: ContentSubmission) => {
    const result = await contentSubmissionService.remove(contentSubmission.getKeyValue())
    if (result.error) {
      message.value = result.error
    }
    return result
  }

  /**
   * Create a new contentSubmission
   * @param contentSubmissionFormDTO ContentSubmission object to create
   */
  const createContentSubmission = async (
    contentSubmissionFormDTO: Partial<ContentSubmissionFormDTO>,
  ) => {
    const dto = new ContentSubmissionFormDTO(contentSubmissionFormDTO)
    const contentSubmission: Partial<ContentSubmission> = await dto.toEntity()
    const { data, error, errors } = await contentSubmissionService.create(contentSubmission)
    if (error) {
      message.value = error
    }
    return { data, errors }
  }

  /**
   * Update an existing contentSubmission
   * @param contentSubmissionFormDTO ContentSubmission object to update
   */
  const updateContentSubmission = async (
    id: number | string,
    contentSubmissionFormDTO: Partial<ContentSubmissionFormDTO>,
  ) => {
    const dto = new ContentSubmissionFormDTO(contentSubmissionFormDTO)
    const contentSubmission: Partial<ContentSubmission> = await dto.toEntity()
    const { data, error } = await contentSubmissionService.update(id, contentSubmission)
    if (error) {
      message.value = error
    }
    return data
  }

  // API Requests
  /** Load all contentSubmissions from the API */
  const loadContentSubmissions = async (
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
    } = await contentSubmissionService.getAll(pagination, sortFields)
    handleResponse(data, err, paginationresult)

    if (unpagined && contentSubmissions.value.length > 0) {
      while (paginationData.value.hasNext()) {
        paginationData.value.nextPage()
        const {
          data: nextData,
          error: nextErr,
          pagination: nextPagination,
        } = await contentSubmissionService.getAll(paginationData.value.toParameter(), sortFields)
        handleResponse(nextData, nextErr, nextPagination, true)
      }
    }
    stopLoading()
  }

  /**
   * Get a single contentSubmission by id
   * @param id ContentSubmission ID
   */
  const getContentSubmissionById = async (id: number | string) => {
    const result = await contentSubmissionService.getById(id)
    if (result.error) {
      message.value = result.error
    }
    return result
  }

  /**
   * Search contentSubmissions with filters, pagination, and sorting
   * @param filters Partial ContentSubmission object containing search filters
   * @param pagination Pagination request parameters
   * @param sortFields Array of fields to sort
   */
  const searchContentSubmissions = async (
    unpagined: boolean = false,
    filters: Partial<ContentSubmission>,
    pagination: PaginationRequestParameter,
    sortFields: SortFieldParameter[],
  ) => {
    message.value = null
    startLoading()
    const {
      data,
      error: err,
      pagination: paginationresult,
    } = await contentSubmissionService.search(filters, pagination, sortFields)
    handleResponse(data, err, paginationresult)

    if (unpagined) {
      while (paginationData.value.hasNext()) {
        paginationData.value.nextPage()
        const {
          data: nextData,
          error: nextErr,
          pagination: nextPagination,
        } = await contentSubmissionService.search(
          filters,
          paginationData.value.toParameter(),
          sortFields,
        )
        handleResponse(nextData, nextErr, nextPagination, true)
      }
    }
    stopLoading()
  }
  const exportContentSubmissionsToCsv = async (data: Partial<ContentSubmission[]>) => {
    console.log('Exporting ContentSubmission to CSV', data)
    await contentSubmissionService.exportCsv(data)
  }

  const getAllContentSubmissionsByArtistId = async (
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
    } = await contentSubmissionService.getAllByArtistId(artistId, pagination, sortFields)
    handleResponse(data, err, paginationresult)

    if (unpagined) {
      while (paginationData.value.hasNext() && contentSubmissions.value.length > 0) {
        paginationData.value.nextPage()
        const {
          data: nextData,
          error: nextErr,
          pagination: nextPagination,
        } = await contentSubmissionService.getAllByArtistId(
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
    contentSubmissions,
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
    exportContentSubmissionsToCsv,
    loadContentSubmissions,
    searchContentSubmissions,
    getContentSubmissionById,
    viewContentSubmission,
    goToListView,
    goToCreateFormView,
    goToUpdateFormView,
    deleteContentSubmission,
    createContentSubmission,
    updateContentSubmission,
    getAllContentSubmissionsByArtistId,
  }
}
