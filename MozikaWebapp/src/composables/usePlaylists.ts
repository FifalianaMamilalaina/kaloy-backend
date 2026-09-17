import { ref, watch } from 'vue'
import { Playlist, PlaylistFormDTO } from '@/models/PlaylistModel'
import * as playlistService from '@/services/PlaylistService'
import * as columnConfigService from '@/services/ColumnConfigService'
import { useLoading } from './useLoading'
import { PaginationRequestParameter, SortFieldParameter } from '@/models/api/RequestModel'
import { PaginationData } from '@/models/api/PageResponseModel'
import { useRouter } from 'vue-router'
import { Download, DownloadFormDTO } from '@/models/DownloadModel'
import { PlaylistSong, PlaylistSongFormDTO } from '@/models/PlaylistSongModel'

export const DEFAULT_LIST_VIEW_FIELDS = ['id']

/**
 * Composable to manage Playlist entities including CRUD operations,
 * search, pagination, foreign key loading, and navigation.
 */
export function usePlaylists() {
  /** List of all playlists */
  const playlists = ref<Playlist[]>([])

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
  const savedLayoutMode = localStorage.getItem('layoutMode_playlist') as 'list' | 'card' | null
  const layoutMode = ref<'list' | 'card'>(savedLayoutMode || 'list')

  watch(layoutMode, (newValue) => {
    localStorage.setItem('layoutMode_playlist', newValue)
  })

  /**
   * Handles API response by updating the playlists and pagination
   * @param data Array of Playlist returned by the API
   * @param err Optional error message
   * @param pagination Optional pagination data
   */
  const handleResponse = (
    data: Playlist[],
    err: string | undefined,
    pagination?: PaginationData,
    concatData: boolean = false,
  ) => {
    if (err) {
      message.value = err
    }
    if (concatData) {
      playlists.value = playlists.value.concat(data)
    } else {
      playlists.value = data
    }
    if (pagination !== undefined) {
      paginationData.value = new PaginationData(pagination)
    }
  }

  const loadColumnConfig = async () => {
    try {
      const response = await columnConfigService.getVisibleFields('playlist', 'list')
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
      const response = await columnConfigService.updateVisibleFields('playlist', 'list', fields)
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
  /** Navigate to playlist detail view */
  const viewPlaylist = (playlist: Playlist) => {
    router.push({ name: 'playlistdetailsview', params: { id: playlist.getKeyValue() } })
  }

  /** Navigate to playlist list view */
  const goToListView = () => {
    router.push({ path: '/playlists' })
  }

  /** Navigate to playlist create form */
  const goToCreateFormView = () => {
    router.push({ path: '/playlists/create' })
  }

  /** Navigate to playlist update form */
  const goToUpdateFormView = (playlist: Playlist) => {
    router.push({ name: 'playlistupdateview', params: { id: playlist.getKeyValue() } })
  }

  /**
   * Delete a playlist by id
   * @param playlist Playlist object to delete
   */
  const deletePlaylist = async (playlist: Playlist) => {
    const result = await playlistService.remove(playlist.getKeyValue())
    if (result.error) {
      message.value = result.error
    }
    return result
  }

  /**
   * Create a new playlist
   * @param fullData Object containing playlist and his details
   */
  const createPlaylist = async (fullData: {
    playlist: Partial<PlaylistFormDTO>
    downloads: DownloadFormDTO[]
    playlistSongs: PlaylistSongFormDTO[]
  }) => {
    const dto = new PlaylistFormDTO(fullData.playlist)
    const playlist: Partial<Playlist> = await dto.toEntity()
    const downloads: Partial<Download>[] = await DownloadFormDTO.toEntities(fullData.downloads)
    const playlistSongs: Partial<PlaylistSong>[] = await PlaylistSongFormDTO.toEntities(
      fullData.playlistSongs,
    )

    const { data, error, errors } = await playlistService.create({
      playlist,
      downloads,
      playlistSongs,
    })
    if (error) {
      message.value = error
    }
    return { data, errors }
  }

  /**
   * Update an existing playlist with full details
   * @param id Playlist ID to update
   * @param fullData Object containing playlist and its details
   */
  const updatePlaylist = async (
    id: number | string,
    fullData: {
      playlist: Partial<PlaylistFormDTO>
      downloads: DownloadFormDTO[]
      playlistSongs: PlaylistSongFormDTO[]
    },
  ) => {
    const dto = new PlaylistFormDTO(fullData.playlist)
    const playlist: Partial<Playlist> = await dto.toEntity()

    const downloads: Partial<Download>[] = await DownloadFormDTO.toEntities(fullData.downloads)
    const playlistSongs: Partial<PlaylistSong>[] = await PlaylistSongFormDTO.toEntities(
      fullData.playlistSongs,
    )

    const { data, error, errors } = await playlistService.update(id, {
      playlist,
      downloads,
      playlistSongs,
    })
    if (error) {
      message.value = error
    }
    return { data, errors }
  }

  // API Requests
  /** Load all playlists from the API */
  const loadPlaylists = async (
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
    } = await playlistService.getAll(pagination, sortFields)
    handleResponse(data, err, paginationresult)

    if (unpagined && playlists.value.length > 0) {
      while (paginationData.value.hasNext()) {
        paginationData.value.nextPage()
        const {
          data: nextData,
          error: nextErr,
          pagination: nextPagination,
        } = await playlistService.getAll(paginationData.value.toParameter(), sortFields)
        handleResponse(nextData, nextErr, nextPagination, true)
      }
    }
    stopLoading()
  }

  /**
   * Get a single playlist by id
   * @param id Playlist ID
   */
  const getPlaylistById = async (id: number | string) => {
    const result = await playlistService.getById(id)
    if (result.error) {
      message.value = result.error
    }
    return result
  }

  /**
   * Search playlists with filters, pagination, and sorting
   * @param filters Partial Playlist object containing search filters
   * @param pagination Pagination request parameters
   * @param sortFields Array of fields to sort
   */
  const searchPlaylists = async (
    unpagined: boolean = false,
    filters: Partial<Playlist>,
    pagination: PaginationRequestParameter,
    sortFields: SortFieldParameter[],
  ) => {
    message.value = null
    startLoading()
    const {
      data,
      error: err,
      pagination: paginationresult,
    } = await playlistService.search(filters, pagination, sortFields)
    handleResponse(data, err, paginationresult)

    if (unpagined) {
      while (paginationData.value.hasNext()) {
        paginationData.value.nextPage()
        const {
          data: nextData,
          error: nextErr,
          pagination: nextPagination,
        } = await playlistService.search(filters, paginationData.value.toParameter(), sortFields)
        handleResponse(nextData, nextErr, nextPagination, true)
      }
    }
    stopLoading()
  }
  const exportPlaylistsToCsv = async (data: Partial<Playlist[]>) => {
    console.log('Exporting Playlist to CSV', data)
    await playlistService.exportCsv(data)
  }

  const getAllPlaylistsByUserId = async (
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
    } = await playlistService.getAllByUserId(userId, pagination, sortFields)
    handleResponse(data, err, paginationresult)

    if (unpagined) {
      while (paginationData.value.hasNext() && playlists.value.length > 0) {
        paginationData.value.nextPage()
        const {
          data: nextData,
          error: nextErr,
          pagination: nextPagination,
        } = await playlistService.getAllByUserId(
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
    playlists,
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
    exportPlaylistsToCsv,
    loadPlaylists,
    searchPlaylists,
    getPlaylistById,
    viewPlaylist,
    goToListView,
    goToCreateFormView,
    goToUpdateFormView,
    deletePlaylist,
    createPlaylist,
    updatePlaylist,
    getAllPlaylistsByUserId,
  }
}
