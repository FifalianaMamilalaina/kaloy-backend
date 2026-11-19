<template>
  <div class="mz-page-view">
    <!-- Header -->
    <PageHeader :title="$t('entity.nav', { entity: 'Event' })" :subtitle="$t('entity.list.nav')">
      <template #actions>
        <LayoutSwitcher v-model="layoutMode" />
        <GenesisButton
          title="Create new event"
          @click="goToCreateFormView"
          class="mz-btn mz-btn-primary"
        >
          <PlusIcon />
          {{ $t('button.addEntity', { entity: 'Event' }) }}
        </GenesisButton>
      </template>
    </PageHeader>

    <!-- Table -->
    <EntityTable
      :entity-model="{}"
      :entity-search-fields="searchFields"
      :searchFn="searchEvents"
      :getPaginationData="getPaginationData"
      :listComponent="EventList"
      :entities="events"
      :message="message"
      :visible-fields="visibleListFields"
      :layout-mode="layoutMode"
      @update:visible-fields="handleUpdateVisibleFields"
      @export:csv="onExport"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import type { EntitySearchField } from '@/models/EntityModel'
import { Event } from '@/models/EventModel'
import EventList from '@/entities/event/EventList.vue'
import EntityTable from '@/components/table/EntityTable.vue'
import LayoutSwitcher from '@/components/button/LayoutSwitcher.vue'
import { useEvents } from '@/composables/useEvents'
import GenesisButton from '@/components/button/GenesisButton.vue'
import PlusIcon from '@/components/icons/PlusIcon.vue'
import PageHeader from '@/components/common/PageHeader.vue'

const {
  events,
  searchEvents,
  message,
  getPaginationData,
  goToCreateFormView,
  exportEventsToCsv,
  visibleListFields,
  loadColumnConfig,
  saveColumnConfig,
  layoutMode,
} = useEvents()

const searchFields = ref<EntitySearchField[]>(Event.getAllSearchFieldsMetadata())

const handleUpdateVisibleFields = async (newFields: string[]) => {
  const result = await saveColumnConfig(newFields)
  if (!result.success) {
    console.error('Échec de la sauvegarde de la configuration des colonnes:', result.error)
  }
}

const onExport = async () => {
  await exportEventsToCsv(events.value)
}

onMounted(() => {
  loadColumnConfig()
})
</script>
<style scoped>
.mz-page-view {
  width: 100%;
  padding: var(--mz-space-2);
}
</style>
