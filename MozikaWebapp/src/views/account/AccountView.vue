<script setup lang="ts">
import { ref, computed, nextTick, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { me, changeContact } from '@/services/AuthService'
import heroImg from '@/assets/images/hero-headphones.jpg'

const router = useRouter()

const currentEmail = ref('')
const currentStatus = ref('')
const newEmail = ref('')
const errorMessage = ref('')
const successMessage = ref('')

// Nouveau : état d'édition + envoi en cours
const editing = ref(false)
const submitting = ref(false)
const newEmailInput = ref<HTMLInputElement | null>(null)

const initial = computed(() =>
  currentEmail.value ? currentEmail.value.charAt(0).toUpperCase() : '?',
)

async function loadProfile() {
  const response = await me()
  if (response.success && response.data) {
    currentEmail.value = response.data.email
    currentStatus.value = response.data.status
  } else {
    errorMessage.value = 'Impossible de charger votre profil.'
  }
}

async function startEdit() {
  editing.value = true
  errorMessage.value = ''
  successMessage.value = ''
  await nextTick()
  newEmailInput.value?.focus()
}

function cancelEdit() {
  editing.value = false
  newEmail.value = ''
  errorMessage.value = ''
  successMessage.value = ''
}

async function handleChangeContact() {
  errorMessage.value = ''
  successMessage.value = ''
  submitting.value = true

  try {
    const response = await changeContact({ newEmail: newEmail.value, newPhone: null })

    if (response.success && response.data) {
      successMessage.value = `${response.data.message} à ${response.data.destination}`
      router.push({
        name: 'login-otp',
        query: { otpToken: response.data.otpToken, expiresAt: response.data.expiresAt },
      })
    } else {
      errorMessage.value = response.error || 'Une erreur est survenue.'
    }
  } finally {
    submitting.value = false
  }
}

onMounted(() => {
  loadProfile()
})
</script>

<template>
  <div class="account-page" :style="{ backgroundImage: `url(${heroImg})` }">
    <div class="account-backdrop"></div>

    <form class="account-card" @submit.prevent="handleChangeContact">
      <!-- En-tête -->
      <header class="card-header">
        <h1>Mon compte</h1>
      </header>

      <!-- Profil -->
      <section class="card-section profile">
        <div class="avatar" aria-hidden="true">{{ initial }}</div>
        <div class="profile-info">
          <h2 class="profile-email">{{ currentEmail || 'Chargement…' }}</h2>
          <span class="status-pill">
            <span class="status-dot" aria-hidden="true"></span>
            Statut : {{ currentStatus || '…' }}
          </span>
        </div>
      </section>

      <!-- Email -->
      <section class="card-section">
        <div class="field">
          <label for="currentEmail">Email</label>
          <div class="field-row">
            <input id="currentEmail" type="email" :value="currentEmail" readonly />
            <button type="button" class="btn btn-secondary" :disabled="editing" @click="startEdit">
              Modifier l'email
            </button>
          </div>
          <p class="hint">Utilisé pour vous connecter à votre compte.</p>
        </div>

        <div v-if="editing" class="field">
          <label for="newEmail">Nouvel email</label>
          <input
            id="newEmail"
            ref="newEmailInput"
            type="email"
            v-model="newEmail"
            placeholder="nom@exemple.com"
            required
          />
          <p class="hint">Un code de vérification sera envoyé à cette adresse.</p>
        </div>

        <p v-if="successMessage" class="notice success" role="status">{{ successMessage }}</p>
        <p v-if="errorMessage" class="notice error" role="alert">{{ errorMessage }}</p>
      </section>

      <!-- Actions -->
      <footer class="card-footer">
        <button type="button" class="btn btn-secondary" :disabled="!editing" @click="cancelEdit">
          Annuler
        </button>
        <button
          type="submit"
          class="btn btn-primary"
          :disabled="!editing || !newEmail || submitting"
        >
          {{ submitting ? 'Envoi en cours…' : 'Envoyer le code de vérification' }}
        </button>
      </footer>
    </form>
  </div>
</template>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Plus+Jakarta+Sans:wght@400;500;600;700;800&display=swap');

/* ── Palette (tirée de ton image) + unité d'échelle ──
   --u : grandit avec l'écran. Plus gros : 0.08vw | Plus petit : 0.05vw */
.account-page {
  --navy: #151531;
  --indigo: #323181;
  --orange: #e45a01;
  --beige: #f1e5cf;
  --cream: #fcf4e7;
  --mist: #f7f7f7;
  --line: #efe7d8;
  --field-border: #ddd3c0;
  --muted: rgba(21, 21, 49, 0.55);
  --u: max(1px, 0.065vw);

  min-height: 100vh;
  width: 100%;
  box-sizing: border-box;
  padding: calc(72 * max(1px, 0.052vw) + 48 * var(--u)) 4vw calc(90 * var(--u));
  position: relative;
  background-size: cover;
  background-position: center 25%;
  background-repeat: no-repeat;
  background-attachment: fixed;
  font-family: 'Plus Jakarta Sans', sans-serif;
  color: var(--navy);
}

/* ── Fond sombre par-dessus l'image (identique à la page d'accueil) ── */
.account-backdrop {
  position: absolute;
  inset: 0;
  background: radial-gradient(
    circle at 60% 30%,
    rgba(27, 19, 64, 0.48) 0%,
    rgba(15, 10, 35, 0.88) 100%
  );
  pointer-events: none;
}

/* ── Carte ── */
.account-card {
  max-width: calc(900 * var(--u));
  margin: 0 auto;
  background: rgba(255, 255, 255, 0.8);
  backdrop-filter: blur(18px) saturate(1.15);
  -webkit-backdrop-filter: blur(18px) saturate(1.15);
  border: 1px solid rgba(255, 255, 255, 0.55);
  border-radius: calc(26 * var(--u));
  position: relative;
  z-index: 1;
  box-shadow: 0 calc(24 * var(--u)) calc(60 * var(--u)) rgba(0, 0, 0, 0.4);
  overflow: hidden;
}

.card-header {
  padding: calc(30 * var(--u)) calc(44 * var(--u));
  border-bottom: 1px solid var(--line);
}

h1 {
  margin: 0;
  font-size: calc(30 * var(--u));
  font-weight: 800;
  letter-spacing: -0.02em;
}

.card-section {
  padding: calc(34 * var(--u)) calc(44 * var(--u));
}

.card-section + .card-section {
  border-top: 1px solid var(--line);
}

/* ── Profil ── */
.profile {
  display: flex;
  align-items: center;
  gap: calc(26 * var(--u));
}

.avatar {
  flex-shrink: 0;
  width: calc(92 * var(--u));
  height: calc(92 * var(--u));
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--cream);
  color: var(--indigo);
  font-size: calc(38 * var(--u));
  font-weight: 800;
  box-shadow: 0 0 0 calc(4 * var(--u)) var(--beige);
}

