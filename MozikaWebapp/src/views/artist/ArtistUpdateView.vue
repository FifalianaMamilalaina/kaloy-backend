<template>
  <div class="w-full">
    <!-- Header -->
    <div class="flex items-center justify-between mb-6">
      <h3 class="text-xl font-semibold text-base-content">
        Artist /
        <span class="text-base-content/50 font-normal">{{ $t('entity.update.nav') }}</span>
      </h3>
      <GenesisButton
        :title="$t('button.backToListDescription')"
        @click="goToListView"
        class="btn-secondary"
      >
        <LeftArrowIcon />
        {{ $t('button.backToList') }}
      </GenesisButton>
    </div>

    <!-- Form -->
    <div
      v-if="
        entity &&
        albumLoaded &&
        artistGroupMemberLoaded &&
        concertLoaded &&
        contentSubmissionLoaded &&
        editorialPlaylistLoaded &&
        eventLoaded &&
        followLoaded &&
        songLoaded
      "
    >
      <artist-form
        :artist="entity"
        :albumsData="albums"
        :artistGroupMembersData="artistGroupMembers"
        :concertsData="concerts"
        :contentSubmissionsData="contentSubmissions"
        :editorialPlaylistsData="editorialPlaylists"
        :eventsData="events"
        :followsData="follows"
        :songsData="songs"
        :violations="violations"
        :submit-label="$t('entity.update.submitLabel', { entity: 'Artist' })"
        @submit="updateHandler"
        @cancel="cancelHandler"
      />
    </div>

    <!-- Alert -->
    <AlertPopup
      :message="message ?? undefined"
      title="Error 500"
      :visible="alertPopup"
      @close="closePopup"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import ArtistForm from '@/entities/artist/ArtistForm.vue'
import AlertPopup from '@/components/popup/AlertPopup.vue'
import { useArtists } from '@/composables/useArtists'
import { Artist, ArtistFormDTO } from '@/models/ArtistModel'
import { usePopup } from '@/composables/usePopup'
import GenesisButton from '@/components/button/GenesisButton.vue'
import LeftArrowIcon from '@/components/icons/LeftArrowIcon.vue'
import { useFreezeScreenStore } from '@/stores/useFreezeScreenStore.ts'
import { useAlbums } from '@/composables/useAlbums'
import { AlbumFormDTO } from '@/models/AlbumModel'
import { useArtistGroupMembers } from '@/composables/useArtistGroupMembers'
import { ArtistGroupMemberFormDTO } from '@/models/ArtistGroupMemberModel'
import { useConcerts } from '@/composables/useConcerts'
import { ConcertFormDTO } from '@/models/ConcertModel'
import { useContentSubmissions } from '@/composables/useContentSubmissions'
import { ContentSubmissionFormDTO } from '@/models/ContentSubmissionModel'
import { useEditorialPlaylists } from '@/composables/useEditorialPlaylists'
import { EditorialPlaylistFormDTO } from '@/models/EditorialPlaylistModel'
import { useEvents } from '@/composables/useEvents'
import { EventFormDTO } from '@/models/EventModel'
import { useFollows } from '@/composables/useFollows'
import { FollowFormDTO } from '@/models/FollowModel'
import { useSongs } from '@/composables/useSongs'
import { SongFormDTO } from '@/models/SongModel'

import { PaginationRequestParameter } from '@/models/api/RequestModel'

const route = useRoute()
const pathId = Number(route.params.id)
const { closePopup, openPopup, visible: alertPopup } = usePopup()
const { getArtistById, goToListView, updateArtist, viewArtist, message } = useArtists()
const entity = ref<Artist | null>(null)
const freezeStore = useFreezeScreenStore()

