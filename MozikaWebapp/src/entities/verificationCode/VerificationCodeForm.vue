<template>
  <div class="card bg-base-100">
    <div class="card-body">
      <form @submit.prevent="handleSubmit" class="grid grid-cols-1 w-7/12 gap-4 mx-auto">
        <!-- <form @submit.prevent="handleSubmit" class=""> -->
        <div class="grid grid-cols-1 gap-4">
          <!-- Userid users FK -->
          <GenesisSelectSearchCriteria
            v-if="useridUsersSearchField?.multicriteriaSelect"
            label="Userid users"
            key="verificationCodeUseridUsers"
            :search-function="useridUsersSearchField?.multicriteriaSelect.searchFunction"
            :filters="useridUsersSearchField?.multicriteriaSelect.filters"
            :default-value="useridUsersDefaultValue"
            :violation="violations ? violations['useridUsers'] : undefined"
            @option-selected="(selectedValue) => (formModel.useridUsers = String(selectedValue))"
          />

          <!-- Channelid verification channels FK -->
          <GenesisSelectSearchCriteria
            v-if="channelidVerificationChannelsSearchField?.multicriteriaSelect"
            label="Channelid verification channels"
            key="verificationCodeChannelidVerificationChannels"
            :search-function="
              channelidVerificationChannelsSearchField?.multicriteriaSelect.searchFunction
            "
            :filters="channelidVerificationChannelsSearchField?.multicriteriaSelect.filters"
            :default-value="channelidVerificationChannelsDefaultValue"
            :violation="violations ? violations['channelidVerificationChannels'] : undefined"
            @option-selected="
              (selectedValue) => (formModel.channelidVerificationChannels = String(selectedValue))
            "
          />

          <!-- Destination -->
          <GenesisInput
            label="Destination"
            :violation="violations ? violations['destination'] : undefined"
            placeholder="Enter Destination"
            type="text"
            v-model="formModel.destination"
            :value="formModel.destination"
          />

          <!-- Code -->
          <GenesisInput
            label="Code"
            :violation="violations ? violations['code'] : undefined"
            placeholder="Enter Code"
            type="text"
            v-model="formModel.code"
            :value="formModel.code"
          />

          <!-- Expires at -->
          <GenesisInput
            label="Expires at"
            :violation="violations ? violations['expiresAt'] : undefined"
            placeholder="Enter Expires at"
            type="datetime-local"
            v-model="formModel.expiresAt"
            :value="formModel.expiresAt"
          />

          <!-- Consumed at -->
          <GenesisInput
            label="Consumed at"
            :violation="violations ? violations['consumedAt'] : undefined"
            placeholder="Enter Consumed at"
            type="datetime-local"
            v-model="formModel.consumedAt"
            :value="formModel.consumedAt"
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
import { VerificationCode, VerificationCodeFormDTO } from '@/models/VerificationCodeModel.ts'
import GenesisButton from '@/components/button/GenesisButton.vue'
import GenesisInput from '@/components/form/GenesisInput.vue'
import GenesisSelectSearchCriteria from '@/components/form/GenesisSelectSearchCriteria.vue'

const props = defineProps<{
  verificationCode?: VerificationCode
  violations?: Record<string, string> | null
  submitLabel?: string
}>()

const emit = defineEmits<{
  (e: 'submit', payload: Partial<VerificationCodeFormDTO>): void
  (e: 'cancel', payload: Partial<VerificationCodeFormDTO>): void
}>()

const formModel = ref<Partial<VerificationCodeFormDTO>>({
  ...VerificationCodeFormDTO.parse(props.verificationCode),
})
const useridUsersSearchField = VerificationCode.getSearchFieldByKey('useridUsers')
const useridUsersDefaultValue = props.verificationCode
  ? (props.verificationCode?.useridUsers?.getKeyValue?.() ?? undefined)
  : undefined
const channelidVerificationChannelsSearchField = VerificationCode.getSearchFieldByKey(
  'channelidVerificationChannels',
)
const channelidVerificationChannelsDefaultValue = props.verificationCode
  ? (props.verificationCode?.channelidVerificationChannels?.getKeyValue?.() ?? undefined)
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
