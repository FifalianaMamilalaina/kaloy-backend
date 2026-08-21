import { ref, watch } from 'vue'
import { MemberStatuse, MemberStatuseFormDTO } from '@/models/MemberStatuseModel'
import * as memberStatuseService from '@/services/MemberStatuseService'
import * as columnConfigService from '@/services/ColumnConfigService'
import { useLoading } from './useLoading'
import { PaginationRequestParameter, SortFieldParameter } from '@/models/api/RequestModel'
import { PaginationData } from '@/models/api/PageResponseModel'
import { useRouter } from 'vue-router'

export const DEFAULT_LIST_VIEW_FIELDS = ['id']

/**
 * Composable to manage MemberStatuse entities including CRUD operations,
 * search, pagination, foreign key loading, and navigation.
 */
export function useMemberStatuses() {
  /** List of all memberStatuses */
  const memberStatuses = ref<MemberStatuse[]>([])

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
  const savedLayoutMode = localStorage.getItem('layoutMode_memberStatuse') as 'list' | 'card' | null
  const layoutMode = ref<'list' | 'card'>(savedLayoutMode || 'list')

  watch(layoutMode, (newValue) => {
    localStorage.setItem('layoutMode_memberStatuse', newValue)
  })

  /**
   * Handles API response by updating the memberStatuses and pagination
   * @param data Array of MemberStatuse returned by the API
   * @param err Optional error message
   * @param pagination Optional pagination data
   */
  const handleResponse = (
    data: MemberStatuse[],
    err: string | undefined,
    pagination?: PaginationData,
    concatData: boolean = false,
  ) => {
    if (err) {
      message.value = err
    }
    if (concatData) {
      memberStatuses.value = memberStatuses.value.concat(data)
    } else {
      memberStatuses.value = data
    }
    if (pagination !== undefined) {
      paginationData.value = new PaginationData(pagination)
    }
  }

  const loadColumnConfig = async () => {
    try {
      const response = await columnConfigService.getVisibleFields('memberStatuse', 'list')
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
        'memberStatuse',
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
  /** Navigate to memberStatuse detail view */
  const viewMemberStatuse = (memberStatuse: MemberStatuse) => {
    router.push({ name: 'memberStatusedetailsview', params: { id: memberStatuse.getKeyValue() } })
  }

  /** Navigate to memberStatuse list view */
  const goToListView = () => {
    router.push({ path: '/memberStatuses' })
  }

  /** Navigate to memberStatuse create form */
  const goToCreateFormView = () => {
    router.push({ path: '/memberStatuses/create' })
  }

  /** Navigate to memberStatuse update form */
  const goToUpdateFormView = (memberStatuse: MemberStatuse) => {
    router.push({ name: 'memberStatuseupdateview', params: { id: memberStatuse.getKeyValue() } })
  }

  /**
   * Delete a memberStatuse by id
   * @param memberStatuse MemberStatuse object to delete
   */
  const deleteMemberStatuse = async (memberStatuse: MemberStatuse) => {
    const result = await memberStatuseService.remove(memberStatuse.getKeyValue())
    if (result.error) {
      message.value = result.error
    }
    return result
  }

  /**
   * Create a new memberStatuse
   * @param memberStatuseFormDTO MemberStatuse object to create
   */
  const createMemberStatuse = async (memberStatuseFormDTO: Partial<MemberStatuseFormDTO>) => {
    const dto = new MemberStatuseFormDTO(memberStatuseFormDTO)
    const memberStatuse: Partial<MemberStatuse> = await dto.toEntity()
    const { data, error, errors } = await memberStatuseService.create(memberStatuse)
    if (error) {
      message.value = error
    }
    return { data, errors }
  }

  /**
   * Update an existing memberStatuse
   * @param memberStatuseFormDTO MemberStatuse object to update
   */
  const updateMemberStatuse = async (
    id: number | string,
    memberStatuseFormDTO: Partial<MemberStatuseFormDTO>,
  ) => {
    const dto = new MemberStatuseFormDTO(memberStatuseFormDTO)
    const memberStatuse: Partial<MemberStatuse> = await dto.toEntity()
    const { data, error } = await memberStatuseService.update(id, memberStatuse)
    if (error) {
      message.value = error
    }
    return data
  }

  // API Requests
  /** Load all memberStatuses from the API */
  const loadMemberStatuses = async (
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
    } = await memberStatuseService.getAll(pagination, sortFields)
    handleResponse(data, err, paginationresult)

    if (unpagined && memberStatuses.value.length > 0) {
      while (paginationData.value.hasNext()) {
        paginationData.value.nextPage()
        const {
          data: nextData,
          error: nextErr,
          pagination: nextPagination,
        } = await memberStatuseService.getAll(paginationData.value.toParameter(), sortFields)
        handleResponse(nextData, nextErr, nextPagination, true)
      }
    }
    stopLoading()
  }

  /**
   * Get a single memberStatuse by id
   * @param id MemberStatuse ID
   */
  const getMemberStatuseById = async (id: number | string) => {
    const result = await memberStatuseService.getById(id)
    if (result.error) {
      message.value = result.error
    }
    return result
  }

  /**
   * Search memberStatuses with filters, pagination, and sorting
   * @param filters Partial MemberStatuse object containing search filters
   * @param pagination Pagination request parameters
   * @param sortFields Array of fields to sort
   */
  const searchMemberStatuses = async (
    unpagined: boolean = false,
    filters: Partial<MemberStatuse>,
    pagination: PaginationRequestParameter,
    sortFields: SortFieldParameter[],
  ) => {
    message.value = null
    startLoading()
    const {
      data,
      error: err,
      pagination: paginationresult,
    } = await memberStatuseService.search(filters, pagination, sortFields)
    handleResponse(data, err, paginationresult)

    if (unpagined) {
      while (paginationData.value.hasNext()) {
        paginationData.value.nextPage()
        const {
          data: nextData,
          error: nextErr,
          pagination: nextPagination,
        } = await memberStatuseService.search(
          filters,
          paginationData.value.toParameter(),
          sortFields,
        )
        handleResponse(nextData, nextErr, nextPagination, true)
      }
    }
    stopLoading()
  }
  const exportMemberStatusesToCsv = async (data: Partial<MemberStatuse[]>) => {
    console.log('Exporting MemberStatuse to CSV', data)
    await memberStatuseService.exportCsv(data)
  }

  /** Getter for pagination data */
  const getPaginationData = () => paginationData.value

  return {
    // state
    memberStatuses,
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
    exportMemberStatusesToCsv,
    loadMemberStatuses,
    searchMemberStatuses,
    getMemberStatuseById,
    viewMemberStatuse,
    goToListView,
    goToCreateFormView,
    goToUpdateFormView,
    deleteMemberStatuse,
    createMemberStatuse,
    updateMemberStatuse,
  }
}
