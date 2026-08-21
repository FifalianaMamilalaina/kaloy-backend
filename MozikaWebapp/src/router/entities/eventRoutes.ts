import type { RouteRecordRaw } from 'vue-router'

const eventRoutes: Array<RouteRecordRaw> = [
  {
    path: 'events',
    name: 'eventlistview',
    component: () => import('@/views/event/EventListView.vue'),
  },
  {
    path: 'events/:id',
    name: 'eventdetailsview',
    component: () => import('@/views/event/EventDetailsView.vue'),
  },
  {
    path: 'events/update/:id',
    name: 'eventupdateview',
    component: () => import('@/views/event/EventUpdateView.vue'),
  },
  {
    path: 'events/create',
    name: 'eventcreateview',
    component: () => import('@/views/event/EventCreateView.vue'),
  },
]

export default eventRoutes
