import { ref, watch } from 'vue'
import {
  NotificationPreference,
  NotificationPreferenceFormDTO,
} from '@/models/NotificationPreferenceModel'
import * as notificationPreferenceService from '@/services/NotificationPreferenceService'
import * as columnConfigService from '@/services/ColumnConfigService'
import { useLoading } from './useLoading'
import { PaginationRequestParameter, SortFieldParameter } from '@/models/api/RequestModel'
import { PaginationData } from '@/models/api/PageResponseModel'
import { useRouter } from 'vue-router'

export const DEFAULT_LIST_VIEW_FIELDS = ['id']

/**
 * Composable to manage NotificationPreference entities including CRUD operations,
 * search, pagination, foreign key loading, and navigation.
 */
export function useNotificationPreferences() {
  /** List of all notificationPreferences */
  const notificationPreferences = ref<NotificationPreference[]>([])

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
  const savedLayoutMode = localStorage.getItem('layoutMode_notificationPreference') as
    | 'list'
    | 'card'
    | null
  const layoutMode = ref<'list' | 'card'>(savedLayoutMode || 'list')

  watch(layoutMode, (newValue) => {
    localStorage.setItem('layoutMode_notificationPreference', newValue)
  })

  /**
   * Handles API response by updating the notificationPreferences and pagination
   * @param data Array of NotificationPreference returned by the API
   * @param err Optional error message
   * @param pagination Optional pagination data
   */
  const handleResponse = (
    data: NotificationPreference[],
    err: string | undefined,
    pagination?: PaginationData,
    concatData: boolean = false,
  ) => {
    if (err) {
      message.value = err
    }
    if (concatData) {
      notificationPreferences.value = notificationPreferences.value.concat(data)
    } else {
      notificationPreferences.value = data
    }
    if (pagination !== undefined) {
      paginationData.value = new PaginationData(pagination)
    }
  }

  const loadColumnConfig = async () => {
    try {
      const response = await columnConfigService.getVisibleFields('notificationPreference', 'list')
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
        'notificationPreference',
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
  /** Navigate to notificationPreference detail view */
  const viewNotificationPreference = (notificationPreference: NotificationPreference) => {
    router.push({
      name: 'notificationPreferencedetailsview',
      params: { id: notificationPreference.getKeyValue() },
    })
  }

  /** Navigate to notificationPreference list view */
  const goToListView = () => {
    router.push({ path: '/notificationPreferences' })
  }

  /** Navigate to notificationPreference create form */
  const goToCreateFormView = () => {
    router.push({ path: '/notificationPreferences/create' })
  }

  /** Navigate to notificationPreference update form */
  const goToUpdateFormView = (notificationPreference: NotificationPreference) => {
    router.push({
      name: 'notificationPreferenceupdateview',
      params: { id: notificationPreference.getKeyValue() },
    })
  }

  /**
   * Delete a notificationPreference by id
   * @param notificationPreference NotificationPreference object to delete
   */
  const deleteNotificationPreference = async (notificationPreference: NotificationPreference) => {
    const result = await notificationPreferenceService.remove(notificationPreference.getKeyValue())
    if (result.error) {
      message.value = result.error
    }
    return result
  }

  /**
   * Create a new notificationPreference
   * @param notificationPreferenceFormDTO NotificationPreference object to create
   */
  const createNotificationPreference = async (
    notificationPreferenceFormDTO: Partial<NotificationPreferenceFormDTO>,
  ) => {
    const dto = new NotificationPreferenceFormDTO(notificationPreferenceFormDTO)
    const notificationPreference: Partial<NotificationPreference> = await dto.toEntity()
    const { data, error, errors } =
      await notificationPreferenceService.create(notificationPreference)
    if (error) {
      message.value = error
    }
    return { data, errors }
  }

  /**
   * Update an existing notificationPreference
   * @param notificationPreferenceFormDTO NotificationPreference object to update
   */
  const updateNotificationPreference = async (
    id: number | string,
    notificationPreferenceFormDTO: Partial<NotificationPreferenceFormDTO>,
  ) => {
    const dto = new NotificationPreferenceFormDTO(notificationPreferenceFormDTO)
    const notificationPreference: Partial<NotificationPreference> = await dto.toEntity()
    const { data, error } = await notificationPreferenceService.update(id, notificationPreference)
    if (error) {
      message.value = error
    }
    return data
  }

  // API Requests
  /** Load all notificationPreferences from the API */
  const loadNotificationPreferences = async (
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
    } = await notificationPreferenceService.getAll(pagination, sortFields)
    handleResponse(data, err, paginationresult)

    if (unpagined && notificationPreferences.value.length > 0) {
      while (paginationData.value.hasNext()) {
        paginationData.value.nextPage()
        const {
          data: nextData,
          error: nextErr,
          pagination: nextPagination,
        } = await notificationPreferenceService.getAll(
          paginationData.value.toParameter(),
          sortFields,
        )
        handleResponse(nextData, nextErr, nextPagination, true)
      }
    }
    stopLoading()
  }

  /**
   * Get a single notificationPreference by id
   * @param id NotificationPreference ID
   */
  const getNotificationPreferenceById = async (id: number | string) => {
    const result = await notificationPreferenceService.getById(id)
    if (result.error) {
      message.value = result.error
    }
    return result
  }

  /**
   * Search notificationPreferences with filters, pagination, and sorting
   * @param filters Partial NotificationPreference object containing search filters
   * @param pagination Pagination request parameters
   * @param sortFields Array of fields to sort
   */
  const searchNotificationPreferences = async (
    unpagined: boolean = false,
    filters: Partial<NotificationPreference>,
    pagination: PaginationRequestParameter,
    sortFields: SortFieldParameter[],
  ) => {
    message.value = null
    startLoading()
    const {
      data,
      error: err,
      pagination: paginationresult,
    } = await notificationPreferenceService.search(filters, pagination, sortFields)
    handleResponse(data, err, paginationresult)

    if (unpagined) {
      while (paginationData.value.hasNext()) {
        paginationData.value.nextPage()
        const {
          data: nextData,
          error: nextErr,
          pagination: nextPagination,
        } = await notificationPreferenceService.search(
          filters,
          paginationData.value.toParameter(),
          sortFields,
        )
        handleResponse(nextData, nextErr, nextPagination, true)
      }
    }
    stopLoading()
  }
  const exportNotificationPreferencesToCsv = async (data: Partial<NotificationPreference[]>) => {
    console.log('Exporting NotificationPreference to CSV', data)
    await notificationPreferenceService.exportCsv(data)
  }

  const getAllNotificationPreferencesByUserId = async (
    userId: number | string | undefined,
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
    } = await notificationPreferenceService.getAllByUserId(userId, pagination, sortFields)
    handleResponse(data, err, paginationresult)

    if (unpagined) {
      while (paginationData.value.hasNext() && notificationPreferences.value.length > 0) {
        paginationData.value.nextPage()
        const {
          data: nextData,
          error: nextErr,
          pagination: nextPagination,
        } = await notificationPreferenceService.getAllByUserId(
          userId,
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
    notificationPreferences,
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
    exportNotificationPreferencesToCsv,
    loadNotificationPreferences,
    searchNotificationPreferences,
    getNotificationPreferenceById,
    viewNotificationPreference,
    goToListView,
    goToCreateFormView,
    goToUpdateFormView,
    deleteNotificationPreference,
    createNotificationPreference,
    updateNotificationPreference,
    getAllNotificationPreferencesByUserId,
  }
}
