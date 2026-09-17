<template>
  <div class="w-full">
    <!-- Header -->
    <div class="flex items-center justify-between mb-6">
      <h3 class="text-xl font-semibold text-base-content">
        {{ $t('entity.nav', { entity: 'VerificationCode' }) }}
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
    <VerificationCodeDetails v-if="entity" :verificationCode="entity" />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import VerificationCodeDetails from '@/entities/verificationCode/VerificationCodeDetails.vue'
import { useVerificationCodes } from '@/composables/useVerificationCodes'
import { VerificationCode } from '@/models/VerificationCodeModel'
import GenesisButton from '@/components/button/GenesisButton.vue'
import LeftArrowIcon from '@/components/icons/LeftArrowIcon.vue'

const route = useRoute()
const pathId = Number(route.params.id)
const { getVerificationCodeById, goToListView } = useVerificationCodes()
const entity = ref<VerificationCode | null>(null)

onMounted(async () => {
  const result = await getVerificationCodeById(pathId)
  entity.value = result.data || null
})
</script>
<style scoped></style>
