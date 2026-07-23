<script setup>
import { Check } from '@lucide/vue'
import { RouterView, useRoute } from 'vue-router'
import SiteFooter from './components/SiteFooter.vue'
import SiteHeader from './components/SiteHeader.vue'
import { toast } from './state'

const currentRoute = useRoute()
</script>

<template>
  <div class="site-shell route-site">
    <SiteHeader v-if="!currentRoute.meta.standalone" />
    <main class="route-main" :class="{ 'standalone-main': currentRoute.meta.standalone }">
      <RouterView v-slot="{ Component, route }">
        <Transition name="route-page" mode="out-in">
          <component :is="Component" :key="route.fullPath" />
        </Transition>
      </RouterView>
    </main>
    <SiteFooter v-if="!currentRoute.meta.standalone" />

    <Transition name="toast">
      <div v-if="toast" class="toast-message"><Check :size="17" />{{ toast }}</div>
    </Transition>
  </div>
</template>
