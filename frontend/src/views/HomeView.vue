<script setup>
import {
  ArrowRight,
  Bell,
  PackageCheck,
  Search,
  ShieldCheck,
} from '@lucide/vue'
import { ref } from 'vue'
import { RouterLink, useRouter } from 'vue-router'

const router = useRouter()
const keyword = ref('')

function search() {
  const value = keyword.value.trim()
  router.push({ path: '/hall', query: value ? { keyword: value } : {} })
}

function quickSearch(value) {
  router.push({ path: '/hall', query: { keyword: value } })
}
</script>

<template>
  <main class="home-gateway">
    <section class="section-wrap gateway-layout">
      <div class="gateway-copy route-reveal">
        <div class="eyebrow"><span></span> 校园失物招领服务</div>
        <h1>丢失的东西，<br /><em>应该有路回来。</em></h1>
        <p>
          一个更清楚、更安全的校园寻物入口。搜索已有信息，或发布你的失物与招领。
        </p>

        <form class="gateway-search" @submit.prevent="search">
          <Search :size="20" />
          <input
            v-model="keyword"
            type="search"
            placeholder="搜索物品、地点或特征"
            aria-label="搜索失物招领信息"
          />
          <button type="submit">搜索 <ArrowRight :size="16" /></button>
        </form>

        <div class="gateway-tags">
          <span>常用搜索</span>
          <button v-for="tag in ['校园卡', '耳机', '钥匙', '雨伞']" :key="tag" @click="quickSearch(tag)">
            {{ tag }}
          </button>
        </div>

        <div class="gateway-main-actions">
          <RouterLink to="/hall" class="gateway-primary">
            进入招领大厅 <ArrowRight :size="17" />
          </RouterLink>
          <RouterLink to="/publish" class="gateway-secondary">发布信息</RouterLink>
        </div>
      </div>

      <aside class="gateway-panel route-reveal delay-1" aria-label="快速入口">
        <div class="gateway-panel-head">
          <span>QUICK ACCESS</span>
          <strong>你现在需要？</strong>
        </div>

        <RouterLink to="/publish?type=lost" class="gateway-route-card">
          <span class="gateway-route-icon"><Search :size="20" /></span>
          <span><b>我丢了东西</b><small>填写信息，发布寻物启事</small></span>
          <ArrowRight :size="17" />
        </RouterLink>

        <RouterLink to="/publish?type=found" class="gateway-route-card">
          <span class="gateway-route-icon found"><PackageCheck :size="20" /></span>
          <span><b>我捡到东西</b><small>登记物品，等待失主认领</small></span>
          <ArrowRight :size="17" />
        </RouterLink>

        <RouterLink to="/clues" class="gateway-route-card">
          <span class="gateway-route-icon clue"><Bell :size="20" /></span>
          <span><b>查看线索动态</b><small>了解最新归还与认领进展</small></span>
          <ArrowRight :size="17" />
        </RouterLink>

        <RouterLink to="/guide" class="gateway-guide-link">
          <ShieldCheck :size="16" /> 认领前先阅读安全指南
        </RouterLink>
      </aside>
    </section>

    <div class="gateway-status">
      <div class="section-wrap gateway-status-inner">
        <span><b>28</b> 条信息等待匹配</span>
        <i></i>
        <span><b>8</b> 件物品今日找回</span>
        <i></i>
        <span><b>校园身份核验</b> 降低冒领风险</span>
      </div>
    </div>
  </main>
</template>
