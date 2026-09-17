<template>
  <div class="w-full">
    <!-- Header -->
    <div class="flex items-center justify-between mb-6">
      <h3 class="text-xl font-semibold text-base-content">
        {{ $t('entity.nav', { entity: 'Playlist' }) }}
        <span class="text-base-content/50 font-normal"> {{ $t('entity.list.nav') }}</span>
      </h3>
      <div class="flex items-center gap-3">
        <!-- Composant réutilisable pour le basculement de vue -->
        <LayoutSwitcher v-model="layoutMode" />

        <GenesisButton
          title="Create new playlist"
          @click="goToCreateFormView"
          class="btn-secondary"
        >
          <PlusIcon />
          {{ $t('button.addEntity', { entity: 'Playlist' }) }}
        </GenesisButton>
      </div>
    </div>

    <!-- Table -->
    <EntityTable
      :entity-model="{}"
      :entity-search-fields="searchFields"
      :searchFn="searchPlaylists"
      :getPaginationData="getPaginationData"
      :listComponent="PlaylistList"
      :entities="playlists"
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
import { Playlist } from '@/models/PlaylistModel'
import PlaylistList from '@/entities/playlist/PlaylistList.vue'
import EntityTable from '@/components/table/EntityTable.vue'
import LayoutSwitcher from '@/components/button/LayoutSwitcher.vue'
import { usePlaylists } from '@/composables/usePlaylists'
import GenesisButton from '@/components/button/GenesisButton.vue'
import PlusIcon from '@/components/icons/PlusIcon.vue'

const {
  playlists,
  searchPlaylists,
  message,
  getPaginationData,
  goToCreateFormView,
  exportPlaylistsToCsv,
  visibleListFields,
  loadColumnConfig,
  saveColumnConfig,
  layoutMode,
} = usePlaylists()

const searchFields = ref<EntitySearchField[]>(Playlist.getAllSearchFieldsMetadata())

const handleUpdateVisibleFields = async (newFields: string[]) => {
  const result = await saveColumnConfig(newFields)
  if (!result.success) {
    console.error('Échec de la sauvegarde de la configuration des colonnes:', result.error)
  }
}

const onExport = async () => {
  await exportPlaylistsToCsv(playlists.value)
}

onMounted(() => {
  loadColumnConfig()
})
</script>
<style scoped></style>
