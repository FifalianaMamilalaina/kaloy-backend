<template>
  <div class="w-full">
    <!-- Header -->
    <div class="flex items-center justify-between mb-6">
      <h3 class="text-xl font-semibold text-base-content">
        {{ $t('entity.nav', { entity: 'VerificationCode' }) }}
        <span class="text-base-content/50 font-normal"> {{ $t('entity.list.nav') }}</span>
      </h3>
      <div class="flex items-center gap-3">
        <!-- Composant réutilisable pour le basculement de vue -->
        <LayoutSwitcher v-model="layoutMode" />

        <GenesisButton
          title="Create new verificationCode"
          @click="goToCreateFormView"
          class="btn-secondary"
        >
          <PlusIcon />
          {{ $t('button.addEntity', { entity: 'VerificationCode' }) }}
        </GenesisButton>
      </div>
    </div>

    <!-- Table -->
    <EntityTable
      :entity-model="{}"
      :entity-search-fields="searchFields"
      :searchFn="searchVerificationCodes"
      :getPaginationData="getPaginationData"
      :listComponent="VerificationCodeList"
      :entities="verificationCodes"
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
import { VerificationCode } from '@/models/VerificationCodeModel'
import VerificationCodeList from '@/entities/verificationCode/VerificationCodeList.vue'
import EntityTable from '@/components/table/EntityTable.vue'
import LayoutSwitcher from '@/components/button/LayoutSwitcher.vue'
import { useVerificationCodes } from '@/composables/useVerificationCodes'
import GenesisButton from '@/components/button/GenesisButton.vue'
import PlusIcon from '@/components/icons/PlusIcon.vue'

const {
  verificationCodes,
  searchVerificationCodes,
  message,
  getPaginationData,
  goToCreateFormView,
  exportVerificationCodesToCsv,
  visibleListFields,
  loadColumnConfig,
  saveColumnConfig,
  layoutMode,
} = useVerificationCodes()

const searchFields = ref<EntitySearchField[]>(VerificationCode.getAllSearchFieldsMetadata())

const handleUpdateVisibleFields = async (newFields: string[]) => {
  const result = await saveColumnConfig(newFields)
  if (!result.success) {
    console.error('Échec de la sauvegarde de la configuration des colonnes:', result.error)
  }
}

const onExport = async () => {
  await exportVerificationCodesToCsv(verificationCodes.value)
}

onMounted(() => {
  loadColumnConfig()
})
</script>
<style scoped></style>
