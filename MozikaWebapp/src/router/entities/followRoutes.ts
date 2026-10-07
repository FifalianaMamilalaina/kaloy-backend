import type { RouteRecordRaw } from 'vue-router'

const followRoutes: Array<RouteRecordRaw> = [
  {
    path: 'follows',
    name: 'followlistview',
    component: () => import('@/views/follow/FollowListView.vue'),
  },
  {
    path: 'follows/:id',
    name: 'followdetailsview',
    component: () => import('@/views/follow/FollowDetailsView.vue'),
  },
  {
    path: 'follows/update/:id',
    name: 'followupdateview',
    component: () => import('@/views/follow/FollowUpdateView.vue'),
  },
  {
    path: 'follows/create',
    name: 'followcreateview',
    component: () => import('@/views/follow/FollowCreateView.vue'),
  },
]

export default followRoutes
