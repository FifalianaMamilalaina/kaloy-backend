import { ref, watch } from 'vue'
import { PlaylistSong, PlaylistSongFormDTO } from '@/models/PlaylistSongModel'
import * as playlistSongService from '@/services/PlaylistSongService'
import * as columnConfigService from '@/services/ColumnConfigService'
import { useLoading } from './useLoading'
import { PaginationRequestParameter, SortFieldParameter } from '@/models/api/RequestModel'
import { PaginationData } from '@/models/api/PageResponseModel'
import { useRouter } from 'vue-router'

export const DEFAULT_LIST_VIEW_FIELDS = ['id']

/**
 * Composable to manage PlaylistSong entities including CRUD operations,
 * search, pagination, foreign key loading, and navigation.
 */
export function usePlaylistSongs() {
  /** List of all playlistSongs */
  const playlistSongs = ref<PlaylistSong[]>([])

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
  const savedLayoutMode = localStorage.getItem('layoutMode_playlistSong') as 'list' | 'card' | null
  const layoutMode = ref<'list' | 'card'>(savedLayoutMode || 'list')

  watch(layoutMode, (newValue) => {
    localStorage.setItem('layoutMode_playlistSong', newValue)
  })

  /**
   * Handles API response by updating the playlistSongs and pagination
   * @param data Array of PlaylistSong returned by the API
   * @param err Optional error message
   * @param pagination Optional pagination data
   */
  const handleResponse = (
    data: PlaylistSong[],
    err: string | undefined,
    pagination?: PaginationData,
    concatData: boolean = false,
  ) => {
    if (err) {
      message.value = err
    }
    if (concatData) {
      playlistSongs.value = playlistSongs.value.concat(data)
    } else {
      playlistSongs.value = data
    }
    if (pagination !== undefined) {
      paginationData.value = new PaginationData(pagination)
    }
  }

  const loadColumnConfig = async () => {
    try {
      const response = await columnConfigService.getVisibleFields('playlistSong', 'list')
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
      const response = await columnConfigService.updateVisibleFields('playlistSong', 'list', fields)
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
  /** Navigate to playlistSong detail view */
  const viewPlaylistSong = (playlistSong: PlaylistSong) => {
    router.push({ name: 'playlistSongdetailsview', params: { id: playlistSong.getKeyValue() } })
  }

  /** Navigate to playlistSong list view */
  const goToListView = () => {
    router.push({ path: '/playlistSongs' })
  }

  /** Navigate to playlistSong create form */
  const goToCreateFormView = () => {
    router.push({ path: '/playlistSongs/create' })
  }

  /** Navigate to playlistSong update form */
  const goToUpdateFormView = (playlistSong: PlaylistSong) => {
    router.push({ name: 'playlistSongupdateview', params: { id: playlistSong.getKeyValue() } })
  }

  /**
   * Delete a playlistSong by id
   * @param playlistSong PlaylistSong object to delete
   */
  const deletePlaylistSong = async (playlistSong: PlaylistSong) => {
    const result = await playlistSongService.remove(playlistSong.getKeyValue())
    if (result.error) {
      message.value = result.error
    }
    return result
  }

  /**
   * Create a new playlistSong
   * @param playlistSongFormDTO PlaylistSong object to create
   */
  const createPlaylistSong = async (playlistSongFormDTO: Partial<PlaylistSongFormDTO>) => {
    const dto = new PlaylistSongFormDTO(playlistSongFormDTO)
    const playlistSong: Partial<PlaylistSong> = await dto.toEntity()
    const { data, error, errors } = await playlistSongService.create(playlistSong)
    if (error) {
      message.value = error
    }
    return { data, errors }
  }

  /**
   * Update an existing playlistSong
   * @param playlistSongFormDTO PlaylistSong object to update
   */
  const updatePlaylistSong = async (
    id: number | string,
    playlistSongFormDTO: Partial<PlaylistSongFormDTO>,
  ) => {
    const dto = new PlaylistSongFormDTO(playlistSongFormDTO)
    const playlistSong: Partial<PlaylistSong> = await dto.toEntity()
    const { data, error } = await playlistSongService.update(id, playlistSong)
    if (error) {
      message.value = error
    }
    return data
  }

  // API Requests
  /** Load all playlistSongs from the API */
  const loadPlaylistSongs = async (
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
    } = await playlistSongService.getAll(pagination, sortFields)
    handleResponse(data, err, paginationresult)

    if (unpagined && playlistSongs.value.length > 0) {
      while (paginationData.value.hasNext()) {
        paginationData.value.nextPage()
        const {
          data: nextData,
          error: nextErr,
          pagination: nextPagination,
        } = await playlistSongService.getAll(paginationData.value.toParameter(), sortFields)
        handleResponse(nextData, nextErr, nextPagination, true)
      }
    }
    stopLoading()
  }

  /**
   * Get a single playlistSong by id
   * @param id PlaylistSong ID
   */
  const getPlaylistSongById = async (id: number | string) => {
    const result = await playlistSongService.getById(id)
    if (result.error) {
      message.value = result.error
    }
    return result
  }

  /**
   * Search playlistSongs with filters, pagination, and sorting
   * @param filters Partial PlaylistSong object containing search filters
   * @param pagination Pagination request parameters
   * @param sortFields Array of fields to sort
   */
  const searchPlaylistSongs = async (
    unpagined: boolean = false,
    filters: Partial<PlaylistSong>,
    pagination: PaginationRequestParameter,
    sortFields: SortFieldParameter[],
  ) => {
    message.value = null
    startLoading()
    const {
      data,
      error: err,
      pagination: paginationresult,
    } = await playlistSongService.search(filters, pagination, sortFields)
    handleResponse(data, err, paginationresult)

    if (unpagined) {
      while (paginationData.value.hasNext()) {
        paginationData.value.nextPage()
        const {
          data: nextData,
          error: nextErr,
          pagination: nextPagination,
        } = await playlistSongService.search(
          filters,
          paginationData.value.toParameter(),
          sortFields,
        )
        handleResponse(nextData, nextErr, nextPagination, true)
      }
    }
    stopLoading()
  }
  const exportPlaylistSongsToCsv = async (data: Partial<PlaylistSong[]>) => {
    console.log('Exporting PlaylistSong to CSV', data)
    await playlistSongService.exportCsv(data)
  }

  const getAllPlaylistSongsByPlaylistId = async (
    playlistId: number | string | undefined,
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
    } = await playlistSongService.getAllByPlaylistId(playlistId, pagination, sortFields)
    handleResponse(data, err, paginationresult)

    if (unpagined) {
      while (paginationData.value.hasNext() && playlistSongs.value.length > 0) {
        paginationData.value.nextPage()
        const {
          data: nextData,
          error: nextErr,
          pagination: nextPagination,
        } = await playlistSongService.getAllByPlaylistId(
          playlistId,
          paginationData.value.toParameter(),
          sortFields,
        )
        handleResponse(nextData, nextErr, nextPagination, true)
      }
    }
    stopLoading()
  }
  const getAllPlaylistSongsBySongId = async (
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
    } = await playlistSongService.getAllBySongId(songId, pagination, sortFields)
    handleResponse(data, err, paginationresult)

    if (unpagined) {
      while (paginationData.value.hasNext() && playlistSongs.value.length > 0) {
        paginationData.value.nextPage()
        const {
          data: nextData,
          error: nextErr,
          pagination: nextPagination,
        } = await playlistSongService.getAllBySongId(
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
    playlistSongs,
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
    exportPlaylistSongsToCsv,
    loadPlaylistSongs,
    searchPlaylistSongs,
    getPlaylistSongById,
    viewPlaylistSong,
    goToListView,
    goToCreateFormView,
    goToUpdateFormView,
    deletePlaylistSong,
    createPlaylistSong,
    updatePlaylistSong,
    getAllPlaylistSongsByPlaylistId,
    getAllPlaylistSongsBySongId,
  }
}
