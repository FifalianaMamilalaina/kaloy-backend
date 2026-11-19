<template>
  <div class="mz-card mz-card-interactive user-card">
    <!-- Header : Avatar + Infos d'identification -->
    <div class="user-card-header">
      <div class="user-avatar">
        <span>{{ initials }}</span>
      </div>
      <div class="user-meta">
        <h3 class="user-display-name" :title="userData['email'] ?? 'Utilisateur'">
          {{ username }}
        </h3>
        <span class="user-email" :title="userData['email']">{{ userData['email'] ?? '--' }}</span>
      </div>
      <span v-if="userData['roleidUserRoles']" class="mz-badge mz-badge-accent user-role-badge">
        {{ userData['roleidUserRoles'].getKeyValue?.() ?? userData['roleidUserRoles'] }}
      </span>
    </div>

    <!-- Statuts et tags rapides -->
    <div class="user-tags">
      <span v-if="userData['statusidUserStatuses']" class="mz-badge mz-badge-success">
        {{ userData['statusidUserStatuses'].getKeyValue?.() ?? userData['statusidUserStatuses'] }}
      </span>
      <span
        v-if="userData['emailVerifiedAt']"
        class="mz-badge mz-badge-primary"
        title="Email vérifié"
      >
        Email ✓
      </span>
      <span
        v-if="userData['phoneVerifiedAt']"
        class="mz-badge mz-badge-primary"
        title="Téléphone vérifié"
      >
        Tél ✓
      </span>
    </div>

    <!-- Champs configurés -->
    <div class="user-fields">
      <template v-for="field in User.getAllSearchFieldsMetadata()" :key="field.key">
        <div
          v-if="
            field.showInTable &&
            visibleFields.includes(field.key) &&
            !['email', 'passwordHash'].includes(field.key)
          "
          class="user-field-row"
        >
          <span class="field-label">{{ field.label }}</span>
          <span class="field-value">
            <template v-if="field.type === 'date'">
              {{ formatDate(userData[field.key]) }}
            </template>
            <template v-else-if="field.type === 'number'">
              {{ userData[field.key] != null ? $n(userData[field.key], 'decimal') : '--' }}
            </template>
            <template v-else-if="field.type === 'select'">
              {{
                userData[field.key] != null
                  ? (userData[field.key].getKeyValue?.() ?? userData[field.key])
                  : '--'
              }}
            </template>
            <template v-else-if="field.type === 'file'">
              <img
                v-if="userData[field.key]"
                :src="getUrl(userData[field.key])"
                :alt="field.key"
                class="field-img"
              />
            </template>
            <template v-else>
              {{ userData[field.key] ?? '--' }}
            </template>
          </span>
        </div>
      </template>
    </div>

    <!-- Actions en bas de carte -->
    <div class="user-card-footer">
      <EntityRowActions
        :view="viewAction"
        :remove="removeAction"
        :edit="editAction"
        @delete-row="deleteRow"
        @view-row="viewRow"
        @edit-row="updateRow"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, type PropType } from 'vue'
import { User } from '@/models/UserModel'
import EntityRowActions from '@/components/table/EntityRowActions.vue'
import { useDateFormat } from '@/composables/useDateFormat'

const props = defineProps({
  user: {
    type: Object as PropType<User>,
    required: true,
  },
  viewAction: {
    type: Boolean,
    default: true,
  },
  editAction: {
    type: Boolean,
    default: true,
  },
  removeAction: {
    type: Boolean,
    default: true,
  },
  visibleFields: {
    type: Array as PropType<string[]>,
    default: () => [],
  },
})

const userData = computed(() => {
  return props.user as Record<string, any>
})

const username = computed(() => {
  const email = userData.value['email']
  if (!email) return `User #${userData.value['id'] ?? '?'}`
  return email.split('@')[0]
})

const initials = computed(() => {
  const name = username.value
  return name.slice(0, 2).toUpperCase()
})

const emit = defineEmits<{
  (e: 'request-delete', user: User): void
  (e: 'request-view', user: User): void
  (e: 'request-update', user: User): void
}>()

const deleteRow = () => emit('request-delete', props.user)
const viewRow = () => emit('request-view', props.user)
const updateRow = () => emit('request-update', props.user)
const { formatDate } = useDateFormat()

const getUrl = (val: string) => val
</script>

<style scoped>
.user-card {
  display: flex;
  flex-direction: column;
  height: 100%;
  padding: var(--mz-space-4);
  gap: var(--mz-space-3);
  transition:
    transform var(--mz-transition-base),
    box-shadow var(--mz-transition-base);
}

.user-card:hover {
  transform: translateY(-2px);
  box-shadow: var(--mz-shadow-card-hover);
}

.user-card-header {
  display: flex;
  align-items: center;
  gap: var(--mz-space-3);
  padding-bottom: var(--mz-space-3);
  border-bottom: 1px solid var(--mz-border-subtle-light);
}

.user-avatar {
  width: 44px;
  height: 44px;
  border-radius: var(--mz-radius-full);
  background: linear-gradient(135deg, var(--mz-color-primary) 0%, var(--mz-color-secondary) 100%);
  color: #ffffff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 700;
  font-size: var(--mz-font-size-sm);
  flex-shrink: 0;
  box-shadow: 0 2px 8px rgba(139, 92, 246, 0.25);
}

.user-meta {
  display: flex;
  flex-direction: column;
  min-width: 0;
  flex: 1;
}

.user-display-name {
  font-size: var(--mz-font-size-base);
  font-weight: 700;
  color: var(--mz-text-on-light-high);
  margin: 0;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.user-email {
  font-size: 11px;
  color: var(--mz-text-on-light-muted);
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.user-role-badge {
  flex-shrink: 0;
}

.user-tags {
  display: flex;
  gap: var(--mz-space-2);
  flex-wrap: wrap;
}

.user-fields {
  display: flex;
  flex-direction: column;
  gap: 6px;
  flex: 1;
  font-size: var(--mz-font-size-xs);
}

.user-field-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: var(--mz-space-2);
  padding: 4px 0;
  border-bottom: 1px solid var(--mz-border-subtle-light);
}

.user-field-row:last-child {
  border-bottom: none;
}

.field-label {
  font-weight: 600;
  text-transform: uppercase;
  letter-spacing: 0.04em;
  color: var(--mz-text-on-light-muted);
  font-size: 10px;
}

.field-value {
  color: var(--mz-text-on-light-high);
  font-weight: 500;
  text-align: right;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  max-width: 60%;
}

.field-img {
  max-height: 28px;
  max-width: 28px;
  border-radius: var(--mz-radius-sm);
  object-fit: cover;
}

.user-card-footer {
  display: flex;
  justify-content: flex-end;
  padding-top: var(--mz-space-2);
  border-top: 1px solid var(--mz-border-subtle-light);
  margin-top: auto;
}
</style>
