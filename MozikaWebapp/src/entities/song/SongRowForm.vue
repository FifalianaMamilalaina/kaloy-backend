<script setup lang="ts">
import GenesisSelectSearchCriteria from '@/components/form/GenesisSelectSearchCriteria.vue'
import GenesisInput from '@/components/form/GenesisInput.vue'
import { ref, onMounted } from 'vue'
import { Song, SongFormDTO } from '@/models/SongModel'
import TrashIcon from '@/components/icons/TrashIcon.vue'

const props = defineProps<{
  internalId: number
  song: Song
  violations?: Record<string, string> | null
}>()

const emit = defineEmits<{
  (e: 'request:remove'): void
  (e: 'udpate:model-value', value: SongFormDTO): void
}>()

const formModel = ref<SongFormDTO>(SongFormDTO.parse(props.song))
const storagetypeidAudioStorageTypesSearchField = Song.getSearchFieldByKey(
  'storagetypeidAudioStorageTypes',
)
const storagetypeidAudioStorageTypesDefaultValue = props.song
  ? (props.song?.storagetypeidAudioStorageTypes?.getReferenceValue?.() ?? undefined)
  : undefined

function removeRow() {
  emit('request:remove')
}

function updateModel() {
  emit('udpate:model-value', formModel.value)
}

onMounted(() => {
  updateModel()
})
</script>

