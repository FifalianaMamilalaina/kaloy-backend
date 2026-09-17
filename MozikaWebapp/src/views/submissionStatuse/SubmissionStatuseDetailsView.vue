<template>
  <div class="w-full">
    <!-- Header -->
    <div class="flex items-center justify-between mb-6">
      <h3 class="text-xl font-semibold text-base-content">
        {{ $t('entity.nav', { entity: 'SubmissionStatuse' }) }}
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
    <SubmissionStatuseDetails v-if="entity" :submissionStatuse="entity" />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import SubmissionStatuseDetails from '@/entities/submissionStatuse/SubmissionStatuseDetails.vue'
import { useSubmissionStatuses } from '@/composables/useSubmissionStatuses'
import { SubmissionStatuse } from '@/models/SubmissionStatuseModel'
import GenesisButton from '@/components/button/GenesisButton.vue'
import LeftArrowIcon from '@/components/icons/LeftArrowIcon.vue'

const route = useRoute()
const pathId = Number(route.params.id)
const { getSubmissionStatuseById, goToListView } = useSubmissionStatuses()
const entity = ref<SubmissionStatuse | null>(null)

onMounted(async () => {
  const result = await getSubmissionStatuseById(pathId)
  entity.value = result.data || null
})
</script>
<style scoped></style>
