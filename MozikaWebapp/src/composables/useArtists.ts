import { ref, watch } from 'vue'
import { Artist, ArtistFormDTO } from '@/models/ArtistModel'
import * as artistService from '@/services/ArtistService'
import * as columnConfigService from '@/services/ColumnConfigService'
import { useLoading } from './useLoading'
import { PaginationRequestParameter, SortFieldParameter } from '@/models/api/RequestModel'
import { PaginationData } from '@/models/api/PageResponseModel'
import { useRouter } from 'vue-router'
import { Album, AlbumFormDTO } from '@/models/AlbumModel'
import { ArtistGroupMember, ArtistGroupMemberFormDTO } from '@/models/ArtistGroupMemberModel'
import { Concert, ConcertFormDTO } from '@/models/ConcertModel'
import { ContentSubmission, ContentSubmissionFormDTO } from '@/models/ContentSubmissionModel'
import { EditorialPlaylist, EditorialPlaylistFormDTO } from '@/models/EditorialPlaylistModel'
import { Event, EventFormDTO } from '@/models/EventModel'
import { Follow, FollowFormDTO } from '@/models/FollowModel'
import { Song, SongFormDTO } from '@/models/SongModel'

export const DEFAULT_LIST_VIEW_FIELDS = ['id']

/**
 * Composable to manage Artist entities including CRUD operations,
 * search, pagination, foreign key loading, and navigation.
 */
