import { ref, watch } from 'vue'
import { InteractionTarget, InteractionTargetFormDTO } from '@/models/InteractionTargetModel'
import * as interactionTargetService from '@/services/InteractionTargetService'
import * as columnConfigService from '@/services/ColumnConfigService'
import { useLoading } from './useLoading'
import { PaginationRequestParameter, SortFieldParameter } from '@/models/api/RequestModel'
import { PaginationData } from '@/models/api/PageResponseModel'
import { useRouter } from 'vue-router'

export const DEFAULT_LIST_VIEW_FIELDS = ['id']

/**
 * Composable to manage InteractionTarget entities including CRUD operations,
 * search, pagination, foreign key loading, and navigation.
 */
export function useInteractionTargets() {
  /** List of all interactionTargets */
  const interactionTargets = ref<InteractionTarget[]>([])

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
  const savedLayoutMode = localStorage.getItem('layoutMode_interactionTarget') as
    | 'list'
    | 'card'
    | null
  const layoutMode = ref<'list' | 'card'>(savedLayoutMode || 'list')

  watch(layoutMode, (newValue) => {
    localStorage.setItem('layoutMode_interactionTarget', newValue)
  })

  /**
   * Handles API response by updating the interactionTargets and pagination
   * @param data Array of InteractionTarget returned by the API
   * @param err Optional error message
   * @param pagination Optional pagination data
   */
  const handleResponse = (
    data: InteractionTarget[],
    err: string | undefined,
    pagination?: PaginationData,
    concatData: boolean = false,
  ) => {
    if (err) {
      message.value = err
    }
    if (concatData) {
      interactionTargets.value = interactionTargets.value.concat(data)
    } else {
      interactionTargets.value = data
    }
    if (pagination !== undefined) {
      paginationData.value = new PaginationData(pagination)
    }
  }

  const loadColumnConfig = async () => {
    try {
      const response = await columnConfigService.getVisibleFields('interactionTarget', 'list')
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
        'interactionTarget',
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
  /** Navigate to interactionTarget detail view */
  const viewInteractionTarget = (interactionTarget: InteractionTarget) => {
    router.push({
      name: 'interactionTargetdetailsview',
      params: { id: interactionTarget.getKeyValue() },
    })
  }

  /** Navigate to interactionTarget list view */
  const goToListView = () => {
    router.push({ path: '/interactionTargets' })
  }

  /** Navigate to interactionTarget create form */
  const goToCreateFormView = () => {
    router.push({ path: '/interactionTargets/create' })
  }

  /** Navigate to interactionTarget update form */
  const goToUpdateFormView = (interactionTarget: InteractionTarget) => {
    router.push({
      name: 'interactionTargetupdateview',
      params: { id: interactionTarget.getKeyValue() },
    })
  }

  /**
   * Delete a interactionTarget by id
   * @param interactionTarget InteractionTarget object to delete
   */
  const deleteInteractionTarget = async (interactionTarget: InteractionTarget) => {
    const result = await interactionTargetService.remove(interactionTarget.getKeyValue())
    if (result.error) {
      message.value = result.error
    }
    return result
  }

  /**
   * Create a new interactionTarget
   * @param interactionTargetFormDTO InteractionTarget object to create
   */
  const createInteractionTarget = async (
    interactionTargetFormDTO: Partial<InteractionTargetFormDTO>,
  ) => {
    const dto = new InteractionTargetFormDTO(interactionTargetFormDTO)
    const interactionTarget: Partial<InteractionTarget> = await dto.toEntity()
    const { data, error, errors } = await interactionTargetService.create(interactionTarget)
    if (error) {
      message.value = error
    }
    return { data, errors }
  }

  /**
   * Update an existing interactionTarget
   * @param interactionTargetFormDTO InteractionTarget object to update
   */
  const updateInteractionTarget = async (
    id: number | string,
    interactionTargetFormDTO: Partial<InteractionTargetFormDTO>,
  ) => {
    const dto = new InteractionTargetFormDTO(interactionTargetFormDTO)
    const interactionTarget: Partial<InteractionTarget> = await dto.toEntity()
    const { data, error } = await interactionTargetService.update(id, interactionTarget)
    if (error) {
      message.value = error
    }
    return data
  }

  // API Requests
  /** Load all interactionTargets from the API */
  const loadInteractionTargets = async (
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
    } = await interactionTargetService.getAll(pagination, sortFields)
    handleResponse(data, err, paginationresult)

    if (unpagined && interactionTargets.value.length > 0) {
      while (paginationData.value.hasNext()) {
        paginationData.value.nextPage()
        const {
          data: nextData,
          error: nextErr,
          pagination: nextPagination,
        } = await interactionTargetService.getAll(paginationData.value.toParameter(), sortFields)
        handleResponse(nextData, nextErr, nextPagination, true)
      }
    }
    stopLoading()
  }

  /**
   * Get a single interactionTarget by id
   * @param id InteractionTarget ID
   */
  const getInteractionTargetById = async (id: number | string) => {
    const result = await interactionTargetService.getById(id)
    if (result.error) {
      message.value = result.error
    }
    return result
  }

  /**
   * Search interactionTargets with filters, pagination, and sorting
   * @param filters Partial InteractionTarget object containing search filters
   * @param pagination Pagination request parameters
   * @param sortFields Array of fields to sort
   */
  const searchInteractionTargets = async (
    unpagined: boolean = false,
    filters: Partial<InteractionTarget>,
    pagination: PaginationRequestParameter,
    sortFields: SortFieldParameter[],
  ) => {
    message.value = null
    startLoading()
    const {
      data,
      error: err,
      pagination: paginationresult,
    } = await interactionTargetService.search(filters, pagination, sortFields)
    handleResponse(data, err, paginationresult)

    if (unpagined) {
      while (paginationData.value.hasNext()) {
        paginationData.value.nextPage()
        const {
          data: nextData,
          error: nextErr,
          pagination: nextPagination,
        } = await interactionTargetService.search(
          filters,
          paginationData.value.toParameter(),
          sortFields,
        )
        handleResponse(nextData, nextErr, nextPagination, true)
      }
    }
    stopLoading()
  }
  const exportInteractionTargetsToCsv = async (data: Partial<InteractionTarget[]>) => {
    console.log('Exporting InteractionTarget to CSV', data)
    await interactionTargetService.exportCsv(data)
  }

  /** Getter for pagination data */
  const getPaginationData = () => paginationData.value

  return {
    // state
    interactionTargets,
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
    exportInteractionTargetsToCsv,
    loadInteractionTargets,
    searchInteractionTargets,
    getInteractionTargetById,
    viewInteractionTarget,
    goToListView,
    goToCreateFormView,
    goToUpdateFormView,
    deleteInteractionTarget,
    createInteractionTarget,
    updateInteractionTarget,
  }
}
