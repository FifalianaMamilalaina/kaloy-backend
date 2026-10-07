<template>
  <div class="w-full">
    <div class="flex items-center justify-between mb-6">
      <h3 class="text-xl font-semibold text-base-content">
        {{ $t('entity.nav', { entity: 'Artist' }) }}
        <span class="text-base-content/50 font-normal">{{ $t('entity.create.nav') }}</span>
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

    <artist-form
      :submit-label="$t('entity.create.submitLabel', { entity: 'Artist' })"
      :albumsData="initialAlbums"
      :artistGroupMembersData="initialArtistGroupMembers"
      :concertsData="initialConcerts"
      :contentSubmissionsData="initialContentSubmissions"
      :editorialPlaylistsData="initialEditorialPlaylists"
      :eventsData="initialEvents"
      :followsData="initialFollows"
      :songsData="initialSongs"
      :violations="violations"
      @submit="createHandler"
      @cancel="goToListView"
    />

    <AlertPopup
      :message="message ?? undefined"
      title="Error"
      :visible="alertPopup"
      @close="closePopup"
    />
  </div>
</template>

<script setup lang="ts">
import ArtistForm from '@/entities/artist/ArtistForm.vue'
import AlertPopup from '@/components/popup/AlertPopup.vue'
import GenesisButton from '@/components/button/GenesisButton.vue'
import LeftArrowIcon from '@/components/icons/LeftArrowIcon.vue'
import { ArtistFormDTO } from '@/models/ArtistModel'
import { useArtists } from '@/composables/useArtists'
import { usePopup } from '@/composables/usePopup'
import { useFreezeScreenStore } from '@/stores/useFreezeScreenStore'
import { ref } from 'vue'
import type { Album, AlbumFormDTO } from '@/models/AlbumModel'
import type { ArtistGroupMember, ArtistGroupMemberFormDTO } from '@/models/ArtistGroupMemberModel'
import type { Concert, ConcertFormDTO } from '@/models/ConcertModel'
import type { ContentSubmission, ContentSubmissionFormDTO } from '@/models/ContentSubmissionModel'
import type { EditorialPlaylist, EditorialPlaylistFormDTO } from '@/models/EditorialPlaylistModel'
import type { Event, EventFormDTO } from '@/models/EventModel'
import type { Follow, FollowFormDTO } from '@/models/FollowModel'
import type { Song, SongFormDTO } from '@/models/SongModel'

const { createArtist, goToListView, message, viewArtist } = useArtists()
const { openPopup, closePopup, visible: alertPopup } = usePopup()
const freezeStore = useFreezeScreenStore()
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
const initialAlbums = ref<Album[]>([])
const initialArtistGroupMembers = ref<ArtistGroupMember[]>([])
const initialConcerts = ref<Concert[]>([])
const initialContentSubmissions = ref<ContentSubmission[]>([])
const initialEditorialPlaylists = ref<EditorialPlaylist[]>([])
const initialEvents = ref<Event[]>([])
const initialFollows = ref<Follow[]>([])
const initialSongs = ref<Song[]>([])

const createHandler = async (payload: {
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
  freezeStore.freeze('Creating a new Artist ...')
  violations.value = undefined
  message.value = null
  try {
    const { data, errors } = await createArtist(payload)
    if (errors && Object.keys(errors).length > 0) {
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
    console.error('Error creating artist:', error)
    openPopup()
  } finally {
    freezeStore.unfreeze()
  }
}
</script>
<style scoped></style>
