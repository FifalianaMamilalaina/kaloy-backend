<template>
  <tr class="hover:bg-base-200">
    <template v-for="field in NotificationType.getAllSearchFieldsMetadata()" :key="field.key">
      <td
        v-show="field.showInTable && visibleFields.includes(field.key)"
        class="px-3 py-2"
        :class="{
          'sticky left-0 bg-base-200 font-semibold': field.identifier,
        }"
      >
        <template v-if="field.type === 'date'">
          {{ formatDate(notificationTypeData[field.key]) }}
        </template>

        <template v-else-if="field.type === 'number'">
          {{
            notificationTypeData[field.key] != null
              ? $n(notificationTypeData[field.key], 'decimal')
              : '--'
          }}
        </template>

        <template v-else-if="field.type === 'select'">
          {{
            notificationTypeData[field.key] != null
              ? notificationTypeData[field.key].getKeyValue()
              : '--'
          }}
        </template>

        <template v-else-if="field.type === 'file'">
          <img
            v-if="notificationTypeData[field.key]"
            :src="getUrl(notificationTypeData[field.key])"
            :alt="field.key"
            class="max-h-10 max-w-10"
          />
        </template>

        <template v-else>
          {{ notificationTypeData[field.key] ?? '--' }}
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
import { NotificationType } from '@/models/NotificationTypeModel'
import EntityRowActions from '@/components/table/EntityRowActions.vue'
import { useDateFormat } from '@/composables/useDateFormat'

const props = defineProps({
  notificationType: {
    type: Object as PropType<NotificationType>,
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

const notificationTypeData = computed(() => {
  return props.notificationType as Record<string, any>
})

const emit = defineEmits<{
  (e: 'request-delete', notificationType: NotificationType): void
  (e: 'request-view', notificationType: NotificationType): void
  (e: 'request-update', notificationType: NotificationType): void
}>()

const deleteRow = () => emit('request-delete', props.notificationType)
const viewRow = () => emit('request-view', props.notificationType)
const updateRow = () => emit('request-update', props.notificationType)
const { formatDate } = useDateFormat()
</script>
<style scoped></style>
