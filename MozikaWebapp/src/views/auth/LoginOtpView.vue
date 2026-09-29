<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { sendOtp, verifyOtp } from '@/services/AuthService'
import heroImg from '@/assets/images/hero-headphones.jpg'

const route = useRoute()
const router = useRouter()

const otpToken = ref((route.query.otpToken as string) || '')
const code = ref('')
const errorMessage = ref('')
const successMessage = ref('')
const isLoading = ref(false)

const secondsUntilExpiration = ref(0)
const secondsUntilResend = ref(0)
let expirationInterval: ReturnType<typeof setInterval> | undefined
let resendInterval: ReturnType<typeof setInterval> | undefined

function startExpirationCountdown(expiresAtIso: string) {
  clearInterval(expirationInterval)
  const expiresAt = new Date(expiresAtIso).getTime()

  expirationInterval = setInterval(() => {
    const remaining = Math.max(0, Math.floor((expiresAt - Date.now()) / 1000))
    secondsUntilExpiration.value = remaining
    if (remaining === 0) clearInterval(expirationInterval)
  }, 1000)
}

function startResendCountdown() {
  clearInterval(resendInterval)
  secondsUntilResend.value = 60

  resendInterval = setInterval(() => {
    secondsUntilResend.value = Math.max(0, secondsUntilResend.value - 1)
    if (secondsUntilResend.value === 0) clearInterval(resendInterval)
  }, 1000)
}

async function sendCode() {
  errorMessage.value = ''
  successMessage.value = ''

  const response = await sendOtp(otpToken.value)

  if (response.success && response.data) {
    otpToken.value = response.data.otpToken
    successMessage.value = `Code envoyé (${response.data.destination})`
    startExpirationCountdown(response.data.expiresAt)
    startResendCountdown()
  } else {
    errorMessage.value = response.error || 'Une erreur est survenue.'
  }
}

async function handleVerify() {
  errorMessage.value = ''
  successMessage.value = ''
  isLoading.value = true

  try {
    const response = await verifyOtp(otpToken.value, code.value)

    if (response.success && response.data) {
      successMessage.value = `${response.data.message}`
      setTimeout(() => router.push({ name: 'home' }), 1200)
    } else {
      errorMessage.value = response.error || 'Code invalide ou expiré.'
    }
  } catch {
    errorMessage.value = 'Erreur lors de la vérification.'
  } finally {
    isLoading.value = false
  }
}

onMounted(() => {
  if (route.query.expiresAt) {
    startExpirationCountdown(route.query.expiresAt as string)
    startResendCountdown()
  } else {
    sendCode()
  }
})

onUnmounted(() => {
  clearInterval(expirationInterval)
  clearInterval(resendInterval)
})
</script>

<template>
  <div class="auth-wrapper" :style="{ backgroundImage: `url(${heroImg})` }">
    <div class="auth-backdrop"></div>

    <!-- Floating Glassmorphic Card (Grand & Well Proportioned) -->
    <div class="glass-card">
      <div class="card-header">
        <div class="shield-badge">
          <svg
            viewBox="0 0 24 24"
            fill="none"
            stroke="currentColor"
            stroke-width="2"
            stroke-linecap="round"
            stroke-linejoin="round"
          >
            <path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z"></path>
          </svg>
        </div>
        <h1 class="card-title">Vérification</h1>
        <p class="card-subtitle">Entrez le code de sécurité reçu pour confirmer votre compte.</p>
      </div>

      <form @submit.prevent="handleVerify" class="auth-form">
        <!-- OTP Code Input -->
        <div class="input-group">
          <input
            id="code"
            type="text"
            v-model="code"
            placeholder="Code à 6 chiffres"
            maxlength="6"
            required
            class="glass-input code-input"
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
              <rect x="3" y="11" width="18" height="11" rx="2" ry="2"></rect>
              <path d="M7 11V7a5 5 0 0 1 10 0v4"></path>
            </svg>
          </span>
        </div>

        <!-- Timer / Expiration info -->
        <div class="timer-row">
          <span v-if="secondsUntilExpiration > 0" class="timer-badge">
            Expire dans {{ secondsUntilExpiration }}s
          </span>
          <button
            type="button"
            class="resend-btn"
            :disabled="secondsUntilResend > 0"
            @click="sendCode"
          >
            {{ secondsUntilResend > 0 ? `Renvoyer (${secondsUntilResend}s)` : 'Renvoyer le code' }}
          </button>
        </div>

        <!-- Verify button -->
        <button type="submit" class="btn-verify" :disabled="isLoading">
          <span v-if="isLoading" class="spinner"></span>
          <span v-else>Valider le code</span>
        </button>

        <p v-if="successMessage" class="success-msg">{{ successMessage }}</p>
        <p v-if="errorMessage" class="error-msg">{{ errorMessage }}</p>
      </form>

      <div class="card-footer">
        <router-link :to="{ name: 'login' }" class="back-link">
          ← Retour à la connexion
        </router-link>
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
  text-align: center;
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

