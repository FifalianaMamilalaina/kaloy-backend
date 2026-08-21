<script setup lang="ts">
import GenesisSelectSearchCriteria from '@/components/form/GenesisSelectSearchCriteria.vue'
import GenesisInput from '@/components/form/GenesisInput.vue'
import { ref, onMounted } from 'vue'
import { SearchHistory, SearchHistoryFormDTO } from '@/models/SearchHistoryModel'
import TrashIcon from '@/components/icons/TrashIcon.vue'

const props = defineProps<{
  internalId: number
  searchHistory: SearchHistory
  violations?: Record<string, string> | null
}>()

const emit = defineEmits<{
  (e: 'request:remove'): void
  (e: 'udpate:model-value', value: SearchHistoryFormDTO): void
}>()

const formModel = ref<SearchHistoryFormDTO>(SearchHistoryFormDTO.parse(props.searchHistory))

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
        :violation="violations ? violations['queryText'] : undefined"
        placeholder="Enter Query text"
        type="text"
        v-model="formModel.queryText"
        :value="formModel.queryText"
        @update:model-value="(newVal) => updateModel()"
      />
    </td>

    <td>
      <GenesisInput
        :violation="violations ? violations['searchedAt'] : undefined"
        placeholder="Enter Searched at"
        type="datetime-local"
        v-model="formModel.searchedAt"
        :value="formModel.searchedAt"
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
