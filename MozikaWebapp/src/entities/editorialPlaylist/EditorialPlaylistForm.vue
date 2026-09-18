<template>
  <div class="card bg-base-100">
    <div class="card-body">
      <form @submit.prevent="handleSubmit" class="grid grid-cols-1 w-7/12 gap-4 mx-auto">
        <!-- <form @submit.prevent="handleSubmit" class=""> -->
        <div class="grid grid-cols-1 gap-4">
          <!-- Artistid artists FK -->
          <GenesisSelectSearchCriteria
            v-if="artistidArtistsSearchField?.multicriteriaSelect"
            label="Artistid artists"
            key="editorialPlaylistArtistidArtists"
            :search-function="artistidArtistsSearchField?.multicriteriaSelect.searchFunction"
            :filters="artistidArtistsSearchField?.multicriteriaSelect.filters"
            :default-value="artistidArtistsDefaultValue"
            :violation="violations ? violations['artistidArtists'] : undefined"
            @option-selected="
              (selectedValue) => (formModel.artistidArtists = String(selectedValue))
            "
          />

          <!-- Title -->
          <GenesisInput
            label="Title"
            :violation="violations ? violations['title'] : undefined"
            placeholder="Enter Title"
            type="text"
            v-model="formModel.title"
            :value="formModel.title"
          />

          <!-- Description -->
          <GenesisInput
            label="Description"
            :violation="violations ? violations['description'] : undefined"
            placeholder="Enter Description"
            type="text"
            v-model="formModel.description"
            :value="formModel.description"
          />

          <!-- Cover url -->
          <GenesisInput
            label="Cover url"
            :violation="violations ? violations['coverUrl'] : undefined"
            placeholder="Enter Cover url"
            type="text"
            v-model="formModel.coverUrl"
            :value="formModel.coverUrl"
          />

          <!-- Is featured -->
          <label class="flex items-center gap-2">
            <input type="checkbox" class="checkbox" v-model="formModel.isFeatured" />
            <span>Is featured</span>
          </label>

          <!-- Created at -->
          <GenesisInput
            label="Created at"
            :violation="violations ? violations['createdAt'] : undefined"
            placeholder="Enter Created at"
            type="datetime-local"
            v-model="formModel.createdAt"
            :value="formModel.createdAt"
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
import { EditorialPlaylist, EditorialPlaylistFormDTO } from '@/models/EditorialPlaylistModel.ts'
import GenesisButton from '@/components/button/GenesisButton.vue'
import GenesisInput from '@/components/form/GenesisInput.vue'
import GenesisSelectSearchCriteria from '@/components/form/GenesisSelectSearchCriteria.vue'

const props = defineProps<{
  editorialPlaylist?: EditorialPlaylist
  violations?: Record<string, string> | null
  submitLabel?: string
}>()

const emit = defineEmits<{
  (e: 'submit', payload: Partial<EditorialPlaylistFormDTO>): void
  (e: 'cancel', payload: Partial<EditorialPlaylistFormDTO>): void
}>()

const formModel = ref<Partial<EditorialPlaylistFormDTO>>({
  ...EditorialPlaylistFormDTO.parse(props.editorialPlaylist),
})
const artistidArtistsSearchField = EditorialPlaylist.getSearchFieldByKey('artistidArtists')
const artistidArtistsDefaultValue = props.editorialPlaylist
  ? (props.editorialPlaylist?.artistidArtists?.getKeyValue?.() ?? undefined)
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
