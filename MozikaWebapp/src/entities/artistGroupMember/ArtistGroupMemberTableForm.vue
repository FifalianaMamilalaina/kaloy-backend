<template>
  <div class="w-full bg-base-100 border rounded border-base-300 p-4">
    <h3 class="text-lg font-semibold mb-4 text-gray-700">Artist group member</h3>

    <div class="">
      <table class="table w-full">
        <thead>
          <tr class="text-primary-focus">
            <th class="">#</th>
            <th class="">Memberartistid artists</th>
            <th class="">Full name</th>
            <th class="">Roleinstrumentid instrument roles</th>
            <th class="">Photo url</th>
            <th class="">Statusid member statuses</th>
            <th class="">Created at</th>
            <th class=""></th>
          </tr>
        </thead>
        <tbody>
          <template v-for="(row, index) in rows" :key="row._internalId">
            <ArtistGroupMemberRowForm
              :internalId="index + 1"
              :artistGroupMember="row.data"
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
import { ArtistGroupMember, ArtistGroupMemberFormDTO } from '@/models/ArtistGroupMemberModel'
import GenesisAddButton from '@/components/button/GenesisAddButton.vue'
import ArtistGroupMemberRowForm from '@/entities/artistGroupMember/ArtistGroupMemberRowForm.vue'
import GenesisInput from '@/components/form/GenesisInput.vue'
import PlusIcon from '@/components/icons/PlusIcon.vue'

const rowToAdd = ref(10)
const props = defineProps<{
  getTableDataCallback: (data: ArtistGroupMemberFormDTO[]) => void
  initialData?: ArtistGroupMember[]
  violations?: Record<string, string>[]
}>()

const { rows, addRow, removeRow, getTableData } = useTableForm<
  ArtistGroupMember,
  ArtistGroupMemberFormDTO
>(props.initialData)

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
