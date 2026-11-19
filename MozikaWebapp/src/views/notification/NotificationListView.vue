<template>
  <div class="mz-page-view">
    <!-- Header -->
    <PageHeader
      :title="$t('entity.nav', { entity: 'Notification' })"
      :subtitle="$t('entity.list.nav')"
    >
      <template #actions>
        <LayoutSwitcher v-model="layoutMode" />
        <GenesisButton
          title="Create new notification"
          @click="goToCreateFormView"
          class="mz-btn mz-btn-primary"
        >
          <PlusIcon />
          {{ $t('button.addEntity', { entity: 'Notification' }) }}
        </GenesisButton>
      </template>
    </PageHeader>

    <!-- Table -->
    <EntityTable
      :entity-model="{}"
      :entity-search-fields="searchFields"
      :searchFn="searchNotifications"
      :getPaginationData="getPaginationData"
      :listComponent="NotificationList"
      :entities="notifications"
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
import { Notification } from '@/models/NotificationModel'
import NotificationList from '@/entities/notification/NotificationList.vue'
import EntityTable from '@/components/table/EntityTable.vue'
import LayoutSwitcher from '@/components/button/LayoutSwitcher.vue'
import PageHeader from '@/components/common/PageHeader.vue'
import { useNotifications } from '@/composables/useNotifications'
import GenesisButton from '@/components/button/GenesisButton.vue'
import PlusIcon from '@/components/icons/PlusIcon.vue'

const {
  notifications,
  searchNotifications,
  message,
  getPaginationData,
  goToCreateFormView,
  exportNotificationsToCsv,
  visibleListFields,
  loadColumnConfig,
  saveColumnConfig,
  layoutMode,
} = useNotifications()

const searchFields = ref<EntitySearchField[]>(Notification.getAllSearchFieldsMetadata())

const handleUpdateVisibleFields = async (newFields: string[]) => {
  const result = await saveColumnConfig(newFields)
  if (!result.success) {
    console.error('Échec de la sauvegarde de la configuration des colonnes:', result.error)
  }
}

const onExport = async () => {
  await exportNotificationsToCsv(notifications.value)
}

onMounted(() => {
  loadColumnConfig()
})
</script>
<style scoped></style>
