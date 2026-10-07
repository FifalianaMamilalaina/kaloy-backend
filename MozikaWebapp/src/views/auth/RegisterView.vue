<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { register } from '@/services/AuthService'
import type { RegisterPayload } from '@/models/auth/AuthModels'


const router = useRouter()

const email = ref('')
const password = ref('')
import heroImg from '@/assets/images/hero-headphones.jpg'

const router = useRouter()

const fullName = ref('')
const email = ref('')
const userName = ref('')
const password = ref('')
const confirmPassword = ref('')
const showPassword = ref(false)
const showConfirmPassword = ref(false)
const role = ref<'CLIENT' | 'ARTIST'>('CLIENT')
const artistType = ref<'solo' | 'group'>('solo')
const stageName = ref('')

const successMessage = ref('')
const errorMessage = ref('')
const isLoading = ref(false)


async function handleSubmit() {
  successMessage.value = ''
  errorMessage.value = ''

  if (password.value !== confirmPassword.value) {
    errorMessage.value = 'Les mots de passe ne correspondent pas.'
    return
  }

  isLoading.value = true

  const payload: RegisterPayload = {
    email: email.value,
    phone: null,
    password: password.value,
    role: role.value,
    artistType: role.value === 'ARTIST' ? artistType.value : null,
    stageName: role.value === 'ARTIST' ? stageName.value : null,
  }


  const response = await register(payload)

  if (response.success && response.data) {
    successMessage.value = `Compte créé avec succès (id: ${response.data.id})`
    router.push({ name: 'login-otp', query: { otpToken: response.data.otpToken } })
  } else {
    errorMessage.value = response.error || 'Une erreur est survenue.'

  try {
    const response = await register(payload)

    if (response.success && response.data) {
      successMessage.value = `Compte créé avec succès !`
      router.push({ name: 'login-otp', query: { otpToken: response.data.otpToken } })
    } else {
      errorMessage.value = response.error || 'Une erreur est survenue lors de l’inscription.'
    }
  } catch {
    errorMessage.value = 'Erreur réseau ou serveur inaccessible.'
  } finally {
    isLoading.value = false
  }
}
</script>

<template>
  <div class="auth-page">
    <h1>Inscription</h1>

    <form @submit.prevent="handleSubmit">
      <div class="field">
        <label for="email">Email</label>
        <input id="email" type="email" v-model="email" required />
      </div>

      <div class="field">
        <label for="password">Mot de passe</label>
        <input id="password" type="password" v-model="password" required />
      </div>

      <div class="field">
        <label for="role">Type de compte</label>
        <select id="role" v-model="role">
          <option value="CLIENT">Client</option>
          <option value="ARTIST">Artiste</option>
        </select>
      </div>

      <div v-if="role === 'ARTIST'" class="field">
        <label for="artistType">Type d'artiste</label>
        <select id="artistType" v-model="artistType">
          <option value="solo">Solo</option>
          <option value="group">Groupe</option>
        </select>
      </div>

      <div v-if="role === 'ARTIST'" class="field">
        <label for="stageName">Nom de scène</label>
        <input id="stageName" type="text" v-model="stageName" />
      </div>

      <button type="submit">Créer mon compte</button>
    </form>

    <p v-if="successMessage" class="success">{{ successMessage }}</p>
    <p v-if="errorMessage" class="error">{{ errorMessage }}</p>

    <p class="link">
      Déjà un compte ? <router-link :to="{ name: 'login' }">Se connecter</router-link>
    </p>
  <div class="auth-wrapper" :style="{ backgroundImage: `url(${heroImg})` }">
    <div class="auth-backdrop"></div>

    <!-- Floating Glassmorphic Card (Larger & Well Proportioned) -->
    <div class="glass-card">
      <div class="card-header">
        <h1 class="card-title">Register</h1>
        <p class="card-subtitle">Create your account to start your journey</p>
      </div>

      <form @submit.prevent="handleSubmit" class="auth-form">
        <div class="form-grid">
          <!-- Full Name -->
          <div class="input-group">
            <input
              id="fullName"
              type="text"
              v-model="fullName"
              placeholder="Full Name"
              autocomplete="name"
              class="glass-input"
            />
            <span class="input-icon" aria-hidden="true">
              <svg
                viewBox="0 0 24 24"
                fill="none"
                stroke="currentColor"
                stroke-width="1.8"
                stroke-linecap="round"
                stroke-linejoin="round"
              >
                <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"></path>
                <circle cx="12" cy="7" r="4"></circle>
              </svg>
            </span>
          </div>

          <!-- Username -->
          <div class="input-group">
            <input
              id="userName"
              type="text"
              v-model="userName"
              placeholder="Username"
              autocomplete="username"
              class="glass-input"
            />
            <span class="input-icon" aria-hidden="true">
              <svg
                viewBox="0 0 24 24"
                fill="none"
                stroke="currentColor"
                stroke-width="1.8"
                stroke-linecap="round"
                stroke-linejoin="round"
              >
                <circle cx="12" cy="12" r="4"></circle>
                <path d="M16 8v5a3 3 0 0 0 6 0v-1a10 10 0 1 0-3.92 7.94"></path>
              </svg>
            </span>
          </div>

          <!-- Email (Full Width) -->
          <div class="input-group full-width">
            <input
              id="email"
              type="email"
              v-model="email"
              placeholder="Email Address"
              autocomplete="email"
              required
              class="glass-input"
            />
            <span class="input-icon" aria-hidden="true">
              <svg
                viewBox="0 0 24 24"
                fill="none"
                stroke="currentColor"
                stroke-width="1.8"
                stroke-linecap="round"
                stroke-linejoin="round"
              >
                <path
                  d="M4 4h16c1.1 0 2 .9 2 2v12c0 1.1-.9 2-2 2H4c-1.1 0-2-.9-2-2V6c0-1.1.9-2 2-2z"
                ></path>
                <polyline points="22,6 12,13 2,6"></polyline>
              </svg>
            </span>
          </div>

          <!-- Password with toggle -->
          <div class="input-group">
            <input
              id="password"
              :type="showPassword ? 'text' : 'password'"
              v-model="password"
              placeholder="Password"
              autocomplete="new-password"
              required
              class="glass-input"
            />
            <button
              type="button"
              class="input-icon-btn"
              @click="showPassword = !showPassword"
              :aria-label="showPassword ? 'Masquer' : 'Afficher'"
            >
              <svg
                v-if="showPassword"
                viewBox="0 0 24 24"
                fill="none"
                stroke="currentColor"
                stroke-width="1.8"
                stroke-linecap="round"
                stroke-linejoin="round"
              >
                <path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"></path>
                <circle cx="12" cy="12" r="3"></circle>
              </svg>
              <svg
                v-else
                viewBox="0 0 24 24"
                fill="none"
                stroke="currentColor"
                stroke-width="1.8"
                stroke-linecap="round"
                stroke-linejoin="round"
              >
                <path
                  d="M17.94 17.94A10.07 10.07 0 0 1 12 20c-7 0-11-8-11-8a18.45 18.45 0 0 1 5.06-5.94M9.9 4.24A9.12 9.12 0 0 1 12 4c7 0 11 8 11 8a18.5 18.5 0 0 1-2.16 3.19m-6.72-1.07a3 3 0 1 1-4.24-4.24"
                ></path>
                <line x1="1" y1="1" x2="23" y2="23"></line>
              </svg>
            </button>
          </div>

          <!-- Confirm Password with toggle -->
          <div class="input-group">
            <input
              id="confirmPassword"
              :type="showConfirmPassword ? 'text' : 'password'"
              v-model="confirmPassword"
              placeholder="Confirm Password"
              autocomplete="new-password"
              required
              class="glass-input"
            />
            <button
              type="button"
              class="input-icon-btn"
              @click="showConfirmPassword = !showConfirmPassword"
              :aria-label="showConfirmPassword ? 'Masquer' : 'Afficher'"
            >
              <svg
                v-if="showConfirmPassword"
                viewBox="0 0 24 24"
                fill="none"
                stroke="currentColor"
                stroke-width="1.8"
                stroke-linecap="round"
                stroke-linejoin="round"
              >
                <path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"></path>
                <circle cx="12" cy="12" r="3"></circle>
              </svg>
              <svg
                v-else
                viewBox="0 0 24 24"
                fill="none"
                stroke="currentColor"
                stroke-width="1.8"
                stroke-linecap="round"
                stroke-linejoin="round"
              >
                <path
                  d="M17.94 17.94A10.07 10.07 0 0 1 12 20c-7 0-11-8-11-8a18.45 18.45 0 0 1 5.06-5.94M9.9 4.24A9.12 9.12 0 0 1 12 4c7 0 11 8 11 8a18.5 18.5 0 0 1-2.16 3.19m-6.72-1.07a3 3 0 1 1-4.24-4.24"
                ></path>
                <line x1="1" y1="1" x2="23" y2="23"></line>
              </svg>
            </button>
          </div>
        </div>

        <!-- Account Type Toggle -->
        <div class="role-section">
          <label class="section-label">Account Type</label>
          <div class="glass-toggle">
            <button
              type="button"
              :class="['toggle-btn', { active: role === 'CLIENT' }]"
              @click="role = 'CLIENT'"
            >
              Auditeur
            </button>
            <button
              type="button"
              :class="['toggle-btn', { active: role === 'ARTIST' }]"
              @click="role = 'ARTIST'"
            >
              Artiste
            </button>
          </div>
        </div>

        <!-- Artist Fields -->
        <template v-if="role === 'ARTIST'">
          <div class="form-grid">
            <div class="input-group">
              <input
                id="stageName"
                type="text"
                v-model="stageName"
                placeholder="Stage / Artist Name"
                class="glass-input"
              />
              <span class="input-icon" aria-hidden="true">
                <svg
                  viewBox="0 0 24 24"
                  fill="none"
                  stroke="currentColor"
                  stroke-width="1.8"
                  stroke-linecap="round"
                  stroke-linejoin="round"
                >
                  <path d="M9 18V5l12-2v13"></path>
                  <circle cx="6" cy="18" r="3"></circle>
                  <circle cx="18" cy="16" r="3"></circle>
                </svg>
              </span>
            </div>

            <div class="role-section">
              <label class="section-label">Formation</label>
              <div class="glass-toggle">
                <button
                  type="button"
                  :class="['toggle-btn', { active: artistType === 'solo' }]"
                  @click="artistType = 'solo'"
                >
                  Solo
                </button>
                <button
                  type="button"
                  :class="['toggle-btn', { active: artistType === 'group' }]"
                  @click="artistType = 'group'"
                >
                  Groupe
                </button>
              </div>
            </div>
          </div>
        </template>

        <!-- Submit Button -->
        <button type="submit" class="btn-register" :disabled="isLoading">
          <span v-if="isLoading" class="spinner"></span>
          <span v-else>Register</span>
        </button>

        <p v-if="successMessage" class="success-msg">{{ successMessage }}</p>
        <p v-if="errorMessage" class="error-msg">{{ errorMessage }}</p>
      </form>

      <!-- Footer -->
      <div class="card-footer">
        <span>Already have an account?</span>
        <router-link :to="{ name: 'login' }" class="login-link">Login</router-link>
      </div>
    </div>
  </div>
</template>

<style scoped>

.auth-page {
  max-width: 400px;
  margin: 60px auto;
  font-family: sans-serif;
}
.field {
  margin-bottom: 16px;
  display: flex;
  flex-direction: column;
}
label {
  margin-bottom: 4px;
  font-weight: bold;
}
input,
select {
  padding: 8px;
  font-size: 14px;
}
button {
  padding: 10px;
  width: 100%;
  background-color: #42b883;
  color: white;
  border: none;
  cursor: pointer;
}
.success {
  color: green;
  margin-top: 16px;
}
.error {
  color: red;
  margin-top: 16px;
}
.link {
  margin-top: 16px;
  text-align: center;

@import url('https://fonts.googleapis.com/css2?family=Plus+Jakarta+Sans:wght@400;500;600;700;800&display=swap');

.auth-wrapper {
  min-height: 100vh;
  width: 100%;
  position: relative;
  background-size: cover;
  background-position: center 25%;
  background-repeat: no-repeat;
  background-attachment: fixed;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: clamp(32px, 4vh, 80px) clamp(20px, 3vw, 60px);
  font-family:
    'Plus Jakarta Sans',
    -apple-system,
    BlinkMacSystemFont,
    sans-serif;
  box-sizing: border-box;
}

.auth-backdrop {
  position: absolute;
  inset: 0;
  background: radial-gradient(
    circle at center,
    rgba(27, 19, 64, 0.42) 0%,
    rgba(15, 10, 30, 0.74) 100%
  );
  pointer-events: none;
}

/* ── Large & Well-Proportioned Card ── */
.glass-card {
  position: relative;
  z-index: 10;
  width: 100%;
  max-width: clamp(560px, 46vw, 1150px);
  padding: clamp(48px, 4.2vw, 84px) clamp(38px, 4vw, 76px);
  background: rgba(255, 255, 255, 0.13);
  backdrop-filter: blur(28px) saturate(180%);
  -webkit-backdrop-filter: blur(28px) saturate(180%);
  border: 2px solid rgba(255, 255, 255, 0.45);
  border-radius: clamp(28px, 2.4vw, 44px);
  box-shadow:
    0 40px 80px rgba(0, 0, 0, 0.48),
    inset 0 1.5px 2px rgba(255, 255, 255, 0.5);
  color: #ffffff;
  box-sizing: border-box;
  animation: cardFadeIn 0.5s ease-out;
}

@keyframes cardFadeIn {
  from {
    opacity: 0;
    transform: translateY(16px) scale(0.98);
  }
  to {
    opacity: 1;
    transform: translateY(0) scale(1);
  }
}

.card-header {
  margin-bottom: clamp(24px, 2.2vw, 38px);
  text-align: left;
}

.card-title {
  font-size: clamp(2.4rem, 2.8vw, 3.8rem);
  font-weight: 800;
  color: #ffffff;
  margin: 0 0 clamp(8px, 1vw, 14px);
  letter-spacing: -0.02em;
  text-shadow: 0 3px 12px rgba(0, 0, 0, 0.35);
}

.card-subtitle {
  font-size: clamp(1.1rem, 1.22vw, 1.55rem);
  color: rgba(255, 255, 255, 0.88);
  margin: 0;
  line-height: 1.45;
}

.auth-form {
  display: flex;
  flex-direction: column;
  gap: clamp(18px, 1.6vw, 28px);
}

/* ── 2-Column Form Grid for Large Screens ── */
.form-grid {
  display: grid;
  grid-template-columns: 1fr;
  gap: clamp(16px, 1.4vw, 24px);
  width: 100%;
}

@media (min-width: 768px) {
  .form-grid {
    grid-template-columns: 1fr 1fr;
  }
  .full-width {
    grid-column: 1 / -1;
  }
}

.input-group {
  position: relative;
  display: flex;
  align-items: center;
  width: 100%;
}

.glass-input {
  width: 100%;
  padding: clamp(18px, 1.35vw, 26px) clamp(56px, 4vw, 70px) clamp(18px, 1.35vw, 26px)
    clamp(22px, 1.6vw, 32px);
  font-size: clamp(16px, 1.18vw, 21px);
  font-family: inherit;
  color: #ffffff;
  background: rgba(255, 255, 255, 0.1);
  border: 1.8px solid rgba(255, 255, 255, 0.42);
  border-radius: clamp(18px, 1.35vw, 24px);
  outline: none;
  box-sizing: border-box;
  transition: all 0.25s ease;
}

.glass-input::placeholder {
  color: rgba(255, 255, 255, 0.68);
}

.glass-input:focus {
  background: rgba(255, 255, 255, 0.2);
  border-color: rgba(255, 255, 255, 0.95);
  box-shadow: 0 0 28px rgba(255, 255, 255, 0.32);
}

.input-icon,
.input-icon-btn {
  position: absolute;
  right: clamp(18px, 1.4vw, 26px);
  display: flex;
  align-items: center;
  justify-content: center;
  color: rgba(255, 255, 255, 0.82);
}

.input-icon svg,
.input-icon-btn svg {
  width: clamp(22px, 1.5vw, 30px);
  height: clamp(22px, 1.5vw, 30px);
}

.input-icon-btn {
  background: none;
  border: none;
  padding: 6px;
  cursor: pointer;
  transition: color 0.15s ease;
}

.input-icon-btn:hover {
  color: #ffffff;
}

/* ── Role & Toggle Section ── */
.role-section {
  display: flex;
  flex-direction: column;
  gap: 10px;
  width: 100%;
}

.section-label {
  font-size: clamp(13px, 0.95vw, 16px);
  font-weight: 600;
  text-transform: uppercase;
  letter-spacing: 0.06em;
  color: rgba(255, 255, 255, 0.82);
  margin-left: 4px;
}

.glass-toggle {
  display: flex;
  background: rgba(0, 0, 0, 0.28);
  border: 1.5px solid rgba(255, 255, 255, 0.32);
  border-radius: clamp(16px, 1.2vw, 22px);
  padding: 5px;
  gap: 6px;
}

.toggle-btn {
  flex: 1;
  padding: clamp(13px, 1vw, 18px);
  border: none;
  background: transparent;
  color: rgba(255, 255, 255, 0.76);
  font-size: clamp(15px, 1.1vw, 19px);
  font-weight: 600;
  font-family: inherit;
  border-radius: clamp(12px, 0.9vw, 18px);
  cursor: pointer;
  transition: all 0.25s ease;
}

.toggle-btn.active {
  background: linear-gradient(135deg, #4a3b8f 0%, #e67300 100%);
  color: #ffffff;
  box-shadow: 0 6px 18px rgba(230, 115, 0, 0.42);
}

/* ── Register Button ── */
.btn-register {
  width: 100%;
  padding: clamp(18px, 1.4vw, 26px);
  font-family: inherit;
  font-size: clamp(18px, 1.3vw, 24px);
  font-weight: 700;
  border: 1.5px solid rgba(255, 255, 255, 0.32);
  border-radius: clamp(20px, 1.4vw, 26px);
  background: linear-gradient(135deg, #4a3b8f 0%, #e67300 100%);
  color: #ffffff;
  cursor: pointer;
  box-shadow: 0 14px 34px rgba(230, 115, 0, 0.42);
  transition: all 0.25s ease;
  margin-top: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.btn-register:hover:not(:disabled) {
  transform: translateY(-3px);
  box-shadow: 0 20px 42px rgba(230, 115, 0, 0.55);
  filter: brightness(1.1);
}

.btn-register:active:not(:disabled) {
  transform: translateY(0);
}

.btn-register:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.spinner {
  width: 26px;
  height: 26px;
  border: 3px solid rgba(255, 255, 255, 0.3);
  border-top-color: #ffffff;
  border-radius: 50%;
  animation: spin 0.7s linear infinite;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

.error-msg {
  background: rgba(220, 38, 38, 0.25);
  border: 1.5px solid rgba(239, 68, 68, 0.55);
  color: #ffc9c9;
  padding: clamp(14px, 1vw, 18px);
  border-radius: 16px;
  font-size: clamp(14px, 1vw, 17px);
  text-align: center;
  margin: 4px 0 0;
}

.success-msg {
  background: rgba(34, 197, 94, 0.25);
  border: 1.5px solid rgba(34, 197, 94, 0.55);
  color: #bbf7d0;
  padding: clamp(14px, 1vw, 18px);
  border-radius: 16px;
  font-size: clamp(14px, 1vw, 17px);
  text-align: center;
  margin: 4px 0 0;
}

.card-footer {
  margin-top: clamp(26px, 2.2vw, 38px);
  text-align: center;
  font-size: clamp(15px, 1.12vw, 19px);
  color: rgba(255, 255, 255, 0.85);
}

.login-link {
  color: #ffffff;
  font-weight: 700;
  text-decoration: underline;
  text-underline-offset: 5px;
  margin-left: 8px;
  transition: color 0.15s ease;
}

.login-link:hover {
  color: #ffa726;
}

@media (max-width: 480px) {
  .glass-card {
    padding: 30px 20px;
    border-radius: 24px;
  }
}}
</style>
