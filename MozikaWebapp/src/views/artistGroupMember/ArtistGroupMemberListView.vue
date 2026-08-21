<template>
  <div class="w-full">
    <!-- Header -->
    <div class="flex items-center justify-between mb-6">
      <h3 class="text-xl font-semibold text-base-content">
        {{ $t('entity.nav', { entity: 'ArtistGroupMember' }) }}
        <span class="text-base-content/50 font-normal"> {{ $t('entity.list.nav') }}</span>
      </h3>
      <div class="flex items-center gap-3">
        <!-- Composant réutilisable pour le basculement de vue -->
        <LayoutSwitcher v-model="layoutMode" />

        <GenesisButton
          title="Create new artistGroupMember"
          @click="goToCreateFormView"
          class="btn-secondary"
        >
          <PlusIcon />
          {{ $t('button.addEntity', { entity: 'ArtistGroupMember' }) }}
        </GenesisButton>
      </div>
    </div>

    <!-- Table -->
    <EntityTable
      :entity-model="{}"
      :entity-search-fields="searchFields"
      :searchFn="searchArtistGroupMembers"
      :getPaginationData="getPaginationData"
      :listComponent="ArtistGroupMemberList"
      :entities="artistGroupMembers"
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
import { ArtistGroupMember } from '@/models/ArtistGroupMemberModel'
import ArtistGroupMemberList from '@/entities/artistGroupMember/ArtistGroupMemberList.vue'
import EntityTable from '@/components/table/EntityTable.vue'
import LayoutSwitcher from '@/components/button/LayoutSwitcher.vue'
import { useArtistGroupMembers } from '@/composables/useArtistGroupMembers'
import GenesisButton from '@/components/button/GenesisButton.vue'
import PlusIcon from '@/components/icons/PlusIcon.vue'

const {
  artistGroupMembers,
  searchArtistGroupMembers,
  message,
  getPaginationData,
  goToCreateFormView,
  exportArtistGroupMembersToCsv,
  visibleListFields,
  loadColumnConfig,
  saveColumnConfig,
  layoutMode,
} = useArtistGroupMembers()

const searchFields = ref<EntitySearchField[]>(ArtistGroupMember.getAllSearchFieldsMetadata())

const handleUpdateVisibleFields = async (newFields: string[]) => {
  const result = await saveColumnConfig(newFields)
  if (!result.success) {
    console.error('Échec de la sauvegarde de la configuration des colonnes:', result.error)
  }
}

const onExport = async () => {
  await exportArtistGroupMembersToCsv(artistGroupMembers.value)
}

onMounted(() => {
  loadColumnConfig()
})
</script>
<style scoped></style>
