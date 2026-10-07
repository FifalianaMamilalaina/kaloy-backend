<script setup lang="ts">
import { ref, computed, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getSongById } from '@/config/songs'

type TabKey = 'audio' | 'video' | 'karaoke' | 'playback'

interface Media {
  kind: 'youtube' | 'audio' | 'none'
  src?: string
  message?: string
}

const tabs: { key: TabKey; label: string }[] = [
  { key: 'audio', label: 'Audio' },
  { key: 'video', label: 'Vidéo' },
  { key: 'karaoke', label: 'Karaoké' },
  { key: 'playback', label: 'Playback' },
]

const route = useRoute()
const router = useRouter()

// La chanson affichée dépend de l'id dans l'URL : /songs/:id
const song = computed(() => getSongById(Number(route.params.id)))

const activeTab = ref<TabKey>('video')
const showSolfa = ref(false)

// Quand on passe d'une chanson à une autre, on repart de zéro
watch(
  () => route.params.id,
  () => {
    activeTab.value = 'video'
    showSolfa.value = false
  },
)

const activeLabel = computed(() => tabs.find((t) => t.key === activeTab.value)?.label ?? '')

const youtubeSrc = (id: string) => `https://www.youtube.com/embed/${id}?rel=0`

const media = computed<Media>(() => {
  const s = song.value
  if (!s) return { kind: 'none', message: '' }

  switch (activeTab.value) {
    case 'audio':
      return s.audioUrl
        ? { kind: 'audio', src: s.audioUrl }
        : { kind: 'none', message: 'Aucun fichier audio pour ce titre.' }
    case 'video':
      return s.youtubeId
        ? { kind: 'youtube', src: youtubeSrc(s.youtubeId) }
        : { kind: 'none', message: 'Aucune vidéo pour ce titre.' }
    case 'karaoke':
      return s.karaokeYoutubeId
        ? { kind: 'youtube', src: youtubeSrc(s.karaokeYoutubeId) }
        : { kind: 'none', message: "Le karaoké n'est pas encore disponible." }
    case 'playback':
      return s.playbackUrl
        ? { kind: 'audio', src: s.playbackUrl }
        : { kind: 'none', message: 'Aucun playback pour ce titre.' }
  }
})

function goBack() {
  if (window.history.length > 1) router.back()
  else router.push({ name: 'home' })
}
</script>

<template>
  <div class="song-page">
    <div class="player">
      <!-- En-tête : flèche de retour + titre -->
      <header class="player-header">
        <button type="button" class="back-btn" aria-label="Retour" @click="goBack">
          <svg
            viewBox="0 0 24 24"
            fill="none"
            stroke="currentColor"
            stroke-width="2.6"
            stroke-linecap="round"
            stroke-linejoin="round"
          >
            <polyline points="6 9 12 15 18 9"></polyline>
          </svg>
        </button>
        <span class="player-heading">Lecteur</span>
        <span class="header-spacer" aria-hidden="true"></span>
      </header>

      <template v-if="song">
        <!-- Zone média (vidéo / audio / karaoké / playback) -->
        <div class="media-card">
          <iframe
            v-if="media.kind === 'youtube'"
            :key="`${song.id}-${activeTab}`"
            class="media-frame"
            :src="media.src"
            :title="`${song.title} — ${activeLabel}`"
            allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture"
            referrerpolicy="strict-origin-when-cross-origin"
            allowfullscreen
          ></iframe>

          <div v-else-if="media.kind === 'audio'" class="audio-panel">
            <div class="audio-cover" :style="{ background: song.coverBg }">
              <svg viewBox="0 0 24 24" fill="currentColor">
                <path d="M12 3v10.55A4 4 0 1 0 14 17V7h4V3h-6z"></path>
              </svg>
            </div>
            <audio
              :key="`${song.id}-${activeTab}`"
              :src="media.src"
              controls
              preload="none"
            ></audio>
          </div>

          <div v-else class="media-empty">
            <svg viewBox="0 0 24 24" fill="currentColor">
              <path d="M12 3v10.55A4 4 0 1 0 14 17V7h4V3h-6z"></path>
            </svg>
            <p>{{ media.message }}</p>
          </div>
        </div>

        <!-- Titre, artiste, album -->
        <h1 class="song-title">{{ song.title }}</h1>
        <p class="song-artist">{{ song.artist }}</p>
        <p class="song-album">{{ song.album }}</p>

        <!-- Onglets -->
        <div class="tabs" role="tablist" aria-label="Type de lecture">
          <button
            v-for="tab in tabs"
            :key="tab.key"
            type="button"
            role="tab"
            class="tab"
            :aria-selected="activeTab === tab.key"
            @click="activeTab = tab.key"
          >
            {{ tab.label }}
          </button>
        </div>

        <!-- Partition solfa -->
        <button
          v-if="song.solfa"
          type="button"
          class="solfa-btn"
          :aria-expanded="showSolfa"
          @click="showSolfa = !showSolfa"
        >
          <svg viewBox="0 0 24 24" fill="currentColor">
            <path d="M12 3v10.55A4 4 0 1 0 14 17V7h4V3h-6z"></path>
          </svg>
          Partition Solfa
        </button>

        <section v-if="showSolfa && song.solfa" class="info-card">
          <h2 class="card-title">Partition Solfa</h2>
          <pre class="solfa-text">{{ song.solfa }}</pre>
        </section>

        <!-- Paroles -->
        <section class="info-card">
          <h2 class="card-title">Paroles</h2>
          <template v-if="song.lyrics.length">
            <p v-for="(line, i) in song.lyrics" :key="i" class="lyric-line">{{ line }}</p>
          </template>
          <p v-else class="lyric-line empty">Les paroles ne sont pas encore disponibles.</p>
        </section>

        <!-- Crédits -->
        <section v-if="song.credits.length" class="info-card">
          <h2 class="card-title">Crédits</h2>
          <dl class="credits">
            <div v-for="credit in song.credits" :key="credit.label" class="credit-row">
              <dt>{{ credit.label }}</dt>
              <dd>{{ credit.value }}</dd>
            </div>
          </dl>
        </section>
      </template>

      <!-- Chanson introuvable -->
      <div v-else class="not-found">
        <p>Cette chanson est introuvable.</p>
        <button type="button" class="solfa-btn" @click="router.push({ name: 'home' })">
          Retour à l'accueil
        </button>
      </div>
    </div>
  </div>
