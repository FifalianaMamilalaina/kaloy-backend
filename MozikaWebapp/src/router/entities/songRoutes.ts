import type { RouteRecordRaw } from 'vue-router'

const songRoutes: Array<RouteRecordRaw> = [
  {
    path: 'songs',
    name: 'songlistview',
    component: () => import('@/views/song/SongListView.vue'),
  },
  {
    path: 'songs/:id',
    name: 'songdetailsview',
    component: () => import('@/views/song/SongDetailsView.vue'),
  },
  {
    path: 'songs/update/:id',
    name: 'songupdateview',
    component: () => import('@/views/song/SongUpdateView.vue'),
  },
  {
    path: 'songs/create',
    name: 'songcreateview',
    component: () => import('@/views/song/SongCreateView.vue'),
  },
]

export default songRoutes
