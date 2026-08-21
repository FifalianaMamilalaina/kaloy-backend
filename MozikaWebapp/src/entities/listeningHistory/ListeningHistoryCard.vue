<template>
  <div
    class="card bg-base-100 shadow-md border border-base-300 hover:shadow-lg transition-shadow duration-200"
  >
    <div class="card-body p-4">
      <!-- Titre de la carte -->
      <h2 class="card-title text-base font-bold text-primary mb-2 truncate">
        <span> #{{ listeningHistoryData['id'] }} - ListeningHistory </span>
      </h2>

      <!-- Corps de la carte : affiche UNIQUEMENT les champs visibles -->
      <div class="space-y-1 text-sm flex-1">
        <template v-for="field in ListeningHistory.getAllSearchFieldsMetadata()" :key="field.key">
          <div
            v-if="field.showInTable && visibleFields.includes(field.key)"
            class="flex justify-between gap-2 border-b border-base-200 py-1 last:border-0"
          >
            <span class="font-semibold text-base-content/70 text-xs uppercase tracking-wide">
              {{ field.label }}
            </span>
            <span class="text-right text-base-content truncate max-w-[60%]">
              <template v-if="field.type === 'date'">
                {{ formatDate(listeningHistoryData[field.key]) }}
              </template>
              <template v-else-if="field.type === 'number'">
                {{
                  listeningHistoryData[field.key] != null
                    ? $n(listeningHistoryData[field.key], 'decimal')
                    : '--'
                }}
              </template>
              <template v-else-if="field.type === 'select'">
                {{
                  listeningHistoryData[field.key] != null
                    ? listeningHistoryData[field.key].getKeyValue()
                    : '--'
                }}
              </template>
              <template v-else-if="field.type === 'file'">
                <img
                  v-if="listeningHistoryData[field.key]"
                  :src="getUrl(listeningHistoryData[field.key])"
                  :alt="field.key"
                  class="max-h-10 max-w-10"
                />
              </template>
              <template v-else>
                {{ listeningHistoryData[field.key] ?? '--' }}
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
import { ListeningHistory } from '@/models/ListeningHistoryModel'
import EntityRowActions from '@/components/table/EntityRowActions.vue'
import { useDateFormat } from '@/composables/useDateFormat'

const props = defineProps({
  listeningHistory: {
    type: Object as PropType<ListeningHistory>,
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

const listeningHistoryData = computed(() => {
  return props.listeningHistory as Record<string, any>
})

const emit = defineEmits<{
  (e: 'request-delete', listeningHistory: ListeningHistory): void
  (e: 'request-view', listeningHistory: ListeningHistory): void
  (e: 'request-update', listeningHistory: ListeningHistory): void
}>()

const deleteRow = () => emit('request-delete', props.listeningHistory)
const viewRow = () => emit('request-view', props.listeningHistory)
const updateRow = () => emit('request-update', props.listeningHistory)
const { formatDate } = useDateFormat()
</script>
<style scoped></style>
