<template>
  <div class="w-full">
    <!-- Header -->
    <div class="flex items-center justify-between mb-6">
      <h3 class="text-xl font-semibold text-base-content">
        {{ $t('entity.nav', { entity: 'PlaylistSong' }) }}
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
    <PlaylistSongDetails v-if="entity" :playlistSong="entity" />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import PlaylistSongDetails from '@/entities/playlistSong/PlaylistSongDetails.vue'
import { usePlaylistSongs } from '@/composables/usePlaylistSongs'
import { PlaylistSong } from '@/models/PlaylistSongModel'
import GenesisButton from '@/components/button/GenesisButton.vue'
import LeftArrowIcon from '@/components/icons/LeftArrowIcon.vue'

const route = useRoute()
const pathId = Number(route.params.id)
const { getPlaylistSongById, goToListView } = usePlaylistSongs()
const entity = ref<PlaylistSong | null>(null)

onMounted(async () => {
  const result = await getPlaylistSongById(pathId)
  entity.value = result.data || null
})
</script>
<style scoped></style>
