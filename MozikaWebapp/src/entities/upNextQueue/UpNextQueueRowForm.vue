<script setup lang="ts">
import GenesisSelectSearchCriteria from '@/components/form/GenesisSelectSearchCriteria.vue'
import GenesisInput from '@/components/form/GenesisInput.vue'
import { ref, onMounted } from 'vue'
import { UpNextQueue, UpNextQueueFormDTO } from '@/models/UpNextQueueModel'
import TrashIcon from '@/components/icons/TrashIcon.vue'

const props = defineProps<{
  internalId: number
  upNextQueue: UpNextQueue
  violations?: Record<string, string> | null
}>()

const emit = defineEmits<{
  (e: 'request:remove'): void
  (e: 'udpate:model-value', value: UpNextQueueFormDTO): void
}>()

const formModel = ref<UpNextQueueFormDTO>(UpNextQueueFormDTO.parse(props.upNextQueue))

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
        :violation="violations ? violations['position'] : undefined"
        placeholder="Enter Position"
        type="number"
        v-model="formModel.position"
        :value="formModel.position"
        @update:model-value="(newVal) => updateModel()"
      />
    </td>

    <td>
      <GenesisInput
        :violation="violations ? violations['addedAt'] : undefined"
        placeholder="Enter Added at"
        type="datetime-local"
        v-model="formModel.addedAt"
        :value="formModel.addedAt"
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
