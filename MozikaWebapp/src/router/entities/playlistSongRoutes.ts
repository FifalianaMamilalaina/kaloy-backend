import type { RouteRecordRaw } from 'vue-router'

const playlistSongRoutes: Array<RouteRecordRaw> = [
  {
    path: 'playlistSongs',
    name: 'playlistSonglistview',
    component: () => import('@/views/playlistSong/PlaylistSongListView.vue'),
  },
  {
    path: 'playlistSongs/:id',
    name: 'playlistSongdetailsview',
    component: () => import('@/views/playlistSong/PlaylistSongDetailsView.vue'),
  },
  {
    path: 'playlistSongs/update/:id',
    name: 'playlistSongupdateview',
    component: () => import('@/views/playlistSong/PlaylistSongUpdateView.vue'),
  },
  {
    path: 'playlistSongs/create',
    name: 'playlistSongcreateview',
    component: () => import('@/views/playlistSong/PlaylistSongCreateView.vue'),
  },
]

export default playlistSongRoutes
