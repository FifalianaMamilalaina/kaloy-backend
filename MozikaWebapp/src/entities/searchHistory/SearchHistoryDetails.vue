<template>
  <div class="card bg-base-100">
    <div class="card-body">
      <div v-if="searchHistory">
        <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
          <div>
            <label for="searchHistoryId" class="block text-sm font-medium mb-1">Id</label>
            <div id="searchHistoryId" class="input w-full bg-base-100 cursor-default">
              <span>{{ searchHistory.id ? $n(searchHistory.id, 'decimal') : '--' }}</span>
            </div>
          </div>
          <div>
            <label for="searchHistoryUseridUsers" class="block text-sm font-medium mb-1"
              >User</label
            >
            <div id="searchHistoryUseridUsers" class="input w-full bg-base-100 cursor-default">
              <span>{{ searchHistory.useridUsers?.getKeyValue() ?? '' }}</span>
            </div>
          </div>
          <div>
            <label for="searchHistoryQueryText" class="block text-sm font-medium mb-1"
              >Query text</label
            >
            <div id="searchHistoryQueryText" class="input w-full bg-base-100 cursor-default">
              <span>{{ searchHistory.queryText ?? '' }}</span>
            </div>
          </div>
          <div>
            <label for="searchHistorySearchedAt" class="block text-sm font-medium mb-1"
              >Searched at</label
            >
            <div id="searchHistorySearchedAt" class="input w-full bg-base-100 cursor-default">
              <span>{{ searchHistory.searchedAt ?? '' }}</span>
            </div>
          </div>
        </div>
        <div class="flex justify-end gap-3 mt-6">
          <GenesisButton
            @click="goToUpdateFormView(searchHistory)"
            class="btn btn-outline btn-neutral"
          >
            <EditIcon class="mr-2" />
            <span>{{ $t('button.update') }}</span>
          </GenesisButton>
          <GenesisButton
            @click="openDeletePopup(searchHistory)"
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
    :message="`Êtes-vous sûr de vouloir supprimer SearchHistory searchHistory?.id ?`"
    subMessage="Cette action est irréversible."
    @confirm="confirmDelete"
    @cancel="closePopup"
  />
</template>

<script setup lang="ts">
import type { PropType } from 'vue'

import type { SearchHistory } from '@/models/SearchHistoryModel'
import GenesisButton from '@/components/button/GenesisButton.vue'
import { usePopup } from '@/composables/usePopup'
import { useSearchHistorys } from '@/composables/useSearchHistorys'
import DeleteConfirmationPopup from '@/components/popup/DeleteConfirmationPopup.vue'
import TrashIcon from '@/components/icons/TrashIcon.vue'
import EditIcon from '@/components/icons/EditIcon.vue'
import { useDateFormat } from '@/composables/useDateFormat'

const props = defineProps({
  searchHistory: {
    type: Object as PropType<SearchHistory>,
    required: true,
  },
})
// Stores
const { closePopup, openPopup, visible: deletePopup } = usePopup(false)
const { deleteSearchHistory, goToListView, goToUpdateFormView } = useSearchHistorys()

// Methods
const openDeletePopup = (entity: SearchHistory) => {
  if (!entity) return
  openPopup()
}

const confirmDelete = async () => {
  const { success, error } = await deleteSearchHistory(props.searchHistory)
  if (!success) console.error(error)
  closePopup()
  goToListView()
}

const { formatDate } = useDateFormat()
</script>
<style scoped></style>
