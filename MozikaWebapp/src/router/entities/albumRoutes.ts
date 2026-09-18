import type { RouteRecordRaw } from 'vue-router'

const albumRoutes: Array<RouteRecordRaw> = [
  {
    path: 'albums',
    name: 'albumlistview',
    component: () => import('@/views/album/AlbumListView.vue'),
  },
  {
    path: 'albums/:id',
    name: 'albumdetailsview',
    component: () => import('@/views/album/AlbumDetailsView.vue'),
  },
  {
    path: 'albums/update/:id',
    name: 'albumupdateview',
    component: () => import('@/views/album/AlbumUpdateView.vue'),
  },
  {
    path: 'albums/create',
    name: 'albumcreateview',
    component: () => import('@/views/album/AlbumCreateView.vue'),
  },
]

export default albumRoutes
