<template>
  <div class="mz-page-view">
    <!-- Header -->
    <PageHeader
      title="Pipeline d'Ingestion"
      subtitle="Supervision et historique des archives et fichiers traités"
    >
      <template #actions>
        <LayoutSwitcher v-model="layoutMode" />
        <GenesisButton
          title="Déposer une archive (.zip)"
          @click="goToCreateFormView"
          class="mz-btn mz-btn-primary"
        >
          <PlusIcon />
          Déposer une archive (.zip)
        </GenesisButton>
      </template>
    </PageHeader>

    <!-- Table -->
    <EntityTable
      :entity-model="{}"
      :entity-search-fields="searchFields"
      :searchFn="searchContentSubmissions"
      :getPaginationData="getPaginationData"
      :listComponent="ContentSubmissionList"
      :entities="contentSubmissions"
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
import { ContentSubmission } from '@/models/ContentSubmissionModel'
import ContentSubmissionList from '@/entities/contentSubmission/ContentSubmissionList.vue'
import EntityTable from '@/components/table/EntityTable.vue'
import LayoutSwitcher from '@/components/button/LayoutSwitcher.vue'
import PageHeader from '@/components/common/PageHeader.vue'
import { useContentSubmissions } from '@/composables/useContentSubmissions'
import GenesisButton from '@/components/button/GenesisButton.vue'
import PlusIcon from '@/components/icons/PlusIcon.vue'

const {
  contentSubmissions,
  searchContentSubmissions,
  message,
  getPaginationData,
  goToCreateFormView,
  exportContentSubmissionsToCsv,
  visibleListFields,
  loadColumnConfig,
  saveColumnConfig,
  layoutMode,
} = useContentSubmissions()

const searchFields = ref<EntitySearchField[]>(ContentSubmission.getAllSearchFieldsMetadata())

const handleUpdateVisibleFields = async (newFields: string[]) => {
  const result = await saveColumnConfig(newFields)
  if (!result.success) {
    console.error('Échec de la sauvegarde de la configuration des colonnes:', result.error)
  }
}

const onExport = async () => {
  await exportContentSubmissionsToCsv(contentSubmissions.value)
}

onMounted(() => {
  loadColumnConfig()
})
</script>
<style scoped></style>
