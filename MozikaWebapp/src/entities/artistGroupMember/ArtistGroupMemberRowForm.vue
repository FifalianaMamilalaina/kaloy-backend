<script setup lang="ts">
import GenesisSelectSearchCriteria from '@/components/form/GenesisSelectSearchCriteria.vue'
import GenesisInput from '@/components/form/GenesisInput.vue'
import { ref, onMounted } from 'vue'
import { ArtistGroupMember, ArtistGroupMemberFormDTO } from '@/models/ArtistGroupMemberModel'
import TrashIcon from '@/components/icons/TrashIcon.vue'

const props = defineProps<{
  internalId: number
  artistGroupMember: ArtistGroupMember
  violations?: Record<string, string> | null
}>()

const emit = defineEmits<{
  (e: 'request:remove'): void
  (e: 'udpate:model-value', value: ArtistGroupMemberFormDTO): void
}>()

const formModel = ref<ArtistGroupMemberFormDTO>(
  ArtistGroupMemberFormDTO.parse(props.artistGroupMember),
)
const memberartistidArtistsSearchField =
  ArtistGroupMember.getSearchFieldByKey('memberartistidArtists')
const memberartistidArtistsDefaultValue = props.artistGroupMember
  ? (props.artistGroupMember?.memberartistidArtists?.getReferenceValue?.() ?? undefined)
  : undefined
const roleinstrumentidInstrumentRolesSearchField = ArtistGroupMember.getSearchFieldByKey(
  'roleinstrumentidInstrumentRoles',
)
const roleinstrumentidInstrumentRolesDefaultValue = props.artistGroupMember
  ? (props.artistGroupMember?.roleinstrumentidInstrumentRoles?.getReferenceValue?.() ?? undefined)
  : undefined
const statusidMemberStatusesSearchField =
  ArtistGroupMember.getSearchFieldByKey('statusidMemberStatuses')
const statusidMemberStatusesDefaultValue = props.artistGroupMember
  ? (props.artistGroupMember?.statusidMemberStatuses?.getReferenceValue?.() ?? undefined)
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
        v-if="memberartistidArtistsSearchField?.multicriteriaSelect"
        :violation="violations ? violations['memberartistidArtists'] : undefined"
        placeholder="Select Memberartistid artists"
        key="ArtistGroupMemberMemberartistidArtists"
        :search-function="memberartistidArtistsSearchField?.multicriteriaSelect.searchFunction"
        :filters="memberartistidArtistsSearchField?.multicriteriaSelect.filters"
        :default-value="memberartistidArtistsDefaultValue"
        @option-selected="
          (selectedValue) => {
            formModel.memberartistidArtists = String(selectedValue)
            updateModel()
          }
        "
      />
    </td>

    <td>
      <GenesisInput
        :violation="violations ? violations['fullName'] : undefined"
        placeholder="Enter Full name"
        type="text"
        v-model="formModel.fullName"
        :value="formModel.fullName"
        @update:model-value="(newVal) => updateModel()"
      />
    </td>

    <td>
      <GenesisSelectSearchCriteria
        v-if="roleinstrumentidInstrumentRolesSearchField?.multicriteriaSelect"
        :violation="violations ? violations['roleinstrumentidInstrumentRoles'] : undefined"
        placeholder="Select Roleinstrumentid instrument roles"
        key="ArtistGroupMemberRoleinstrumentidInstrumentRoles"
        :search-function="
          roleinstrumentidInstrumentRolesSearchField?.multicriteriaSelect.searchFunction
        "
        :filters="roleinstrumentidInstrumentRolesSearchField?.multicriteriaSelect.filters"
        :default-value="roleinstrumentidInstrumentRolesDefaultValue"
        @option-selected="
          (selectedValue) => {
            formModel.roleinstrumentidInstrumentRoles = String(selectedValue)
            updateModel()
          }
        "
      />
    </td>

    <td>
      <GenesisInput
        :violation="violations ? violations['photoUrl'] : undefined"
        placeholder="Enter Photo url"
        type="text"
        v-model="formModel.photoUrl"
        :value="formModel.photoUrl"
        @update:model-value="(newVal) => updateModel()"
      />
    </td>

    <td>
      <GenesisSelectSearchCriteria
        v-if="statusidMemberStatusesSearchField?.multicriteriaSelect"
        :violation="violations ? violations['statusidMemberStatuses'] : undefined"
        placeholder="Select Statusid member statuses"
        key="ArtistGroupMemberStatusidMemberStatuses"
        :search-function="statusidMemberStatusesSearchField?.multicriteriaSelect.searchFunction"
        :filters="statusidMemberStatusesSearchField?.multicriteriaSelect.filters"
        :default-value="statusidMemberStatusesDefaultValue"
        @option-selected="
          (selectedValue) => {
            formModel.statusidMemberStatuses = String(selectedValue)
            updateModel()
          }
        "
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
