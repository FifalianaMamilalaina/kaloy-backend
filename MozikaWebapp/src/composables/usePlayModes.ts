import { ref, watch } from 'vue'
import { PlayMode, PlayModeFormDTO } from '@/models/PlayModeModel'
import * as playModeService from '@/services/PlayModeService'
import * as columnConfigService from '@/services/ColumnConfigService'
import { useLoading } from './useLoading'
import { PaginationRequestParameter, SortFieldParameter } from '@/models/api/RequestModel'
import { PaginationData } from '@/models/api/PageResponseModel'
import { useRouter } from 'vue-router'

export const DEFAULT_LIST_VIEW_FIELDS = ['id']

/**
 * Composable to manage PlayMode entities including CRUD operations,
 * search, pagination, foreign key loading, and navigation.
 */
export function usePlayModes() {
  /** List of all playModes */
  const playModes = ref<PlayMode[]>([])

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
  const savedLayoutMode = localStorage.getItem('layoutMode_playMode') as 'list' | 'card' | null
  const layoutMode = ref<'list' | 'card'>(savedLayoutMode || 'list')

  watch(layoutMode, (newValue) => {
    localStorage.setItem('layoutMode_playMode', newValue)
  })

  /**
   * Handles API response by updating the playModes and pagination
   * @param data Array of PlayMode returned by the API
   * @param err Optional error message
   * @param pagination Optional pagination data
   */
  const handleResponse = (
    data: PlayMode[],
    err: string | undefined,
    pagination?: PaginationData,
    concatData: boolean = false,
  ) => {
    if (err) {
      message.value = err
    }
    if (concatData) {
      playModes.value = playModes.value.concat(data)
    } else {
      playModes.value = data
    }
    if (pagination !== undefined) {
      paginationData.value = new PaginationData(pagination)
    }
  }

  const loadColumnConfig = async () => {
    try {
      const response = await columnConfigService.getVisibleFields('playMode', 'list')
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
      const response = await columnConfigService.updateVisibleFields('playMode', 'list', fields)
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
  /** Navigate to playMode detail view */
  const viewPlayMode = (playMode: PlayMode) => {
    router.push({ name: 'playModedetailsview', params: { id: playMode.getKeyValue() } })
  }

  /** Navigate to playMode list view */
  const goToListView = () => {
    router.push({ path: '/playModes' })
  }

  /** Navigate to playMode create form */
  const goToCreateFormView = () => {
    router.push({ path: '/playModes/create' })
  }

  /** Navigate to playMode update form */
  const goToUpdateFormView = (playMode: PlayMode) => {
    router.push({ name: 'playModeupdateview', params: { id: playMode.getKeyValue() } })
  }

  /**
   * Delete a playMode by id
   * @param playMode PlayMode object to delete
   */
  const deletePlayMode = async (playMode: PlayMode) => {
    const result = await playModeService.remove(playMode.getKeyValue())
    if (result.error) {
      message.value = result.error
    }
    return result
  }

  /**
   * Create a new playMode
   * @param playModeFormDTO PlayMode object to create
   */
  const createPlayMode = async (playModeFormDTO: Partial<PlayModeFormDTO>) => {
    const dto = new PlayModeFormDTO(playModeFormDTO)
    const playMode: Partial<PlayMode> = await dto.toEntity()
    const { data, error, errors } = await playModeService.create(playMode)
    if (error) {
      message.value = error
    }
    return { data, errors }
  }

  /**
   * Update an existing playMode
   * @param playModeFormDTO PlayMode object to update
   */
  const updatePlayMode = async (id: number | string, playModeFormDTO: Partial<PlayModeFormDTO>) => {
    const dto = new PlayModeFormDTO(playModeFormDTO)
    const playMode: Partial<PlayMode> = await dto.toEntity()
    const { data, error } = await playModeService.update(id, playMode)
    if (error) {
      message.value = error
    }
    return data
  }

  // API Requests
  /** Load all playModes from the API */
  const loadPlayModes = async (
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
    } = await playModeService.getAll(pagination, sortFields)
    handleResponse(data, err, paginationresult)

    if (unpagined && playModes.value.length > 0) {
      while (paginationData.value.hasNext()) {
        paginationData.value.nextPage()
        const {
          data: nextData,
          error: nextErr,
          pagination: nextPagination,
        } = await playModeService.getAll(paginationData.value.toParameter(), sortFields)
        handleResponse(nextData, nextErr, nextPagination, true)
      }
    }
    stopLoading()
  }

  /**
   * Get a single playMode by id
   * @param id PlayMode ID
   */
  const getPlayModeById = async (id: number | string) => {
    const result = await playModeService.getById(id)
    if (result.error) {
      message.value = result.error
    }
    return result
  }

  /**
   * Search playModes with filters, pagination, and sorting
   * @param filters Partial PlayMode object containing search filters
   * @param pagination Pagination request parameters
   * @param sortFields Array of fields to sort
   */
  const searchPlayModes = async (
    unpagined: boolean = false,
    filters: Partial<PlayMode>,
    pagination: PaginationRequestParameter,
    sortFields: SortFieldParameter[],
  ) => {
    message.value = null
    startLoading()
    const {
      data,
      error: err,
      pagination: paginationresult,
    } = await playModeService.search(filters, pagination, sortFields)
    handleResponse(data, err, paginationresult)

    if (unpagined) {
      while (paginationData.value.hasNext()) {
        paginationData.value.nextPage()
        const {
          data: nextData,
          error: nextErr,
          pagination: nextPagination,
        } = await playModeService.search(filters, paginationData.value.toParameter(), sortFields)
        handleResponse(nextData, nextErr, nextPagination, true)
      }
    }
    stopLoading()
  }
  const exportPlayModesToCsv = async (data: Partial<PlayMode[]>) => {
    console.log('Exporting PlayMode to CSV', data)
    await playModeService.exportCsv(data)
  }

  /** Getter for pagination data */
  const getPaginationData = () => paginationData.value

  return {
    // state
    playModes,
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
    exportPlayModesToCsv,
    loadPlayModes,
    searchPlayModes,
    getPlayModeById,
    viewPlayMode,
    goToListView,
    goToCreateFormView,
    goToUpdateFormView,
    deletePlayMode,
    createPlayMode,
    updatePlayMode,
  }
}
