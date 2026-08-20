<template>
  <tr class="hover:bg-base-200">
    <template v-for="field in UserStatusHistory.getAllSearchFieldsMetadata()" :key="field.key">
      <td
        v-show="field.showInTable && visibleFields.includes(field.key)"
        class="px-3 py-2"
        :class="{
          'sticky left-0 bg-base-200 font-semibold': field.identifier,
        }"
      >
        <template v-if="field.type === 'date'">
          {{ formatDate(userStatusHistoryData[field.key]) }}
        </template>

        <template v-else-if="field.type === 'number'">
          {{
            userStatusHistoryData[field.key] != null
              ? $n(userStatusHistoryData[field.key], 'decimal')
              : '--'
          }}
        </template>

        <template v-else-if="field.type === 'select'">
          {{
            userStatusHistoryData[field.key] != null
              ? userStatusHistoryData[field.key].getKeyValue()
              : '--'
          }}
        </template>

        <template v-else-if="field.type === 'file'">
          <img
            v-if="userStatusHistoryData[field.key]"
            :src="getUrl(userStatusHistoryData[field.key])"
            :alt="field.key"
            class="max-h-10 max-w-10"
          />
        </template>

        <template v-else>
          {{ userStatusHistoryData[field.key] ?? '--' }}
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
import { UserStatusHistory } from '@/models/UserStatusHistoryModel'
import EntityRowActions from '@/components/table/EntityRowActions.vue'
import { useDateFormat } from '@/composables/useDateFormat'

const props = defineProps({
  userStatusHistory: {
    type: Object as PropType<UserStatusHistory>,
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

const userStatusHistoryData = computed(() => {
  return props.userStatusHistory as Record<string, any>
})

const emit = defineEmits<{
  (e: 'request-delete', userStatusHistory: UserStatusHistory): void
  (e: 'request-view', userStatusHistory: UserStatusHistory): void
  (e: 'request-update', userStatusHistory: UserStatusHistory): void
}>()

const deleteRow = () => emit('request-delete', props.userStatusHistory)
const viewRow = () => emit('request-view', props.userStatusHistory)
const updateRow = () => emit('request-update', props.userStatusHistory)
const { formatDate } = useDateFormat()
</script>
<style scoped></style>
