<template>
  <nav class="top-navbar" :class="{ 'on-hero': isHeroPage }">
    <div class="navbar-inner">
      <!-- Left: Brand Logo -->
      <div class="brand-section">
        <router-link to="/home" class="brand-link" @click="closeDropdown">
          <AppLogo size="42" />
          <span class="brand-name">Mozika</span>
        </router-link>
      </div>

      <!-- Center: 6 Modules Horizontal Navigation with Clickable Dropdowns -->
      <div class="nav-links-section">
        <router-link to="/home" class="nav-item home-link" @click="closeDropdown">
          <HomeIcon class="nav-icon" />
          <span>Accueil</span>
        </router-link>

        <div
          v-for="(section, index) in navigations"
          :key="section.sectionName"
          class="nav-dropdown-wrapper"
          @mouseenter="onMouseEnter(index)"
          @mouseleave="onMouseLeave"
        >
          <!-- Trigger Button (Click toggles dropdown open/close) -->
          <button
            type="button"
            class="nav-item dropdown-toggle"
            :class="{ active: openSectionIndex === index || isSectionActive(section) }"
            @click="toggleDropdown(index, $event)"
            :aria-expanded="openSectionIndex === index"
          >
            <font-awesome-icon v-if="section.icon" :icon="section.icon" class="nav-icon" />
            <span>{{ section.sectionName }}</span>
            <svg
              class="chevron-icon"
              :class="{ rotated: openSectionIndex === index }"
              width="12"
              height="12"
              viewBox="0 0 24 24"
              fill="none"
              stroke="currentColor"
              stroke-width="2.5"
            >
              <polyline points="6 9 12 15 18 9"></polyline>
            </svg>
          </button>

          <!-- Dropdown Submenu Container -->
          <transition name="dropdown-anim">
            <div
              v-show="openSectionIndex === index"
              class="dropdown-menu"
              :class="{ 'align-right': index >= 3 }"
            >
              <div class="dropdown-header">
                <span class="dropdown-header-title">{{ section.sectionName }}</span>
              </div>
              <div class="dropdown-divider"></div>

              <!-- List of sub-links for this module -->
              <router-link
                v-for="child in section.navChilds"
                :key="child.navTitle"
                :to="child.navLink"
                class="dropdown-link"
                @click="closeDropdown"
              >
                <span class="link-arrow">
                  <svg
                    width="12"
                    height="12"
                    viewBox="0 0 24 24"
                    fill="none"
                    stroke="currentColor"
                    stroke-width="2.5"
                  >
                    <polyline points="9 18 15 12 9 6"></polyline>
                  </svg>
                </span>
                <span class="link-text">{{ child.navTitle }}</span>
              </router-link>
            </div>
          </transition>
        </div>
      </div>

      <!-- Right: User Auth State & Settings -->
      <div class="right-section">
        <template v-if="authStore.isAuthenticated">
          <router-link
            :to="{ name: 'account' }"
            class="user-pill"
            title="Mon compte"
            @click="closeDropdown"
          >
            <span class="avatar-circle">
              <svg
                width="15"
                height="15"
                viewBox="0 0 24 24"
                fill="none"
                stroke="currentColor"
                stroke-width="2"
              >
                <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"></path>
                <circle cx="12" cy="7" r="4"></circle>
              </svg>
            </span>
            <span class="user-role">{{ userRoleShort }}</span>
          </router-link>

          <button @click="handleLogout" class="btn-icon-logout" title="Déconnexion">
            <svg
              width="17"
              height="17"
              viewBox="0 0 24 24"
              fill="none"
              stroke="currentColor"
              stroke-width="2"
            >
              <path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4"></path>
              <polyline points="16 17 21 12 16 7"></polyline>
              <line x1="21" y1="12" x2="9" y2="12"></line>
            </svg>
          </button>
        </template>
        <template v-else>
          <router-link :to="{ name: 'login' }" class="auth-btn login-btn" @click="closeDropdown">
            Connexion
          </router-link>
          <router-link
            :to="{ name: 'register' }"
            class="auth-btn register-btn"
            @click="closeDropdown"
          >
            S'inscrire
          </router-link>
        </template>

        <router-link
          to="/settings"
          class="btn-icon-settings"
          title="Paramètres"
          @click="closeDropdown"
        >
          <GearIcon class="w-5 h-5" />
        </router-link>
      </div>
    </div>
  </nav>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useAuthStore } from '@/stores/useAuthStore'
import navigations from '@/config/navigations.ts'
import HomeIcon from '@/components/icons/HomeIcon.vue'
import GearIcon from '@/components/icons/GearIcon.vue'
import AppLogo from '@/components/AppLogo.vue'

