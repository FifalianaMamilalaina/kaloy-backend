<template>
  <tr class="hover:bg-base-200">
    <template v-for="field in Client.getAllSearchFieldsMetadata()" :key="field.key">
      <td
        v-show="field.showInTable && visibleFields.includes(field.key)"
        class="px-3 py-2"
        :class="{
          'sticky left-0 bg-base-200 font-semibold': field.identifier,
        }"
      >
        <template v-if="field.type === 'date'">
          {{ formatDate(clientData[field.key]) }}
        </template>

        <template v-else-if="field.type === 'number'">
          {{ clientData[field.key] != null ? $n(clientData[field.key], 'decimal') : '--' }}
        </template>

        <template v-else-if="field.type === 'select'">
          {{ clientData[field.key] != null ? clientData[field.key].getKeyValue() : '--' }}
        </template>

        <template v-else-if="field.type === 'file'">
          <img
            v-if="clientData[field.key]"
            :src="getUrl(clientData[field.key])"
            :alt="field.key"
            class="max-h-10 max-w-10"
          />
        </template>

        <template v-else>
          {{ clientData[field.key] ?? '--' }}
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
import { Client } from '@/models/ClientModel'
import EntityRowActions from '@/components/table/EntityRowActions.vue'
import { useDateFormat } from '@/composables/useDateFormat'

const props = defineProps({
  client: {
    type: Object as PropType<Client>,
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

const clientData = computed(() => {
  return props.client as Record<string, any>
})

const emit = defineEmits<{
  (e: 'request-delete', client: Client): void
  (e: 'request-view', client: Client): void
  (e: 'request-update', client: Client): void
}>()

const deleteRow = () => emit('request-delete', props.client)
const viewRow = () => emit('request-view', props.client)
const updateRow = () => emit('request-update', props.client)
const { formatDate } = useDateFormat()
</script>
<style scoped></style>
