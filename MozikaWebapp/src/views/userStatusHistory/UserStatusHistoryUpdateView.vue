<template>
  <div class="w-full">
    <!-- Header -->
    <div class="flex items-center justify-between mb-6">
      <h3 class="text-xl font-semibold text-base-content">
        UserStatusHistory /
        <span class="text-base-content/50 font-normal">{{ $t('entity.update.nav') }}</span>
      </h3>
      <GenesisButton
        :title="$t('button.backToListDescription')"
        @click="goToListView"
        class="btn-secondary"
      >
        <LeftArrowIcon />
        {{ $t('button.backToList') }}
      </GenesisButton>
    </div>

    <!-- Form -->
    <div v-if="entity">
      <userStatusHistory-form
        :userStatusHistory="entity"
        :violations="violations"
        :submit-label="$t('entity.update.submitLabel', { entity: 'UserStatusHistory' })"
        @submit="updateHandler"
        @cancel="cancelHandler"
      />
    </div>

    <!-- Alert -->
    <AlertPopup
      :message="message ?? undefined"
      title="Error 500"
      :visible="alertPopup"
      @close="closePopup"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import UserStatusHistoryForm from '@/entities/userStatusHistory/UserStatusHistoryForm.vue'
import AlertPopup from '@/components/popup/AlertPopup.vue'
import { useUserStatusHistorys } from '@/composables/useUserStatusHistorys'
import { UserStatusHistory, UserStatusHistoryFormDTO } from '@/models/UserStatusHistoryModel'
import { usePopup } from '@/composables/usePopup'
import GenesisButton from '@/components/button/GenesisButton.vue'
import LeftArrowIcon from '@/components/icons/LeftArrowIcon.vue'
import { useFreezeScreenStore } from '@/stores/useFreezeScreenStore.ts'

const route = useRoute()
const pathId = Number(route.params.id)
const { closePopup, openPopup, visible: alertPopup } = usePopup()
const {
  getUserStatusHistoryById,
  goToListView,
  updateUserStatusHistory,
  viewUserStatusHistory,
  message,
} = useUserStatusHistorys()
const entity = ref<UserStatusHistory | null>(null)
const freezeStore = useFreezeScreenStore()

const violations = ref<Record<string, object> | null>(null)

const updateHandler = async (formDTO: Partial<UserStatusHistoryFormDTO>) => {
  freezeStore.freeze('Updating userStatusHistory ' + pathId + ' ...')
  try {
    const data = await updateUserStatusHistory(pathId, formDTO)
    if (data && !message.value) viewUserStatusHistory(data)
    else throw new Error(String(message.value))
  } catch (error: unknown) {
    console.error(error)
  } finally {
    freezeStore.unfreeze()
  }
}

const cancelHandler = () => {
  if (entity.value) {
    viewUserStatusHistory(entity.value)
  }
}

onMounted(async () => {
  const result = await getUserStatusHistoryById(pathId)
  if (result.data) {
    entity.value = result.data
  } else {
    openPopup()
  }
})
</script>
<style scoped></style>
