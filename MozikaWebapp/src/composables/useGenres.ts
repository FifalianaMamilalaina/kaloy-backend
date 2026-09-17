import { ref, watch } from 'vue'
import { Genre, GenreFormDTO } from '@/models/GenreModel'
import * as genreService from '@/services/GenreService'
import * as columnConfigService from '@/services/ColumnConfigService'
import { useLoading } from './useLoading'
import { PaginationRequestParameter, SortFieldParameter } from '@/models/api/RequestModel'
import { PaginationData } from '@/models/api/PageResponseModel'
import { useRouter } from 'vue-router'
import { SongGenre, SongGenreFormDTO } from '@/models/SongGenreModel'

export const DEFAULT_LIST_VIEW_FIELDS = ['id']

/**
 * Composable to manage Genre entities including CRUD operations,
 * search, pagination, foreign key loading, and navigation.
 */
export function useGenres() {
  /** List of all genres */
  const genres = ref<Genre[]>([])

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
  const savedLayoutMode = localStorage.getItem('layoutMode_genre') as 'list' | 'card' | null
  const layoutMode = ref<'list' | 'card'>(savedLayoutMode || 'list')

  watch(layoutMode, (newValue) => {
    localStorage.setItem('layoutMode_genre', newValue)
  })

  /**
   * Handles API response by updating the genres and pagination
   * @param data Array of Genre returned by the API
   * @param err Optional error message
   * @param pagination Optional pagination data
   */
  const handleResponse = (
    data: Genre[],
    err: string | undefined,
    pagination?: PaginationData,
    concatData: boolean = false,
  ) => {
    if (err) {
      message.value = err
    }
    if (concatData) {
      genres.value = genres.value.concat(data)
    } else {
      genres.value = data
    }
    if (pagination !== undefined) {
      paginationData.value = new PaginationData(pagination)
    }
  }

  const loadColumnConfig = async () => {
    try {
      const response = await columnConfigService.getVisibleFields('genre', 'list')
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
      const response = await columnConfigService.updateVisibleFields('genre', 'list', fields)
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
  /** Navigate to genre detail view */
  const viewGenre = (genre: Genre) => {
    router.push({ name: 'genredetailsview', params: { id: genre.getKeyValue() } })
  }

  /** Navigate to genre list view */
  const goToListView = () => {
    router.push({ path: '/genres' })
  }

  /** Navigate to genre create form */
  const goToCreateFormView = () => {
    router.push({ path: '/genres/create' })
  }

  /** Navigate to genre update form */
  const goToUpdateFormView = (genre: Genre) => {
    router.push({ name: 'genreupdateview', params: { id: genre.getKeyValue() } })
  }

  /**
   * Delete a genre by id
   * @param genre Genre object to delete
   */
  const deleteGenre = async (genre: Genre) => {
    const result = await genreService.remove(genre.getKeyValue())
    if (result.error) {
      message.value = result.error
    }
    return result
  }

  /**
   * Create a new genre
   * @param fullData Object containing genre and his details
   */
  const createGenre = async (fullData: {
    genre: Partial<GenreFormDTO>
    songGenres: SongGenreFormDTO[]
  }) => {
    const dto = new GenreFormDTO(fullData.genre)
    const genre: Partial<Genre> = await dto.toEntity()
    const songGenres: Partial<SongGenre>[] = await SongGenreFormDTO.toEntities(fullData.songGenres)

    const { data, error, errors } = await genreService.create({ genre, songGenres })
    if (error) {
      message.value = error
    }
    return { data, errors }
  }

  /**
   * Update an existing genre with full details
   * @param id Genre ID to update
   * @param fullData Object containing genre and its details
   */
  const updateGenre = async (
    id: number | string,
    fullData: {
      genre: Partial<GenreFormDTO>
      songGenres: SongGenreFormDTO[]
    },
  ) => {
    const dto = new GenreFormDTO(fullData.genre)
    const genre: Partial<Genre> = await dto.toEntity()

    const songGenres: Partial<SongGenre>[] = await SongGenreFormDTO.toEntities(fullData.songGenres)

    const { data, error, errors } = await genreService.update(id, {
      genre,
      songGenres,
    })
    if (error) {
      message.value = error
    }
    return { data, errors }
  }

  // API Requests
  /** Load all genres from the API */
  const loadGenres = async (
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
    } = await genreService.getAll(pagination, sortFields)
    handleResponse(data, err, paginationresult)

    if (unpagined && genres.value.length > 0) {
      while (paginationData.value.hasNext()) {
        paginationData.value.nextPage()
        const {
          data: nextData,
          error: nextErr,
          pagination: nextPagination,
        } = await genreService.getAll(paginationData.value.toParameter(), sortFields)
        handleResponse(nextData, nextErr, nextPagination, true)
      }
    }
    stopLoading()
  }

  /**
   * Get a single genre by id
   * @param id Genre ID
   */
  const getGenreById = async (id: number | string) => {
    const result = await genreService.getById(id)
    if (result.error) {
      message.value = result.error
    }
    return result
  }

  /**
   * Search genres with filters, pagination, and sorting
   * @param filters Partial Genre object containing search filters
   * @param pagination Pagination request parameters
   * @param sortFields Array of fields to sort
   */
  const searchGenres = async (
    unpagined: boolean = false,
    filters: Partial<Genre>,
    pagination: PaginationRequestParameter,
    sortFields: SortFieldParameter[],
  ) => {
    message.value = null
    startLoading()
    const {
      data,
      error: err,
      pagination: paginationresult,
    } = await genreService.search(filters, pagination, sortFields)
    handleResponse(data, err, paginationresult)

    if (unpagined) {
      while (paginationData.value.hasNext()) {
        paginationData.value.nextPage()
        const {
          data: nextData,
          error: nextErr,
          pagination: nextPagination,
        } = await genreService.search(filters, paginationData.value.toParameter(), sortFields)
        handleResponse(nextData, nextErr, nextPagination, true)
      }
    }
    stopLoading()
  }
  const exportGenresToCsv = async (data: Partial<Genre[]>) => {
    console.log('Exporting Genre to CSV', data)
    await genreService.exportCsv(data)
  }

  /** Getter for pagination data */
  const getPaginationData = () => paginationData.value

  return {
    // state
    genres,
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
    exportGenresToCsv,
    loadGenres,
    searchGenres,
    getGenreById,
    viewGenre,
    goToListView,
    goToCreateFormView,
    goToUpdateFormView,
    deleteGenre,
    createGenre,
    updateGenre,
  }
}
