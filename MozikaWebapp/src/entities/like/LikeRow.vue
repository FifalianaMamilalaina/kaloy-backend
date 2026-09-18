<template>
  <tr class="hover:bg-base-200">
    <template v-for="field in Like.getAllSearchFieldsMetadata()" :key="field.key">
      <td
        v-show="field.showInTable && visibleFields.includes(field.key)"
        class="px-3 py-2"
        :class="{
          'sticky left-0 bg-base-200 font-semibold': field.identifier,
        }"
      >
        <template v-if="field.type === 'date'">
          {{ formatDate(likeData[field.key]) }}
        </template>

        <template v-else-if="field.type === 'number'">
          {{ likeData[field.key] != null ? $n(likeData[field.key], 'decimal') : '--' }}
        </template>

        <template v-else-if="field.type === 'select'">
          {{ likeData[field.key] != null ? likeData[field.key].getKeyValue() : '--' }}
        </template>

        <template v-else-if="field.type === 'file'">
          <img
            v-if="likeData[field.key]"
            :src="getUrl(likeData[field.key])"
            :alt="field.key"
            class="max-h-10 max-w-10"
          />
        </template>

        <template v-else>
          {{ likeData[field.key] ?? '--' }}
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
import { Like } from '@/models/LikeModel'
import EntityRowActions from '@/components/table/EntityRowActions.vue'
import { useDateFormat } from '@/composables/useDateFormat'

const props = defineProps({
  like: {
    type: Object as PropType<Like>,
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

const likeData = computed(() => {
  return props.like as Record<string, any>
})

const emit = defineEmits<{
  (e: 'request-delete', like: Like): void
  (e: 'request-view', like: Like): void
  (e: 'request-update', like: Like): void
}>()

const deleteRow = () => emit('request-delete', props.like)
const viewRow = () => emit('request-view', props.like)
const updateRow = () => emit('request-update', props.like)
const { formatDate } = useDateFormat()
</script>
<style scoped></style>
