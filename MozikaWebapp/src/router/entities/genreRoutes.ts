import type { RouteRecordRaw } from 'vue-router'

const genreRoutes: Array<RouteRecordRaw> = [
  {
    path: 'genres',
    name: 'genrelistview',
    component: () => import('@/views/genre/GenreListView.vue'),
  },
  {
    path: 'genres/:id',
    name: 'genredetailsview',
    component: () => import('@/views/genre/GenreDetailsView.vue'),
  },
  {
    path: 'genres/update/:id',
    name: 'genreupdateview',
    component: () => import('@/views/genre/GenreUpdateView.vue'),
  },
  {
    path: 'genres/create',
    name: 'genrecreateview',
    component: () => import('@/views/genre/GenreCreateView.vue'),
  },
]

export default genreRoutes
