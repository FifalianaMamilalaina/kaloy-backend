<template>
  <LogoIcon :src="logo" alt="Mozika Logo" :width="size" />
</template>

<script setup lang="ts">
import LogoIcon from '@/components/icons/LogoIcon.vue'
import logo from '@/assets/images/logo.png'

defineProps({
  size: {
    type: String,
    default: '60',
  },
})
</script>
