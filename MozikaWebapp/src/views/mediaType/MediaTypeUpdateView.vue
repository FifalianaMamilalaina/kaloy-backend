<template>
  <div class="w-full">
    <!-- Header -->
    <div class="flex items-center justify-between mb-6">
      <h3 class="text-xl font-semibold text-base-content">
        MediaType /
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
      <mediaType-form
        :mediaType="entity"
        :violations="violations"
        :submit-label="$t('entity.update.submitLabel', { entity: 'MediaType' })"
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
import MediaTypeForm from '@/entities/mediaType/MediaTypeForm.vue'
import AlertPopup from '@/components/popup/AlertPopup.vue'
import { useMediaTypes } from '@/composables/useMediaTypes'
import { MediaType, MediaTypeFormDTO } from '@/models/MediaTypeModel'
import { usePopup } from '@/composables/usePopup'
import GenesisButton from '@/components/button/GenesisButton.vue'
import LeftArrowIcon from '@/components/icons/LeftArrowIcon.vue'
import { useFreezeScreenStore } from '@/stores/useFreezeScreenStore.ts'

const route = useRoute()
const pathId = Number(route.params.id)
const { closePopup, openPopup, visible: alertPopup } = usePopup()
const { getMediaTypeById, goToListView, updateMediaType, viewMediaType, message } = useMediaTypes()
const entity = ref<MediaType | null>(null)
const freezeStore = useFreezeScreenStore()

const violations = ref<Record<string, object> | null>(null)

const updateHandler = async (formDTO: Partial<MediaTypeFormDTO>) => {
  freezeStore.freeze('Updating mediaType ' + pathId + ' ...')
  try {
    const data = await updateMediaType(pathId, formDTO)
    if (data && !message.value) viewMediaType(data)
    else throw new Error(String(message.value))
  } catch (error: unknown) {
    console.error(error)
  } finally {
    freezeStore.unfreeze()
  }
}

const cancelHandler = () => {
  if (entity.value) {
    viewMediaType(entity.value)
  }
}

onMounted(async () => {
  const result = await getMediaTypeById(pathId)
  if (result.data) {
    entity.value = result.data
  } else {
    openPopup()
  }
})
</script>
<style scoped></style>
