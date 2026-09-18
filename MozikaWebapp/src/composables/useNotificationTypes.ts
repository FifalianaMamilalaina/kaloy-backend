import { ref, watch } from 'vue'
import { NotificationType, NotificationTypeFormDTO } from '@/models/NotificationTypeModel'
import * as notificationTypeService from '@/services/NotificationTypeService'
import * as columnConfigService from '@/services/ColumnConfigService'
import { useLoading } from './useLoading'
import { PaginationRequestParameter, SortFieldParameter } from '@/models/api/RequestModel'
import { PaginationData } from '@/models/api/PageResponseModel'
import { useRouter } from 'vue-router'

export const DEFAULT_LIST_VIEW_FIELDS = ['id']

/**
 * Composable to manage NotificationType entities including CRUD operations,
 * search, pagination, foreign key loading, and navigation.
 */
export function useNotificationTypes() {
  /** List of all notificationTypes */
  const notificationTypes = ref<NotificationType[]>([])

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
  const savedLayoutMode = localStorage.getItem('layoutMode_notificationType') as
    | 'list'
    | 'card'
    | null
  const layoutMode = ref<'list' | 'card'>(savedLayoutMode || 'list')

  watch(layoutMode, (newValue) => {
    localStorage.setItem('layoutMode_notificationType', newValue)
  })

  /**
   * Handles API response by updating the notificationTypes and pagination
   * @param data Array of NotificationType returned by the API
   * @param err Optional error message
   * @param pagination Optional pagination data
   */
  const handleResponse = (
    data: NotificationType[],
    err: string | undefined,
    pagination?: PaginationData,
    concatData: boolean = false,
  ) => {
    if (err) {
      message.value = err
    }
    if (concatData) {
      notificationTypes.value = notificationTypes.value.concat(data)
    } else {
      notificationTypes.value = data
    }
    if (pagination !== undefined) {
      paginationData.value = new PaginationData(pagination)
    }
  }

  const loadColumnConfig = async () => {
    try {
      const response = await columnConfigService.getVisibleFields('notificationType', 'list')
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
        'notificationType',
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
  /** Navigate to notificationType detail view */
  const viewNotificationType = (notificationType: NotificationType) => {
    router.push({
      name: 'notificationTypedetailsview',
      params: { id: notificationType.getKeyValue() },
    })
  }

  /** Navigate to notificationType list view */
  const goToListView = () => {
    router.push({ path: '/notificationTypes' })
  }

  /** Navigate to notificationType create form */
  const goToCreateFormView = () => {
    router.push({ path: '/notificationTypes/create' })
  }

  /** Navigate to notificationType update form */
  const goToUpdateFormView = (notificationType: NotificationType) => {
    router.push({
      name: 'notificationTypeupdateview',
      params: { id: notificationType.getKeyValue() },
    })
  }

  /**
   * Delete a notificationType by id
   * @param notificationType NotificationType object to delete
   */
  const deleteNotificationType = async (notificationType: NotificationType) => {
    const result = await notificationTypeService.remove(notificationType.getKeyValue())
    if (result.error) {
      message.value = result.error
    }
    return result
  }

  /**
   * Create a new notificationType
   * @param notificationTypeFormDTO NotificationType object to create
   */
  const createNotificationType = async (
    notificationTypeFormDTO: Partial<NotificationTypeFormDTO>,
  ) => {
    const dto = new NotificationTypeFormDTO(notificationTypeFormDTO)
    const notificationType: Partial<NotificationType> = await dto.toEntity()
    const { data, error, errors } = await notificationTypeService.create(notificationType)
    if (error) {
      message.value = error
    }
    return { data, errors }
  }

  /**
   * Update an existing notificationType
   * @param notificationTypeFormDTO NotificationType object to update
   */
  const updateNotificationType = async (
    id: number | string,
    notificationTypeFormDTO: Partial<NotificationTypeFormDTO>,
  ) => {
    const dto = new NotificationTypeFormDTO(notificationTypeFormDTO)
    const notificationType: Partial<NotificationType> = await dto.toEntity()
    const { data, error } = await notificationTypeService.update(id, notificationType)
    if (error) {
      message.value = error
    }
    return data
  }

  // API Requests
  /** Load all notificationTypes from the API */
  const loadNotificationTypes = async (
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
    } = await notificationTypeService.getAll(pagination, sortFields)
    handleResponse(data, err, paginationresult)

    if (unpagined && notificationTypes.value.length > 0) {
      while (paginationData.value.hasNext()) {
        paginationData.value.nextPage()
        const {
          data: nextData,
          error: nextErr,
          pagination: nextPagination,
        } = await notificationTypeService.getAll(paginationData.value.toParameter(), sortFields)
        handleResponse(nextData, nextErr, nextPagination, true)
      }
    }
    stopLoading()
  }

  /**
   * Get a single notificationType by id
   * @param id NotificationType ID
   */
  const getNotificationTypeById = async (id: number | string) => {
    const result = await notificationTypeService.getById(id)
    if (result.error) {
      message.value = result.error
    }
    return result
  }

  /**
   * Search notificationTypes with filters, pagination, and sorting
   * @param filters Partial NotificationType object containing search filters
   * @param pagination Pagination request parameters
   * @param sortFields Array of fields to sort
   */
  const searchNotificationTypes = async (
    unpagined: boolean = false,
    filters: Partial<NotificationType>,
    pagination: PaginationRequestParameter,
    sortFields: SortFieldParameter[],
  ) => {
    message.value = null
    startLoading()
    const {
      data,
      error: err,
      pagination: paginationresult,
    } = await notificationTypeService.search(filters, pagination, sortFields)
    handleResponse(data, err, paginationresult)

    if (unpagined) {
      while (paginationData.value.hasNext()) {
        paginationData.value.nextPage()
        const {
          data: nextData,
          error: nextErr,
          pagination: nextPagination,
        } = await notificationTypeService.search(
          filters,
          paginationData.value.toParameter(),
          sortFields,
        )
        handleResponse(nextData, nextErr, nextPagination, true)
      }
    }
    stopLoading()
  }
  const exportNotificationTypesToCsv = async (data: Partial<NotificationType[]>) => {
    console.log('Exporting NotificationType to CSV', data)
    await notificationTypeService.exportCsv(data)
  }

  /** Getter for pagination data */
  const getPaginationData = () => paginationData.value

  return {
    // state
    notificationTypes,
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
    exportNotificationTypesToCsv,
    loadNotificationTypes,
    searchNotificationTypes,
    getNotificationTypeById,
    viewNotificationType,
    goToListView,
    goToCreateFormView,
    goToUpdateFormView,
    deleteNotificationType,
    createNotificationType,
    updateNotificationType,
  }
}
