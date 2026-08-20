import type { RouteRecordRaw } from 'vue-router'

const userStatusHistoryRoutes: Array<RouteRecordRaw> = [
  {
    path: 'userStatusHistorys',
    name: 'userStatusHistorylistview',
    component: () => import('@/views/userStatusHistory/UserStatusHistoryListView.vue'),
  },
  {
    path: 'userStatusHistorys/:id',
    name: 'userStatusHistorydetailsview',
    component: () => import('@/views/userStatusHistory/UserStatusHistoryDetailsView.vue'),
  },
  {
    path: 'userStatusHistorys/update/:id',
    name: 'userStatusHistoryupdateview',
    component: () => import('@/views/userStatusHistory/UserStatusHistoryUpdateView.vue'),
  },
  {
    path: 'userStatusHistorys/create',
    name: 'userStatusHistorycreateview',
    component: () => import('@/views/userStatusHistory/UserStatusHistoryCreateView.vue'),
  },
]

export default userStatusHistoryRoutes