.profile-info {
  min-width: 0;
}

.profile-email {
  margin: 0 0 calc(12 * var(--u));
  font-size: calc(22 * var(--u));
  font-weight: 700;
  overflow-wrap: anywhere;
}

.status-pill {
  display: inline-flex;
  align-items: center;
  gap: calc(9 * var(--u));
  padding: calc(7 * var(--u)) calc(16 * var(--u));
  border-radius: calc(999 * var(--u));
  background: var(--cream);
  border: 1px solid var(--beige);
  font-size: calc(14.5 * var(--u));
  font-weight: 600;
}

.status-dot {
  width: calc(9 * var(--u));
  height: calc(9 * var(--u));
  border-radius: 50%;
  background: var(--orange);
}

/* ── Champs ── */
.field + .field {
  margin-top: calc(28 * var(--u));
}

label {
  display: block;
  margin-bottom: calc(10 * var(--u));
  font-size: calc(15.5 * var(--u));
  font-weight: 700;
}

.field-row {
  display: flex;
  gap: calc(14 * var(--u));
  align-items: stretch;
}

input {
  width: 100%;
  box-sizing: border-box;
  padding: calc(16 * var(--u)) calc(20 * var(--u));
  font-family: inherit;
  font-size: calc(17 * var(--u));
  color: var(--navy);
  background: #ffffff;
  border: 1px solid var(--field-border);
  border-radius: calc(15 * var(--u));
  outline: none;
  transition:
    border-color 0.2s ease,
    box-shadow 0.2s ease;
}

