import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

export const useAuthStore = defineStore('auth', () => {
  const token = ref<string | null>(localStorage.getItem('token'))
  const userId = ref<string | null>(localStorage.getItem('userId'))
  const role = ref<string | null>(localStorage.getItem('role'))
  const pendingAction = ref<(() => void) | null>(null)

  const isAuthenticated = computed(() => !!token.value)

  function setAuth(data: { token: string; userId: number; role: string }) {
    token.value = data.token
    userId.value = String(data.userId)
    role.value = data.role

    localStorage.setItem('token', data.token)
    localStorage.setItem('userId', String(data.userId))
    localStorage.setItem('role', data.role)
  }

  function logout() {
    token.value = null
    userId.value = null
    role.value = null

    localStorage.removeItem('token')
    localStorage.removeItem('userId')
    localStorage.removeItem('role')
  }

  function requireAuth(action: () => void) {
    if (isAuthenticated.value) {
      action()
      return
    }
    pendingAction.value = action
  }

  function consumePendingAction() {
    if (pendingAction.value) {
      const action = pendingAction.value
      pendingAction.value = null
      action()
    }
  }

  return {
    token,
    userId,
    role,
    isAuthenticated,
    setAuth,
    logout,
    pendingAction,
    requireAuth,
    consumePendingAction,
  }
})
