<template>
  <tr class="hover:bg-base-200">
    <template v-for="field in NotificationPreference.getAllSearchFieldsMetadata()" :key="field.key">
      <td
        v-show="field.showInTable && visibleFields.includes(field.key)"
        class="px-3 py-2"
        :class="{
          'sticky left-0 bg-base-200 font-semibold': field.identifier,
        }"
      >
        <template v-if="field.type === 'date'">
          {{ formatDate(notificationPreferenceData[field.key]) }}
        </template>

        <template v-else-if="field.type === 'number'">
          {{
            notificationPreferenceData[field.key] != null
              ? $n(notificationPreferenceData[field.key], 'decimal')
              : '--'
          }}
        </template>

        <template v-else-if="field.type === 'select'">
          {{
            notificationPreferenceData[field.key] != null
              ? notificationPreferenceData[field.key].getKeyValue()
              : '--'
          }}
        </template>

        <template v-else-if="field.type === 'file'">
          <img
            v-if="notificationPreferenceData[field.key]"
            :src="getUrl(notificationPreferenceData[field.key])"
            :alt="field.key"
            class="max-h-10 max-w-10"
          />
        </template>

        <template v-else>
          {{ notificationPreferenceData[field.key] ?? '--' }}
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
import { NotificationPreference } from '@/models/NotificationPreferenceModel'
import EntityRowActions from '@/components/table/EntityRowActions.vue'
import { useDateFormat } from '@/composables/useDateFormat'

const props = defineProps({
  notificationPreference: {
    type: Object as PropType<NotificationPreference>,
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

const notificationPreferenceData = computed(() => {
  return props.notificationPreference as Record<string, any>
})

const emit = defineEmits<{
  (e: 'request-delete', notificationPreference: NotificationPreference): void
  (e: 'request-view', notificationPreference: NotificationPreference): void
  (e: 'request-update', notificationPreference: NotificationPreference): void
}>()

const deleteRow = () => emit('request-delete', props.notificationPreference)
const viewRow = () => emit('request-view', props.notificationPreference)
const updateRow = () => emit('request-update', props.notificationPreference)
const { formatDate } = useDateFormat()
</script>
<style scoped></style>
