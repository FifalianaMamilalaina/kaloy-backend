const navigations = [
  // ENTITIES NAVIGATION
  {
    icon: 'fas fa-layer-group',
    sectionName: 'navbar.entities',
    navChilds: [
      {
        navTitle: 'entities.album.nav.title',
        navChilds: [
          {
            navTitle: 'entities.album.nav.list',
            navLink: '/albums',
          },
          {
            navTitle: 'entities.album.nav.create',
            navLink: '/albums/create',
          },
        ],
      },

      {
        navTitle: 'entities.artistGroupMember.nav.title',
        navChilds: [
          {
            navTitle: 'entities.artistGroupMember.nav.list',
            navLink: '/artistGroupMembers',
          },
          {
            navTitle: 'entities.artistGroupMember.nav.create',
            navLink: '/artistGroupMembers/create',
          },
        ],
      },

      {
        navTitle: 'entities.artistType.nav.title',
        navChilds: [
          {
            navTitle: 'entities.artistType.nav.list',
            navLink: '/artistTypes',
          },
          {
            navTitle: 'entities.artistType.nav.create',
            navLink: '/artistTypes/create',
          },
        ],
      },

      {
        navTitle: 'entities.artist.nav.title',
        navChilds: [
          {
            navTitle: 'entities.artist.nav.list',
            navLink: '/artists',
          },
          {
            navTitle: 'entities.artist.nav.create',
            navLink: '/artists/create',
          },
        ],
      },

      {
        navTitle: 'entities.audioStorageType.nav.title',
        navChilds: [
          {
            navTitle: 'entities.audioStorageType.nav.list',
            navLink: '/audioStorageTypes',
          },
          {
            navTitle: 'entities.audioStorageType.nav.create',
            navLink: '/audioStorageTypes/create',
          },
        ],
      },

      {
        navTitle: 'entities.client.nav.title',
        navChilds: [
          {
            navTitle: 'entities.client.nav.list',
            navLink: '/clients',
          },
          {
            navTitle: 'entities.client.nav.create',
            navLink: '/clients/create',
          },
        ],
      },

      {
        navTitle: 'entities.comment.nav.title',
        navChilds: [
          {
            navTitle: 'entities.comment.nav.list',
            navLink: '/comments',
          },
          {
            navTitle: 'entities.comment.nav.create',
            navLink: '/comments/create',
          },
        ],
      },

      {
        navTitle: 'entities.concert.nav.title',
        navChilds: [
          {
            navTitle: 'entities.concert.nav.list',
            navLink: '/concerts',
          },
          {
            navTitle: 'entities.concert.nav.create',
            navLink: '/concerts/create',
          },
        ],
      },

      {
        navTitle: 'entities.contentSubmission.nav.title',
        navChilds: [
          {
            navTitle: 'entities.contentSubmission.nav.list',
            navLink: '/contentSubmissions',
          },
          {
            navTitle: 'entities.contentSubmission.nav.create',
            navLink: '/contentSubmissions/create',
          },
        ],
      },

      {
        navTitle: 'entities.download.nav.title',
        navChilds: [
          {
            navTitle: 'entities.download.nav.list',
            navLink: '/downloads',
          },
          {
            navTitle: 'entities.download.nav.create',
            navLink: '/downloads/create',
          },
        ],
      },

      {
        navTitle: 'entities.editorialPlaylistSong.nav.title',
        navChilds: [
          {
            navTitle: 'entities.editorialPlaylistSong.nav.list',
            navLink: '/editorialPlaylistSongs',
          },
          {
            navTitle: 'entities.editorialPlaylistSong.nav.create',
            navLink: '/editorialPlaylistSongs/create',
          },
        ],
      },

      {
        navTitle: 'entities.editorialPlaylist.nav.title',
        navChilds: [
          {
            navTitle: 'entities.editorialPlaylist.nav.list',
            navLink: '/editorialPlaylists',
          },
          {
            navTitle: 'entities.editorialPlaylist.nav.create',
            navLink: '/editorialPlaylists/create',
          },
        ],
      },

      {
        navTitle: 'entities.eventMedia.nav.title',
        navChilds: [
          {
            navTitle: 'entities.eventMedia.nav.list',
            navLink: '/eventMedias',
          },
          {
            navTitle: 'entities.eventMedia.nav.create',
            navLink: '/eventMedias/create',
          },
        ],
      },

      {
        navTitle: 'entities.eventModerationStatuse.nav.title',
        navChilds: [
          {
            navTitle: 'entities.eventModerationStatuse.nav.list',
            navLink: '/eventModerationStatuses',
          },
          {
            navTitle: 'entities.eventModerationStatuse.nav.create',
            navLink: '/eventModerationStatuses/create',
          },
        ],
      },

      {
        navTitle: 'entities.event.nav.title',
        navChilds: [
          {
            navTitle: 'entities.event.nav.list',
            navLink: '/events',
          },
          {
            navTitle: 'entities.event.nav.create',
            navLink: '/events/create',
          },
        ],
      },

      {
        navTitle: 'entities.follow.nav.title',
        navChilds: [
          {
            navTitle: 'entities.follow.nav.list',
            navLink: '/follows',
          },
          {
            navTitle: 'entities.follow.nav.create',
            navLink: '/follows/create',
          },
        ],
      },

      {
        navTitle: 'entities.genre.nav.title',
        navChilds: [
          {
            navTitle: 'entities.genre.nav.list',
            navLink: '/genres',
          },
          {
            navTitle: 'entities.genre.nav.create',
            navLink: '/genres/create',
          },
        ],
      },

      {
        navTitle: 'entities.instrumentRole.nav.title',
        navChilds: [
          {
            navTitle: 'entities.instrumentRole.nav.list',
            navLink: '/instrumentRoles',
          },
          {
            navTitle: 'entities.instrumentRole.nav.create',
            navLink: '/instrumentRoles/create',
          },
        ],
      },

      {
        navTitle: 'entities.interactionTarget.nav.title',
        navChilds: [
          {
            navTitle: 'entities.interactionTarget.nav.list',
            navLink: '/interactionTargets',
          },
          {
            navTitle: 'entities.interactionTarget.nav.create',
            navLink: '/interactionTargets/create',
          },
        ],
      },

      {
        navTitle: 'entities.like.nav.title',
        navChilds: [
          {
            navTitle: 'entities.like.nav.list',
            navLink: '/likes',
          },
          {
            navTitle: 'entities.like.nav.create',
            navLink: '/likes/create',
          },
        ],
      },

      {
        navTitle: 'entities.listeningHistory.nav.title',
        navChilds: [
          {
            navTitle: 'entities.listeningHistory.nav.list',
            navLink: '/listeningHistorys',
          },
          {
            navTitle: 'entities.listeningHistory.nav.create',
            navLink: '/listeningHistorys/create',
          },
        ],
      },

      {
        navTitle: 'entities.mediaType.nav.title',
        navChilds: [
          {
            navTitle: 'entities.mediaType.nav.list',
            navLink: '/mediaTypes',
          },
          {
            navTitle: 'entities.mediaType.nav.create',
            navLink: '/mediaTypes/create',
          },
        ],
      },

      {
        navTitle: 'entities.memberStatuse.nav.title',
        navChilds: [
          {
            navTitle: 'entities.memberStatuse.nav.list',
            navLink: '/memberStatuses',
          },
          {
            navTitle: 'entities.memberStatuse.nav.create',
            navLink: '/memberStatuses/create',
          },
        ],
      },

      {
        navTitle: 'entities.notificationPreference.nav.title',
        navChilds: [
          {
            navTitle: 'entities.notificationPreference.nav.list',
            navLink: '/notificationPreferences',
          },
          {
            navTitle: 'entities.notificationPreference.nav.create',
            navLink: '/notificationPreferences/create',
          },
        ],
      },

      {
        navTitle: 'entities.notificationType.nav.title',
        navChilds: [
          {
            navTitle: 'entities.notificationType.nav.list',
            navLink: '/notificationTypes',
          },
          {
            navTitle: 'entities.notificationType.nav.create',
            navLink: '/notificationTypes/create',
          },
        ],
      },

      {
        navTitle: 'entities.notification.nav.title',
        navChilds: [
          {
            navTitle: 'entities.notification.nav.list',
            navLink: '/notifications',
          },
          {
            navTitle: 'entities.notification.nav.create',
            navLink: '/notifications/create',
          },
        ],
      },

      {
        navTitle: 'entities.participationStatuse.nav.title',
        navChilds: [
          {
            navTitle: 'entities.participationStatuse.nav.list',
            navLink: '/participationStatuses',
          },
          {
            navTitle: 'entities.participationStatuse.nav.create',
            navLink: '/participationStatuses/create',
          },
        ],
      },

      {
        navTitle: 'entities.playMode.nav.title',
        navChilds: [
          {
            navTitle: 'entities.playMode.nav.list',
            navLink: '/playModes',
          },
          {
            navTitle: 'entities.playMode.nav.create',
            navLink: '/playModes/create',
          },
        ],
      },

      {
        navTitle: 'entities.playlistSong.nav.title',
        navChilds: [
          {
            navTitle: 'entities.playlistSong.nav.list',
            navLink: '/playlistSongs',
          },
          {
            navTitle: 'entities.playlistSong.nav.create',
            navLink: '/playlistSongs/create',
          },
        ],
      },

      {
        navTitle: 'entities.playlistVisibilitie.nav.title',
        navChilds: [
          {
            navTitle: 'entities.playlistVisibilitie.nav.list',
            navLink: '/playlistVisibilities',
          },
          {
            navTitle: 'entities.playlistVisibilitie.nav.create',
            navLink: '/playlistVisibilities/create',
          },
        ],
      },

      {
        navTitle: 'entities.playlist.nav.title',
        navChilds: [
          {
            navTitle: 'entities.playlist.nav.list',
            navLink: '/playlists',
          },
          {
            navTitle: 'entities.playlist.nav.create',
            navLink: '/playlists/create',
          },
        ],
      },

      {
        navTitle: 'entities.reportStatuse.nav.title',
        navChilds: [
          {
            navTitle: 'entities.reportStatuse.nav.list',
            navLink: '/reportStatuses',
          },
          {
            navTitle: 'entities.reportStatuse.nav.create',
            navLink: '/reportStatuses/create',
          },
        ],
      },

      {
        navTitle: 'entities.report.nav.title',
        navChilds: [
          {
            navTitle: 'entities.report.nav.list',
            navLink: '/reports',
          },
          {
            navTitle: 'entities.report.nav.create',
            navLink: '/reports/create',
          },
        ],
      },

      {
        navTitle: 'entities.searchHistory.nav.title',
        navChilds: [
          {
            navTitle: 'entities.searchHistory.nav.list',
            navLink: '/searchHistorys',
          },
          {
            navTitle: 'entities.searchHistory.nav.create',
            navLink: '/searchHistorys/create',
          },
        ],
      },

      {
        navTitle: 'entities.songGenre.nav.title',
        navChilds: [
          {
            navTitle: 'entities.songGenre.nav.list',
            navLink: '/songGenres',
          },
          {
            navTitle: 'entities.songGenre.nav.create',
            navLink: '/songGenres/create',
          },
        ],
      },

      {
        navTitle: 'entities.song.nav.title',
        navChilds: [
          {
            navTitle: 'entities.song.nav.list',
            navLink: '/songs',
          },
          {
            navTitle: 'entities.song.nav.create',
            navLink: '/songs/create',
          },
        ],
      },

      {
        navTitle: 'entities.submissionStatuse.nav.title',
        navChilds: [
          {
            navTitle: 'entities.submissionStatuse.nav.list',
            navLink: '/submissionStatuses',
          },
          {
            navTitle: 'entities.submissionStatuse.nav.create',
            navLink: '/submissionStatuses/create',
          },
        ],
      },

      {
        navTitle: 'entities.upNextQueue.nav.title',
        navChilds: [
          {
            navTitle: 'entities.upNextQueue.nav.list',
            navLink: '/upNextQueues',
          },
          {
            navTitle: 'entities.upNextQueue.nav.create',
            navLink: '/upNextQueues/create',
          },
        ],
      },

      {
        navTitle: 'entities.userRole.nav.title',
        navChilds: [
          {
            navTitle: 'entities.userRole.nav.list',
            navLink: '/userRoles',
          },
          {
            navTitle: 'entities.userRole.nav.create',
            navLink: '/userRoles/create',
          },
        ],
      },

      {
        navTitle: 'entities.userStatusHistory.nav.title',
        navChilds: [
          {
            navTitle: 'entities.userStatusHistory.nav.list',
            navLink: '/userStatusHistorys',
          },
          {
            navTitle: 'entities.userStatusHistory.nav.create',
            navLink: '/userStatusHistorys/create',
          },
        ],
      },

      {
        navTitle: 'entities.userStatuse.nav.title',
        navChilds: [
          {
            navTitle: 'entities.userStatuse.nav.list',
            navLink: '/userStatuses',
          },
          {
            navTitle: 'entities.userStatuse.nav.create',
            navLink: '/userStatuses/create',
          },
        ],
      },

      {
        navTitle: 'entities.user.nav.title',
        navChilds: [
          {
            navTitle: 'entities.user.nav.list',
            navLink: '/users',
          },
          {
            navTitle: 'entities.user.nav.create',
            navLink: '/users/create',
          },
        ],
      },

      {
        navTitle: 'entities.venue.nav.title',
        navChilds: [
          {
            navTitle: 'entities.venue.nav.list',
            navLink: '/venues',
          },
          {
            navTitle: 'entities.venue.nav.create',
            navLink: '/venues/create',
          },
        ],
      },

      {
        navTitle: 'entities.verificationChannel.nav.title',
        navChilds: [
          {
            navTitle: 'entities.verificationChannel.nav.list',
            navLink: '/verificationChannels',
          },
          {
            navTitle: 'entities.verificationChannel.nav.create',
            navLink: '/verificationChannels/create',
          },
        ],
      },

      {
        navTitle: 'entities.verificationCode.nav.title',
        navChilds: [
          {
            navTitle: 'entities.verificationCode.nav.list',
            navLink: '/verificationCodes',
          },
          {
            navTitle: 'entities.verificationCode.nav.create',
            navLink: '/verificationCodes/create',
          },
        ],
      },

      {
        navTitle: 'entities.verificationStatuse.nav.title',
        navChilds: [
          {
            navTitle: 'entities.verificationStatuse.nav.list',
            navLink: '/verificationStatuses',
          },
          {
            navTitle: 'entities.verificationStatuse.nav.create',
            navLink: '/verificationStatuses/create',
          },
        ],
      },
    ],
  },
  // VIEWS NAVIGATION
  {
    icon: 'fas fa-box',
    sectionName: 'navbar.views',
    navChilds: [],
  },
]
export default navigations
