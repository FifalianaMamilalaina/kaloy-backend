<template>
  <div class="card bg-base-100">
    <div class="card-body">
      <form @submit.prevent="handleSubmit" class="grid grid-cols-1 w-7/12 gap-4 mx-auto">
        <!-- <form @submit.prevent="handleSubmit" class=""> -->
        <div class="grid grid-cols-1 gap-4">
          <!-- Userid users FK -->
          <GenesisSelectSearchCriteria
            v-if="useridUsersSearchField?.multicriteriaSelect"
            label="Userid users"
            key="artistUseridUsers"
            :search-function="useridUsersSearchField?.multicriteriaSelect.searchFunction"
            :filters="useridUsersSearchField?.multicriteriaSelect.filters"
            :default-value="useridUsersDefaultValue"
            :violation="artistViolations ? artistViolations['useridUsers'] : undefined"
            @option-selected="(selectedValue) => (formModel.useridUsers = String(selectedValue))"
          />

          <!-- Artisttypeid artist types FK -->
          <GenesisSelectSearchCriteria
            v-if="artisttypeidArtistTypesSearchField?.multicriteriaSelect"
            label="Artisttypeid artist types"
            key="artistArtisttypeidArtistTypes"
            :search-function="
              artisttypeidArtistTypesSearchField?.multicriteriaSelect.searchFunction
            "
            :filters="artisttypeidArtistTypesSearchField?.multicriteriaSelect.filters"
            :default-value="artisttypeidArtistTypesDefaultValue"
            :violation="artistViolations ? artistViolations['artisttypeidArtistTypes'] : undefined"
            @option-selected="
              (selectedValue) => (formModel.artisttypeidArtistTypes = String(selectedValue))
            "
          />

          <!-- Stage name -->
          <GenesisInput
            label="Stage name"
            :violation="artistViolations ? artistViolations['stageName'] : undefined"
            placeholder="Enter Stage name"
            type="text"
            v-model="formModel.stageName"
            :value="formModel.stageName"
          />

          <!-- Active since year -->
          <GenesisInput
            label="Active since year"
            :violation="artistViolations ? artistViolations['activeSinceYear'] : undefined"
            placeholder="Enter Active since year"
            type="number"
            v-model="formModel.activeSinceYear"
            :value="formModel.activeSinceYear"
          />

          <!-- Photo url -->
          <GenesisInput
            label="Photo url"
            :violation="artistViolations ? artistViolations['photoUrl'] : undefined"
            placeholder="Enter Photo url"
            type="text"
            v-model="formModel.photoUrl"
            :value="formModel.photoUrl"
          />

          <!-- Bio -->
          <GenesisInput
            label="Bio"
            :violation="artistViolations ? artistViolations['bio'] : undefined"
            placeholder="Enter Bio"
            type="text"
            v-model="formModel.bio"
            :value="formModel.bio"
          />

          <!-- Verificationstatusid verification statuses FK -->
          <GenesisSelectSearchCriteria
            v-if="verificationstatusidVerificationStatusesSearchField?.multicriteriaSelect"
            label="Verificationstatusid verification statuses"
            key="artistVerificationstatusidVerificationStatuses"
            :search-function="
              verificationstatusidVerificationStatusesSearchField?.multicriteriaSelect
                .searchFunction
            "
            :filters="
              verificationstatusidVerificationStatusesSearchField?.multicriteriaSelect.filters
            "
            :default-value="verificationstatusidVerificationStatusesDefaultValue"
            :violation="
              artistViolations
                ? artistViolations['verificationstatusidVerificationStatuses']
                : undefined
            "
            @option-selected="
              (selectedValue) =>
                (formModel.verificationstatusidVerificationStatuses = String(selectedValue))
            "
          />

          <!-- Verified at -->
          <GenesisInput
            label="Verified at"
            :violation="artistViolations ? artistViolations['verifiedAt'] : undefined"
            placeholder="Enter Verified at"
            type="datetime-local"
            v-model="formModel.verifiedAt"
            :value="formModel.verifiedAt"
          />

          <!-- Is certified -->
          <label class="flex items-center gap-2">
            <input type="checkbox" class="checkbox" v-model="formModel.isCertified" />
            <span>Is certified</span>
          </label>

          <!-- Created at -->
          <GenesisInput
            label="Created at"
            :violation="artistViolations ? artistViolations['createdAt'] : undefined"
            placeholder="Enter Created at"
            type="datetime-local"
            v-model="formModel.createdAt"
            :value="formModel.createdAt"
          />
        </div>
        <AlbumTableForm
          :initial-data="albumsData"
          :get-table-data-callback="setAlbums"
          :violations="albumViolations"
          class="w-full"
        />
        <ArtistGroupMemberTableForm
          :initial-data="artistGroupMembersData"
          :get-table-data-callback="setArtistGroupMembers"
          :violations="artistGroupMemberViolations"
          class="w-full"
        />
        <ConcertTableForm
          :initial-data="concertsData"
          :get-table-data-callback="setConcerts"
          :violations="concertViolations"
          class="w-full"
        />
        <ContentSubmissionTableForm
          :initial-data="contentSubmissionsData"
          :get-table-data-callback="setContentSubmissions"
          :violations="contentSubmissionViolations"
          class="w-full"
        />
        <EditorialPlaylistTableForm
          :initial-data="editorialPlaylistsData"
          :get-table-data-callback="setEditorialPlaylists"
          :violations="editorialPlaylistViolations"
          class="w-full"
        />
        <EventTableForm
          :initial-data="eventsData"
          :get-table-data-callback="setEvents"
          :violations="eventViolations"
          class="w-full"
        />
        <FollowTableForm
          :initial-data="followsData"
          :get-table-data-callback="setFollows"
          :violations="followViolations"
          class="w-full"
        />
        <SongTableForm
          :initial-data="songsData"
          :get-table-data-callback="setSongs"
          :violations="songViolations"
          class="w-full"
        />

        <!-- Action buttons -->
        <div class="flex justify-end gap-3 mt-6">
          <GenesisButton
            type="submit"
            class="btn btn-primary text-primary-content"
            :label="submitLabel"
          />
          <GenesisButton
            @click="cancelForm"
            class="btn btn-outline btn-error"
            :label="$t('button.cancel')"
          />
        </div>
      </form>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { Artist, ArtistFormDTO } from '@/models/ArtistModel.ts'
