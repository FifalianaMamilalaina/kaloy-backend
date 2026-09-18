<template>
  <div class="card bg-base-100">
    <div class="card-body">
      <form @submit.prevent="handleSubmit" class="grid grid-cols-1 w-7/12 gap-4 mx-auto">
        <!-- <form @submit.prevent="handleSubmit" class=""> -->
        <div class="grid grid-cols-1 gap-4">
          <!-- Eventid events FK -->
          <GenesisSelectSearchCriteria
            v-if="eventidEventsSearchField?.multicriteriaSelect"
            label="Eventid events"
            key="eventMediaEventidEvents"
            :search-function="eventidEventsSearchField?.multicriteriaSelect.searchFunction"
            :filters="eventidEventsSearchField?.multicriteriaSelect.filters"
            :default-value="eventidEventsDefaultValue"
            :violation="violations ? violations['eventidEvents'] : undefined"
            @option-selected="(selectedValue) => (formModel.eventidEvents = String(selectedValue))"
          />

          <!-- Uploaderuserid users FK -->
          <GenesisSelectSearchCriteria
            v-if="uploaderuseridUsersSearchField?.multicriteriaSelect"
            label="Uploaderuserid users"
            key="eventMediaUploaderuseridUsers"
            :search-function="uploaderuseridUsersSearchField?.multicriteriaSelect.searchFunction"
            :filters="uploaderuseridUsersSearchField?.multicriteriaSelect.filters"
            :default-value="uploaderuseridUsersDefaultValue"
            :violation="violations ? violations['uploaderuseridUsers'] : undefined"
            @option-selected="
              (selectedValue) => (formModel.uploaderuseridUsers = String(selectedValue))
            "
          />

          <!-- Mediatypeid media types FK -->
          <GenesisSelectSearchCriteria
            v-if="mediatypeidMediaTypesSearchField?.multicriteriaSelect"
            label="Mediatypeid media types"
            key="eventMediaMediatypeidMediaTypes"
            :search-function="mediatypeidMediaTypesSearchField?.multicriteriaSelect.searchFunction"
            :filters="mediatypeidMediaTypesSearchField?.multicriteriaSelect.filters"
            :default-value="mediatypeidMediaTypesDefaultValue"
            :violation="violations ? violations['mediatypeidMediaTypes'] : undefined"
            @option-selected="
              (selectedValue) => (formModel.mediatypeidMediaTypes = String(selectedValue))
            "
          />

          <!-- Url -->
          <GenesisInput
            label="Url"
            :violation="violations ? violations['url'] : undefined"
            placeholder="Enter Url"
            type="text"
            v-model="formModel.url"
            :value="formModel.url"
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
import { EventMedia, EventMediaFormDTO } from '@/models/EventMediaModel.ts'
import GenesisButton from '@/components/button/GenesisButton.vue'
import GenesisInput from '@/components/form/GenesisInput.vue'
import GenesisSelectSearchCriteria from '@/components/form/GenesisSelectSearchCriteria.vue'

const props = defineProps<{
  eventMedia?: EventMedia
  violations?: Record<string, string> | null
  submitLabel?: string
}>()

const emit = defineEmits<{
  (e: 'submit', payload: Partial<EventMediaFormDTO>): void
  (e: 'cancel', payload: Partial<EventMediaFormDTO>): void
}>()

const formModel = ref<Partial<EventMediaFormDTO>>({ ...EventMediaFormDTO.parse(props.eventMedia) })
const eventidEventsSearchField = EventMedia.getSearchFieldByKey('eventidEvents')
const eventidEventsDefaultValue = props.eventMedia
  ? (props.eventMedia?.eventidEvents?.getKeyValue?.() ?? undefined)
  : undefined
const uploaderuseridUsersSearchField = EventMedia.getSearchFieldByKey('uploaderuseridUsers')
const uploaderuseridUsersDefaultValue = props.eventMedia
  ? (props.eventMedia?.uploaderuseridUsers?.getKeyValue?.() ?? undefined)
  : undefined
const mediatypeidMediaTypesSearchField = EventMedia.getSearchFieldByKey('mediatypeidMediaTypes')
const mediatypeidMediaTypesDefaultValue = props.eventMedia
  ? (props.eventMedia?.mediatypeidMediaTypes?.getKeyValue?.() ?? undefined)
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
