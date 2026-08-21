<script setup lang="ts">
import GenesisSelectSearchCriteria from '@/components/form/GenesisSelectSearchCriteria.vue'
import GenesisInput from '@/components/form/GenesisInput.vue'
import { ref, onMounted } from 'vue'
import { Playlist, PlaylistFormDTO } from '@/models/PlaylistModel'
import TrashIcon from '@/components/icons/TrashIcon.vue'

const props = defineProps<{
  internalId: number
  playlist: Playlist
  violations?: Record<string, string> | null
}>()

const emit = defineEmits<{
  (e: 'request:remove'): void
  (e: 'udpate:model-value', value: PlaylistFormDTO): void
}>()

const formModel = ref<PlaylistFormDTO>(PlaylistFormDTO.parse(props.playlist))
const visibilityidPlaylistVisibilitiesSearchField = Playlist.getSearchFieldByKey(
  'visibilityidPlaylistVisibilities',
)
const visibilityidPlaylistVisibilitiesDefaultValue = props.playlist
  ? (props.playlist?.visibilityidPlaylistVisibilities?.getReferenceValue?.() ?? undefined)
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
        :violation="violations ? violations['name'] : undefined"
        placeholder="Enter Name"
        type="text"
        v-model="formModel.name"
        :value="formModel.name"
        @update:model-value="(newVal) => updateModel()"
      />
    </td>

    <td>
      <GenesisSelectSearchCriteria
        v-if="visibilityidPlaylistVisibilitiesSearchField?.multicriteriaSelect"
        :violation="violations ? violations['visibilityidPlaylistVisibilities'] : undefined"
        placeholder="Select Visibilityid playlist visibilities"
        key="PlaylistVisibilityidPlaylistVisibilities"
        :search-function="
          visibilityidPlaylistVisibilitiesSearchField?.multicriteriaSelect.searchFunction
        "
        :filters="visibilityidPlaylistVisibilitiesSearchField?.multicriteriaSelect.filters"
        :default-value="visibilityidPlaylistVisibilitiesDefaultValue"
        @option-selected="
          (selectedValue) => {
            formModel.visibilityidPlaylistVisibilities = String(selectedValue)
            updateModel()
          }
        "
      />
    </td>

    <td>
      <GenesisInput
        :violation="violations ? violations['shareToken'] : undefined"
        placeholder="Enter Share token"
        type="text"
        v-model="formModel.shareToken"
        :value="formModel.shareToken"
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

    <td>
      <GenesisInput
        :violation="violations ? violations['updatedAt'] : undefined"
        placeholder="Enter Updated at"
        type="datetime-local"
        v-model="formModel.updatedAt"
        :value="formModel.updatedAt"
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
