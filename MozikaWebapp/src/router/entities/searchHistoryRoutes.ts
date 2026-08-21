import type { RouteRecordRaw } from 'vue-router'

const searchHistoryRoutes: Array<RouteRecordRaw> = [
  {
    path: 'searchHistorys',
    name: 'searchHistorylistview',
    component: () => import('@/views/searchHistory/SearchHistoryListView.vue'),
  },
  {
    path: 'searchHistorys/:id',
    name: 'searchHistorydetailsview',
    component: () => import('@/views/searchHistory/SearchHistoryDetailsView.vue'),
  },
  {
    path: 'searchHistorys/update/:id',
    name: 'searchHistoryupdateview',
    component: () => import('@/views/searchHistory/SearchHistoryUpdateView.vue'),
  },
  {
    path: 'searchHistorys/create',
    name: 'searchHistorycreateview',
    component: () => import('@/views/searchHistory/SearchHistoryCreateView.vue'),
  },
]

export default searchHistoryRoutes
