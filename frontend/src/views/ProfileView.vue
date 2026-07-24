<script setup>
import {
  BadgeCheck,
  BellRing,
  Building2,
  Camera,
  Check,
  ChevronRight,
  Heart,
  LogOut,
  LockKeyhole,
  Mail,
  PackageSearch,
  Phone,
  Save,
  RotateCcw,
  ShieldCheck,
  UserRound,
} from '@lucide/vue'
import { computed, onMounted, reactive, ref } from 'vue'
import { RouterLink, useRouter } from 'vue-router'
import { logoutAccount, resetAvatar, updateAvatar } from '../api/auth'
import { fetchDrafts, fetchMyItems } from '../api/items'
import { uploadImage } from '../api/uploads'
import { clearAuthSession, currentUser, loadFavorites, showToast, syncCurrentUser } from '../state'

const router = useRouter()
const activeTab = ref('profile')
const editing = ref(false)
const avatarInput = ref(null)
const avatarUploading = ref(false)
const avatarUrl = computed(() => currentUser.value?.avatarUrl || '/images/default-avatar.png')
const profile = reactive({
  name: currentUser.value?.name || '校园用户',
  studentId: currentUser.value?.studentId || '',
  college: '计算机与信息工程学院',
  className: '软件工程 2302 班',
  phone: currentUser.value?.phone || '',
  email: currentUser.value?.email || '',
  bio: '愿每一次举手之劳都有回音。',
})

const myPosts = ref([])
const postsLoading = ref(true)
const postsError = ref('')
const favoriteItems = ref([])
const favoritesLoading = ref(true)
const favoritesError = ref('')
const solvedPostCount = computed(() => myPosts.value.filter((item) => item.solved).length)
const tabs = computed(() => [
  { id: 'profile', label: '基本资料', icon: UserRound },
  { id: 'posts', label: '我的发布', icon: PackageSearch, count: myPosts.value.length },
  { id: 'favorites', label: '我的收藏', icon: Heart, count: favoriteItems.value.length },
  { id: 'security', label: '账号安全', icon: ShieldCheck },
])

function formatPostTime(value) {
  if (!value) return '刚刚发布'
  return new Intl.DateTimeFormat('zh-CN', { month: 'long', day: 'numeric', hour: '2-digit', minute: '2-digit' }).format(new Date(value))
}

function normalizePost(item) {
  const solved = item.status === 'RESOLVED' || item.status === 'CLOSED'
  return {
    ...item,
    imageUrl: item.images?.[0]?.url || '',
    icon: PackageSearch,
    tone: item.type === 'lost' ? 'blue' : 'sage',
    time: formatPostTime(item.createdAt),
    statusText: solved ? '已找回' : item.type === 'lost' ? '正在寻找' : '等待认领',
    solved,
  }
}

async function loadMyPosts() {
  postsLoading.value = true
  postsError.value = ''
  try {
    const [published, drafts] = await Promise.all([fetchMyItems(), fetchDrafts()])
    const draftRecords = drafts.map((draft) => ({
      ...normalizePost({ ...draft, title: draft.title || '未命名草稿', location: draft.location || '尚未填写地点', status: 'DRAFT', createdAt: draft.updatedAt }),
      rowKey: `draft-${draft.id}`,
      isDraft: true,
      statusText: '草稿',
    }))
    myPosts.value = [...draftRecords, ...published.map((item) => ({ ...normalizePost(item), rowKey: `post-${item.id}` }))]
  } catch (error) {
    postsError.value = error.message || '我的发布加载失败，请稍后重试'
  } finally {
    postsLoading.value = false
  }
}

async function loadFavoriteItems() {
  favoritesLoading.value = true
  favoritesError.value = ''
  try {
    favoriteItems.value = (await loadFavorites()).map(normalizePost)
  } catch (error) {
    favoritesError.value = error.message || '收藏记录加载失败，请稍后重试'
  } finally {
    favoritesLoading.value = false
  }
}

onMounted(() => Promise.all([loadMyPosts(), loadFavoriteItems()]))

async function chooseAvatar(event) {
  const file = event.target.files?.[0]
  event.target.value = ''
  if (!file) return
  if (!['image/jpeg', 'image/png', 'image/webp'].includes(file.type)) return showToast('头像仅支持 JPG、PNG 或 WebP')
  if (file.size > 5 * 1024 * 1024) return showToast('头像不能超过 5MB')
  avatarUploading.value = true
  try {
    const uploaded = await uploadImage(file)
    syncCurrentUser(await updateAvatar({ key: uploaded.key, url: uploaded.url }))
    showToast('头像已更新')
  } catch (error) {
    showToast(error.message || '头像更新失败，请稍后重试')
  } finally {
    avatarUploading.value = false
  }
}

