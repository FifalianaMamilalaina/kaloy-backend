<template>
  <div class="home-page-wrapper" :style="{ backgroundImage: `url(${heroImg})` }">
    <div class="home-backdrop"></div>

    <div class="home-page-container">
      <!-- Header: Search Section -->
      <header class="search-header-section">
        <div class="header-top">
          <h1 class="page-main-title">Rechercher</h1>
          <div class="header-meta">
            <span v-if="authStore.isAuthenticated" class="welcome-tag">
              Connecté : <strong>{{ userRoleName }}</strong>
            </span>
            <span v-else class="welcome-tag guest"> Mode Visiteur </span>
          </div>
        </div>

        <!-- Search Input Bar -->
        <div class="search-bar-wrapper">
          <span class="search-icon" aria-hidden="true">
            <svg
              width="22"
              height="22"
              viewBox="0 0 24 24"
              fill="none"
              stroke="currentColor"
              stroke-width="2"
              stroke-linecap="round"
              stroke-linejoin="round"
            >
              <circle cx="11" cy="11" r="8"></circle>
              <line x1="21" y1="21" x2="16.65" y2="16.65"></line>
            </svg>
          </span>
          <input
            type="text"
            v-model="searchQuery"
            placeholder="Rechercher"
            class="main-search-input"
          />
          <button
            v-if="searchQuery"
            @click="searchQuery = ''"
            class="clear-search-btn"
            aria-label="Effacer"
          >
            <svg
              width="18"
              height="18"
              viewBox="0 0 24 24"
              fill="none"
              stroke="currentColor"
              stroke-width="2"
            >
              <line x1="18" y1="6" x2="6" y2="18"></line>
              <line x1="6" y1="6" x2="18" y2="18"></line>
            </svg>
          </button>
        </div>

        <!-- Filter Tags -->
        <div class="filter-chips">
          <button
            v-for="filter in filters"
            :key="filter"
            :class="['filter-chip', { active: activeFilter === filter }]"
            @click="activeFilter = filter"
          >
            {{ filter }}
          </button>
        </div>
      </header>

      <!-- SECTION 1 : Nouveauté -->
      <section class="music-section">
        <div class="section-heading">
          <h2 class="section-title">Nouveauté</h2>
          <a href="#" @click.prevent="showAllNew = !showAllNew" class="see-all-link">
            {{ showAllNew ? 'réduire' : 'voir tout' }}
          </a>
        </div>

        <div class="cards-grid">
          <!-- Un clic sur la carte ouvre la page détail -->
          <div
            v-for="item in displayedNewReleases"
            :key="item.id"
            class="music-card"
            role="link"
            tabindex="0"
            :aria-label="`Ouvrir ${item.title}`"
            @click="goToSong(item.id)"
            @keydown.enter="goToSong(item.id)"
          >
            <div class="card-cover-wrapper" :style="{ background: item.coverBg }">
              <div class="cover-placeholder">
                <svg
                  width="42"
                  height="42"
                  viewBox="0 0 24 24"
                  fill="none"
                  stroke="currentColor"
                  stroke-width="1.6"
                >
                  <path d="M9 18V5l12-2v13"></path>
                  <circle cx="6" cy="18" r="3"></circle>
                  <circle cx="18" cy="16" r="3"></circle>
                </svg>
              </div>
              <!-- .stop : le bouton lecture ne déclenche pas l'ouverture de la page -->
              <button
                class="play-overlay-btn"
                :aria-label="`Écouter ${item.title}`"
                @click.stop="playTrack(item)"
              >
                <svg width="20" height="20" viewBox="0 0 24 24" fill="currentColor">
                  <polygon points="5 3 19 12 5 21 5 3"></polygon>
                </svg>
              </button>
            </div>

            <div class="card-details">
              <div class="card-title-row">
                <h3 class="card-title">{{ item.title }}</h3>
                <span
                  v-if="item.isCertified"
                  class="certified-badge"
                  title="Artiste certifié (Module S5)"
                >
                  <svg width="14" height="14" viewBox="0 0 24 24" fill="currentColor">
                    <path
                      d="M12 2l3.09 6.26L22 9.27l-5 4.87 1.18 6.88L12 17.77l-6.18 3.25L7 14.14 2 9.27l6.91-1.01L12 2z"
                    ></path>
                  </svg>
                </span>
              </div>
              <p class="card-date">{{ item.date }}</p>
              <p class="card-artist">{{ item.artist }} • {{ item.type }}</p>

              <button
                :class="['btn-follow-compact', { following: isFollowing(item.artistId) }]"
                @click.stop="toggleFollow(item.artistId)"
              >
                {{ isFollowing(item.artistId) ? 'Abonné' : '+ Suivre' }}
              </button>
            </div>
          </div>
        </div>
      </section>

      <!-- SECTION 2 : Recommandé pour vous -->
      <section class="music-section">
        <div class="section-heading">
          <h2 class="section-title">Recommandé pour vous</h2>
        </div>

        <div class="recommended-list">
          <div
            v-for="track in filteredRecommended"
            :key="track.id"
            class="recommended-item"
            role="link"
            tabindex="0"
            :aria-label="`Ouvrir ${track.title}`"
            @click="goToSong(track.id)"
            @keydown.enter="goToSong(track.id)"
          >
            <div class="item-thumb" :style="{ background: track.thumbBg }">
              <svg
                width="24"
                height="24"
                viewBox="0 0 24 24"
                fill="none"
                stroke="currentColor"
                stroke-width="1.8"
              >
                <rect x="3" y="3" width="18" height="18" rx="4"></rect>
                <circle cx="12" cy="12" r="3"></circle>
              </svg>
            </div>

            <div class="item-info">
              <div class="item-title-row">
                <h3 class="item-title">{{ track.title }}</h3>
                <span v-if="track.isCertified" class="certified-badge" title="Artiste certifié">
                  <svg width="13" height="13" viewBox="0 0 24 24" fill="currentColor">
                    <path
                      d="M12 2l3.09 6.26L22 9.27l-5 4.87 1.18 6.88L12 17.77l-6.18 3.25L7 14.14 2 9.27l6.91-1.01L12 2z"
                    ></path>
                  </svg>
                </span>
              </div>
              <p class="item-meta">
                {{ track.year }} • <span class="badge-type">{{ track.type }}</span>
              </p>
            </div>

            <div class="item-actions">
              <button class="btn-play-mini" @click.stop="playTrack(track)" title="Écouter">
                <svg width="16" height="16" viewBox="0 0 24 24" fill="currentColor">
                  <polygon points="5 3 19 12 5 21 5 3"></polygon>
                </svg>
              </button>
              <button
                :class="['btn-follow-chip', { following: isFollowing(track.artistId) }]"
                @click.stop="toggleFollow(track.artistId)"
                title="Module Social S6 : Follow / Unfollow"
              >
                {{ isFollowing(track.artistId) ? 'Abonné' : 'Suivre' }}
              </button>
            </div>
          </div>
        </div>
      </section>

      <!-- Mini lecteur (quand un titre est sélectionné avec le bouton lecture) -->
      <div v-if="currentPlaying" class="mini-player-bar">
        <div class="player-left">
          <div class="player-thumb">🎵</div>
          <div class="player-info">
            <strong>{{ currentPlaying.title }}</strong>
            <span>{{ currentPlaying.artist }}</span>
          </div>
        </div>
        <div class="player-controls">
          <button class="player-btn-close" @click="currentPlaying = null">✕</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/useAuthStore'
