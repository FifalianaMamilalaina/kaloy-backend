<script setup lang="ts">
import GenesisSelectSearchCriteria from '@/components/form/GenesisSelectSearchCriteria.vue'
import GenesisInput from '@/components/form/GenesisInput.vue'
import { ref, onMounted } from 'vue'
import { Like, LikeFormDTO } from '@/models/LikeModel'
import TrashIcon from '@/components/icons/TrashIcon.vue'

const props = defineProps<{
  internalId: number
  like: Like
  violations?: Record<string, string> | null
}>()

const emit = defineEmits<{
  (e: 'request:remove'): void
  (e: 'udpate:model-value', value: LikeFormDTO): void
}>()

const formModel = ref<LikeFormDTO>(LikeFormDTO.parse(props.like))
const targettypeidInteractionTargetsSearchField = Like.getSearchFieldByKey(
  'targettypeidInteractionTargets',
)
const targettypeidInteractionTargetsDefaultValue = props.like
  ? (props.like?.targettypeidInteractionTargets?.getReferenceValue?.() ?? undefined)
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
        v-if="targettypeidInteractionTargetsSearchField?.multicriteriaSelect"
        :violation="violations ? violations['targettypeidInteractionTargets'] : undefined"
        placeholder="Select Targettypeid interaction targets"
        key="LikeTargettypeidInteractionTargets"
        :search-function="
          targettypeidInteractionTargetsSearchField?.multicriteriaSelect.searchFunction
        "
        :filters="targettypeidInteractionTargetsSearchField?.multicriteriaSelect.filters"
        :default-value="targettypeidInteractionTargetsDefaultValue"
        @option-selected="
          (selectedValue) => {
            formModel.targettypeidInteractionTargets = String(selectedValue)
            updateModel()
          }
        "
      />
    </td>

    <td>
      <GenesisInput
        :violation="violations ? violations['targetId'] : undefined"
        placeholder="Enter Target id"
        type="number"
        v-model="formModel.targetId"
        :value="formModel.targetId"
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
