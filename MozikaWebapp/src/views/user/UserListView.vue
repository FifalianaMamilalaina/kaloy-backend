<template>
  <div class="mz-page-view">
    <!-- Header -->
    <PageHeader :title="$t('entity.nav', { entity: 'User' })" :subtitle="$t('entity.list.nav')">
      <template #actions>
        <LayoutSwitcher v-model="layoutMode" />
        <GenesisButton
          title="Create new user"
          @click="goToCreateFormView"
          class="mz-btn mz-btn-primary"
        >
          <PlusIcon />
          {{ $t('button.addEntity', { entity: 'User' }) }}
        </GenesisButton>
      </template>
    </PageHeader>

    <!-- Table -->
    <EntityTable
      :entity-model="{}"
      :entity-search-fields="searchFields"
      :searchFn="searchUsers"
      :getPaginationData="getPaginationData"
      :listComponent="UserList"
      :entities="users"
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
import { User } from '@/models/UserModel'
import UserList from '@/entities/user/UserList.vue'
import EntityTable from '@/components/table/EntityTable.vue'
import LayoutSwitcher from '@/components/button/LayoutSwitcher.vue'
import PageHeader from '@/components/common/PageHeader.vue'
import { useUsers } from '@/composables/useUsers'
import GenesisButton from '@/components/button/GenesisButton.vue'
import PlusIcon from '@/components/icons/PlusIcon.vue'

const {
  users,
  searchUsers,
  message,
  getPaginationData,
  goToCreateFormView,
  exportUsersToCsv,
  visibleListFields,
  loadColumnConfig,
  saveColumnConfig,
  layoutMode,
} = useUsers()

const searchFields = ref<EntitySearchField[]>(User.getAllSearchFieldsMetadata())

const handleUpdateVisibleFields = async (newFields: string[]) => {
  const result = await saveColumnConfig(newFields)
  if (!result.success) {
    console.error('Échec de la sauvegarde de la configuration des colonnes:', result.error)
  }
}

const onExport = async () => {
  await exportUsersToCsv(users.value)
}

onMounted(() => {
  loadColumnConfig()
})
</script>
<style scoped></style>
