<template>
  <div class="w-full bg-base-100 border rounded border-base-300 p-4">
    <h3 class="text-lg font-semibold mb-4 text-gray-700">Song</h3>

    <div class="">
      <table class="table w-full">
        <thead>
          <tr class="text-primary-focus">
            <th class="">#</th>
            <th class="">Title</th>
            <th class="">Duration seconds</th>
            <th class="">Release date</th>
            <th class="">Language</th>
            <th class="">Author composer</th>
            <th class="">Musical arranger</th>
            <th class="">Recording location</th>
            <th class="">Recording date</th>
            <th class="">Storagetypeid audio storage types</th>
            <th class="">Audio url</th>
            <th class="">Audio file</th>
            <th class="">Video url</th>
            <th class="">Karaoke audio</th>
            <th class="">Lyrics</th>
            <th class="">Lyrics sync data</th>
            <th class="">Solfa</th>
            <th class="">Playback</th>
            <th class="">Is downloadable</th>
            <th class="">Created at</th>
            <th class=""></th>
          </tr>
        </thead>
        <tbody>
          <template v-for="(row, index) in rows" :key="row._internalId">
            <SongRowForm
              :internalId="index + 1"
              :song="row.data"
              :violations="violations ? violations[index] : undefined"
              @request:remove="removeRowHandler(row._internalId)"
              @udpate:model-value="(rowData) => (row.rowValue = rowData)"
            />
          </template>
        </tbody>
        <tfoot>
          <tr></tr>
        </tfoot>
      </table>
    </div>
    <div class="mt-4 gap-2 flex justify-end">
      <div class="w-25">
        <GenesisInput min="1" type="number" v-model="rowToAdd" :value="rowToAdd" />
      </div>
      <GenesisAddButton type="button" @click="addRowHandler"><PlusIcon /></GenesisAddButton>
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref, watch } from 'vue'
import { useTableForm } from '@/composables/useTableForm'
import { Song, SongFormDTO } from '@/models/SongModel'
import GenesisAddButton from '@/components/button/GenesisAddButton.vue'
import SongRowForm from '@/entities/song/SongRowForm.vue'
import GenesisInput from '@/components/form/GenesisInput.vue'
import PlusIcon from '@/components/icons/PlusIcon.vue'

const rowToAdd = ref(10)
const props = defineProps<{
  getTableDataCallback: (data: SongFormDTO[]) => void
  initialData?: Song[]
  violations?: Record<string, string>[]
}>()

const { rows, addRow, removeRow, getTableData } = useTableForm<Song, SongFormDTO>(props.initialData)

const addRowHandler = () => {
  addRow(rowToAdd.value)
  props.getTableDataCallback(getTableData())
}

const removeRowHandler = (internalId: number) => {
  removeRow(internalId)
  props.getTableDataCallback(getTableData())
}

onMounted(() => {
  props.getTableDataCallback(getTableData())
})
watch(
  () => rows.value,
  () => {
    props.getTableDataCallback(getTableData())
  },
  { deep: true },
)
</script>

<style scoped>
.table :where(th, td) {
  padding: 0.5rem 0.5rem;
}
</style>
