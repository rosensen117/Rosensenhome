<script setup>
import { Clock3, Heart, MapPin, MessageCircle, ShieldCheck } from '@lucide/vue'
import { RouterLink } from 'vue-router'
import { favorites, toggleFavorite } from '../state'

defineProps({ item: { type: Object, required: true } })
</script>

<template>
  <article class="item-card route-item-card">
    <div class="item-visual" :class="`tone-${item.tone}`">
      <span class="item-type" :class="item.type">{{ item.type === 'lost' ? '正在寻找' : '等待认领' }}</span>
      <component :is="item.icon" :size="74" :stroke-width="1.25" />
      <span v-if="item.hot" class="hot-stamp">急寻</span>
      <button class="favorite" :class="{ active: favorites.has(item.id) }" :aria-label="favorites.has(item.id) ? '取消收藏' : '收藏'" @click="toggleFavorite(item.id)">
        <Heart :size="18" :fill="favorites.has(item.id) ? 'currentColor' : 'none'" />
      </button>
    </div>
    <div class="item-body">
      <div class="item-meta"><span>{{ item.category }}</span><span v-if="item.verified" class="verified"><ShieldCheck :size="13" />已核验</span></div>
      <h3>{{ item.title }}</h3>
      <p>{{ item.description }}</p>
      <div class="item-facts"><span><MapPin :size="15" />{{ item.location }}</span><span><Clock3 :size="15" />{{ item.time }}</span></div>
      <div class="item-footer"><span><MessageCircle :size="15" /> {{ item.clues }} 条线索</span><RouterLink :to="`/items/${item.id}`">查看详情 <span aria-hidden="true">→</span></RouterLink></div>
    </div>
  </article>
</template>
