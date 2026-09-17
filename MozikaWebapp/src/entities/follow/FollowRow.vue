<template>
  <tr class="hover:bg-base-200">
    <template v-for="field in Follow.getAllSearchFieldsMetadata()" :key="field.key">
      <td
        v-show="field.showInTable && visibleFields.includes(field.key)"
        class="px-3 py-2"
        :class="{
          'sticky left-0 bg-base-200 font-semibold': field.identifier,
        }"
      >
        <template v-if="field.type === 'date'">
          {{ formatDate(followData[field.key]) }}
        </template>

        <template v-else-if="field.type === 'number'">
          {{ followData[field.key] != null ? $n(followData[field.key], 'decimal') : '--' }}
        </template>

        <template v-else-if="field.type === 'select'">
          {{ followData[field.key] != null ? followData[field.key].getKeyValue() : '--' }}
        </template>

        <template v-else-if="field.type === 'file'">
          <img
            v-if="followData[field.key]"
            :src="getUrl(followData[field.key])"
            :alt="field.key"
            class="max-h-10 max-w-10"
          />
        </template>

        <template v-else>
          {{ followData[field.key] ?? '--' }}
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
import { Follow } from '@/models/FollowModel'
import EntityRowActions from '@/components/table/EntityRowActions.vue'
import { useDateFormat } from '@/composables/useDateFormat'

const props = defineProps({
  follow: {
    type: Object as PropType<Follow>,
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

const followData = computed(() => {
  return props.follow as Record<string, any>
})

const emit = defineEmits<{
  (e: 'request-delete', follow: Follow): void
  (e: 'request-view', follow: Follow): void
  (e: 'request-update', follow: Follow): void
}>()

const deleteRow = () => emit('request-delete', props.follow)
const viewRow = () => emit('request-view', props.follow)
const updateRow = () => emit('request-update', props.follow)
const { formatDate } = useDateFormat()
</script>
<style scoped></style>
