<template>
  <div class="mz-page-view">
    <!-- Header -->
    <PageHeader :title="$t('entity.nav', { entity: 'Artist' })" :subtitle="$t('entity.list.nav')">
      <template #actions>
        <LayoutSwitcher v-model="layoutMode" />
        <GenesisButton
          title="Create new artist"
          @click="goToCreateFormView"
          class="mz-btn mz-btn-primary"
        >
          <PlusIcon />
          {{ $t('button.addEntity', { entity: 'Artist' }) }}
        </GenesisButton>
      </template>
    </PageHeader>

    <!-- Table -->
    <EntityTable
      :entity-model="{}"
      :entity-search-fields="searchFields"
      :searchFn="searchArtists"
      :getPaginationData="getPaginationData"
      :listComponent="ArtistList"
      :entities="artists"
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
import { Artist } from '@/models/ArtistModel'
import ArtistList from '@/entities/artist/ArtistList.vue'
import EntityTable from '@/components/table/EntityTable.vue'
import LayoutSwitcher from '@/components/button/LayoutSwitcher.vue'
import PageHeader from '@/components/common/PageHeader.vue'
import { useArtists } from '@/composables/useArtists'
import GenesisButton from '@/components/button/GenesisButton.vue'
import PlusIcon from '@/components/icons/PlusIcon.vue'

const {
  artists,
  searchArtists,
  message,
  getPaginationData,
  goToCreateFormView,
  exportArtistsToCsv,
  visibleListFields,
  loadColumnConfig,
  saveColumnConfig,
  layoutMode,
} = useArtists()

const searchFields = ref<EntitySearchField[]>(Artist.getAllSearchFieldsMetadata())

const handleUpdateVisibleFields = async (newFields: string[]) => {
  const result = await saveColumnConfig(newFields)
  if (!result.success) {
    console.error('Échec de la sauvegarde de la configuration des colonnes:', result.error)
  }
}

const onExport = async () => {
  await exportArtistsToCsv(artists.value)
}

onMounted(() => {
  loadColumnConfig()
})
</script>
<style scoped></style>
