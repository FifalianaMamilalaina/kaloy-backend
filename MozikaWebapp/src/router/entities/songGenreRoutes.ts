import type { RouteRecordRaw } from 'vue-router'

const songGenreRoutes: Array<RouteRecordRaw> = [
  {
    path: 'songGenres',
    name: 'songGenrelistview',
    component: () => import('@/views/songGenre/SongGenreListView.vue'),
  },
  {
    path: 'songGenres/:id',
    name: 'songGenredetailsview',
    component: () => import('@/views/songGenre/SongGenreDetailsView.vue'),
  },
  {
    path: 'songGenres/update/:id',
    name: 'songGenreupdateview',
    component: () => import('@/views/songGenre/SongGenreUpdateView.vue'),
  },
  {
    path: 'songGenres/create',
    name: 'songGenrecreateview',
    component: () => import('@/views/songGenre/SongGenreCreateView.vue'),
  },
]

export default songGenreRoutes