import heroImg from '@/assets/images/hero-headphones.jpg'
import { newReleases, recommendedTracks, type Song } from '@/config/songs'

const router = useRouter()
const authStore = useAuthStore()

const searchQuery = ref('')
const activeFilter = ref('Tous')
const showAllNew = ref(false)
const currentPlaying = ref<Song | null>(null)

// Social Follow reactive state (Module S6)
const followedArtistIds = ref<Set<number>>(new Set([1]))

const userRoleName = computed(() => {
  if (!authStore.role) return 'Utilisateur'
  if (authStore.role === 'CLIENT') return 'Client'
  if (authStore.role === 'ARTIST') return 'Artiste'
  if (authStore.role === 'ADMIN') return 'Admin'
  return authStore.role
})

const filters = ['Tous', 'Nouveautés', 'Recommandé', 'Artistes', 'Groupes']

const displayedNewReleases = computed(() => {
  let list = newReleases
  if (searchQuery.value) {
    const q = searchQuery.value.toLowerCase()
    list = list.filter(
      (item) => item.title.toLowerCase().includes(q) || item.artist.toLowerCase().includes(q),
    )
  }
  if (activeFilter.value === 'Groupes') {
    list = list.filter((i) => i.type === 'Groupe')
  }
  if (!showAllNew.value) {
    return list.slice(0, 3)
  }
  return list
})

