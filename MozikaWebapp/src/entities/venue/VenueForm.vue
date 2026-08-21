<template>
  <div class="card bg-base-100">
    <div class="card-body">
      <form @submit.prevent="handleSubmit" class="grid grid-cols-1 w-7/12 gap-4 mx-auto">
        <!-- <form @submit.prevent="handleSubmit" class=""> -->
        <div class="grid grid-cols-1 gap-4">
          <!-- Name -->
          <GenesisInput
            label="Name"
            :violation="venueViolations ? venueViolations['name'] : undefined"
            placeholder="Enter Name"
            type="text"
            v-model="formModel.name"
            :value="formModel.name"
          />

          <!-- Location -->
          <GenesisInput
            label="Location"
            :violation="venueViolations ? venueViolations['location'] : undefined"
            placeholder="Enter Location"
            type="text"
            v-model="formModel.location"
            :value="formModel.location"
          />
        </div>
        <ConcertTableForm
          :initial-data="concertsData"
          :get-table-data-callback="setConcerts"
          :violations="concertViolations"
          class="w-full"
        />

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
import { ref, computed, onMounted } from 'vue'
import { Venue, VenueFormDTO } from '@/models/VenueModel.ts'
import GenesisButton from '@/components/button/GenesisButton.vue'
import GenesisInput from '@/components/form/GenesisInput.vue'
import { ConcertFormDTO, type Concert } from '@/models/ConcertModel.ts'
import ConcertTableForm from '@/entities/concert/ConcertTableForm.vue'

const props = defineProps<{
  venue?: Venue

  concertsData?: Concert[]

  violations?: {
    venue: Record<string, string>
    concerts: Record<string, string>[]
  }

  submitLabel?: string
}>()

const emit = defineEmits<{
  (e: 'submit', payload: { venue: Partial<VenueFormDTO>; concerts: ConcertFormDTO[] }): void
  (e: 'cancel', payload: Partial<VenueFormDTO>): void
}>()

const venueViolations = computed(() => (props.violations ? props.violations.venue : {}))
const concertViolations = computed(() => (props.violations ? props.violations.concerts : []))

const formModel = ref<Partial<VenueFormDTO>>({ ...VenueFormDTO.parse(props.venue) })

const concerts = ref<ConcertFormDTO[]>([])
const setConcerts = (data: ConcertFormDTO[]) => {
  concerts.value = data
}

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

  emit('submit', { venue: data, concerts: concerts.value })
}

function cancelForm() {
  emit('cancel', formModel.value)
}
onMounted(async () => {
  if (Array.isArray(props.concertsData) && props.concertsData.length > 0) {
    setConcerts(ConcertFormDTO.parseList(props.concertsData))
  }
})
</script>
<style scoped></style>
