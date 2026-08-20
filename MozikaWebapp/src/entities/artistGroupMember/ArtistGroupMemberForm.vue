<template>
  <div class="card bg-base-100">
    <div class="card-body">
      <form @submit.prevent="handleSubmit" class="grid grid-cols-1 w-7/12 gap-4 mx-auto">
        <!-- <form @submit.prevent="handleSubmit" class=""> -->
        <div class="grid grid-cols-1 gap-4">
          <!-- Groupartistid artists FK -->
          <GenesisSelectSearchCriteria
            v-if="groupartistidArtistsSearchField?.multicriteriaSelect"
            label="Groupartistid artists"
            key="artistGroupMemberGroupartistidArtists"
            :search-function="groupartistidArtistsSearchField?.multicriteriaSelect.searchFunction"
            :filters="groupartistidArtistsSearchField?.multicriteriaSelect.filters"
            :default-value="groupartistidArtistsDefaultValue"
            :violation="violations ? violations['groupartistidArtists'] : undefined"
            @option-selected="
              (selectedValue) => (formModel.groupartistidArtists = String(selectedValue))
            "
          />

          <!-- Memberartistid artists FK -->
          <GenesisSelectSearchCriteria
            v-if="memberartistidArtistsSearchField?.multicriteriaSelect"
            label="Memberartistid artists"
            key="artistGroupMemberMemberartistidArtists"
            :search-function="memberartistidArtistsSearchField?.multicriteriaSelect.searchFunction"
            :filters="memberartistidArtistsSearchField?.multicriteriaSelect.filters"
            :default-value="memberartistidArtistsDefaultValue"
            :violation="violations ? violations['memberartistidArtists'] : undefined"
            @option-selected="
              (selectedValue) => (formModel.memberartistidArtists = String(selectedValue))
            "
          />

          <!-- Full name -->
          <GenesisInput
            label="Full name"
            :violation="violations ? violations['fullName'] : undefined"
            placeholder="Enter Full name"
            type="text"
            v-model="formModel.fullName"
            :value="formModel.fullName"
          />

          <!-- Roleinstrumentid instrument roles FK -->
          <GenesisSelectSearchCriteria
            v-if="roleinstrumentidInstrumentRolesSearchField?.multicriteriaSelect"
            label="Roleinstrumentid instrument roles"
            key="artistGroupMemberRoleinstrumentidInstrumentRoles"
            :search-function="
              roleinstrumentidInstrumentRolesSearchField?.multicriteriaSelect.searchFunction
            "
            :filters="roleinstrumentidInstrumentRolesSearchField?.multicriteriaSelect.filters"
            :default-value="roleinstrumentidInstrumentRolesDefaultValue"
            :violation="violations ? violations['roleinstrumentidInstrumentRoles'] : undefined"
            @option-selected="
              (selectedValue) => (formModel.roleinstrumentidInstrumentRoles = String(selectedValue))
            "
          />

          <!-- Photo url -->
          <GenesisInput
            label="Photo url"
            :violation="violations ? violations['photoUrl'] : undefined"
            placeholder="Enter Photo url"
            type="text"
            v-model="formModel.photoUrl"
            :value="formModel.photoUrl"
          />

          <!-- Statusid member statuses FK -->
          <GenesisSelectSearchCriteria
            v-if="statusidMemberStatusesSearchField?.multicriteriaSelect"
            label="Statusid member statuses"
            key="artistGroupMemberStatusidMemberStatuses"
            :search-function="statusidMemberStatusesSearchField?.multicriteriaSelect.searchFunction"
            :filters="statusidMemberStatusesSearchField?.multicriteriaSelect.filters"
            :default-value="statusidMemberStatusesDefaultValue"
            :violation="violations ? violations['statusidMemberStatuses'] : undefined"
            @option-selected="
              (selectedValue) => (formModel.statusidMemberStatuses = String(selectedValue))
            "
          />

          <!-- Created at -->
          <GenesisInput
            label="Created at"
            :violation="violations ? violations['createdAt'] : undefined"
            placeholder="Enter Created at"
            type="datetime-local"
            v-model="formModel.createdAt"
            :value="formModel.createdAt"
          />
        </div>

        <!-- Action buttons -->
        <div class="flex justify-end gap-3 mt-6">
          <GenesisButton
            type="submit"
            class="btn btn-primary text-primary-content"
            :label="submitLabel"
          />
          <GenesisButton
            @click="cancelForm"
            class="btn btn-outline btn-error"
            :label="$t('button.cancel')"
          />
        </div>
      </form>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { ArtistGroupMember, ArtistGroupMemberFormDTO } from '@/models/ArtistGroupMemberModel.ts'
import GenesisButton from '@/components/button/GenesisButton.vue'
import GenesisInput from '@/components/form/GenesisInput.vue'
import GenesisSelectSearchCriteria from '@/components/form/GenesisSelectSearchCriteria.vue'

const props = defineProps<{
  artistGroupMember?: ArtistGroupMember
  violations?: Record<string, string> | null
  submitLabel?: string
}>()

const emit = defineEmits<{
  (e: 'submit', payload: Partial<ArtistGroupMemberFormDTO>): void
  (e: 'cancel', payload: Partial<ArtistGroupMemberFormDTO>): void
}>()

const formModel = ref<Partial<ArtistGroupMemberFormDTO>>({
  ...ArtistGroupMemberFormDTO.parse(props.artistGroupMember),
})
const groupartistidArtistsSearchField =
  ArtistGroupMember.getSearchFieldByKey('groupartistidArtists')
const groupartistidArtistsDefaultValue = props.artistGroupMember
  ? (props.artistGroupMember?.groupartistidArtists?.getKeyValue?.() ?? undefined)
  : undefined
const memberartistidArtistsSearchField =
  ArtistGroupMember.getSearchFieldByKey('memberartistidArtists')
const memberartistidArtistsDefaultValue = props.artistGroupMember
  ? (props.artistGroupMember?.memberartistidArtists?.getKeyValue?.() ?? undefined)
  : undefined
const roleinstrumentidInstrumentRolesSearchField = ArtistGroupMember.getSearchFieldByKey(
  'roleinstrumentidInstrumentRoles',
)
const roleinstrumentidInstrumentRolesDefaultValue = props.artistGroupMember
  ? (props.artistGroupMember?.roleinstrumentidInstrumentRoles?.getKeyValue?.() ?? undefined)
  : undefined
const statusidMemberStatusesSearchField =
  ArtistGroupMember.getSearchFieldByKey('statusidMemberStatuses')
const statusidMemberStatusesDefaultValue = props.artistGroupMember
  ? (props.artistGroupMember?.statusidMemberStatuses?.getKeyValue?.() ?? undefined)
  : undefined

const fileToBase64 = (file: File): Promise<string> => {
  return new Promise((resolve, reject) => {
    const reader = new FileReader()
    reader.onload = () => {
      if (typeof reader.result !== 'string') {
        reject(new Error('Unable to read the selected file'))
        return
      }
      const commaIndex = reader.result.indexOf(',')
      resolve(commaIndex >= 0 ? reader.result.substring(commaIndex + 1) : reader.result)
    }
    reader.onerror = () => {
      reject(reader.error ?? new Error('Unable to read the selected file'))
    }
    reader.readAsDataURL(file)
  })
}
async function handleSubmit() {
  const data: any = { ...formModel.value }
  Object.keys(data).forEach((key) => {
    if (data[key] === '' || data[key] === null || data[key] === undefined) {
      delete data[key]
    }
  })

  emit('submit', data)
}

function cancelForm() {
  emit('cancel', formModel.value)
}
</script>
<style scoped></style>
