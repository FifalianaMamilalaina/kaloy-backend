<template>
  <aside class="mz-sidebar">
    <!-- Brand -->
    <router-link to="/" class="mz-sidebar-brand">
      <AppLogo size="44" />
      <span class="brand-name">{{ $t('projectName') }}</span>
    </router-link>

    <!-- Navigation -->
    <div class="mz-sidebar-body">
      <nav class="mz-sidebar-nav">
        <!-- Accueil -->
        <div class="nav-item-wrap">
          <router-link to="/home" class="mz-nav-link" title="Home">
            <span class="mz-nav-link-icon"><HomeIcon /></span>
            <span class="mz-nav-link-text">{{ $t('navbar.home') }}</span>
          </router-link>
        </div>

        <!-- Paramètres -->
        <div class="nav-item-wrap">
          <router-link to="/settings" class="mz-nav-link" title="Settings">
            <span class="mz-nav-link-icon"><GearIcon /></span>
            <span class="mz-nav-link-text">{{ $t('navbar.settings') }}</span>
          </router-link>
        </div>

        <!-- Sections dynamiques -->
        <template v-for="section in navigations" :key="section.sectionName">
          <div class="mz-nav-section-title">
            <font-awesome-icon
              v-if="section.icon"
              :icon="section.icon"
              class="mz-nav-section-icon"
            />
            {{ $t(section.sectionName) }}
          </div>
          <GenesisNavList :items="section.navChilds" />
        </template>
      </nav>
    </div>

    <!-- Bas de sidebar -->
    <div class="mz-sidebar-footer"></div>
  </aside>
</template>

<script setup lang="ts">
import GenesisNavList from './GenesisNavList.vue'
import navigations from '@/config/navigations.ts'
import HomeIcon from '@/components/icons/HomeIcon.vue'
import GearIcon from '@/components/icons/GearIcon.vue'
import AppLogo from '@/components/AppLogo.vue'
</script>

<style scoped>
/* ── Sidebar container ── */
.mz-sidebar {
  min-width: 270px;
  max-width: 270px;
  background: var(--mz-brand-dark-surface);
  border-right: 1px solid var(--mz-border-subtle-dark);
  display: flex;
  flex-direction: column;
  height: 100vh;
  position: sticky;
  top: 0;
  overflow: hidden;
}

/* ── Brand ── */
.mz-sidebar-brand {
  display: flex;
  align-items: center;
  gap: var(--mz-space-3);
  padding: var(--mz-space-5) var(--mz-space-5);
  text-decoration: none;
  border-bottom: 1px solid var(--mz-border-subtle-dark);
  flex-shrink: 0;
}

.brand-name {
  font-size: var(--mz-font-size-md);
  font-weight: 800;
  letter-spacing: 0.06em;
  text-transform: uppercase;
  background: linear-gradient(135deg, #ffffff 30%, #ffa24d 100%);
  -webkit-background-clip: text;
  background-clip: text;
  color: transparent;
}

/* ── Body scrollable ── */
.mz-sidebar-body {
  flex: 1;
  overflow-y: auto;
  padding: var(--mz-space-4) var(--mz-space-3);
}

.mz-sidebar-body::-webkit-scrollbar {
  width: 4px;
}
.mz-sidebar-body::-webkit-scrollbar-track {
  background: transparent;
}
.mz-sidebar-body::-webkit-scrollbar-thumb {
  background: var(--mz-border-subtle-dark);
  border-radius: 99px;
}

/* ── Navigation ── */
.mz-sidebar-nav {
  display: flex;
  flex-direction: column;
  gap: var(--mz-space-1);
}

/* Section title */
.mz-nav-section-title {
  display: flex;
  align-items: center;
  gap: var(--mz-space-2);
  font-size: var(--mz-font-size-xs);
  font-weight: 700;
  text-transform: uppercase;
  letter-spacing: 0.07em;
  color: var(--mz-text-on-dark-muted);
  padding: var(--mz-space-4) var(--mz-space-3) var(--mz-space-2);
  margin-top: var(--mz-space-2);
}

.mz-nav-section-icon {
  font-size: 10px;
  opacity: 0.7;
}

/* Nav item wrapper */
.nav-item-wrap {
  display: flex;
}

/* Nav link */
.mz-nav-link {
  display: flex;
  align-items: center;
  gap: var(--mz-space-3);
  padding: 9px var(--mz-space-3);
  border-radius: var(--mz-radius-sm);
  color: var(--mz-text-on-dark-medium);
  text-decoration: none;
  font-size: var(--mz-font-size-sm);
  font-weight: 600;
  width: 100%;
  transition: all 0.18s ease;
}

.mz-nav-link:hover {
  background: rgba(255, 255, 255, 0.07);
  color: var(--mz-text-on-dark-high);
}

.mz-nav-link.router-link-active {
  background: var(--mz-brand-accent-light);
  color: var(--mz-brand-accent);
  font-weight: 700;
  position: relative;
}

.mz-nav-link.router-link-active::before {
  content: '';
  position: absolute;
  left: 0;
  top: 50%;
  transform: translateY(-50%);
  width: 3px;
  height: 60%;
  background: var(--mz-brand-accent);
  border-radius: 0 4px 4px 0;
}

.mz-nav-link-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 20px;
  flex-shrink: 0;
  opacity: 0.8;
}

.mz-nav-link-text {
  flex: 1;
}

/* ── Footer ── */
.mz-sidebar-footer {
  border-top: 1px solid var(--mz-border-subtle-dark);
  padding: var(--mz-space-4) var(--mz-space-5);
  min-height: 20px;
}
</style>
