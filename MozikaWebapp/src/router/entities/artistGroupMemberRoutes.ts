import type { RouteRecordRaw } from 'vue-router'

const artistGroupMemberRoutes: Array<RouteRecordRaw> = [
  {
    path: 'artistGroupMembers',
    name: 'artistGroupMemberlistview',
    component: () => import('@/views/artistGroupMember/ArtistGroupMemberListView.vue'),
  },
  {
    path: 'artistGroupMembers/:id',
    name: 'artistGroupMemberdetailsview',
    component: () => import('@/views/artistGroupMember/ArtistGroupMemberDetailsView.vue'),
  },
  {
    path: 'artistGroupMembers/update/:id',
    name: 'artistGroupMemberupdateview',
    component: () => import('@/views/artistGroupMember/ArtistGroupMemberUpdateView.vue'),
  },
  {
    path: 'artistGroupMembers/create',
    name: 'artistGroupMembercreateview',
    component: () => import('@/views/artistGroupMember/ArtistGroupMemberCreateView.vue'),
  },
]

export default artistGroupMemberRoutes
