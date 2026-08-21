<script setup lang="ts">
import GenesisSelectSearchCriteria from '@/components/form/GenesisSelectSearchCriteria.vue'
import GenesisInput from '@/components/form/GenesisInput.vue'
import { ref, onMounted } from 'vue'
import { EventMedia, EventMediaFormDTO } from '@/models/EventMediaModel'
import TrashIcon from '@/components/icons/TrashIcon.vue'

const props = defineProps<{
  internalId: number
  eventMedia: EventMedia
  violations?: Record<string, string> | null
}>()

const emit = defineEmits<{
  (e: 'request:remove'): void
  (e: 'udpate:model-value', value: EventMediaFormDTO): void
}>()

const formModel = ref<EventMediaFormDTO>(EventMediaFormDTO.parse(props.eventMedia))
const mediatypeidMediaTypesSearchField = EventMedia.getSearchFieldByKey('mediatypeidMediaTypes')
const mediatypeidMediaTypesDefaultValue = props.eventMedia
  ? (props.eventMedia?.mediatypeidMediaTypes?.getReferenceValue?.() ?? undefined)
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
        v-if="mediatypeidMediaTypesSearchField?.multicriteriaSelect"
        :violation="violations ? violations['mediatypeidMediaTypes'] : undefined"
        placeholder="Select Mediatypeid media types"
        key="EventMediaMediatypeidMediaTypes"
        :search-function="mediatypeidMediaTypesSearchField?.multicriteriaSelect.searchFunction"
        :filters="mediatypeidMediaTypesSearchField?.multicriteriaSelect.filters"
        :default-value="mediatypeidMediaTypesDefaultValue"
        @option-selected="
          (selectedValue) => {
            formModel.mediatypeidMediaTypes = String(selectedValue)
            updateModel()
          }
        "
      />
    </td>

    <td>
      <GenesisInput
        :violation="violations ? violations['url'] : undefined"
        placeholder="Enter Url"
        type="text"
        v-model="formModel.url"
        :value="formModel.url"
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
