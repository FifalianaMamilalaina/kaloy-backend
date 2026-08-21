const english = {
  messages: {
    projectName: 'Mozika',
    common: {
      update: 'update',
      delete: 'delete',
      create: 'create',
      apply: 'Apply',
      go: 'go',
      views: 'views',
      view: 'view',
      details: 'details',
      list: 'list',
      new: 'new',
      actions: 'actions',
      back: 'back',
      layout: 'layout',
      theme: 'theme',
      general: 'general',
      settings: 'settings',
      entities: 'entities',
      entity: 'entity',
      filter: 'filter',
      rows: 'rows',
      showing: 'showing',
      previous: 'previous',
      next: 'next',
      page: 'page',
      add: 'add',
      description: 'description',
      return: 'return',
      home: 'home',
      login: 'login',
      logout: 'logout',
      register: 'register',
      profile: 'profile',
      username: 'username',
      email: 'email',
      password: 'password',
      language: 'language',
      confirm: 'confirm',
      cancel: 'cancel',
      no: 'no',
      save: 'Save',
      edit: 'Edit',
      read: 'read',
    },
    box_confirm: {
      H1Se: 'Are you sure you want to delete this item?',
      H2Se: 'This cannot be undone',
    },
    header: {
      backList: 'Back to list',
      list: 'List',
      details: 'Details',
      add: 'Add new',
      form: 'Form',
    },
    banner: {
      lang: 'Language',
      disconnect: 'logout',
    },
    saying_words: {
      entries: 'new entries',
      details: 'details',
      information: 'information',
      and: 'and',
      needs: "what you don't need",
    },
    navbar: {
      core: 'Core',
      entities: 'ENTITIES',
      views: 'VIEWS',
      settings: 'Settings',
      home: 'Home',
      customisation: 'LAYOUT CUSTOMISATION',
    },

    authentication: {
      login: 'Login',
      logout: 'Logout',
      register: 'Register',
      profile: 'Profile',
      username: 'Username',
      email: 'Email',
      password: 'Password',
      confirmPassword: 'Confirm your @:common.password',
      passwordPlaceholder: '••••••••',

      title: 'Sign in',
      submit: 'Sign in',
      invalid: 'Invalid credentials',
      createAccount: 'Create account',
      backToSignIn: 'Back to sign in',
      dontHaveAccount: "You don't have an account?",
    },

    register: {
      title: 'Create account',
      name: 'Name',
      email: 'Email',
      password: 'Password',
      submit: 'Register',
      backToSignIn: 'Back to sign in',
      success: 'Account created successfully',
      error: 'Registration failed',
      haveAccount: 'You already have an account?',
    },
    home: {
      greet: 'Hello',
      greetDeveloper: '@.capitalize:home.greet Developer',
      welcome: 'Welcome on @.capitalize:projectName frontend app',
      simple_welcome: 'Welcome',
      titles: {
        entities: 'List of @:common.entities',
        views: 'List of @:common.views',
      },
    },

    pagination: {
      sizeLabel: 'Showing:',
      previous: 'previous @:common.page',
      next: 'next @:common.page',
      goToLabel: 'Go to ',
      totalRows: ' of {total} @:common.rows',
      pageOf: 'Page {page} of {total}',
    },

    search: {
      simple_filter: 'Filters',
      selection: 'select a field',
      filters: ' No @:common.filter | filter | filter ({count})',
    },

    entity: {
      nav: '{entity} / ',
      list: {
        nav: 'List',
        filters: 'Filters',
        addFilter: 'Add filter',
        removeFilter: 'Remove {{label}} filter',
      },
      create: {
        nav: 'New',
        submitLabel: 'Create {entity}',
      },
      update: {
        nav: 'Update',
        submitLabel: 'Update {entity}',
      },
      details: {
        nav: 'Details',
      },
    },

    button: {
      addEntity: 'Add @:common.new {entity}',
      applySearch: 'Apply',
      backTo: 'Back to {name}',
      backToList: 'Back to @:common.list',
      backToListDescription: 'Return to the @:common.list @:common.view',
      go: 'Go',
      delete: 'Delete',
      update: 'Update',
      create: 'Create',
      cancel: 'Cancel',
      validate: 'Submit',
      export: 'Export to CSV',
      back: 'Retrun',
    },

    settings: {
      layout: 'Layout',
      theme: 'Theme',
      general: 'General',
      language: 'Language',
      pageTitle: 'Settings',
      switchLayout: 'Switch Layout',
    },

    // Entity definitions (scalable for many entities)
    entities: {
      album: {
        name: 'album | albums',
        nav: {
          title: '@.capitalize:entities.album.name',
          create: 'Create @.capitalize:entities.album.name',
          list: '@.capitalize:entities.album.name @:common.list',
        },
      },
      artistGroupMember: {
        name: 'artistGroupMember | artistGroupMembers',
        nav: {
          title: '@.capitalize:entities.artistGroupMember.name',
          create: 'Create @.capitalize:entities.artistGroupMember.name',
          list: '@.capitalize:entities.artistGroupMember.name @:common.list',
        },
      },
      artistType: {
        name: 'artistType | artistTypes',
        nav: {
          title: '@.capitalize:entities.artistType.name',
          create: 'Create @.capitalize:entities.artistType.name',
          list: '@.capitalize:entities.artistType.name @:common.list',
        },
      },
      artist: {
        name: 'artist | artists',
        nav: {
          title: '@.capitalize:entities.artist.name',
          create: 'Create @.capitalize:entities.artist.name',
          list: '@.capitalize:entities.artist.name @:common.list',
        },
      },
      audioStorageType: {
        name: 'audioStorageType | audioStorageTypes',
        nav: {
          title: '@.capitalize:entities.audioStorageType.name',
          create: 'Create @.capitalize:entities.audioStorageType.name',
          list: '@.capitalize:entities.audioStorageType.name @:common.list',
        },
      },
      client: {
        name: 'client | clients',
        nav: {
          title: '@.capitalize:entities.client.name',
          create: 'Create @.capitalize:entities.client.name',
          list: '@.capitalize:entities.client.name @:common.list',
        },
      },
      comment: {
        name: 'comment | comments',
        nav: {
          title: '@.capitalize:entities.comment.name',
          create: 'Create @.capitalize:entities.comment.name',
          list: '@.capitalize:entities.comment.name @:common.list',
        },
      },
      concert: {
        name: 'concert | concerts',
        nav: {
          title: '@.capitalize:entities.concert.name',
          create: 'Create @.capitalize:entities.concert.name',
          list: '@.capitalize:entities.concert.name @:common.list',
        },
      },
      contentSubmission: {
        name: 'contentSubmission | contentSubmissions',
        nav: {
          title: '@.capitalize:entities.contentSubmission.name',
          create: 'Create @.capitalize:entities.contentSubmission.name',
          list: '@.capitalize:entities.contentSubmission.name @:common.list',
        },
      },
      download: {
        name: 'download | downloads',
        nav: {
          title: '@.capitalize:entities.download.name',
          create: 'Create @.capitalize:entities.download.name',
          list: '@.capitalize:entities.download.name @:common.list',
        },
      },
      editorialPlaylistSong: {
        name: 'editorialPlaylistSong | editorialPlaylistSongs',
        nav: {
          title: '@.capitalize:entities.editorialPlaylistSong.name',
          create: 'Create @.capitalize:entities.editorialPlaylistSong.name',
          list: '@.capitalize:entities.editorialPlaylistSong.name @:common.list',
        },
      },
      editorialPlaylist: {
        name: 'editorialPlaylist | editorialPlaylists',
        nav: {
          title: '@.capitalize:entities.editorialPlaylist.name',
          create: 'Create @.capitalize:entities.editorialPlaylist.name',
          list: '@.capitalize:entities.editorialPlaylist.name @:common.list',
        },
      },
      eventMedia: {
        name: 'eventMedia | eventMedias',
        nav: {
          title: '@.capitalize:entities.eventMedia.name',
          create: 'Create @.capitalize:entities.eventMedia.name',
          list: '@.capitalize:entities.eventMedia.name @:common.list',
        },
      },
      eventModerationStatuse: {
        name: 'eventModerationStatuse | eventModerationStatuses',
        nav: {
          title: '@.capitalize:entities.eventModerationStatuse.name',
          create: 'Create @.capitalize:entities.eventModerationStatuse.name',
          list: '@.capitalize:entities.eventModerationStatuse.name @:common.list',
        },
      },
      event: {
        name: 'event | events',
        nav: {
          title: '@.capitalize:entities.event.name',
          create: 'Create @.capitalize:entities.event.name',
          list: '@.capitalize:entities.event.name @:common.list',
        },
      },
      follow: {
        name: 'follow | follows',
        nav: {
          title: '@.capitalize:entities.follow.name',
          create: 'Create @.capitalize:entities.follow.name',
          list: '@.capitalize:entities.follow.name @:common.list',
        },
      },
      genre: {
        name: 'genre | genres',
        nav: {
          title: '@.capitalize:entities.genre.name',
          create: 'Create @.capitalize:entities.genre.name',
          list: '@.capitalize:entities.genre.name @:common.list',
        },
      },
      instrumentRole: {
        name: 'instrumentRole | instrumentRoles',
        nav: {
          title: '@.capitalize:entities.instrumentRole.name',
          create: 'Create @.capitalize:entities.instrumentRole.name',
          list: '@.capitalize:entities.instrumentRole.name @:common.list',
        },
      },
      interactionTarget: {
        name: 'interactionTarget | interactionTargets',
        nav: {
          title: '@.capitalize:entities.interactionTarget.name',
          create: 'Create @.capitalize:entities.interactionTarget.name',
          list: '@.capitalize:entities.interactionTarget.name @:common.list',
        },
      },
      like: {
        name: 'like | likes',
        nav: {
          title: '@.capitalize:entities.like.name',
          create: 'Create @.capitalize:entities.like.name',
          list: '@.capitalize:entities.like.name @:common.list',
        },
      },
      listeningHistory: {
        name: 'listeningHistory | listeningHistorys',
        nav: {
          title: '@.capitalize:entities.listeningHistory.name',
          create: 'Create @.capitalize:entities.listeningHistory.name',
          list: '@.capitalize:entities.listeningHistory.name @:common.list',
        },
      },
      mediaType: {
        name: 'mediaType | mediaTypes',
        nav: {
          title: '@.capitalize:entities.mediaType.name',
          create: 'Create @.capitalize:entities.mediaType.name',
          list: '@.capitalize:entities.mediaType.name @:common.list',
        },
      },
      memberStatuse: {
        name: 'memberStatuse | memberStatuses',
        nav: {
          title: '@.capitalize:entities.memberStatuse.name',
          create: 'Create @.capitalize:entities.memberStatuse.name',
          list: '@.capitalize:entities.memberStatuse.name @:common.list',
        },
      },
      notificationPreference: {
        name: 'notificationPreference | notificationPreferences',
        nav: {
          title: '@.capitalize:entities.notificationPreference.name',
          create: 'Create @.capitalize:entities.notificationPreference.name',
          list: '@.capitalize:entities.notificationPreference.name @:common.list',
        },
      },
      notificationType: {
        name: 'notificationType | notificationTypes',
        nav: {
          title: '@.capitalize:entities.notificationType.name',
          create: 'Create @.capitalize:entities.notificationType.name',
          list: '@.capitalize:entities.notificationType.name @:common.list',
        },
      },
      notification: {
        name: 'notification | notifications',
        nav: {
          title: '@.capitalize:entities.notification.name',
          create: 'Create @.capitalize:entities.notification.name',
          list: '@.capitalize:entities.notification.name @:common.list',
        },
      },
      participationStatuse: {
        name: 'participationStatuse | participationStatuses',
        nav: {
          title: '@.capitalize:entities.participationStatuse.name',
          create: 'Create @.capitalize:entities.participationStatuse.name',
          list: '@.capitalize:entities.participationStatuse.name @:common.list',
        },
      },
      playMode: {
        name: 'playMode | playModes',
        nav: {
          title: '@.capitalize:entities.playMode.name',
          create: 'Create @.capitalize:entities.playMode.name',
          list: '@.capitalize:entities.playMode.name @:common.list',
        },
      },
      playlistSong: {
        name: 'playlistSong | playlistSongs',
        nav: {
          title: '@.capitalize:entities.playlistSong.name',
          create: 'Create @.capitalize:entities.playlistSong.name',
          list: '@.capitalize:entities.playlistSong.name @:common.list',
        },
      },
      playlistVisibilitie: {
        name: 'playlistVisibilitie | playlistVisibilities',
        nav: {
          title: '@.capitalize:entities.playlistVisibilitie.name',
          create: 'Create @.capitalize:entities.playlistVisibilitie.name',
          list: '@.capitalize:entities.playlistVisibilitie.name @:common.list',
        },
      },
      playlist: {
        name: 'playlist | playlists',
        nav: {
          title: '@.capitalize:entities.playlist.name',
          create: 'Create @.capitalize:entities.playlist.name',
          list: '@.capitalize:entities.playlist.name @:common.list',
        },
      },
      reportStatuse: {
        name: 'reportStatuse | reportStatuses',
        nav: {
          title: '@.capitalize:entities.reportStatuse.name',
          create: 'Create @.capitalize:entities.reportStatuse.name',
          list: '@.capitalize:entities.reportStatuse.name @:common.list',
        },
      },
      report: {
        name: 'report | reports',
        nav: {
          title: '@.capitalize:entities.report.name',
          create: 'Create @.capitalize:entities.report.name',
          list: '@.capitalize:entities.report.name @:common.list',
        },
      },
      searchHistory: {
        name: 'searchHistory | searchHistorys',
        nav: {
          title: '@.capitalize:entities.searchHistory.name',
          create: 'Create @.capitalize:entities.searchHistory.name',
          list: '@.capitalize:entities.searchHistory.name @:common.list',
        },
      },
      songGenre: {
        name: 'songGenre | songGenres',
        nav: {
          title: '@.capitalize:entities.songGenre.name',
          create: 'Create @.capitalize:entities.songGenre.name',
          list: '@.capitalize:entities.songGenre.name @:common.list',
        },
      },
      song: {
        name: 'song | songs',
        nav: {
          title: '@.capitalize:entities.song.name',
          create: 'Create @.capitalize:entities.song.name',
          list: '@.capitalize:entities.song.name @:common.list',
        },
      },
      submissionStatuse: {
        name: 'submissionStatuse | submissionStatuses',
        nav: {
          title: '@.capitalize:entities.submissionStatuse.name',
          create: 'Create @.capitalize:entities.submissionStatuse.name',
          list: '@.capitalize:entities.submissionStatuse.name @:common.list',
        },
      },
      upNextQueue: {
        name: 'upNextQueue | upNextQueues',
        nav: {
          title: '@.capitalize:entities.upNextQueue.name',
          create: 'Create @.capitalize:entities.upNextQueue.name',
          list: '@.capitalize:entities.upNextQueue.name @:common.list',
        },
      },
      userRole: {
        name: 'userRole | userRoles',
        nav: {
          title: '@.capitalize:entities.userRole.name',
          create: 'Create @.capitalize:entities.userRole.name',
          list: '@.capitalize:entities.userRole.name @:common.list',
        },
      },
      userStatusHistory: {
        name: 'userStatusHistory | userStatusHistorys',
        nav: {
          title: '@.capitalize:entities.userStatusHistory.name',
          create: 'Create @.capitalize:entities.userStatusHistory.name',
          list: '@.capitalize:entities.userStatusHistory.name @:common.list',
        },
      },
      userStatuse: {
        name: 'userStatuse | userStatuses',
        nav: {
          title: '@.capitalize:entities.userStatuse.name',
          create: 'Create @.capitalize:entities.userStatuse.name',
          list: '@.capitalize:entities.userStatuse.name @:common.list',
        },
      },
      user: {
        name: 'user | users',
        nav: {
          title: '@.capitalize:entities.user.name',
          create: 'Create @.capitalize:entities.user.name',
          list: '@.capitalize:entities.user.name @:common.list',
        },
      },
      venue: {
        name: 'venue | venues',
        nav: {
          title: '@.capitalize:entities.venue.name',
          create: 'Create @.capitalize:entities.venue.name',
          list: '@.capitalize:entities.venue.name @:common.list',
        },
      },
      verificationChannel: {
        name: 'verificationChannel | verificationChannels',
        nav: {
          title: '@.capitalize:entities.verificationChannel.name',
          create: 'Create @.capitalize:entities.verificationChannel.name',
          list: '@.capitalize:entities.verificationChannel.name @:common.list',
        },
      },
      verificationCode: {
        name: 'verificationCode | verificationCodes',
        nav: {
          title: '@.capitalize:entities.verificationCode.name',
          create: 'Create @.capitalize:entities.verificationCode.name',
          list: '@.capitalize:entities.verificationCode.name @:common.list',
        },
      },
      verificationStatuse: {
        name: 'verificationStatuse | verificationStatuses',
        nav: {
          title: '@.capitalize:entities.verificationStatuse.name',
          create: 'Create @.capitalize:entities.verificationStatuse.name',
          list: '@.capitalize:entities.verificationStatuse.name @:common.list',
        },
      },
    },
    views: {},
    error: {
      error: 'Error',
      400: 'Bad request',
      403: 'Access denided',
      404: {
        title: 'Page not found',
        description: "Sorry, the page you are searching doesn't exist or moved to another url",
      },
      500: 'Error 500',
    },
    layout: {
      switchToHorizontal: 'Switch to horizontal',
      switchToVertical: 'Switch to vertical',
    },
    theme: {
      switchToLight: 'Light mode',
      switchToDark: 'Dark mode',
    },
    language: {
      choose: 'Language',
      en: 'English',
      fr: 'French',
      es: 'Spanish',
      de: 'German',
      it: 'Italian',
      pt: 'Portuguese',
      'pt-BR': 'Portuguese (Brazil)',
      zh: 'Chinese',
      ja: 'Japanese',
    },
    state: {
      loading: 'Loading…',
      notFound: 'Not found',
      success: 'Success',
      creating: 'Creating...',
      updating: 'Updating...',
    },
    confirmation: {
      delete: 'Confirm deletion?',
    },
    navigation: {
      list: 'List',
      add: 'Add',
    },
    profile: {
      myProfile: 'My Profile',
      edit: 'Edit Profile',
      changePassword: 'Change Password',
      oldPassword: 'Current Password',
      newPassword: 'New Password',
      confirmPassword: 'Confirm Password',
      updateSuccess: 'Profile updated successfully',
      updateError: 'Failed to update profile',
      usernameChangedLogout:
        'Username changed. You will be logged out to sign in with your new credentials.',
      usernameChangeWarning:
        '⚠️ Warning: Changing your username will log you out. You will need to sign in with your new credentials.',
      passwordChangeSuccess: 'Password changed successfully',
      passwordChangeError: 'Failed to change password',
      passwordMismatch: 'Passwords do not match',
    },
  },

  dateTimeFormats: {
    short: { year: 'numeric', month: 'short', day: 'numeric' },
    long: { year: 'numeric', month: 'long', day: 'numeric', weekday: 'long' },
    time: { hour: '2-digit', minute: '2-digit' },
    full: {
      year: 'numeric',
      month: 'long',
      day: 'numeric',
      weekday: 'long',
      hour: '2-digit',
      minute: '2-digit',
    },
  },
  numberFormats: {
    currency: {
      style: 'currency',
      currency: 'USD',
      currencyDisplay: 'symbol',
    },
    decimal: {
      style: 'decimal',
      minimumFractionDigits: 2,
      maximumFractionDigits: 2,
    },
    percent: {
      style: 'percent',
      useGrouping: true,
    },
  },
}
export default english
