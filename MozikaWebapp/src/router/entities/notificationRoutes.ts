import type { RouteRecordRaw } from 'vue-router'

const notificationRoutes: Array<RouteRecordRaw> = [
  {
    path: 'notifications',
    name: 'notificationlistview',
    component: () => import('@/views/notification/NotificationListView.vue'),
  },
  {
    path: 'notifications/:id',
    name: 'notificationdetailsview',
    component: () => import('@/views/notification/NotificationDetailsView.vue'),
  },
  {
    path: 'notifications/update/:id',
    name: 'notificationupdateview',
    component: () => import('@/views/notification/NotificationUpdateView.vue'),
  },
  {
    path: 'notifications/create',
    name: 'notificationcreateview',
    component: () => import('@/views/notification/NotificationCreateView.vue'),
  },
]

export default notificationRoutes
