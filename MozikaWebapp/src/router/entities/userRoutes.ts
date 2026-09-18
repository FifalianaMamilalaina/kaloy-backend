import type { RouteRecordRaw } from 'vue-router'

const userRoutes: Array<RouteRecordRaw> = [
  {
    path: 'users',
    name: 'userlistview',
    component: () => import('@/views/user/UserListView.vue'),
  },
  {
    path: 'users/:id',
    name: 'userdetailsview',
    component: () => import('@/views/user/UserDetailsView.vue'),
  },
  {
    path: 'users/update/:id',
    name: 'userupdateview',
    component: () => import('@/views/user/UserUpdateView.vue'),
  },
  {
    path: 'users/create',
    name: 'usercreateview',
    component: () => import('@/views/user/UserCreateView.vue'),
  },
]

export default userRoutes
