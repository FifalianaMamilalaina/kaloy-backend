<template>
  <div class="mz-profile-card">
    <div class="profile-header-banner"></div>
    <div class="profile-content">
      <div class="profile-avatar-row">
        <div class="avatar-wrapper">
          <img v-if="photoUrl" :src="photoUrl" :alt="name" class="profile-avatar-img" />
          <div v-else class="profile-avatar-fallback">{{ initials }}</div>
          <span v-if="isCertified" class="certified-badge" title="Artiste Certifié">
            <svg width="14" height="14" viewBox="0 0 24 24" fill="currentColor">
              <path
                d="M12 2l3.09 6.26L22 9.27l-5 4.87 1.18 6.88L12 17.77l-6.18 3.25L7 14.14 2 9.27l6.91-1.01L12 2z"
              ></path>
            </svg>
          </span>
        </div>

        <button
          v-if="showFollowBtn"
          :class="['mz-btn', isFollowing ? 'mz-btn-outline is-active' : 'mz-btn-primary']"
          @click="$emit('toggle-follow')"
        >
          <svg
            v-if="!isFollowing"
            width="14"
            height="14"
            viewBox="0 0 24 24"
            fill="none"
            stroke="currentColor"
            stroke-width="2.5"
          >
            <line x1="12" y1="5" x2="12" y2="19"></line>
            <line x1="5" y1="12" x2="19" y2="12"></line>
          </svg>
          <span>{{ isFollowing ? 'Abonné' : 'Suivre' }}</span>
        </button>
      </div>

      <div class="profile-info-block">
        <div class="name-row">
          <h2 class="profile-name">{{ name }}</h2>
          <span v-if="roleTag" class="mz-badge mz-badge-primary">{{ roleTag }}</span>
        </div>
        <p v-if="username" class="profile-username">@{{ username }}</p>
        <p v-if="bio" class="profile-bio">{{ bio }}</p>
      </div>

      <!-- Statistiques groupées : Abonnés, Abonnements, Publications/Morceaux -->
      <div class="profile-stats-bar">
        <div class="stat-item">
          <span class="stat-count">{{ followersCount ?? 0 }}</span>
          <span class="stat-label">Abonnés</span>
        </div>
        <div class="stat-divider"></div>
        <div class="stat-item">
          <span class="stat-count">{{ followingCount ?? 0 }}</span>
          <span class="stat-label">Abonnements</span>
        </div>
        <div v-if="tracksCount !== undefined" class="stat-divider"></div>
        <div v-if="tracksCount !== undefined" class="stat-item">
          <span class="stat-count">{{ tracksCount }}</span>
          <span class="stat-label">Morceaux</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'

const props = withDefaults(
  defineProps<{
    name: string
    username?: string
    photoUrl?: string
    bio?: string
    roleTag?: string
    isCertified?: boolean
    followersCount?: number
    followingCount?: number
    tracksCount?: number
    isFollowing?: boolean
    showFollowBtn?: boolean
  }>(),
  {
    username: '',
    photoUrl: '',
    bio: '',
    roleTag: '',
    isCertified: false,
    followersCount: 0,
    followingCount: 0,
    tracksCount: undefined,
    isFollowing: false,
    showFollowBtn: true,
  },
)

defineEmits<{
  (e: 'toggle-follow'): void
}>()

const initials = computed(() => {
  if (!props.name) return '?'
  const parts = props.name.trim().split(/\s+/)
  return parts.length > 1
    ? (parts[0][0] + parts[1][0]).toUpperCase()
    : parts[0].slice(0, 2).toUpperCase()
})
</script>

<style scoped>
.mz-profile-card {
  background: var(--mz-surface-light);
  border: 1px solid var(--mz-border-subtle-light);
  border-radius: var(--mz-radius-xl);
  overflow: hidden;
  box-shadow: var(--mz-shadow-sm);
  transition: box-shadow 0.2s ease;
}
.mz-profile-card:hover {
  box-shadow: var(--mz-shadow-md);
}

.profile-header-banner {
  height: 90px;
  background: linear-gradient(135deg, #1b1340 0%, #3d2880 60%, #e67300 100%);
}

.profile-content {
  padding: 0 var(--mz-space-6) var(--mz-space-6);
  position: relative;
}

.profile-avatar-row {
  display: flex;
  align-items: flex-end;
  justify-content: space-between;
  margin-top: -42px;
  margin-bottom: var(--mz-space-4);
}

.avatar-wrapper {
  position: relative;
  width: 84px;
  height: 84px;
  border-radius: 50%;
  border: 3.5px solid #ffffff;
  box-shadow: var(--mz-shadow-sm);
  background: #ffffff;
}

.profile-avatar-img {
  width: 100%;
  height: 100%;
  border-radius: 50%;
  object-fit: cover;
}

.profile-avatar-fallback {
  width: 100%;
  height: 100%;
  border-radius: 50%;
  background: linear-gradient(135deg, #1b1340 0%, #e67300 100%);
  color: #ffffff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  font-weight: 800;
}

.certified-badge {
  position: absolute;
  bottom: 0;
  right: 0;
  background: #3b82f6;
  color: #ffffff;
  width: 22px;
  height: 22px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  border: 2px solid #ffffff;
}

.profile-info-block {
  margin-bottom: var(--mz-space-5);
}

.name-row {
  display: flex;
  align-items: center;
  gap: var(--mz-space-2);
  flex-wrap: wrap;
}

.profile-name {
  font-size: 20px;
  font-weight: 800;
  color: var(--mz-text-on-light-high);
  margin: 0;
  line-height: 1.2;
}

.profile-username {
  font-size: 13.5px;
  font-weight: 600;
  color: var(--mz-text-on-light-muted);
  margin: 2px 0 8px;
}

.profile-bio {
  font-size: 14.5px;
  line-height: 1.55;
  color: var(--mz-text-on-light-medium);
  margin: 0;
}

.profile-stats-bar {
  display: flex;
  align-items: center;
  background: var(--mz-surface-light-soft);
  border: 1px solid var(--mz-border-subtle-light);
  border-radius: var(--mz-radius-lg);
  padding: 12px 16px;
}

.stat-item {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.stat-count {
  font-size: 18px;
  font-weight: 800;
  color: var(--mz-text-on-light-high);
  line-height: 1;
}

.stat-label {
  font-size: 11.5px;
  font-weight: 600;
  color: var(--mz-text-on-light-muted);
  text-transform: uppercase;
  letter-spacing: 0.04em;
  margin-top: 4px;
}

.stat-divider {
  width: 1px;
  height: 28px;
  background: var(--mz-border-subtle-light);
}
</style>
