<script setup>
import {
  BadgeCheck,
  BellRing,
  Building2,
  Check,
  ChevronRight,
  Heart,
  LogOut,
  LockKeyhole,
  Mail,
  PackageSearch,
  Phone,
  Save,
  ShieldCheck,
  UserRound,
} from '@lucide/vue'
import { computed, reactive, ref } from 'vue'
import { RouterLink, useRouter } from 'vue-router'
import { logoutAccount } from '../api/auth'
import { items } from '../data'
import { clearAuthSession, currentUser, favorites, showToast } from '../state'

const router = useRouter()
const activeTab = ref('profile')
const editing = ref(false)
const profile = reactive({
  name: currentUser.value?.name || '校园用户',
  studentId: currentUser.value?.studentId || '',
  college: '计算机与信息工程学院',
  className: '软件工程 2302 班',
  phone: currentUser.value?.phone || '',
  email: currentUser.value?.email || '',
  bio: '愿每一次举手之劳都有回音。',
})

const tabs = [
  { id: 'profile', label: '基本资料', icon: UserRound },
  { id: 'posts', label: '我的发布', icon: PackageSearch, count: 3 },
  { id: 'favorites', label: '我的收藏', icon: Heart, count: 1 },
  { id: 'security', label: '账号安全', icon: ShieldCheck },
]

const myPosts = [
  { ...items[1], statusText: '正在寻找', views: 126 },
  { ...items[4], statusText: '等待认领', views: 84 },
  { ...items[5], statusText: '已找回', views: 57, solved: true },
]

const favoriteItems = computed(() => items.filter((item) => favorites.value.has(item.id)))

function saveProfile() {
  editing.value = false
  showToast('个人资料已保存')
}

function securityAction(text) {
  showToast(`${text}将在接入 Spring Boot 后端后启用`)
}

async function logout() {
  try { await logoutAccount() } catch { /* 后端不可用时也退出本机状态 */ }
  clearAuthSession()
  showToast('已安全退出登录')
  router.push('/login')
}
</script>

