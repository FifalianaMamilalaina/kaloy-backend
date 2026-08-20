<template>
  <div class="w-full">
    <!-- Header -->
    <div class="flex items-center justify-between mb-6">
      <h3 class="text-xl font-semibold text-base-content">
        {{ $t('entity.nav', { entity: 'ContentSubmission' }) }}
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
    <ContentSubmissionDetails v-if="entity" :contentSubmission="entity" />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import ContentSubmissionDetails from '@/entities/contentSubmission/ContentSubmissionDetails.vue'
import { useContentSubmissions } from '@/composables/useContentSubmissions'
import { ContentSubmission } from '@/models/ContentSubmissionModel'
import GenesisButton from '@/components/button/GenesisButton.vue'
import LeftArrowIcon from '@/components/icons/LeftArrowIcon.vue'

const route = useRoute()
const pathId = Number(route.params.id)
const { getContentSubmissionById, goToListView } = useContentSubmissions()
const entity = ref<ContentSubmission | null>(null)

onMounted(async () => {
  const result = await getContentSubmissionById(pathId)
  entity.value = result.data || null
})
</script>
<style scoped></style>
