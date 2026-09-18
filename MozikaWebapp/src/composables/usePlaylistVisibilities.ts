import { ref, watch } from 'vue'
import { PlaylistVisibilitie, PlaylistVisibilitieFormDTO } from '@/models/PlaylistVisibilitieModel'
import * as playlistVisibilitieService from '@/services/PlaylistVisibilitieService'
import * as columnConfigService from '@/services/ColumnConfigService'
import { useLoading } from './useLoading'
import { PaginationRequestParameter, SortFieldParameter } from '@/models/api/RequestModel'
import { PaginationData } from '@/models/api/PageResponseModel'
import { useRouter } from 'vue-router'

export const DEFAULT_LIST_VIEW_FIELDS = ['id']

/**
 * Composable to manage PlaylistVisibilitie entities including CRUD operations,
 * search, pagination, foreign key loading, and navigation.
 */
export function usePlaylistVisibilities() {
  /** List of all playlistVisibilities */
  const playlistVisibilities = ref<PlaylistVisibilitie[]>([])

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
  const savedLayoutMode = localStorage.getItem('layoutMode_playlistVisibilitie') as
    | 'list'
    | 'card'
    | null
  const layoutMode = ref<'list' | 'card'>(savedLayoutMode || 'list')

  watch(layoutMode, (newValue) => {
    localStorage.setItem('layoutMode_playlistVisibilitie', newValue)
  })

  /**
   * Handles API response by updating the playlistVisibilities and pagination
   * @param data Array of PlaylistVisibilitie returned by the API
   * @param err Optional error message
   * @param pagination Optional pagination data
   */
  const handleResponse = (
    data: PlaylistVisibilitie[],
    err: string | undefined,
    pagination?: PaginationData,
    concatData: boolean = false,
  ) => {
    if (err) {
      message.value = err
    }
    if (concatData) {
      playlistVisibilities.value = playlistVisibilities.value.concat(data)
    } else {
      playlistVisibilities.value = data
    }
    if (pagination !== undefined) {
      paginationData.value = new PaginationData(pagination)
    }
  }

  const loadColumnConfig = async () => {
    try {
      const response = await columnConfigService.getVisibleFields('playlistVisibilitie', 'list')
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
        'playlistVisibilitie',
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
  /** Navigate to playlistVisibilitie detail view */
  const viewPlaylistVisibilitie = (playlistVisibilitie: PlaylistVisibilitie) => {
    router.push({
      name: 'playlistVisibilitiedetailsview',
      params: { id: playlistVisibilitie.getKeyValue() },
    })
  }

  /** Navigate to playlistVisibilitie list view */
  const goToListView = () => {
    router.push({ path: '/playlistVisibilities' })
  }

  /** Navigate to playlistVisibilitie create form */
  const goToCreateFormView = () => {
    router.push({ path: '/playlistVisibilities/create' })
  }

  /** Navigate to playlistVisibilitie update form */
  const goToUpdateFormView = (playlistVisibilitie: PlaylistVisibilitie) => {
    router.push({
      name: 'playlistVisibilitieupdateview',
      params: { id: playlistVisibilitie.getKeyValue() },
    })
  }

  /**
   * Delete a playlistVisibilitie by id
   * @param playlistVisibilitie PlaylistVisibilitie object to delete
   */
  const deletePlaylistVisibilitie = async (playlistVisibilitie: PlaylistVisibilitie) => {
    const result = await playlistVisibilitieService.remove(playlistVisibilitie.getKeyValue())
    if (result.error) {
      message.value = result.error
    }
    return result
  }

  /**
   * Create a new playlistVisibilitie
   * @param playlistVisibilitieFormDTO PlaylistVisibilitie object to create
   */
  const createPlaylistVisibilitie = async (
    playlistVisibilitieFormDTO: Partial<PlaylistVisibilitieFormDTO>,
  ) => {
    const dto = new PlaylistVisibilitieFormDTO(playlistVisibilitieFormDTO)
    const playlistVisibilitie: Partial<PlaylistVisibilitie> = await dto.toEntity()
    const { data, error, errors } = await playlistVisibilitieService.create(playlistVisibilitie)
    if (error) {
      message.value = error
    }
    return { data, errors }
  }

  /**
   * Update an existing playlistVisibilitie
   * @param playlistVisibilitieFormDTO PlaylistVisibilitie object to update
   */
  const updatePlaylistVisibilitie = async (
    id: number | string,
    playlistVisibilitieFormDTO: Partial<PlaylistVisibilitieFormDTO>,
  ) => {
    const dto = new PlaylistVisibilitieFormDTO(playlistVisibilitieFormDTO)
    const playlistVisibilitie: Partial<PlaylistVisibilitie> = await dto.toEntity()
    const { data, error } = await playlistVisibilitieService.update(id, playlistVisibilitie)
    if (error) {
      message.value = error
    }
    return data
  }

  // API Requests
  /** Load all playlistVisibilities from the API */
  const loadPlaylistVisibilities = async (
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
    } = await playlistVisibilitieService.getAll(pagination, sortFields)
    handleResponse(data, err, paginationresult)

    if (unpagined && playlistVisibilities.value.length > 0) {
      while (paginationData.value.hasNext()) {
        paginationData.value.nextPage()
        const {
          data: nextData,
          error: nextErr,
          pagination: nextPagination,
        } = await playlistVisibilitieService.getAll(paginationData.value.toParameter(), sortFields)
        handleResponse(nextData, nextErr, nextPagination, true)
      }
    }
    stopLoading()
  }

  /**
   * Get a single playlistVisibilitie by id
   * @param id PlaylistVisibilitie ID
   */
  const getPlaylistVisibilitieById = async (id: number | string) => {
    const result = await playlistVisibilitieService.getById(id)
    if (result.error) {
      message.value = result.error
    }
    return result
  }

  /**
   * Search playlistVisibilities with filters, pagination, and sorting
   * @param filters Partial PlaylistVisibilitie object containing search filters
   * @param pagination Pagination request parameters
   * @param sortFields Array of fields to sort
   */
  const searchPlaylistVisibilities = async (
    unpagined: boolean = false,
    filters: Partial<PlaylistVisibilitie>,
    pagination: PaginationRequestParameter,
    sortFields: SortFieldParameter[],
  ) => {
    message.value = null
    startLoading()
    const {
      data,
      error: err,
      pagination: paginationresult,
    } = await playlistVisibilitieService.search(filters, pagination, sortFields)
    handleResponse(data, err, paginationresult)

    if (unpagined) {
      while (paginationData.value.hasNext()) {
        paginationData.value.nextPage()
        const {
          data: nextData,
          error: nextErr,
          pagination: nextPagination,
        } = await playlistVisibilitieService.search(
          filters,
          paginationData.value.toParameter(),
          sortFields,
        )
        handleResponse(nextData, nextErr, nextPagination, true)
      }
    }
    stopLoading()
  }
  const exportPlaylistVisibilitiesToCsv = async (data: Partial<PlaylistVisibilitie[]>) => {
    console.log('Exporting PlaylistVisibilitie to CSV', data)
    await playlistVisibilitieService.exportCsv(data)
  }

  /** Getter for pagination data */
  const getPaginationData = () => paginationData.value

  return {
    // state
    playlistVisibilities,
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
    exportPlaylistVisibilitiesToCsv,
    loadPlaylistVisibilities,
    searchPlaylistVisibilities,
    getPlaylistVisibilitieById,
    viewPlaylistVisibilitie,
    goToListView,
    goToCreateFormView,
    goToUpdateFormView,
    deletePlaylistVisibilitie,
    createPlaylistVisibilitie,
    updatePlaylistVisibilitie,
  }
}
