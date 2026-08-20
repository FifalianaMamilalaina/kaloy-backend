<template>
  <div class="w-full">
    <!-- Header -->
    <div class="items-center mb-6">
      <h2 class="text-xl font-bold text-primary">{{ $t('home.greetDeveloper') }}</h2>
      <span class="text-gray-500">{{ $t('home.welcome') }}</span>
    </div>

    <h2 class="text-xl font-bold text-neutral">{{ $t('home.titles.entities') }}</h2>
    <hr class="border-gray-200 mb-4" />
    <!-- Quick Actions -->
    <div class="grid grid-cols-1 md:grid-cols-4 gap-4 mb-8">
      <div
        class="p-4 bg-base-100 shadow-md rounded-xl flex flex-col border border-gray-200 items-start"
      >
        <h3 class="font-semibold text-lg mb-2">Albums</h3>
        <ul class="menu w-full">
          <li @click="useAlbum.goToListView()">
            <span>
              <LinkIcon />
              View list of Album
            </span>
          </li>

          <li @click="useAlbum.goToCreateFormView()">
            <span>
              <LinkIcon />
              Add new Album
            </span>
          </li>
        </ul>
      </div>
      <div
        class="p-4 bg-base-100 shadow-md rounded-xl flex flex-col border border-gray-200 items-start"
      >
        <h3 class="font-semibold text-lg mb-2">ArtistGroupMembers</h3>
        <ul class="menu w-full">
          <li @click="useArtistGroupMember.goToListView()">
            <span>
              <LinkIcon />
              View list of ArtistGroupMember
            </span>
          </li>

          <li @click="useArtistGroupMember.goToCreateFormView()">
            <span>
              <LinkIcon />
              Add new ArtistGroupMember
            </span>
          </li>
        </ul>
      </div>
      <div
        class="p-4 bg-base-100 shadow-md rounded-xl flex flex-col border border-gray-200 items-start"
      >
        <h3 class="font-semibold text-lg mb-2">ArtistTypes</h3>
        <ul class="menu w-full">
          <li @click="useArtistType.goToListView()">
            <span>
              <LinkIcon />
              View list of ArtistType
            </span>
          </li>

          <li @click="useArtistType.goToCreateFormView()">
            <span>
              <LinkIcon />
              Add new ArtistType
            </span>
          </li>
        </ul>
      </div>
      <div
        class="p-4 bg-base-100 shadow-md rounded-xl flex flex-col border border-gray-200 items-start"
      >
        <h3 class="font-semibold text-lg mb-2">Artists</h3>
        <ul class="menu w-full">
          <li @click="useArtist.goToListView()">
            <span>
              <LinkIcon />
              View list of Artist
            </span>
          </li>

          <li @click="useArtist.goToCreateFormView()">
            <span>
              <LinkIcon />
              Add new Artist
            </span>
          </li>
        </ul>
      </div>
      <div
        class="p-4 bg-base-100 shadow-md rounded-xl flex flex-col border border-gray-200 items-start"
      >
        <h3 class="font-semibold text-lg mb-2">AudioStorageTypes</h3>
        <ul class="menu w-full">
          <li @click="useAudioStorageType.goToListView()">
            <span>
              <LinkIcon />
              View list of AudioStorageType
            </span>
          </li>

          <li @click="useAudioStorageType.goToCreateFormView()">
            <span>
              <LinkIcon />
              Add new AudioStorageType
            </span>
          </li>
        </ul>
      </div>
      <div
        class="p-4 bg-base-100 shadow-md rounded-xl flex flex-col border border-gray-200 items-start"
      >
        <h3 class="font-semibold text-lg mb-2">Clients</h3>
        <ul class="menu w-full">
          <li @click="useClient.goToListView()">
            <span>
              <LinkIcon />
              View list of Client
            </span>
          </li>

          <li @click="useClient.goToCreateFormView()">
            <span>
              <LinkIcon />
              Add new Client
            </span>
          </li>
        </ul>
      </div>
      <div
        class="p-4 bg-base-100 shadow-md rounded-xl flex flex-col border border-gray-200 items-start"
      >
        <h3 class="font-semibold text-lg mb-2">Comments</h3>
        <ul class="menu w-full">
          <li @click="useComment.goToListView()">
            <span>
              <LinkIcon />
              View list of Comment
            </span>
          </li>

          <li @click="useComment.goToCreateFormView()">
            <span>
              <LinkIcon />
              Add new Comment
            </span>
          </li>
        </ul>
      </div>
      <div
        class="p-4 bg-base-100 shadow-md rounded-xl flex flex-col border border-gray-200 items-start"
      >
        <h3 class="font-semibold text-lg mb-2">Concerts</h3>
        <ul class="menu w-full">
          <li @click="useConcert.goToListView()">
            <span>
              <LinkIcon />
              View list of Concert
            </span>
          </li>

          <li @click="useConcert.goToCreateFormView()">
            <span>
              <LinkIcon />
              Add new Concert
            </span>
          </li>
        </ul>
      </div>
      <div
        class="p-4 bg-base-100 shadow-md rounded-xl flex flex-col border border-gray-200 items-start"
      >
        <h3 class="font-semibold text-lg mb-2">ContentSubmissions</h3>
        <ul class="menu w-full">
          <li @click="useContentSubmission.goToListView()">
            <span>
              <LinkIcon />
              View list of ContentSubmission
            </span>
          </li>

          <li @click="useContentSubmission.goToCreateFormView()">
            <span>
              <LinkIcon />
              Add new ContentSubmission
            </span>
          </li>
        </ul>
      </div>
      <div
        class="p-4 bg-base-100 shadow-md rounded-xl flex flex-col border border-gray-200 items-start"
      >
        <h3 class="font-semibold text-lg mb-2">Downloads</h3>
        <ul class="menu w-full">
          <li @click="useDownload.goToListView()">
            <span>
              <LinkIcon />
              View list of Download
            </span>
          </li>

          <li @click="useDownload.goToCreateFormView()">
            <span>
              <LinkIcon />
              Add new Download
            </span>
          </li>
        </ul>
      </div>
      <div
        class="p-4 bg-base-100 shadow-md rounded-xl flex flex-col border border-gray-200 items-start"
      >
        <h3 class="font-semibold text-lg mb-2">EditorialPlaylistSongs</h3>
        <ul class="menu w-full">
          <li @click="useEditorialPlaylistSong.goToListView()">
            <span>
              <LinkIcon />
              View list of EditorialPlaylistSong
            </span>
          </li>

          <li @click="useEditorialPlaylistSong.goToCreateFormView()">
            <span>
              <LinkIcon />
              Add new EditorialPlaylistSong
            </span>
          </li>
        </ul>
      </div>
      <div
        class="p-4 bg-base-100 shadow-md rounded-xl flex flex-col border border-gray-200 items-start"
      >
        <h3 class="font-semibold text-lg mb-2">EditorialPlaylists</h3>
        <ul class="menu w-full">
          <li @click="useEditorialPlaylist.goToListView()">
            <span>
              <LinkIcon />
              View list of EditorialPlaylist
            </span>
          </li>

          <li @click="useEditorialPlaylist.goToCreateFormView()">
            <span>
              <LinkIcon />
              Add new EditorialPlaylist
            </span>
          </li>
        </ul>
      </div>
      <div
        class="p-4 bg-base-100 shadow-md rounded-xl flex flex-col border border-gray-200 items-start"
      >
        <h3 class="font-semibold text-lg mb-2">EventMedias</h3>
        <ul class="menu w-full">
          <li @click="useEventMedia.goToListView()">
            <span>
              <LinkIcon />
              View list of EventMedia
            </span>
          </li>

          <li @click="useEventMedia.goToCreateFormView()">
            <span>
              <LinkIcon />
              Add new EventMedia
            </span>
          </li>
        </ul>
      </div>
      <div
        class="p-4 bg-base-100 shadow-md rounded-xl flex flex-col border border-gray-200 items-start"
      >
        <h3 class="font-semibold text-lg mb-2">EventModerationStatuses</h3>
        <ul class="menu w-full">
          <li @click="useEventModerationStatuse.goToListView()">
            <span>
              <LinkIcon />
              View list of EventModerationStatuse
            </span>
          </li>

          <li @click="useEventModerationStatuse.goToCreateFormView()">
            <span>
              <LinkIcon />
              Add new EventModerationStatuse
            </span>
          </li>
        </ul>
      </div>
      <div
        class="p-4 bg-base-100 shadow-md rounded-xl flex flex-col border border-gray-200 items-start"
      >
        <h3 class="font-semibold text-lg mb-2">Events</h3>
        <ul class="menu w-full">
          <li @click="useEvent.goToListView()">
            <span>
              <LinkIcon />
              View list of Event
            </span>
          </li>

          <li @click="useEvent.goToCreateFormView()">
            <span>
              <LinkIcon />
              Add new Event
            </span>
          </li>
        </ul>
      </div>
      <div
        class="p-4 bg-base-100 shadow-md rounded-xl flex flex-col border border-gray-200 items-start"
      >
        <h3 class="font-semibold text-lg mb-2">Follows</h3>
        <ul class="menu w-full">
          <li @click="useFollow.goToListView()">
            <span>
              <LinkIcon />
              View list of Follow
            </span>
          </li>

          <li @click="useFollow.goToCreateFormView()">
            <span>
              <LinkIcon />
              Add new Follow
            </span>
          </li>
        </ul>
      </div>
      <div
        class="p-4 bg-base-100 shadow-md rounded-xl flex flex-col border border-gray-200 items-start"
      >
        <h3 class="font-semibold text-lg mb-2">Genres</h3>
        <ul class="menu w-full">
          <li @click="useGenre.goToListView()">
            <span>
              <LinkIcon />
              View list of Genre
            </span>
          </li>

          <li @click="useGenre.goToCreateFormView()">
            <span>
              <LinkIcon />
              Add new Genre
            </span>
          </li>
        </ul>
      </div>
      <div
        class="p-4 bg-base-100 shadow-md rounded-xl flex flex-col border border-gray-200 items-start"
      >
        <h3 class="font-semibold text-lg mb-2">InstrumentRoles</h3>
        <ul class="menu w-full">
          <li @click="useInstrumentRole.goToListView()">
            <span>
              <LinkIcon />
              View list of InstrumentRole
            </span>
          </li>

          <li @click="useInstrumentRole.goToCreateFormView()">
            <span>
              <LinkIcon />
              Add new InstrumentRole
            </span>
          </li>
        </ul>
      </div>
      <div
        class="p-4 bg-base-100 shadow-md rounded-xl flex flex-col border border-gray-200 items-start"
      >
        <h3 class="font-semibold text-lg mb-2">InteractionTargets</h3>
        <ul class="menu w-full">
          <li @click="useInteractionTarget.goToListView()">
            <span>
              <LinkIcon />
              View list of InteractionTarget
            </span>
          </li>

          <li @click="useInteractionTarget.goToCreateFormView()">
            <span>
              <LinkIcon />
              Add new InteractionTarget
            </span>
          </li>
        </ul>
      </div>
      <div
        class="p-4 bg-base-100 shadow-md rounded-xl flex flex-col border border-gray-200 items-start"
      >
        <h3 class="font-semibold text-lg mb-2">Likes</h3>
        <ul class="menu w-full">
          <li @click="useLike.goToListView()">
            <span>
              <LinkIcon />
              View list of Like
            </span>
          </li>

          <li @click="useLike.goToCreateFormView()">
            <span>
              <LinkIcon />
              Add new Like
            </span>
          </li>
        </ul>
      </div>
      <div
        class="p-4 bg-base-100 shadow-md rounded-xl flex flex-col border border-gray-200 items-start"
      >
        <h3 class="font-semibold text-lg mb-2">ListeningHistorys</h3>
        <ul class="menu w-full">
          <li @click="useListeningHistory.goToListView()">
            <span>
              <LinkIcon />
              View list of ListeningHistory
            </span>
          </li>

          <li @click="useListeningHistory.goToCreateFormView()">
            <span>
              <LinkIcon />
              Add new ListeningHistory
            </span>
          </li>
        </ul>
      </div>
      <div
        class="p-4 bg-base-100 shadow-md rounded-xl flex flex-col border border-gray-200 items-start"
      >
        <h3 class="font-semibold text-lg mb-2">MediaTypes</h3>
        <ul class="menu w-full">
          <li @click="useMediaType.goToListView()">
            <span>
              <LinkIcon />
              View list of MediaType
            </span>
          </li>

          <li @click="useMediaType.goToCreateFormView()">
            <span>
              <LinkIcon />
              Add new MediaType
            </span>
          </li>
        </ul>
      </div>
      <div
        class="p-4 bg-base-100 shadow-md rounded-xl flex flex-col border border-gray-200 items-start"
      >
        <h3 class="font-semibold text-lg mb-2">MemberStatuses</h3>
        <ul class="menu w-full">
          <li @click="useMemberStatuse.goToListView()">
            <span>
              <LinkIcon />
              View list of MemberStatuse
            </span>
          </li>

          <li @click="useMemberStatuse.goToCreateFormView()">
            <span>
              <LinkIcon />
              Add new MemberStatuse
            </span>
          </li>
        </ul>
      </div>
      <div
        class="p-4 bg-base-100 shadow-md rounded-xl flex flex-col border border-gray-200 items-start"
      >
        <h3 class="font-semibold text-lg mb-2">NotificationPreferences</h3>
        <ul class="menu w-full">
          <li @click="useNotificationPreference.goToListView()">
            <span>
              <LinkIcon />
              View list of NotificationPreference
            </span>
          </li>

          <li @click="useNotificationPreference.goToCreateFormView()">
            <span>
              <LinkIcon />
              Add new NotificationPreference
            </span>
          </li>
        </ul>
      </div>
      <div
        class="p-4 bg-base-100 shadow-md rounded-xl flex flex-col border border-gray-200 items-start"
      >
        <h3 class="font-semibold text-lg mb-2">NotificationTypes</h3>
        <ul class="menu w-full">
          <li @click="useNotificationType.goToListView()">
            <span>
              <LinkIcon />
              View list of NotificationType
            </span>
          </li>

          <li @click="useNotificationType.goToCreateFormView()">
            <span>
              <LinkIcon />
              Add new NotificationType
            </span>
          </li>
        </ul>
      </div>
      <div
        class="p-4 bg-base-100 shadow-md rounded-xl flex flex-col border border-gray-200 items-start"
      >
        <h3 class="font-semibold text-lg mb-2">Notifications</h3>
        <ul class="menu w-full">
          <li @click="useNotification.goToListView()">
            <span>
              <LinkIcon />
              View list of Notification
            </span>
          </li>

          <li @click="useNotification.goToCreateFormView()">
            <span>
              <LinkIcon />
              Add new Notification
            </span>
          </li>
        </ul>
      </div>
      <div
        class="p-4 bg-base-100 shadow-md rounded-xl flex flex-col border border-gray-200 items-start"
      >
        <h3 class="font-semibold text-lg mb-2">ParticipationStatuses</h3>
        <ul class="menu w-full">
          <li @click="useParticipationStatuse.goToListView()">
            <span>
              <LinkIcon />
              View list of ParticipationStatuse
            </span>
          </li>

          <li @click="useParticipationStatuse.goToCreateFormView()">
            <span>
              <LinkIcon />
              Add new ParticipationStatuse
            </span>
          </li>
        </ul>
      </div>
      <div
        class="p-4 bg-base-100 shadow-md rounded-xl flex flex-col border border-gray-200 items-start"
      >
        <h3 class="font-semibold text-lg mb-2">PlayModes</h3>
        <ul class="menu w-full">
          <li @click="usePlayMode.goToListView()">
            <span>
              <LinkIcon />
              View list of PlayMode
            </span>
          </li>

          <li @click="usePlayMode.goToCreateFormView()">
            <span>
              <LinkIcon />
              Add new PlayMode
            </span>
          </li>
        </ul>
      </div>
      <div
        class="p-4 bg-base-100 shadow-md rounded-xl flex flex-col border border-gray-200 items-start"
      >
        <h3 class="font-semibold text-lg mb-2">PlaylistSongs</h3>
        <ul class="menu w-full">
          <li @click="usePlaylistSong.goToListView()">
            <span>
              <LinkIcon />
              View list of PlaylistSong
            </span>
          </li>

          <li @click="usePlaylistSong.goToCreateFormView()">
            <span>
              <LinkIcon />
              Add new PlaylistSong
            </span>
          </li>
        </ul>
      </div>
      <div
        class="p-4 bg-base-100 shadow-md rounded-xl flex flex-col border border-gray-200 items-start"
      >
        <h3 class="font-semibold text-lg mb-2">PlaylistVisibilities</h3>
        <ul class="menu w-full">
          <li @click="usePlaylistVisibilitie.goToListView()">
            <span>
              <LinkIcon />
              View list of PlaylistVisibilitie
            </span>
          </li>

          <li @click="usePlaylistVisibilitie.goToCreateFormView()">
            <span>
              <LinkIcon />
              Add new PlaylistVisibilitie
            </span>
          </li>
        </ul>
      </div>
      <div
        class="p-4 bg-base-100 shadow-md rounded-xl flex flex-col border border-gray-200 items-start"
      >
        <h3 class="font-semibold text-lg mb-2">Playlists</h3>
        <ul class="menu w-full">
          <li @click="usePlaylist.goToListView()">
            <span>
              <LinkIcon />
              View list of Playlist
            </span>
          </li>

          <li @click="usePlaylist.goToCreateFormView()">
            <span>
              <LinkIcon />
              Add new Playlist
            </span>
          </li>
        </ul>
      </div>
      <div
        class="p-4 bg-base-100 shadow-md rounded-xl flex flex-col border border-gray-200 items-start"
      >
        <h3 class="font-semibold text-lg mb-2">ReportStatuses</h3>
        <ul class="menu w-full">
          <li @click="useReportStatuse.goToListView()">
            <span>
              <LinkIcon />
              View list of ReportStatuse
            </span>
          </li>

          <li @click="useReportStatuse.goToCreateFormView()">
            <span>
              <LinkIcon />
              Add new ReportStatuse
            </span>
          </li>
        </ul>
      </div>
      <div
        class="p-4 bg-base-100 shadow-md rounded-xl flex flex-col border border-gray-200 items-start"
      >
        <h3 class="font-semibold text-lg mb-2">Reports</h3>
        <ul class="menu w-full">
          <li @click="useReport.goToListView()">
            <span>
              <LinkIcon />
              View list of Report
            </span>
          </li>

          <li @click="useReport.goToCreateFormView()">
            <span>
              <LinkIcon />
              Add new Report
            </span>
          </li>
        </ul>
      </div>
      <div
        class="p-4 bg-base-100 shadow-md rounded-xl flex flex-col border border-gray-200 items-start"
      >
        <h3 class="font-semibold text-lg mb-2">SearchHistorys</h3>
        <ul class="menu w-full">
          <li @click="useSearchHistory.goToListView()">
            <span>
              <LinkIcon />
              View list of SearchHistory
            </span>
          </li>

          <li @click="useSearchHistory.goToCreateFormView()">
            <span>
              <LinkIcon />
              Add new SearchHistory
            </span>
          </li>
        </ul>
      </div>
      <div
        class="p-4 bg-base-100 shadow-md rounded-xl flex flex-col border border-gray-200 items-start"
      >
        <h3 class="font-semibold text-lg mb-2">SongGenres</h3>
        <ul class="menu w-full">
          <li @click="useSongGenre.goToListView()">
            <span>
              <LinkIcon />
              View list of SongGenre
            </span>
          </li>

          <li @click="useSongGenre.goToCreateFormView()">
            <span>
              <LinkIcon />
              Add new SongGenre
            </span>
          </li>
        </ul>
      </div>
      <div
        class="p-4 bg-base-100 shadow-md rounded-xl flex flex-col border border-gray-200 items-start"
      >
        <h3 class="font-semibold text-lg mb-2">Songs</h3>
        <ul class="menu w-full">
          <li @click="useSong.goToListView()">
            <span>
              <LinkIcon />
              View list of Song
            </span>
          </li>

          <li @click="useSong.goToCreateFormView()">
            <span>
              <LinkIcon />
              Add new Song
            </span>
          </li>
        </ul>
      </div>
      <div
        class="p-4 bg-base-100 shadow-md rounded-xl flex flex-col border border-gray-200 items-start"
      >
        <h3 class="font-semibold text-lg mb-2">SubmissionStatuses</h3>
        <ul class="menu w-full">
          <li @click="useSubmissionStatuse.goToListView()">
            <span>
              <LinkIcon />
              View list of SubmissionStatuse
            </span>
          </li>

          <li @click="useSubmissionStatuse.goToCreateFormView()">
            <span>
              <LinkIcon />
              Add new SubmissionStatuse
            </span>
          </li>
        </ul>
      </div>
      <div
        class="p-4 bg-base-100 shadow-md rounded-xl flex flex-col border border-gray-200 items-start"
      >
        <h3 class="font-semibold text-lg mb-2">UpNextQueues</h3>
        <ul class="menu w-full">
          <li @click="useUpNextQueue.goToListView()">
            <span>
              <LinkIcon />
              View list of UpNextQueue
            </span>
          </li>

          <li @click="useUpNextQueue.goToCreateFormView()">
            <span>
              <LinkIcon />
              Add new UpNextQueue
            </span>
          </li>
        </ul>
      </div>
      <div
        class="p-4 bg-base-100 shadow-md rounded-xl flex flex-col border border-gray-200 items-start"
      >
        <h3 class="font-semibold text-lg mb-2">UserRoles</h3>
        <ul class="menu w-full">
          <li @click="useUserRole.goToListView()">
            <span>
              <LinkIcon />
              View list of UserRole
            </span>
          </li>

          <li @click="useUserRole.goToCreateFormView()">
            <span>
              <LinkIcon />
              Add new UserRole
            </span>
          </li>
        </ul>
      </div>
      <div
        class="p-4 bg-base-100 shadow-md rounded-xl flex flex-col border border-gray-200 items-start"
      >
        <h3 class="font-semibold text-lg mb-2">UserStatusHistorys</h3>
        <ul class="menu w-full">
          <li @click="useUserStatusHistory.goToListView()">
            <span>
              <LinkIcon />
              View list of UserStatusHistory
            </span>
          </li>

          <li @click="useUserStatusHistory.goToCreateFormView()">
            <span>
              <LinkIcon />
              Add new UserStatusHistory
            </span>
          </li>
        </ul>
      </div>
      <div
        class="p-4 bg-base-100 shadow-md rounded-xl flex flex-col border border-gray-200 items-start"
      >
        <h3 class="font-semibold text-lg mb-2">UserStatuses</h3>
        <ul class="menu w-full">
          <li @click="useUserStatuse.goToListView()">
            <span>
              <LinkIcon />
              View list of UserStatuse
            </span>
          </li>

          <li @click="useUserStatuse.goToCreateFormView()">
            <span>
              <LinkIcon />
              Add new UserStatuse
            </span>
          </li>
        </ul>
      </div>
      <div
        class="p-4 bg-base-100 shadow-md rounded-xl flex flex-col border border-gray-200 items-start"
      >
        <h3 class="font-semibold text-lg mb-2">Users</h3>
        <ul class="menu w-full">
          <li @click="useUser.goToListView()">
            <span>
              <LinkIcon />
              View list of User
            </span>
          </li>

          <li @click="useUser.goToCreateFormView()">
            <span>
              <LinkIcon />
              Add new User
            </span>
          </li>
        </ul>
      </div>
      <div
        class="p-4 bg-base-100 shadow-md rounded-xl flex flex-col border border-gray-200 items-start"
      >
        <h3 class="font-semibold text-lg mb-2">Venues</h3>
        <ul class="menu w-full">
          <li @click="useVenue.goToListView()">
            <span>
              <LinkIcon />
              View list of Venue
            </span>
          </li>

          <li @click="useVenue.goToCreateFormView()">
            <span>
              <LinkIcon />
              Add new Venue
            </span>
          </li>
        </ul>
      </div>
      <div
        class="p-4 bg-base-100 shadow-md rounded-xl flex flex-col border border-gray-200 items-start"
      >
        <h3 class="font-semibold text-lg mb-2">VerificationChannels</h3>
        <ul class="menu w-full">
          <li @click="useVerificationChannel.goToListView()">
            <span>
              <LinkIcon />
              View list of VerificationChannel
            </span>
          </li>

          <li @click="useVerificationChannel.goToCreateFormView()">
            <span>
              <LinkIcon />
              Add new VerificationChannel
            </span>
          </li>
        </ul>
      </div>
      <div
        class="p-4 bg-base-100 shadow-md rounded-xl flex flex-col border border-gray-200 items-start"
      >
        <h3 class="font-semibold text-lg mb-2">VerificationCodes</h3>
        <ul class="menu w-full">
          <li @click="useVerificationCode.goToListView()">
            <span>
              <LinkIcon />
              View list of VerificationCode
            </span>
          </li>

          <li @click="useVerificationCode.goToCreateFormView()">
            <span>
              <LinkIcon />
              Add new VerificationCode
            </span>
          </li>
        </ul>
      </div>
      <div
        class="p-4 bg-base-100 shadow-md rounded-xl flex flex-col border border-gray-200 items-start"
      >
        <h3 class="font-semibold text-lg mb-2">VerificationStatuses</h3>
        <ul class="menu w-full">
          <li @click="useVerificationStatuse.goToListView()">
            <span>
              <LinkIcon />
              View list of VerificationStatuse
            </span>
          </li>

          <li @click="useVerificationStatuse.goToCreateFormView()">
            <span>
              <LinkIcon />
              Add new VerificationStatuse
            </span>
          </li>
        </ul>
      </div>
    </div>

    <h2 class="text-xl font-bold text-neutral">{{ $t('home.titles.views') }}</h2>
    <hr class="border-gray-200 mb-4" />
    <!-- Quick Actions -->
    <div class="grid grid-cols-1 md:grid-cols-4 gap-4 mb-8"></div>
  </div>
