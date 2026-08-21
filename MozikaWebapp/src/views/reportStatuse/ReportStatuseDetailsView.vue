<template>
  <div class="w-full">
    <!-- Header -->
    <div class="flex items-center justify-between mb-6">
      <h3 class="text-xl font-semibold text-base-content">
        {{ $t('entity.nav', { entity: 'ReportStatuse' }) }}
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
    <ReportStatuseDetails v-if="entity" :reportStatuse="entity" />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import ReportStatuseDetails from '@/entities/reportStatuse/ReportStatuseDetails.vue'
import { useReportStatuses } from '@/composables/useReportStatuses'
import { ReportStatuse } from '@/models/ReportStatuseModel'
import GenesisButton from '@/components/button/GenesisButton.vue'
import LeftArrowIcon from '@/components/icons/LeftArrowIcon.vue'

const route = useRoute()
const pathId = Number(route.params.id)
const { getReportStatuseById, goToListView } = useReportStatuses()
const entity = ref<ReportStatuse | null>(null)

onMounted(async () => {
  const result = await getReportStatuseById(pathId)
  entity.value = result.data || null
})
</script>
<style scoped></style>
