import type { RouteRecordRaw } from 'vue-router'

const reportStatuseRoutes: Array<RouteRecordRaw> = [
  {
    path: 'reportStatuses',
    name: 'reportStatuselistview',
    component: () => import('@/views/reportStatuse/ReportStatuseListView.vue'),
  },
  {
    path: 'reportStatuses/:id',
    name: 'reportStatusedetailsview',
    component: () => import('@/views/reportStatuse/ReportStatuseDetailsView.vue'),
  },
  {
    path: 'reportStatuses/update/:id',
    name: 'reportStatuseupdateview',
    component: () => import('@/views/reportStatuse/ReportStatuseUpdateView.vue'),
  },
  {
    path: 'reportStatuses/create',
    name: 'reportStatusecreateview',
    component: () => import('@/views/reportStatuse/ReportStatuseCreateView.vue'),
  },
]

export default reportStatuseRoutes
