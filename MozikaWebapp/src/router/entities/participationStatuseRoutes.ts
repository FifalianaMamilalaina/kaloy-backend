import type { RouteRecordRaw } from 'vue-router'

const participationStatuseRoutes: Array<RouteRecordRaw> = [
  {
    path: 'participationStatuses',
    name: 'participationStatuselistview',
    component: () => import('@/views/participationStatuse/ParticipationStatuseListView.vue'),
  },
  {
    path: 'participationStatuses/:id',
    name: 'participationStatusedetailsview',
    component: () => import('@/views/participationStatuse/ParticipationStatuseDetailsView.vue'),
  },
  {
    path: 'participationStatuses/update/:id',
    name: 'participationStatuseupdateview',
    component: () => import('@/views/participationStatuse/ParticipationStatuseUpdateView.vue'),
  },
  {
    path: 'participationStatuses/create',
    name: 'participationStatusecreateview',
    component: () => import('@/views/participationStatuse/ParticipationStatuseCreateView.vue'),
  },
]

export default participationStatuseRoutes
