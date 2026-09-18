<template>
  <div class="w-full">
    <!-- Header -->
    <div class="flex items-center justify-between mb-6">
      <h3 class="text-xl font-semibold text-base-content">
        {{ $t('entity.nav', { entity: 'PlaylistVisibilitie' }) }}
        <span class="text-base-content/50 font-normal"> {{ $t('entity.list.nav') }}</span>
      </h3>
      <div class="flex items-center gap-3">
        <!-- Composant réutilisable pour le basculement de vue -->
        <LayoutSwitcher v-model="layoutMode" />

        <GenesisButton
          title="Create new playlistVisibilitie"
          @click="goToCreateFormView"
          class="btn-secondary"
        >
          <PlusIcon />
          {{ $t('button.addEntity', { entity: 'PlaylistVisibilitie' }) }}
        </GenesisButton>
      </div>
    </div>

    <!-- Table -->
    <EntityTable
      :entity-model="{}"
      :entity-search-fields="searchFields"
      :searchFn="searchPlaylistVisibilities"
      :getPaginationData="getPaginationData"
      :listComponent="PlaylistVisibilitieList"
      :entities="playlistVisibilities"
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
import { PlaylistVisibilitie } from '@/models/PlaylistVisibilitieModel'
import PlaylistVisibilitieList from '@/entities/playlistVisibilitie/PlaylistVisibilitieList.vue'
import EntityTable from '@/components/table/EntityTable.vue'
import LayoutSwitcher from '@/components/button/LayoutSwitcher.vue'
import { usePlaylistVisibilities } from '@/composables/usePlaylistVisibilities'
import GenesisButton from '@/components/button/GenesisButton.vue'
import PlusIcon from '@/components/icons/PlusIcon.vue'

const {
  playlistVisibilities,
  searchPlaylistVisibilities,
  message,
  getPaginationData,
  goToCreateFormView,
  exportPlaylistVisibilitiesToCsv,
  visibleListFields,
  loadColumnConfig,
  saveColumnConfig,
  layoutMode,
} = usePlaylistVisibilities()

const searchFields = ref<EntitySearchField[]>(PlaylistVisibilitie.getAllSearchFieldsMetadata())

const handleUpdateVisibleFields = async (newFields: string[]) => {
  const result = await saveColumnConfig(newFields)
  if (!result.success) {
    console.error('Échec de la sauvegarde de la configuration des colonnes:', result.error)
  }
}

const onExport = async () => {
  await exportPlaylistVisibilitiesToCsv(playlistVisibilities.value)
}

onMounted(() => {
  loadColumnConfig()
})
</script>
<style scoped></style>
