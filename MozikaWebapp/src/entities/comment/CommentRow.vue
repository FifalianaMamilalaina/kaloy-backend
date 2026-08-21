<template>
  <tr class="hover:bg-base-200">
    <template v-for="field in Comment.getAllSearchFieldsMetadata()" :key="field.key">
      <td
        v-show="field.showInTable && visibleFields.includes(field.key)"
        class="px-3 py-2"
        :class="{
          'sticky left-0 bg-base-200 font-semibold': field.identifier,
        }"
      >
        <template v-if="field.type === 'date'">
          {{ formatDate(commentData[field.key]) }}
        </template>

        <template v-else-if="field.type === 'number'">
          {{ commentData[field.key] != null ? $n(commentData[field.key], 'decimal') : '--' }}
        </template>

        <template v-else-if="field.type === 'select'">
          {{ commentData[field.key] != null ? commentData[field.key].getKeyValue() : '--' }}
        </template>

        <template v-else-if="field.type === 'file'">
          <img
            v-if="commentData[field.key]"
            :src="getUrl(commentData[field.key])"
            :alt="field.key"
            class="max-h-10 max-w-10"
          />
        </template>

        <template v-else>
          {{ commentData[field.key] ?? '--' }}
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
import { Comment } from '@/models/CommentModel'
import EntityRowActions from '@/components/table/EntityRowActions.vue'
import { useDateFormat } from '@/composables/useDateFormat'

const props = defineProps({
  comment: {
    type: Object as PropType<Comment>,
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

const commentData = computed(() => {
  return props.comment as Record<string, any>
})

const emit = defineEmits<{
  (e: 'request-delete', comment: Comment): void
  (e: 'request-view', comment: Comment): void
  (e: 'request-update', comment: Comment): void
}>()

const deleteRow = () => emit('request-delete', props.comment)
const viewRow = () => emit('request-view', props.comment)
const updateRow = () => emit('request-update', props.comment)
const { formatDate } = useDateFormat()
</script>
<style scoped></style>
