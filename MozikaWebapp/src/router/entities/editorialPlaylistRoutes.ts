import type { RouteRecordRaw } from 'vue-router'

const editorialPlaylistRoutes: Array<RouteRecordRaw> = [
  {
    path: 'editorialPlaylists',
    name: 'editorialPlaylistlistview',
    component: () => import('@/views/editorialPlaylist/EditorialPlaylistListView.vue'),
  },
  {
    path: 'editorialPlaylists/:id',
    name: 'editorialPlaylistdetailsview',
    component: () => import('@/views/editorialPlaylist/EditorialPlaylistDetailsView.vue'),
  },
  {
    path: 'editorialPlaylists/update/:id',
    name: 'editorialPlaylistupdateview',
    component: () => import('@/views/editorialPlaylist/EditorialPlaylistUpdateView.vue'),
  },
  {
    path: 'editorialPlaylists/create',
    name: 'editorialPlaylistcreateview',
    component: () => import('@/views/editorialPlaylist/EditorialPlaylistCreateView.vue'),
  },
]

export default editorialPlaylistRoutes
