import type { RouteRecordRaw } from 'vue-router'

const eventMediaRoutes: Array<RouteRecordRaw> = [
  {
    path: 'eventMedias',
    name: 'eventMedialistview',
    component: () => import('@/views/eventMedia/EventMediaListView.vue'),
  },
  {
    path: 'eventMedias/:id',
    name: 'eventMediadetailsview',
    component: () => import('@/views/eventMedia/EventMediaDetailsView.vue'),
  },
  {
    path: 'eventMedias/update/:id',
    name: 'eventMediaupdateview',
    component: () => import('@/views/eventMedia/EventMediaUpdateView.vue'),
  },
  {
    path: 'eventMedias/create',
    name: 'eventMediacreateview',
    component: () => import('@/views/eventMedia/EventMediaCreateView.vue'),
  },
]

export default eventMediaRoutes
