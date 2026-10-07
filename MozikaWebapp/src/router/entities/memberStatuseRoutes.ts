import type { RouteRecordRaw } from 'vue-router'

const memberStatuseRoutes: Array<RouteRecordRaw> = [
  {
    path: 'memberStatuses',
    name: 'memberStatuselistview',
    component: () => import('@/views/memberStatuse/MemberStatuseListView.vue'),
  },
  {
    path: 'memberStatuses/:id',
    name: 'memberStatusedetailsview',
    component: () => import('@/views/memberStatuse/MemberStatuseDetailsView.vue'),
  },
  {
    path: 'memberStatuses/update/:id',
    name: 'memberStatuseupdateview',
    component: () => import('@/views/memberStatuse/MemberStatuseUpdateView.vue'),
  },
  {
    path: 'memberStatuses/create',
    name: 'memberStatusecreateview',
    component: () => import('@/views/memberStatuse/MemberStatuseCreateView.vue'),
  },
]

export default memberStatuseRoutes