</template>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Plus+Jakarta+Sans:wght@400;500;600;700;800&display=swap');

/* ── Palette (ton image) + unité d'échelle ──
   --u : 1 unité = 1 px de la maquette (720px de large). Elle grandit avec l'écran
   et se réduit sur téléphone. Plus gros : 0.08vw | Plus petit : 0.05vw          */
.song-page {
  --navy: #151531;
  --deep: #10102a;
  --indigo: #323181;
  --orange: #e45a01;
  --orange-soft: #ff8a3d;
  --cream: #fcf4e7;
  --card: rgba(252, 244, 231, 0.06);
  --text: #fcf4e7;
  --muted: rgba(252, 244, 231, 0.55);
  --faint: rgba(252, 244, 231, 0.38);
  --u: min(calc(100vw / 720), max(1px, 0.065vw));

  min-height: 100vh;
  box-sizing: border-box;
  /* 72 * unité de la barre = hauteur de la navbar transparente */
  padding: calc(72 * max(1px, 0.052vw) + 24 * var(--u)) 0 calc(90 * var(--u));
  background-color: var(--deep);
  /* Dégradé : indigo en haut → bleu nuit → presque noir */
  background-image: linear-gradient(
    180deg,
    #323181 calc(0 * var(--u)),
    #232357 calc(380 * var(--u)),
    #151531 calc(900 * var(--u)),
    #10102a calc(1500 * var(--u))
  );
  background-repeat: no-repeat;
  font-family:
    'Plus Jakarta Sans',
    -apple-system,
    BlinkMacSystemFont,
    sans-serif;
  color: var(--text);
}

.player {
  width: min(100%, calc(720 * var(--u)));
  margin: 0 auto;
  padding: 0 calc(44 * var(--u));
  box-sizing: border-box;
}

/* ── En-tête ── */
.player-header {
  display: grid;
  grid-template-columns: calc(88 * var(--u)) 1fr calc(88 * var(--u));
  align-items: center;
  height: calc(96 * var(--u));
  margin: 0 calc(-28 * var(--u)) calc(8 * var(--u));
}

.back-btn {
  width: calc(88 * var(--u));
  height: calc(88 * var(--u));
  display: flex;
  align-items: center;
  justify-content: center;
  background: none;
  border: none;
  border-radius: 50%;
  color: var(--text);
  cursor: pointer;
  transition: background-color 0.2s ease;
}

.back-btn svg {
  width: calc(40 * var(--u));
  height: calc(40 * var(--u));
}

.back-btn:hover {
  background: rgba(252, 244, 231, 0.1);
}

.player-heading {
  text-align: center;
  font-size: calc(26 * var(--u));
  font-weight: 600;
  letter-spacing: 0.02em;
  color: var(--muted);
}

/* ── Zone média ── */
.media-card {
  aspect-ratio: 632 / 355;
  border-radius: calc(28 * var(--u));
  overflow: hidden;
  background:
    radial-gradient(circle at 72% 48%, rgba(50, 49, 129, 0.35) 0%, rgba(50, 49, 129, 0) 55%),
    #0c0c18;
  display: flex;
}

.media-frame {
  width: 100%;
  height: 100%;
  border: 0;
}

