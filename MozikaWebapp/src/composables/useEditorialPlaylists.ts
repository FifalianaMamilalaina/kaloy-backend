import { ref, watch } from 'vue'
import { EditorialPlaylist, EditorialPlaylistFormDTO } from '@/models/EditorialPlaylistModel'
import * as editorialPlaylistService from '@/services/EditorialPlaylistService'
import * as columnConfigService from '@/services/ColumnConfigService'
import { useLoading } from './useLoading'
import { PaginationRequestParameter, SortFieldParameter } from '@/models/api/RequestModel'
import { PaginationData } from '@/models/api/PageResponseModel'
import { useRouter } from 'vue-router'

export const DEFAULT_LIST_VIEW_FIELDS = ['id']

/**
 * Composable to manage EditorialPlaylist entities including CRUD operations,
 * search, pagination, foreign key loading, and navigation.
 */
export function useEditorialPlaylists() {
  /** List of all editorialPlaylists */
  const editorialPlaylists = ref<EditorialPlaylist[]>([])

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
  const savedLayoutMode = localStorage.getItem('layoutMode_editorialPlaylist') as
    | 'list'
    | 'card'
    | null
  const layoutMode = ref<'list' | 'card'>(savedLayoutMode || 'list')

  watch(layoutMode, (newValue) => {
    localStorage.setItem('layoutMode_editorialPlaylist', newValue)
  })

  /**
   * Handles API response by updating the editorialPlaylists and pagination
   * @param data Array of EditorialPlaylist returned by the API
   * @param err Optional error message
   * @param pagination Optional pagination data
   */
  const handleResponse = (
    data: EditorialPlaylist[],
    err: string | undefined,
    pagination?: PaginationData,
    concatData: boolean = false,
  ) => {
    if (err) {
      message.value = err
    }
    if (concatData) {
      editorialPlaylists.value = editorialPlaylists.value.concat(data)
    } else {
      editorialPlaylists.value = data
    }
    if (pagination !== undefined) {
      paginationData.value = new PaginationData(pagination)
    }
  }

  const loadColumnConfig = async () => {
    try {
      const response = await columnConfigService.getVisibleFields('editorialPlaylist', 'list')
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
        'editorialPlaylist',
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
  /** Navigate to editorialPlaylist detail view */
  const viewEditorialPlaylist = (editorialPlaylist: EditorialPlaylist) => {
    router.push({
      name: 'editorialPlaylistdetailsview',
      params: { id: editorialPlaylist.getKeyValue() },
    })
  }

  /** Navigate to editorialPlaylist list view */
  const goToListView = () => {
    router.push({ path: '/editorialPlaylists' })
  }

  /** Navigate to editorialPlaylist create form */
  const goToCreateFormView = () => {
    router.push({ path: '/editorialPlaylists/create' })
  }

  /** Navigate to editorialPlaylist update form */
  const goToUpdateFormView = (editorialPlaylist: EditorialPlaylist) => {
    router.push({
      name: 'editorialPlaylistupdateview',
      params: { id: editorialPlaylist.getKeyValue() },
    })
  }

  /**
   * Delete a editorialPlaylist by id
   * @param editorialPlaylist EditorialPlaylist object to delete
   */
  const deleteEditorialPlaylist = async (editorialPlaylist: EditorialPlaylist) => {
    const result = await editorialPlaylistService.remove(editorialPlaylist.getKeyValue())
    if (result.error) {
      message.value = result.error
    }
    return result
  }

  /**
   * Create a new editorialPlaylist
   * @param editorialPlaylistFormDTO EditorialPlaylist object to create
   */
  const createEditorialPlaylist = async (
    editorialPlaylistFormDTO: Partial<EditorialPlaylistFormDTO>,
  ) => {
    const dto = new EditorialPlaylistFormDTO(editorialPlaylistFormDTO)
    const editorialPlaylist: Partial<EditorialPlaylist> = await dto.toEntity()
    const { data, error, errors } = await editorialPlaylistService.create(editorialPlaylist)
    if (error) {
      message.value = error
    }
    return { data, errors }
  }

  /**
   * Update an existing editorialPlaylist
   * @param editorialPlaylistFormDTO EditorialPlaylist object to update
   */
  const updateEditorialPlaylist = async (
    id: number | string,
    editorialPlaylistFormDTO: Partial<EditorialPlaylistFormDTO>,
  ) => {
    const dto = new EditorialPlaylistFormDTO(editorialPlaylistFormDTO)
    const editorialPlaylist: Partial<EditorialPlaylist> = await dto.toEntity()
    const { data, error } = await editorialPlaylistService.update(id, editorialPlaylist)
    if (error) {
      message.value = error
    }
    return data
  }

  // API Requests
  /** Load all editorialPlaylists from the API */
  const loadEditorialPlaylists = async (
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
    } = await editorialPlaylistService.getAll(pagination, sortFields)
    handleResponse(data, err, paginationresult)

    if (unpagined && editorialPlaylists.value.length > 0) {
      while (paginationData.value.hasNext()) {
        paginationData.value.nextPage()
        const {
          data: nextData,
          error: nextErr,
          pagination: nextPagination,
        } = await editorialPlaylistService.getAll(paginationData.value.toParameter(), sortFields)
        handleResponse(nextData, nextErr, nextPagination, true)
      }
    }
    stopLoading()
  }

  /**
   * Get a single editorialPlaylist by id
   * @param id EditorialPlaylist ID
   */
  const getEditorialPlaylistById = async (id: number | string) => {
    const result = await editorialPlaylistService.getById(id)
    if (result.error) {
      message.value = result.error
    }
    return result
  }

  /**
   * Search editorialPlaylists with filters, pagination, and sorting
   * @param filters Partial EditorialPlaylist object containing search filters
   * @param pagination Pagination request parameters
   * @param sortFields Array of fields to sort
   */
  const searchEditorialPlaylists = async (
    unpagined: boolean = false,
    filters: Partial<EditorialPlaylist>,
    pagination: PaginationRequestParameter,
    sortFields: SortFieldParameter[],
  ) => {
    message.value = null
    startLoading()
    const {
      data,
      error: err,
      pagination: paginationresult,
    } = await editorialPlaylistService.search(filters, pagination, sortFields)
    handleResponse(data, err, paginationresult)

    if (unpagined) {
      while (paginationData.value.hasNext()) {
        paginationData.value.nextPage()
        const {
          data: nextData,
          error: nextErr,
          pagination: nextPagination,
        } = await editorialPlaylistService.search(
          filters,
          paginationData.value.toParameter(),
          sortFields,
        )
        handleResponse(nextData, nextErr, nextPagination, true)
      }
    }
    stopLoading()
  }
  const exportEditorialPlaylistsToCsv = async (data: Partial<EditorialPlaylist[]>) => {
    console.log('Exporting EditorialPlaylist to CSV', data)
    await editorialPlaylistService.exportCsv(data)
  }

  const getAllEditorialPlaylistsByArtistId = async (
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
    } = await editorialPlaylistService.getAllByArtistId(artistId, pagination, sortFields)
    handleResponse(data, err, paginationresult)

    if (unpagined) {
      while (paginationData.value.hasNext() && editorialPlaylists.value.length > 0) {
        paginationData.value.nextPage()
        const {
          data: nextData,
          error: nextErr,
          pagination: nextPagination,
        } = await editorialPlaylistService.getAllByArtistId(
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
    editorialPlaylists,
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
    exportEditorialPlaylistsToCsv,
    loadEditorialPlaylists,
    searchEditorialPlaylists,
    getEditorialPlaylistById,
    viewEditorialPlaylist,
    goToListView,
    goToCreateFormView,
    goToUpdateFormView,
    deleteEditorialPlaylist,
    createEditorialPlaylist,
    updateEditorialPlaylist,
    getAllEditorialPlaylistsByArtistId,
  }
}
