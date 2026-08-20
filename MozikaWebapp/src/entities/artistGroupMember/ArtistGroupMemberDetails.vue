<template>
  <div class="card bg-base-100">
    <div class="card-body">
      <div v-if="artistGroupMember">
        <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
          <div>
            <label for="artistGroupMemberId" class="block text-sm font-medium mb-1">Id</label>
            <div id="artistGroupMemberId" class="input w-full bg-base-100 cursor-default">
              <span>{{ artistGroupMember.id ? $n(artistGroupMember.id, 'decimal') : '--' }}</span>
            </div>
          </div>
          <div>
            <label
              for="artistGroupMemberGroupartistidArtists"
              class="block text-sm font-medium mb-1"
              >Artist</label
            >
            <div
              id="artistGroupMemberGroupartistidArtists"
              class="input w-full bg-base-100 cursor-default"
            >
              <span>{{ artistGroupMember.groupartistidArtists?.getKeyValue() ?? '' }}</span>
            </div>
          </div>
          <div>
            <label
              for="artistGroupMemberMemberartistidArtists"
              class="block text-sm font-medium mb-1"
              >Artist</label
            >
            <div
              id="artistGroupMemberMemberartistidArtists"
              class="input w-full bg-base-100 cursor-default"
            >
              <span>{{ artistGroupMember.memberartistidArtists?.getKeyValue() ?? '' }}</span>
            </div>
          </div>
          <div>
            <label for="artistGroupMemberFullName" class="block text-sm font-medium mb-1"
              >Full name</label
            >
            <div id="artistGroupMemberFullName" class="input w-full bg-base-100 cursor-default">
              <span>{{ artistGroupMember.fullName ?? '' }}</span>
            </div>
          </div>
          <div>
            <label
              for="artistGroupMemberRoleinstrumentidInstrumentRoles"
              class="block text-sm font-medium mb-1"
              >Instrument role</label
            >
            <div
              id="artistGroupMemberRoleinstrumentidInstrumentRoles"
              class="input w-full bg-base-100 cursor-default"
            >
              <span>{{
                artistGroupMember.roleinstrumentidInstrumentRoles?.getKeyValue() ?? ''
              }}</span>
            </div>
          </div>
          <div>
            <label for="artistGroupMemberPhotoUrl" class="block text-sm font-medium mb-1"
              >Photo url</label
            >
            <div id="artistGroupMemberPhotoUrl" class="input w-full bg-base-100 cursor-default">
              <span>{{ artistGroupMember.photoUrl ?? '' }}</span>
            </div>
          </div>
          <div>
            <label
              for="artistGroupMemberStatusidMemberStatuses"
              class="block text-sm font-medium mb-1"
              >Member statuse</label
            >
            <div
              id="artistGroupMemberStatusidMemberStatuses"
              class="input w-full bg-base-100 cursor-default"
            >
              <span>{{ artistGroupMember.statusidMemberStatuses?.getKeyValue() ?? '' }}</span>
            </div>
          </div>
          <div>
            <label for="artistGroupMemberCreatedAt" class="block text-sm font-medium mb-1"
              >Created at</label
            >
            <div id="artistGroupMemberCreatedAt" class="input w-full bg-base-100 cursor-default">
              <span>{{ artistGroupMember.createdAt ?? '' }}</span>
            </div>
          </div>
        </div>
        <div class="flex justify-end gap-3 mt-6">
          <GenesisButton
            @click="goToUpdateFormView(artistGroupMember)"
            class="btn btn-outline btn-neutral"
          >
            <EditIcon class="mr-2" />
            <span>{{ $t('button.update') }}</span>
          </GenesisButton>
          <GenesisButton
            @click="openDeletePopup(artistGroupMember)"
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
    :message="`Êtes-vous sûr de vouloir supprimer ArtistGroupMember artistGroupMember?.id ?`"
    subMessage="Cette action est irréversible."
    @confirm="confirmDelete"
    @cancel="closePopup"
  />
</template>

<script setup lang="ts">
import type { PropType } from 'vue'

import type { ArtistGroupMember } from '@/models/ArtistGroupMemberModel'
import GenesisButton from '@/components/button/GenesisButton.vue'
import { usePopup } from '@/composables/usePopup'
import { useArtistGroupMembers } from '@/composables/useArtistGroupMembers'
import DeleteConfirmationPopup from '@/components/popup/DeleteConfirmationPopup.vue'
import TrashIcon from '@/components/icons/TrashIcon.vue'
import EditIcon from '@/components/icons/EditIcon.vue'
import { useDateFormat } from '@/composables/useDateFormat'

const props = defineProps({
  artistGroupMember: {
    type: Object as PropType<ArtistGroupMember>,
    required: true,
  },
})
// Stores
const { closePopup, openPopup, visible: deletePopup } = usePopup(false)
const { deleteArtistGroupMember, goToListView, goToUpdateFormView } = useArtistGroupMembers()

// Methods
const openDeletePopup = (entity: ArtistGroupMember) => {
  if (!entity) return
  openPopup()
}

const confirmDelete = async () => {
  const { success, error } = await deleteArtistGroupMember(props.artistGroupMember)
  if (!success) console.error(error)
  closePopup()
  goToListView()
}

const { formatDate } = useDateFormat()
</script>
<style scoped></style>
