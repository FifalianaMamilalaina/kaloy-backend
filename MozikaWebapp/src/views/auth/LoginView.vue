<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { login } from '@/services/AuthService'
import { useAuthStore } from '@/stores/useAuthStore'

const router = useRouter()
const authStore = useAuthStore()

const email = ref('')
const password = ref('')
const errorMessage = ref('')

async function handleSubmit() {
  errorMessage.value = ''

  const response = await login({ email: email.value, phone: null, password: password.value })

  if (response.success && response.data) {
    authStore.setAuth(response.data)
    authStore.consumePendingAction()
    router.push({ name: 'home' })
  } else {
    errorMessage.value = response.error || 'Une erreur est survenue.'
  }
}
</script>

<template>
  <div class="auth-page">
    <h1>Connexion</h1>

    <form @submit.prevent="handleSubmit">
      <div class="field">
        <label for="email">Email</label>
        <input id="email" type="email" v-model="email" required />
      </div>

      <div class="field">
        <label for="password">Mot de passe</label>
        <input id="password" type="password" v-model="password" required />
      </div>

      <button type="submit">Se connecter</button>
    </form>

    <p v-if="errorMessage" class="error">{{ errorMessage }}</p>

    <p class="link">
      Pas encore de compte ? <router-link :to="{ name: 'register' }">S'inscrire</router-link>
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
.error {
  color: red;
  margin-top: 16px;
}
.link {
  margin-top: 16px;
  text-align: center;
}
</style>
