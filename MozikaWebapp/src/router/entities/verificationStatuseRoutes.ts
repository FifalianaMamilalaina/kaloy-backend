import type { RouteRecordRaw } from 'vue-router'

const verificationStatuseRoutes: Array<RouteRecordRaw> = [
  {
    path: 'verificationStatuses',
    name: 'verificationStatuselistview',
    component: () => import('@/views/verificationStatuse/VerificationStatuseListView.vue'),
  },
  {
    path: 'verificationStatuses/:id',
    name: 'verificationStatusedetailsview',
    component: () => import('@/views/verificationStatuse/VerificationStatuseDetailsView.vue'),
  },
  {
    path: 'verificationStatuses/update/:id',
    name: 'verificationStatuseupdateview',
    component: () => import('@/views/verificationStatuse/VerificationStatuseUpdateView.vue'),
  },
  {
    path: 'verificationStatuses/create',
    name: 'verificationStatusecreateview',
    component: () => import('@/views/verificationStatuse/VerificationStatuseCreateView.vue'),
  },
]

export default verificationStatuseRoutes
