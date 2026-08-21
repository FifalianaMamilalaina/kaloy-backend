import { ref, watch } from 'vue'
import { Song, SongFormDTO } from '@/models/SongModel'
import * as songService from '@/services/SongService'
import * as columnConfigService from '@/services/ColumnConfigService'
import { useLoading } from './useLoading'
import { PaginationRequestParameter, SortFieldParameter } from '@/models/api/RequestModel'
import { PaginationData } from '@/models/api/PageResponseModel'
import { useRouter } from 'vue-router'
import {
  EditorialPlaylistSong,
  EditorialPlaylistSongFormDTO,
} from '@/models/EditorialPlaylistSongModel'
import { ListeningHistory, ListeningHistoryFormDTO } from '@/models/ListeningHistoryModel'
import { PlaylistSong, PlaylistSongFormDTO } from '@/models/PlaylistSongModel'
import { SongGenre, SongGenreFormDTO } from '@/models/SongGenreModel'
import { UpNextQueue, UpNextQueueFormDTO } from '@/models/UpNextQueueModel'

export const DEFAULT_LIST_VIEW_FIELDS = ['id']

/**
 * Composable to manage Song entities including CRUD operations,
 * search, pagination, foreign key loading, and navigation.
 */
export function useSongs() {
  /** List of all songs */
  const songs = ref<Song[]>([])

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
  const savedLayoutMode = localStorage.getItem('layoutMode_song') as 'list' | 'card' | null
  const layoutMode = ref<'list' | 'card'>(savedLayoutMode || 'list')

  watch(layoutMode, (newValue) => {
    localStorage.setItem('layoutMode_song', newValue)
  })

  /**
   * Handles API response by updating the songs and pagination
   * @param data Array of Song returned by the API
   * @param err Optional error message
   * @param pagination Optional pagination data
   */
  const handleResponse = (
    data: Song[],
    err: string | undefined,
    pagination?: PaginationData,
    concatData: boolean = false,
  ) => {
    if (err) {
      message.value = err
    }
    if (concatData) {
      songs.value = songs.value.concat(data)
    } else {
      songs.value = data
    }
    if (pagination !== undefined) {
      paginationData.value = new PaginationData(pagination)
    }
  }

  const loadColumnConfig = async () => {
    try {
      const response = await columnConfigService.getVisibleFields('song', 'list')
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
      const response = await columnConfigService.updateVisibleFields('song', 'list', fields)
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
  /** Navigate to song detail view */
  const viewSong = (song: Song) => {
    router.push({ name: 'songdetailsview', params: { id: song.getKeyValue() } })
  }

  /** Navigate to song list view */
  const goToListView = () => {
    router.push({ path: '/songs' })
  }

  /** Navigate to song create form */
  const goToCreateFormView = () => {
    router.push({ path: '/songs/create' })
  }

  /** Navigate to song update form */
  const goToUpdateFormView = (song: Song) => {
    router.push({ name: 'songupdateview', params: { id: song.getKeyValue() } })
  }

  /**
   * Delete a song by id
   * @param song Song object to delete
   */
  const deleteSong = async (song: Song) => {
    const result = await songService.remove(song.getKeyValue())
    if (result.error) {
      message.value = result.error
    }
    return result
  }

  /**
   * Create a new song
   * @param fullData Object containing song and his details
   */
  const createSong = async (fullData: {
    song: Partial<SongFormDTO>
    editorialPlaylistSongs: EditorialPlaylistSongFormDTO[]
    listeningHistorys: ListeningHistoryFormDTO[]
    playlistSongs: PlaylistSongFormDTO[]
    songGenres: SongGenreFormDTO[]
    upNextQueues: UpNextQueueFormDTO[]
  }) => {
    const dto = new SongFormDTO(fullData.song)
    const song: Partial<Song> = await dto.toEntity()
    const editorialPlaylistSongs: Partial<EditorialPlaylistSong>[] =
      await EditorialPlaylistSongFormDTO.toEntities(fullData.editorialPlaylistSongs)
    const listeningHistorys: Partial<ListeningHistory>[] = await ListeningHistoryFormDTO.toEntities(
      fullData.listeningHistorys,
    )
    const playlistSongs: Partial<PlaylistSong>[] = await PlaylistSongFormDTO.toEntities(
      fullData.playlistSongs,
    )
    const songGenres: Partial<SongGenre>[] = await SongGenreFormDTO.toEntities(fullData.songGenres)
    const upNextQueues: Partial<UpNextQueue>[] = await UpNextQueueFormDTO.toEntities(
      fullData.upNextQueues,
    )

    const { data, error, errors } = await songService.create({
      song,
      editorialPlaylistSongs,
      listeningHistorys,
      playlistSongs,
      songGenres,
      upNextQueues,
    })
    if (error) {
      message.value = error
    }
    return { data, errors }
  }

  /**
   * Update an existing song with full details
   * @param id Song ID to update
   * @param fullData Object containing song and its details
   */
  const updateSong = async (
    id: number | string,
    fullData: {
      song: Partial<SongFormDTO>
      editorialPlaylistSongs: EditorialPlaylistSongFormDTO[]
      listeningHistorys: ListeningHistoryFormDTO[]
      playlistSongs: PlaylistSongFormDTO[]
      songGenres: SongGenreFormDTO[]
      upNextQueues: UpNextQueueFormDTO[]
    },
  ) => {
    const dto = new SongFormDTO(fullData.song)
    const song: Partial<Song> = await dto.toEntity()

    const editorialPlaylistSongs: Partial<EditorialPlaylistSong>[] =
      await EditorialPlaylistSongFormDTO.toEntities(fullData.editorialPlaylistSongs)
    const listeningHistorys: Partial<ListeningHistory>[] = await ListeningHistoryFormDTO.toEntities(
      fullData.listeningHistorys,
    )
    const playlistSongs: Partial<PlaylistSong>[] = await PlaylistSongFormDTO.toEntities(
      fullData.playlistSongs,
    )
    const songGenres: Partial<SongGenre>[] = await SongGenreFormDTO.toEntities(fullData.songGenres)
    const upNextQueues: Partial<UpNextQueue>[] = await UpNextQueueFormDTO.toEntities(
      fullData.upNextQueues,
    )

    const { data, error, errors } = await songService.update(id, {
      song,
      editorialPlaylistSongs,
      listeningHistorys,
      playlistSongs,
      songGenres,
      upNextQueues,
    })
    if (error) {
      message.value = error
    }
    return { data, errors }
  }

  // API Requests
  /** Load all songs from the API */
  const loadSongs = async (
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
    } = await songService.getAll(pagination, sortFields)
    handleResponse(data, err, paginationresult)

    if (unpagined && songs.value.length > 0) {
      while (paginationData.value.hasNext()) {
        paginationData.value.nextPage()
        const {
          data: nextData,
          error: nextErr,
          pagination: nextPagination,
        } = await songService.getAll(paginationData.value.toParameter(), sortFields)
        handleResponse(nextData, nextErr, nextPagination, true)
      }
    }
    stopLoading()
  }

  /**
   * Get a single song by id
   * @param id Song ID
   */
  const getSongById = async (id: number | string) => {
    const result = await songService.getById(id)
    if (result.error) {
      message.value = result.error
    }
    return result
  }

  /**
   * Search songs with filters, pagination, and sorting
   * @param filters Partial Song object containing search filters
   * @param pagination Pagination request parameters
   * @param sortFields Array of fields to sort
   */
  const searchSongs = async (
    unpagined: boolean = false,
    filters: Partial<Song>,
    pagination: PaginationRequestParameter,
    sortFields: SortFieldParameter[],
  ) => {
    message.value = null
    startLoading()
    const {
      data,
      error: err,
      pagination: paginationresult,
    } = await songService.search(filters, pagination, sortFields)
    handleResponse(data, err, paginationresult)

    if (unpagined) {
      while (paginationData.value.hasNext()) {
        paginationData.value.nextPage()
        const {
          data: nextData,
          error: nextErr,
          pagination: nextPagination,
        } = await songService.search(filters, paginationData.value.toParameter(), sortFields)
        handleResponse(nextData, nextErr, nextPagination, true)
      }
    }
    stopLoading()
  }
  const exportSongsToCsv = async (data: Partial<Song[]>) => {
    console.log('Exporting Song to CSV', data)
    await songService.exportCsv(data)
  }

  const getAllSongsByArtistId = async (
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
    } = await songService.getAllByArtistId(artistId, pagination, sortFields)
    handleResponse(data, err, paginationresult)

    if (unpagined) {
      while (paginationData.value.hasNext() && songs.value.length > 0) {
        paginationData.value.nextPage()
        const {
          data: nextData,
          error: nextErr,
          pagination: nextPagination,
        } = await songService.getAllByArtistId(
          artistId,
          paginationData.value.toParameter(),
          sortFields,
        )
        handleResponse(nextData, nextErr, nextPagination, true)
      }
    }
    stopLoading()
  }
  const getAllSongsByAlbumId = async (
    albumId: number | string | undefined,
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
    } = await songService.getAllByAlbumId(albumId, pagination, sortFields)
    handleResponse(data, err, paginationresult)

    if (unpagined) {
      while (paginationData.value.hasNext() && songs.value.length > 0) {
        paginationData.value.nextPage()
        const {
          data: nextData,
          error: nextErr,
          pagination: nextPagination,
        } = await songService.getAllByAlbumId(
          albumId,
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
    songs,
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
    exportSongsToCsv,
    loadSongs,
    searchSongs,
    getSongById,
    viewSong,
    goToListView,
    goToCreateFormView,
    goToUpdateFormView,
    deleteSong,
    createSong,
    updateSong,
    getAllSongsByArtistId,
    getAllSongsByAlbumId,
  }
}
