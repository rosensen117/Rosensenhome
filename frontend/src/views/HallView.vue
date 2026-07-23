<script setup>
import { Check, ChevronDown, CircleHelp, Filter, MapPin, Search, X } from '@lucide/vue'
import { computed, onBeforeUnmount, onMounted, ref, watch } from 'vue'
import { useRoute } from 'vue-router'
import ItemCard from '../components/ItemCard.vue'
import { categories, items, places } from '../data'

const route = useRoute()
const typeFilter = ref(route.query.type === 'lost' || route.query.type === 'found' ? route.query.type : 'all')
const categoryFilter = ref('全部')
const placeFilter = ref('全部地点')
const keyword = ref(String(route.query.keyword || ''))
const sortMode = ref('最新发布')
const onlyVerified = ref(false)
const showFilters = ref(false)
const placeMenuOpen = ref(false)
const sortMenuOpen = ref(false)
const currentPage = ref(1)
const pageSize = 4

const filteredItems = computed(() => {
  const query = keyword.value.trim().toLowerCase()
  const result = items.filter((item) => {
    const typeMatch = typeFilter.value === 'all' || item.type === typeFilter.value
    const categoryMatch = categoryFilter.value === '全部' || item.category === categoryFilter.value
    const placeMatch = placeFilter.value === '全部地点' || item.placeGroup === placeFilter.value
    const verifiedMatch = !onlyVerified.value || item.verified
    const textMatch = !query || [item.title, item.description, item.location, item.category].join(' ').toLowerCase().includes(query)
    return typeMatch && categoryMatch && placeMatch && verifiedMatch && textMatch
  })
  return sortMode.value === '线索最多' ? [...result].sort((a, b) => b.clues - a.clues) : [...result].sort((a, b) => b.dateOrder - a.dateOrder)
})

const totalPages = computed(() => Math.max(1, Math.ceil(filteredItems.value.length / pageSize)))
const paginatedItems = computed(() => {
  const start = (currentPage.value - 1) * pageSize
  return filteredItems.value.slice(start, start + pageSize)
})

watch([keyword, typeFilter, categoryFilter, placeFilter, onlyVerified, sortMode], () => {
  currentPage.value = 1
})

function resetFilters() {
  typeFilter.value = 'all'
  categoryFilter.value = '全部'
  placeFilter.value = '全部地点'
  onlyVerified.value = false
  keyword.value = ''
}

function selectPlace(place) {
  placeFilter.value = place
  placeMenuOpen.value = false
}

function selectSort(mode) {
  sortMode.value = mode
  sortMenuOpen.value = false
}

function closeMenus() {
  placeMenuOpen.value = false
  sortMenuOpen.value = false
}

function goToPage(page) {
  currentPage.value = Math.min(Math.max(page, 1), totalPages.value)
  document.querySelector('.hall-workspace')?.scrollIntoView({ behavior: 'smooth', block: 'start' })
}

onMounted(() => document.addEventListener('click', closeMenus))
onBeforeUnmount(() => document.removeEventListener('click', closeMenus))
</script>

