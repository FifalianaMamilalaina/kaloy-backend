import type { RouteRecordRaw } from 'vue-router'

const clientRoutes: Array<RouteRecordRaw> = [
  {
    path: 'clients',
    name: 'clientlistview',
    component: () => import('@/views/client/ClientListView.vue'),
  },
  {
    path: 'clients/:id',
    name: 'clientdetailsview',
    component: () => import('@/views/client/ClientDetailsView.vue'),
  },
  {
    path: 'clients/update/:id',
    name: 'clientupdateview',
    component: () => import('@/views/client/ClientUpdateView.vue'),
  },
  {
    path: 'clients/create',
    name: 'clientcreateview',
    component: () => import('@/views/client/ClientCreateView.vue'),
  },
]

export default clientRoutes