export function useArtists() {
  /** List of all artists */
  const artists = ref<Artist[]>([])

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
  const savedLayoutMode = localStorage.getItem('layoutMode_artist') as 'list' | 'card' | null
  const layoutMode = ref<'list' | 'card'>(savedLayoutMode || 'list')

  watch(layoutMode, (newValue) => {
    localStorage.setItem('layoutMode_artist', newValue)
  })

  /**
   * Handles API response by updating the artists and pagination
   * @param data Array of Artist returned by the API
   * @param err Optional error message
   * @param pagination Optional pagination data
   */
  const handleResponse = (
    data: Artist[],
    err: string | undefined,
    pagination?: PaginationData,
    concatData: boolean = false,
  ) => {
    if (err) {
      message.value = err
    }
    if (concatData) {
      artists.value = artists.value.concat(data)
    } else {
      artists.value = data
    }
    if (pagination !== undefined) {
      paginationData.value = new PaginationData(pagination)
    }
  }

  const loadColumnConfig = async () => {
    try {
      const response = await columnConfigService.getVisibleFields('artist', 'list')
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
      const response = await columnConfigService.updateVisibleFields('artist', 'list', fields)
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
  /** Navigate to artist detail view */
  const viewArtist = (artist: Artist) => {
    router.push({ name: 'artistdetailsview', params: { id: artist.getKeyValue() } })
  }

  /** Navigate to artist list view */
  const goToListView = () => {
    router.push({ path: '/artists' })
  }

  /** Navigate to artist create form */
  const goToCreateFormView = () => {
    router.push({ path: '/artists/create' })
  }

  /** Navigate to artist update form */
  const goToUpdateFormView = (artist: Artist) => {
    router.push({ name: 'artistupdateview', params: { id: artist.getKeyValue() } })
  }

  /**
   * Delete a artist by id
   * @param artist Artist object to delete
   */
  const deleteArtist = async (artist: Artist) => {
    const result = await artistService.remove(artist.getKeyValue())
    if (result.error) {
      message.value = result.error
    }
    return result
  }

  /**
   * Create a new artist
   * @param fullData Object containing artist and his details
   */
  const createArtist = async (fullData: {
    artist: Partial<ArtistFormDTO>
    albums: AlbumFormDTO[]
    artistGroupMembers: ArtistGroupMemberFormDTO[]
    concerts: ConcertFormDTO[]
    contentSubmissions: ContentSubmissionFormDTO[]
    editorialPlaylists: EditorialPlaylistFormDTO[]
    events: EventFormDTO[]
    follows: FollowFormDTO[]
    songs: SongFormDTO[]
  }) => {
    const dto = new ArtistFormDTO(fullData.artist)
    const artist: Partial<Artist> = await dto.toEntity()
    const albums: Partial<Album>[] = await AlbumFormDTO.toEntities(fullData.albums)
    const artistGroupMembers: Partial<ArtistGroupMember>[] =
      await ArtistGroupMemberFormDTO.toEntities(fullData.artistGroupMembers)
    const concerts: Partial<Concert>[] = await ConcertFormDTO.toEntities(fullData.concerts)
    const contentSubmissions: Partial<ContentSubmission>[] =
      await ContentSubmissionFormDTO.toEntities(fullData.contentSubmissions)
    const editorialPlaylists: Partial<EditorialPlaylist>[] =
      await EditorialPlaylistFormDTO.toEntities(fullData.editorialPlaylists)
    const events: Partial<Event>[] = await EventFormDTO.toEntities(fullData.events)
    const follows: Partial<Follow>[] = await FollowFormDTO.toEntities(fullData.follows)
    const songs: Partial<Song>[] = await SongFormDTO.toEntities(fullData.songs)

    const { data, error, errors } = await artistService.create({
      artist,
      albums,
      artistGroupMembers,
      concerts,
      contentSubmissions,
      editorialPlaylists,
      events,
      follows,
      songs,
    })
    if (error) {
      message.value = error
    }
    return { data, errors }
  }

  /**
   * Update an existing artist with full details
   * @param id Artist ID to update
   * @param fullData Object containing artist and its details
   */
  const updateArtist = async (
    id: number | string,
    fullData: {
      artist: Partial<ArtistFormDTO>
      albums: AlbumFormDTO[]
      artistGroupMembers: ArtistGroupMemberFormDTO[]
      concerts: ConcertFormDTO[]
      contentSubmissions: ContentSubmissionFormDTO[]
      editorialPlaylists: EditorialPlaylistFormDTO[]
      events: EventFormDTO[]
      follows: FollowFormDTO[]
      songs: SongFormDTO[]
    },
  ) => {
    const dto = new ArtistFormDTO(fullData.artist)
    const artist: Partial<Artist> = await dto.toEntity()

    const albums: Partial<Album>[] = await AlbumFormDTO.toEntities(fullData.albums)
    const artistGroupMembers: Partial<ArtistGroupMember>[] =
      await ArtistGroupMemberFormDTO.toEntities(fullData.artistGroupMembers)
    const concerts: Partial<Concert>[] = await ConcertFormDTO.toEntities(fullData.concerts)
    const contentSubmissions: Partial<ContentSubmission>[] =
      await ContentSubmissionFormDTO.toEntities(fullData.contentSubmissions)
    const editorialPlaylists: Partial<EditorialPlaylist>[] =
      await EditorialPlaylistFormDTO.toEntities(fullData.editorialPlaylists)
    const events: Partial<Event>[] = await EventFormDTO.toEntities(fullData.events)
    const follows: Partial<Follow>[] = await FollowFormDTO.toEntities(fullData.follows)
    const songs: Partial<Song>[] = await SongFormDTO.toEntities(fullData.songs)

    const { data, error, errors } = await artistService.update(id, {
      artist,
      albums,
      artistGroupMembers,
      concerts,
      contentSubmissions,
      editorialPlaylists,
      events,
      follows,
      songs,
    })
    if (error) {
      message.value = error
    }
    return { data, errors }
  }

  // API Requests
  /** Load all artists from the API */
  const loadArtists = async (
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
    } = await artistService.getAll(pagination, sortFields)
    handleResponse(data, err, paginationresult)

    if (unpagined && artists.value.length > 0) {
      while (paginationData.value.hasNext()) {
        paginationData.value.nextPage()
        const {
          data: nextData,
          error: nextErr,
          pagination: nextPagination,
        } = await artistService.getAll(paginationData.value.toParameter(), sortFields)
        handleResponse(nextData, nextErr, nextPagination, true)
      }
    }
    stopLoading()
  }

  /**
   * Get a single artist by id
   * @param id Artist ID
   */
  const getArtistById = async (id: number | string) => {
    const result = await artistService.getById(id)
    if (result.error) {
      message.value = result.error
    }
    return result
  }

  /**
   * Search artists with filters, pagination, and sorting
   * @param filters Partial Artist object containing search filters
   * @param pagination Pagination request parameters
   * @param sortFields Array of fields to sort
   */
  const searchArtists = async (
    unpagined: boolean = false,
    filters: Partial<Artist>,
    pagination: PaginationRequestParameter,
    sortFields: SortFieldParameter[],
  ) => {
    message.value = null
    startLoading()
    const {
      data,
      error: err,
      pagination: paginationresult,
    } = await artistService.search(filters, pagination, sortFields)
    handleResponse(data, err, paginationresult)

    if (unpagined) {
      while (paginationData.value.hasNext()) {
        paginationData.value.nextPage()
        const {
          data: nextData,
          error: nextErr,
          pagination: nextPagination,
        } = await artistService.search(filters, paginationData.value.toParameter(), sortFields)
        handleResponse(nextData, nextErr, nextPagination, true)
      }
    }
    stopLoading()
  }
  const exportArtistsToCsv = async (data: Partial<Artist[]>) => {
    console.log('Exporting Artist to CSV', data)
    await artistService.exportCsv(data)
  }

  const getAllArtistsByUserId = async (
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
    } = await artistService.getAllByUserId(userId, pagination, sortFields)
    handleResponse(data, err, paginationresult)

    if (unpagined) {
      while (paginationData.value.hasNext() && artists.value.length > 0) {
        paginationData.value.nextPage()
        const {
          data: nextData,
          error: nextErr,
          pagination: nextPagination,
        } = await artistService.getAllByUserId(
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
    artists,
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
    exportArtistsToCsv,
    loadArtists,
    searchArtists,
    getArtistById,
    viewArtist,
    goToListView,
    goToCreateFormView,
    goToUpdateFormView,
    deleteArtist,
    createArtist,
    updateArtist,
    getAllArtistsByUserId,
  }
}
