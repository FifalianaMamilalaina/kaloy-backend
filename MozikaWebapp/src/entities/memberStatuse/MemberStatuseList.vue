<template>
  <!-- View Switcher : Affiche dynamiquement le composant selon le mode -->
  <component
    :is="currentLayoutComponent"
    :data="data"
    :message="message"
    :loading="loading"
    :visible-fields="visibleFields"
    :view-action="viewAction"
    :edit-action="editAction"
    :remove-action="removeAction"
    @request:refresh="$emit('request:refresh')"
    @sortby="$emit('sortby', $event)"
    @request-view="viewMemberStatuse"
    @request-delete="openDeletePopup"
    @request-update="goToUpdateFormView"
  />

  <!-- Delete confirmation popup (Partagé) -->
  <DeleteConfirmationPopup
    :visible="deletePopup"
    :message="$t('confirmation.delete')"
    @confirm="confirmDelete"
    @cancel="closePopup"
  />
</template>

<script setup lang="ts">
import MemberStatuseTable from './MemberStatuseTable.vue'
import MemberStatuseGrid from './MemberStatuseGrid.vue'
import { useMemberStatuses } from '@/composables/useMemberStatuses'
import { MemberStatuse } from '@/models/MemberStatuseModel'
import { usePopup } from '@/composables/usePopup'
import DeleteConfirmationPopup from '@/components/popup/DeleteConfirmationPopup.vue'
import { ref, computed, type PropType, type Component } from 'vue'
import { SortFieldParameter } from '@/models/api/RequestModel'

const props = defineProps({
  data: { type: Array as PropType<MemberStatuse[]>, required: true },
  message: String,
  loading: { type: Boolean, default: false },
  viewAction: { type: Boolean, default: true },
  editAction: { type: Boolean, default: true },
  removeAction: { type: Boolean, default: true },
  visibleFields: { type: Array as PropType<string[]>, default: () => [] },
  layoutMode: { type: String as PropType<'list' | 'card'>, default: 'list' },
})

const emit = defineEmits<{
  (e: 'request:refresh'): void
  (e: 'sortby', sortData: SortFieldParameter): void
}>()

// Ajouter un nouveau mode = ajouter une seule ligne ici
const layoutComponents: Record<string, Component> = {
  list: MemberStatuseTable,
  card: MemberStatuseGrid,
  // Exemples futurs :
  // kanban: MemberStatuseKanban,
  // timeline: MemberStatuseTimeline,
}

// Fallback sur MemberStatuseTable si le mode n'existe pas (sécurité)
const currentLayoutComponent = computed<Component>(
  () => layoutComponents[props.layoutMode] ?? MemberStatuseTable,
)

const selectedEntity = ref<MemberStatuse>()
const { visible: deletePopup, closePopup } = usePopup(false)
const { deleteMemberStatuse, viewMemberStatuse, goToUpdateFormView } = useMemberStatuses()

const openDeletePopup = (entity: MemberStatuse) => {
  if (!entity) return
  selectedEntity.value = entity
  deletePopup.value = true
}

const confirmDelete = async () => {
  const { success, error } = await deleteMemberStatuse(selectedEntity.value!)
  if (success) {
    deletePopup.value = false
    emit('request:refresh')
  } else {
    console.error(error)
  }
}
</script>
<style scoped></style>
