import type { RouteRecordRaw } from 'vue-router'

const verificationChannelRoutes: Array<RouteRecordRaw> = [
  {
    path: 'verificationChannels',
    name: 'verificationChannellistview',
    component: () => import('@/views/verificationChannel/VerificationChannelListView.vue'),
  },
  {
    path: 'verificationChannels/:id',
    name: 'verificationChanneldetailsview',
    component: () => import('@/views/verificationChannel/VerificationChannelDetailsView.vue'),
  },
  {
    path: 'verificationChannels/update/:id',
    name: 'verificationChannelupdateview',
    component: () => import('@/views/verificationChannel/VerificationChannelUpdateView.vue'),
  },
  {
    path: 'verificationChannels/create',
    name: 'verificationChannelcreateview',
    component: () => import('@/views/verificationChannel/VerificationChannelCreateView.vue'),
  },
]

export default verificationChannelRoutes
