<template>
  <tr class="hover:bg-base-200">
    <template v-for="field in ParticipationStatuse.getAllSearchFieldsMetadata()" :key="field.key">
      <td
        v-show="field.showInTable && visibleFields.includes(field.key)"
        class="px-3 py-2"
        :class="{
          'sticky left-0 bg-base-200 font-semibold': field.identifier,
        }"
      >
        <template v-if="field.type === 'date'">
          {{ formatDate(participationStatuseData[field.key]) }}
        </template>

        <template v-else-if="field.type === 'number'">
          {{
            participationStatuseData[field.key] != null
              ? $n(participationStatuseData[field.key], 'decimal')
              : '--'
          }}
        </template>

        <template v-else-if="field.type === 'select'">
          {{
            participationStatuseData[field.key] != null
              ? participationStatuseData[field.key].getKeyValue()
              : '--'
          }}
        </template>

        <template v-else-if="field.type === 'file'">
          <img
            v-if="participationStatuseData[field.key]"
            :src="getUrl(participationStatuseData[field.key])"
            :alt="field.key"
            class="max-h-10 max-w-10"
          />
        </template>

        <template v-else>
          {{ participationStatuseData[field.key] ?? '--' }}
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
import { ParticipationStatuse } from '@/models/ParticipationStatuseModel'
import EntityRowActions from '@/components/table/EntityRowActions.vue'
import { useDateFormat } from '@/composables/useDateFormat'

const props = defineProps({
  participationStatuse: {
    type: Object as PropType<ParticipationStatuse>,
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

const participationStatuseData = computed(() => {
  return props.participationStatuse as Record<string, any>
})

const emit = defineEmits<{
  (e: 'request-delete', participationStatuse: ParticipationStatuse): void
  (e: 'request-view', participationStatuse: ParticipationStatuse): void
  (e: 'request-update', participationStatuse: ParticipationStatuse): void
}>()

const deleteRow = () => emit('request-delete', props.participationStatuse)
const viewRow = () => emit('request-view', props.participationStatuse)
const updateRow = () => emit('request-update', props.participationStatuse)
const { formatDate } = useDateFormat()
</script>
<style scoped></style>
