import type { RouteRecordRaw } from 'vue-router'

const verificationCodeRoutes: Array<RouteRecordRaw> = [
  {
    path: 'verificationCodes',
    name: 'verificationCodelistview',
    component: () => import('@/views/verificationCode/VerificationCodeListView.vue'),
  },
  {
    path: 'verificationCodes/:id',
    name: 'verificationCodedetailsview',
    component: () => import('@/views/verificationCode/VerificationCodeDetailsView.vue'),
  },
  {
    path: 'verificationCodes/update/:id',
    name: 'verificationCodeupdateview',
    component: () => import('@/views/verificationCode/VerificationCodeUpdateView.vue'),
  },
  {
    path: 'verificationCodes/create',
    name: 'verificationCodecreateview',
    component: () => import('@/views/verificationCode/VerificationCodeCreateView.vue'),
  },
]

export default verificationCodeRoutes
