<script setup lang="ts">
import GenesisSelectSearchCriteria from '@/components/form/GenesisSelectSearchCriteria.vue'
import GenesisInput from '@/components/form/GenesisInput.vue'
import { ref, onMounted } from 'vue'
import { Comment, CommentFormDTO } from '@/models/CommentModel'
import TrashIcon from '@/components/icons/TrashIcon.vue'

const props = defineProps<{
  internalId: number
  comment: Comment
  violations?: Record<string, string> | null
}>()

const emit = defineEmits<{
  (e: 'request:remove'): void
  (e: 'udpate:model-value', value: CommentFormDTO): void
}>()

const formModel = ref<CommentFormDTO>(CommentFormDTO.parse(props.comment))
const targettypeidInteractionTargetsSearchField = Comment.getSearchFieldByKey(
  'targettypeidInteractionTargets',
)
const targettypeidInteractionTargetsDefaultValue = props.comment
  ? (props.comment?.targettypeidInteractionTargets?.getReferenceValue?.() ?? undefined)
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
        key="CommentTargettypeidInteractionTargets"
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
        :violation="violations ? violations['content'] : undefined"
        placeholder="Enter Content"
        type="text"
        v-model="formModel.content"
        :value="formModel.content"
        @update:model-value="(newVal) => updateModel()"
      />
    </td>

    <td>
      <GenesisInput
        :violation="violations ? violations['isHidden'] : undefined"
        placeholder="Enter Is hidden"
        type="checkbox"
        v-model="formModel.isHidden"
        :value="formModel.isHidden"
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
