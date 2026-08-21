const french = {
  messages: {
    projectName: 'Mozika',
    common: {
      update: 'modifier',
      delete: 'supprimer',
      create: 'créer',
      apply: 'appliquer',
      go: 'aller',

      views: 'vues',
      view: 'vue',
      details: 'détails',
      list: 'liste',
      new: 'nouveau',
      actions: 'actions',
      back: 'retour',
      layout: 'mise en page',
      theme: 'thème',
      general: 'général',
      settings: 'paramètres',
      entities: 'entités',
      entity: 'entité',
      filter: 'filtre',
      rows: 'lignes',
      showing: 'affichage',
      previous: 'précédent',
      next: 'suivant',
      page: 'page',
      add: 'ajouter',
      description: 'description',
      return: 'retourner',
      home: 'accueil',
      login: 'connexion',
      logout: 'déconnexion',
      register: 'inscription',
      profile: 'profil',
      username: "nom d'utilisateur",
      email: 'email',
      password: 'mot de passe',
      language: 'langue',
      confirm: 'confirmer',
      cancel: 'annuler',
      no: 'aucun',
      read: 'lire',
      edit: 'Éditer',
      save: 'Sauvegarder',
    },
    box_confirm: {
      H1Se: 'Voulez-vous effacer cet élément définitivement?',
      H2Se: 'Pas de retour en arrière possible',
    },
    header: {
      backList: 'Revenir à la liste',
      list: 'Liste',
      details: 'Détails',
      add: "Formulaire d'ajout de",
      form: 'Formulaire',
    },
    banner: {
      lang: 'Langue',
      disconnect: 'se déconnecter',
    },
    saying_words: {
      entries: 'de nouvelles entrées',
      details: 'les détails',
      information: 'les informations',
      and: 'et',
      needs: "ce dont vous n'avez pas besoin",
    },

    navbar: {
      core: 'Noyau',
      entities: 'ENTITES',
      views: 'VUES',
      settings: 'Parametres',
      home: 'Accueil',
      customisation: 'Personnalisation de layout',
    },

    authentication: {
      title: 'Se connecter',
      login: 'Connexion',
      logout: 'Se Deconnecter',
      register: 'Inscription',
      profile: 'Profile',
      username: 'Nom d’utilisateur',
      email: 'Email',
      password: 'Mot de passe',
      confirmPassword: 'Confirmer votre @:common.password',
      submit: 'Se connecter',
      invalid: 'Identifiants invalides',
      createAccount: 'Créer un compte',
      backToSignIn: 'Retour à la connexion',
      dontHaveAccount: "Vous n'avez pas de compte?",
      passwordPlaceholder: '••••••••',
    },

    register: {
      title: 'Créer un compte',
      name: 'Nom',
      email: 'Email',
      password: 'Mot de passe',
      submit: "S'inscrire",
      backToSignIn: 'Retour à la connexion',
      success: 'Compte créé avec succès',
      error: "Échec de l'inscription",
      haveAccount: 'Vous avez déjà un compte?',
    },

    home: {
      greet: 'Bonjour',
      greetDeveloper: '@.capitalize:home.greet Développeur',
      welcome: 'Bienvenue sur l’application frontend @.capitalize:projectName',
      titles: {
        entities: 'List des @:common.entities',
        views: 'List des @:common.views',
      },
      simple_welcome: 'Bienvenue',
      add: 'Ajouter',
      save: 'Enregistrer',
      cancel: 'Annuler',
      edit: 'Éditer',
    },

    pagination: {
      sizeLabel: 'Affichage:',
      previous: 'page @:common.previous',
      next: 'page @:common.next',
      goToLabel: 'Aller à la page',
      totalRows: ' sur {total} @:common.rows',
      pageOf: 'Page {page} sur {total}',
    },

    search: {
      filters: '@.capitalize:common.no @:common.filter | filter | filter ({count})',
      simple_filter: 'Filtres',
      selection: 'Choisir un champ',
    },

    entity: {
      nav: '{entity} / ',
      list: {
        nav: 'List',
        filters: 'Filtres',
        addFilter: 'Ajouter un filtre',
        removeFilter: 'Supprimer le filtre {{label}}',
      },
      create: {
        nav: 'Nouveau',
        submitLabel: 'Cree {entity}',
      },
      update: {
        nav: 'Modifier',
        submitLabel: 'Modifier {entity}',
      },
      details: {
        nav: 'Details',
      },
    },

    button: {
      addEntity: 'Cree un(e) @:common.new(elle) {entity}',
      applySearch: 'Appliquer',
      go: 'Aller',
      validate: 'Valider',
      delete: 'Supprimer',
      update: 'Modifier',
      create: 'Cree',
      cancel: 'Annuler',
      backTo: 'Retrour vers {name}',
      backToList: 'Retour à la @:common.list',
      backToListDescription: 'Retour à la @:common.view @:common.list',
      export: 'Exporter en CSV',
      back: 'Retour',
    },

    settings: {
      layout: 'Mise en page',
      theme: 'Them',
      general: 'General',
      language: 'Langue',
      pageTitle: 'Parametres',
      switchLayout: 'Changer de mise en page',
    },

    // Définitions d’entités (scalable)
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
      error: 'Erreur',
      400: 'Requête invalide',
      403: 'Accès interdit',
      404: {
        title: 'Page introuvable',
        description: "Désolé, la page que vous recherchez n'existe pas ou a été déplacée.",
      },
      500: 'Erreur interne (500)',
    },
    layout: {
      switchToHorizontal: "Passer à l'horizontal",
      switchToVertical: 'Passer à la vertical',
    },
    theme: {
      switchToLight: 'Mode clair',
      switchToDark: 'Mode sombre',
    },
    language: {
      choose: 'Langue',
      en: 'Anglais',
      fr: 'Français',
      es: 'Espagnol',
      de: 'Allemand',
      it: 'Italien',
      pt: 'Portugais',
      'pt-BR': 'Portugais (Brésil)',
      zh: 'Chinois',
      ja: 'Japonais',
    },
    state: {
      loading: 'Chargement…',
      notFound: 'Introuvable',
      success: 'Succès',
      creating: 'Création en cours…',
      updating: 'Mise à jour en cours…',
    },
    confirmation: {
      delete: 'Confirmer la suppression ?',
    },
    navigation: {
      list: 'Liste',
      add: 'Ajouter',
    },
    profile: {
      myProfile: 'Mon profil',
      edit: 'Modifier mon profil',
      changePassword: 'Changer mot de passe',
      oldPassword: 'Mot de passe actuel',
      newPassword: 'Nouveau mot de passe',
      confirmPassword: 'Confirmer mot de passe',
      updateSuccess: 'Profil mis à jour avec succès',
      updateError: 'Échec de la mise à jour',
      usernameChangedLogout:
        "Nom d'utilisateur modifié. Vous allez être déconnecté pour vous reconnecter avec vos nouveaux identifiants.",
      usernameChangeWarning:
        "⚠️ Attention : Changer votre nom d'utilisateur vous déconnectera. Vous devrez vous reconnecter avec vos nouveaux identifiants.",
      passwordChangeSuccess: 'Mot de passe modifié avec succès',
      passwordChangeError: 'Échec du changement de mot de passe',
      passwordMismatch: 'Les mots de passe ne correspondent pas',
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
      currency: 'EUR',
      currencyDisplay: 'symbol', // 1 234,56 €
    },
    decimal: {
      style: 'decimal',
      minimumFractionDigits: 2,
      maximumFractionDigits: 2,
    },
    percent: {
      style: 'percent',
      useGrouping: true, // 88 %
    },
  },
}
export default french
