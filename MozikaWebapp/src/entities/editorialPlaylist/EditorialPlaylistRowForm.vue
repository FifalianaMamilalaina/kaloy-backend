<script setup lang="ts">
import GenesisSelectSearchCriteria from '@/components/form/GenesisSelectSearchCriteria.vue'
import GenesisInput from '@/components/form/GenesisInput.vue'
import { ref, onMounted } from 'vue'
import { EditorialPlaylist, EditorialPlaylistFormDTO } from '@/models/EditorialPlaylistModel'
import TrashIcon from '@/components/icons/TrashIcon.vue'

const props = defineProps<{
  internalId: number
  editorialPlaylist: EditorialPlaylist
  violations?: Record<string, string> | null
}>()

const emit = defineEmits<{
  (e: 'request:remove'): void
  (e: 'udpate:model-value', value: EditorialPlaylistFormDTO): void
}>()

const formModel = ref<EditorialPlaylistFormDTO>(
  EditorialPlaylistFormDTO.parse(props.editorialPlaylist),
)

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
        :violation="violations ? violations['description'] : undefined"
        placeholder="Enter Description"
        type="text"
        v-model="formModel.description"
        :value="formModel.description"
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
        :violation="violations ? violations['isFeatured'] : undefined"
        placeholder="Enter Is featured"
        type="checkbox"
        v-model="formModel.isFeatured"
        :value="formModel.isFeatured"
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
