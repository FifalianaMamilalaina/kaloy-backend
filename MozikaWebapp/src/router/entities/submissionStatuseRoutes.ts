import type { RouteRecordRaw } from 'vue-router'

const submissionStatuseRoutes: Array<RouteRecordRaw> = [
  {
    path: 'submissionStatuses',
    name: 'submissionStatuselistview',
    component: () => import('@/views/submissionStatuse/SubmissionStatuseListView.vue'),
  },
  {
    path: 'submissionStatuses/:id',
    name: 'submissionStatusedetailsview',
    component: () => import('@/views/submissionStatuse/SubmissionStatuseDetailsView.vue'),
  },
  {
    path: 'submissionStatuses/update/:id',
    name: 'submissionStatuseupdateview',
    component: () => import('@/views/submissionStatuse/SubmissionStatuseUpdateView.vue'),
  },
  {
    path: 'submissionStatuses/create',
    name: 'submissionStatusecreateview',
    component: () => import('@/views/submissionStatuse/SubmissionStatuseCreateView.vue'),
  },
]

export default submissionStatuseRoutes
