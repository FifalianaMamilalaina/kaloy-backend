import type { RouteRecordRaw } from 'vue-router'

const listeningHistoryRoutes: Array<RouteRecordRaw> = [
  {
    path: 'listeningHistorys',
    name: 'listeningHistorylistview',
    component: () => import('@/views/listeningHistory/ListeningHistoryListView.vue'),
  },
  {
    path: 'listeningHistorys/:id',
    name: 'listeningHistorydetailsview',
    component: () => import('@/views/listeningHistory/ListeningHistoryDetailsView.vue'),
  },
  {
    path: 'listeningHistorys/update/:id',
    name: 'listeningHistoryupdateview',
    component: () => import('@/views/listeningHistory/ListeningHistoryUpdateView.vue'),
  },
  {
    path: 'listeningHistorys/create',
    name: 'listeningHistorycreateview',
    component: () => import('@/views/listeningHistory/ListeningHistoryCreateView.vue'),
  },
]

export default listeningHistoryRoutes
