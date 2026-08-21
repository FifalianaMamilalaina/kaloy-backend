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
            key="listeningHistoryUseridUsers"
            :search-function="useridUsersSearchField?.multicriteriaSelect.searchFunction"
            :filters="useridUsersSearchField?.multicriteriaSelect.filters"
            :default-value="useridUsersDefaultValue"
            :violation="violations ? violations['useridUsers'] : undefined"
            @option-selected="(selectedValue) => (formModel.useridUsers = String(selectedValue))"
          />

          <!-- Songid songs FK -->
          <GenesisSelectSearchCriteria
            v-if="songidSongsSearchField?.multicriteriaSelect"
            label="Songid songs"
            key="listeningHistorySongidSongs"
            :search-function="songidSongsSearchField?.multicriteriaSelect.searchFunction"
            :filters="songidSongsSearchField?.multicriteriaSelect.filters"
            :default-value="songidSongsDefaultValue"
            :violation="violations ? violations['songidSongs'] : undefined"
            @option-selected="(selectedValue) => (formModel.songidSongs = String(selectedValue))"
          />

          <!-- Playmodeid play modes FK -->
          <GenesisSelectSearchCriteria
            v-if="playmodeidPlayModesSearchField?.multicriteriaSelect"
            label="Playmodeid play modes"
            key="listeningHistoryPlaymodeidPlayModes"
            :search-function="playmodeidPlayModesSearchField?.multicriteriaSelect.searchFunction"
            :filters="playmodeidPlayModesSearchField?.multicriteriaSelect.filters"
            :default-value="playmodeidPlayModesDefaultValue"
            :violation="violations ? violations['playmodeidPlayModes'] : undefined"
            @option-selected="
              (selectedValue) => (formModel.playmodeidPlayModes = String(selectedValue))
            "
          />

          <!-- Listened at -->
          <GenesisInput
            label="Listened at"
            :violation="violations ? violations['listenedAt'] : undefined"
            placeholder="Enter Listened at"
            type="datetime-local"
            v-model="formModel.listenedAt"
            :value="formModel.listenedAt"
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
import { ListeningHistory, ListeningHistoryFormDTO } from '@/models/ListeningHistoryModel.ts'
import GenesisButton from '@/components/button/GenesisButton.vue'
import GenesisInput from '@/components/form/GenesisInput.vue'
import GenesisSelectSearchCriteria from '@/components/form/GenesisSelectSearchCriteria.vue'

const props = defineProps<{
  listeningHistory?: ListeningHistory
  violations?: Record<string, string> | null
  submitLabel?: string
}>()

const emit = defineEmits<{
  (e: 'submit', payload: Partial<ListeningHistoryFormDTO>): void
  (e: 'cancel', payload: Partial<ListeningHistoryFormDTO>): void
}>()

const formModel = ref<Partial<ListeningHistoryFormDTO>>({
  ...ListeningHistoryFormDTO.parse(props.listeningHistory),
})
const useridUsersSearchField = ListeningHistory.getSearchFieldByKey('useridUsers')
const useridUsersDefaultValue = props.listeningHistory
  ? (props.listeningHistory?.useridUsers?.getKeyValue?.() ?? undefined)
  : undefined
const songidSongsSearchField = ListeningHistory.getSearchFieldByKey('songidSongs')
const songidSongsDefaultValue = props.listeningHistory
  ? (props.listeningHistory?.songidSongs?.getKeyValue?.() ?? undefined)
  : undefined
const playmodeidPlayModesSearchField = ListeningHistory.getSearchFieldByKey('playmodeidPlayModes')
const playmodeidPlayModesDefaultValue = props.listeningHistory
  ? (props.listeningHistory?.playmodeidPlayModes?.getKeyValue?.() ?? undefined)
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
