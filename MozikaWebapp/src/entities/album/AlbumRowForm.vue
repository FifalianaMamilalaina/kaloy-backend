<script setup lang="ts">
import GenesisSelectSearchCriteria from '@/components/form/GenesisSelectSearchCriteria.vue'
import GenesisInput from '@/components/form/GenesisInput.vue'
import { ref, onMounted } from 'vue'
import { Album, AlbumFormDTO } from '@/models/AlbumModel'
import TrashIcon from '@/components/icons/TrashIcon.vue'

const props = defineProps<{
  internalId: number
  album: Album
  violations?: Record<string, string> | null
}>()

const emit = defineEmits<{
  (e: 'request:remove'): void
  (e: 'udpate:model-value', value: AlbumFormDTO): void
}>()

const formModel = ref<AlbumFormDTO>(AlbumFormDTO.parse(props.album))

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
        :violation="violations ? violations['title'] : undefined"
        placeholder="Enter Title"
        type="text"
        v-model="formModel.title"
        :value="formModel.title"
        @update:model-value="(newVal) => updateModel()"
      />
    </td>

    <td>
      <GenesisInput
        :violation="violations ? violations['coverUrl'] : undefined"
        placeholder="Enter Cover url"
        type="text"
        v-model="formModel.coverUrl"
        :value="formModel.coverUrl"
        @update:model-value="(newVal) => updateModel()"
      />
    </td>

    <td>
      <GenesisInput
        :violation="violations ? violations['releaseDate'] : undefined"
        placeholder="Enter Release date"
        type="date"
        v-model="formModel.releaseDate"
        :value="formModel.releaseDate"
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
