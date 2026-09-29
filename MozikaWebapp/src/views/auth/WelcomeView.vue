<script setup lang="ts">
import { useRouter } from 'vue-router'
import heroImg from '@/assets/images/hero-headphones.jpg'

const router = useRouter()

function goExplore() {
  router.push({ name: 'home' })
}

function goRegister() {
  router.push({ name: 'register' })
}

function goLogin() {
  router.push({ name: 'login' })
}
</script>

<template>
  <div class="auth-wrapper" :style="{ backgroundImage: `url(${heroImg})` }">
    <div class="auth-backdrop"></div>

    <!-- Floating Glassmorphic Card (Grand & Well Proportioned) -->
    <div class="glass-card">
      <div class="card-header">
        <div class="music-badge">
          <svg
            viewBox="0 0 24 24"
            fill="none"
            stroke="currentColor"
            stroke-width="2"
            stroke-linecap="round"
            stroke-linejoin="round"
          >
            <path d="M9 18V5l12-2v13"></path>
            <circle cx="6" cy="18" r="3"></circle>
            <circle cx="18" cy="16" r="3"></circle>
          </svg>
          <span>Mozika</span>
        </div>
        <h1 class="card-title">Bienvenue sur l'application, c'est parti !</h1>
        <p class="card-subtitle">
          Explorez, écoutez et partagez les meilleurs morceaux et artistes.
        </p>
      </div>

      <div class="action-buttons">
        <button class="btn-primary" @click="goExplore">
          <span>Explorer</span>
          <svg
            viewBox="0 0 24 24"
            fill="none"
            stroke="currentColor"
            stroke-width="2.4"
            stroke-linecap="round"
            stroke-linejoin="round"
          >
            <line x1="5" y1="12" x2="19" y2="12"></line>
            <polyline points="12 5 19 12 12 19"></polyline>
          </svg>
        </button>

        <button class="btn-secondary" @click="goRegister">
          <span>S'inscrire avec e-mail</span>
        </button>
      </div>

      <!-- Separator -->
      <div class="separator">
        <span class="sep-line"></span>
        <span class="sep-text">Ou</span>
        <span class="sep-line"></span>
      </div>

      <!-- Social Logins -->
      <div class="social-row">
        <button class="social-pill" aria-label="Google">
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
        <button class="social-pill" aria-label="Facebook">
          <svg viewBox="0 0 24 24" fill="#1877F2">
            <path
              d="M24 12.073c0-6.627-5.373-12-12-12s-12 5.373-12 12c0 5.99 4.388 10.954 10.125 11.854v-8.385H7.078v-3.47h3.047V9.43c0-3.007 1.792-4.669 4.533-4.669 1.312 0 2.686.235 2.686.235v2.953H15.83c-1.491 0-1.956.925-1.956 1.874v2.25h3.328l-.532 3.47h-2.796v8.385C19.612 23.027 24 18.062 24 12.073z"
            />
          </svg>
        </button>
        <button class="social-pill" aria-label="X">
          <svg viewBox="0 0 24 24" fill="#FFFFFF">
            <path
              d="M18.244 2.25h3.308l-7.227 8.26 8.502 11.24H16.17l-5.214-6.817L4.99 21.75H1.68l7.73-8.835L1.254 2.25H8.08l4.713 6.231zm-1.161 17.52h1.833L7.084 4.126H5.117z"
            />
          </svg>
        </button>
      </div>

      <!-- Footer -->
      <div class="card-footer">
        <span>Vous avez déjà un compte ?</span>
        <a href="#" @click.prevent="goLogin" class="login-link">Se connecter</a>
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
  max-width: clamp(560px, 42vw, 980px);
  padding: clamp(52px, 4.4vw, 88px) clamp(40px, 4vw, 80px);
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

.music-badge {
  display: inline-flex;
  align-items: center;
  gap: 12px;
  padding: clamp(10px, 0.9vw, 16px) clamp(20px, 1.5vw, 30px);
  background: rgba(255, 255, 255, 0.15);
  border: 1.5px solid rgba(255, 255, 255, 0.4);
  border-radius: 999px;
  font-size: clamp(14.5px, 1.05vw, 18px);
  font-weight: 700;
  letter-spacing: 0.06em;
  color: #ffa726;
  margin-bottom: clamp(20px, 1.8vw, 32px);
}

.music-badge svg {
  width: clamp(20px, 1.4vw, 26px);
  height: clamp(20px, 1.4vw, 26px);
}

.card-header {
  margin-bottom: clamp(32px, 2.8vw, 48px);
}

.card-title {
  font-size: clamp(2.3rem, 2.7vw, 3.8rem);
  font-weight: 800;
  color: #ffffff;
  margin: 0 0 clamp(12px, 1.2vw, 20px);
  line-height: 1.25;
  letter-spacing: -0.02em;
  text-shadow: 0 4px 16px rgba(0, 0, 0, 0.38);
}

.card-subtitle {
  font-size: clamp(1.1rem, 1.25vw, 1.6rem);
  color: rgba(255, 255, 255, 0.88);
  margin: 0;
  line-height: 1.5;
}

.action-buttons {
  display: flex;
  flex-direction: column;
  gap: clamp(16px, 1.4vw, 24px);
}

.btn-primary {
  width: 100%;
  padding: clamp(18px, 1.4vw, 26px) clamp(28px, 2.5vw, 44px);
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
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
}

.btn-primary svg {
  width: clamp(20px, 1.4vw, 26px);
  height: clamp(20px, 1.4vw, 26px);
}

.btn-primary:hover {
  transform: translateY(-3px);
  box-shadow: 0 20px 42px rgba(230, 115, 0, 0.55);
  filter: brightness(1.1);
}

.btn-secondary {
  width: 100%;
  padding: clamp(17px, 1.3vw, 24px) clamp(28px, 2.5vw, 44px);
  font-family: inherit;
  font-size: clamp(17px, 1.22vw, 22px);
  font-weight: 600;
  border: 1.8px solid rgba(255, 255, 255, 0.45);
  border-radius: clamp(20px, 1.4vw, 26px);
  background: rgba(255, 255, 255, 0.12);
  color: #ffffff;
  cursor: pointer;
  transition: all 0.25s ease;
}

.btn-secondary:hover {
  background: rgba(255, 255, 255, 0.24);
  border-color: rgba(255, 255, 255, 0.85);
  transform: translateY(-3px);
}

/* ── Separator ── */
.separator {
  display: flex;
  align-items: center;
  gap: 18px;
  margin: clamp(28px, 2.4vw, 42px) 0 clamp(22px, 1.8vw, 32px);
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

/* ── Footer ── */
.card-footer {
  margin-top: clamp(28px, 2.4vw, 42px);
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
    padding: 30px 22px;
    border-radius: 24px;
  }
}
</style>