<template>
  <div class="route-page-shell profile-page">
    <section class="profile-hero">
      <div class="section-wrap profile-hero-inner">
        <div class="profile-identity">
          <div class="profile-avatar">林<span></span></div>
          <div>
            <span class="section-kicker">STUDENT PROFILE</span>
            <h1>{{ profile.name }}</h1>
            <p><BadgeCheck :size="15" /> 校园身份已核验 · 软件工程 2302 班</p>
          </div>
        </div>
        <div class="profile-hero-stats">
          <div><strong>03</strong><span>累计发布</span></div>
          <div><strong>02</strong><span>成功找回</span></div>
          <div><strong>96</strong><span>信用分</span></div>
        </div>
      </div>
    </section>

    <section class="section-wrap profile-layout">
      <aside class="profile-sidebar">
        <div class="profile-completion">
          <div><span>资料完整度</span><b>88%</b></div>
          <div class="completion-track"><span></span></div>
          <p>完善联系方式有助于更快收到线索。</p>
        </div>

        <nav class="profile-tabs" aria-label="个人中心功能">
          <button
            v-for="tab in tabs"
            :key="tab.id"
            :class="{ active: activeTab === tab.id }"
            @click="activeTab = tab.id"
          >
            <component :is="tab.icon" :size="18" />
            <span>{{ tab.label }}</span>
            <em v-if="tab.count">{{ tab.count }}</em>
            <ChevronRight v-else :size="15" />
          </button>
          <button @click="logout"><LogOut :size="18" /><span>退出登录</span><ChevronRight :size="15" /></button>
        </nav>

        <div class="profile-verify-card">
          <ShieldCheck :size="23" />
          <div><b>校园身份已认证</b><span>认证于 2026-03-12</span></div>
          <Check :size="16" />
        </div>
      </aside>

      <main class="profile-content">
        <Transition name="profile-panel" mode="out-in">
          <section v-if="activeTab === 'profile'" key="profile" class="profile-panel">
            <header class="profile-panel-head">
              <div><span>PROFILE DETAILS</span><h2>基本资料</h2><p>这些信息仅用于校园失物招领服务。</p></div>
              <button class="profile-edit-button" @click="editing = !editing">{{ editing ? '取消编辑' : '编辑资料' }}</button>
            </header>

            <form class="profile-form" @submit.prevent="saveProfile">
              <label><span>姓名</span><div><UserRound :size="17" /><input v-model="profile.name" :disabled="!editing" /></div></label>
              <label><span>学号</span><div><BadgeCheck :size="17" /><input v-model="profile.studentId" disabled /></div><small>学号已脱敏，不支持自行修改</small></label>
              <label class="wide"><span>所属学院</span><div><Building2 :size="17" /><input v-model="profile.college" :disabled="!editing" /></div></label>
              <label><span>班级</span><div><Building2 :size="17" /><input v-model="profile.className" :disabled="!editing" /></div></label>
              <label><span>手机号码</span><div><Phone :size="17" /><input v-model="profile.phone" :disabled="!editing" /></div></label>
              <label class="wide"><span>校园邮箱</span><div><Mail :size="17" /><input v-model="profile.email" :disabled="!editing" /></div></label>
              <label class="wide"><span>个人简介</span><textarea v-model="profile.bio" :disabled="!editing" rows="3"></textarea></label>
              <div v-if="editing" class="profile-form-actions"><button type="submit"><Save :size="16" /> 保存修改</button></div>
            </form>
          </section>

          <section v-else-if="activeTab === 'posts'" key="posts" class="profile-panel">
            <header class="profile-panel-head">
              <div><span>MY POSTS</span><h2>我的发布</h2><p>管理你发布的寻物与招领信息。</p></div>
              <RouterLink to="/publish">发布新信息</RouterLink>
            </header>
            <div class="profile-record-list">
              <article v-for="item in myPosts" :key="item.id">
                <div class="record-icon" :class="item.tone"><component :is="item.icon" :size="28" /></div>
                <div class="record-copy"><span>{{ item.category }} · {{ item.time }}</span><h3>{{ item.title }}</h3><p>{{ item.location }} · {{ item.clues }} 条线索 · {{ item.views }} 次浏览</p></div>
                <b :class="{ solved: item.solved }">{{ item.statusText }}</b>
                <RouterLink :to="`/items/${item.id}`">查看</RouterLink>
              </article>
            </div>
          </section>

          <section v-else-if="activeTab === 'favorites'" key="favorites" class="profile-panel">
            <header class="profile-panel-head"><div><span>SAVED ITEMS</span><h2>我的收藏</h2><p>集中查看你关注的失物招领信息。</p></div></header>
            <div v-if="favoriteItems.length" class="profile-record-list">
              <article v-for="item in favoriteItems" :key="item.id">
                <div class="record-icon" :class="item.tone"><component :is="item.icon" :size="28" /></div>
                <div class="record-copy"><span>{{ item.category }} · {{ item.time }}</span><h3>{{ item.title }}</h3><p>{{ item.location }} · {{ item.clues }} 条线索</p></div>
                <b>关注中</b><RouterLink :to="`/items/${item.id}`">查看</RouterLink>
              </article>
            </div>
            <div v-else class="profile-empty"><Heart :size="34" /><h3>还没有收藏信息</h3><RouterLink to="/hall">去寻物大厅看看</RouterLink></div>
          </section>

          <section v-else key="security" class="profile-panel">
            <header class="profile-panel-head"><div><span>ACCOUNT SECURITY</span><h2>账号安全</h2><p>管理验证方式与消息提醒。</p></div></header>
            <div class="security-list">
              <button @click="securityAction('修改登录密码')"><span class="security-icon"><LockKeyhole :size="20" /></span><span><b>登录密码</b><small>建议定期更新密码，保护账号安全</small></span><em>修改</em><ChevronRight :size="16" /></button>
              <button @click="securityAction('更换手机号码')"><span class="security-icon"><Phone :size="20" /></span><span><b>绑定手机</b><small>{{ profile.phone }}</small></span><em>已绑定</em><ChevronRight :size="16" /></button>
              <button @click="securityAction('消息提醒设置')"><span class="security-icon"><BellRing :size="20" /></span><span><b>消息提醒</b><small>匹配线索、留言与认领进度通知</small></span><em>已开启</em><ChevronRight :size="16" /></button>
            </div>
          </section>
        </Transition>
      </main>
    </section>
  </div>
</template>