.media-empty {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: calc(18 * var(--u));
  padding: 0 calc(40 * var(--u));
  text-align: center;
  color: var(--muted);
}

.media-empty svg {
  width: calc(56 * var(--u));
  height: calc(56 * var(--u));
  opacity: 0.6;
}

.media-empty p {
  margin: 0;
  font-size: calc(25 * var(--u));
  line-height: 1.4;
}

.audio-panel {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: calc(26 * var(--u));
  padding: 0 calc(40 * var(--u));
}

.audio-cover {
  width: calc(130 * var(--u));
  height: calc(130 * var(--u));
  border-radius: calc(28 * var(--u));
  display: flex;
  align-items: center;
  justify-content: center;
  color: rgba(21, 21, 49, 0.3);
}

.audio-cover svg {
  width: calc(60 * var(--u));
  height: calc(60 * var(--u));
}

.audio-panel audio {
  width: 100%;
  color-scheme: dark;
}

/* ── Titre / artiste / album ── */
.song-title {
  margin: calc(44 * var(--u)) 0 0;
  font-size: calc(42 * var(--u));
  font-weight: 800;
  line-height: 1.15;
  letter-spacing: -0.01em;
}

.song-artist {
  margin: calc(12 * var(--u)) 0 0;
  font-size: calc(27 * var(--u));
  color: var(--muted);
}

.song-album {
  margin: calc(14 * var(--u)) 0 0;
  font-size: calc(23 * var(--u));
  letter-spacing: 0.04em;
  color: var(--faint);
}

/* ── Onglets ── */
.tabs {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: calc(4 * var(--u));
  margin-top: calc(44 * var(--u));
  padding: calc(8 * var(--u));
  background: var(--card);
  border-radius: calc(26 * var(--u));
}

.tab {
  height: calc(86 * var(--u));
  border: none;
  border-radius: calc(20 * var(--u));
  background: transparent;
  font-family: inherit;
  font-size: calc(24 * var(--u));
  font-weight: 500;
  color: var(--muted);
  cursor: pointer;
  transition:
    background-color 0.2s ease,
    color 0.2s ease;
}

.tab:hover {
  color: var(--text);
}

.tab[aria-selected='true'] {
  background: var(--indigo);
  color: var(--cream);
  font-weight: 700;
}

/* ── Bouton Partition Solfa ── */
.solfa-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: calc(16 * var(--u));
  width: 100%;
  height: calc(76 * var(--u));
  margin: calc(38 * var(--u)) 0 calc(8 * var(--u));
  background: transparent;
  border: calc(2 * var(--u)) solid var(--orange);
  border-radius: calc(22 * var(--u));
  font-family: inherit;
  font-size: calc(27 * var(--u));
  font-weight: 600;
  color: var(--orange-soft);
  cursor: pointer;
  transition: background-color 0.2s ease;
}

.solfa-btn svg {
  width: calc(28 * var(--u));
  height: calc(28 * var(--u));
}

.solfa-btn:hover {
  background: rgba(228, 90, 1, 0.12);
}

/* ── Cartes Paroles / Crédits ── */
.info-card {
  margin-top: calc(30 * var(--u));
  padding: calc(34 * var(--u)) calc(30 * var(--u));
  background: var(--card);
  border-radius: calc(28 * var(--u));
}

.card-title {
  margin: 0 0 calc(22 * var(--u));
  font-size: calc(22 * var(--u));
  font-weight: 700;
  color: var(--muted);
}

.lyric-line {
  margin: 0;
  font-size: calc(29 * var(--u));
  line-height: calc(41 * var(--u));
  color: var(--text);
}

.lyric-line.empty {
  color: var(--faint);
}

.solfa-text {
  margin: 0;
  overflow-x: auto;
  font-family: 'Courier New', monospace;
  font-size: calc(25 * var(--u));
  line-height: 1.7;
  color: var(--text);
}

.credits {
  margin: 0;
}

.credit-row {
  display: flex;
  justify-content: space-between;
  gap: calc(24 * var(--u));
  font-size: calc(25 * var(--u));
  line-height: calc(52 * var(--u));
}

.credit-row dt {
  color: var(--muted);
}

.credit-row dd {
  margin: 0;
  text-align: right;
  color: var(--text);
}

.not-found {
  margin-top: calc(60 * var(--u));
  text-align: center;
  font-size: calc(28 * var(--u));
  color: var(--muted);
}

/* ── Accessibilité ── */
.back-btn:focus-visible,
.tab:focus-visible,
.solfa-btn:focus-visible {
  outline: calc(3 * var(--u)) solid var(--orange);
  outline-offset: calc(3 * var(--u));
}

@media (prefers-reduced-motion: reduce) {
  .back-btn,
  .tab,
  .solfa-btn {
    transition: none;
  }
}
</style>
