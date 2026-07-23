import { ref } from 'vue'
import { readAuthSession, removeAuthSession, saveAuthSession } from './api/auth'

const initialSession = readAuthSession()

export const toast = ref('')
export const favorites = ref(new Set([2]))
export const currentUser = ref(initialSession?.user || null)
export const isLoggedIn = ref(Boolean(initialSession?.token && initialSession?.user))
export const isAdminLoggedIn = ref(initialSession?.user?.role === 'ADMIN')

export function setAuthSession(session, remember = false) {
  saveAuthSession(session, remember)
  currentUser.value = session.user
  isLoggedIn.value = true
  isAdminLoggedIn.value = session.user?.role === 'ADMIN'
}

export function clearAuthSession() {
  removeAuthSession()
  currentUser.value = null
  isLoggedIn.value = false
  isAdminLoggedIn.value = false
}

let toastTimer

export function showToast(message) {
  toast.value = message
  window.clearTimeout(toastTimer)
  toastTimer = window.setTimeout(() => {
    toast.value = ''
  }, 2600)
}

export function toggleFavorite(id) {
  const next = new Set(favorites.value)
  if (next.has(id)) {
    next.delete(id)
    showToast('已取消收藏')
  } else {
    next.add(id)
    showToast('已收藏，后续动态会提醒你')
  }
  favorites.value = next
}
