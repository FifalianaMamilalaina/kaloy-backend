<template>
  <nav class="navbar bg-base-200/40 border-b border-base-content/10 shadow-sm">
    <div class="flex items-center justify-between w-full px-4">
      <div class="flex items-center gap-4 flex-1">
        <router-link to="/" class="flex items-center gap-2" @click="closeDropdown">
          <AppLogo size="60" />
          <h1 class="font-semibold uppercase tracking-wide text-base hidden md:block">
            {{ $t('projectName') }}
          </h1>
        </router-link>
      </div>

      <div class="flex items-center justify-end gap-2">
        <ul class="menu menu-horizontal flex-nowrap p-0 flex items-center gap-2">
          <li v-for="(section, index) in navigations" :key="section.sectionName" class="relative">
            <details
              class="dropdown-bottom dropdown-end lg:dropdown-start"
              :open="openSectionIndex === index"
              @toggle="handleDropdownToggle(index, $event)"
            >
              <summary class="btn btn-ghost opacity-80 flex items-center gap-2">
                <font-awesome-icon v-if="section.icon" :icon="section.icon" />
                <span class="hidden lg:inline">{{ $t(section.sectionName) }}</span>
              </summary>

              <ul class="p-2 menu dropdown-content bg-base-200 rounded-box shadow-md min-w-52 z-3">
                <GenesisTopNavList :items="section.navChilds" @close="closeDropdown" />
              </ul>
            </details>
          </li>
        </ul>
        <div class="h-6 border-l border-base-content/10 mx-2"></div>

        <LanguageSwitcherButton />

        <router-link
          to="/home"
          class="btn btn-ghost tooltip tooltip-bottom"
          :data-tip="$t('navbar.home')"
          @click="closeDropdown"
        >
          <HomeIcon class="w-5 h-5" />
        </router-link>

        <router-link
          to="/settings"
          class="btn btn-ghost tooltip tooltip-bottom"
          :data-tip="$t('navbar.settings')"
          @click="closeDropdown"
        >
          <GearIcon class="w-5 h-5" />
        </router-link>
      </div>
    </div>
  </nav>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import GenesisTopNavList from './GenesisTopNavList.vue'
import navigations from '@/config/navigations.ts'
import HomeIcon from '@/components/icons/HomeIcon.vue'
import GearIcon from '@/components/icons/GearIcon.vue'
import AppLogo from '@/components/AppLogo.vue'
import LanguageSwitcherButton from '@/components/switcher/LanguageSwitcherButton.vue'

const openSectionIndex = ref<number | null>(null)

// Gère l'ouverture d'un seul dropdown à la fois
const handleDropdownToggle = (index: number, event: Event) => {
  const detailsElement = event.currentTarget as HTMLDetailsElement

  if (detailsElement.open) {
    openSectionIndex.value = index
  } else if (openSectionIndex.value === index) {
    openSectionIndex.value = null
  }
}

const closeDropdown = () => {
  openSectionIndex.value = null
}
</script>

<style scoped>
/* Le style reste inchangé */
.router-link-active {
  font-weight: bold;
  color: var(--color-primary);
  position: relative;
}

.router-link-active:not(.btn)::after {
  /* :not(.btn) pour exclure les boutons (icônes) */
  content: '';
  position: absolute;
  bottom: -4px;
  left: 0;
  width: 100%;
  height: 2px;
  background-color: var(--color-primary);
  border-radius: 4px;
}
</style>
