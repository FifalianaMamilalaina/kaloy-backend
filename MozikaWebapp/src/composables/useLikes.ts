import { ref, watch } from 'vue'
import { Like, LikeFormDTO } from '@/models/LikeModel'
import * as likeService from '@/services/LikeService'
import * as columnConfigService from '@/services/ColumnConfigService'
import { useLoading } from './useLoading'
import { PaginationRequestParameter, SortFieldParameter } from '@/models/api/RequestModel'
import { PaginationData } from '@/models/api/PageResponseModel'
import { useRouter } from 'vue-router'

export const DEFAULT_LIST_VIEW_FIELDS = ['id']

/**
 * Composable to manage Like entities including CRUD operations,
 * search, pagination, foreign key loading, and navigation.
 */
export function useLikes() {
  /** List of all likes */
  const likes = ref<Like[]>([])

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
  const savedLayoutMode = localStorage.getItem('layoutMode_like') as 'list' | 'card' | null
  const layoutMode = ref<'list' | 'card'>(savedLayoutMode || 'list')

  watch(layoutMode, (newValue) => {
    localStorage.setItem('layoutMode_like', newValue)
  })

  /**
   * Handles API response by updating the likes and pagination
   * @param data Array of Like returned by the API
   * @param err Optional error message
   * @param pagination Optional pagination data
   */
  const handleResponse = (
    data: Like[],
    err: string | undefined,
    pagination?: PaginationData,
    concatData: boolean = false,
  ) => {
    if (err) {
      message.value = err
    }
    if (concatData) {
      likes.value = likes.value.concat(data)
    } else {
      likes.value = data
    }
    if (pagination !== undefined) {
      paginationData.value = new PaginationData(pagination)
    }
  }

  const loadColumnConfig = async () => {
    try {
      const response = await columnConfigService.getVisibleFields('like', 'list')
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
      const response = await columnConfigService.updateVisibleFields('like', 'list', fields)
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
  /** Navigate to like detail view */
  const viewLike = (like: Like) => {
    router.push({ name: 'likedetailsview', params: { id: like.getKeyValue() } })
  }

  /** Navigate to like list view */
  const goToListView = () => {
    router.push({ path: '/likes' })
  }

  /** Navigate to like create form */
  const goToCreateFormView = () => {
    router.push({ path: '/likes/create' })
  }

  /** Navigate to like update form */
  const goToUpdateFormView = (like: Like) => {
    router.push({ name: 'likeupdateview', params: { id: like.getKeyValue() } })
  }

  /**
   * Delete a like by id
   * @param like Like object to delete
   */
  const deleteLike = async (like: Like) => {
    const result = await likeService.remove(like.getKeyValue())
    if (result.error) {
      message.value = result.error
    }
    return result
  }

  /**
   * Create a new like
   * @param likeFormDTO Like object to create
   */
  const createLike = async (likeFormDTO: Partial<LikeFormDTO>) => {
    const dto = new LikeFormDTO(likeFormDTO)
    const like: Partial<Like> = await dto.toEntity()
    const { data, error, errors } = await likeService.create(like)
    if (error) {
      message.value = error
    }
    return { data, errors }
  }

  /**
   * Update an existing like
   * @param likeFormDTO Like object to update
   */
  const updateLike = async (id: number | string, likeFormDTO: Partial<LikeFormDTO>) => {
    const dto = new LikeFormDTO(likeFormDTO)
    const like: Partial<Like> = await dto.toEntity()
    const { data, error } = await likeService.update(id, like)
    if (error) {
      message.value = error
    }
    return data
  }

  // API Requests
  /** Load all likes from the API */
  const loadLikes = async (
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
    } = await likeService.getAll(pagination, sortFields)
    handleResponse(data, err, paginationresult)

    if (unpagined && likes.value.length > 0) {
      while (paginationData.value.hasNext()) {
        paginationData.value.nextPage()
        const {
          data: nextData,
          error: nextErr,
          pagination: nextPagination,
        } = await likeService.getAll(paginationData.value.toParameter(), sortFields)
        handleResponse(nextData, nextErr, nextPagination, true)
      }
    }
    stopLoading()
  }

  /**
   * Get a single like by id
   * @param id Like ID
   */
  const getLikeById = async (id: number | string) => {
    const result = await likeService.getById(id)
    if (result.error) {
      message.value = result.error
    }
    return result
  }

  /**
   * Search likes with filters, pagination, and sorting
   * @param filters Partial Like object containing search filters
   * @param pagination Pagination request parameters
   * @param sortFields Array of fields to sort
   */
  const searchLikes = async (
    unpagined: boolean = false,
    filters: Partial<Like>,
    pagination: PaginationRequestParameter,
    sortFields: SortFieldParameter[],
  ) => {
    message.value = null
    startLoading()
    const {
      data,
      error: err,
      pagination: paginationresult,
    } = await likeService.search(filters, pagination, sortFields)
    handleResponse(data, err, paginationresult)

    if (unpagined) {
      while (paginationData.value.hasNext()) {
        paginationData.value.nextPage()
        const {
          data: nextData,
          error: nextErr,
          pagination: nextPagination,
        } = await likeService.search(filters, paginationData.value.toParameter(), sortFields)
        handleResponse(nextData, nextErr, nextPagination, true)
      }
    }
    stopLoading()
  }
  const exportLikesToCsv = async (data: Partial<Like[]>) => {
    console.log('Exporting Like to CSV', data)
    await likeService.exportCsv(data)
  }

  const getAllLikesByUserId = async (
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
    } = await likeService.getAllByUserId(userId, pagination, sortFields)
    handleResponse(data, err, paginationresult)

    if (unpagined) {
      while (paginationData.value.hasNext() && likes.value.length > 0) {
        paginationData.value.nextPage()
        const {
          data: nextData,
          error: nextErr,
          pagination: nextPagination,
        } = await likeService.getAllByUserId(userId, paginationData.value.toParameter(), sortFields)
        handleResponse(nextData, nextErr, nextPagination, true)
      }
    }
    stopLoading()
  }

  /** Getter for pagination data */
  const getPaginationData = () => paginationData.value

  return {
    // state
    likes,
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
    exportLikesToCsv,
    loadLikes,
    searchLikes,
    getLikeById,
    viewLike,
    goToListView,
    goToCreateFormView,
    goToUpdateFormView,
    deleteLike,
    createLike,
    updateLike,
    getAllLikesByUserId,
  }
}
