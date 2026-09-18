import { createRouter, createWebHistory } from 'vue-router'
import routes from './routes'
import { useFreezeScreenStore } from '@/stores/useFreezeScreenStore'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes,
})

router.beforeEach((to, from, next) => {
  const loading = useFreezeScreenStore()
  loading.freeze('Loading')
  next()
})

router.afterEach(() => {
  const loading = useFreezeScreenStore()
  setTimeout(() => loading.unfreeze(), 150) // small delay to prevent flicker
})

export default router
