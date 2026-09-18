import { ref, watch } from 'vue'
import {
  EditorialPlaylistSong,
  EditorialPlaylistSongFormDTO,
} from '@/models/EditorialPlaylistSongModel'
import * as editorialPlaylistSongService from '@/services/EditorialPlaylistSongService'
import * as columnConfigService from '@/services/ColumnConfigService'
import { useLoading } from './useLoading'
import { PaginationRequestParameter, SortFieldParameter } from '@/models/api/RequestModel'
import { PaginationData } from '@/models/api/PageResponseModel'
import { useRouter } from 'vue-router'

export const DEFAULT_LIST_VIEW_FIELDS = ['id']

/**
 * Composable to manage EditorialPlaylistSong entities including CRUD operations,
 * search, pagination, foreign key loading, and navigation.
 */
export function useEditorialPlaylistSongs() {
  /** List of all editorialPlaylistSongs */
  const editorialPlaylistSongs = ref<EditorialPlaylistSong[]>([])

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
  const savedLayoutMode = localStorage.getItem('layoutMode_editorialPlaylistSong') as
    | 'list'
    | 'card'
    | null
  const layoutMode = ref<'list' | 'card'>(savedLayoutMode || 'list')

  watch(layoutMode, (newValue) => {
    localStorage.setItem('layoutMode_editorialPlaylistSong', newValue)
  })

  /**
   * Handles API response by updating the editorialPlaylistSongs and pagination
   * @param data Array of EditorialPlaylistSong returned by the API
   * @param err Optional error message
   * @param pagination Optional pagination data
   */
  const handleResponse = (
    data: EditorialPlaylistSong[],
    err: string | undefined,
    pagination?: PaginationData,
    concatData: boolean = false,
  ) => {
    if (err) {
      message.value = err
    }
    if (concatData) {
      editorialPlaylistSongs.value = editorialPlaylistSongs.value.concat(data)
    } else {
      editorialPlaylistSongs.value = data
    }
    if (pagination !== undefined) {
      paginationData.value = new PaginationData(pagination)
    }
  }

  const loadColumnConfig = async () => {
    try {
      const response = await columnConfigService.getVisibleFields('editorialPlaylistSong', 'list')
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
        'editorialPlaylistSong',
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
  /** Navigate to editorialPlaylistSong detail view */
  const viewEditorialPlaylistSong = (editorialPlaylistSong: EditorialPlaylistSong) => {
    router.push({
      name: 'editorialPlaylistSongdetailsview',
      params: { id: editorialPlaylistSong.getKeyValue() },
    })
  }

  /** Navigate to editorialPlaylistSong list view */
  const goToListView = () => {
    router.push({ path: '/editorialPlaylistSongs' })
  }

  /** Navigate to editorialPlaylistSong create form */
  const goToCreateFormView = () => {
    router.push({ path: '/editorialPlaylistSongs/create' })
  }

  /** Navigate to editorialPlaylistSong update form */
  const goToUpdateFormView = (editorialPlaylistSong: EditorialPlaylistSong) => {
    router.push({
      name: 'editorialPlaylistSongupdateview',
      params: { id: editorialPlaylistSong.getKeyValue() },
    })
  }

  /**
   * Delete a editorialPlaylistSong by id
   * @param editorialPlaylistSong EditorialPlaylistSong object to delete
   */
  const deleteEditorialPlaylistSong = async (editorialPlaylistSong: EditorialPlaylistSong) => {
    const result = await editorialPlaylistSongService.remove(editorialPlaylistSong.getKeyValue())
    if (result.error) {
      message.value = result.error
    }
    return result
  }

  /**
   * Create a new editorialPlaylistSong
   * @param editorialPlaylistSongFormDTO EditorialPlaylistSong object to create
   */
  const createEditorialPlaylistSong = async (
    editorialPlaylistSongFormDTO: Partial<EditorialPlaylistSongFormDTO>,
  ) => {
    const dto = new EditorialPlaylistSongFormDTO(editorialPlaylistSongFormDTO)
    const editorialPlaylistSong: Partial<EditorialPlaylistSong> = await dto.toEntity()
    const { data, error, errors } = await editorialPlaylistSongService.create(editorialPlaylistSong)
    if (error) {
      message.value = error
    }
    return { data, errors }
  }

  /**
   * Update an existing editorialPlaylistSong
   * @param editorialPlaylistSongFormDTO EditorialPlaylistSong object to update
   */
  const updateEditorialPlaylistSong = async (
    id: number | string,
    editorialPlaylistSongFormDTO: Partial<EditorialPlaylistSongFormDTO>,
  ) => {
    const dto = new EditorialPlaylistSongFormDTO(editorialPlaylistSongFormDTO)
    const editorialPlaylistSong: Partial<EditorialPlaylistSong> = await dto.toEntity()
    const { data, error } = await editorialPlaylistSongService.update(id, editorialPlaylistSong)
    if (error) {
      message.value = error
    }
    return data
  }

  // API Requests
  /** Load all editorialPlaylistSongs from the API */
  const loadEditorialPlaylistSongs = async (
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
    } = await editorialPlaylistSongService.getAll(pagination, sortFields)
    handleResponse(data, err, paginationresult)

    if (unpagined && editorialPlaylistSongs.value.length > 0) {
      while (paginationData.value.hasNext()) {
        paginationData.value.nextPage()
        const {
          data: nextData,
          error: nextErr,
          pagination: nextPagination,
        } = await editorialPlaylistSongService.getAll(
          paginationData.value.toParameter(),
          sortFields,
        )
        handleResponse(nextData, nextErr, nextPagination, true)
      }
    }
    stopLoading()
  }

  /**
   * Get a single editorialPlaylistSong by id
   * @param id EditorialPlaylistSong ID
   */
  const getEditorialPlaylistSongById = async (id: number | string) => {
    const result = await editorialPlaylistSongService.getById(id)
    if (result.error) {
      message.value = result.error
    }
    return result
  }

  /**
   * Search editorialPlaylistSongs with filters, pagination, and sorting
   * @param filters Partial EditorialPlaylistSong object containing search filters
   * @param pagination Pagination request parameters
   * @param sortFields Array of fields to sort
   */
  const searchEditorialPlaylistSongs = async (
    unpagined: boolean = false,
    filters: Partial<EditorialPlaylistSong>,
    pagination: PaginationRequestParameter,
    sortFields: SortFieldParameter[],
  ) => {
    message.value = null
    startLoading()
    const {
      data,
      error: err,
      pagination: paginationresult,
    } = await editorialPlaylistSongService.search(filters, pagination, sortFields)
    handleResponse(data, err, paginationresult)

    if (unpagined) {
      while (paginationData.value.hasNext()) {
        paginationData.value.nextPage()
        const {
          data: nextData,
          error: nextErr,
          pagination: nextPagination,
        } = await editorialPlaylistSongService.search(
          filters,
          paginationData.value.toParameter(),
          sortFields,
        )
        handleResponse(nextData, nextErr, nextPagination, true)
      }
    }
    stopLoading()
  }
  const exportEditorialPlaylistSongsToCsv = async (data: Partial<EditorialPlaylistSong[]>) => {
    console.log('Exporting EditorialPlaylistSong to CSV', data)
    await editorialPlaylistSongService.exportCsv(data)
  }

  const getAllEditorialPlaylistSongsBySongId = async (
    songId: number | string | undefined,
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
    } = await editorialPlaylistSongService.getAllBySongId(songId, pagination, sortFields)
    handleResponse(data, err, paginationresult)

    if (unpagined) {
      while (paginationData.value.hasNext() && editorialPlaylistSongs.value.length > 0) {
        paginationData.value.nextPage()
        const {
          data: nextData,
          error: nextErr,
          pagination: nextPagination,
        } = await editorialPlaylistSongService.getAllBySongId(
          songId,
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
    editorialPlaylistSongs,
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
    exportEditorialPlaylistSongsToCsv,
    loadEditorialPlaylistSongs,
    searchEditorialPlaylistSongs,
    getEditorialPlaylistSongById,
    viewEditorialPlaylistSong,
    goToListView,
    goToCreateFormView,
    goToUpdateFormView,
    deleteEditorialPlaylistSong,
    createEditorialPlaylistSong,
    updateEditorialPlaylistSong,
    getAllEditorialPlaylistSongsBySongId,
  }
}
