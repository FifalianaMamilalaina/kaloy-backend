<template>
  <tr class="hover:bg-base-200">
    <template v-for="field in ArtistGroupMember.getAllSearchFieldsMetadata()" :key="field.key">
      <td
        v-show="field.showInTable && visibleFields.includes(field.key)"
        class="px-3 py-2"
        :class="{
          'sticky left-0 bg-base-200 font-semibold': field.identifier,
        }"
      >
        <template v-if="field.type === 'date'">
          {{ formatDate(artistGroupMemberData[field.key]) }}
        </template>

        <template v-else-if="field.type === 'number'">
          {{
            artistGroupMemberData[field.key] != null
              ? $n(artistGroupMemberData[field.key], 'decimal')
              : '--'
          }}
        </template>

        <template v-else-if="field.type === 'select'">
          {{
            artistGroupMemberData[field.key] != null
              ? artistGroupMemberData[field.key].getKeyValue()
              : '--'
          }}
        </template>

        <template v-else-if="field.type === 'file'">
          <img
            v-if="artistGroupMemberData[field.key]"
            :src="getUrl(artistGroupMemberData[field.key])"
            :alt="field.key"
            class="max-h-10 max-w-10"
          />
        </template>

        <template v-else>
          {{ artistGroupMemberData[field.key] ?? '--' }}
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
import { ArtistGroupMember } from '@/models/ArtistGroupMemberModel'
import EntityRowActions from '@/components/table/EntityRowActions.vue'
import { useDateFormat } from '@/composables/useDateFormat'

const props = defineProps({
  artistGroupMember: {
    type: Object as PropType<ArtistGroupMember>,
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

const artistGroupMemberData = computed(() => {
  return props.artistGroupMember as Record<string, any>
})

const emit = defineEmits<{
  (e: 'request-delete', artistGroupMember: ArtistGroupMember): void
  (e: 'request-view', artistGroupMember: ArtistGroupMember): void
  (e: 'request-update', artistGroupMember: ArtistGroupMember): void
}>()

const deleteRow = () => emit('request-delete', props.artistGroupMember)
const viewRow = () => emit('request-view', props.artistGroupMember)
const updateRow = () => emit('request-update', props.artistGroupMember)
const { formatDate } = useDateFormat()
</script>
<style scoped></style>
