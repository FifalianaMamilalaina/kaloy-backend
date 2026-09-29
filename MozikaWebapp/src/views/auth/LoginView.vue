<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { login } from '@/services/AuthService'
import { useAuthStore } from '@/stores/useAuthStore'
import heroImg from '@/assets/images/hero-headphones.jpg'

const router = useRouter()
const authStore = useAuthStore()

const email = ref('')
const password = ref('')
const rememberMe = ref(false)
const showPassword = ref(false)
const errorMessage = ref('')
const isLoading = ref(false)

async function handleSubmit() {
  errorMessage.value = ''
  isLoading.value = true

  try {
    const response = await login({ email: email.value, phone: null, password: password.value })

    if (response.success && response.data) {
      authStore.setAuth(response.data)
      authStore.consumePendingAction()
      router.push({ name: 'home' })
    } else {
      errorMessage.value = response.error || 'Identifiants incorrects ou erreur de connexion.'
    }
  } catch {
    errorMessage.value = 'Une erreur est survenue lors de la connexion.'
  } finally {
    isLoading.value = false
  }
}
</script>

<template>
  <div class="auth-wrapper" :style="{ backgroundImage: `url(${heroImg})` }">
    <div class="auth-backdrop"></div>

    <!-- Floating Glassmorphic Card (Larger & Well Proportioned) -->
    <div class="glass-card">
      <div class="card-header">
        <h1 class="card-title">Login</h1>
        <p class="card-subtitle">Welcome back please login to your account</p>
      </div>

      <form @submit.prevent="handleSubmit" class="auth-form">
        <!-- Username / Email Field with user icon -->
        <div class="input-group">
          <input
            id="email"
            type="text"
            v-model="email"
            placeholder="User Name"
            autocomplete="username"
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
              <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"></path>
              <circle cx="12" cy="7" r="4"></circle>
            </svg>
          </span>
        </div>

        <!-- Password Field with eye toggle icon -->
        <div class="input-group">
          <input
            id="password"
            :type="showPassword ? 'text' : 'password'"
            v-model="password"
            placeholder="Password"
            autocomplete="current-password"
            required
            class="glass-input"
          />
          <button
            type="button"
            class="input-icon-btn"
            @click="showPassword = !showPassword"
            :aria-label="showPassword ? 'Masquer le mot de passe' : 'Afficher le mot de passe'"
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

        <!-- Remember me & Forgot password -->
        <div class="options-row">
          <label class="remember-me">
            <input type="checkbox" v-model="rememberMe" class="custom-checkbox" />
            <span class="checkbox-box">
              <svg
                viewBox="0 0 24 24"
                fill="none"
                stroke="currentColor"
                stroke-width="3"
                stroke-linecap="round"
                stroke-linejoin="round"
              >
                <polyline points="20 6 9 17 4 12"></polyline>
              </svg>
            </span>
            <span class="remember-text">Remember me</span>
          </label>
          <a href="#" class="forgot-link">Forgot password?</a>
        </div>

        <!-- Main Login Button -->
        <button type="submit" class="btn-login" :disabled="isLoading">
          <span v-if="isLoading" class="spinner"></span>
          <span v-else>Login</span>
        </button>

        <p v-if="errorMessage" class="error-msg">{{ errorMessage }}</p>
      </form>

      <!-- Footer: Don't have an account? Signup -->
      <div class="card-footer">
        <span>Don't have an account?</span>
        <router-link :to="{ name: 'register' }" class="signup-link">Signup</router-link>
      </div>

      <!-- Separator -->
      <div class="separator">
        <span class="sep-line"></span>
        <span class="sep-text">Ou</span>
        <span class="sep-line"></span>
      </div>

      <!-- Social Logins -->
      <div class="social-row">
        <button class="social-pill" aria-label="Connexion avec Google">
          <svg viewBox="0 0 24 24">
            <path
              d="M22.56 12.25c0-.78-.07-1.53-.2-2.25H12v4.26h5.92a5.06 5.06 0 0 1-2.2 3.32v2.77h3.57c2.08-1.92 3.28-4.74 3.28-8.1z"
              fill="#4285F4"
            />
            <path
              d="M12 23c2.97 0 5.46-.98 7.28-2.66l-3.57-2.77c-.98.66-2.23 1.06-3.71 1.06-2.86 0-5.29-1.93-6.16-4.53H2.18v2.84C3.99 20.53 7.7 23 12 23z"
              fill="#34A853"
            />
            <path
              d="M5.84 14.09c-.22-.66-.35-1.36-.35-2.09s.13-1.43.35-2.09V7.07H2.18C1.43 8.55 1 10.22 1 12s.43 3.45 1.18 4.93l2.85-2.22.81-.62z"
              fill="#FBBC05"
            />
            <path
              d="M12 5.38c1.62 0 3.06.56 4.21 1.64l3.15-3.15C17.45 2.09 14.97 1 12 1 7.7 1 3.99 3.47 2.18 7.07l3.66 2.84c.87-2.6 3.3-4.53 6.16-4.53z"
              fill="#EA4335"
            />
          </svg>
        </button>
        <button class="social-pill" aria-label="Connexion avec Facebook">
          <svg viewBox="0 0 24 24" fill="#1877F2">
            <path
              d="M24 12.073c0-6.627-5.373-12-12-12s-12 5.373-12 12c0 5.99 4.388 10.954 10.125 11.854v-8.385H7.078v-3.47h3.047V9.43c0-3.007 1.792-4.669 4.533-4.669 1.312 0 2.686.235 2.686.235v2.953H15.83c-1.491 0-1.956.925-1.956 1.874v2.25h3.328l-.532 3.47h-2.796v8.385C19.612 23.027 24 18.062 24 12.073z"
            />
          </svg>
        </button>
        <button class="social-pill" aria-label="Connexion avec X">
          <svg viewBox="0 0 24 24" fill="#FFFFFF">
            <path
              d="M18.244 2.25h3.308l-7.227 8.26 8.502 11.24H16.17l-5.214-6.817L4.99 21.75H1.68l7.73-8.835L1.254 2.25H8.08l4.713 6.231zm-1.161 17.52h1.833L7.084 4.126H5.117z"
            />
          </svg>
        </button>
      </div>
    </div>
  </div>
