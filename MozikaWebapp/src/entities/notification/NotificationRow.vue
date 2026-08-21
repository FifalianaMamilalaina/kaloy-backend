<template>
  <tr class="hover:bg-base-200">
    <template v-for="field in Notification.getAllSearchFieldsMetadata()" :key="field.key">
      <td
        v-show="field.showInTable && visibleFields.includes(field.key)"
        class="px-3 py-2"
        :class="{
          'sticky left-0 bg-base-200 font-semibold': field.identifier,
        }"
      >
        <template v-if="field.type === 'date'">
          {{ formatDate(notificationData[field.key]) }}
        </template>

        <template v-else-if="field.type === 'number'">
          {{
            notificationData[field.key] != null ? $n(notificationData[field.key], 'decimal') : '--'
          }}
        </template>

        <template v-else-if="field.type === 'select'">
          {{
            notificationData[field.key] != null ? notificationData[field.key].getKeyValue() : '--'
          }}
        </template>

        <template v-else-if="field.type === 'file'">
          <img
            v-if="notificationData[field.key]"
            :src="getUrl(notificationData[field.key])"
            :alt="field.key"
            class="max-h-10 max-w-10"
          />
        </template>

        <template v-else>
          {{ notificationData[field.key] ?? '--' }}
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
import { Notification } from '@/models/NotificationModel'
import EntityRowActions from '@/components/table/EntityRowActions.vue'
import { useDateFormat } from '@/composables/useDateFormat'

const props = defineProps({
  notification: {
    type: Object as PropType<Notification>,
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

const notificationData = computed(() => {
  return props.notification as Record<string, any>
})

const emit = defineEmits<{
  (e: 'request-delete', notification: Notification): void
  (e: 'request-view', notification: Notification): void
  (e: 'request-update', notification: Notification): void
}>()

const deleteRow = () => emit('request-delete', props.notification)
const viewRow = () => emit('request-view', props.notification)
const updateRow = () => emit('request-update', props.notification)
const { formatDate } = useDateFormat()
</script>
<style scoped></style>
