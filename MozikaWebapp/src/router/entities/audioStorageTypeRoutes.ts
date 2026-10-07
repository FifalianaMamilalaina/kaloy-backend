import type { RouteRecordRaw } from 'vue-router'

const audioStorageTypeRoutes: Array<RouteRecordRaw> = [
  {
    path: 'audioStorageTypes',
    name: 'audioStorageTypelistview',
    component: () => import('@/views/audioStorageType/AudioStorageTypeListView.vue'),
  },
  {
    path: 'audioStorageTypes/:id',
    name: 'audioStorageTypedetailsview',
    component: () => import('@/views/audioStorageType/AudioStorageTypeDetailsView.vue'),
  },
  {
    path: 'audioStorageTypes/update/:id',
    name: 'audioStorageTypeupdateview',
    component: () => import('@/views/audioStorageType/AudioStorageTypeUpdateView.vue'),
  },
  {
    path: 'audioStorageTypes/create',
    name: 'audioStorageTypecreateview',
    component: () => import('@/views/audioStorageType/AudioStorageTypeCreateView.vue'),
  },
]

export default audioStorageTypeRoutes