</template>

<style scoped>
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

/* ── Cinematic Backdrop Overlay ── */
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
  max-width: clamp(540px, 40vw, 980px);
  padding: clamp(50px, 4.4vw, 88px) clamp(40px, 4vw, 80px);
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

/* ── Header ── */
.card-header {
  margin-bottom: clamp(26px, 2.4vw, 42px);
  text-align: left;
}

.card-title {
  font-size: clamp(2.5rem, 3vw, 4.2rem);
  font-weight: 800;
  color: #ffffff;
  margin: 0 0 clamp(8px, 1vw, 14px);
  letter-spacing: -0.02em;
  text-shadow: 0 3px 12px rgba(0, 0, 0, 0.35);
}

.card-subtitle {
  font-size: clamp(1.1rem, 1.25vw, 1.6rem);
  color: rgba(255, 255, 255, 0.88);
  margin: 0;
  font-weight: 400;
  line-height: 1.45;
}

/* ── Form & Inputs ── */
.auth-form {
  display: flex;
  flex-direction: column;
  gap: clamp(20px, 1.8vw, 30px);
}

.input-group {
  position: relative;
  display: flex;
  align-items: center;
  width: 100%;
}

.glass-input {
  width: 100%;
  padding: clamp(18px, 1.4vw, 26px) clamp(58px, 4.2vw, 74px) clamp(18px, 1.4vw, 26px)
    clamp(22px, 1.8vw, 34px);
  font-size: clamp(16px, 1.22vw, 21.5px);
  font-family: inherit;
  color: #ffffff;
  background: rgba(255, 255, 255, 0.1);
  border: 1.8px solid rgba(255, 255, 255, 0.42);
  border-radius: clamp(18px, 1.4vw, 26px);
  outline: none;
  box-sizing: border-box;
  transition: all 0.25s ease;
}

.glass-input::placeholder {
  color: rgba(255, 255, 255, 0.68);
  font-weight: 400;
}

.glass-input:focus {
  background: rgba(255, 255, 255, 0.2);
  border-color: rgba(255, 255, 255, 0.95);
  box-shadow: 0 0 28px rgba(255, 255, 255, 0.32);
}

.input-icon,
.input-icon-btn {
  position: absolute;
  right: clamp(18px, 1.5vw, 28px);
  display: flex;
  align-items: center;
  justify-content: center;
  color: rgba(255, 255, 255, 0.82);
  transition: color 0.15s ease;
}