const filteredRecommended = computed(() => {
  let list = recommendedTracks
  if (searchQuery.value) {
    const q = searchQuery.value.toLowerCase()
    list = list.filter((item) => item.title.toLowerCase().includes(q))
  }
  if (activeFilter.value === 'Groupes') {
    list = list.filter((i) => i.type === 'Groupe')
  }
  return list
})

function isFollowing(artistId: number): boolean {
  return followedArtistIds.value.has(artistId)
}

function toggleFollow(artistId: number) {
  if (followedArtistIds.value.has(artistId)) {
    followedArtistIds.value.delete(artistId)
  } else {
    followedArtistIds.value.add(artistId)
  }
}

function playTrack(track: Song) {
  currentPlaying.value = track
}

// Ouvre la page « Lecteur » de la chanson choisie
// (les alert() ci-dessous sont temporaires : ils servent à voir pourquoi une navigation échoue)
async function goToSong(id: number) {
  try {
    const failure = await router.push({ name: 'song-detail', params: { id } })
    if (failure) {
      alert('Navigation bloquée : ' + failure.message)
    } else if (router.currentRoute.value.name !== 'song-detail') {
      alert('Le routeur a redirigé vers : ' + router.currentRoute.value.fullPath)
    }
  } catch (e) {
    alert('Erreur de navigation : ' + (e as Error).message)
  }
}
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Plus+Jakarta+Sans:wght@400;500;600;700;800&display=swap');

/* ╔══════════════════════════════════════════════════════════╗
   ║  --u = 1 "unité" qui grandit avec la largeur de l'écran  ║
   ║  Pour tout agrandir/réduire, change seulement 0.065vw    ║
   ║  (ex: 0.08vw = plus gros, 0.05vw = plus petit)           ║
   ╚══════════════════════════════════════════════════════════╝ */
.home-page-wrapper {
  --u: max(1px, 0.065vw);
  min-height: 100vh;
  width: 100%;
  position: relative;
  background-size: cover;
  background-position: center 25%;
  background-repeat: no-repeat;
  background-attachment: fixed;
}

.home-backdrop {
  position: absolute;
  inset: 0;
  background: radial-gradient(
    circle at 60% 30%,
    rgba(27, 19, 64, 0.48) 0%,
    rgba(15, 10, 35, 0.88) 100%
  );
  pointer-events: none;
}

.home-page-container {
  position: relative;
  z-index: 1;
  width: 100%;
  max-width: none;
  margin: 0;
  padding: calc(72 * max(1px, 0.052vw) + 32 * var(--u)) 4vw calc(110 * var(--u));
  box-sizing: border-box;
  font-family:
    'Plus Jakarta Sans',
    -apple-system,
    BlinkMacSystemFont,
    sans-serif;
  color: #1a1a1a;
}

/* ── Header: Search Section ── */
.search-header-section {
  margin-bottom: calc(40 * var(--u));
}

.header-top {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: calc(20 * var(--u));
}

.page-main-title {
  font-size: calc(46 * var(--u));
  font-weight: 800;
  color: #ffffff;
  letter-spacing: -0.025em;
  margin: 0;
  text-shadow: 0 calc(2 * var(--u)) calc(14 * var(--u)) rgba(0, 0, 0, 0.5);
}

.welcome-tag {
  font-size: calc(14 * var(--u));
  color: #f1f5f9;
  background: rgba(255, 255, 255, 0.15);
  backdrop-filter: blur(calc(10 * var(--u)));
  -webkit-backdrop-filter: blur(calc(10 * var(--u)));
  border: 1px solid rgba(255, 255, 255, 0.25);
  padding: calc(7 * var(--u)) calc(18 * var(--u));
  border-radius: calc(999 * var(--u));
  font-weight: 600;
}

