import type { RouteRecordRaw } from 'vue-router'

const artistRoutes: Array<RouteRecordRaw> = [
  {
    path: 'artists',
    name: 'artistlistview',
    component: () => import('@/views/artist/ArtistListView.vue'),
  },
  {
    path: 'artists/:id',
    name: 'artistdetailsview',
    component: () => import('@/views/artist/ArtistDetailsView.vue'),
  },
  {
    path: 'artists/update/:id',
    name: 'artistupdateview',
    component: () => import('@/views/artist/ArtistUpdateView.vue'),
  },
  {
    path: 'artists/create',
    name: 'artistcreateview',
    component: () => import('@/views/artist/ArtistCreateView.vue'),
  },
]

export default artistRoutes
