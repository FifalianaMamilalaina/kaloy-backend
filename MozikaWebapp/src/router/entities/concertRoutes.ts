import type { RouteRecordRaw } from 'vue-router'

const concertRoutes: Array<RouteRecordRaw> = [
  {
    path: 'concerts',
    name: 'concertlistview',
    component: () => import('@/views/concert/ConcertListView.vue'),
  },
  {
    path: 'concerts/:id',
    name: 'concertdetailsview',
    component: () => import('@/views/concert/ConcertDetailsView.vue'),
  },
  {
    path: 'concerts/update/:id',
    name: 'concertupdateview',
    component: () => import('@/views/concert/ConcertUpdateView.vue'),
  },
  {
    path: 'concerts/create',
    name: 'concertcreateview',
    component: () => import('@/views/concert/ConcertCreateView.vue'),
  },
]

export default concertRoutes