.welcome-tag.guest {
  background: rgba(230, 115, 0, 0.25);
  border-color: rgba(230, 115, 0, 0.5);
  color: #ffd8a8;
}

/* Search Bar */
.search-bar-wrapper {
  position: relative;
  display: flex;
  align-items: center;
  width: 100%;
  margin-bottom: calc(20 * var(--u));
}

.main-search-input {
  width: 100%;
  padding: calc(18 * var(--u)) calc(56 * var(--u)) calc(18 * var(--u)) calc(62 * var(--u));
  font-size: calc(18 * var(--u));
  font-family: inherit;
  color: #1e293b;
  background: #fff3e0;
  border: 1.5px solid transparent;
  border-radius: calc(24 * var(--u));
  outline: none;
  transition: all 0.2s ease;
  box-sizing: border-box;
  box-shadow: 0 calc(8 * var(--u)) calc(30 * var(--u)) rgba(0, 0, 0, 0.35);
}

.main-search-input::placeholder {
  color: #bfa88a;
  font-weight: 500;
}

.main-search-input:focus {
  background: #ffffff;
  border-color: #e67300;
  box-shadow: 0 calc(10 * var(--u)) calc(35 * var(--u)) rgba(230, 115, 0, 0.3);
}

.search-icon {
  position: absolute;
  left: calc(22 * var(--u));
  display: flex;
  align-items: center;
  color: #bfa88a;
  pointer-events: none;
}

.search-icon svg {
  width: calc(24 * var(--u));
  height: calc(24 * var(--u));
}

.clear-search-btn {
  position: absolute;
  right: calc(20 * var(--u));
  background: none;
  border: none;
  color: #94a3b8;
  cursor: pointer;
  padding: calc(4 * var(--u));
  display: flex;
  align-items: center;
}

.clear-search-btn svg {
  width: calc(20 * var(--u));
  height: calc(20 * var(--u));
}

/* Filter Chips */
.filter-chips {
  display: flex;
  align-items: center;
  gap: calc(12 * var(--u));
  overflow-x: auto;
  padding-bottom: calc(4 * var(--u));
  scrollbar-width: none;
}

.filter-chips::-webkit-scrollbar {
  display: none;
}

.filter-chip {
  padding: calc(9 * var(--u)) calc(22 * var(--u));
  font-size: calc(15 * var(--u));
  font-weight: 600;
  border-radius: calc(999 * var(--u));
  border: 1px solid rgba(255, 255, 255, 0.25);
  background: rgba(255, 255, 255, 0.14);
  backdrop-filter: blur(calc(10 * var(--u)));
  -webkit-backdrop-filter: blur(calc(10 * var(--u)));
  color: #ffffff;
  cursor: pointer;
  white-space: nowrap;
  transition: all 0.2s ease;
}

.filter-chip:hover {
  background: rgba(255, 255, 255, 0.25);
  border-color: rgba(255, 255, 255, 0.45);
}

.filter-chip.active {
  background: linear-gradient(135deg, #1b1340 0%, #e67300 100%);
  color: #ffffff;
  border-color: #e67300;
  box-shadow: 0 calc(4 * var(--u)) calc(16 * var(--u)) rgba(230, 115, 0, 0.4);
}

/* ── Section Styles ── */
.music-section {
  margin-bottom: calc(48 * var(--u));
}

.section-heading {
  display: flex;
  align-items: baseline;
  justify-content: space-between;
  margin-bottom: calc(20 * var(--u));
}

.section-title {
  font-size: calc(26 * var(--u));
  font-weight: 800;
  color: #ffffff;
  letter-spacing: -0.015em;
  margin: 0;
  text-shadow: 0 calc(2 * var(--u)) calc(10 * var(--u)) rgba(0, 0, 0, 0.4);
}

.see-all-link {
  font-size: calc(15 * var(--u));
  font-weight: 700;
  color: #ffa500;
  text-decoration: none;
  transition: all 0.15s ease;
}

.see-all-link:hover {
  color: #ffbe4d;
  text-decoration: underline;
}

/* ── SECTION 1 : Cards Grid (Nouveauté) ── */
.cards-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(min(100%, calc(260 * var(--u))), 1fr));
  gap: calc(24 * var(--u));
}