import GenesisButton from '@/components/button/GenesisButton.vue'
import GenesisInput from '@/components/form/GenesisInput.vue'
import GenesisSelectSearchCriteria from '@/components/form/GenesisSelectSearchCriteria.vue'
import { AlbumFormDTO, type Album } from '@/models/AlbumModel.ts'
import AlbumTableForm from '@/entities/album/AlbumTableForm.vue'
import {
  ArtistGroupMemberFormDTO,
  type ArtistGroupMember,
} from '@/models/ArtistGroupMemberModel.ts'
import ArtistGroupMemberTableForm from '@/entities/artistGroupMember/ArtistGroupMemberTableForm.vue'
import { ConcertFormDTO, type Concert } from '@/models/ConcertModel.ts'
import ConcertTableForm from '@/entities/concert/ConcertTableForm.vue'
import {
  ContentSubmissionFormDTO,
  type ContentSubmission,
} from '@/models/ContentSubmissionModel.ts'
import ContentSubmissionTableForm from '@/entities/contentSubmission/ContentSubmissionTableForm.vue'
import {
  EditorialPlaylistFormDTO,
  type EditorialPlaylist,
} from '@/models/EditorialPlaylistModel.ts'
import EditorialPlaylistTableForm from '@/entities/editorialPlaylist/EditorialPlaylistTableForm.vue'
import { EventFormDTO, type Event } from '@/models/EventModel.ts'
import EventTableForm from '@/entities/event/EventTableForm.vue'
import { FollowFormDTO, type Follow } from '@/models/FollowModel.ts'
import FollowTableForm from '@/entities/follow/FollowTableForm.vue'
import { SongFormDTO, type Song } from '@/models/SongModel.ts'
import SongTableForm from '@/entities/song/SongTableForm.vue'

