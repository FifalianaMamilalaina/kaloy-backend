<script setup lang="ts">
import GenesisSelectSearchCriteria from '@/components/form/GenesisSelectSearchCriteria.vue'
import GenesisInput from '@/components/form/GenesisInput.vue'
import { ref, onMounted } from 'vue'
import { VerificationCode, VerificationCodeFormDTO } from '@/models/VerificationCodeModel'
import TrashIcon from '@/components/icons/TrashIcon.vue'

const props = defineProps<{
  internalId: number
  verificationCode: VerificationCode
  violations?: Record<string, string> | null
}>()

const emit = defineEmits<{
  (e: 'request:remove'): void
  (e: 'udpate:model-value', value: VerificationCodeFormDTO): void
}>()

const formModel = ref<VerificationCodeFormDTO>(
  VerificationCodeFormDTO.parse(props.verificationCode),
)
const channelidVerificationChannelsSearchField = VerificationCode.getSearchFieldByKey(
  'channelidVerificationChannels',
)
const channelidVerificationChannelsDefaultValue = props.verificationCode
  ? (props.verificationCode?.channelidVerificationChannels?.getReferenceValue?.() ?? undefined)
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
        v-if="channelidVerificationChannelsSearchField?.multicriteriaSelect"
        :violation="violations ? violations['channelidVerificationChannels'] : undefined"
        placeholder="Select Channelid verification channels"
        key="VerificationCodeChannelidVerificationChannels"
        :search-function="
          channelidVerificationChannelsSearchField?.multicriteriaSelect.searchFunction
        "
        :filters="channelidVerificationChannelsSearchField?.multicriteriaSelect.filters"
        :default-value="channelidVerificationChannelsDefaultValue"
        @option-selected="
          (selectedValue) => {
            formModel.channelidVerificationChannels = String(selectedValue)
            updateModel()
          }
        "
      />
    </td>

    <td>
      <GenesisInput
        :violation="violations ? violations['destination'] : undefined"
        placeholder="Enter Destination"
        type="text"
        v-model="formModel.destination"
        :value="formModel.destination"
        @update:model-value="(newVal) => updateModel()"
      />
    </td>

    <td>
      <GenesisInput
        :violation="violations ? violations['code'] : undefined"
        placeholder="Enter Code"
        type="text"
        v-model="formModel.code"
        :value="formModel.code"
        @update:model-value="(newVal) => updateModel()"
      />
    </td>

    <td>
      <GenesisInput
        :violation="violations ? violations['expiresAt'] : undefined"
        placeholder="Enter Expires at"
        type="datetime-local"
        v-model="formModel.expiresAt"
        :value="formModel.expiresAt"
        @update:model-value="(newVal) => updateModel()"
      />
    </td>

    <td>
      <GenesisInput
        :violation="violations ? violations['consumedAt'] : undefined"
        placeholder="Enter Consumed at"
        type="datetime-local"
        v-model="formModel.consumedAt"
        :value="formModel.consumedAt"
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
