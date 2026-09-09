<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { sendOtp, verifyOtp } from '@/services/AuthService'

const route = useRoute()
const router = useRouter()

const otpToken = ref((route.query.otpToken as string) || '')
const code = ref('')
const errorMessage = ref('')
const successMessage = ref('')

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

  const response = await verifyOtp(otpToken.value, code.value)

  if (response.success && response.data) {
    successMessage.value = `${response.data.message} (statut : ${response.data.status})`
    setTimeout(() => router.push({ name: 'home' }), 1500)
  } else {
    errorMessage.value = response.error || 'Une erreur est survenue.'
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
  <div class="auth-page">
    <h1>Vérification du compte</h1>
    <p>Un code à 6 chiffres a été envoyé pour confirmer votre compte.</p>

    <div class="field">
      <label for="code">Code de vérification</label>
      <input id="code" type="text" v-model="code" maxlength="6" placeholder="123456" />
    </div>

    <p v-if="secondsUntilExpiration > 0" class="info">
      Ce code expire dans {{ Math.floor(secondsUntilExpiration / 60) }} min
      {{ secondsUntilExpiration % 60 }} s
    </p>
    <p v-else-if="secondsUntilExpiration === 0" class="error">
      Le code a expiré, demandez-en un nouveau.
    </p>

    <button @click="handleVerify">Valider le code</button>

    <button class="resend" :disabled="secondsUntilResend > 0" @click="sendCode">
      {{ secondsUntilResend > 0 ? `Renvoyer (${secondsUntilResend}s)` : 'Renvoyer le code' }}
    </button>

    <p v-if="successMessage" class="success">{{ successMessage }}</p>
    <p v-if="errorMessage" class="error">{{ errorMessage }}</p>
  </div>
</template>

<style scoped>
.auth-page {
  max-width: 400px;
  margin: 60px auto;
  font-family: sans-serif;
}
.field {
  margin: 16px 0;
  display: flex;
  flex-direction: column;
}
label {
  margin-bottom: 4px;
  font-weight: bold;
}
input {
  padding: 8px;
  font-size: 18px;
  letter-spacing: 4px;
  text-align: center;
}
button {
  display: block;
  width: 100%;
  padding: 10px;
  margin-top: 10px;
  cursor: pointer;
  border: none;
}
button:first-of-type {
  background-color: #42b883;
  color: white;
}
.resend {
  background-color: #eee;
  color: #333;
}
.resend:disabled {
  color: #999;
  cursor: not-allowed;
}
.info {
  color: #555;
}
.error {
  color: red;
}
.success {
  color: green;
}
</style>
