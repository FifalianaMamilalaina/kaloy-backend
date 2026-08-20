<template>
  <div class="w-full">
    <!-- Header -->
    <div class="flex items-center justify-between mb-6">
      <h3 class="text-xl font-semibold text-base-content">
        {{ $t('entity.nav', { entity: 'EditorialPlaylistSong' }) }}
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
    <EditorialPlaylistSongDetails v-if="entity" :editorialPlaylistSong="entity" />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import EditorialPlaylistSongDetails from '@/entities/editorialPlaylistSong/EditorialPlaylistSongDetails.vue'
import { useEditorialPlaylistSongs } from '@/composables/useEditorialPlaylistSongs'
import { EditorialPlaylistSong } from '@/models/EditorialPlaylistSongModel'
import GenesisButton from '@/components/button/GenesisButton.vue'
import LeftArrowIcon from '@/components/icons/LeftArrowIcon.vue'

const route = useRoute()
const pathId = Number(route.params.id)
const { getEditorialPlaylistSongById, goToListView } = useEditorialPlaylistSongs()
const entity = ref<EditorialPlaylistSong | null>(null)

onMounted(async () => {
  const result = await getEditorialPlaylistSongById(pathId)
  entity.value = result.data || null
})
</script>
<style scoped></style>
