import type { RouteRecordRaw } from 'vue-router'

const upNextQueueRoutes: Array<RouteRecordRaw> = [
  {
    path: 'upNextQueues',
    name: 'upNextQueuelistview',
    component: () => import('@/views/upNextQueue/UpNextQueueListView.vue'),
  },
  {
    path: 'upNextQueues/:id',
    name: 'upNextQueuedetailsview',
    component: () => import('@/views/upNextQueue/UpNextQueueDetailsView.vue'),
  },
  {
    path: 'upNextQueues/update/:id',
    name: 'upNextQueueupdateview',
    component: () => import('@/views/upNextQueue/UpNextQueueUpdateView.vue'),
  },
  {
    path: 'upNextQueues/create',
    name: 'upNextQueuecreateview',
    component: () => import('@/views/upNextQueue/UpNextQueueCreateView.vue'),
  },
]

export default upNextQueueRoutes
