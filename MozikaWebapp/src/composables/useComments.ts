import { ref, watch } from 'vue'
import { Comment, CommentFormDTO } from '@/models/CommentModel'
import * as commentService from '@/services/CommentService'
import * as columnConfigService from '@/services/ColumnConfigService'
import { useLoading } from './useLoading'
import { PaginationRequestParameter, SortFieldParameter } from '@/models/api/RequestModel'
import { PaginationData } from '@/models/api/PageResponseModel'
import { useRouter } from 'vue-router'

export const DEFAULT_LIST_VIEW_FIELDS = ['id']

/**
 * Composable to manage Comment entities including CRUD operations,
 * search, pagination, foreign key loading, and navigation.
 */
export function useComments() {
  /** List of all comments */
  const comments = ref<Comment[]>([])

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
  const savedLayoutMode = localStorage.getItem('layoutMode_comment') as 'list' | 'card' | null
  const layoutMode = ref<'list' | 'card'>(savedLayoutMode || 'list')

  watch(layoutMode, (newValue) => {
    localStorage.setItem('layoutMode_comment', newValue)
  })

  /**
   * Handles API response by updating the comments and pagination
   * @param data Array of Comment returned by the API
   * @param err Optional error message
   * @param pagination Optional pagination data
   */
  const handleResponse = (
    data: Comment[],
    err: string | undefined,
    pagination?: PaginationData,
    concatData: boolean = false,
  ) => {
    if (err) {
      message.value = err
    }
    if (concatData) {
      comments.value = comments.value.concat(data)
    } else {
      comments.value = data
    }
    if (pagination !== undefined) {
      paginationData.value = new PaginationData(pagination)
    }
  }

  const loadColumnConfig = async () => {
    try {
      const response = await columnConfigService.getVisibleFields('comment', 'list')
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
      const response = await columnConfigService.updateVisibleFields('comment', 'list', fields)
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
  /** Navigate to comment detail view */
  const viewComment = (comment: Comment) => {
    router.push({ name: 'commentdetailsview', params: { id: comment.getKeyValue() } })
  }

  /** Navigate to comment list view */
  const goToListView = () => {
    router.push({ path: '/comments' })
  }

  /** Navigate to comment create form */
  const goToCreateFormView = () => {
    router.push({ path: '/comments/create' })
  }

  /** Navigate to comment update form */
  const goToUpdateFormView = (comment: Comment) => {
    router.push({ name: 'commentupdateview', params: { id: comment.getKeyValue() } })
  }

  /**
   * Delete a comment by id
   * @param comment Comment object to delete
   */
  const deleteComment = async (comment: Comment) => {
    const result = await commentService.remove(comment.getKeyValue())
    if (result.error) {
      message.value = result.error
    }
    return result
  }

  /**
   * Create a new comment
   * @param commentFormDTO Comment object to create
   */
  const createComment = async (commentFormDTO: Partial<CommentFormDTO>) => {
    const dto = new CommentFormDTO(commentFormDTO)
    const comment: Partial<Comment> = await dto.toEntity()
    const { data, error, errors } = await commentService.create(comment)
    if (error) {
      message.value = error
    }
    return { data, errors }
  }

  /**
   * Update an existing comment
   * @param commentFormDTO Comment object to update
   */
  const updateComment = async (id: number | string, commentFormDTO: Partial<CommentFormDTO>) => {
    const dto = new CommentFormDTO(commentFormDTO)
    const comment: Partial<Comment> = await dto.toEntity()
    const { data, error } = await commentService.update(id, comment)
    if (error) {
      message.value = error
    }
    return data
  }

  // API Requests
  /** Load all comments from the API */
  const loadComments = async (
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
    } = await commentService.getAll(pagination, sortFields)
    handleResponse(data, err, paginationresult)

    if (unpagined && comments.value.length > 0) {
      while (paginationData.value.hasNext()) {
        paginationData.value.nextPage()
        const {
          data: nextData,
          error: nextErr,
          pagination: nextPagination,
        } = await commentService.getAll(paginationData.value.toParameter(), sortFields)
        handleResponse(nextData, nextErr, nextPagination, true)
      }
    }
    stopLoading()
  }

  /**
   * Get a single comment by id
   * @param id Comment ID
   */
  const getCommentById = async (id: number | string) => {
    const result = await commentService.getById(id)
    if (result.error) {
      message.value = result.error
    }
    return result
  }

  /**
   * Search comments with filters, pagination, and sorting
   * @param filters Partial Comment object containing search filters
   * @param pagination Pagination request parameters
   * @param sortFields Array of fields to sort
   */
  const searchComments = async (
    unpagined: boolean = false,
    filters: Partial<Comment>,
    pagination: PaginationRequestParameter,
    sortFields: SortFieldParameter[],
  ) => {
    message.value = null
    startLoading()
    const {
      data,
      error: err,
      pagination: paginationresult,
    } = await commentService.search(filters, pagination, sortFields)
    handleResponse(data, err, paginationresult)

    if (unpagined) {
      while (paginationData.value.hasNext()) {
        paginationData.value.nextPage()
        const {
          data: nextData,
          error: nextErr,
          pagination: nextPagination,
        } = await commentService.search(filters, paginationData.value.toParameter(), sortFields)
        handleResponse(nextData, nextErr, nextPagination, true)
      }
    }
    stopLoading()
  }
  const exportCommentsToCsv = async (data: Partial<Comment[]>) => {
    console.log('Exporting Comment to CSV', data)
    await commentService.exportCsv(data)
  }

  const getAllCommentsByUserId = async (
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
    } = await commentService.getAllByUserId(userId, pagination, sortFields)
    handleResponse(data, err, paginationresult)

    if (unpagined) {
      while (paginationData.value.hasNext() && comments.value.length > 0) {
        paginationData.value.nextPage()
        const {
          data: nextData,
          error: nextErr,
          pagination: nextPagination,
        } = await commentService.getAllByUserId(
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
    comments,
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
    exportCommentsToCsv,
    loadComments,
    searchComments,
    getCommentById,
    viewComment,
    goToListView,
    goToCreateFormView,
    goToUpdateFormView,
    deleteComment,
    createComment,
    updateComment,
    getAllCommentsByUserId,
  }
}
