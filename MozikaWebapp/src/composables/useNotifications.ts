import { ref, watch } from 'vue'
import { Notification, NotificationFormDTO } from '@/models/NotificationModel'
import * as notificationService from '@/services/NotificationService'
import * as columnConfigService from '@/services/ColumnConfigService'
import { useLoading } from './useLoading'
import { PaginationRequestParameter, SortFieldParameter } from '@/models/api/RequestModel'
import { PaginationData } from '@/models/api/PageResponseModel'
import { useRouter } from 'vue-router'

export const DEFAULT_LIST_VIEW_FIELDS = ['id']

/**
 * Composable to manage Notification entities including CRUD operations,
 * search, pagination, foreign key loading, and navigation.
 */
export function useNotifications() {
  /** List of all notifications */
  const notifications = ref<Notification[]>([])

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
  const savedLayoutMode = localStorage.getItem('layoutMode_notification') as 'list' | 'card' | null
  const layoutMode = ref<'list' | 'card'>(savedLayoutMode || 'list')

  watch(layoutMode, (newValue) => {
    localStorage.setItem('layoutMode_notification', newValue)
  })

  /**
   * Handles API response by updating the notifications and pagination
   * @param data Array of Notification returned by the API
   * @param err Optional error message
   * @param pagination Optional pagination data
   */
  const handleResponse = (
    data: Notification[],
    err: string | undefined,
    pagination?: PaginationData,
    concatData: boolean = false,
  ) => {
    if (err) {
      message.value = err
    }
    if (concatData) {
      notifications.value = notifications.value.concat(data)
    } else {
      notifications.value = data
    }
    if (pagination !== undefined) {
      paginationData.value = new PaginationData(pagination)
    }
  }

  const loadColumnConfig = async () => {
    try {
      const response = await columnConfigService.getVisibleFields('notification', 'list')
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
      const response = await columnConfigService.updateVisibleFields('notification', 'list', fields)
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
  /** Navigate to notification detail view */
  const viewNotification = (notification: Notification) => {
    router.push({ name: 'notificationdetailsview', params: { id: notification.getKeyValue() } })
  }

  /** Navigate to notification list view */
  const goToListView = () => {
    router.push({ path: '/notifications' })
  }

  /** Navigate to notification create form */
  const goToCreateFormView = () => {
    router.push({ path: '/notifications/create' })
  }

  /** Navigate to notification update form */
  const goToUpdateFormView = (notification: Notification) => {
    router.push({ name: 'notificationupdateview', params: { id: notification.getKeyValue() } })
  }

  /**
   * Delete a notification by id
   * @param notification Notification object to delete
   */
  const deleteNotification = async (notification: Notification) => {
    const result = await notificationService.remove(notification.getKeyValue())
    if (result.error) {
      message.value = result.error
    }
    return result
  }

  /**
   * Create a new notification
   * @param notificationFormDTO Notification object to create
   */
  const createNotification = async (notificationFormDTO: Partial<NotificationFormDTO>) => {
    const dto = new NotificationFormDTO(notificationFormDTO)
    const notification: Partial<Notification> = await dto.toEntity()
    const { data, error, errors } = await notificationService.create(notification)
    if (error) {
      message.value = error
    }
    return { data, errors }
  }

  /**
   * Update an existing notification
   * @param notificationFormDTO Notification object to update
   */
  const updateNotification = async (
    id: number | string,
    notificationFormDTO: Partial<NotificationFormDTO>,
  ) => {
    const dto = new NotificationFormDTO(notificationFormDTO)
    const notification: Partial<Notification> = await dto.toEntity()
    const { data, error } = await notificationService.update(id, notification)
    if (error) {
      message.value = error
    }
    return data
  }

  // API Requests
  /** Load all notifications from the API */
  const loadNotifications = async (
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
    } = await notificationService.getAll(pagination, sortFields)
    handleResponse(data, err, paginationresult)

    if (unpagined && notifications.value.length > 0) {
      while (paginationData.value.hasNext()) {
        paginationData.value.nextPage()
        const {
          data: nextData,
          error: nextErr,
          pagination: nextPagination,
        } = await notificationService.getAll(paginationData.value.toParameter(), sortFields)
        handleResponse(nextData, nextErr, nextPagination, true)
      }
    }
    stopLoading()
  }

  /**
   * Get a single notification by id
   * @param id Notification ID
   */
  const getNotificationById = async (id: number | string) => {
    const result = await notificationService.getById(id)
    if (result.error) {
      message.value = result.error
    }
    return result
  }

  /**
   * Search notifications with filters, pagination, and sorting
   * @param filters Partial Notification object containing search filters
   * @param pagination Pagination request parameters
   * @param sortFields Array of fields to sort
   */
  const searchNotifications = async (
    unpagined: boolean = false,
    filters: Partial<Notification>,
    pagination: PaginationRequestParameter,
    sortFields: SortFieldParameter[],
  ) => {
    message.value = null
    startLoading()
    const {
      data,
      error: err,
      pagination: paginationresult,
    } = await notificationService.search(filters, pagination, sortFields)
    handleResponse(data, err, paginationresult)

    if (unpagined) {
      while (paginationData.value.hasNext()) {
        paginationData.value.nextPage()
        const {
          data: nextData,
          error: nextErr,
          pagination: nextPagination,
        } = await notificationService.search(
          filters,
          paginationData.value.toParameter(),
          sortFields,
        )
        handleResponse(nextData, nextErr, nextPagination, true)
      }
    }
    stopLoading()
  }
  const exportNotificationsToCsv = async (data: Partial<Notification[]>) => {
    console.log('Exporting Notification to CSV', data)
    await notificationService.exportCsv(data)
  }

  const getAllNotificationsByUserId = async (
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
    } = await notificationService.getAllByUserId(userId, pagination, sortFields)
    handleResponse(data, err, paginationresult)

    if (unpagined) {
      while (paginationData.value.hasNext() && notifications.value.length > 0) {
        paginationData.value.nextPage()
        const {
          data: nextData,
          error: nextErr,
          pagination: nextPagination,
        } = await notificationService.getAllByUserId(
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
    notifications,
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
    exportNotificationsToCsv,
    loadNotifications,
    searchNotifications,
    getNotificationById,
    viewNotification,
    goToListView,
    goToCreateFormView,
    goToUpdateFormView,
    deleteNotification,
    createNotification,
    updateNotification,
    getAllNotificationsByUserId,
  }
}
