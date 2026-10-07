<template>
  <tr class="hover:bg-base-200">
    <template v-for="field in AudioStorageType.getAllSearchFieldsMetadata()" :key="field.key">
      <td
        v-show="field.showInTable && visibleFields.includes(field.key)"
        class="px-3 py-2"
        :class="{
          'sticky left-0 bg-base-200 font-semibold': field.identifier,
        }"
      >
        <template v-if="field.type === 'date'">
          {{ formatDate(audioStorageTypeData[field.key]) }}
        </template>

        <template v-else-if="field.type === 'number'">
          {{
            audioStorageTypeData[field.key] != null
              ? $n(audioStorageTypeData[field.key], 'decimal')
              : '--'
          }}
        </template>

        <template v-else-if="field.type === 'select'">
          {{
            audioStorageTypeData[field.key] != null
              ? audioStorageTypeData[field.key].getKeyValue()
              : '--'
          }}
        </template>

        <template v-else-if="field.type === 'file'">
          <img
            v-if="audioStorageTypeData[field.key]"
            :src="getUrl(audioStorageTypeData[field.key])"
            :alt="field.key"
            class="max-h-10 max-w-10"
          />
        </template>

        <template v-else>
          {{ audioStorageTypeData[field.key] ?? '--' }}
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
import { AudioStorageType } from '@/models/AudioStorageTypeModel'
import EntityRowActions from '@/components/table/EntityRowActions.vue'
import { useDateFormat } from '@/composables/useDateFormat'

const props = defineProps({
  audioStorageType: {
    type: Object as PropType<AudioStorageType>,
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

const audioStorageTypeData = computed(() => {
  return props.audioStorageType as Record<string, any>
})

const emit = defineEmits<{
  (e: 'request-delete', audioStorageType: AudioStorageType): void
  (e: 'request-view', audioStorageType: AudioStorageType): void
  (e: 'request-update', audioStorageType: AudioStorageType): void
}>()

const deleteRow = () => emit('request-delete', props.audioStorageType)
const viewRow = () => emit('request-view', props.audioStorageType)
const updateRow = () => emit('request-update', props.audioStorageType)
const { formatDate } = useDateFormat()
</script>
<style scoped></style>