const props = defineProps<{
  artist?: Artist

  albumsData?: Album[]
  artistGroupMembersData?: ArtistGroupMember[]
  concertsData?: Concert[]
  contentSubmissionsData?: ContentSubmission[]
  editorialPlaylistsData?: EditorialPlaylist[]
  eventsData?: Event[]
  followsData?: Follow[]
  songsData?: Song[]

  violations?: {
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

  submitLabel?: string
}>()

const emit = defineEmits<{
  (
    e: 'submit',
    payload: {
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
  ): void
  (e: 'cancel', payload: Partial<ArtistFormDTO>): void
}>()

const artistViolations = computed(() => (props.violations ? props.violations.artist : {}))
const albumViolations = computed(() => (props.violations ? props.violations.albums : []))
const artistGroupMemberViolations = computed(() =>
  props.violations ? props.violations.artistGroupMembers : [],
)
const concertViolations = computed(() => (props.violations ? props.violations.concerts : []))
const contentSubmissionViolations = computed(() =>
  props.violations ? props.violations.contentSubmissions : [],
)
const editorialPlaylistViolations = computed(() =>
  props.violations ? props.violations.editorialPlaylists : [],
)
const eventViolations = computed(() => (props.violations ? props.violations.events : []))
const followViolations = computed(() => (props.violations ? props.violations.follows : []))
const songViolations = computed(() => (props.violations ? props.violations.songs : []))

const formModel = ref<Partial<ArtistFormDTO>>({ ...ArtistFormDTO.parse(props.artist) })
const useridUsersSearchField = Artist.getSearchFieldByKey('useridUsers')
const useridUsersDefaultValue = props.artist
  ? (props.artist?.useridUsers?.getKeyValue?.() ?? undefined)
  : undefined
const artisttypeidArtistTypesSearchField = Artist.getSearchFieldByKey('artisttypeidArtistTypes')
const artisttypeidArtistTypesDefaultValue = props.artist
  ? (props.artist?.artisttypeidArtistTypes?.getKeyValue?.() ?? undefined)
  : undefined
const verificationstatusidVerificationStatusesSearchField = Artist.getSearchFieldByKey(
  'verificationstatusidVerificationStatuses',
)
const verificationstatusidVerificationStatusesDefaultValue = props.artist
  ? (props.artist?.verificationstatusidVerificationStatuses?.getKeyValue?.() ?? undefined)
  : undefined

const albums = ref<AlbumFormDTO[]>([])
const setAlbums = (data: AlbumFormDTO[]) => {
  albums.value = data
}
const artistGroupMembers = ref<ArtistGroupMemberFormDTO[]>([])
const setArtistGroupMembers = (data: ArtistGroupMemberFormDTO[]) => {
  artistGroupMembers.value = data
}
const concerts = ref<ConcertFormDTO[]>([])
const setConcerts = (data: ConcertFormDTO[]) => {
  concerts.value = data
}
const contentSubmissions = ref<ContentSubmissionFormDTO[]>([])
const setContentSubmissions = (data: ContentSubmissionFormDTO[]) => {
  contentSubmissions.value = data
}
const editorialPlaylists = ref<EditorialPlaylistFormDTO[]>([])
const setEditorialPlaylists = (data: EditorialPlaylistFormDTO[]) => {
  editorialPlaylists.value = data
}
const events = ref<EventFormDTO[]>([])
const setEvents = (data: EventFormDTO[]) => {
  events.value = data
}
const follows = ref<FollowFormDTO[]>([])
const setFollows = (data: FollowFormDTO[]) => {
  follows.value = data
}
const songs = ref<SongFormDTO[]>([])
const setSongs = (data: SongFormDTO[]) => {
  songs.value = data
}

const fileToBase64 = (file: File): Promise<string> => {
  return new Promise((resolve, reject) => {
    const reader = new FileReader()
    reader.onload = () => {
      if (typeof reader.result !== 'string') {
        reject(new Error('Unable to read the selected file'))
        return
      }
      const commaIndex = reader.result.indexOf(',')
      resolve(commaIndex >= 0 ? reader.result.substring(commaIndex + 1) : reader.result)
    }
    reader.onerror = () => {
      reject(reader.error ?? new Error('Unable to read the selected file'))
    }
    reader.readAsDataURL(file)
  })
}
async function handleSubmit() {
  const data: any = { ...formModel.value }
  Object.keys(data).forEach((key) => {
    if (data[key] === '' || data[key] === null || data[key] === undefined) {
      delete data[key]
    }
  })

  emit('submit', {
    artist: data,
    albums: albums.value,
    artistGroupMembers: artistGroupMembers.value,
    concerts: concerts.value,
    contentSubmissions: contentSubmissions.value,
    editorialPlaylists: editorialPlaylists.value,
    events: events.value,
    follows: follows.value,
    songs: songs.value,
  })
}

function cancelForm() {
  emit('cancel', formModel.value)
}
onMounted(async () => {
  if (Array.isArray(props.albumsData) && props.albumsData.length > 0) {
    setAlbums(AlbumFormDTO.parseList(props.albumsData))
  }
  if (Array.isArray(props.artistGroupMembersData) && props.artistGroupMembersData.length > 0) {
    setArtistGroupMembers(ArtistGroupMemberFormDTO.parseList(props.artistGroupMembersData))
  }
  if (Array.isArray(props.concertsData) && props.concertsData.length > 0) {
    setConcerts(ConcertFormDTO.parseList(props.concertsData))
  }
  if (Array.isArray(props.contentSubmissionsData) && props.contentSubmissionsData.length > 0) {
    setContentSubmissions(ContentSubmissionFormDTO.parseList(props.contentSubmissionsData))
  }
  if (Array.isArray(props.editorialPlaylistsData) && props.editorialPlaylistsData.length > 0) {
    setEditorialPlaylists(EditorialPlaylistFormDTO.parseList(props.editorialPlaylistsData))
  }
  if (Array.isArray(props.eventsData) && props.eventsData.length > 0) {
    setEvents(EventFormDTO.parseList(props.eventsData))
  }
  if (Array.isArray(props.followsData) && props.followsData.length > 0) {
    setFollows(FollowFormDTO.parseList(props.followsData))
  }
  if (Array.isArray(props.songsData) && props.songsData.length > 0) {
    setSongs(SongFormDTO.parseList(props.songsData))
  }
})
</script>
<style scoped></style>
