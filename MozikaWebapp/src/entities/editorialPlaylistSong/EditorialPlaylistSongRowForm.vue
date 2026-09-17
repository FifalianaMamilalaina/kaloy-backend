<script setup lang="ts">
import GenesisSelectSearchCriteria from '@/components/form/GenesisSelectSearchCriteria.vue'
import GenesisInput from '@/components/form/GenesisInput.vue'
import { ref, onMounted } from 'vue'
import {
  EditorialPlaylistSong,
  EditorialPlaylistSongFormDTO,
} from '@/models/EditorialPlaylistSongModel'
import TrashIcon from '@/components/icons/TrashIcon.vue'

const props = defineProps<{
  internalId: number
  editorialPlaylistSong: EditorialPlaylistSong
  violations?: Record<string, string> | null
}>()

const emit = defineEmits<{
  (e: 'request:remove'): void
  (e: 'udpate:model-value', value: EditorialPlaylistSongFormDTO): void
}>()

const formModel = ref<EditorialPlaylistSongFormDTO>(
  EditorialPlaylistSongFormDTO.parse(props.editorialPlaylistSong),
)
const editorialplaylistidEditorialPlaylistsSearchField = EditorialPlaylistSong.getSearchFieldByKey(
  'editorialplaylistidEditorialPlaylists',
)
const editorialplaylistidEditorialPlaylistsDefaultValue = props.editorialPlaylistSong
  ? (props.editorialPlaylistSong?.editorialplaylistidEditorialPlaylists?.getReferenceValue?.() ??
    undefined)
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
      <GenesisSelectSearchCriteria
        v-if="editorialplaylistidEditorialPlaylistsSearchField?.multicriteriaSelect"
        :violation="violations ? violations['editorialplaylistidEditorialPlaylists'] : undefined"
        placeholder="Select Editorialplaylistid editorial playlists"
        key="EditorialPlaylistSongEditorialplaylistidEditorialPlaylists"
        :search-function="
          editorialplaylistidEditorialPlaylistsSearchField?.multicriteriaSelect.searchFunction
        "
        :filters="editorialplaylistidEditorialPlaylistsSearchField?.multicriteriaSelect.filters"
        :default-value="editorialplaylistidEditorialPlaylistsDefaultValue"
        @option-selected="
          (selectedValue) => {
            formModel.editorialplaylistidEditorialPlaylists = String(selectedValue)
            updateModel()
          }
        "
      />
    </td>

    <td>
      <GenesisInput
        :violation="violations ? violations['position'] : undefined"
        placeholder="Enter Position"
        type="number"
        v-model="formModel.position"
        :value="formModel.position"
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
