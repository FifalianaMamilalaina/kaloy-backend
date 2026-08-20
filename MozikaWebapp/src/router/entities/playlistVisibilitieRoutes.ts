import type { RouteRecordRaw } from 'vue-router'

const playlistVisibilitieRoutes: Array<RouteRecordRaw> = [
  {
    path: 'playlistVisibilities',
    name: 'playlistVisibilitielistview',
    component: () => import('@/views/playlistVisibilitie/PlaylistVisibilitieListView.vue'),
  },
  {
    path: 'playlistVisibilities/:id',
    name: 'playlistVisibilitiedetailsview',
    component: () => import('@/views/playlistVisibilitie/PlaylistVisibilitieDetailsView.vue'),
  },
  {
    path: 'playlistVisibilities/update/:id',
    name: 'playlistVisibilitieupdateview',
    component: () => import('@/views/playlistVisibilitie/PlaylistVisibilitieUpdateView.vue'),
  },
  {
    path: 'playlistVisibilities/create',
    name: 'playlistVisibilitiecreateview',
    component: () => import('@/views/playlistVisibilitie/PlaylistVisibilitieCreateView.vue'),
  },
]

export default playlistVisibilitieRoutes
