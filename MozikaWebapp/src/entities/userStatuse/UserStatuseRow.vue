<template>
  <tr class="hover:bg-base-200">
    <template v-for="field in UserStatuse.getAllSearchFieldsMetadata()" :key="field.key">
      <td
        v-show="field.showInTable && visibleFields.includes(field.key)"
        class="px-3 py-2"
        :class="{
          'sticky left-0 bg-base-200 font-semibold': field.identifier,
        }"
      >
        <template v-if="field.type === 'date'">
          {{ formatDate(userStatuseData[field.key]) }}
        </template>

        <template v-else-if="field.type === 'number'">
          {{
            userStatuseData[field.key] != null ? $n(userStatuseData[field.key], 'decimal') : '--'
          }}
        </template>

        <template v-else-if="field.type === 'select'">
          {{ userStatuseData[field.key] != null ? userStatuseData[field.key].getKeyValue() : '--' }}
        </template>

        <template v-else-if="field.type === 'file'">
          <img
            v-if="userStatuseData[field.key]"
            :src="getUrl(userStatuseData[field.key])"
            :alt="field.key"
            class="max-h-10 max-w-10"
          />
        </template>

        <template v-else>
          {{ userStatuseData[field.key] ?? '--' }}
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
import { UserStatuse } from '@/models/UserStatuseModel'
import EntityRowActions from '@/components/table/EntityRowActions.vue'
import { useDateFormat } from '@/composables/useDateFormat'

const props = defineProps({
  userStatuse: {
    type: Object as PropType<UserStatuse>,
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

const userStatuseData = computed(() => {
  return props.userStatuse as Record<string, any>
})

const emit = defineEmits<{
  (e: 'request-delete', userStatuse: UserStatuse): void
  (e: 'request-view', userStatuse: UserStatuse): void
  (e: 'request-update', userStatuse: UserStatuse): void
}>()

const deleteRow = () => emit('request-delete', props.userStatuse)
const viewRow = () => emit('request-view', props.userStatuse)
const updateRow = () => emit('request-update', props.userStatuse)
const { formatDate } = useDateFormat()
</script>
<style scoped></style>
