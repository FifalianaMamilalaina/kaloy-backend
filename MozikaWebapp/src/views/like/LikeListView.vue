<template>
  <div class="w-full">
    <!-- Header -->
    <div class="flex items-center justify-between mb-6">
      <h3 class="text-xl font-semibold text-base-content">
        {{ $t('entity.nav', { entity: 'Like' }) }}
        <span class="text-base-content/50 font-normal"> {{ $t('entity.list.nav') }}</span>
      </h3>
      <div class="flex items-center gap-3">
        <!-- Composant réutilisable pour le basculement de vue -->
        <LayoutSwitcher v-model="layoutMode" />

        <GenesisButton title="Create new like" @click="goToCreateFormView" class="btn-secondary">
          <PlusIcon />
          {{ $t('button.addEntity', { entity: 'Like' }) }}
        </GenesisButton>
      </div>
    </div>

    <!-- Table -->
    <EntityTable
      :entity-model="{}"
      :entity-search-fields="searchFields"
      :searchFn="searchLikes"
      :getPaginationData="getPaginationData"
      :listComponent="LikeList"
      :entities="likes"
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
import { Like } from '@/models/LikeModel'
import LikeList from '@/entities/like/LikeList.vue'
import EntityTable from '@/components/table/EntityTable.vue'
import LayoutSwitcher from '@/components/button/LayoutSwitcher.vue'
import { useLikes } from '@/composables/useLikes'
import GenesisButton from '@/components/button/GenesisButton.vue'
import PlusIcon from '@/components/icons/PlusIcon.vue'

const {
  likes,
  searchLikes,
  message,
  getPaginationData,
  goToCreateFormView,
  exportLikesToCsv,
  visibleListFields,
  loadColumnConfig,
  saveColumnConfig,
  layoutMode,
} = useLikes()

const searchFields = ref<EntitySearchField[]>(Like.getAllSearchFieldsMetadata())

const handleUpdateVisibleFields = async (newFields: string[]) => {
  const result = await saveColumnConfig(newFields)
  if (!result.success) {
    console.error('Échec de la sauvegarde de la configuration des colonnes:', result.error)
  }
}

const onExport = async () => {
  await exportLikesToCsv(likes.value)
}

onMounted(() => {
  loadColumnConfig()
})
</script>
<style scoped></style>
