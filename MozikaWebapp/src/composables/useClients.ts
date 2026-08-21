import { ref, watch } from 'vue'
import { Client, ClientFormDTO } from '@/models/ClientModel'
import * as clientService from '@/services/ClientService'
import * as columnConfigService from '@/services/ColumnConfigService'
import { useLoading } from './useLoading'
import { PaginationRequestParameter, SortFieldParameter } from '@/models/api/RequestModel'
import { PaginationData } from '@/models/api/PageResponseModel'
import { useRouter } from 'vue-router'

export const DEFAULT_LIST_VIEW_FIELDS = ['id']

/**
 * Composable to manage Client entities including CRUD operations,
 * search, pagination, foreign key loading, and navigation.
 */
export function useClients() {
  /** List of all clients */
  const clients = ref<Client[]>([])

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
  const savedLayoutMode = localStorage.getItem('layoutMode_client') as 'list' | 'card' | null
  const layoutMode = ref<'list' | 'card'>(savedLayoutMode || 'list')

  watch(layoutMode, (newValue) => {
    localStorage.setItem('layoutMode_client', newValue)
  })

  /**
   * Handles API response by updating the clients and pagination
   * @param data Array of Client returned by the API
   * @param err Optional error message
   * @param pagination Optional pagination data
   */
  const handleResponse = (
    data: Client[],
    err: string | undefined,
    pagination?: PaginationData,
    concatData: boolean = false,
  ) => {
    if (err) {
      message.value = err
    }
    if (concatData) {
      clients.value = clients.value.concat(data)
    } else {
      clients.value = data
    }
    if (pagination !== undefined) {
      paginationData.value = new PaginationData(pagination)
    }
  }

  const loadColumnConfig = async () => {
    try {
      const response = await columnConfigService.getVisibleFields('client', 'list')
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
      const response = await columnConfigService.updateVisibleFields('client', 'list', fields)
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
  /** Navigate to client detail view */
  const viewClient = (client: Client) => {
    router.push({ name: 'clientdetailsview', params: { id: client.getKeyValue() } })
  }

  /** Navigate to client list view */
  const goToListView = () => {
    router.push({ path: '/clients' })
  }

  /** Navigate to client create form */
  const goToCreateFormView = () => {
    router.push({ path: '/clients/create' })
  }

  /** Navigate to client update form */
  const goToUpdateFormView = (client: Client) => {
    router.push({ name: 'clientupdateview', params: { id: client.getKeyValue() } })
  }

  /**
   * Delete a client by id
   * @param client Client object to delete
   */
  const deleteClient = async (client: Client) => {
    const result = await clientService.remove(client.getKeyValue())
    if (result.error) {
      message.value = result.error
    }
    return result
  }

  /**
   * Create a new client
   * @param clientFormDTO Client object to create
   */
  const createClient = async (clientFormDTO: Partial<ClientFormDTO>) => {
    const dto = new ClientFormDTO(clientFormDTO)
    const client: Partial<Client> = await dto.toEntity()
    const { data, error, errors } = await clientService.create(client)
    if (error) {
      message.value = error
    }
    return { data, errors }
  }

  /**
   * Update an existing client
   * @param clientFormDTO Client object to update
   */
  const updateClient = async (id: number | string, clientFormDTO: Partial<ClientFormDTO>) => {
    const dto = new ClientFormDTO(clientFormDTO)
    const client: Partial<Client> = await dto.toEntity()
    const { data, error } = await clientService.update(id, client)
    if (error) {
      message.value = error
    }
    return data
  }

  // API Requests
  /** Load all clients from the API */
  const loadClients = async (
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
    } = await clientService.getAll(pagination, sortFields)
    handleResponse(data, err, paginationresult)

    if (unpagined && clients.value.length > 0) {
      while (paginationData.value.hasNext()) {
        paginationData.value.nextPage()
        const {
          data: nextData,
          error: nextErr,
          pagination: nextPagination,
        } = await clientService.getAll(paginationData.value.toParameter(), sortFields)
        handleResponse(nextData, nextErr, nextPagination, true)
      }
    }
    stopLoading()
  }

  /**
   * Get a single client by id
   * @param id Client ID
   */
  const getClientById = async (id: number | string) => {
    const result = await clientService.getById(id)
    if (result.error) {
      message.value = result.error
    }
    return result
  }

  /**
   * Search clients with filters, pagination, and sorting
   * @param filters Partial Client object containing search filters
   * @param pagination Pagination request parameters
   * @param sortFields Array of fields to sort
   */
  const searchClients = async (
    unpagined: boolean = false,
    filters: Partial<Client>,
    pagination: PaginationRequestParameter,
    sortFields: SortFieldParameter[],
  ) => {
    message.value = null
    startLoading()
    const {
      data,
      error: err,
      pagination: paginationresult,
    } = await clientService.search(filters, pagination, sortFields)
    handleResponse(data, err, paginationresult)

    if (unpagined) {
      while (paginationData.value.hasNext()) {
        paginationData.value.nextPage()
        const {
          data: nextData,
          error: nextErr,
          pagination: nextPagination,
        } = await clientService.search(filters, paginationData.value.toParameter(), sortFields)
        handleResponse(nextData, nextErr, nextPagination, true)
      }
    }
    stopLoading()
  }
  const exportClientsToCsv = async (data: Partial<Client[]>) => {
    console.log('Exporting Client to CSV', data)
    await clientService.exportCsv(data)
  }

  const getAllClientsByUserId = async (
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
    } = await clientService.getAllByUserId(userId, pagination, sortFields)
    handleResponse(data, err, paginationresult)

    if (unpagined) {
      while (paginationData.value.hasNext() && clients.value.length > 0) {
        paginationData.value.nextPage()
        const {
          data: nextData,
          error: nextErr,
          pagination: nextPagination,
        } = await clientService.getAllByUserId(
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
    clients,
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
    exportClientsToCsv,
    loadClients,
    searchClients,
    getClientById,
    viewClient,
    goToListView,
    goToCreateFormView,
    goToUpdateFormView,
    deleteClient,
    createClient,
    updateClient,
    getAllClientsByUserId,
  }
}
