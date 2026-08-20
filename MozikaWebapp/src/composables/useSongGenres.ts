import { ref, watch } from 'vue'
import { SongGenre, SongGenreFormDTO } from '@/models/SongGenreModel'
import * as songGenreService from '@/services/SongGenreService'
import * as columnConfigService from '@/services/ColumnConfigService'
import { useLoading } from './useLoading'
import { PaginationRequestParameter, SortFieldParameter } from '@/models/api/RequestModel'
import { PaginationData } from '@/models/api/PageResponseModel'
import { useRouter } from 'vue-router'

export const DEFAULT_LIST_VIEW_FIELDS = ['id']

/**
 * Composable to manage SongGenre entities including CRUD operations,
 * search, pagination, foreign key loading, and navigation.
 */
export function useSongGenres() {
  /** List of all songGenres */
  const songGenres = ref<SongGenre[]>([])

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
  const savedLayoutMode = localStorage.getItem('layoutMode_songGenre') as 'list' | 'card' | null
  const layoutMode = ref<'list' | 'card'>(savedLayoutMode || 'list')

  watch(layoutMode, (newValue) => {
    localStorage.setItem('layoutMode_songGenre', newValue)
  })

  /**
   * Handles API response by updating the songGenres and pagination
   * @param data Array of SongGenre returned by the API
   * @param err Optional error message
   * @param pagination Optional pagination data
   */
  const handleResponse = (
    data: SongGenre[],
    err: string | undefined,
    pagination?: PaginationData,
    concatData: boolean = false,
  ) => {
    if (err) {
      message.value = err
    }
    if (concatData) {
      songGenres.value = songGenres.value.concat(data)
    } else {
      songGenres.value = data
    }
    if (pagination !== undefined) {
      paginationData.value = new PaginationData(pagination)
    }
  }

  const loadColumnConfig = async () => {
    try {
      const response = await columnConfigService.getVisibleFields('songGenre', 'list')
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
      const response = await columnConfigService.updateVisibleFields('songGenre', 'list', fields)
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
  /** Navigate to songGenre detail view */
  const viewSongGenre = (songGenre: SongGenre) => {
    router.push({ name: 'songGenredetailsview', params: { id: songGenre.getKeyValue() } })
  }

  /** Navigate to songGenre list view */
  const goToListView = () => {
    router.push({ path: '/songGenres' })
  }

  /** Navigate to songGenre create form */
  const goToCreateFormView = () => {
    router.push({ path: '/songGenres/create' })
  }

  /** Navigate to songGenre update form */
  const goToUpdateFormView = (songGenre: SongGenre) => {
    router.push({ name: 'songGenreupdateview', params: { id: songGenre.getKeyValue() } })
  }

  /**
   * Delete a songGenre by id
   * @param songGenre SongGenre object to delete
   */
  const deleteSongGenre = async (songGenre: SongGenre) => {
    const result = await songGenreService.remove(songGenre.getKeyValue())
    if (result.error) {
      message.value = result.error
    }
    return result
  }

  /**
   * Create a new songGenre
   * @param songGenreFormDTO SongGenre object to create
   */
  const createSongGenre = async (songGenreFormDTO: Partial<SongGenreFormDTO>) => {
    const dto = new SongGenreFormDTO(songGenreFormDTO)
    const songGenre: Partial<SongGenre> = await dto.toEntity()
    const { data, error, errors } = await songGenreService.create(songGenre)
    if (error) {
      message.value = error
    }
    return { data, errors }
  }

  /**
   * Update an existing songGenre
   * @param songGenreFormDTO SongGenre object to update
   */
  const updateSongGenre = async (
    id: number | string,
    songGenreFormDTO: Partial<SongGenreFormDTO>,
  ) => {
    const dto = new SongGenreFormDTO(songGenreFormDTO)
    const songGenre: Partial<SongGenre> = await dto.toEntity()
    const { data, error } = await songGenreService.update(id, songGenre)
    if (error) {
      message.value = error
    }
    return data
  }

  // API Requests
  /** Load all songGenres from the API */
  const loadSongGenres = async (
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
    } = await songGenreService.getAll(pagination, sortFields)
    handleResponse(data, err, paginationresult)

    if (unpagined && songGenres.value.length > 0) {
      while (paginationData.value.hasNext()) {
        paginationData.value.nextPage()
        const {
          data: nextData,
          error: nextErr,
          pagination: nextPagination,
        } = await songGenreService.getAll(paginationData.value.toParameter(), sortFields)
        handleResponse(nextData, nextErr, nextPagination, true)
      }
    }
    stopLoading()
  }

  /**
   * Get a single songGenre by id
   * @param id SongGenre ID
   */
  const getSongGenreById = async (id: number | string) => {
    const result = await songGenreService.getById(id)
    if (result.error) {
      message.value = result.error
    }
    return result
  }

  /**
   * Search songGenres with filters, pagination, and sorting
   * @param filters Partial SongGenre object containing search filters
   * @param pagination Pagination request parameters
   * @param sortFields Array of fields to sort
   */
  const searchSongGenres = async (
    unpagined: boolean = false,
    filters: Partial<SongGenre>,
    pagination: PaginationRequestParameter,
    sortFields: SortFieldParameter[],
  ) => {
    message.value = null
    startLoading()
    const {
      data,
      error: err,
      pagination: paginationresult,
    } = await songGenreService.search(filters, pagination, sortFields)
    handleResponse(data, err, paginationresult)

    if (unpagined) {
      while (paginationData.value.hasNext()) {
        paginationData.value.nextPage()
        const {
          data: nextData,
          error: nextErr,
          pagination: nextPagination,
        } = await songGenreService.search(filters, paginationData.value.toParameter(), sortFields)
        handleResponse(nextData, nextErr, nextPagination, true)
      }
    }
    stopLoading()
  }
  const exportSongGenresToCsv = async (data: Partial<SongGenre[]>) => {
    console.log('Exporting SongGenre to CSV', data)
    await songGenreService.exportCsv(data)
  }

  const getAllSongGenresBySongId = async (
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
    } = await songGenreService.getAllBySongId(songId, pagination, sortFields)
    handleResponse(data, err, paginationresult)

    if (unpagined) {
      while (paginationData.value.hasNext() && songGenres.value.length > 0) {
        paginationData.value.nextPage()
        const {
          data: nextData,
          error: nextErr,
          pagination: nextPagination,
        } = await songGenreService.getAllBySongId(
          songId,
          paginationData.value.toParameter(),
          sortFields,
        )
        handleResponse(nextData, nextErr, nextPagination, true)
      }
    }
    stopLoading()
  }
  const getAllSongGenresByGenreId = async (
    genreId: number | string | undefined,
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
    } = await songGenreService.getAllByGenreId(genreId, pagination, sortFields)
    handleResponse(data, err, paginationresult)

    if (unpagined) {
      while (paginationData.value.hasNext() && songGenres.value.length > 0) {
        paginationData.value.nextPage()
        const {
          data: nextData,
          error: nextErr,
          pagination: nextPagination,
        } = await songGenreService.getAllByGenreId(
          genreId,
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
    songGenres,
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
    exportSongGenresToCsv,
    loadSongGenres,
    searchSongGenres,
    getSongGenreById,
    viewSongGenre,
    goToListView,
    goToCreateFormView,
    goToUpdateFormView,
    deleteSongGenre,
    createSongGenre,
    updateSongGenre,
    getAllSongGenresBySongId,
    getAllSongGenresByGenreId,
  }
}
