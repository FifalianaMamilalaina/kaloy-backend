import { ref, watch } from 'vue'
import { Follow, FollowFormDTO } from '@/models/FollowModel'
import * as followService from '@/services/FollowService'
import * as columnConfigService from '@/services/ColumnConfigService'
import { useLoading } from './useLoading'
import { PaginationRequestParameter, SortFieldParameter } from '@/models/api/RequestModel'
import { PaginationData } from '@/models/api/PageResponseModel'
import { useRouter } from 'vue-router'

export const DEFAULT_LIST_VIEW_FIELDS = ['id']

/**
 * Composable to manage Follow entities including CRUD operations,
 * search, pagination, foreign key loading, and navigation.
 */
export function useFollows() {
  /** List of all follows */
  const follows = ref<Follow[]>([])

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
  const savedLayoutMode = localStorage.getItem('layoutMode_follow') as 'list' | 'card' | null
  const layoutMode = ref<'list' | 'card'>(savedLayoutMode || 'list')

  watch(layoutMode, (newValue) => {
    localStorage.setItem('layoutMode_follow', newValue)
  })

  /**
   * Handles API response by updating the follows and pagination
   * @param data Array of Follow returned by the API
   * @param err Optional error message
   * @param pagination Optional pagination data
   */
  const handleResponse = (
    data: Follow[],
    err: string | undefined,
    pagination?: PaginationData,
    concatData: boolean = false,
  ) => {
    if (err) {
      message.value = err
    }
    if (concatData) {
      follows.value = follows.value.concat(data)
    } else {
      follows.value = data
    }
    if (pagination !== undefined) {
      paginationData.value = new PaginationData(pagination)
    }
  }

  const loadColumnConfig = async () => {
    try {
      const response = await columnConfigService.getVisibleFields('follow', 'list')
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
      const response = await columnConfigService.updateVisibleFields('follow', 'list', fields)
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
  /** Navigate to follow detail view */
  const viewFollow = (follow: Follow) => {
    router.push({ name: 'followdetailsview', params: { id: follow.getKeyValue() } })
  }

  /** Navigate to follow list view */
  const goToListView = () => {
    router.push({ path: '/follows' })
  }

  /** Navigate to follow create form */
  const goToCreateFormView = () => {
    router.push({ path: '/follows/create' })
  }

  /** Navigate to follow update form */
  const goToUpdateFormView = (follow: Follow) => {
    router.push({ name: 'followupdateview', params: { id: follow.getKeyValue() } })
  }

  /**
   * Delete a follow by id
   * @param follow Follow object to delete
   */
  const deleteFollow = async (follow: Follow) => {
    const result = await followService.remove(follow.getKeyValue())
    if (result.error) {
      message.value = result.error
    }
    return result
  }

  /**
   * Create a new follow
   * @param followFormDTO Follow object to create
   */
  const createFollow = async (followFormDTO: Partial<FollowFormDTO>) => {
    const dto = new FollowFormDTO(followFormDTO)
    const follow: Partial<Follow> = await dto.toEntity()
    const { data, error, errors } = await followService.create(follow)
    if (error) {
      message.value = error
    }
    return { data, errors }
  }

  /**
   * Update an existing follow
   * @param followFormDTO Follow object to update
   */
  const updateFollow = async (id: number | string, followFormDTO: Partial<FollowFormDTO>) => {
    const dto = new FollowFormDTO(followFormDTO)
    const follow: Partial<Follow> = await dto.toEntity()
    const { data, error } = await followService.update(id, follow)
    if (error) {
      message.value = error
    }
    return data
  }

  // API Requests
  /** Load all follows from the API */
  const loadFollows = async (
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
    } = await followService.getAll(pagination, sortFields)
    handleResponse(data, err, paginationresult)

    if (unpagined && follows.value.length > 0) {
      while (paginationData.value.hasNext()) {
        paginationData.value.nextPage()
        const {
          data: nextData,
          error: nextErr,
          pagination: nextPagination,
        } = await followService.getAll(paginationData.value.toParameter(), sortFields)
        handleResponse(nextData, nextErr, nextPagination, true)
      }
    }
    stopLoading()
  }

  /**
   * Get a single follow by id
   * @param id Follow ID
   */
  const getFollowById = async (id: number | string) => {
    const result = await followService.getById(id)
    if (result.error) {
      message.value = result.error
    }
    return result
  }

  /**
   * Search follows with filters, pagination, and sorting
   * @param filters Partial Follow object containing search filters
   * @param pagination Pagination request parameters
   * @param sortFields Array of fields to sort
   */
  const searchFollows = async (
    unpagined: boolean = false,
    filters: Partial<Follow>,
    pagination: PaginationRequestParameter,
    sortFields: SortFieldParameter[],
  ) => {
    message.value = null
    startLoading()
    const {
      data,
      error: err,
      pagination: paginationresult,
    } = await followService.search(filters, pagination, sortFields)
    handleResponse(data, err, paginationresult)

    if (unpagined) {
      while (paginationData.value.hasNext()) {
        paginationData.value.nextPage()
        const {
          data: nextData,
          error: nextErr,
          pagination: nextPagination,
        } = await followService.search(filters, paginationData.value.toParameter(), sortFields)
        handleResponse(nextData, nextErr, nextPagination, true)
      }
    }
    stopLoading()
  }
  const exportFollowsToCsv = async (data: Partial<Follow[]>) => {
    console.log('Exporting Follow to CSV', data)
    await followService.exportCsv(data)
  }

  const getAllFollowsByUserId = async (
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
    } = await followService.getAllByUserId(userId, pagination, sortFields)
    handleResponse(data, err, paginationresult)

    if (unpagined) {
      while (paginationData.value.hasNext() && follows.value.length > 0) {
        paginationData.value.nextPage()
        const {
          data: nextData,
          error: nextErr,
          pagination: nextPagination,
        } = await followService.getAllByUserId(
          userId,
          paginationData.value.toParameter(),
          sortFields,
        )
        handleResponse(nextData, nextErr, nextPagination, true)
      }
    }
    stopLoading()
  }
  const getAllFollowsByArtistId = async (
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
    } = await followService.getAllByArtistId(artistId, pagination, sortFields)
    handleResponse(data, err, paginationresult)

    if (unpagined) {
      while (paginationData.value.hasNext() && follows.value.length > 0) {
        paginationData.value.nextPage()
        const {
          data: nextData,
          error: nextErr,
          pagination: nextPagination,
        } = await followService.getAllByArtistId(
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
    follows,
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
    exportFollowsToCsv,
    loadFollows,
    searchFollows,
    getFollowById,
    viewFollow,
    goToListView,
    goToCreateFormView,
    goToUpdateFormView,
    deleteFollow,
    createFollow,
    updateFollow,
    getAllFollowsByUserId,
    getAllFollowsByArtistId,
  }
}
