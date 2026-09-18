import type { RouteRecordRaw } from 'vue-router'

const notificationPreferenceRoutes: Array<RouteRecordRaw> = [
  {
    path: 'notificationPreferences',
    name: 'notificationPreferencelistview',
    component: () => import('@/views/notificationPreference/NotificationPreferenceListView.vue'),
  },
  {
    path: 'notificationPreferences/:id',
    name: 'notificationPreferencedetailsview',
    component: () => import('@/views/notificationPreference/NotificationPreferenceDetailsView.vue'),
  },
  {
    path: 'notificationPreferences/update/:id',
    name: 'notificationPreferenceupdateview',
    component: () => import('@/views/notificationPreference/NotificationPreferenceUpdateView.vue'),
  },
  {
    path: 'notificationPreferences/create',
    name: 'notificationPreferencecreateview',
    component: () => import('@/views/notificationPreference/NotificationPreferenceCreateView.vue'),
  },
]

export default notificationPreferenceRoutes