const { albums, getAllAlbumsByArtistId } = useAlbums()
const albumLoaded = ref(false)
const loadAlbums = async () => {
  try {
    await getAllAlbumsByArtistId(pathId, true, new PaginationRequestParameter(), [])
    albumLoaded.value = true
  } catch (error) {
    console.error('Error loading artist with details:', error)
    albumLoaded.value = true // Continuer même en cas d'erreur
  }
}
const { artistGroupMembers, getAllArtistGroupMembersByArtistId } = useArtistGroupMembers()
const artistGroupMemberLoaded = ref(false)
const loadArtistGroupMembers = async () => {
  try {
    await getAllArtistGroupMembersByArtistId(pathId, true, new PaginationRequestParameter(), [])
    artistGroupMemberLoaded.value = true
  } catch (error) {
    console.error('Error loading artist with details:', error)
    artistGroupMemberLoaded.value = true // Continuer même en cas d'erreur
  }
}
const { concerts, getAllConcertsByArtistId } = useConcerts()
const concertLoaded = ref(false)
const loadConcerts = async () => {
  try {
    await getAllConcertsByArtistId(pathId, true, new PaginationRequestParameter(), [])
    concertLoaded.value = true
  } catch (error) {
    console.error('Error loading artist with details:', error)
    concertLoaded.value = true // Continuer même en cas d'erreur
  }
}
const { contentSubmissions, getAllContentSubmissionsByArtistId } = useContentSubmissions()
const contentSubmissionLoaded = ref(false)
const loadContentSubmissions = async () => {
  try {
    await getAllContentSubmissionsByArtistId(pathId, true, new PaginationRequestParameter(), [])
    contentSubmissionLoaded.value = true
  } catch (error) {
    console.error('Error loading artist with details:', error)
    contentSubmissionLoaded.value = true // Continuer même en cas d'erreur
  }
}
const { editorialPlaylists, getAllEditorialPlaylistsByArtistId } = useEditorialPlaylists()
const editorialPlaylistLoaded = ref(false)
const loadEditorialPlaylists = async () => {
  try {
    await getAllEditorialPlaylistsByArtistId(pathId, true, new PaginationRequestParameter(), [])
    editorialPlaylistLoaded.value = true
  } catch (error) {
    console.error('Error loading artist with details:', error)
    editorialPlaylistLoaded.value = true // Continuer même en cas d'erreur
  }
}
const { events, getAllEventsByArtistId } = useEvents()
const eventLoaded = ref(false)
const loadEvents = async () => {
  try {
    await getAllEventsByArtistId(pathId, true, new PaginationRequestParameter(), [])
    eventLoaded.value = true
  } catch (error) {
    console.error('Error loading artist with details:', error)
    eventLoaded.value = true // Continuer même en cas d'erreur
  }
}
const { follows, getAllFollowsByArtistId } = useFollows()
const followLoaded = ref(false)
const loadFollows = async () => {
  try {
    await getAllFollowsByArtistId(pathId, true, new PaginationRequestParameter(), [])
    followLoaded.value = true
  } catch (error) {
    console.error('Error loading artist with details:', error)
    followLoaded.value = true // Continuer même en cas d'erreur
  }
}
const { songs, getAllSongsByArtistId } = useSongs()
const songLoaded = ref(false)
const loadSongs = async () => {
  try {
    await getAllSongsByArtistId(pathId, true, new PaginationRequestParameter(), [])
    songLoaded.value = true
  } catch (error) {
    console.error('Error loading artist with details:', error)
    songLoaded.value = true // Continuer même en cas d'erreur
  }
}

const violations = ref<
  | {
      artist: Record<string, string>
      albums: Record<string, string>[]
      artistGroupMembers: Record<string, string>[]
      concerts: Record<string, string>[]
      contentSubmissions: Record<string, string>[]
      editorialPlaylists: Record<string, string>[]
      events: Record<string, string>[]
      follows: Record<string, string>[]
      songs: Record<string, string>[]
    }
  | undefined
>(undefined)

const updateHandler = async (payload: {
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
  freezeStore.freeze('Updating artist ' + pathId + ' ...')
  message.value = null
  violations.value = {
    artist: {},
    albums: [],
    artistGroupMembers: [],
    concerts: [],
    contentSubmissions: [],
    editorialPlaylists: [],
    events: [],
    follows: [],
    songs: [],
  }
  try {
    const { data, errors } = await updateArtist(pathId, payload)

    if (errors && Object.keys(errors).length > 0) {
      // Gérer les erreurs de validation
      violations.value = errors as {
        artist: Record<string, string>
        albums: Record<string, string>[]
        artistGroupMembers: Record<string, string>[]
        concerts: Record<string, string>[]
        contentSubmissions: Record<string, string>[]
        editorialPlaylists: Record<string, string>[]
        events: Record<string, string>[]
        follows: Record<string, string>[]
        songs: Record<string, string>[]
      }
    }
    if (data && !message.value) {
      viewArtist(data)
    } else {
      throw new Error(String(message.value))
    }
  } catch (error: unknown) {
    console.error(error)
  } finally {
    freezeStore.unfreeze()
  }
}

const cancelHandler = () => {
  if (entity.value) {
    viewArtist(entity.value)
  }
}

onMounted(async () => {
  const result = await getArtistById(pathId)
  if (result.data) {
    entity.value = result.data
    await loadAlbums()
    await loadArtistGroupMembers()
    await loadConcerts()
    await loadContentSubmissions()
    await loadEditorialPlaylists()
    await loadEvents()
    await loadFollows()
    await loadSongs()
  } else {
    openPopup()
  }
})
</script>
<style scoped></style>