.shield-badge {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: clamp(60px, 4.5vw, 84px);
  height: clamp(60px, 4.5vw, 84px);
  border-radius: clamp(18px, 1.3vw, 24px);
  background: rgba(255, 255, 255, 0.15);
  border: 1.8px solid rgba(255, 255, 255, 0.4);
  color: #ffa726;
  margin-bottom: clamp(20px, 1.8vw, 32px);
}

.shield-badge svg {
  width: clamp(28px, 2.2vw, 40px);
  height: clamp(28px, 2.2vw, 40px);
}

.card-header {
  margin-bottom: clamp(30px, 2.6vw, 44px);
}

.card-title {
  font-size: clamp(2.4rem, 2.8vw, 3.8rem);
  font-weight: 800;
  color: #ffffff;
  margin: 0 0 clamp(8px, 1vw, 14px);
  letter-spacing: -0.02em;
  text-shadow: 0 4px 16px rgba(0, 0, 0, 0.38);
}

.card-subtitle {
  font-size: clamp(1.1rem, 1.25vw, 1.6rem);
  color: rgba(255, 255, 255, 0.88);
  margin: 0;
  line-height: 1.45;
}

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

.code-input {
  text-align: center;
  letter-spacing: 0.35em;
  font-size: clamp(1.8rem, 2.2vw, 2.8rem);
  font-weight: 800;
}

.glass-input::placeholder {
  color: rgba(255, 255, 255, 0.68);
  font-size: clamp(15px, 1.1vw, 19px);
  letter-spacing: normal;
  font-weight: 400;
}

.glass-input:focus {
  background: rgba(255, 255, 255, 0.2);
  border-color: rgba(255, 255, 255, 0.95);
  box-shadow: 0 0 28px rgba(255, 255, 255, 0.32);
}

.input-icon {
  position: absolute;
  right: clamp(18px, 1.5vw, 28px);
  display: flex;
  align-items: center;
  justify-content: center;
  color: rgba(255, 255, 255, 0.82);
}

.input-icon svg {
  width: clamp(22px, 1.6vw, 30px);
  height: clamp(22px, 1.6vw, 30px);
}

.timer-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: clamp(14.5px, 1.1vw, 18px);
  padding: 0 6px;
}

.timer-badge {
  color: rgba(255, 255, 255, 0.78);
}

.resend-btn {
  background: none;
  border: none;
  color: #ffa726;
  font-weight: 600;
  cursor: pointer;
  font-family: inherit;
  font-size: clamp(14.5px, 1.1vw, 18px);
  transition: opacity 0.15s ease;
}

.resend-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.btn-verify {
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

.btn-verify:hover:not(:disabled) {
  transform: translateY(-3px);
  box-shadow: 0 20px 42px rgba(230, 115, 0, 0.55);
  filter: brightness(1.1);
}

.btn-verify:active:not(:disabled) {
  transform: translateY(0);
}

.btn-verify:disabled {
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
  margin-top: clamp(28px, 2.4vw, 42px);
}

.back-link {
  color: rgba(255, 255, 255, 0.88);
  text-decoration: none;
  font-size: clamp(15px, 1.1vw, 18px);
  font-weight: 500;
  transition: color 0.15s ease;
}

.back-link:hover {
  color: #ffffff;
  text-decoration: underline;
}

@media (max-width: 480px) {
  .glass-card {
    padding: 30px 22px;
    border-radius: 24px;
  }
}
</style>
