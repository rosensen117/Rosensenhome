<script setup>
import { Bell, CheckCircle2, ChevronRight, MessageCircle, PackageCheck, Search } from '@lucide/vue'
import { RouterLink } from 'vue-router'
import { showToast } from '../state'

const events = [
  { time: '刚刚', type: 'found', title: '拾到一张校园卡', detail: '第三教学楼 · 姓名已隐藏', status: '等待认领', icon: PackageCheck },
  { time: '8 分钟前', type: 'solved', title: '黑色保温杯已归还', detail: '感谢一食堂值班同学保管', status: '已找回', icon: CheckCircle2 },
  { time: '21 分钟前', type: 'message', title: '“蓝色卡套”收到新线索', detail: '可能出现在图书馆东侧自习区', status: '新线索', icon: MessageCircle },
  { time: '36 分钟前', type: 'found', title: '南区操场发现一串钥匙', detail: '红色挂绳，具体数字牌未公开', status: '等待认领', icon: PackageCheck },
  { time: '1 小时前', type: 'solved', title: '无线耳机与失主完成交接', detail: '二教服务台 · 双方已确认', status: '已找回', icon: CheckCircle2 },
]
</script>

<template>
  <div class="route-page-shell clues-page">
    <section class="route-page-banner clues-banner"><div class="section-wrap route-page-banner-inner"><div><span class="section-kicker">LIVE CLUES</span><h1>线索动态</h1><p>查看校园里最新的拾取、归还与匹配消息。</p></div><button class="banner-action" @click="showToast('已订阅最新校园线索')"><Bell :size="17" />订阅动态</button></div></section>
    <section class="section-wrap clue-page-layout">
      <div class="clue-feed">
        <div class="clue-feed-toolbar"><div><b>实时更新</b><span>最近更新于 1 分钟前</span></div><div class="live-indicator"><i></i>LIVE</div></div>
        <article v-for="event in events" :key="event.time + event.title" class="clue-feed-item">
          <time>{{ event.time }}</time><div class="feed-icon" :class="event.type"><component :is="event.icon" :size="20" /></div><div><span>{{ event.status }}</span><h2>{{ event.title }}</h2><p>{{ event.detail }}</p></div><button aria-label="查看相关信息" @click="showToast('正在打开相关信息')"><ChevronRight :size="19" /></button>
        </article>
      </div>
      <aside class="clue-page-sidebar">
        <article class="clue-summary"><span class="section-kicker">TODAY</span><h2>今天的校园</h2><dl><div><dt>新发布</dt><dd>12</dd></div><div><dt>新线索</dt><dd>28</dd></div><div><dt>已找回</dt><dd>08</dd></div></dl></article>
        <article class="clue-help-card"><Search :size="26" /><h3>你正在寻找东西？</h3><p>进入寻物大厅搜索，或发布信息让更多人帮你留意。</p><RouterLink to="/hall">进入寻物大厅 →</RouterLink></article>
      </aside>
    </section>
  </div>
</template>
