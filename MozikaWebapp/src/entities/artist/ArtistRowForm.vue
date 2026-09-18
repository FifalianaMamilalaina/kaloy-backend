<script setup lang="ts">
import GenesisSelectSearchCriteria from '@/components/form/GenesisSelectSearchCriteria.vue'
import GenesisInput from '@/components/form/GenesisInput.vue'
import { ref, onMounted } from 'vue'
import { Artist, ArtistFormDTO } from '@/models/ArtistModel'
import TrashIcon from '@/components/icons/TrashIcon.vue'

const props = defineProps<{
  internalId: number
  artist: Artist
  violations?: Record<string, string> | null
}>()

const emit = defineEmits<{
  (e: 'request:remove'): void
  (e: 'udpate:model-value', value: ArtistFormDTO): void
}>()

const formModel = ref<ArtistFormDTO>(ArtistFormDTO.parse(props.artist))
const artisttypeidArtistTypesSearchField = Artist.getSearchFieldByKey('artisttypeidArtistTypes')
const artisttypeidArtistTypesDefaultValue = props.artist
  ? (props.artist?.artisttypeidArtistTypes?.getReferenceValue?.() ?? undefined)
  : undefined
const verificationstatusidVerificationStatusesSearchField = Artist.getSearchFieldByKey(
  'verificationstatusidVerificationStatuses',
)
const verificationstatusidVerificationStatusesDefaultValue = props.artist
  ? (props.artist?.verificationstatusidVerificationStatuses?.getReferenceValue?.() ?? undefined)
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
        v-if="artisttypeidArtistTypesSearchField?.multicriteriaSelect"
        :violation="violations ? violations['artisttypeidArtistTypes'] : undefined"
        placeholder="Select Artisttypeid artist types"
        key="ArtistArtisttypeidArtistTypes"
        :search-function="artisttypeidArtistTypesSearchField?.multicriteriaSelect.searchFunction"
        :filters="artisttypeidArtistTypesSearchField?.multicriteriaSelect.filters"
        :default-value="artisttypeidArtistTypesDefaultValue"
        @option-selected="
          (selectedValue) => {
            formModel.artisttypeidArtistTypes = String(selectedValue)
            updateModel()
          }
        "
      />
    </td>

    <td>
      <GenesisInput
        :violation="violations ? violations['stageName'] : undefined"
        placeholder="Enter Stage name"
        type="text"
        v-model="formModel.stageName"
        :value="formModel.stageName"
        @update:model-value="(newVal) => updateModel()"
      />
    </td>

    <td>
      <GenesisInput
        :violation="violations ? violations['activeSinceYear'] : undefined"
        placeholder="Enter Active since year"
        type="number"
        v-model="formModel.activeSinceYear"
        :value="formModel.activeSinceYear"
        @update:model-value="(newVal) => updateModel()"
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
      <GenesisInput
        :violation="violations ? violations['bio'] : undefined"
        placeholder="Enter Bio"
        type="text"
        v-model="formModel.bio"
        :value="formModel.bio"
        @update:model-value="(newVal) => updateModel()"
      />
    </td>

    <td>
      <GenesisSelectSearchCriteria
        v-if="verificationstatusidVerificationStatusesSearchField?.multicriteriaSelect"
        :violation="violations ? violations['verificationstatusidVerificationStatuses'] : undefined"
        placeholder="Select Verificationstatusid verification statuses"
        key="ArtistVerificationstatusidVerificationStatuses"
        :search-function="
          verificationstatusidVerificationStatusesSearchField?.multicriteriaSelect.searchFunction
        "
        :filters="verificationstatusidVerificationStatusesSearchField?.multicriteriaSelect.filters"
        :default-value="verificationstatusidVerificationStatusesDefaultValue"
        @option-selected="
          (selectedValue) => {
            formModel.verificationstatusidVerificationStatuses = String(selectedValue)
            updateModel()
          }
        "
      />
    </td>

    <td>
      <GenesisInput
        :violation="violations ? violations['verifiedAt'] : undefined"
        placeholder="Enter Verified at"
        type="datetime-local"
        v-model="formModel.verifiedAt"
        :value="formModel.verifiedAt"
        @update:model-value="(newVal) => updateModel()"
      />
    </td>

    <td>
      <GenesisInput
        :violation="violations ? violations['isCertified'] : undefined"
        placeholder="Enter Is certified"
        type="checkbox"
        v-model="formModel.isCertified"
        :value="formModel.isCertified"
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
