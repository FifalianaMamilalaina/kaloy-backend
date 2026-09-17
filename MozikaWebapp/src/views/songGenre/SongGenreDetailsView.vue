<template>
  <div class="w-full">
    <!-- Header -->
    <div class="flex items-center justify-between mb-6">
      <h3 class="text-xl font-semibold text-base-content">
        {{ $t('entity.nav', { entity: 'SongGenre' }) }}
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
    <SongGenreDetails v-if="entity" :songGenre="entity" />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import SongGenreDetails from '@/entities/songGenre/SongGenreDetails.vue'
import { useSongGenres } from '@/composables/useSongGenres'
import { SongGenre } from '@/models/SongGenreModel'
import GenesisButton from '@/components/button/GenesisButton.vue'
import LeftArrowIcon from '@/components/icons/LeftArrowIcon.vue'

const route = useRoute()
const pathId = Number(route.params.id)
const { getSongGenreById, goToListView } = useSongGenres()
const entity = ref<SongGenre | null>(null)

onMounted(async () => {
  const result = await getSongGenreById(pathId)
  entity.value = result.data || null
})
</script>
<style scoped></style>
