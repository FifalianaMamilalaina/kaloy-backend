import type { RouteRecordRaw } from 'vue-router'

const userRoleRoutes: Array<RouteRecordRaw> = [
  {
    path: 'userRoles',
    name: 'userRolelistview',
    component: () => import('@/views/userRole/UserRoleListView.vue'),
  },
  {
    path: 'userRoles/:id',
    name: 'userRoledetailsview',
    component: () => import('@/views/userRole/UserRoleDetailsView.vue'),
  },
  {
    path: 'userRoles/update/:id',
    name: 'userRoleupdateview',
    component: () => import('@/views/userRole/UserRoleUpdateView.vue'),
  },
  {
    path: 'userRoles/create',
    name: 'userRolecreateview',
    component: () => import('@/views/userRole/UserRoleCreateView.vue'),
  },
]

export default userRoleRoutes
