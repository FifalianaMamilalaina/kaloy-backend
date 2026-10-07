import { ref, watch } from 'vue'
import { Album, AlbumFormDTO } from '@/models/AlbumModel'
import * as albumService from '@/services/AlbumService'
import * as columnConfigService from '@/services/ColumnConfigService'
import { useLoading } from './useLoading'
import { PaginationRequestParameter, SortFieldParameter } from '@/models/api/RequestModel'
import { PaginationData } from '@/models/api/PageResponseModel'
import { useRouter } from 'vue-router'
import { Song, SongFormDTO } from '@/models/SongModel'

export const DEFAULT_LIST_VIEW_FIELDS = ['id']

/**
 * Composable to manage Album entities including CRUD operations,
 * search, pagination, foreign key loading, and navigation.
 */
export function useAlbums() {
  /** List of all albums */
  const albums = ref<Album[]>([])

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
  const savedLayoutMode = localStorage.getItem('layoutMode_album') as 'list' | 'card' | null
  const layoutMode = ref<'list' | 'card'>(savedLayoutMode || 'list')

  watch(layoutMode, (newValue) => {
    localStorage.setItem('layoutMode_album', newValue)
  })

  /**
   * Handles API response by updating the albums and pagination
   * @param data Array of Album returned by the API
   * @param err Optional error message
   * @param pagination Optional pagination data
   */
  const handleResponse = (
    data: Album[],
    err: string | undefined,
    pagination?: PaginationData,
    concatData: boolean = false,
  ) => {
    if (err) {
      message.value = err
    }
    if (concatData) {
      albums.value = albums.value.concat(data)
    } else {
      albums.value = data
    }
    if (pagination !== undefined) {
      paginationData.value = new PaginationData(pagination)
    }
  }

  const loadColumnConfig = async () => {
    try {
      const response = await columnConfigService.getVisibleFields('album', 'list')
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
      const response = await columnConfigService.updateVisibleFields('album', 'list', fields)
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
  /** Navigate to album detail view */
  const viewAlbum = (album: Album) => {
    router.push({ name: 'albumdetailsview', params: { id: album.getKeyValue() } })
  }

  /** Navigate to album list view */
  const goToListView = () => {
    router.push({ path: '/albums' })
  }

  /** Navigate to album create form */
  const goToCreateFormView = () => {
    router.push({ path: '/albums/create' })
  }

  /** Navigate to album update form */
  const goToUpdateFormView = (album: Album) => {
    router.push({ name: 'albumupdateview', params: { id: album.getKeyValue() } })
  }

  /**
   * Delete a album by id
   * @param album Album object to delete
   */
  const deleteAlbum = async (album: Album) => {
    const result = await albumService.remove(album.getKeyValue())
    if (result.error) {
      message.value = result.error
    }
    return result
  }

  /**
   * Create a new album
   * @param fullData Object containing album and his details
   */
  const createAlbum = async (fullData: { album: Partial<AlbumFormDTO>; songs: SongFormDTO[] }) => {
    const dto = new AlbumFormDTO(fullData.album)
    const album: Partial<Album> = await dto.toEntity()
    const songs: Partial<Song>[] = await SongFormDTO.toEntities(fullData.songs)

    const { data, error, errors } = await albumService.create({ album, songs })
    if (error) {
      message.value = error
    }
    return { data, errors }
  }

  /**
   * Update an existing album with full details
   * @param id Album ID to update
   * @param fullData Object containing album and its details
   */
  const updateAlbum = async (
    id: number | string,
    fullData: {
      album: Partial<AlbumFormDTO>
      songs: SongFormDTO[]
    },
  ) => {
    const dto = new AlbumFormDTO(fullData.album)
    const album: Partial<Album> = await dto.toEntity()

    const songs: Partial<Song>[] = await SongFormDTO.toEntities(fullData.songs)

    const { data, error, errors } = await albumService.update(id, {
      album,
      songs,
    })
    if (error) {
      message.value = error
    }
    return { data, errors }
  }

  // API Requests
  /** Load all albums from the API */
  const loadAlbums = async (
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
    } = await albumService.getAll(pagination, sortFields)
    handleResponse(data, err, paginationresult)

    if (unpagined && albums.value.length > 0) {
      while (paginationData.value.hasNext()) {
        paginationData.value.nextPage()
        const {
          data: nextData,
          error: nextErr,
          pagination: nextPagination,
        } = await albumService.getAll(paginationData.value.toParameter(), sortFields)
        handleResponse(nextData, nextErr, nextPagination, true)
      }
    }
    stopLoading()
  }

  /**
   * Get a single album by id
   * @param id Album ID
   */
  const getAlbumById = async (id: number | string) => {
    const result = await albumService.getById(id)
    if (result.error) {
      message.value = result.error
    }
    return result
  }

  /**
   * Search albums with filters, pagination, and sorting
   * @param filters Partial Album object containing search filters
   * @param pagination Pagination request parameters
   * @param sortFields Array of fields to sort
   */
  const searchAlbums = async (
    unpagined: boolean = false,
    filters: Partial<Album>,
    pagination: PaginationRequestParameter,
    sortFields: SortFieldParameter[],
  ) => {
    message.value = null
    startLoading()
    const {
      data,
      error: err,
      pagination: paginationresult,
    } = await albumService.search(filters, pagination, sortFields)
    handleResponse(data, err, paginationresult)

    if (unpagined) {
      while (paginationData.value.hasNext()) {
        paginationData.value.nextPage()
        const {
          data: nextData,
          error: nextErr,
          pagination: nextPagination,
        } = await albumService.search(filters, paginationData.value.toParameter(), sortFields)
        handleResponse(nextData, nextErr, nextPagination, true)
      }
    }
    stopLoading()
  }
  const exportAlbumsToCsv = async (data: Partial<Album[]>) => {
    console.log('Exporting Album to CSV', data)
    await albumService.exportCsv(data)
  }

  const getAllAlbumsByArtistId = async (
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
    } = await albumService.getAllByArtistId(artistId, pagination, sortFields)
    handleResponse(data, err, paginationresult)

    if (unpagined) {
      while (paginationData.value.hasNext() && albums.value.length > 0) {
        paginationData.value.nextPage()
        const {
          data: nextData,
          error: nextErr,
          pagination: nextPagination,
        } = await albumService.getAllByArtistId(
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
    albums,
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
    exportAlbumsToCsv,
    loadAlbums,
    searchAlbums,
    getAlbumById,
    viewAlbum,
    goToListView,
    goToCreateFormView,
    goToUpdateFormView,
    deleteAlbum,
    createAlbum,
    updateAlbum,
    getAllAlbumsByArtistId,
  }
}
