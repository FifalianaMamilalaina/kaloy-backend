import type { RouteRecordRaw } from 'vue-router'

const playModeRoutes: Array<RouteRecordRaw> = [
  {
    path: 'playModes',
    name: 'playModelistview',
    component: () => import('@/views/playMode/PlayModeListView.vue'),
  },
  {
    path: 'playModes/:id',
    name: 'playModedetailsview',
    component: () => import('@/views/playMode/PlayModeDetailsView.vue'),
  },
  {
    path: 'playModes/update/:id',
    name: 'playModeupdateview',
    component: () => import('@/views/playMode/PlayModeUpdateView.vue'),
  },
  {
    path: 'playModes/create',
    name: 'playModecreateview',
    component: () => import('@/views/playMode/PlayModeCreateView.vue'),
  },
]

export default playModeRoutes
