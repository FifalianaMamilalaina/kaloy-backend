import type { RouteRecordRaw } from 'vue-router'

const playlistRoutes: Array<RouteRecordRaw> = [
  {
    path: 'playlists',
    name: 'playlistlistview',
    component: () => import('@/views/playlist/PlaylistListView.vue'),
  },
  {
    path: 'playlists/:id',
    name: 'playlistdetailsview',
    component: () => import('@/views/playlist/PlaylistDetailsView.vue'),
  },
  {
    path: 'playlists/update/:id',
    name: 'playlistupdateview',
    component: () => import('@/views/playlist/PlaylistUpdateView.vue'),
  },
  {
    path: 'playlists/create',
    name: 'playlistcreateview',
    component: () => import('@/views/playlist/PlaylistCreateView.vue'),
  },
]

export default playlistRoutes
