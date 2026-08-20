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
    @request-view="viewSong"
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
import SongTable from './SongTable.vue'
import SongGrid from './SongGrid.vue'
import { useSongs } from '@/composables/useSongs'
import { Song } from '@/models/SongModel'
import { usePopup } from '@/composables/usePopup'
import DeleteConfirmationPopup from '@/components/popup/DeleteConfirmationPopup.vue'
import { ref, computed, type PropType, type Component } from 'vue'
import { SortFieldParameter } from '@/models/api/RequestModel'

const props = defineProps({
  data: { type: Array as PropType<Song[]>, required: true },
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
  list: SongTable,
  card: SongGrid,
  // Exemples futurs :
  // kanban: SongKanban,
  // timeline: SongTimeline,
}

// Fallback sur SongTable si le mode n'existe pas (sécurité)
const currentLayoutComponent = computed<Component>(
  () => layoutComponents[props.layoutMode] ?? SongTable,
)

const selectedEntity = ref<Song>()
const { visible: deletePopup, closePopup } = usePopup(false)
const { deleteSong, viewSong, goToUpdateFormView } = useSongs()

const openDeletePopup = (entity: Song) => {
  if (!entity) return
  selectedEntity.value = entity
  deletePopup.value = true
}

const confirmDelete = async () => {
  const { success, error } = await deleteSong(selectedEntity.value!)
  if (success) {
    deletePopup.value = false
    emit('request:refresh')
  } else {
    console.error(error)
  }
}
</script>
<style scoped></style>
