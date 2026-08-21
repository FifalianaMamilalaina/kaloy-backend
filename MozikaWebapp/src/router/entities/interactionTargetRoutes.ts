import type { RouteRecordRaw } from 'vue-router'

const interactionTargetRoutes: Array<RouteRecordRaw> = [
  {
    path: 'interactionTargets',
    name: 'interactionTargetlistview',
    component: () => import('@/views/interactionTarget/InteractionTargetListView.vue'),
  },
  {
    path: 'interactionTargets/:id',
    name: 'interactionTargetdetailsview',
    component: () => import('@/views/interactionTarget/InteractionTargetDetailsView.vue'),
  },
  {
    path: 'interactionTargets/update/:id',
    name: 'interactionTargetupdateview',
    component: () => import('@/views/interactionTarget/InteractionTargetUpdateView.vue'),
  },
  {
    path: 'interactionTargets/create',
    name: 'interactionTargetcreateview',
    component: () => import('@/views/interactionTarget/InteractionTargetCreateView.vue'),
  },
]

export default interactionTargetRoutes
