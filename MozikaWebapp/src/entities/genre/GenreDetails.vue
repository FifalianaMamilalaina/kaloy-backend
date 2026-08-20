<template>
  <div class="card bg-base-100">
    <div class="card-body">
      <div v-if="genre">
        <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
          <div>
            <label for="genreId" class="block text-sm font-medium mb-1">Id</label>
            <div id="genreId" class="input w-full bg-base-100 cursor-default">
              <span>{{ genre.id ? $n(genre.id, 'decimal') : '--' }}</span>
            </div>
          </div>
          <div>
            <label for="genreName" class="block text-sm font-medium mb-1">Name</label>
            <div id="genreName" class="input w-full bg-base-100 cursor-default">
              <span>{{ genre.name ?? '' }}</span>
            </div>
          </div>
        </div>
        <div class="flex justify-end gap-3 mt-6">
          <GenesisButton @click="goToUpdateFormView(genre)" class="btn btn-outline btn-neutral">
            <EditIcon class="mr-2" />
            <span>{{ $t('button.update') }}</span>
          </GenesisButton>
          <GenesisButton
            @click="openDeletePopup(genre)"
            class="btn-outline btn-error hover:text-white"
          >
            <TrashIcon class="mr-2" />
            <span>{{ $t('button.delete') }}</span>
          </GenesisButton>
        </div>
      </div>
    </div>
  </div>
  <DeleteConfirmationPopup
    :visible="deletePopup"
    :message="`Êtes-vous sûr de vouloir supprimer Genre genre?.id ?`"
    subMessage="Cette action est irréversible."
    @confirm="confirmDelete"
    @cancel="closePopup"
  />
</template>

<script setup lang="ts">
import type { PropType } from 'vue'

import type { Genre } from '@/models/GenreModel'
import GenesisButton from '@/components/button/GenesisButton.vue'
import { usePopup } from '@/composables/usePopup'
import { useGenres } from '@/composables/useGenres'
import DeleteConfirmationPopup from '@/components/popup/DeleteConfirmationPopup.vue'
import TrashIcon from '@/components/icons/TrashIcon.vue'
import EditIcon from '@/components/icons/EditIcon.vue'
import { useDateFormat } from '@/composables/useDateFormat'

const props = defineProps({
  genre: {
    type: Object as PropType<Genre>,
    required: true,
  },
})
// Stores
const { closePopup, openPopup, visible: deletePopup } = usePopup(false)
const { deleteGenre, goToListView, goToUpdateFormView } = useGenres()

// Methods
const openDeletePopup = (entity: Genre) => {
  if (!entity) return
  openPopup()
}

const confirmDelete = async () => {
  const { success, error } = await deleteGenre(props.genre)
  if (!success) console.error(error)
  closePopup()
  goToListView()
}

const { formatDate } = useDateFormat()
</script>
<style scoped></style>