</template>

<script setup lang="ts">
import LinkIcon from '@/components/icons/LinkIcon.vue'
import { useAlbums } from '@/composables/useAlbums'
import { useArtistGroupMembers } from '@/composables/useArtistGroupMembers'
import { useArtistTypes } from '@/composables/useArtistTypes'
import { useArtists } from '@/composables/useArtists'
import { useAudioStorageTypes } from '@/composables/useAudioStorageTypes'
import { useClients } from '@/composables/useClients'
import { useComments } from '@/composables/useComments'
import { useConcerts } from '@/composables/useConcerts'
import { useContentSubmissions } from '@/composables/useContentSubmissions'
import { useDownloads } from '@/composables/useDownloads'
import { useEditorialPlaylistSongs } from '@/composables/useEditorialPlaylistSongs'
import { useEditorialPlaylists } from '@/composables/useEditorialPlaylists'
import { useEventMedias } from '@/composables/useEventMedias'
import { useEventModerationStatuses } from '@/composables/useEventModerationStatuses'
import { useEvents } from '@/composables/useEvents'
import { useFollows } from '@/composables/useFollows'
import { useGenres } from '@/composables/useGenres'
import { useInstrumentRoles } from '@/composables/useInstrumentRoles'
import { useInteractionTargets } from '@/composables/useInteractionTargets'
import { useLikes } from '@/composables/useLikes'
import { useListeningHistorys } from '@/composables/useListeningHistorys'
import { useMediaTypes } from '@/composables/useMediaTypes'
import { useMemberStatuses } from '@/composables/useMemberStatuses'
import { useNotificationPreferences } from '@/composables/useNotificationPreferences'
import { useNotificationTypes } from '@/composables/useNotificationTypes'
import { useNotifications } from '@/composables/useNotifications'
import { useParticipationStatuses } from '@/composables/useParticipationStatuses'
import { usePlayModes } from '@/composables/usePlayModes'
import { usePlaylistSongs } from '@/composables/usePlaylistSongs'
import { usePlaylistVisibilities } from '@/composables/usePlaylistVisibilities'
import { usePlaylists } from '@/composables/usePlaylists'
import { useReportStatuses } from '@/composables/useReportStatuses'
import { useReports } from '@/composables/useReports'
import { useSearchHistorys } from '@/composables/useSearchHistorys'
import { useSongGenres } from '@/composables/useSongGenres'
import { useSongs } from '@/composables/useSongs'
import { useSubmissionStatuses } from '@/composables/useSubmissionStatuses'
import { useUpNextQueues } from '@/composables/useUpNextQueues'
import { useUserRoles } from '@/composables/useUserRoles'
import { useUserStatusHistorys } from '@/composables/useUserStatusHistorys'
import { useUserStatuses } from '@/composables/useUserStatuses'
import { useUsers } from '@/composables/useUsers'
import { useVenues } from '@/composables/useVenues'
import { useVerificationChannels } from '@/composables/useVerificationChannels'
import { useVerificationCodes } from '@/composables/useVerificationCodes'
import { useVerificationStatuses } from '@/composables/useVerificationStatuses'

