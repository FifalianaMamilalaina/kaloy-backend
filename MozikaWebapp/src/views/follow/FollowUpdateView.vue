<template>
  <div class="w-full">
    <!-- Header -->
    <div class="flex items-center justify-between mb-6">
      <h3 class="text-xl font-semibold text-base-content">
        Follow /
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
      <follow-form
        :follow="entity"
        :violations="violations"
        :submit-label="$t('entity.update.submitLabel', { entity: 'Follow' })"
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
import FollowForm from '@/entities/follow/FollowForm.vue'
import AlertPopup from '@/components/popup/AlertPopup.vue'
import { useFollows } from '@/composables/useFollows'
import { Follow, FollowFormDTO } from '@/models/FollowModel'
import { usePopup } from '@/composables/usePopup'
import GenesisButton from '@/components/button/GenesisButton.vue'
import LeftArrowIcon from '@/components/icons/LeftArrowIcon.vue'
import { useFreezeScreenStore } from '@/stores/useFreezeScreenStore.ts'

const route = useRoute()
const pathId = Number(route.params.id)
const { closePopup, openPopup, visible: alertPopup } = usePopup()
const { getFollowById, goToListView, updateFollow, viewFollow, message } = useFollows()
const entity = ref<Follow | null>(null)
const freezeStore = useFreezeScreenStore()

const violations = ref<Record<string, object> | null>(null)

const updateHandler = async (formDTO: Partial<FollowFormDTO>) => {
  freezeStore.freeze('Updating follow ' + pathId + ' ...')
  try {
    const data = await updateFollow(pathId, formDTO)
    if (data && !message.value) viewFollow(data)
    else throw new Error(String(message.value))
  } catch (error: unknown) {
    console.error(error)
  } finally {
    freezeStore.unfreeze()
  }
}

const cancelHandler = () => {
  if (entity.value) {
    viewFollow(entity.value)
  }
}

onMounted(async () => {
  const result = await getFollowById(pathId)
  if (result.data) {
    entity.value = result.data
  } else {
    openPopup()
  }
})
</script>
<style scoped></style>
