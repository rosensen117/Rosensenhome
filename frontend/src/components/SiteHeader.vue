<script setup>
import { Bell, Compass, Menu, PenLine, UserRound, X } from '@lucide/vue'
import { ref } from 'vue'
import { RouterLink, useRoute } from 'vue-router'
import { isLoggedIn, showToast } from '../state'

const mobileOpen = ref(false)
const route = useRoute()

const navItems = [
  { label: '首页', to: '/' },
  { label: '寻物大厅', to: '/hall' },
  { label: '线索动态', to: '/clues' },
  { label: '认领指南', to: '/guide' },
]

function isActive(to) {
  if (to === '/') return route.path === '/'
  return route.path.startsWith(to)
}
</script>

<template>
  <header class="topbar route-topbar">
    <RouterLink to="/" class="brand" aria-label="拾光首页">
      <span class="brand-mark"><Compass :size="19" /></span>
      <span class="brand-name">拾光</span>
      <span class="brand-divider"></span>
      <span class="brand-sub">校园失物招领</span>
    </RouterLink>

    <nav class="desktop-nav" aria-label="主导航">
      <RouterLink v-for="item in navItems" :key="item.to" :to="item.to" :class="{ active: isActive(item.to) }">{{ item.label }}</RouterLink>
    </nav>

    <div class="top-actions">
      <button class="icon-button notification-button" aria-label="查看通知" @click="showToast('你有 3 条新的匹配提醒')">
        <Bell :size="19" /><span class="notification-dot">3</span>
      </button>
      <RouterLink v-if="isLoggedIn" to="/profile" class="login-button profile-link" :class="{ active: isActive('/profile') }"><UserRound :size="18" /><span>个人中心</span></RouterLink>
      <RouterLink v-else to="/login" class="login-button profile-link" :class="{ active: isActive('/login') || isActive('/register') }"><UserRound :size="18" /><span>登录</span></RouterLink>
      <RouterLink to="/publish" class="publish-button"><PenLine :size="17" /><span>发布信息</span></RouterLink>
      <button class="mobile-menu-button" aria-label="打开菜单" @click="mobileOpen = !mobileOpen"><X v-if="mobileOpen" :size="22" /><Menu v-else :size="22" /></button>
    </div>

    <Transition name="mobile-nav">
      <div v-if="mobileOpen" class="mobile-nav">
        <RouterLink v-for="item in navItems" :key="item.to" :to="item.to" @click="mobileOpen = false">{{ item.label }}</RouterLink>
        <RouterLink v-if="isLoggedIn" to="/profile" @click="mobileOpen = false">个人中心</RouterLink>
        <template v-else><RouterLink to="/login" @click="mobileOpen = false">登录</RouterLink><RouterLink to="/register" @click="mobileOpen = false">注册账号</RouterLink></template>
        <RouterLink to="/publish" class="mobile-publish-link" @click="mobileOpen = false">发布失物或招领</RouterLink>
      </div>
    </Transition>
  </header>
</template>
