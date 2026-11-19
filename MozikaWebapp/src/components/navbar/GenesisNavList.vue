<template>
  <ul class="mz-nav-list">
    <li v-for="item in items" :key="item.navTitle" class="mz-nav-list-item">
      <!-- Lien simple -->
      <router-link v-if="item.navLink" :to="item.navLink" class="mz-nav-link">
        <span class="mz-nav-link-dot"></span>
        <span class="mz-nav-link-text">{{ $t(item.navTitle) }}</span>
      </router-link>

      <!-- Groupe avec sous-éléments -->
      <details
        v-if="item.navChilds"
        :open="item.navChilds?.some((child) => child.navLink === $route.path)"
        class="mz-nav-details"
      >
        <summary class="mz-nav-summary">
          <span class="mz-nav-link-dot"></span>
          <span class="mz-nav-link-text">{{ $t(item.navTitle) }}</span>
          <svg
            class="mz-nav-chevron"
            width="10"
            height="10"
            viewBox="0 0 24 24"
            fill="none"
            stroke="currentColor"
            stroke-width="2.5"
          >
            <polyline points="6 9 12 15 18 9"></polyline>
          </svg>
        </summary>
        <div class="mz-nav-children">
          <GenesisNavList :items="item.navChilds" />
        </div>
      </details>
    </li>
  </ul>
</template>

<script setup lang="ts">
import { useRoute } from 'vue-router'

interface NavItem {
  navTitle: string
  navLink?: string
  navChilds?: NavItem[]
}

defineProps<{
  items: NavItem[]
}>()

const $route = useRoute()
</script>

<style scoped>
.mz-nav-list {
  list-style: none;
  margin: 0;
  padding: 0;
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.mz-nav-list-item {
  display: flex;
  flex-direction: column;
}

/* Lien simple */
.mz-nav-link {
  display: flex;
  align-items: center;
  gap: var(--mz-space-2);
  padding: 8px var(--mz-space-3);
  border-radius: var(--mz-radius-sm);
  color: var(--mz-text-on-dark-medium);
  text-decoration: none;
  font-size: var(--mz-font-size-sm);
  font-weight: 500;
  transition: all 0.15s ease;
  position: relative;
}

.mz-nav-link:hover {
  background: rgba(255, 255, 255, 0.06);
  color: var(--mz-text-on-dark-high);
}

.mz-nav-link.router-link-active {
  background: var(--mz-brand-accent-light);
  color: var(--mz-brand-accent);
  font-weight: 700;
}

.mz-nav-link.router-link-active::before {
  content: '';
  position: absolute;
  left: 0;
  top: 50%;
  transform: translateY(-50%);
  width: 3px;
  height: 55%;
  background: var(--mz-brand-accent);
  border-radius: 0 3px 3px 0;
}

/* Puce de lien */
.mz-nav-link-dot {
  width: 5px;
  height: 5px;
  border-radius: 50%;
  background: currentColor;
  opacity: 0.4;
  flex-shrink: 0;
}

.mz-nav-link.router-link-active .mz-nav-link-dot {
  opacity: 1;
  background: var(--mz-brand-accent);
}

.mz-nav-link-text {
  flex: 1;
}

/* Details / Summary */
.mz-nav-details {
  display: flex;
  flex-direction: column;
}

.mz-nav-summary {
  display: flex;
  align-items: center;
  gap: var(--mz-space-2);
  padding: 8px var(--mz-space-3);
  border-radius: var(--mz-radius-sm);
  color: var(--mz-text-on-dark-medium);
  font-size: var(--mz-font-size-sm);
  font-weight: 500;
  cursor: pointer;
  list-style: none;
  transition: all 0.15s ease;
}

.mz-nav-summary:hover {
  background: rgba(255, 255, 255, 0.06);
  color: var(--mz-text-on-dark-high);
}

.mz-nav-summary::-webkit-details-marker {
  display: none;
}

.mz-nav-chevron {
  margin-left: auto;
  transition: transform 0.2s ease;
  color: var(--mz-text-on-dark-muted);
}

details[open] .mz-nav-chevron {
  transform: rotate(180deg);
}

.mz-nav-children {
  padding-left: var(--mz-space-4);
  margin-top: 2px;
}
</style>
