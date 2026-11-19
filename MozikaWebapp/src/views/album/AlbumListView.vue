<template>
  <div class="mz-page-view">
    <!-- Header -->
    <PageHeader :title="$t('entity.nav', { entity: 'Album' })" :subtitle="$t('entity.list.nav')">
      <template #actions>
        <LayoutSwitcher v-model="layoutMode" />
        <GenesisButton
          title="Create new album"
          @click="goToCreateFormView"
          class="mz-btn mz-btn-primary"
        >
          <PlusIcon />
          {{ $t('button.addEntity', { entity: 'Album' }) }}
        </GenesisButton>
      </template>
    </PageHeader>

    <!-- Table -->
    <EntityTable
      :entity-model="{}"
      :entity-search-fields="searchFields"
      :searchFn="searchAlbums"
      :getPaginationData="getPaginationData"
      :listComponent="AlbumList"
      :entities="albums"
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
import { Album } from '@/models/AlbumModel'
import AlbumList from '@/entities/album/AlbumList.vue'
import EntityTable from '@/components/table/EntityTable.vue'
import LayoutSwitcher from '@/components/button/LayoutSwitcher.vue'
import PageHeader from '@/components/common/PageHeader.vue'
import { useAlbums } from '@/composables/useAlbums'
import GenesisButton from '@/components/button/GenesisButton.vue'
import PlusIcon from '@/components/icons/PlusIcon.vue'

const {
  albums,
  searchAlbums,
  message,
  getPaginationData,
  goToCreateFormView,
  exportAlbumsToCsv,
  visibleListFields,
  loadColumnConfig,
  saveColumnConfig,
  layoutMode,
} = useAlbums()

const searchFields = ref<EntitySearchField[]>(Album.getAllSearchFieldsMetadata())

const handleUpdateVisibleFields = async (newFields: string[]) => {
  const result = await saveColumnConfig(newFields)
  if (!result.success) {
    console.error('Échec de la sauvegarde de la configuration des colonnes:', result.error)
  }
}

const onExport = async () => {
  await exportAlbumsToCsv(albums.value)
}

onMounted(() => {
  loadColumnConfig()
})
</script>
<style scoped></style>
