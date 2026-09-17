import { defineStore } from 'pinia'
import { useLanguageStore } from '@/stores/useLanguageStore.ts'
import { useThemeStore } from '@/stores/useThemeStore.ts'
import { useLayoutStore } from '@/stores/useLayoutStore.ts'

export const useGlobalStore = defineStore('global', () => {
  useLanguageStore()
  useThemeStore()
  useLayoutStore()
  return {}
})
