import type { RouteRecordRaw } from 'vue-router'

const reportRoutes: Array<RouteRecordRaw> = [
  {
    path: 'reports',
    name: 'reportlistview',
    component: () => import('@/views/report/ReportListView.vue'),
  },
  {
    path: 'reports/:id',
    name: 'reportdetailsview',
    component: () => import('@/views/report/ReportDetailsView.vue'),
  },
  {
    path: 'reports/update/:id',
    name: 'reportupdateview',
    component: () => import('@/views/report/ReportUpdateView.vue'),
  },
  {
    path: 'reports/create',
    name: 'reportcreateview',
    component: () => import('@/views/report/ReportCreateView.vue'),
  },
]

export default reportRoutes
