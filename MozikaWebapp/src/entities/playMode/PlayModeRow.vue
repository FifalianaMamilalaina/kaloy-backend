<template>
  <tr class="hover:bg-base-200">
    <template v-for="field in PlayMode.getAllSearchFieldsMetadata()" :key="field.key">
      <td
        v-show="field.showInTable && visibleFields.includes(field.key)"
        class="px-3 py-2"
        :class="{
          'sticky left-0 bg-base-200 font-semibold': field.identifier,
        }"
      >
        <template v-if="field.type === 'date'">
          {{ formatDate(playModeData[field.key]) }}
        </template>

        <template v-else-if="field.type === 'number'">
          {{ playModeData[field.key] != null ? $n(playModeData[field.key], 'decimal') : '--' }}
        </template>

        <template v-else-if="field.type === 'select'">
          {{ playModeData[field.key] != null ? playModeData[field.key].getKeyValue() : '--' }}
        </template>

        <template v-else-if="field.type === 'file'">
          <img
            v-if="playModeData[field.key]"
            :src="getUrl(playModeData[field.key])"
            :alt="field.key"
            class="max-h-10 max-w-10"
          />
        </template>

        <template v-else>
          {{ playModeData[field.key] ?? '--' }}
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
import { PlayMode } from '@/models/PlayModeModel'
import EntityRowActions from '@/components/table/EntityRowActions.vue'
import { useDateFormat } from '@/composables/useDateFormat'

const props = defineProps({
  playMode: {
    type: Object as PropType<PlayMode>,
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

const playModeData = computed(() => {
  return props.playMode as Record<string, any>
})

const emit = defineEmits<{
  (e: 'request-delete', playMode: PlayMode): void
  (e: 'request-view', playMode: PlayMode): void
  (e: 'request-update', playMode: PlayMode): void
}>()

const deleteRow = () => emit('request-delete', props.playMode)
const viewRow = () => emit('request-view', props.playMode)
const updateRow = () => emit('request-update', props.playMode)
const { formatDate } = useDateFormat()
</script>
<style scoped></style>
