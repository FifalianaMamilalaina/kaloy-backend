<template>
  <tr class="hover:bg-base-200">
    <template v-for="field in VerificationChannel.getAllSearchFieldsMetadata()" :key="field.key">
      <td
        v-show="field.showInTable && visibleFields.includes(field.key)"
        class="px-3 py-2"
        :class="{
          'sticky left-0 bg-base-200 font-semibold': field.identifier,
        }"
      >
        <template v-if="field.type === 'date'">
          {{ formatDate(verificationChannelData[field.key]) }}
        </template>

        <template v-else-if="field.type === 'number'">
          {{
            verificationChannelData[field.key] != null
              ? $n(verificationChannelData[field.key], 'decimal')
              : '--'
          }}
        </template>

        <template v-else-if="field.type === 'select'">
          {{
            verificationChannelData[field.key] != null
              ? verificationChannelData[field.key].getKeyValue()
              : '--'
          }}
        </template>

        <template v-else-if="field.type === 'file'">
          <img
            v-if="verificationChannelData[field.key]"
            :src="getUrl(verificationChannelData[field.key])"
            :alt="field.key"
            class="max-h-10 max-w-10"
          />
        </template>

        <template v-else>
          {{ verificationChannelData[field.key] ?? '--' }}
        </template>
      </td>
    </template>

    <!-- Actions -->
    <td class="px-3 py-2 bg-base-200 sticky right-0 text-end">
      <div class="rounded-md inline-flex">
        <EntityRowActions
          :view="viewAction"
          :remove="removeAction"
          :edit="editAction"
          @delete-row="deleteRow"
          @view-row="viewRow"
          @edit-row="updateRow"
        />
      </div>
    </td>
  </tr>
</template>

<script setup lang="ts">
import { computed, type PropType } from 'vue'
import { VerificationChannel } from '@/models/VerificationChannelModel'
import EntityRowActions from '@/components/table/EntityRowActions.vue'
import { useDateFormat } from '@/composables/useDateFormat'

const props = defineProps({
  verificationChannel: {
    type: Object as PropType<VerificationChannel>,
    required: true,
  },
  viewAction: {
    type: Boolean,
    default: true,
  },
  editAction: {
    type: Boolean,
    default: true,
  },
  removeAction: {
    type: Boolean,
    default: true,
  },
  visibleFields: {
    type: Array as PropType<string[]>,
    default: () => [],
  },
})

const verificationChannelData = computed(() => {
  return props.verificationChannel as Record<string, any>
})

const emit = defineEmits<{
  (e: 'request-delete', verificationChannel: VerificationChannel): void
  (e: 'request-view', verificationChannel: VerificationChannel): void
  (e: 'request-update', verificationChannel: VerificationChannel): void
}>()

const deleteRow = () => emit('request-delete', props.verificationChannel)
const viewRow = () => emit('request-view', props.verificationChannel)
const updateRow = () => emit('request-update', props.verificationChannel)
const { formatDate } = useDateFormat()
</script>
<style scoped></style>