const useAlbum = useAlbums()
const useArtistGroupMember = useArtistGroupMembers()
const useArtistType = useArtistTypes()
const useArtist = useArtists()
const useAudioStorageType = useAudioStorageTypes()
const useClient = useClients()
const useComment = useComments()
const useConcert = useConcerts()
const useContentSubmission = useContentSubmissions()
const useDownload = useDownloads()
const useEditorialPlaylistSong = useEditorialPlaylistSongs()
const useEditorialPlaylist = useEditorialPlaylists()
const useEventMedia = useEventMedias()
const useEventModerationStatuse = useEventModerationStatuses()
const useEvent = useEvents()
const useFollow = useFollows()
const useGenre = useGenres()
const useInstrumentRole = useInstrumentRoles()
const useInteractionTarget = useInteractionTargets()
const useLike = useLikes()
const useListeningHistory = useListeningHistorys()
const useMediaType = useMediaTypes()
const useMemberStatuse = useMemberStatuses()
const useNotificationPreference = useNotificationPreferences()
const useNotificationType = useNotificationTypes()
const useNotification = useNotifications()
const useParticipationStatuse = useParticipationStatuses()
const usePlayMode = usePlayModes()
const usePlaylistSong = usePlaylistSongs()
const usePlaylistVisibilitie = usePlaylistVisibilities()
const usePlaylist = usePlaylists()
const useReportStatuse = useReportStatuses()
const useReport = useReports()
const useSearchHistory = useSearchHistorys()
const useSongGenre = useSongGenres()
const useSong = useSongs()
const useSubmissionStatuse = useSubmissionStatuses()
const useUpNextQueue = useUpNextQueues()
const useUserRole = useUserRoles()
const useUserStatusHistory = useUserStatusHistorys()
const useUserStatuse = useUserStatuses()
const useUser = useUsers()
const useVenue = useVenues()
const useVerificationChannel = useVerificationChannels()
const useVerificationCode = useVerificationCodes()
const useVerificationStatuse = useVerificationStatuses()
</script>
