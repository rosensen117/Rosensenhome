<script setup>
import { ArrowLeft, CalendarDays, Eye, Flag, Heart, MapPin, MessageCircle, PackageCheck, Send, ShieldCheck, UserRound } from '@lucide/vue'
import { computed, ref } from 'vue'
import { RouterLink, useRoute } from 'vue-router'
import ItemCard from '../components/ItemCard.vue'
import { items } from '../data'
import { favorites, showToast, toggleFavorite } from '../state'

const route = useRoute()
const item = computed(() => items.find((entry) => entry.id === Number(route.params.id)))
const clue = ref('')
const related = computed(() => item.value ? items.filter((entry) => entry.id !== item.value.id && entry.category === item.value.category).slice(0, 2) : [])

function sendClue() {
  if (!clue.value.trim()) return showToast('请先填写线索内容')
  clue.value = ''
  showToast('线索已提交，发布者会收到提醒')
}
</script>

<template>
  <div v-if="item" class="route-page-shell detail-page">
    <div class="section-wrap detail-breadcrumb"><RouterLink to="/hall"><ArrowLeft :size="15" />返回寻物大厅</RouterLink><span>/</span><span>{{ item.category }}</span><span>/</span><b>{{ item.title }}</b></div>
    <section class="section-wrap detail-layout">
      <div class="detail-main">
        <div class="detail-visual" :class="`tone-${item.tone}`"><span class="item-type" :class="item.type">{{ item.type === 'lost' ? '正在寻找' : '等待认领' }}</span><component :is="item.icon" :size="150" :stroke-width="1.1" /><span v-if="item.hot" class="hot-stamp">急寻</span></div>
        <article class="detail-copy-card">
          <div class="detail-title-row"><div><span class="detail-category">{{ item.category }}</span><h1>{{ item.title }}</h1></div><button class="detail-favorite" :class="{ active: favorites.has(item.id) }" @click="toggleFavorite(item.id)"><Heart :size="20" :fill="favorites.has(item.id) ? 'currentColor' : 'none'" />{{ favorites.has(item.id) ? '已收藏' : '收藏' }}</button></div>
          <div class="detail-stats"><span><Eye :size="15" />{{ item.views }} 次浏览</span><span><MessageCircle :size="15" />{{ item.clues }} 条线索</span><span><ShieldCheck :size="15" />信息已审核</span></div>
          <div class="detail-description"><h2>物品描述</h2><p>{{ item.description }}</p></div>
          <dl class="detail-facts"><div><dt><MapPin :size="17" />地点</dt><dd>{{ item.location }}</dd></div><div><dt><CalendarDays :size="17" />时间</dt><dd>{{ item.eventTime }}</dd></div></dl>
          <div class="detail-privacy"><ShieldCheck :size="19" /><p><b>隐藏特征不会公开展示</b><br />为了避免冒领，具体编号、内容物或独特痕迹仅在认领核验时使用。</p></div>
        </article>

        <article class="clue-form-card">
          <div><span class="section-kicker">ADD A CLUE</span><h2>我可能见过</h2><p>请描述你看到物品的时间、地点或去向，避免填写个人敏感信息。</p></div>
          <form @submit.prevent="sendClue"><textarea v-model="clue" rows="4" maxlength="200" placeholder="例如：今天 15:00 左右在二教一楼服务台看到过类似物品……"></textarea><div><span>{{ clue.length }} / 200</span><button><Send :size="16" />提交线索</button></div></form>
        </article>
      </div>

      <aside class="detail-sidebar">
        <article class="publisher-card"><div class="publisher-avatar"><UserRound :size="26" /></div><div><span>信息发布者</span><h3>{{ item.publisher }}</h3><p>{{ item.publisherMeta }}</p></div><div class="publisher-trust"><ShieldCheck :size="16" />近 90 天信用良好</div></article>
        <article class="claim-card"><PackageCheck :size="25" /><h3>{{ item.type === 'found' ? '这是你的物品？' : '你找到了它？' }}</h3><p>{{ item.type === 'found' ? '提交认领申请并回答隐藏特征，发布者会核验你的信息。' : '向失主发送线索，说明物品当前所在位置。' }}</p><button @click="showToast(item.type === 'found' ? '认领申请表将在接入后端后启用' : '已打开线索填写区域')">{{ item.type === 'found' ? '申请认领' : '提供线索' }}</button></article>
        <button class="report-link" @click="showToast('举报入口将在接入后台后启用')"><Flag :size="15" />举报不实或违规信息</button>
      </aside>
    </section>

    <section v-if="related.length" class="section-wrap related-section"><div class="section-heading"><div><span class="section-kicker">RELATED POSTS</span><h2>相似信息</h2></div><RouterLink to="/hall">查看更多 →</RouterLink></div><div class="item-grid"><ItemCard v-for="entry in related" :key="entry.id" :item="entry" /></div></section>
  </div>
  <div v-else class="not-found-inline"><h1>没有找到这条信息</h1><RouterLink to="/hall">返回寻物大厅</RouterLink></div>
</template>