.input-icon svg,
.input-icon-btn svg {
  width: clamp(22px, 1.6vw, 30px);
  height: clamp(22px, 1.6vw, 30px);
}

.input-icon-btn {
  background: none;
  border: none;
  padding: 6px;
  cursor: pointer;
}

.input-icon-btn:hover {
  color: #ffffff;
}

/* ── Options Row (Remember me & Forgot password) ── */
.options-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  font-size: clamp(14.5px, 1.1vw, 19px);
  margin-top: -4px;
}

.remember-me {
  display: flex;
  align-items: center;
  gap: 12px;
  cursor: pointer;
  user-select: none;
}

.custom-checkbox {
  position: absolute;
  opacity: 0;
  cursor: pointer;
  height: 0;
  width: 0;
}

.checkbox-box {
  width: clamp(21px, 1.4vw, 28px);
  height: clamp(21px, 1.4vw, 28px);
  border-radius: clamp(6px, 0.5vw, 8px);
  border: 1.8px solid rgba(255, 255, 255, 0.58);
  background: rgba(255, 255, 255, 0.1);
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s ease;
}

.checkbox-box svg {
  width: clamp(13px, 0.9vw, 18px);
  height: clamp(13px, 0.9vw, 18px);
  opacity: 0;
  transform: scale(0.6);
  transition: all 0.15s ease;
  color: #ffffff;
}

.custom-checkbox:checked ~ .checkbox-box {
  background: #e67300;
  border-color: #e67300;
}

.custom-checkbox:checked ~ .checkbox-box svg {
  opacity: 1;
  transform: scale(1);
}

.remember-text {
  color: rgba(255, 255, 255, 0.92);
  font-weight: 500;
}

.forgot-link {
  color: rgba(255, 255, 255, 0.8);
  text-decoration: none;
  font-weight: 500;
  transition: color 0.15s ease;
}

.forgot-link:hover {
  color: #ffffff;
  text-decoration: underline;
}

/* ── Login Button ── */
.btn-login {
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
  margin-top: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.btn-login:hover:not(:disabled) {
  transform: translateY(-3px);
  box-shadow: 0 20px 42px rgba(230, 115, 0, 0.55);
  filter: brightness(1.1);
}

.btn-login:active:not(:disabled) {
  transform: translateY(0);
}

.btn-login:disabled {
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

/* ── Footer ── */
.card-footer {
  margin-top: clamp(26px, 2.2vw, 38px);
  text-align: center;
  font-size: clamp(15px, 1.12vw, 19px);
  color: rgba(255, 255, 255, 0.85);
}

.signup-link {
  color: #ffffff;
  font-weight: 700;
  text-decoration: underline;
  text-underline-offset: 5px;
  margin-left: 8px;
  transition: color 0.15s ease;
}

.signup-link:hover {
  color: #ffa726;
}

/* ── Separator ── */
.separator {
  display: flex;
  align-items: center;
  gap: 18px;
  margin: clamp(26px, 2.2vw, 38px) 0 clamp(20px, 1.8vw, 30px);
}

.sep-line {
  flex: 1;
  height: 1.8px;
  background: rgba(255, 255, 255, 0.28);
}

.sep-text {
  font-size: clamp(14.5px, 1.1vw, 18px);
  color: rgba(255, 255, 255, 0.75);
  font-weight: 500;
}

/* ── Social Row ── */
.social-row {
  display: flex;
  justify-content: center;
  gap: clamp(16px, 1.4vw, 24px);
}

.social-pill {
  width: clamp(72px, 5.5vw, 110px);
  height: clamp(52px, 3.8vw, 76px);
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(255, 255, 255, 0.14);
  border: 1.8px solid rgba(255, 255, 255, 0.4);
  border-radius: clamp(18px, 1.3vw, 24px);
  cursor: pointer;
  transition: all 0.25s ease;
}

.social-pill svg {
  width: clamp(24px, 1.6vw, 32px);
  height: clamp(24px, 1.6vw, 32px);
}

.social-pill:hover {
  background: rgba(255, 255, 255, 0.28);
  border-color: rgba(255, 255, 255, 0.8);
  transform: translateY(-2px);
}

/* ── Mobile adjustments ── */
@media (max-width: 480px) {
  .glass-card {
    padding: 30px 24px;
    border-radius: 24px;
  }
}
</style>
