<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { me, changeContact } from '@/services/AuthService'

const router = useRouter()

const currentEmail = ref('')
const currentStatus = ref('')
const newEmail = ref('')
const errorMessage = ref('')
const successMessage = ref('')

async function loadProfile() {
  const response = await me()
  if (response.success && response.data) {
    currentEmail.value = response.data.email
    currentStatus.value = response.data.status
  } else {
    errorMessage.value = 'Impossible de charger votre profil.'
  }
}

async function handleChangeContact() {
  errorMessage.value = ''
  successMessage.value = ''

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
}

onMounted(() => {
  loadProfile()
})
</script>

<template>
  <div class="account-page">
    <h1>Mon compte</h1>

    <div class="current-info">
      <p><strong>Email actuel :</strong> {{ currentEmail }}</p>
      <p><strong>Statut :</strong> {{ currentStatus }}</p>
    </div>

    <h2>Changer d'email</h2>
    <form @submit.prevent="handleChangeContact">
      <div class="field">
        <label for="newEmail">Nouvel email</label>
        <input id="newEmail" type="email" v-model="newEmail" required />
      </div>
      <button type="submit">Envoyer le code de vérification</button>
    </form>

    <p v-if="successMessage" class="success">{{ successMessage }}</p>
    <p v-if="errorMessage" class="error">{{ errorMessage }}</p>
  </div>
</template>

<style scoped>
.account-page {
  max-width: 500px;
  margin: 20px 0;
  font-family: sans-serif;
}
.current-info {
  background-color: #f5f5f5;
  color: #333;
  padding: 12px;
  border-radius: 4px;
  margin-bottom: 24px;
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
input {
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
</style>
