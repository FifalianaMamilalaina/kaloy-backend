import type { RouteRecordRaw } from 'vue-router'

const artistTypeRoutes: Array<RouteRecordRaw> = [
  {
    path: 'artistTypes',
    name: 'artistTypelistview',
    component: () => import('@/views/artistType/ArtistTypeListView.vue'),
  },
  {
    path: 'artistTypes/:id',
    name: 'artistTypedetailsview',
    component: () => import('@/views/artistType/ArtistTypeDetailsView.vue'),
  },
  {
    path: 'artistTypes/update/:id',
    name: 'artistTypeupdateview',
    component: () => import('@/views/artistType/ArtistTypeUpdateView.vue'),
  },
  {
    path: 'artistTypes/create',
    name: 'artistTypecreateview',
    component: () => import('@/views/artistType/ArtistTypeCreateView.vue'),
  },
]

export default artistTypeRoutes
