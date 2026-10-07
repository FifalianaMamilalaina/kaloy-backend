<template>
  <tr class="hover:bg-base-200">
    <template v-for="field in SearchHistory.getAllSearchFieldsMetadata()" :key="field.key">
      <td
        v-show="field.showInTable && visibleFields.includes(field.key)"
        class="px-3 py-2"
        :class="{
          'sticky left-0 bg-base-200 font-semibold': field.identifier,
        }"
      >
        <template v-if="field.type === 'date'">
          {{ formatDate(searchHistoryData[field.key]) }}
        </template>

        <template v-else-if="field.type === 'number'">
          {{
            searchHistoryData[field.key] != null
              ? $n(searchHistoryData[field.key], 'decimal')
              : '--'
          }}
        </template>

        <template v-else-if="field.type === 'select'">
          {{
            searchHistoryData[field.key] != null ? searchHistoryData[field.key].getKeyValue() : '--'
          }}
        </template>

        <template v-else-if="field.type === 'file'">
          <img
            v-if="searchHistoryData[field.key]"
            :src="getUrl(searchHistoryData[field.key])"
            :alt="field.key"
            class="max-h-10 max-w-10"
          />
        </template>

        <template v-else>
          {{ searchHistoryData[field.key] ?? '--' }}
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
import { SearchHistory } from '@/models/SearchHistoryModel'
import EntityRowActions from '@/components/table/EntityRowActions.vue'
import { useDateFormat } from '@/composables/useDateFormat'

const props = defineProps({
  searchHistory: {
    type: Object as PropType<SearchHistory>,
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

const searchHistoryData = computed(() => {
  return props.searchHistory as Record<string, any>
})

const emit = defineEmits<{
  (e: 'request-delete', searchHistory: SearchHistory): void
  (e: 'request-view', searchHistory: SearchHistory): void
  (e: 'request-update', searchHistory: SearchHistory): void
}>()

const deleteRow = () => emit('request-delete', props.searchHistory)
const viewRow = () => emit('request-view', props.searchHistory)
const updateRow = () => emit('request-update', props.searchHistory)
const { formatDate } = useDateFormat()
</script>
<style scoped></style>
