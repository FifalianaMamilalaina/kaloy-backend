<template>
  <div class="mz-page-view">
    <!-- Header -->
    <PageHeader :title="$t('entity.nav', { entity: 'Song' })" :subtitle="$t('entity.list.nav')">
      <template #actions>
        <LayoutSwitcher v-model="layoutMode" />
        <GenesisButton
          title="Create new song"
          @click="goToCreateFormView"
          class="mz-btn mz-btn-primary"
        >
          <PlusIcon />
          {{ $t('button.addEntity', { entity: 'Song' }) }}
        </GenesisButton>
      </template>
    </PageHeader>

    <!-- Table -->
    <EntityTable
      :entity-model="{}"
      :entity-search-fields="searchFields"
      :searchFn="searchSongs"
      :getPaginationData="getPaginationData"
      :listComponent="SongList"
      :entities="songs"
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
import { Song } from '@/models/SongModel'
import SongList from '@/entities/song/SongList.vue'
import EntityTable from '@/components/table/EntityTable.vue'
import LayoutSwitcher from '@/components/button/LayoutSwitcher.vue'
import { useSongs } from '@/composables/useSongs'
import GenesisButton from '@/components/button/GenesisButton.vue'
import PlusIcon from '@/components/icons/PlusIcon.vue'
import PageHeader from '@/components/common/PageHeader.vue'

const {
  songs,
  searchSongs,
  message,
  getPaginationData,
  goToCreateFormView,
  exportSongsToCsv,
  visibleListFields,
  loadColumnConfig,
  saveColumnConfig,
  layoutMode,
} = useSongs()

const searchFields = ref<EntitySearchField[]>(Song.getAllSearchFieldsMetadata())

const handleUpdateVisibleFields = async (newFields: string[]) => {
  const result = await saveColumnConfig(newFields)
  if (!result.success) {
    console.error('Échec de la sauvegarde de la configuration des colonnes:', result.error)
  }
}

const onExport = async () => {
  await exportSongsToCsv(songs.value)
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
