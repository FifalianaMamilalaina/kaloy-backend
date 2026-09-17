import type { RouteRecordRaw } from 'vue-router'

const likeRoutes: Array<RouteRecordRaw> = [
  {
    path: 'likes',
    name: 'likelistview',
    component: () => import('@/views/like/LikeListView.vue'),
  },
  {
    path: 'likes/:id',
    name: 'likedetailsview',
    component: () => import('@/views/like/LikeDetailsView.vue'),
  },
  {
    path: 'likes/update/:id',
    name: 'likeupdateview',
    component: () => import('@/views/like/LikeUpdateView.vue'),
  },
  {
    path: 'likes/create',
    name: 'likecreateview',
    component: () => import('@/views/like/LikeCreateView.vue'),
  },
]

export default likeRoutes