const router = useRouter()
const route = useRoute()
const authStore = useAuthStore()

const openSectionIndex = ref<number | null>(null)
let hoverTimeout: any = null

// Pages qui ont l'image de fond : la barre y devient transparente (effet verre).
// Pour ajouter une page, ajoute simplement le nom de sa route ici.
const heroRoutes = ['home', 'account']
const isHeroPage = computed(() => heroRoutes.includes(route.name as string))

const userRoleShort = computed(() => {
  if (!authStore.role) return 'Compte'
  if (authStore.role === 'CLIENT') return 'Client'
  if (authStore.role === 'ARTIST') return 'Artiste'
  if (authStore.role === 'ADMIN') return 'Admin'
  return authStore.role
})

// Toggle on click
function toggleDropdown(index: number, event: MouseEvent) {
  event.stopPropagation()
  if (hoverTimeout) clearTimeout(hoverTimeout)
  openSectionIndex.value = openSectionIndex.value === index ? null : index
}

// Hover handlers with debounce to prevent accidental closes
function onMouseEnter(index: number) {
  if (hoverTimeout) clearTimeout(hoverTimeout)
  openSectionIndex.value = index
}

function onMouseLeave() {
  if (hoverTimeout) clearTimeout(hoverTimeout)
  hoverTimeout = setTimeout(() => {
    openSectionIndex.value = null
  }, 220)
}

function closeDropdown() {
  if (hoverTimeout) clearTimeout(hoverTimeout)
  openSectionIndex.value = null
}

function isSectionActive(section: { navChilds: { navLink: string }[] }) {
  return section.navChilds.some((c) => c.navLink === route.path)
}

function handleLogout() {
  authStore.logout()
  closeDropdown()
  router.push({ name: 'home' })
}

// Click outside handler
function onDocumentClick(e: MouseEvent) {
  const target = e.target as HTMLElement
  if (!target.closest('.nav-dropdown-wrapper')) {
    openSectionIndex.value = null
  }
}

onMounted(() => {
  document.addEventListener('click', onDocumentClick)
})

onUnmounted(() => {
  document.removeEventListener('click', onDocumentClick)
  if (hoverTimeout) clearTimeout(hoverTimeout)
})
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Plus+Jakarta+Sans:wght@500;600;700;800&display=swap');

/* ╔════════════════════════════════════════════════════════════╗
   ║  --nu = unité de la barre (grandit avec la largeur d'écran) ║
   ║  Plus gros : 0.065vw   |   Plus petit : 0.045vw             ║
   ╚════════════════════════════════════════════════════════════╝ */
.top-navbar {
  --nu: max(1px, 0.052vw);
  position: sticky;
  top: 0;
  z-index: 9999;
  width: 100%;
  background: rgba(255, 255, 255, 0.97);
  backdrop-filter: blur(calc(14 * var(--nu)));
  -webkit-backdrop-filter: blur(calc(14 * var(--nu)));
  border-bottom: calc(1 * var(--nu)) solid #e2e8f0;
  box-shadow: 0 calc(4 * var(--nu)) calc(18 * var(--nu)) rgba(27, 19, 64, 0.05);
  font-family: 'Plus Jakarta Sans', sans-serif;
  overflow: visible;
}

.navbar-inner {
  width: 100%;
  max-width: none;
  margin: 0;
  padding: 0 4vw;
  box-sizing: border-box;
  height: calc(68 * var(--nu));
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: calc(16 * var(--nu));
  overflow: visible;
}

/* ── Brand ── */
.brand-section {
  display: flex;
  align-items: center;
  flex-shrink: 0;
}

.brand-link {
  display: flex;
  align-items: center;
  gap: calc(10 * var(--nu));
  text-decoration: none;
}

.brand-link :deep(svg),
.brand-link :deep(img) {
  width: calc(42 * var(--nu));
  height: calc(42 * var(--nu));
}

