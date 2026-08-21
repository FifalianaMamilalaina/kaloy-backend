<script setup lang="ts">
import GenesisSelectSearchCriteria from '@/components/form/GenesisSelectSearchCriteria.vue'
import GenesisInput from '@/components/form/GenesisInput.vue'
import { ref, onMounted } from 'vue'
import { ListeningHistory, ListeningHistoryFormDTO } from '@/models/ListeningHistoryModel'
import TrashIcon from '@/components/icons/TrashIcon.vue'

const props = defineProps<{
  internalId: number
  listeningHistory: ListeningHistory
  violations?: Record<string, string> | null
}>()

const emit = defineEmits<{
  (e: 'request:remove'): void
  (e: 'udpate:model-value', value: ListeningHistoryFormDTO): void
}>()

const formModel = ref<ListeningHistoryFormDTO>(
  ListeningHistoryFormDTO.parse(props.listeningHistory),
)
const playmodeidPlayModesSearchField = ListeningHistory.getSearchFieldByKey('playmodeidPlayModes')
const playmodeidPlayModesDefaultValue = props.listeningHistory
  ? (props.listeningHistory?.playmodeidPlayModes?.getReferenceValue?.() ?? undefined)
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
        v-if="playmodeidPlayModesSearchField?.multicriteriaSelect"
        :violation="violations ? violations['playmodeidPlayModes'] : undefined"
        placeholder="Select Playmodeid play modes"
        key="ListeningHistoryPlaymodeidPlayModes"
        :search-function="playmodeidPlayModesSearchField?.multicriteriaSelect.searchFunction"
        :filters="playmodeidPlayModesSearchField?.multicriteriaSelect.filters"
        :default-value="playmodeidPlayModesDefaultValue"
        @option-selected="
          (selectedValue) => {
            formModel.playmodeidPlayModes = String(selectedValue)
            updateModel()
          }
        "
      />
    </td>

    <td>
      <GenesisInput
        :violation="violations ? violations['listenedAt'] : undefined"
        placeholder="Enter Listened at"
        type="datetime-local"
        v-model="formModel.listenedAt"
        :value="formModel.listenedAt"
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