<template>
  <div class="route-page-shell hall-page">
    <section class="route-page-banner">
      <div class="section-wrap route-page-banner-inner">
        <div><span class="section-kicker">LOST &amp; FOUND BOARD</span><h1>寻物大厅</h1><p>按物品、地点和时间筛选，找到可能匹配的信息。</p></div>
        <div class="route-page-index">01 / HALL</div>
      </div>
    </section>

    <section class="section-wrap hall-workspace">
      <form class="hall-search" @submit.prevent><Search :size="20" /><input v-model="keyword" type="search" placeholder="搜索物品名称、地点或特征" /><button type="submit">搜索</button></form>
      <div class="browse-layout route-browse-layout">
        <aside class="filter-panel" :class="{ open: showFilters }">
          <div class="filter-title"><span><Filter :size="18" />筛选信息</span><button @click="resetFilters">重置</button></div>
          <div class="filter-group"><label>信息类型</label><div class="segmented vertical">
            <button :class="{ active: typeFilter === 'all' }" @click="typeFilter = 'all'">全部信息 <span>{{ items.length }}</span></button>
            <button :class="{ active: typeFilter === 'lost' }" @click="typeFilter = 'lost'">寻找失物 <span>{{ items.filter(i => i.type === 'lost').length }}</span></button>
            <button :class="{ active: typeFilter === 'found' }" @click="typeFilter = 'found'">拾到物品 <span>{{ items.filter(i => i.type === 'found').length }}</span></button>
          </div></div>
          <div class="filter-group"><label>物品分类</label><div class="choice-list"><button v-for="category in categories" :key="category" :class="{ active: categoryFilter === category }" @click="categoryFilter = category"><span>{{ category }}</span><Check v-if="categoryFilter === category" :size="15" /></button></div></div>
          <div class="filter-group">
            <label id="place-filter-label">发现地点</label>
            <div class="place-select" :class="{ open: placeMenuOpen }" @click.stop>
              <button
                type="button"
                class="place-select-trigger"
                aria-labelledby="place-filter-label"
                :aria-expanded="placeMenuOpen"
                aria-haspopup="listbox"
                @click="placeMenuOpen = !placeMenuOpen"
              >
                <MapPin :size="16" />
                <span>{{ placeFilter }}</span>
                <ChevronDown :size="15" />
              </button>
              <Transition name="select-pop">
                <div v-if="placeMenuOpen" class="place-select-menu" role="listbox" aria-labelledby="place-filter-label">
                  <button
                    v-for="place in places"
                    :key="place"
                    type="button"
                    role="option"
                    :aria-selected="placeFilter === place"
                    :class="{ active: placeFilter === place }"
                    @click="selectPlace(place)"
                  >
                    <span>{{ place }}</span>
                    <Check v-if="placeFilter === place" :size="14" />
                  </button>
                </div>
              </Transition>
            </div>
          </div>
          <label class="verify-toggle"><input v-model="onlyVerified" type="checkbox" /><span class="toggle-track"><span></span></span><span>只看已核验用户</span></label>
          <div class="filter-help"><CircleHelp :size="17" /><p>没有找到？试试减少筛选条件，或发布一条寻物信息。</p></div>
        </aside>

        <div class="listing-content">
          <div class="listing-toolbar">
            <button class="mobile-filter" @click="showFilters = !showFilters"><Filter :size="17" />筛选</button>
            <div class="hall-result-label"><strong>{{ filteredItems.length }}</strong> 条匹配信息</div>
            <div class="active-filters">
              <button v-if="typeFilter !== 'all'" @click="typeFilter = 'all'">{{ typeFilter === 'lost' ? '寻找失物' : '拾到物品' }} <X :size="13" /></button>
              <button v-if="categoryFilter !== '全部'" @click="categoryFilter = '全部'">{{ categoryFilter }} <X :size="13" /></button>
            </div>
            <div class="sort-select" :class="{ open: sortMenuOpen }" @click.stop>
              <span>排序</span>
              <button
                type="button"
                class="sort-select-trigger"
                :aria-expanded="sortMenuOpen"
                aria-haspopup="listbox"
                @click="sortMenuOpen = !sortMenuOpen"
              >
                <b>{{ sortMode }}</b>
                <ChevronDown :size="14" />
              </button>
              <Transition name="select-pop">
                <div v-if="sortMenuOpen" class="sort-select-menu" role="listbox" aria-label="排序方式">
                  <button
                    v-for="mode in ['最新发布', '线索最多']"
                    :key="mode"
                    type="button"
                    role="option"
                    :aria-selected="sortMode === mode"
                    :class="{ active: sortMode === mode }"
                    @click="selectSort(mode)"
                  >
                    <span>{{ mode }}</span>
                    <Check v-if="sortMode === mode" :size="13" />
                  </button>
                </div>
              </Transition>
            </div>
          </div>
          <TransitionGroup v-if="filteredItems.length" name="card-list" tag="div" class="item-grid paginated-item-grid"><ItemCard v-for="item in paginatedItems" :key="item.id" :item="item" /></TransitionGroup>
          <div v-else class="empty-state"><Search :size="42" :stroke-width="1.3" /><h3>暂时没有找到匹配信息</h3><p>换个关键词或减少筛选条件，也可以先发布信息等待线索。</p><button @click="resetFilters">清除全部筛选</button></div>
          <nav v-if="filteredItems.length" class="hall-pagination" aria-label="寻物信息分页">
            <div class="pagination-summary">
              <span>每页固定 {{ pageSize }} 条</span>
              <b>{{ currentPage }} / {{ totalPages }}</b>
            </div>
            <div class="pagination-controls">
              <button type="button" :disabled="currentPage === 1" @click="goToPage(currentPage - 1)">上一页</button>
              <button
                v-for="page in totalPages"
                :key="page"
                type="button"
                :class="{ active: currentPage === page }"
                :aria-current="currentPage === page ? 'page' : undefined"
                @click="goToPage(page)"
              >
                {{ String(page).padStart(2, '0') }}
              </button>
              <button type="button" :disabled="currentPage === totalPages" @click="goToPage(currentPage + 1)">下一页</button>
            </div>
          </nav>
        </div>
      </div>
    </section>
  </div>
</template>
