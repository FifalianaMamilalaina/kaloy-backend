<template>
  <tr class="hover:bg-base-200">
    <template v-for="field in User.getAllSearchFieldsMetadata()" :key="field.key">
      <td
        v-show="field.showInTable && visibleFields.includes(field.key)"
        class="px-3 py-2"
        :class="{
          'sticky left-0 bg-base-200 font-semibold': field.identifier,
        }"
      >
        <template v-if="field.type === 'date'">
          {{ formatDate(userData[field.key]) }}
        </template>

        <template v-else-if="field.type === 'number'">
          {{ userData[field.key] != null ? $n(userData[field.key], 'decimal') : '--' }}
        </template>

        <template v-else-if="field.type === 'select'">
          {{ userData[field.key] != null ? userData[field.key].getKeyValue() : '--' }}
        </template>

        <template v-else-if="field.type === 'file'">
          <img
            v-if="userData[field.key]"
            :src="getUrl(userData[field.key])"
            :alt="field.key"
            class="max-h-10 max-w-10"
          />
        </template>

        <template v-else>
          {{ userData[field.key] ?? '--' }}
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
import { User } from '@/models/UserModel'
import EntityRowActions from '@/components/table/EntityRowActions.vue'
import { useDateFormat } from '@/composables/useDateFormat'

const props = defineProps({
  user: {
    type: Object as PropType<User>,
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

const userData = computed(() => {
  return props.user as Record<string, any>
})

const emit = defineEmits<{
  (e: 'request-delete', user: User): void
  (e: 'request-view', user: User): void
  (e: 'request-update', user: User): void
}>()

const deleteRow = () => emit('request-delete', props.user)
const viewRow = () => emit('request-view', props.user)
const updateRow = () => emit('request-update', props.user)
const { formatDate } = useDateFormat()
</script>
<style scoped></style>
