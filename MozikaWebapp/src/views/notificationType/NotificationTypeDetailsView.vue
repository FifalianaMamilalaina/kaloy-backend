<template>
  <div class="w-full">
    <!-- Header -->
    <div class="flex items-center justify-between mb-6">
      <h3 class="text-xl font-semibold text-base-content">
        {{ $t('entity.nav', { entity: 'NotificationType' }) }}
        <span class="text-base-content/50 font-normal">{{ $t('entity.details.nav') }}</span>
      </h3>

      <!-- Back button -->
      <GenesisButton
        :title="$t('button.backToListDescription')"
        @click="goToListView"
        class="btn-secondary"
      >
        <LeftArrowIcon />
        {{ $t('button.backToList') }}
      </GenesisButton>
    </div>
    <!-- Entity details -->
    <NotificationTypeDetails v-if="entity" :notificationType="entity" />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import NotificationTypeDetails from '@/entities/notificationType/NotificationTypeDetails.vue'
import { useNotificationTypes } from '@/composables/useNotificationTypes'
import { NotificationType } from '@/models/NotificationTypeModel'
import GenesisButton from '@/components/button/GenesisButton.vue'
import LeftArrowIcon from '@/components/icons/LeftArrowIcon.vue'

const route = useRoute()
const pathId = Number(route.params.id)
const { getNotificationTypeById, goToListView } = useNotificationTypes()
const entity = ref<NotificationType | null>(null)

onMounted(async () => {
  const result = await getNotificationTypeById(pathId)
  entity.value = result.data || null
})
</script>
<style scoped></style>
