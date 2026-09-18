<template>
  <div class="w-full">
    <!-- Header -->
    <div class="flex items-center justify-between mb-6">
      <h3 class="text-xl font-semibold text-base-content">
        {{ $t('entity.nav', { entity: 'PlaylistVisibilitie' }) }}
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
    <PlaylistVisibilitieDetails v-if="entity" :playlistVisibilitie="entity" />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import PlaylistVisibilitieDetails from '@/entities/playlistVisibilitie/PlaylistVisibilitieDetails.vue'
import { usePlaylistVisibilities } from '@/composables/usePlaylistVisibilities'
import { PlaylistVisibilitie } from '@/models/PlaylistVisibilitieModel'
import GenesisButton from '@/components/button/GenesisButton.vue'
import LeftArrowIcon from '@/components/icons/LeftArrowIcon.vue'

const route = useRoute()
const pathId = Number(route.params.id)
const { getPlaylistVisibilitieById, goToListView } = usePlaylistVisibilities()
const entity = ref<PlaylistVisibilitie | null>(null)

onMounted(async () => {
  const result = await getPlaylistVisibilitieById(pathId)
  entity.value = result.data || null
})
</script>
<style scoped></style>
