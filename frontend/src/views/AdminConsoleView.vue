<script setup>
import {
  ArrowUpRight,
  Bell,
  Check,
  ChevronDown,
  CircleCheck,
  ClipboardCheck,
  Clock3,
  ExternalLink,
  FileWarning,
  Handshake,
  LayoutDashboard,
  LogOut,
  Megaphone,
  Menu,
  MoreHorizontal,
  Plus,
  ScrollText,
  Search,
  ShieldCheck,
  TriangleAlert,
  UserRound,
  UsersRound,
  X,
  XCircle,
} from '@lucide/vue'
import { computed, ref } from 'vue'
import { RouterLink, useRoute, useRouter } from 'vue-router'
import { logoutAccount } from '../api/auth'
import { clearAuthSession, currentUser, showToast } from '../state'

const route = useRoute()
const router = useRouter()
const sidebarOpen = ref(false)
const section = computed(() => route.meta.adminSection || 'dashboard')

const navItems = [
  { id: 'dashboard', label: '管理工作台', to: '/admin/dashboard', icon: LayoutDashboard },
  { id: 'reviews', label: '信息审核', to: '/admin/reviews', icon: ClipboardCheck, badge: 8 },
  { id: 'claims', label: '认领管理', to: '/admin/claims', icon: Handshake, badge: 3 },
  { id: 'users', label: '用户管理', to: '/admin/users', icon: UsersRound },
  { id: 'notices', label: '公告管理', to: '/admin/notices', icon: Megaphone },
  { id: 'logs', label: '系统日志', to: '/admin/logs', icon: ScrollText },
]

const sectionMeta = {
  dashboard: ['管理工作台', '查看校园失物招领系统的今日运行概况'],
  reviews: ['信息审核', '审核用户发布的失物与招领信息'],
  claims: ['认领管理', '跟进认领申请、异常申诉与交接进度'],
  users: ['用户管理', '管理校园用户认证状态与账号权限'],
  notices: ['公告管理', '发布和维护面向全校的服务公告'],
  logs: ['系统日志', '追踪管理操作与系统安全事件'],
}

const metrics = [
  { label: '待审核信息', value: '08', change: '+3', icon: ClipboardCheck, tone: 'amber' },
  { label: '今日新增发布', value: '24', change: '+18%', icon: FileWarning, tone: 'blue' },
  { label: '进行中认领', value: '13', change: '+2', icon: Handshake, tone: 'green' },
  { label: '已认证用户', value: '1,286', change: '+12', icon: UsersRound, tone: 'slate' },
]

const reviewRows = [
  { id: 'LF-0722-018', title: '银灰色无线耳机盒', type: '招领', user: '周同学', place: '第二教学楼', time: '15:42', risk: '普通', status: '待审核' },
  { id: 'LF-0722-017', title: '黑色双肩包', type: '寻物', user: '林同学', place: '西操场看台', time: '15:18', risk: '加急', status: '待审核' },
  { id: 'LF-0722-016', title: '蓝色校园卡套', type: '寻物', user: '李同学', place: '图书馆三层', time: '14:56', risk: '普通', status: '待审核' },
  { id: 'LF-0722-015', title: '白色充电宝', type: '招领', user: '陈同学', place: '第一食堂', time: '14:31', risk: '普通', status: '待审核' },
]

const claimRows = [
  { id: 'CL-24078', item: '银灰色无线耳机盒', applicant: '王同学', owner: '周同学', time: '今天 15:20', status: '等待发布者确认' },
  { id: 'CL-24077', item: '一串宿舍钥匙', applicant: '赵同学', owner: '孙同学', time: '今天 13:46', status: '特征核验中' },
  { id: 'CL-24076', item: '白色充电宝', applicant: '刘同学', owner: '陈同学', time: '今天 10:12', status: '申诉处理中' },
  { id: 'CL-24075', item: '《数据结构》笔记本', applicant: '林同学', owner: '吴同学', time: '昨天 18:30', status: '交接完成' },
]