<template>
  <tr>
    <td>
      {{ internalId }}
    </td>
    <td>
      <GenesisInput
        :violation="violations ? violations['title'] : undefined"
        placeholder="Enter Title"
        type="text"
        v-model="formModel.title"
        :value="formModel.title"
        @update:model-value="(newVal) => updateModel()"
      />
    </td>

    <td>
      <GenesisInput
        :violation="violations ? violations['durationSeconds'] : undefined"
        placeholder="Enter Duration seconds"
        type="number"
        v-model="formModel.durationSeconds"
        :value="formModel.durationSeconds"
        @update:model-value="(newVal) => updateModel()"
      />
    </td>

    <td>
      <GenesisInput
        :violation="violations ? violations['releaseDate'] : undefined"
        placeholder="Enter Release date"
        type="date"
        v-model="formModel.releaseDate"
        :value="formModel.releaseDate"
        @update:model-value="(newVal) => updateModel()"
      />
    </td>

    <td>
      <GenesisInput
        :violation="violations ? violations['language'] : undefined"
        placeholder="Enter Language"
        type="text"
        v-model="formModel.language"
        :value="formModel.language"
        @update:model-value="(newVal) => updateModel()"
      />
    </td>

    <td>
      <GenesisInput
        :violation="violations ? violations['authorComposer'] : undefined"
        placeholder="Enter Author composer"
        type="text"
        v-model="formModel.authorComposer"
        :value="formModel.authorComposer"
        @update:model-value="(newVal) => updateModel()"
      />
    </td>

    <td>
      <GenesisInput
        :violation="violations ? violations['musicalArranger'] : undefined"
        placeholder="Enter Musical arranger"
        type="text"
        v-model="formModel.musicalArranger"
        :value="formModel.musicalArranger"
        @update:model-value="(newVal) => updateModel()"
      />
    </td>

    <td>
      <GenesisInput
        :violation="violations ? violations['recordingLocation'] : undefined"
        placeholder="Enter Recording location"
        type="text"
        v-model="formModel.recordingLocation"
        :value="formModel.recordingLocation"
        @update:model-value="(newVal) => updateModel()"
      />
    </td>

    <td>
      <GenesisInput
        :violation="violations ? violations['recordingDate'] : undefined"
        placeholder="Enter Recording date"
        type="date"
        v-model="formModel.recordingDate"
        :value="formModel.recordingDate"
        @update:model-value="(newVal) => updateModel()"
      />
    </td>

    <td>
      <GenesisSelectSearchCriteria
        v-if="storagetypeidAudioStorageTypesSearchField?.multicriteriaSelect"
        :violation="violations ? violations['storagetypeidAudioStorageTypes'] : undefined"
        placeholder="Select Storagetypeid audio storage types"
        key="SongStoragetypeidAudioStorageTypes"
        :search-function="
          storagetypeidAudioStorageTypesSearchField?.multicriteriaSelect.searchFunction
        "
        :filters="storagetypeidAudioStorageTypesSearchField?.multicriteriaSelect.filters"
        :default-value="storagetypeidAudioStorageTypesDefaultValue"
        @option-selected="
          (selectedValue) => {
            formModel.storagetypeidAudioStorageTypes = String(selectedValue)
            updateModel()
          }
        "
      />
    </td>

    <td>
      <GenesisInput
        :violation="violations ? violations['audioUrl'] : undefined"
        placeholder="Enter Audio url"
        type="text"
        v-model="formModel.audioUrl"
        :value="formModel.audioUrl"
        @update:model-value="(newVal) => updateModel()"
      />
    </td>

    <td>
      <GenesisInput
        :violation="violations ? violations['audioFile'] : undefined"
        placeholder="Enter Audio file"
        type="file"
        v-model="formModel.audioFile"
        :value="formModel.audioFile"
        @update:model-value="(newVal) => updateModel()"
      />
    </td>

    <td>
      <GenesisInput
        :violation="violations ? violations['videoUrl'] : undefined"
        placeholder="Enter Video url"
        type="text"
        v-model="formModel.videoUrl"
        :value="formModel.videoUrl"
        @update:model-value="(newVal) => updateModel()"
      />
    </td>

    <td>
      <GenesisInput
        :violation="violations ? violations['karaokeAudio'] : undefined"
        placeholder="Enter Karaoke audio"
        type="file"
        v-model="formModel.karaokeAudio"
        :value="formModel.karaokeAudio"
        @update:model-value="(newVal) => updateModel()"
      />
    </td>

    <td>
      <GenesisInput
        :violation="violations ? violations['lyrics'] : undefined"
        placeholder="Enter Lyrics"
        type="text"
        v-model="formModel.lyrics"
        :value="formModel.lyrics"
        @update:model-value="(newVal) => updateModel()"
      />
    </td>

    <td>
      <GenesisInput
        :violation="violations ? violations['lyricsSyncData'] : undefined"
        placeholder="Enter Lyrics sync data"
        type="text"
        v-model="formModel.lyricsSyncData"
        :value="formModel.lyricsSyncData"
        @update:model-value="(newVal) => updateModel()"
      />
    </td>

    <td>
      <GenesisInput
        :violation="violations ? violations['solfa'] : undefined"
        placeholder="Enter Solfa"
        type="text"
        v-model="formModel.solfa"
        :value="formModel.solfa"
        @update:model-value="(newVal) => updateModel()"
      />
    </td>

    <td>
      <GenesisInput
        :violation="violations ? violations['playback'] : undefined"
        placeholder="Enter Playback"
        type="file"
        v-model="formModel.playback"
        :value="formModel.playback"
        @update:model-value="(newVal) => updateModel()"
      />
    </td>

    <td>
      <GenesisInput
        :violation="violations ? violations['isDownloadable'] : undefined"
        placeholder="Enter Is downloadable"
        type="checkbox"
        v-model="formModel.isDownloadable"
        :value="formModel.isDownloadable"
        @update:model-value="(newVal) => updateModel()"
      />
    </td>

    <td>
      <GenesisInput
        :violation="violations ? violations['createdAt'] : undefined"
        placeholder="Enter Created at"
        type="datetime-local"
        v-model="formModel.createdAt"
        :value="formModel.createdAt"
        @update:model-value="(newVal) => updateModel()"
      />
    </td>

    <td class="text-center">
      <button type="button" title="delete row" @click="removeRow" class="btn btn-error btn-outline">
        <TrashIcon />
      </button>
    </td>
  </tr>
</template>

<style scoped></style>
