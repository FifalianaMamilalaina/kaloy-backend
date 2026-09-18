import { ref, watch } from 'vue'
import { ArtistGroupMember, ArtistGroupMemberFormDTO } from '@/models/ArtistGroupMemberModel'
import * as artistGroupMemberService from '@/services/ArtistGroupMemberService'
import * as columnConfigService from '@/services/ColumnConfigService'
import { useLoading } from './useLoading'
import { PaginationRequestParameter, SortFieldParameter } from '@/models/api/RequestModel'
import { PaginationData } from '@/models/api/PageResponseModel'
import { useRouter } from 'vue-router'

export const DEFAULT_LIST_VIEW_FIELDS = ['id']

/**
 * Composable to manage ArtistGroupMember entities including CRUD operations,
 * search, pagination, foreign key loading, and navigation.
 */
export function useArtistGroupMembers() {
  /** List of all artistGroupMembers */
  const artistGroupMembers = ref<ArtistGroupMember[]>([])

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
  const savedLayoutMode = localStorage.getItem('layoutMode_artistGroupMember') as
    | 'list'
    | 'card'
    | null
  const layoutMode = ref<'list' | 'card'>(savedLayoutMode || 'list')

  watch(layoutMode, (newValue) => {
    localStorage.setItem('layoutMode_artistGroupMember', newValue)
  })

  /**
   * Handles API response by updating the artistGroupMembers and pagination
   * @param data Array of ArtistGroupMember returned by the API
   * @param err Optional error message
   * @param pagination Optional pagination data
   */
  const handleResponse = (
    data: ArtistGroupMember[],
    err: string | undefined,
    pagination?: PaginationData,
    concatData: boolean = false,
  ) => {
    if (err) {
      message.value = err
    }
    if (concatData) {
      artistGroupMembers.value = artistGroupMembers.value.concat(data)
    } else {
      artistGroupMembers.value = data
    }
    if (pagination !== undefined) {
      paginationData.value = new PaginationData(pagination)
    }
  }

  const loadColumnConfig = async () => {
    try {
      const response = await columnConfigService.getVisibleFields('artistGroupMember', 'list')
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
        'artistGroupMember',
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
  /** Navigate to artistGroupMember detail view */
  const viewArtistGroupMember = (artistGroupMember: ArtistGroupMember) => {
    router.push({
      name: 'artistGroupMemberdetailsview',
      params: { id: artistGroupMember.getKeyValue() },
    })
  }

  /** Navigate to artistGroupMember list view */
  const goToListView = () => {
    router.push({ path: '/artistGroupMembers' })
  }

  /** Navigate to artistGroupMember create form */
  const goToCreateFormView = () => {
    router.push({ path: '/artistGroupMembers/create' })
  }

  /** Navigate to artistGroupMember update form */
  const goToUpdateFormView = (artistGroupMember: ArtistGroupMember) => {
    router.push({
      name: 'artistGroupMemberupdateview',
      params: { id: artistGroupMember.getKeyValue() },
    })
  }

  /**
   * Delete a artistGroupMember by id
   * @param artistGroupMember ArtistGroupMember object to delete
   */
  const deleteArtistGroupMember = async (artistGroupMember: ArtistGroupMember) => {
    const result = await artistGroupMemberService.remove(artistGroupMember.getKeyValue())
    if (result.error) {
      message.value = result.error
    }
    return result
  }

  /**
   * Create a new artistGroupMember
   * @param artistGroupMemberFormDTO ArtistGroupMember object to create
   */
  const createArtistGroupMember = async (
    artistGroupMemberFormDTO: Partial<ArtistGroupMemberFormDTO>,
  ) => {
    const dto = new ArtistGroupMemberFormDTO(artistGroupMemberFormDTO)
    const artistGroupMember: Partial<ArtistGroupMember> = await dto.toEntity()
    const { data, error, errors } = await artistGroupMemberService.create(artistGroupMember)
    if (error) {
      message.value = error
    }
    return { data, errors }
  }

  /**
   * Update an existing artistGroupMember
   * @param artistGroupMemberFormDTO ArtistGroupMember object to update
   */
  const updateArtistGroupMember = async (
    id: number | string,
    artistGroupMemberFormDTO: Partial<ArtistGroupMemberFormDTO>,
  ) => {
    const dto = new ArtistGroupMemberFormDTO(artistGroupMemberFormDTO)
    const artistGroupMember: Partial<ArtistGroupMember> = await dto.toEntity()
    const { data, error } = await artistGroupMemberService.update(id, artistGroupMember)
    if (error) {
      message.value = error
    }
    return data
  }

  // API Requests
  /** Load all artistGroupMembers from the API */
  const loadArtistGroupMembers = async (
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
    } = await artistGroupMemberService.getAll(pagination, sortFields)
    handleResponse(data, err, paginationresult)

    if (unpagined && artistGroupMembers.value.length > 0) {
      while (paginationData.value.hasNext()) {
        paginationData.value.nextPage()
        const {
          data: nextData,
          error: nextErr,
          pagination: nextPagination,
        } = await artistGroupMemberService.getAll(paginationData.value.toParameter(), sortFields)
        handleResponse(nextData, nextErr, nextPagination, true)
      }
    }
    stopLoading()
  }

  /**
   * Get a single artistGroupMember by id
   * @param id ArtistGroupMember ID
   */
  const getArtistGroupMemberById = async (id: number | string) => {
    const result = await artistGroupMemberService.getById(id)
    if (result.error) {
      message.value = result.error
    }
    return result
  }

  /**
   * Search artistGroupMembers with filters, pagination, and sorting
   * @param filters Partial ArtistGroupMember object containing search filters
   * @param pagination Pagination request parameters
   * @param sortFields Array of fields to sort
   */
  const searchArtistGroupMembers = async (
    unpagined: boolean = false,
    filters: Partial<ArtistGroupMember>,
    pagination: PaginationRequestParameter,
    sortFields: SortFieldParameter[],
  ) => {
    message.value = null
    startLoading()
    const {
      data,
      error: err,
      pagination: paginationresult,
    } = await artistGroupMemberService.search(filters, pagination, sortFields)
    handleResponse(data, err, paginationresult)

    if (unpagined) {
      while (paginationData.value.hasNext()) {
        paginationData.value.nextPage()
        const {
          data: nextData,
          error: nextErr,
          pagination: nextPagination,
        } = await artistGroupMemberService.search(
          filters,
          paginationData.value.toParameter(),
          sortFields,
        )
        handleResponse(nextData, nextErr, nextPagination, true)
      }
    }
    stopLoading()
  }
  const exportArtistGroupMembersToCsv = async (data: Partial<ArtistGroupMember[]>) => {
    console.log('Exporting ArtistGroupMember to CSV', data)
    await artistGroupMemberService.exportCsv(data)
  }

  const getAllArtistGroupMembersByArtistId = async (
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
    } = await artistGroupMemberService.getAllByArtistId(artistId, pagination, sortFields)
    handleResponse(data, err, paginationresult)

    if (unpagined) {
      while (paginationData.value.hasNext() && artistGroupMembers.value.length > 0) {
        paginationData.value.nextPage()
        const {
          data: nextData,
          error: nextErr,
          pagination: nextPagination,
        } = await artistGroupMemberService.getAllByArtistId(
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
    artistGroupMembers,
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
    exportArtistGroupMembersToCsv,
    loadArtistGroupMembers,
    searchArtistGroupMembers,
    getArtistGroupMemberById,
    viewArtistGroupMember,
    goToListView,
    goToCreateFormView,
    goToUpdateFormView,
    deleteArtistGroupMember,
    createArtistGroupMember,
    updateArtistGroupMember,
    getAllArtistGroupMembersByArtistId,
  }
}