input::placeholder {
  color: rgba(21, 21, 49, 0.35);
}

input:focus {
  border-color: var(--orange);
  box-shadow: 0 0 0 calc(4 * var(--u)) rgba(228, 90, 1, 0.16);
}

input[readonly] {
  flex: 1;
  min-width: 0;
  background: var(--cream);
  cursor: default;
}

.hint {
  margin: calc(12 * var(--u)) 0 0;
  font-size: calc(14.5 * var(--u));
  color: var(--muted);
}

/* ── Messages ── */
.notice {
  margin: calc(26 * var(--u)) 0 0;
  padding: calc(15 * var(--u)) calc(20 * var(--u));
  border-radius: calc(13 * var(--u));
  font-size: calc(15.5 * var(--u));
  font-weight: 600;
}

.notice.success {
  background: #eaf6ee;
  color: #1e6b3a;
  box-shadow: inset calc(4 * var(--u)) 0 0 #2e9e5b;
}

.notice.error {
  background: #fdecea;
  color: #a32a1f;
  box-shadow: inset calc(4 * var(--u)) 0 0 #d0402f;
}

/* ── Boutons ── */
.btn {
  padding: calc(15 * var(--u)) calc(26 * var(--u));
  font-family: inherit;
  font-size: calc(16 * var(--u));
  font-weight: 700;
  border-radius: calc(15 * var(--u));
  cursor: pointer;
  white-space: nowrap;
  transition:
    background-color 0.2s ease,
    box-shadow 0.2s ease,
    border-color 0.2s ease;
}

.btn-secondary {
  background: #ffffff;
  color: var(--navy);
  border: 1px solid var(--field-border);
}

.btn-secondary:hover:not(:disabled) {
  background: var(--cream);
  border-color: var(--beige);
}

.btn-primary {
  background: var(--indigo);
  color: #ffffff;
  border: none;
  box-shadow: 0 calc(8 * var(--u)) calc(20 * var(--u)) rgba(50, 49, 129, 0.28);
}

.btn-primary:hover:not(:disabled) {
  background: var(--navy);
}

.btn:disabled {
  opacity: 0.45;
  cursor: not-allowed;
  box-shadow: none;
}

.btn:focus-visible {
  outline: calc(3 * var(--u)) solid var(--orange);
  outline-offset: calc(3 * var(--u));
}

/* ── Pied de carte ── */
.card-footer {
  display: flex;
  justify-content: flex-end;
  gap: calc(14 * var(--u));
  padding: calc(26 * var(--u)) calc(44 * var(--u));
  background: rgba(253, 250, 244, 0.55);
  border-top: 1px solid var(--line);
}

/* ── Mobile ── */
@media (max-width: 640px) {
  .card-header,
  .card-section,
  .card-footer {
    padding: 20px;
  }
  .profile {
    gap: 16px;
  }
  .avatar {
    width: 68px;
    height: 68px;
    font-size: 28px;
  }
  .field-row {
    flex-direction: column;
  }
  .card-footer {
    flex-direction: column-reverse;
  }
  .btn {
    width: 100%;
  }
}

@media (prefers-reduced-motion: reduce) {
  .btn,
  input {
    transition: none;
  }
}
</style>