const userRows = [
  { name: '林晓雨', id: '2023******18', college: '计算机学院', posts: 3, credit: 96, status: '已认证' },
  { name: '周明哲', id: '2022******06', college: '机械工程学院', posts: 5, credit: 92, status: '已认证' },
  { name: '陈思涵', id: '2024******31', college: '外国语学院', posts: 1, credit: 100, status: '待认证' },
  { name: '赵子安', id: '2021******27', college: '管理学院', posts: 8, credit: 72, status: '受限' },
]

const notices = [
  { title: '暑期失物招领服务时间调整', date: '2026-07-20', scope: '全体用户', status: '已发布' },
  { title: '关于规范校园卡认领流程的通知', date: '2026-07-12', scope: '全体用户', status: '已发布' },
  { title: '新学期志愿者值班安排', date: '2026-08-25', scope: '志愿者', status: '草稿' },
]

const logRows = [
  { time: '2026-07-22 15:46:08', admin: 'admin01', action: '审核通过招领信息', target: 'LF-0722-014', ip: '10.24.16.32', level: '正常' },
  { time: '2026-07-22 15:35:22', admin: 'admin02', action: '限制用户发布权限', target: '2021******27', ip: '10.24.18.11', level: '重要' },
  { time: '2026-07-22 15:12:47', admin: 'admin01', action: '处理认领申诉', target: 'CL-24076', ip: '10.24.16.32', level: '正常' },
  { time: '2026-07-22 14:58:16', admin: 'system', action: '检测到连续登录失败', target: 'admin03', ip: '172.18.4.92', level: '警告' },
]

const trend = [32, 48, 41, 65, 54, 78, 69]
const trendDays = ['周四', '周五', '周六', '周日', '周一', '周二', '今天']

async function logout() {
  try { await logoutAccount() } catch { /* 本地状态仍需清理 */ }
  clearAuthSession()
  showToast('已退出管理员账号')
  router.push('/admin/login')
}

function action(message) {
  showToast(message)
}
</script>

