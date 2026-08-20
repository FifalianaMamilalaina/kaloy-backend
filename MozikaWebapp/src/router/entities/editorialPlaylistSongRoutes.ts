import type { RouteRecordRaw } from 'vue-router'

const editorialPlaylistSongRoutes: Array<RouteRecordRaw> = [
  {
    path: 'editorialPlaylistSongs',
    name: 'editorialPlaylistSonglistview',
    component: () => import('@/views/editorialPlaylistSong/EditorialPlaylistSongListView.vue'),
  },
  {
    path: 'editorialPlaylistSongs/:id',
    name: 'editorialPlaylistSongdetailsview',
    component: () => import('@/views/editorialPlaylistSong/EditorialPlaylistSongDetailsView.vue'),
  },
  {
    path: 'editorialPlaylistSongs/update/:id',
    name: 'editorialPlaylistSongupdateview',
    component: () => import('@/views/editorialPlaylistSong/EditorialPlaylistSongUpdateView.vue'),
  },
  {
    path: 'editorialPlaylistSongs/create',
    name: 'editorialPlaylistSongcreateview',
    component: () => import('@/views/editorialPlaylistSong/EditorialPlaylistSongCreateView.vue'),
  },
]

export default editorialPlaylistSongRoutes
