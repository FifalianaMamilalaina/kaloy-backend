<template>
  <div
    class="card bg-base-100 shadow-md border border-base-300 hover:shadow-lg transition-shadow duration-200"
  >
    <div class="card-body p-4">
      <!-- Titre de la carte -->
      <h2 class="card-title text-base font-bold text-primary mb-2 truncate">
        <span> #{{ searchHistoryData['id'] }} - SearchHistory </span>
      </h2>

      <!-- Corps de la carte : affiche UNIQUEMENT les champs visibles -->
      <div class="space-y-1 text-sm flex-1">
        <template v-for="field in SearchHistory.getAllSearchFieldsMetadata()" :key="field.key">
          <div
            v-if="field.showInTable && visibleFields.includes(field.key)"
            class="flex justify-between gap-2 border-b border-base-200 py-1 last:border-0"
          >
            <span class="font-semibold text-base-content/70 text-xs uppercase tracking-wide">
              {{ field.label }}
            </span>
            <span class="text-right text-base-content truncate max-w-[60%]">
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
                  searchHistoryData[field.key] != null
                    ? searchHistoryData[field.key].getKeyValue()
                    : '--'
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
            </span>
          </div>
        </template>
      </div>

      <!-- Actions en bas de carte -->
      <div class="card-actions justify-end mt-3 pt-2 border-t border-base-200">
        <EntityRowActions
          :view="viewAction"
          :remove="removeAction"
          :edit="editAction"
          @delete-row="deleteRow"
          @view-row="viewRow"
          @edit-row="updateRow"
        />
      </div>
    </div>
  </div>
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