async function restoreDefaultAvatar() {
  if (avatarUploading.value) return
  avatarUploading.value = true
  try {
    syncCurrentUser(await resetAvatar())
    showToast('已恢复默认头像')
  } catch (error) {
    showToast(error.message || '恢复默认头像失败')
  } finally {
    avatarUploading.value = false
  }
}

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
          <div class="profile-avatar-wrap">
            <button type="button" class="profile-avatar" :disabled="avatarUploading" :aria-label="avatarUploading ? '正在上传头像' : '更换头像'" @click="avatarInput.click()">
              <img :src="avatarUrl" alt="个人头像" /><span></span><i><Camera :size="15" /></i>
            </button>
            <input ref="avatarInput" class="visually-hidden" type="file" accept="image/jpeg,image/png,image/webp" @change="chooseAvatar" />
            <button v-if="currentUser?.avatarUrl" type="button" class="avatar-reset" :disabled="avatarUploading" @click="restoreDefaultAvatar"><RotateCcw :size="12" />默认</button>
          </div>
          <div>
            <span class="section-kicker">STUDENT PROFILE</span>
            <h1>{{ profile.name }}</h1>
            <p><BadgeCheck :size="15" /> 校园身份已核验 · 软件工程 2302 班</p>
          </div>
        </div>
        <div class="profile-hero-stats">
          <div><strong>{{ String(myPosts.length).padStart(2, '0') }}</strong><span>累计发布</span></div>
          <div><strong>{{ String(solvedPostCount).padStart(2, '0') }}</strong><span>成功找回</span></div>
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
            <div v-if="postsLoading" class="profile-empty profile-post-state"><PackageSearch :size="34" /><h3>正在加载发布记录</h3></div>
            <div v-else-if="postsError" class="profile-empty profile-post-state"><PackageSearch :size="34" /><h3>{{ postsError }}</h3><button type="button" @click="loadMyPosts">重新加载</button></div>
            <div v-else-if="myPosts.length" class="profile-record-list">
              <article v-for="item in myPosts" :key="item.rowKey">
                <div class="record-icon" :class="item.tone"><img v-if="item.imageUrl" class="record-photo" :src="item.imageUrl" :alt="item.title" /><component v-else :is="item.icon" :size="28" /></div>
                <div class="record-copy"><span>{{ item.category }} · {{ item.time }}</span><h3>{{ item.title }}</h3><p>{{ item.location }} · {{ item.type === 'lost' ? '寻物启事' : '招领启事' }}</p></div>
                <b :class="{ solved: item.solved, draft: item.isDraft }">{{ item.statusText }}</b>
                <RouterLink :to="item.isDraft ? `/publish?draftId=${item.id}` : `/items/${item.id}`">{{ item.isDraft ? '继续编辑' : '查看' }}</RouterLink>
              </article>
            </div>
            <div v-else class="profile-empty profile-post-state"><PackageSearch :size="34" /><h3>还没有发布信息</h3><p>发布后，你可以在这里查看和管理进度。</p><RouterLink to="/publish">去发布第一条信息</RouterLink></div>
          </section>

          <section v-else-if="activeTab === 'favorites'" key="favorites" class="profile-panel">
            <header class="profile-panel-head"><div><span>SAVED ITEMS</span><h2>我的收藏</h2><p>集中查看你关注的失物招领信息。</p></div></header>
            <div v-if="favoritesLoading" class="profile-empty profile-post-state"><Heart :size="34" /><h3>正在加载收藏记录</h3></div>
            <div v-else-if="favoritesError" class="profile-empty profile-post-state"><Heart :size="34" /><h3>{{ favoritesError }}</h3><button type="button" @click="loadFavoriteItems">重新加载</button></div>
            <div v-else-if="favoriteItems.length" class="profile-record-list">
              <article v-for="item in favoriteItems" :key="item.id">
                <div class="record-icon" :class="item.tone"><img v-if="item.imageUrl" class="record-photo" :src="item.imageUrl" :alt="item.title" /><component v-else :is="item.icon" :size="28" /></div>
                <div class="record-copy"><span>{{ item.category }} · {{ item.time }}</span><h3>{{ item.title }}</h3><p>{{ item.location }} · {{ item.type === 'lost' ? '寻物启事' : '招领启事' }}</p></div>
                <b>关注中</b><RouterLink :to="`/items/${item.id}`">查看</RouterLink>
              </article>
            </div>
            <div v-else class="profile-empty profile-post-state"><Heart :size="34" /><h3>还没有收藏信息</h3><p>在寻物大厅点击心形按钮，收藏会保存在你的账号中。</p><RouterLink to="/hall">去寻物大厅看看</RouterLink></div>
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