.music-card {
  background: rgba(255, 255, 255, 0.92);
  backdrop-filter: blur(calc(16 * var(--u)));
  -webkit-backdrop-filter: blur(calc(16 * var(--u)));
  border: 1px solid rgba(255, 255, 255, 0.45);
  border-radius: calc(26 * var(--u));
  padding: calc(18 * var(--u));
  display: flex;
  flex-direction: column;
  transition: all 0.25s ease;
  box-shadow: 0 calc(10 * var(--u)) calc(30 * var(--u)) rgba(0, 0, 0, 0.25);
}

.music-card:hover {
  transform: translateY(calc(-5 * var(--u)));
  box-shadow: 0 calc(20 * var(--u)) calc(42 * var(--u)) rgba(0, 0, 0, 0.35);
  background: rgba(255, 255, 255, 0.98);
  border-color: #ffffff;
}

.card-cover-wrapper {
  width: 100%;
  aspect-ratio: 1 / 0.9;
  border-radius: calc(20 * var(--u));
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
  margin-bottom: calc(14 * var(--u));
}

.cover-placeholder {
  color: rgba(27, 19, 64, 0.25);
  display: flex;
}

.cover-placeholder svg {
  width: calc(48 * var(--u));
  height: calc(48 * var(--u));
}

.play-overlay-btn {
  position: absolute;
  bottom: calc(12 * var(--u));
  right: calc(12 * var(--u));
  width: calc(48 * var(--u));
  height: calc(48 * var(--u));
  border-radius: 50%;
  background: #e67300;
  color: #ffffff;
  border: none;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  box-shadow: 0 calc(6 * var(--u)) calc(16 * var(--u)) rgba(230, 115, 0, 0.35);
  opacity: 0;
  transform: scale(0.85);
  transition: all 0.2s ease;
}

.play-overlay-btn svg {
  width: calc(22 * var(--u));
  height: calc(22 * var(--u));
}

.music-card:hover .play-overlay-btn {
  opacity: 1;
  transform: scale(1);
}

.play-overlay-btn:hover {
  transform: scale(1.08);
  background: #c45e00;
}

.card-details {
  display: flex;
  flex-direction: column;
  gap: calc(4 * var(--u));
}

.card-title-row {
  display: flex;
  align-items: center;
  gap: calc(6 * var(--u));
}

.card-title {
  font-size: calc(19 * var(--u));
  font-weight: 800;
  color: #0f172a;
  margin: 0;
}

.certified-badge {
  color: #3b82f6;
  display: inline-flex;
  align-items: center;
}

.certified-badge svg {
  width: calc(16 * var(--u));
  height: calc(16 * var(--u));
}

.card-date {
  font-size: calc(14.5 * var(--u));
  color: #64748b;
  margin: 0;
  font-weight: 500;
}

.card-artist {
  font-size: calc(13.5 * var(--u));
  color: #94a3b8;
  margin: 0 0 calc(10 * var(--u));
}

.btn-follow-compact {
  align-self: flex-start;
  padding: calc(7 * var(--u)) calc(16 * var(--u));
  font-size: calc(13.5 * var(--u));
  font-weight: 700;
  border-radius: calc(999 * var(--u));
  border: 1px solid #cbd5e1;
  background: #ffffff;
  color: #334155;
  cursor: pointer;
  transition: all 0.2s ease;
}

.btn-follow-compact:hover {
  border-color: #e67300;
  color: #e67300;
}

.btn-follow-compact.following {
  background: rgba(230, 115, 0, 0.1);
  border-color: #e67300;
  color: #e67300;
}

/* ── SECTION 2 : Recommended List (grille multi-colonnes) ── */
.recommended-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(min(100%, calc(520 * var(--u))), 1fr));
  gap: calc(14 * var(--u));
}

.recommended-item {
  display: flex;
  align-items: center;
  gap: calc(18 * var(--u));
  padding: calc(14 * var(--u)) calc(20 * var(--u));
  background: #ffffff;
  border: 1px solid #f1f5f9;
  border-radius: calc(20 * var(--u));
  transition: all 0.2s ease;
}

.recommended-item:hover {
  background: #fdfaf6;
  border-color: #fed7aa;
  transform: translateX(calc(4 * var(--u)));
}

