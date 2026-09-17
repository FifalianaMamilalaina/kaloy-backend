<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { register } from '@/services/AuthService'
import type { RegisterPayload } from '@/models/auth/AuthModels'

const router = useRouter()

const email = ref('')
const password = ref('')
const role = ref<'CLIENT' | 'ARTIST'>('CLIENT')
const artistType = ref<'solo' | 'group'>('solo')
const stageName = ref('')

const successMessage = ref('')
const errorMessage = ref('')

async function handleSubmit() {
  successMessage.value = ''
  errorMessage.value = ''

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
}
</style>
