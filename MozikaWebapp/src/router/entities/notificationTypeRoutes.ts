import type { RouteRecordRaw } from 'vue-router'

const notificationTypeRoutes: Array<RouteRecordRaw> = [
  {
    path: 'notificationTypes',
    name: 'notificationTypelistview',
    component: () => import('@/views/notificationType/NotificationTypeListView.vue'),
  },
  {
    path: 'notificationTypes/:id',
    name: 'notificationTypedetailsview',
    component: () => import('@/views/notificationType/NotificationTypeDetailsView.vue'),
  },
  {
    path: 'notificationTypes/update/:id',
    name: 'notificationTypeupdateview',
    component: () => import('@/views/notificationType/NotificationTypeUpdateView.vue'),
  },
  {
    path: 'notificationTypes/create',
    name: 'notificationTypecreateview',
    component: () => import('@/views/notificationType/NotificationTypeCreateView.vue'),
  },
]

export default notificationTypeRoutes
