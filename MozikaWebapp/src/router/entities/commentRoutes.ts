import type { RouteRecordRaw } from 'vue-router'

const commentRoutes: Array<RouteRecordRaw> = [
  {
    path: 'comments',
    name: 'commentlistview',
    component: () => import('@/views/comment/CommentListView.vue'),
  },
  {
    path: 'comments/:id',
    name: 'commentdetailsview',
    component: () => import('@/views/comment/CommentDetailsView.vue'),
  },
  {
    path: 'comments/update/:id',
    name: 'commentupdateview',
    component: () => import('@/views/comment/CommentUpdateView.vue'),
  },
  {
    path: 'comments/create',
    name: 'commentcreateview',
    component: () => import('@/views/comment/CommentCreateView.vue'),
  },
]

export default commentRoutes
