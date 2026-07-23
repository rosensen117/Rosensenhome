<script setup>
import {
  ArrowLeft,
  ArrowRight,
  Check,
  Eye,
  EyeOff,
  KeyRound,
  LockKeyhole,
  RefreshCw,
  ShieldCheck,
  UserCog,
} from '@lucide/vue'
import { reactive, ref } from 'vue'
import { RouterLink, useRouter } from 'vue-router'
import { loginAdmin } from '../api/auth'
import { setAuthSession, showToast } from '../state'

const router = useRouter()

const form = reactive({ account: '', password: '', captcha: '', remember: false })
const showPassword = ref(false)
const error = ref('')
const submitting = ref(false)
const captchaCode = ref(createCaptcha())

function createCaptcha() {
  const chars = 'ABCDEFGHJKLMNPQRSTUVWXYZ23456789'
  return Array.from({ length: 4 }, () => chars[Math.floor(Math.random() * chars.length)]).join('')
}

function refreshCaptcha() {
  captchaCode.value = createCaptcha()
  form.captcha = ''
}

async function submit() {
  if (!form.account.trim() || !form.password) {
    error.value = '请输入管理员账号和密码'
    return
  }
  if (form.captcha.trim().toUpperCase() !== captchaCode.value) {
    error.value = '验证码不正确，请重新输入'
    refreshCaptcha()
    return
  }

  error.value = ''
  submitting.value = true
  try {
    const result = await loginAdmin(form.account.trim(), form.password)
    setAuthSession(result, form.remember)
    showToast('管理员身份验证通过')
    refreshCaptcha()
    router.push(router.currentRoute.value.query.redirect || '/admin/dashboard')
  } catch (exception) {
    error.value = exception.message
    refreshCaptcha()
  } finally {
    submitting.value = false
  }
}
</script>

<template>
  <div class="admin-login-page">
    <div class="admin-login-grid">
      <aside class="admin-intro-panel">
        <RouterLink to="/" class="admin-back-link"><ArrowLeft :size="16" /> 返回用户端</RouterLink>
        <div class="admin-brand-lockup">
          <span><ShieldCheck :size="27" /></span>
          <div><b>拾光管理端</b><small>CAMPUS LOST &amp; FOUND ADMIN</small></div>
        </div>

        <div class="admin-intro-copy">
          <span>MANAGEMENT PORTAL</span>
          <h1>守护每一条信息，<br />维护每一次信任。</h1>
          <p>用于失物招领信息审核、认领申诉处理、用户管理和系统数据维护。</p>
        </div>

        <div class="admin-duty-list">
          <div><i>01</i><span><b>内容审核</b><small>核验失物与招领信息的真实性</small></span></div>
          <div><i>02</i><span><b>认领监管</b><small>处理异常认领、举报与申诉</small></span></div>
          <div><i>03</i><span><b>系统维护</b><small>管理分类、公告与运行数据</small></span></div>
        </div>

        <p class="admin-security-note"><LockKeyhole :size="15" /> 管理操作将记录账号、时间与设备信息</p>
      </aside>

      <main class="admin-form-panel">
        <div class="admin-form-card">
          <header>
            <div class="admin-form-icon"><UserCog :size="25" /></div>
            <span>ADMINISTRATOR ONLY</span>
            <h2>管理员登录</h2>
            <p>此入口仅供学校授权的系统管理员使用</p>
          </header>

          <form @submit.prevent="submit">
            <label>
              <span>管理员账号</span>
              <div><UserCog :size="18" /><input v-model="form.account" autocomplete="username" placeholder="请输入管理员账号" /></div>
            </label>
            <label>
              <span>登录密码</span>
              <div><KeyRound :size="18" /><input v-model="form.password" :type="showPassword ? 'text' : 'password'" autocomplete="current-password" placeholder="请输入登录密码" /><button type="button" :aria-label="showPassword ? '隐藏密码' : '显示密码'" @click="showPassword = !showPassword"><EyeOff v-if="showPassword" :size="17" /><Eye v-else :size="17" /></button></div>
            </label>
            <label>
              <span>安全验证码</span>
              <div class="admin-captcha-field"><input v-model="form.captcha" maxlength="4" autocomplete="off" placeholder="输入右侧字符" /><button type="button" class="admin-captcha" aria-label="刷新验证码" @click="refreshCaptcha"><b>{{ captchaCode }}</b><RefreshCw :size="13" /></button></div>
            </label>

            <div class="admin-form-options">
              <label><input v-model="form.remember" type="checkbox" /><span><Check :size="11" /></span>在此设备保持登录</label>
              <button type="button" @click="showToast('请联系超级管理员重置密码')">无法登录？</button>
            </div>

            <div v-if="error" class="admin-login-error" role="alert">{{ error }}</div>
            <button class="admin-login-submit" type="submit" :disabled="submitting"><span>{{ submitting ? '正在验证…' : '安全登录' }}</span><ArrowRight :size="17" /></button>
          </form>

          <div class="admin-form-divider"><span>普通用户入口</span></div>
          <RouterLink to="/login" class="admin-user-login">前往学生登录页面 <ArrowRight :size="15" /></RouterLink>
        </div>

        <footer><span>拾光校园失物招领系统</span><span>管理员入口 · 操作留痕</span></footer>
      </main>
    </div>
  </div>
</template>
