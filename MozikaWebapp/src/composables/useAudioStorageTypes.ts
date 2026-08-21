import { ref, watch } from 'vue'
import { AudioStorageType, AudioStorageTypeFormDTO } from '@/models/AudioStorageTypeModel'
import * as audioStorageTypeService from '@/services/AudioStorageTypeService'
import * as columnConfigService from '@/services/ColumnConfigService'
import { useLoading } from './useLoading'
import { PaginationRequestParameter, SortFieldParameter } from '@/models/api/RequestModel'
import { PaginationData } from '@/models/api/PageResponseModel'
import { useRouter } from 'vue-router'

export const DEFAULT_LIST_VIEW_FIELDS = ['id']

/**
 * Composable to manage AudioStorageType entities including CRUD operations,
 * search, pagination, foreign key loading, and navigation.
 */
export function useAudioStorageTypes() {
  /** List of all audioStorageTypes */
  const audioStorageTypes = ref<AudioStorageType[]>([])

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
  const savedLayoutMode = localStorage.getItem('layoutMode_audioStorageType') as
    | 'list'
    | 'card'
    | null
  const layoutMode = ref<'list' | 'card'>(savedLayoutMode || 'list')

  watch(layoutMode, (newValue) => {
    localStorage.setItem('layoutMode_audioStorageType', newValue)
  })

  /**
   * Handles API response by updating the audioStorageTypes and pagination
   * @param data Array of AudioStorageType returned by the API
   * @param err Optional error message
   * @param pagination Optional pagination data
   */
  const handleResponse = (
    data: AudioStorageType[],
    err: string | undefined,
    pagination?: PaginationData,
    concatData: boolean = false,
  ) => {
    if (err) {
      message.value = err
    }
    if (concatData) {
      audioStorageTypes.value = audioStorageTypes.value.concat(data)
    } else {
      audioStorageTypes.value = data
    }
    if (pagination !== undefined) {
      paginationData.value = new PaginationData(pagination)
    }
  }

  const loadColumnConfig = async () => {
    try {
      const response = await columnConfigService.getVisibleFields('audioStorageType', 'list')
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
        'audioStorageType',
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
  /** Navigate to audioStorageType detail view */
  const viewAudioStorageType = (audioStorageType: AudioStorageType) => {
    router.push({
      name: 'audioStorageTypedetailsview',
      params: { id: audioStorageType.getKeyValue() },
    })
  }

  /** Navigate to audioStorageType list view */
  const goToListView = () => {
    router.push({ path: '/audioStorageTypes' })
  }

  /** Navigate to audioStorageType create form */
  const goToCreateFormView = () => {
    router.push({ path: '/audioStorageTypes/create' })
  }

  /** Navigate to audioStorageType update form */
  const goToUpdateFormView = (audioStorageType: AudioStorageType) => {
    router.push({
      name: 'audioStorageTypeupdateview',
      params: { id: audioStorageType.getKeyValue() },
    })
  }

  /**
   * Delete a audioStorageType by id
   * @param audioStorageType AudioStorageType object to delete
   */
  const deleteAudioStorageType = async (audioStorageType: AudioStorageType) => {
    const result = await audioStorageTypeService.remove(audioStorageType.getKeyValue())
    if (result.error) {
      message.value = result.error
    }
    return result
  }

  /**
   * Create a new audioStorageType
   * @param audioStorageTypeFormDTO AudioStorageType object to create
   */
  const createAudioStorageType = async (
    audioStorageTypeFormDTO: Partial<AudioStorageTypeFormDTO>,
  ) => {
    const dto = new AudioStorageTypeFormDTO(audioStorageTypeFormDTO)
    const audioStorageType: Partial<AudioStorageType> = await dto.toEntity()
    const { data, error, errors } = await audioStorageTypeService.create(audioStorageType)
    if (error) {
      message.value = error
    }
    return { data, errors }
  }

  /**
   * Update an existing audioStorageType
   * @param audioStorageTypeFormDTO AudioStorageType object to update
   */
  const updateAudioStorageType = async (
    id: number | string,
    audioStorageTypeFormDTO: Partial<AudioStorageTypeFormDTO>,
  ) => {
    const dto = new AudioStorageTypeFormDTO(audioStorageTypeFormDTO)
    const audioStorageType: Partial<AudioStorageType> = await dto.toEntity()
    const { data, error } = await audioStorageTypeService.update(id, audioStorageType)
    if (error) {
      message.value = error
    }
    return data
  }

  // API Requests
  /** Load all audioStorageTypes from the API */
  const loadAudioStorageTypes = async (
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
    } = await audioStorageTypeService.getAll(pagination, sortFields)
    handleResponse(data, err, paginationresult)

    if (unpagined && audioStorageTypes.value.length > 0) {
      while (paginationData.value.hasNext()) {
        paginationData.value.nextPage()
        const {
          data: nextData,
          error: nextErr,
          pagination: nextPagination,
        } = await audioStorageTypeService.getAll(paginationData.value.toParameter(), sortFields)
        handleResponse(nextData, nextErr, nextPagination, true)
      }
    }
    stopLoading()
  }

  /**
   * Get a single audioStorageType by id
   * @param id AudioStorageType ID
   */
  const getAudioStorageTypeById = async (id: number | string) => {
    const result = await audioStorageTypeService.getById(id)
    if (result.error) {
      message.value = result.error
    }
    return result
  }

  /**
   * Search audioStorageTypes with filters, pagination, and sorting
   * @param filters Partial AudioStorageType object containing search filters
   * @param pagination Pagination request parameters
   * @param sortFields Array of fields to sort
   */
  const searchAudioStorageTypes = async (
    unpagined: boolean = false,
    filters: Partial<AudioStorageType>,
    pagination: PaginationRequestParameter,
    sortFields: SortFieldParameter[],
  ) => {
    message.value = null
    startLoading()
    const {
      data,
      error: err,
      pagination: paginationresult,
    } = await audioStorageTypeService.search(filters, pagination, sortFields)
    handleResponse(data, err, paginationresult)

    if (unpagined) {
      while (paginationData.value.hasNext()) {
        paginationData.value.nextPage()
        const {
          data: nextData,
          error: nextErr,
          pagination: nextPagination,
        } = await audioStorageTypeService.search(
          filters,
          paginationData.value.toParameter(),
          sortFields,
        )
        handleResponse(nextData, nextErr, nextPagination, true)
      }
    }
    stopLoading()
  }
  const exportAudioStorageTypesToCsv = async (data: Partial<AudioStorageType[]>) => {
    console.log('Exporting AudioStorageType to CSV', data)
    await audioStorageTypeService.exportCsv(data)
  }

  /** Getter for pagination data */
  const getPaginationData = () => paginationData.value

  return {
    // state
    audioStorageTypes,
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
    exportAudioStorageTypesToCsv,
    loadAudioStorageTypes,
    searchAudioStorageTypes,
    getAudioStorageTypeById,
    viewAudioStorageType,
    goToListView,
    goToCreateFormView,
    goToUpdateFormView,
    deleteAudioStorageType,
    createAudioStorageType,
    updateAudioStorageType,
  }
}
