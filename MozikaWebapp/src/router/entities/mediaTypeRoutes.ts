import type { RouteRecordRaw } from 'vue-router'

const mediaTypeRoutes: Array<RouteRecordRaw> = [
  {
    path: 'mediaTypes',
    name: 'mediaTypelistview',
    component: () => import('@/views/mediaType/MediaTypeListView.vue'),
  },
  {
    path: 'mediaTypes/:id',
    name: 'mediaTypedetailsview',
    component: () => import('@/views/mediaType/MediaTypeDetailsView.vue'),
  },
  {
    path: 'mediaTypes/update/:id',
    name: 'mediaTypeupdateview',
    component: () => import('@/views/mediaType/MediaTypeUpdateView.vue'),
  },
  {
    path: 'mediaTypes/create',
    name: 'mediaTypecreateview',
    component: () => import('@/views/mediaType/MediaTypeCreateView.vue'),
  },
]

export default mediaTypeRoutes
