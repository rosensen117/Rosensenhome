import { createRouter, createWebHistory } from 'vue-router'
import HomeView from './views/HomeView.vue'
import { isAdminLoggedIn, isLoggedIn } from './state'

const router = createRouter({
  history: createWebHistory(),
  scrollBehavior() {
    return { top: 0, behavior: 'smooth' }
  },
  routes: [
    { path: '/', name: 'home', component: HomeView, meta: { title: '拾光 · 校园失物招领' } },
    { path: '/hall', name: 'hall', component: () => import('./views/HallView.vue'), meta: { title: '寻物大厅 · 拾光' } },
    { path: '/items/:id', name: 'item-detail', component: () => import('./views/DetailView.vue'), meta: { title: '物品详情 · 拾光' } },
    { path: '/publish', name: 'publish', component: () => import('./views/PublishView.vue'), meta: { title: '发布信息 · 拾光', requiresAuth: true } },
    { path: '/clues', name: 'clues', component: () => import('./views/CluesView.vue'), meta: { title: '线索动态 · 拾光' } },
    { path: '/guide', name: 'guide', component: () => import('./views/GuideView.vue'), meta: { title: '认领指南 · 拾光' } },
    { path: '/profile', name: 'profile', component: () => import('./views/ProfileView.vue'), meta: { title: '个人中心 · 拾光', requiresAuth: true } },
    { path: '/login', name: 'login', component: () => import('./views/AuthView.vue'), meta: { title: '登录 · 拾光', mode: 'login' } },
    { path: '/register', name: 'register', component: () => import('./views/AuthView.vue'), meta: { title: '注册 · 拾光', mode: 'register' } },
    { path: '/admin/login', name: 'admin-login', component: () => import('./views/AdminLoginView.vue'), meta: { title: '管理员登录 · 拾光管理端', standalone: true } },
    { path: '/admin', redirect: '/admin/dashboard' },
    { path: '/admin/dashboard', name: 'admin-dashboard', component: () => import('./views/AdminConsoleView.vue'), meta: { title: '管理工作台 · 拾光管理端', standalone: true, requiresAdmin: true, adminSection: 'dashboard' } },
    { path: '/admin/reviews', name: 'admin-reviews', component: () => import('./views/AdminConsoleView.vue'), meta: { title: '信息审核 · 拾光管理端', standalone: true, requiresAdmin: true, adminSection: 'reviews' } },
    { path: '/admin/claims', name: 'admin-claims', component: () => import('./views/AdminConsoleView.vue'), meta: { title: '认领管理 · 拾光管理端', standalone: true, requiresAdmin: true, adminSection: 'claims' } },
    { path: '/admin/users', name: 'admin-users', component: () => import('./views/AdminConsoleView.vue'), meta: { title: '用户管理 · 拾光管理端', standalone: true, requiresAdmin: true, adminSection: 'users' } },
    { path: '/admin/notices', name: 'admin-notices', component: () => import('./views/AdminConsoleView.vue'), meta: { title: '公告管理 · 拾光管理端', standalone: true, requiresAdmin: true, adminSection: 'notices' } },
    { path: '/admin/logs', name: 'admin-logs', component: () => import('./views/AdminConsoleView.vue'), meta: { title: '系统日志 · 拾光管理端', standalone: true, requiresAdmin: true, adminSection: 'logs' } },
    { path: '/:pathMatch(.*)*', name: 'not-found', component: () => import('./views/NotFoundView.vue'), meta: { title: '页面未找到 · 拾光' } },
  ],
})

router.beforeEach((to) => {
  if (to.meta.requiresAdmin && !isAdminLoggedIn.value) {
    return { path: '/admin/login', query: { redirect: to.fullPath } }
  }
  if (to.meta.requiresAuth && !isLoggedIn.value) {
    return { path: '/login', query: { redirect: to.fullPath } }
  }
  if ((to.path === '/login' || to.path === '/register') && isLoggedIn.value) return '/profile'
  if (to.path === '/admin/login' && isAdminLoggedIn.value) return '/admin/dashboard'
})

router.afterEach((to) => {
  document.title = to.meta.title || '拾光 · 校园失物招领'
})

export default router