.brand-name {
  font-size: calc(22 * var(--nu));
  font-weight: 800;
  letter-spacing: 0.04em;
  background: linear-gradient(135deg, #1b1340 0%, #e67300 100%);
  -webkit-background-clip: text;
  background-clip: text;
  color: transparent;
  text-transform: uppercase;
}

/* ── Center Nav Links ── */
.nav-links-section {
  display: flex;
  align-items: center;
  gap: calc(6 * var(--nu));
  flex: 1;
  justify-content: center;
  overflow: visible; /* CRITICAL: must be visible so dropdowns never get clipped */
}

.nav-dropdown-wrapper {
  position: relative;
}

.nav-item {
  display: inline-flex;
  align-items: center;
  gap: calc(7 * var(--nu));
  padding: calc(8 * var(--nu)) calc(14 * var(--nu));
  border-radius: calc(12 * var(--nu));
  font-size: calc(13.5 * var(--nu));
  font-weight: 600;
  color: #475569;
  text-decoration: none;
  background: transparent;
  border: none;
  cursor: pointer;
  transition: all 0.2s ease;
  white-space: nowrap;
}

.nav-item:hover,
.nav-item.active {
  color: #1b1340;
  background: #f1f5f9;
}

.home-link.router-link-active {
  color: #e67300;
  background: rgba(230, 115, 0, 0.08);
}

.nav-icon {
  font-size: calc(14 * var(--nu));
  color: #64748b;
  transition: color 0.2s ease;
}

.home-link .nav-icon {
  width: 1em;
  height: 1em;
}

.nav-item:hover .nav-icon,
.nav-item.active .nav-icon {
  color: #e67300;
}

.chevron-icon {
  width: calc(12 * var(--nu));
  height: calc(12 * var(--nu));
  flex-shrink: 0;
  transition: transform 0.2s ease;
  color: #94a3b8;
}

.chevron-icon.rotated {
  transform: rotate(180deg);
  color: #e67300;
}

/* ── Dropdown Menu Card ── */
.dropdown-menu {
  position: absolute;
  top: calc(100% + calc(8 * var(--nu)));
  left: 0;
  min-width: calc(230 * var(--nu));
  background: #ffffff;
  border: 1px solid #e2e8f0;
  border-radius: calc(16 * var(--nu));
  box-shadow:
    0 calc(18 * var(--nu)) calc(40 * var(--nu)) rgba(27, 19, 64, 0.14),
    0 calc(2 * var(--nu)) calc(6 * var(--nu)) rgba(0, 0, 0, 0.04);
  padding: calc(10 * var(--nu));
  display: flex;
  flex-direction: column;
  gap: calc(2 * var(--nu));
  z-index: 10000;
}

/* Invisible hover-bridge preventing premature mouseleave */
.dropdown-menu::before {
  content: '';
  position: absolute;
  top: calc(-10 * var(--nu));
  left: 0;
  right: 0;
  height: calc(10 * var(--nu));
  background: transparent;
}

.dropdown-menu.align-right {
  left: auto;
  right: 0;
}

.dropdown-header {
  padding: calc(6 * var(--nu)) calc(12 * var(--nu)) calc(2 * var(--nu));
}

.dropdown-header-title {
  font-size: calc(11 * var(--nu));
  font-weight: 800;
  text-transform: uppercase;
  letter-spacing: 0.06em;
  color: #94a3b8;
}

.dropdown-divider {
  height: calc(1 * var(--nu));
  background: #f1f5f9;
  margin: calc(4 * var(--nu)) calc(6 * var(--nu)) calc(6 * var(--nu));
}

.dropdown-link {
  display: flex;
  align-items: center;
  gap: calc(10 * var(--nu));
  padding: calc(10 * var(--nu)) calc(14 * var(--nu));
  border-radius: calc(10 * var(--nu));
  font-size: calc(13.5 * var(--nu));
  font-weight: 600;
  color: #334155;
  text-decoration: none;
  transition: all 0.15s ease;
}

.link-arrow {
  color: #cbd5e1;
  display: flex;
  align-items: center;
  transition:
    transform 0.15s ease,
    color 0.15s ease;
}

.link-arrow svg {
  width: calc(12 * var(--nu));
  height: calc(12 * var(--nu));
}

.dropdown-link:hover {
  background: #fdfaf6;
  color: #e67300;
  transform: translateX(calc(3 * var(--nu)));
}

.dropdown-link:hover .link-arrow {
  color: #e67300;
  transform: translateX(calc(2 * var(--nu)));
}

.dropdown-link.router-link-active {
  background: rgba(230, 115, 0, 0.08);
  color: #e67300;
  font-weight: 700;
}

.dropdown-link.router-link-active .link-arrow {
  color: #e67300;
}

/* Dropdown Animation */
.dropdown-anim-enter-active,
.dropdown-anim-leave-active {
  transition:
    opacity 0.18s ease,
    transform 0.18s ease;
}

.dropdown-anim-enter-from,
.dropdown-anim-leave-to {
  opacity: 0;
  transform: translateY(calc(-6 * var(--nu)));
}

/* ── Right Section ── */
.right-section {
  display: flex;
  align-items: center;
  gap: calc(10 * var(--nu));
  flex-shrink: 0;
}

.user-pill {
  display: inline-flex;
  align-items: center;
  gap: calc(8 * var(--nu));
  padding: calc(5 * var(--nu)) calc(12 * var(--nu)) calc(5 * var(--nu)) calc(6 * var(--nu));
  border-radius: calc(999 * var(--nu));
  background: #f1f5f9;
  border: 1px solid #e2e8f0;
  text-decoration: none;
  color: #1e293b;
  font-size: calc(13 * var(--nu));
  font-weight: 700;
  transition: all 0.2s ease;
}

.user-pill:hover {
  background: #e2e8f0;
  border-color: #cbd5e1;
}

.avatar-circle {
  width: calc(28 * var(--nu));
  height: calc(28 * var(--nu));
  border-radius: 50%;
  background: linear-gradient(135deg, #1b1340 0%, #e67300 100%);
  color: #ffffff;
  display: flex;
  align-items: center;
  justify-content: center;
}

.avatar-circle svg {
  width: calc(15 * var(--nu));
  height: calc(15 * var(--nu));
}

.btn-icon-logout,
.btn-icon-settings {
  width: calc(36 * var(--nu));
  height: calc(36 * var(--nu));
  border-radius: calc(10 * var(--nu));
  display: flex;
  align-items: center;
  justify-content: center;
  color: #64748b;
  background: transparent;
  border: none;
  cursor: pointer;
  transition: all 0.2s ease;
  text-decoration: none;
}

.btn-icon-logout svg {
  width: calc(17 * var(--nu));
  height: calc(17 * var(--nu));
}

.btn-icon-settings :deep(svg) {
  width: calc(20 * var(--nu));
  height: calc(20 * var(--nu));
}

.btn-icon-logout:hover {
  background: #fee2e2;
  color: #ef4444;
}

.btn-icon-settings:hover {
  background: #f1f5f9;
  color: #1e293b;
}

.auth-btn {
  padding: calc(7 * var(--nu)) calc(14 * var(--nu));
  font-size: calc(13.5 * var(--nu));
  font-weight: 600;
  border-radius: calc(10 * var(--nu));
  text-decoration: none;
  transition: all 0.2s ease;
}

.login-btn {
  color: #1b1340;
  background: #f1f5f9;
}

.login-btn:hover {
  background: #e2e8f0;
}

.register-btn {
  background: linear-gradient(135deg, #1b1340 0%, #e67300 100%);
  color: #ffffff;
}

.register-btn:hover {
  filter: brightness(1.1);
  transform: translateY(calc(-1 * var(--nu)));
}

@media (max-width: 992px) {
  .nav-item span {
    display: none;
  }
}

/* ══════════════════════════════════════════════════════════
   Variante "hero" : barre transparente (verre sombre) sur les
   pages qui ont l'image de fond. Le contenu remonte sous la barre.
   ══════════════════════════════════════════════════════════ */
.top-navbar.on-hero {
  margin-bottom: calc(-72 * var(--nu));
  background: rgba(15, 10, 35, 0.42);
  backdrop-filter: blur(calc(16 * var(--nu)));
  -webkit-backdrop-filter: blur(calc(16 * var(--nu)));
  border-bottom: 1px solid rgba(255, 255, 255, 0.14);
  box-shadow: none;
}

.on-hero .brand-name {
  background: linear-gradient(135deg, #ffffff 0%, #ffa24d 100%);
  -webkit-background-clip: text;
  background-clip: text;
}

.on-hero .brand-link :deep(svg),
.on-hero .brand-link :deep(img) {
  filter: brightness(0) invert(1);
}

.on-hero .nav-item {
  color: rgba(255, 255, 255, 0.88);
}

.on-hero .nav-item:hover,
.on-hero .nav-item.active {
  color: #ffffff;
  background: rgba(255, 255, 255, 0.14);
}

.on-hero .home-link.router-link-active {
  color: #ffa24d;
  background: rgba(255, 255, 255, 0.12);
}

.on-hero .nav-icon {
  color: rgba(255, 255, 255, 0.72);
}

.on-hero .nav-item:hover .nav-icon,
.on-hero .nav-item.active .nav-icon {
  color: #ffa24d;
}

.on-hero .chevron-icon {
  color: rgba(255, 255, 255, 0.6);
}

.on-hero .chevron-icon.rotated {
  color: #ffa24d;
}

.on-hero .btn-icon-settings,
.on-hero .btn-icon-logout {
  color: rgba(255, 255, 255, 0.82);
}

.on-hero .btn-icon-settings:hover {
  background: rgba(255, 255, 255, 0.14);
  color: #ffffff;
}

.on-hero .btn-icon-logout:hover {
  background: rgba(239, 68, 68, 0.28);
  color: #fecaca;
}
</style>
