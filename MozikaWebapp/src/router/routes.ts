import type { RouteRecordRaw } from 'vue-router'
import HomeView from '@/views/HomeView.vue'
import SettingsView from '@/views/settings/SettingsView.vue'
import NotFoundView from '@/views/error/NotFoundView.vue'
import MainLayout from '@/layouts/MainLayout.vue'
import albumRoutes from './entities/albumRoutes'
import artistGroupMemberRoutes from './entities/artistGroupMemberRoutes'
import artistTypeRoutes from './entities/artistTypeRoutes'
import artistRoutes from './entities/artistRoutes'
import audioStorageTypeRoutes from './entities/audioStorageTypeRoutes'
import clientRoutes from './entities/clientRoutes'
import commentRoutes from './entities/commentRoutes'
import concertRoutes from './entities/concertRoutes'
import contentSubmissionRoutes from './entities/contentSubmissionRoutes'
import downloadRoutes from './entities/downloadRoutes'
import editorialPlaylistSongRoutes from './entities/editorialPlaylistSongRoutes'
import editorialPlaylistRoutes from './entities/editorialPlaylistRoutes'
import eventMediaRoutes from './entities/eventMediaRoutes'
import eventModerationStatuseRoutes from './entities/eventModerationStatuseRoutes'
import eventRoutes from './entities/eventRoutes'
import followRoutes from './entities/followRoutes'
import genreRoutes from './entities/genreRoutes'
import instrumentRoleRoutes from './entities/instrumentRoleRoutes'
import interactionTargetRoutes from './entities/interactionTargetRoutes'
import likeRoutes from './entities/likeRoutes'
import listeningHistoryRoutes from './entities/listeningHistoryRoutes'
import mediaTypeRoutes from './entities/mediaTypeRoutes'
import memberStatuseRoutes from './entities/memberStatuseRoutes'
import notificationPreferenceRoutes from './entities/notificationPreferenceRoutes'
import notificationTypeRoutes from './entities/notificationTypeRoutes'
import notificationRoutes from './entities/notificationRoutes'
import participationStatuseRoutes from './entities/participationStatuseRoutes'
import playModeRoutes from './entities/playModeRoutes'
import playlistSongRoutes from './entities/playlistSongRoutes'
import playlistVisibilitieRoutes from './entities/playlistVisibilitieRoutes'
import playlistRoutes from './entities/playlistRoutes'
import reportStatuseRoutes from './entities/reportStatuseRoutes'
import reportRoutes from './entities/reportRoutes'
import searchHistoryRoutes from './entities/searchHistoryRoutes'
import songGenreRoutes from './entities/songGenreRoutes'
import songRoutes from './entities/songRoutes'
import submissionStatuseRoutes from './entities/submissionStatuseRoutes'
import upNextQueueRoutes from './entities/upNextQueueRoutes'
import userRoleRoutes from './entities/userRoleRoutes'
import userStatusHistoryRoutes from './entities/userStatusHistoryRoutes'
import userStatuseRoutes from './entities/userStatuseRoutes'
import userRoutes from './entities/userRoutes'
import venueRoutes from './entities/venueRoutes'
import verificationChannelRoutes from './entities/verificationChannelRoutes'
import verificationCodeRoutes from './entities/verificationCodeRoutes'
import verificationStatuseRoutes from './entities/verificationStatuseRoutes'

const routes: Array<RouteRecordRaw> = [
  {
    path: '/',
    redirect: '/home',
  },
  {
    path: '/',
    component: MainLayout,
    children: [
      { path: 'home', name: 'home', component: HomeView },
      { path: 'settings', name: 'settings', component: SettingsView },
      ...albumRoutes,
      ...artistGroupMemberRoutes,
      ...artistTypeRoutes,
      ...artistRoutes,
      ...audioStorageTypeRoutes,
      ...clientRoutes,
      ...commentRoutes,
      ...concertRoutes,
      ...contentSubmissionRoutes,
      ...downloadRoutes,
      ...editorialPlaylistSongRoutes,
      ...editorialPlaylistRoutes,
      ...eventMediaRoutes,
      ...eventModerationStatuseRoutes,
      ...eventRoutes,
      ...followRoutes,
      ...genreRoutes,
      ...instrumentRoleRoutes,
      ...interactionTargetRoutes,
      ...likeRoutes,
      ...listeningHistoryRoutes,
      ...mediaTypeRoutes,
      ...memberStatuseRoutes,
      ...notificationPreferenceRoutes,
      ...notificationTypeRoutes,
      ...notificationRoutes,
      ...participationStatuseRoutes,
      ...playModeRoutes,
      ...playlistSongRoutes,
      ...playlistVisibilitieRoutes,
      ...playlistRoutes,
      ...reportStatuseRoutes,
      ...reportRoutes,
      ...searchHistoryRoutes,
      ...songGenreRoutes,
      ...songRoutes,
      ...submissionStatuseRoutes,
      ...upNextQueueRoutes,
      ...userRoleRoutes,
      ...userStatusHistoryRoutes,
      ...userStatuseRoutes,
      ...userRoutes,
      ...venueRoutes,
      ...verificationChannelRoutes,
      ...verificationCodeRoutes,
      ...verificationStatuseRoutes,
    ],
  },
  {
    path: '/:pathMatch(.*)*',
    name: 'not-found',
    component: NotFoundView,
  },
]
export default routes
