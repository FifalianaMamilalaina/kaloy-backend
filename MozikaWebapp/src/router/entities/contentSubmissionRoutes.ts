import type { RouteRecordRaw } from 'vue-router'

const contentSubmissionRoutes: Array<RouteRecordRaw> = [
  {
    path: 'contentSubmissions',
    name: 'contentSubmissionlistview',
    component: () => import('@/views/contentSubmission/ContentSubmissionListView.vue'),
  },
  {
    path: 'contentSubmissions/:id',
    name: 'contentSubmissiondetailsview',
    component: () => import('@/views/contentSubmission/ContentSubmissionDetailsView.vue'),
  },
  {
    path: 'contentSubmissions/update/:id',
    name: 'contentSubmissionupdateview',
    component: () => import('@/views/contentSubmission/ContentSubmissionUpdateView.vue'),
  },
  {
    path: 'contentSubmissions/create',
    name: 'contentSubmissioncreateview',
    component: () => import('@/views/contentSubmission/ContentSubmissionCreateView.vue'),
  },
]

export default contentSubmissionRoutes
