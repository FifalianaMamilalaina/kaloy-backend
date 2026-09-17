<template>
  <div class="card bg-base-100">
    <div class="card-body">
      <div v-if="song">
        <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
          <div>
            <label for="songId" class="block text-sm font-medium mb-1">Id</label>
            <div id="songId" class="input w-full bg-base-100 cursor-default">
              <span>{{ song.id ? $n(song.id, 'decimal') : '--' }}</span>
            </div>
          </div>
          <div>
            <label for="songArtistidArtists" class="block text-sm font-medium mb-1">Artist</label>
            <div id="songArtistidArtists" class="input w-full bg-base-100 cursor-default">
              <span>{{ song.artistidArtists?.getKeyValue() ?? '' }}</span>
            </div>
          </div>
          <div>
            <label for="songAlbumidAlbums" class="block text-sm font-medium mb-1">Album</label>
            <div id="songAlbumidAlbums" class="input w-full bg-base-100 cursor-default">
              <span>{{ song.albumidAlbums?.getKeyValue() ?? '' }}</span>
            </div>
          </div>
          <div>
            <label for="songTitle" class="block text-sm font-medium mb-1">Title</label>
            <div id="songTitle" class="input w-full bg-base-100 cursor-default">
              <span>{{ song.title ?? '' }}</span>
            </div>
          </div>
          <div>
            <label for="songDurationSeconds" class="block text-sm font-medium mb-1"
              >Duration seconds</label
            >
            <div id="songDurationSeconds" class="input w-full bg-base-100 cursor-default">
              <span>{{ song.durationSeconds ? $n(song.durationSeconds, 'decimal') : '--' }}</span>
            </div>
          </div>
          <div>
            <label for="songReleaseDate" class="block text-sm font-medium mb-1">Release date</label>
            <div id="songReleaseDate" class="input w-full bg-base-100 cursor-default">
              <span>{{ song.releaseDate ? $d(song.releaseDate, 'long') : '--/--/--' }}</span>
            </div>
          </div>
          <div>
            <label for="songLanguage" class="block text-sm font-medium mb-1">Language</label>
            <div id="songLanguage" class="input w-full bg-base-100 cursor-default">
              <span>{{ song.language ?? '' }}</span>
            </div>
          </div>
          <div>
            <label for="songAuthorComposer" class="block text-sm font-medium mb-1"
              >Author composer</label
            >
            <div id="songAuthorComposer" class="input w-full bg-base-100 cursor-default">
              <span>{{ song.authorComposer ?? '' }}</span>
            </div>
          </div>
          <div>
            <label for="songMusicalArranger" class="block text-sm font-medium mb-1"
              >Musical arranger</label
            >
            <div id="songMusicalArranger" class="input w-full bg-base-100 cursor-default">
              <span>{{ song.musicalArranger ?? '' }}</span>
            </div>
          </div>
          <div>
            <label for="songRecordingLocation" class="block text-sm font-medium mb-1"
              >Recording location</label
            >
            <div id="songRecordingLocation" class="input w-full bg-base-100 cursor-default">
              <span>{{ song.recordingLocation ?? '' }}</span>
            </div>
          </div>
          <div>
            <label for="songRecordingDate" class="block text-sm font-medium mb-1"
              >Recording date</label
            >
            <div id="songRecordingDate" class="input w-full bg-base-100 cursor-default">
              <span>{{ song.recordingDate ? $d(song.recordingDate, 'long') : '--/--/--' }}</span>
            </div>
          </div>
          <div>
            <label for="songStoragetypeidAudioStorageTypes" class="block text-sm font-medium mb-1"
              >Audio storage type</label
            >
            <div
              id="songStoragetypeidAudioStorageTypes"
              class="input w-full bg-base-100 cursor-default"
            >
              <span>{{ song.storagetypeidAudioStorageTypes?.getKeyValue() ?? '' }}</span>
            </div>
          </div>
          <div>
            <label for="songAudioUrl" class="block text-sm font-medium mb-1">Audio url</label>
            <div id="songAudioUrl" class="input w-full bg-base-100 cursor-default">
              <span>{{ song.audioUrl ?? '' }}</span>
            </div>
          </div>
          <div>
            <label for="songAudioFile" class="block text-sm font-medium mb-1">Audio file</label>
            <div id="songAudioFile" class="input w-full bg-base-100 cursor-default">
              <img :src="getUrl(song.audioFile)" alt="Audio file" class="max-h-full max-w-full" />
            </div>
          </div>
          <div>
            <label for="songVideoUrl" class="block text-sm font-medium mb-1">Video url</label>
            <div id="songVideoUrl" class="input w-full bg-base-100 cursor-default">
              <span>{{ song.videoUrl ?? '' }}</span>
            </div>
          </div>
          <div>
            <label for="songKaraokeAudio" class="block text-sm font-medium mb-1"
              >Karaoke audio</label
            >
            <div id="songKaraokeAudio" class="input w-full bg-base-100 cursor-default">
              <img
                :src="getUrl(song.karaokeAudio)"
                alt="Karaoke audio"
                class="max-h-full max-w-full"
              />
            </div>
          </div>
          <div>
            <label for="songLyrics" class="block text-sm font-medium mb-1">Lyrics</label>
            <div id="songLyrics" class="input w-full bg-base-100 cursor-default">
              <span>{{ song.lyrics ?? '' }}</span>
            </div>
          </div>
          <div>
            <label for="songLyricsSyncData" class="block text-sm font-medium mb-1"
              >Lyrics sync data</label
            >
            <div id="songLyricsSyncData" class="input w-full bg-base-100 cursor-default">
              <span>{{ song.lyricsSyncData ?? '' }}</span>
            </div>
          </div>
          <div>
            <label for="songSolfa" class="block text-sm font-medium mb-1">Solfa</label>
            <div id="songSolfa" class="input w-full bg-base-100 cursor-default">
              <span>{{ song.solfa ?? '' }}</span>
            </div>
          </div>
          <div>
            <label for="songPlayback" class="block text-sm font-medium mb-1">Playback</label>
            <div id="songPlayback" class="input w-full bg-base-100 cursor-default">
              <img :src="getUrl(song.playback)" alt="Playback" class="max-h-full max-w-full" />
            </div>
          </div>
          <div>
            <label for="songIsDownloadable" class="block text-sm font-medium mb-1"
              >Is downloadable</label
            >
            <div id="songIsDownloadable" class="input w-full bg-base-100 cursor-default">
              <span>{{ song.isDownloadable ?? '' }}</span>
            </div>
          </div>
          <div>
            <label for="songCreatedAt" class="block text-sm font-medium mb-1">Created at</label>
            <div id="songCreatedAt" class="input w-full bg-base-100 cursor-default">
              <span>{{ song.createdAt ?? '' }}</span>
            </div>
          </div>
        </div>
        <div class="flex justify-end gap-3 mt-6">
          <GenesisButton @click="goToUpdateFormView(song)" class="btn btn-outline btn-neutral">
            <EditIcon class="mr-2" />
            <span>{{ $t('button.update') }}</span>
          </GenesisButton>
          <GenesisButton
            @click="openDeletePopup(song)"
            class="btn-outline btn-error hover:text-white"
          >
            <TrashIcon class="mr-2" />
            <span>{{ $t('button.delete') }}</span>
          </GenesisButton>
        </div>
      </div>
    </div>
  </div>
  <DeleteConfirmationPopup
    :visible="deletePopup"
    :message="`Êtes-vous sûr de vouloir supprimer Song song?.id ?`"
    subMessage="Cette action est irréversible."
    @confirm="confirmDelete"
    @cancel="closePopup"
  />
</template>

<script setup lang="ts">
import type { PropType } from 'vue'
import { getUrl } from '@/composables/useImage'
import type { Song } from '@/models/SongModel'
import GenesisButton from '@/components/button/GenesisButton.vue'
import { usePopup } from '@/composables/usePopup'
import { useSongs } from '@/composables/useSongs'
import DeleteConfirmationPopup from '@/components/popup/DeleteConfirmationPopup.vue'
import TrashIcon from '@/components/icons/TrashIcon.vue'
import EditIcon from '@/components/icons/EditIcon.vue'
import { useDateFormat } from '@/composables/useDateFormat'

const props = defineProps({
  song: {
    type: Object as PropType<Song>,
    required: true,
  },
})
// Stores
const { closePopup, openPopup, visible: deletePopup } = usePopup(false)
const { deleteSong, goToListView, goToUpdateFormView } = useSongs()

// Methods
const openDeletePopup = (entity: Song) => {
  if (!entity) return
  openPopup()
}

const confirmDelete = async () => {
  const { success, error } = await deleteSong(props.song)
  if (!success) console.error(error)
  closePopup()
  goToListView()
}

const { formatDate } = useDateFormat()
</script>
<style scoped></style>