<template>
  <div class="admin-console">
    <aside class="admin-sidebar" :class="{ open: sidebarOpen }">
      <div class="admin-console-brand"><span><ShieldCheck :size="22" /></span><div><b>拾光管理端</b><small>ADMIN CONSOLE</small></div><button @click="sidebarOpen = false"><X :size="20" /></button></div>
      <div class="admin-nav-label">系统管理</div>
      <nav class="admin-console-nav">
        <RouterLink v-for="item in navItems" :key="item.id" :to="item.to" :class="{ active: section === item.id }" @click="sidebarOpen = false"><component :is="item.icon" :size="18" /><span>{{ item.label }}</span><em v-if="item.badge">{{ item.badge }}</em></RouterLink>
      </nav>
      <div class="admin-sidebar-help"><TriangleAlert :size="17" /><div><b>安全提醒</b><p>请勿在公共设备上保持登录状态。</p></div></div>
      <div class="admin-account-card"><span class="admin-account-avatar">管</span><div><b>{{ currentUser?.name || '系统管理员' }}</b><small>{{ currentUser?.studentId || 'admin' }} · 管理员</small></div><button aria-label="退出登录" @click="logout"><LogOut :size="17" /></button></div>
    </aside>

    <div v-if="sidebarOpen" class="admin-sidebar-mask" @click="sidebarOpen = false"></div>

    <main class="admin-console-main">
      <header class="admin-console-header">
        <div class="admin-header-title"><button class="admin-sidebar-toggle" @click="sidebarOpen = true"><Menu :size="20" /></button><div><span>ADMIN / {{ section.toUpperCase() }}</span><h1>{{ sectionMeta[section][0] }}</h1><p>{{ sectionMeta[section][1] }}</p></div></div>
        <div class="admin-header-tools"><div class="admin-global-search"><Search :size="17" /><input placeholder="搜索编号、用户或物品" /></div><button class="admin-notification"><Bell :size="18" /><span>5</span></button><div class="admin-date"><b>2026.07.22</b><span>星期三</span></div></div>
      </header>

      <div class="admin-console-content">
        <template v-if="section === 'dashboard'">
          <section class="admin-metrics">
            <article v-for="metric in metrics" :key="metric.label"><div class="admin-metric-icon" :class="metric.tone"><component :is="metric.icon" :size="21" /></div><div><span>{{ metric.label }}</span><strong>{{ metric.value }}</strong></div><em>{{ metric.change }}</em></article>
          </section>

          <section class="admin-dashboard-grid">
            <article class="admin-panel admin-trend-panel">
              <header><div><span>WEEKLY OVERVIEW</span><h2>近七日信息发布趋势</h2></div><button>本周 <ChevronDown :size="14" /></button></header>
              <div class="admin-chart"><div class="admin-chart-axis"><span>80</span><span>60</span><span>40</span><span>20</span><span>0</span></div><div class="admin-bars"><div v-for="(value, index) in trend" :key="index" class="admin-bar-column"><div class="admin-bar-track"><span :style="{ height: `${value}%` }"><i>{{ value }}</i></span></div><small>{{ trendDays[index] }}</small></div></div></div>
            </article>
            <article class="admin-panel admin-status-panel">
              <header><div><span>TODAY STATUS</span><h2>今日处理进度</h2></div></header>
              <div class="admin-ring"><div><strong>76%</strong><span>已处理</span></div></div>
              <dl><div><dt><i class="green"></i>已完成</dt><dd>32</dd></div><div><dt><i class="amber"></i>处理中</dt><dd>10</dd></div><div><dt><i class="gray"></i>待处理</dt><dd>08</dd></div></dl>
            </article>
          </section>

          <section class="admin-panel admin-review-preview">
            <header><div><span>REVIEW QUEUE</span><h2>待审核信息</h2></div><RouterLink to="/admin/reviews">查看全部 <ArrowUpRight :size="14" /></RouterLink></header>
            <div class="admin-table-wrap"><table><thead><tr><th>信息编号</th><th>物品信息</th><th>类型</th><th>发布人</th><th>地点</th><th>时间</th><th>风险</th><th>操作</th></tr></thead><tbody><tr v-for="row in reviewRows" :key="row.id"><td><code>{{ row.id }}</code></td><td><b>{{ row.title }}</b></td><td><span class="admin-type-tag">{{ row.type }}</span></td><td>{{ row.user }}</td><td>{{ row.place }}</td><td>{{ row.time }}</td><td><span :class="['admin-risk', { urgent: row.risk === '加急' }]">{{ row.risk }}</span></td><td><button class="admin-table-action" @click="action(`正在审核 ${row.id}`)">审核</button></td></tr></tbody></table></div>
          </section>
        </template>

        <template v-else-if="section === 'reviews'">
          <section class="admin-section-toolbar"><div class="admin-filter-tabs"><button class="active">待审核 <span>8</span></button><button>已通过</button><button>已驳回</button></div><div><button><Search :size="15" /> 筛选</button><button class="primary" @click="action('批量审核功能将在接入后端后启用')"><Check :size="15" /> 批量审核</button></div></section>
          <section class="admin-panel admin-data-panel"><div class="admin-table-wrap"><table><thead><tr><th><input type="checkbox" /></th><th>信息编号</th><th>物品信息</th><th>类型</th><th>发布人</th><th>发现/丢失地点</th><th>提交时间</th><th>状态</th><th>操作</th></tr></thead><tbody><tr v-for="row in reviewRows" :key="row.id"><td><input type="checkbox" /></td><td><code>{{ row.id }}</code></td><td><b>{{ row.title }}</b></td><td>{{ row.type }}</td><td>{{ row.user }}</td><td>{{ row.place }}</td><td>今天 {{ row.time }}</td><td><span class="admin-status pending">{{ row.status }}</span></td><td class="admin-row-actions"><button @click="action(`已通过 ${row.id}`)"><CircleCheck :size="16" /></button><button @click="action(`已驳回 ${row.id}`)"><XCircle :size="16" /></button><button @click="action(`查看 ${row.id} 详情`)"><ExternalLink :size="15" /></button></td></tr></tbody></table></div></section>
        </template>

        <template v-else-if="section === 'claims'">
          <section class="admin-section-toolbar"><div class="admin-filter-tabs"><button class="active">全部认领</button><button>核验中 <span>3</span></button><button>申诉处理</button><button>已完成</button></div><div><button><Search :size="15" /> 筛选</button></div></section>
          <section class="admin-panel admin-data-panel"><div class="admin-table-wrap"><table><thead><tr><th>申请编号</th><th>关联物品</th><th>申请人</th><th>发布人</th><th>申请时间</th><th>当前进度</th><th>操作</th></tr></thead><tbody><tr v-for="row in claimRows" :key="row.id"><td><code>{{ row.id }}</code></td><td><b>{{ row.item }}</b></td><td>{{ row.applicant }}</td><td>{{ row.owner }}</td><td>{{ row.time }}</td><td><span :class="['admin-status', { danger: row.status.includes('申诉'), done: row.status.includes('完成') }]">{{ row.status }}</span></td><td><button class="admin-table-action" @click="action(`查看认领申请 ${row.id}`)">处理</button></td></tr></tbody></table></div></section>
        </template>

        <template v-else-if="section === 'users'">
          <section class="admin-user-summary"><article><span>注册用户</span><strong>1,342</strong><small>本月 +86</small></article><article><span>已认证</span><strong>1,286</strong><small>认证率 95.8%</small></article><article><span>受限账号</span><strong>07</strong><small>待复核 2</small></article></section>
          <section class="admin-section-toolbar"><div class="admin-filter-tabs"><button class="active">全部用户</button><button>待认证</button><button>受限账号</button></div><div><button><Search :size="15" /> 搜索用户</button></div></section>
          <section class="admin-panel admin-data-panel"><div class="admin-table-wrap"><table><thead><tr><th>用户</th><th>学号</th><th>所属学院</th><th>发布数</th><th>信用分</th><th>认证状态</th><th>操作</th></tr></thead><tbody><tr v-for="row in userRows" :key="row.id"><td><div class="admin-user-cell"><span>{{ row.name.slice(0,1) }}</span><b>{{ row.name }}</b></div></td><td><code>{{ row.id }}</code></td><td>{{ row.college }}</td><td>{{ row.posts }}</td><td><b>{{ row.credit }}</b></td><td><span :class="['admin-status', { pending: row.status === '待认证', danger: row.status === '受限' }]">{{ row.status }}</span></td><td><button class="admin-table-action" @click="action(`查看用户 ${row.name}`)">详情</button></td></tr></tbody></table></div></section>
        </template>

        <template v-else-if="section === 'notices'">
          <section class="admin-section-toolbar"><div><b>公告列表</b><span>共 {{ notices.length }} 条</span></div><div><button class="primary" @click="action('已打开新建公告编辑器')"><Plus :size="15" /> 新建公告</button></div></section>
          <section class="admin-notice-grid"><article v-for="notice in notices" :key="notice.title" class="admin-panel"><div class="admin-notice-icon"><Megaphone :size="20" /></div><span>{{ notice.scope }}</span><h2>{{ notice.title }}</h2><p>发布时间：{{ notice.date }}</p><footer><b :class="{ draft: notice.status === '草稿' }">{{ notice.status }}</b><button @click="action(`编辑公告：${notice.title}`)">编辑</button></footer></article></section>
        </template>

        <template v-else>
          <section class="admin-log-alert"><TriangleAlert :size="19" /><div><b>安全日志不可删除</b><p>所有管理员操作将保留完整记录，用于异常追溯和安全审计。</p></div></section>
          <section class="admin-section-toolbar"><div class="admin-filter-tabs"><button class="active">全部日志</button><button>重要操作</button><button>安全警告</button></div><div><button><Search :size="15" /> 筛选日志</button></div></section>
          <section class="admin-panel admin-data-panel"><div class="admin-table-wrap"><table><thead><tr><th>操作时间</th><th>管理员</th><th>操作内容</th><th>关联对象</th><th>IP 地址</th><th>级别</th><th>详情</th></tr></thead><tbody><tr v-for="row in logRows" :key="row.time"><td><code>{{ row.time }}</code></td><td>{{ row.admin }}</td><td><b>{{ row.action }}</b></td><td>{{ row.target }}</td><td><code>{{ row.ip }}</code></td><td><span :class="['admin-log-level', { warn: row.level === '警告', important: row.level === '重要' }]">{{ row.level }}</span></td><td><button class="admin-icon-action" @click="action('查看日志详情')"><MoreHorizontal :size="17" /></button></td></tr></tbody></table></div></section>
        </template>
      </div>
    </main>
  </div>
</template>
