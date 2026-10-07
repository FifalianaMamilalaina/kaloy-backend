const navigations = [
  // 6 MODULES OFFICIELS DU PROJET MUSICAL (KALOY)
  {
    icon: 'fas fa-shield-alt',
    sectionName: 'Comptes & Auth',
    navChilds: [
      {
        navTitle: 'Mon compte',
        navLink: '/account',
      },
      {
        navTitle: 'Se connecter',
        navLink: '/login',
      },
      {
        navTitle: 'Créer un compte',
        navLink: '/register',
      },
    ],
  },
  {
    icon: 'fas fa-users',
    sectionName: 'Profil Artiste',
    navChilds: [
      {
        navTitle: 'Membres du groupe',
        navLink: '/artistGroupMembers',
      },
      {
        navTitle: 'Ajouter un membre',
        navLink: '/artistGroupMembers/create',
      },
      {
        navTitle: 'Fiches artistes',
        navLink: '/artists',
      },
    ],
  },
  {
    icon: 'fas fa-user-plus',
    sectionName: 'Social (Follow)',
    navChilds: [
      {
        navTitle: 'Mes abonnements',
        navLink: '/follows',
      },
      {
        navTitle: 'Suivre un artiste',
        navLink: '/follows/create',
      },
    ],
  },
  {
    icon: 'fas fa-bell',
    sectionName: 'Notifications',
    navChilds: [
      {
        navTitle: 'Centre de notifications',
        navLink: '/notifications',
      },
      {
        navTitle: 'Préférences',
        navLink: '/notificationPreferences',
      },
    ],
  },
  {
    icon: 'fas fa-cloud-upload-alt',
    sectionName: 'Pipeline Ingestion',
    navChilds: [
      {
        navTitle: 'Déposer une archive (.zip)',
        navLink: '/contentSubmissions/create',
      },
      {
        navTitle: 'Suivi des soumissions',
        navLink: '/contentSubmissions',
      },
    ],
  },
  {
    icon: 'fas fa-user-shield',
    sectionName: 'Administration',
    navChilds: [
      {
        navTitle: 'Validation artistes',
        navLink: '/artists',
      },
      {
        navTitle: 'Supervision pipeline',
        navLink: '/contentSubmissions',
      },
    ],
  },
]

export default navigations
