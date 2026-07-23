<script setup>
import {
  ArrowRight,
  BadgeCheck,
  Check,
  Compass,
  Eye,
  EyeOff,
  KeyRound,
  LockKeyhole,
  Mail,
  Phone,
  ShieldCheck,
  UserRound,
} from '@lucide/vue'
import { computed, reactive, ref, watch } from 'vue'
import { RouterLink, useRoute, useRouter } from 'vue-router'
import { loginAccount, registerAccount } from '../api/auth'
import { setAuthSession, showToast } from '../state'

const route = useRoute()
const router = useRouter()
const showPassword = ref(false)
const submitting = ref(false)
const error = ref('')

const isRegister = computed(() => route.meta.mode === 'register')
const form = reactive({
  account: '',
  name: '',
  studentId: '',
  phone: '',
  email: '',
  password: '',
  confirmPassword: '',
  remember: true,
  agreement: false,
})

watch(() => route.meta.mode, () => {
  error.value = ''
  showPassword.value = false
})

function validate() {
  if (isRegister.value) {
    if (!form.name.trim() || !form.studentId.trim() || !form.phone.trim()) return '请完整填写姓名、学号和手机号码'
    if (!/^1\d{10}$/.test(form.phone)) return '请输入正确的 11 位手机号码'
    if (form.password.length < 6) return '密码长度不能少于 6 位'
    if (form.password !== form.confirmPassword) return '两次输入的密码不一致'
    if (!form.agreement) return '请先阅读并同意用户协议与隐私说明'
  } else {
    if (!form.account.trim() || !form.password) return '请输入学号或手机号和密码'
  }
  return ''
}

async function submit() {
  error.value = validate()
  if (error.value) return

  submitting.value = true
  try {
    const result = isRegister.value
      ? await registerAccount({
          name: form.name.trim(),
          studentId: form.studentId.trim(),
          phone: form.phone.trim(),
          email: form.email.trim() || null,
          password: form.password,
        })
      : await loginAccount(form.account.trim(), form.password)
    setAuthSession(result, isRegister.value || form.remember)
    showToast(isRegister.value ? '注册成功，欢迎加入拾光' : '登录成功，欢迎回来')
    router.push(route.query.redirect || '/profile')
  } catch (exception) {
    error.value = exception.message
  } finally {
    submitting.value = false
  }
}
</script>

<template>
  <main class="auth-page">
    <section class="auth-layout">
      <div class="auth-story">
        <RouterLink to="/" class="auth-story-brand"><span><Compass :size="20" /></span><b>拾光</b><em>校园失物招领</em></RouterLink>
        <div class="auth-story-copy">
          <span class="auth-index">ACCOUNT / 01</span>
          <h1 v-if="isRegister">从一次校园互助，<br />开始认识彼此。</h1>
          <h1 v-else>欢迎回来，<br />这里有你的新线索。</h1>
          <p>{{ isRegister ? '完成校园身份登记，安全发布、沟通和认领。' : '登录后查看收藏、发布记录与最新匹配进展。' }}</p>
        </div>
        <div class="auth-trust-list">
          <div><ShieldCheck :size="19" /><span><b>校园身份核验</b><small>减少冒领与虚假信息</small></span></div>
          <div><BadgeCheck :size="19" /><span><b>隐私信息保护</b><small>联系方式默认不公开</small></span></div>
          <div><KeyRound :size="19" /><span><b>安全认领流程</b><small>特征核对后完成交接</small></span></div>
        </div>
        <div class="auth-story-note">“愿每一次举手之劳，都有回音。”</div>
      </div>

      <div class="auth-form-side">
        <div class="auth-form-card">
          <div class="auth-switch" aria-label="登录注册切换">
            <RouterLink to="/login" :class="{ active: !isRegister }">登录</RouterLink>
            <RouterLink to="/register" :class="{ active: isRegister }">注册</RouterLink>
          </div>

          <header class="auth-form-head">
            <span>{{ isRegister ? 'CREATE ACCOUNT' : 'WELCOME BACK' }}</span>
            <h2>{{ isRegister ? '创建校园账号' : '登录拾光' }}</h2>
            <p>{{ isRegister ? '请使用真实校园信息进行登记' : '使用学号或绑定的手机号登录' }}</p>
          </header>

          <form class="auth-form" @submit.prevent="submit">
            <template v-if="isRegister">
              <div class="auth-form-row">
                <label><span>姓名</span><div><UserRound :size="17" /><input v-model="form.name" autocomplete="name" placeholder="请输入真实姓名" /></div></label>
                <label><span>学号</span><div><BadgeCheck :size="17" /><input v-model="form.studentId" inputmode="numeric" placeholder="请输入学号" /></div></label>
              </div>
              <label><span>手机号码</span><div><Phone :size="17" /><input v-model="form.phone" inputmode="tel" autocomplete="tel" maxlength="11" placeholder="用于接收重要认领通知" /></div></label>
              <label><span>校园邮箱 <em>选填</em></span><div><Mail :size="17" /><input v-model="form.email" type="email" autocomplete="email" placeholder="name@campus.edu.cn" /></div></label>
            </template>

            <label v-else><span>学号或手机号</span><div><UserRound :size="17" /><input v-model="form.account" autocomplete="username" placeholder="请输入学号或手机号码" /></div></label>

            <label><span>密码</span><div><LockKeyhole :size="17" /><input v-model="form.password" :type="showPassword ? 'text' : 'password'" :autocomplete="isRegister ? 'new-password' : 'current-password'" :placeholder="isRegister ? '至少 6 位字符' : '请输入密码'" /><button type="button" :aria-label="showPassword ? '隐藏密码' : '显示密码'" @click="showPassword = !showPassword"><EyeOff v-if="showPassword" :size="17" /><Eye v-else :size="17" /></button></div></label>
            <label v-if="isRegister"><span>确认密码</span><div><LockKeyhole :size="17" /><input v-model="form.confirmPassword" :type="showPassword ? 'text' : 'password'" autocomplete="new-password" placeholder="请再次输入密码" /></div></label>

            <div v-if="!isRegister" class="auth-form-options"><label><input v-model="form.remember" type="checkbox" /><span><Check :size="11" /></span>记住登录状态</label><button type="button" @click="showToast('找回密码功能将在接入后端后启用')">忘记密码？</button></div>
            <label v-else class="auth-agreement"><input v-model="form.agreement" type="checkbox" /><span><Check :size="11" /></span><p>我已阅读并同意《用户服务协议》和《隐私说明》</p></label>

            <div v-if="error" class="auth-error" role="alert">{{ error }}</div>
            <button class="auth-submit" type="submit" :disabled="submitting"><span>{{ submitting ? '处理中…' : (isRegister ? '注册账号' : '登录') }}</span><ArrowRight :size="17" /></button>
          </form>

          <p class="auth-bottom-link">{{ isRegister ? '已经有账号？' : '还没有校园账号？' }} <RouterLink :to="isRegister ? '/login' : '/register'">{{ isRegister ? '返回登录' : '立即注册' }}</RouterLink></p>
          <div v-if="!isRegister" class="auth-admin-entry">
            <RouterLink to="/admin/login"><ShieldCheck :size="14" /> 管理员入口</RouterLink>
            <span>仅限学校授权人员</span>
          </div>
        </div>
      </div>
    </section>
  </main>
</template>
