import type { RouteRecordRaw } from 'vue-router'

const userStatuseRoutes: Array<RouteRecordRaw> = [
  {
    path: 'userStatuses',
    name: 'userStatuselistview',
    component: () => import('@/views/userStatuse/UserStatuseListView.vue'),
  },
  {
    path: 'userStatuses/:id',
    name: 'userStatusedetailsview',
    component: () => import('@/views/userStatuse/UserStatuseDetailsView.vue'),
  },
  {
    path: 'userStatuses/update/:id',
    name: 'userStatuseupdateview',
    component: () => import('@/views/userStatuse/UserStatuseUpdateView.vue'),
  },
  {
    path: 'userStatuses/create',
    name: 'userStatusecreateview',
    component: () => import('@/views/userStatuse/UserStatuseCreateView.vue'),
  },
]

export default userStatuseRoutes
