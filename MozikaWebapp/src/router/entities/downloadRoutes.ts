import type { RouteRecordRaw } from 'vue-router'

const downloadRoutes: Array<RouteRecordRaw> = [
  {
    path: 'downloads',
    name: 'downloadlistview',
    component: () => import('@/views/download/DownloadListView.vue'),
  },
  {
    path: 'downloads/:id',
    name: 'downloaddetailsview',
    component: () => import('@/views/download/DownloadDetailsView.vue'),
  },
  {
    path: 'downloads/update/:id',
    name: 'downloadupdateview',
    component: () => import('@/views/download/DownloadUpdateView.vue'),
  },
  {
    path: 'downloads/create',
    name: 'downloadcreateview',
    component: () => import('@/views/download/DownloadCreateView.vue'),
  },
]

export default downloadRoutes