.item-thumb {
  width: calc(60 * var(--u));
  height: calc(60 * var(--u));
  border-radius: calc(16 * var(--u));
  display: flex;
  align-items: center;
  justify-content: center;
  color: #bfa88a;
  flex-shrink: 0;
}

.item-thumb svg {
  width: calc(28 * var(--u));
  height: calc(28 * var(--u));
}

.item-info {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
  gap: calc(3 * var(--u));
}

.item-title-row {
  display: flex;
  align-items: center;
  gap: calc(6 * var(--u));
}

.item-title {
  font-size: calc(17 * var(--u));
  font-weight: 700;
  color: #1e293b;
  margin: 0;
}

.item-meta {
  font-size: calc(14 * var(--u));
  color: #64748b;
  margin: 0;
}

.badge-type {
  font-size: calc(12.5 * var(--u));
  font-weight: 600;
  color: #4a3b8f;
  background: rgba(74, 59, 143, 0.08);
  padding: calc(2 * var(--u)) calc(9 * var(--u));
  border-radius: calc(7 * var(--u));
}

.item-actions {
  display: flex;
  align-items: center;
  gap: calc(12 * var(--u));
}

.btn-play-mini {
  width: calc(42 * var(--u));
  height: calc(42 * var(--u));
  border-radius: 50%;
  background: #f1f5f9;
  border: none;
  color: #1e293b;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.2s ease;
}

.btn-play-mini svg {
  width: calc(18 * var(--u));
  height: calc(18 * var(--u));
}

.btn-play-mini:hover {
  background: #e67300;
  color: #ffffff;
}

.btn-follow-chip {
  padding: calc(7 * var(--u)) calc(16 * var(--u));
  font-size: calc(13.5 * var(--u));
  font-weight: 700;
  border-radius: calc(999 * var(--u));
  border: 1px solid #cbd5e1;
  background: #ffffff;
  color: #334155;
  cursor: pointer;
  transition: all 0.2s ease;
}

.btn-follow-chip:hover {
  border-color: #e67300;
  color: #e67300;
}

.btn-follow-chip.following {
  background: rgba(230, 115, 0, 0.1);
  border-color: #e67300;
  color: #e67300;
}

/* Mini Player Bar */
.mini-player-bar {
  position: fixed;
  bottom: calc(24 * var(--u));
  left: 50%;
  transform: translateX(-50%);
  width: min(92%, calc(640 * var(--u)));
  background: #1b1340;
  color: #ffffff;
  border-radius: calc(22 * var(--u));
  padding: calc(14 * var(--u)) calc(22 * var(--u));
  display: flex;
  align-items: center;
  justify-content: space-between;
  box-shadow: 0 calc(16 * var(--u)) calc(40 * var(--u)) rgba(27, 19, 64, 0.35);
  z-index: 100;
  animation: slideUp 0.3s ease-out;
}

@keyframes slideUp {
  from {
    opacity: 0;
    transform: translate(-50%, calc(20 * var(--u)));
  }
  to {
    opacity: 1;
    transform: translate(-50%, 0);
  }
}

.player-left {
  display: flex;
  align-items: center;
  gap: calc(14 * var(--u));
}

.player-thumb {
  font-size: calc(24 * var(--u));
}

.player-info strong {
  display: block;
  font-size: calc(15 * var(--u));
}

.player-info span {
  font-size: calc(13 * var(--u));
  color: rgba(255, 255, 255, 0.7);
}

.player-btn-close {
  background: none;
  border: none;
  color: #ffffff;
  font-size: calc(18 * var(--u));
  cursor: pointer;
  padding: calc(6 * var(--u));
}

/* ── Mobile ── */
@media (max-width: 640px) {
  .home-page-container {
    padding: 96px 16px 100px;
  }
  .page-main-title {
    font-size: calc(34 * var(--u));
  }
}

/* ── Cartes / lignes cliquables → page détail ── */
.music-card,
.recommended-item {
  cursor: pointer;
}

.music-card:focus-visible,
.recommended-item:focus-visible {
  outline: calc(3 * var(--u)) solid #ffa500;
  outline-offset: calc(3 * var(--u));
}
</style>
