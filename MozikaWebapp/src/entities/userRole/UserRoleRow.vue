<template>
  <tr class="hover:bg-base-200">
    <template v-for="field in UserRole.getAllSearchFieldsMetadata()" :key="field.key">
      <td
        v-show="field.showInTable && visibleFields.includes(field.key)"
        class="px-3 py-2"
        :class="{
          'sticky left-0 bg-base-200 font-semibold': field.identifier,
        }"
      >
        <template v-if="field.type === 'date'">
          {{ formatDate(userRoleData[field.key]) }}
        </template>

        <template v-else-if="field.type === 'number'">
          {{ userRoleData[field.key] != null ? $n(userRoleData[field.key], 'decimal') : '--' }}
        </template>

        <template v-else-if="field.type === 'select'">
          {{ userRoleData[field.key] != null ? userRoleData[field.key].getKeyValue() : '--' }}
        </template>

        <template v-else-if="field.type === 'file'">
          <img
            v-if="userRoleData[field.key]"
            :src="getUrl(userRoleData[field.key])"
            :alt="field.key"
            class="max-h-10 max-w-10"
          />
        </template>

        <template v-else>
          {{ userRoleData[field.key] ?? '--' }}
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
import { UserRole } from '@/models/UserRoleModel'
import EntityRowActions from '@/components/table/EntityRowActions.vue'
import { useDateFormat } from '@/composables/useDateFormat'

const props = defineProps({
  userRole: {
    type: Object as PropType<UserRole>,
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

const userRoleData = computed(() => {
  return props.userRole as Record<string, any>
})

const emit = defineEmits<{
  (e: 'request-delete', userRole: UserRole): void
  (e: 'request-view', userRole: UserRole): void
  (e: 'request-update', userRole: UserRole): void
}>()

const deleteRow = () => emit('request-delete', props.userRole)
const viewRow = () => emit('request-view', props.userRole)
const updateRow = () => emit('request-update', props.userRole)
const { formatDate } = useDateFormat()
</script>
<style scoped></style>
