<template>
  <div class="card bg-base-100">
    <div class="card-body">
      <div v-if="verificationCode">
        <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
          <div>
            <label for="verificationCodeId" class="block text-sm font-medium mb-1">Id</label>
            <div id="verificationCodeId" class="input w-full bg-base-100 cursor-default">
              <span>{{ verificationCode.id ? $n(verificationCode.id, 'decimal') : '--' }}</span>
            </div>
          </div>
          <div>
            <label for="verificationCodeUseridUsers" class="block text-sm font-medium mb-1"
              >User</label
            >
            <div id="verificationCodeUseridUsers" class="input w-full bg-base-100 cursor-default">
              <span>{{ verificationCode.useridUsers?.getKeyValue() ?? '' }}</span>
            </div>
          </div>
          <div>
            <label
              for="verificationCodeChannelidVerificationChannels"
              class="block text-sm font-medium mb-1"
              >Verification channel</label
            >
            <div
              id="verificationCodeChannelidVerificationChannels"
              class="input w-full bg-base-100 cursor-default"
            >
              <span>{{ verificationCode.channelidVerificationChannels?.getKeyValue() ?? '' }}</span>
            </div>
          </div>
          <div>
            <label for="verificationCodeDestination" class="block text-sm font-medium mb-1"
              >Destination</label
            >
            <div id="verificationCodeDestination" class="input w-full bg-base-100 cursor-default">
              <span>{{ verificationCode.destination ?? '' }}</span>
            </div>
          </div>
          <div>
            <label for="verificationCodeCode" class="block text-sm font-medium mb-1">Code</label>
            <div id="verificationCodeCode" class="input w-full bg-base-100 cursor-default">
              <span>{{ verificationCode.code ?? '' }}</span>
            </div>
          </div>
          <div>
            <label for="verificationCodeExpiresAt" class="block text-sm font-medium mb-1"
              >Expires at</label
            >
            <div id="verificationCodeExpiresAt" class="input w-full bg-base-100 cursor-default">
              <span>{{ verificationCode.expiresAt ?? '' }}</span>
            </div>
          </div>
          <div>
            <label for="verificationCodeConsumedAt" class="block text-sm font-medium mb-1"
              >Consumed at</label
            >
            <div id="verificationCodeConsumedAt" class="input w-full bg-base-100 cursor-default">
              <span>{{ verificationCode.consumedAt ?? '' }}</span>
            </div>
          </div>
          <div>
            <label for="verificationCodeCreatedAt" class="block text-sm font-medium mb-1"
              >Created at</label
            >
            <div id="verificationCodeCreatedAt" class="input w-full bg-base-100 cursor-default">
              <span>{{ verificationCode.createdAt ?? '' }}</span>
            </div>
          </div>
        </div>
        <div class="flex justify-end gap-3 mt-6">
          <GenesisButton
            @click="goToUpdateFormView(verificationCode)"
            class="btn btn-outline btn-neutral"
          >
            <EditIcon class="mr-2" />
            <span>{{ $t('button.update') }}</span>
          </GenesisButton>
          <GenesisButton
            @click="openDeletePopup(verificationCode)"
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
    :message="`Êtes-vous sûr de vouloir supprimer VerificationCode verificationCode?.id ?`"
    subMessage="Cette action est irréversible."
    @confirm="confirmDelete"
    @cancel="closePopup"
  />
</template>

<script setup lang="ts">
import type { PropType } from 'vue'

import type { VerificationCode } from '@/models/VerificationCodeModel'
import GenesisButton from '@/components/button/GenesisButton.vue'
import { usePopup } from '@/composables/usePopup'
import { useVerificationCodes } from '@/composables/useVerificationCodes'
import DeleteConfirmationPopup from '@/components/popup/DeleteConfirmationPopup.vue'
import TrashIcon from '@/components/icons/TrashIcon.vue'
import EditIcon from '@/components/icons/EditIcon.vue'
import { useDateFormat } from '@/composables/useDateFormat'

const props = defineProps({
  verificationCode: {
    type: Object as PropType<VerificationCode>,
    required: true,
  },
})
// Stores
const { closePopup, openPopup, visible: deletePopup } = usePopup(false)
const { deleteVerificationCode, goToListView, goToUpdateFormView } = useVerificationCodes()

// Methods
const openDeletePopup = (entity: VerificationCode) => {
  if (!entity) return
  openPopup()
}

const confirmDelete = async () => {
  const { success, error } = await deleteVerificationCode(props.verificationCode)
  if (!success) console.error(error)
  closePopup()
  goToListView()
}

const { formatDate } = useDateFormat()
</script>
<style scoped></style>
