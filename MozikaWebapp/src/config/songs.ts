// ─────────────────────────────────────────────────────────────
//  Données des chansons, partagées entre la page d'accueil et la
//  page « Lecteur » (détail).
//  ⚠️ Ce sont des données d'EXEMPLE : remplace-les par tes vraies
//  données (API / base de données) quand elles seront disponibles.
// ─────────────────────────────────────────────────────────────

export interface SongCredit {
  label: string
  value: string
}

export interface Song {
  id: number
  title: string
  artist: string
  artistId: number
  album: string
  type: 'Groupe' | 'Solo' | 'Événement'
  isCertified: boolean
  /** Date de sortie affichée sur les cartes « Nouveauté » */
  date?: string
  /** Année affichée dans la liste « Recommandé pour vous » */
  year?: string
  coverBg: string
  thumbBg: string
  /** ID de la vidéo YouTube (la partie après "v=" dans l'URL) */
  youtubeId?: string
  /** ID YouTube de la version karaoké */
  karaokeYoutubeId?: string
  /** Lien vers le fichier audio (mp3, etc.) */
  audioUrl?: string
  /** Lien vers le playback / instrumental */
  playbackUrl?: string
  /** Partition solfa (texte). Si vide, le bouton n'apparaît pas. */
  solfa?: string
  lyrics: string[]
  credits: SongCredit[]
}

const sampleLyrics = [
  '(Introduction instrumentale)',
  "C'est mon premier hit",
  'Sur la plateforme Kaloy',
  'La musique nous unit',
]

const sampleCredits: SongCredit[] = [
  { label: 'Auteur/Compositeur', value: 'Vetsonkira' },
  { label: 'Arrangeur', value: 'Studio Kaloy Production' },
]

const sampleSolfa = ['| d  : r  : m  | f  : s  : -  |', '| l  : s  : f  | m  : r  : d  |'].join(
  '\n',
)

// ── NOUVEAUTÉ ──
export const newReleases: Song[] = [
  {
    id: 1,
    title: 'Somaroho',
    artist: 'Wawa',
    artistId: 1,
    album: 'Premiers Pas',
    type: 'Groupe',
    isCertified: true,
    date: '01 Août 2026',
    coverBg: 'linear-gradient(135deg, #FDEBD0 0%, #F5CBA7 100%)',
    thumbBg: '#FDEBD0',
    solfa: sampleSolfa,
    lyrics: sampleLyrics,
    credits: sampleCredits,
  },
  {
    id: 2,
    title: 'Somaroho Live',
    artist: 'Festival Somaroho',
    artistId: 2,
    album: 'Somaroho — En concert',
    type: 'Événement',
    isCertified: true,
    date: '01 Août 2026',
    coverBg: 'linear-gradient(135deg, #FCF3CF 0%, #F9E79F 100%)',
    thumbBg: '#FCF3CF',
    lyrics: sampleLyrics,
    credits: sampleCredits,
  },
  {
    id: 3,
    title: 'Mora Mora',
    artist: 'Eusébia',
    artistId: 3,
    album: 'Single',
    type: 'Solo',
    isCertified: true,
    date: '15 Juillet 2026',
    coverBg: 'linear-gradient(135deg, #E8F8F5 0%, #D1F2EB 100%)',
    thumbBg: '#E8F8F5',
    lyrics: sampleLyrics,
    credits: sampleCredits,
  },
  {
    id: 4,
    title: 'Tsapiky Moderne',
    artist: 'Damily',
    artistId: 4,
    album: 'Single',
    type: 'Groupe',
    isCertified: false,
    date: '10 Juin 2026',
    coverBg: 'linear-gradient(135deg, #EAECEE 0%, #D5D8DC 100%)',
    thumbBg: '#EAECEE',
    lyrics: sampleLyrics,
    credits: sampleCredits,
  },
]

// ── RECOMMANDÉ POUR VOUS ──
export const recommendedTracks: Song[] = [
  {
    id: 101,
    title: 'T-Kalo groupe Gasy',
    artist: 'T-Kalo groupe Gasy',
    artistId: 10,
    album: 'Premiers Pas',
    type: 'Groupe',
    isCertified: true,
    year: '2020',
    coverBg: 'linear-gradient(135deg, #FFF3E0 0%, #FDEBD0 100%)',
    thumbBg: '#FFF3E0',
    solfa: sampleSolfa,
    lyrics: sampleLyrics,
    credits: sampleCredits,
  },
  {
    id: 102,
    title: 'T-Kalo groupe Gasy — Feo Gasy',
    artist: 'T-Kalo groupe Gasy',
    artistId: 10,
    album: 'Feo Gasy',
    type: 'Groupe',
    isCertified: true,
    year: '2020',
    coverBg: 'linear-gradient(135deg, #FDF2E9 0%, #FAE5D3 100%)',
    thumbBg: '#FDF2E9',
    lyrics: sampleLyrics,
    credits: sampleCredits,
  },
  {
    id: 103,
    title: 'T-Kalo groupe Gasy — Hira Faneva',
    artist: 'T-Kalo groupe Gasy',
    artistId: 10,
    album: 'Hira Faneva',
    type: 'Groupe',
    isCertified: true,
    year: '2020',
    coverBg: 'linear-gradient(135deg, #FEF9E7 0%, #FCF3CF 100%)',
    thumbBg: '#FEF9E7',
    lyrics: sampleLyrics,
    credits: sampleCredits,
  },
  {
    id: 104,
    title: 'T-Kalo groupe Gasy — Mahatsangy',
    artist: 'T-Kalo groupe Gasy',
    artistId: 10,
    album: 'Mahatsangy',
    type: 'Groupe',
    isCertified: true,
    year: '2020',
    coverBg: 'linear-gradient(135deg, #F9EBEA 0%, #F2D7D5 100%)',
    thumbBg: '#F9EBEA',
    lyrics: sampleLyrics,
    credits: sampleCredits,
  },
  {
    id: 105,
    title: 'Salegy Électro',
    artist: 'Artiste Test Kaloy',
    artistId: 11,
    album: 'Single',
    type: 'Solo',
    isCertified: false,
    year: '2022',
    coverBg: 'linear-gradient(135deg, #EAF2F8 0%, #D6EAF8 100%)',
    thumbBg: '#EAF2F8',
    lyrics: sampleLyrics,
    credits: sampleCredits,
  },
]

export const allSongs: Song[] = [...newReleases, ...recommendedTracks]

export function getSongById(id: number): Song | undefined {
  return allSongs.find((song) => song.id === id)
}
