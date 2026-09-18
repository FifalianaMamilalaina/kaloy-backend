import type { RouteRecordRaw } from 'vue-router'

const eventModerationStatuseRoutes: Array<RouteRecordRaw> = [
  {
    path: 'eventModerationStatuses',
    name: 'eventModerationStatuselistview',
    component: () => import('@/views/eventModerationStatuse/EventModerationStatuseListView.vue'),
  },
  {
    path: 'eventModerationStatuses/:id',
    name: 'eventModerationStatusedetailsview',
    component: () => import('@/views/eventModerationStatuse/EventModerationStatuseDetailsView.vue'),
  },
  {
    path: 'eventModerationStatuses/update/:id',
    name: 'eventModerationStatuseupdateview',
    component: () => import('@/views/eventModerationStatuse/EventModerationStatuseUpdateView.vue'),
  },
  {
    path: 'eventModerationStatuses/create',
    name: 'eventModerationStatusecreateview',
    component: () => import('@/views/eventModerationStatuse/EventModerationStatuseCreateView.vue'),
  },
]

export default eventModerationStatuseRoutes
