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
            key="downloadUseridUsers"
            :search-function="useridUsersSearchField?.multicriteriaSelect.searchFunction"
            :filters="useridUsersSearchField?.multicriteriaSelect.filters"
            :default-value="useridUsersDefaultValue"
            :violation="violations ? violations['useridUsers'] : undefined"
            @option-selected="(selectedValue) => (formModel.useridUsers = String(selectedValue))"
          />

          <!-- Playlistid playlists FK -->
          <GenesisSelectSearchCriteria
            v-if="playlistidPlaylistsSearchField?.multicriteriaSelect"
            label="Playlistid playlists"
            key="downloadPlaylistidPlaylists"
            :search-function="playlistidPlaylistsSearchField?.multicriteriaSelect.searchFunction"
            :filters="playlistidPlaylistsSearchField?.multicriteriaSelect.filters"
            :default-value="playlistidPlaylistsDefaultValue"
            :violation="violations ? violations['playlistidPlaylists'] : undefined"
            @option-selected="
              (selectedValue) => (formModel.playlistidPlaylists = String(selectedValue))
            "
          />

          <!-- Downloaded at -->
          <GenesisInput
            label="Downloaded at"
            :violation="violations ? violations['downloadedAt'] : undefined"
            placeholder="Enter Downloaded at"
            type="datetime-local"
            v-model="formModel.downloadedAt"
            :value="formModel.downloadedAt"
          />

          <!-- Expires at -->
          <GenesisInput
            label="Expires at"
            :violation="violations ? violations['expiresAt'] : undefined"
            placeholder="Enter Expires at"
            type="datetime-local"
            v-model="formModel.expiresAt"
            :value="formModel.expiresAt"
          />
        </div>

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
import { ref } from 'vue'
import { Download, DownloadFormDTO } from '@/models/DownloadModel.ts'
import GenesisButton from '@/components/button/GenesisButton.vue'
import GenesisInput from '@/components/form/GenesisInput.vue'
import GenesisSelectSearchCriteria from '@/components/form/GenesisSelectSearchCriteria.vue'

const props = defineProps<{
  download?: Download
  violations?: Record<string, string> | null
  submitLabel?: string
}>()

const emit = defineEmits<{
  (e: 'submit', payload: Partial<DownloadFormDTO>): void
  (e: 'cancel', payload: Partial<DownloadFormDTO>): void
}>()

const formModel = ref<Partial<DownloadFormDTO>>({ ...DownloadFormDTO.parse(props.download) })
const useridUsersSearchField = Download.getSearchFieldByKey('useridUsers')
const useridUsersDefaultValue = props.download
  ? (props.download?.useridUsers?.getKeyValue?.() ?? undefined)
  : undefined
const playlistidPlaylistsSearchField = Download.getSearchFieldByKey('playlistidPlaylists')
const playlistidPlaylistsDefaultValue = props.download
  ? (props.download?.playlistidPlaylists?.getKeyValue?.() ?? undefined)
  : undefined

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

  emit('submit', data)
}

function cancelForm() {
  emit('cancel', formModel.value)
}
</script>
<style scoped></style>
