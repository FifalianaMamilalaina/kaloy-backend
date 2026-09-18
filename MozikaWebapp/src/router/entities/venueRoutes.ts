import type { RouteRecordRaw } from 'vue-router'

const venueRoutes: Array<RouteRecordRaw> = [
  {
    path: 'venues',
    name: 'venuelistview',
    component: () => import('@/views/venue/VenueListView.vue'),
  },
  {
    path: 'venues/:id',
    name: 'venuedetailsview',
    component: () => import('@/views/venue/VenueDetailsView.vue'),
  },
  {
    path: 'venues/update/:id',
    name: 'venueupdateview',
    component: () => import('@/views/venue/VenueUpdateView.vue'),
  },
  {
    path: 'venues/create',
    name: 'venuecreateview',
    component: () => import('@/views/venue/VenueCreateView.vue'),
  },
]

export default venueRoutes
