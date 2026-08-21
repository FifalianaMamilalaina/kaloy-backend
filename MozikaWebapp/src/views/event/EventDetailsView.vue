<template>
  <div class="w-full">
    <!-- Header -->
    <div class="flex items-center justify-between mb-6">
      <h3 class="text-xl font-semibold text-base-content">
        {{ $t('entity.nav', { entity: 'Event' }) }}
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
    <EventDetails v-if="entity" :event="entity" />

    <div class="tabs tabs-border">
      <input
        type="radio"
        name="details_tab"
        class="tab"
        :aria-label="$t('entities.concert.nav.title')"
        checked
      />
      <div class="tab-content card bg-base-100 border border-base-300">
        <div class="card-body">
          <div class="mt-4">
            <EntityTable
              :entity-model="concertEntity"
              :entity-search-fields="concertSearchFields"
              :default-active-filters="['eventidEvents']"
              :searchFn="getConcertsData"
              :getPaginationData="concertGetPaginationData"
              :listComponent="ConcertList"
              :entities="concerts"
              :message="concertMessage"
              :show-filters="false"
              :edit-action="false"
              :remove-action="false"
            />
          </div>
        </div>
      </div>
    </div>
    <div class="tabs tabs-border">
      <input
        type="radio"
        name="details_tab"
        class="tab"
        :aria-label="$t('entities.eventMedia.nav.title')"
        checked
      />
      <div class="tab-content card bg-base-100 border border-base-300">
        <div class="card-body">
          <div class="mt-4">
            <EntityTable
              :entity-model="eventMediaEntity"
              :entity-search-fields="eventMediaSearchFields"
              :default-active-filters="['eventidEvents']"
              :searchFn="getEventMediasData"
              :getPaginationData="eventMediaGetPaginationData"
              :listComponent="EventMediaList"
              :entities="eventMedias"
              :message="eventMediaMessage"
              :show-filters="false"
              :edit-action="false"
              :remove-action="false"
            />
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import EventDetails from '@/entities/event/EventDetails.vue'
import { useEvents } from '@/composables/useEvents'
import { Event } from '@/models/EventModel'
import GenesisButton from '@/components/button/GenesisButton.vue'
import LeftArrowIcon from '@/components/icons/LeftArrowIcon.vue'
import EntityTable from '@/components/table/EntityTable.vue'
import type { EntitySearchField } from '@/models/EntityModel'
import type { PaginationRequestParameter, SortFieldParameter } from '@/models/api/RequestModel'
import { useConcerts } from '@/composables/useConcerts'
import { Concert } from '@/models/ConcertModel'
import ConcertList from '@/entities/concert/ConcertList.vue'
import { useEventMedias } from '@/composables/useEventMedias'
import { EventMedia } from '@/models/EventMediaModel'
import EventMediaList from '@/entities/eventMedia/EventMediaList.vue'

const route = useRoute()
const pathId = Number(route.params.id)
const { getEventById, goToListView } = useEvents()
const entity = ref<Event | null>(null)

const {
  concerts,
  getAllConcertsByEventId,
  message: concertMessage,
  getPaginationData: concertGetPaginationData,
} = useConcerts()
const getConcertsData = async (
  unpagined: boolean,
  filters: Record<string, unknown>,
  pagination: PaginationRequestParameter,
  sortFields: SortFieldParameter[],
) => {
  return await getAllConcertsByEventId(pathId, unpagined, pagination, sortFields)
}
const concertSearchFields = ref<EntitySearchField[]>(Concert.getAllSearchFieldsMetadata())
const concertEntity = ref<Record<string, unknown>>({ eventidEvents: pathId })
const {
  eventMedias,
  getAllEventMediasByEventId,
  message: eventMediaMessage,
  getPaginationData: eventMediaGetPaginationData,
} = useEventMedias()
const getEventMediasData = async (
  unpagined: boolean,
  filters: Record<string, unknown>,
  pagination: PaginationRequestParameter,
  sortFields: SortFieldParameter[],
) => {
  return await getAllEventMediasByEventId(pathId, unpagined, pagination, sortFields)
}
const eventMediaSearchFields = ref<EntitySearchField[]>(EventMedia.getAllSearchFieldsMetadata())
const eventMediaEntity = ref<Record<string, unknown>>({ eventidEvents: pathId })

onMounted(async () => {
  const result = await getEventById(pathId)
  entity.value = result.data || null
})
</script>
<style scoped></style>
