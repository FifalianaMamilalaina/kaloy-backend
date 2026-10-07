import type { RouteRecordRaw } from 'vue-router'

const instrumentRoleRoutes: Array<RouteRecordRaw> = [
  {
    path: 'instrumentRoles',
    name: 'instrumentRolelistview',
    component: () => import('@/views/instrumentRole/InstrumentRoleListView.vue'),
  },
  {
    path: 'instrumentRoles/:id',
    name: 'instrumentRoledetailsview',
    component: () => import('@/views/instrumentRole/InstrumentRoleDetailsView.vue'),
  },
  {
    path: 'instrumentRoles/update/:id',
    name: 'instrumentRoleupdateview',
    component: () => import('@/views/instrumentRole/InstrumentRoleUpdateView.vue'),
  },
  {
    path: 'instrumentRoles/create',
    name: 'instrumentRolecreateview',
    component: () => import('@/views/instrumentRole/InstrumentRoleCreateView.vue'),
  },
]

export default instrumentRoleRoutes
