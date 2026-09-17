<template>
  <tr class="hover:bg-base-200">
    <template v-for="field in Genre.getAllSearchFieldsMetadata()" :key="field.key">
      <td
        v-show="field.showInTable && visibleFields.includes(field.key)"
        class="px-3 py-2"
        :class="{
          'sticky left-0 bg-base-200 font-semibold': field.identifier,
        }"
      >
        <template v-if="field.type === 'date'">
          {{ formatDate(genreData[field.key]) }}
        </template>

        <template v-else-if="field.type === 'number'">
          {{ genreData[field.key] != null ? $n(genreData[field.key], 'decimal') : '--' }}
        </template>

        <template v-else-if="field.type === 'select'">
          {{ genreData[field.key] != null ? genreData[field.key].getKeyValue() : '--' }}
        </template>

        <template v-else-if="field.type === 'file'">
          <img
            v-if="genreData[field.key]"
            :src="getUrl(genreData[field.key])"
            :alt="field.key"
            class="max-h-10 max-w-10"
          />
        </template>

        <template v-else>
          {{ genreData[field.key] ?? '--' }}
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
import { Genre } from '@/models/GenreModel'
import EntityRowActions from '@/components/table/EntityRowActions.vue'
import { useDateFormat } from '@/composables/useDateFormat'

const props = defineProps({
  genre: {
    type: Object as PropType<Genre>,
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

const genreData = computed(() => {
  return props.genre as Record<string, any>
})

const emit = defineEmits<{
  (e: 'request-delete', genre: Genre): void
  (e: 'request-view', genre: Genre): void
  (e: 'request-update', genre: Genre): void
}>()

const deleteRow = () => emit('request-delete', props.genre)
const viewRow = () => emit('request-view', props.genre)
const updateRow = () => emit('request-update', props.genre)
const { formatDate } = useDateFormat()
</script>
<style scoped></style>
