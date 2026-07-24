import { ref } from 'vue'
import { readAuthSession, removeAuthSession, replaceSessionUser, saveAuthSession } from './api/auth'
import { addFavorite, fetchFavorites, removeFavorite } from './api/items'

const initialSession = readAuthSession()

export const toast = ref('')
export const favorites = ref(new Set())
export const currentUser = ref(initialSession?.user || null)
export const isLoggedIn = ref(Boolean(initialSession?.token && initialSession?.user))
export const isAdminLoggedIn = ref(initialSession?.user?.role === 'ADMIN')

export function setAuthSession(session, remember = false) {
  saveAuthSession(session, remember)
  currentUser.value = session.user
  isLoggedIn.value = true
  isAdminLoggedIn.value = session.user?.role === 'ADMIN'
  if (session.user?.role !== 'ADMIN') loadFavorites().catch(() => {})
}

export function clearAuthSession() {
  removeAuthSession()
  currentUser.value = null
  isLoggedIn.value = false
  isAdminLoggedIn.value = false
  favorites.value = new Set()
}

export function syncCurrentUser(user) {
  currentUser.value = user
  replaceSessionUser(user)
}

let toastTimer

export function showToast(message) {
  toast.value = message
  window.clearTimeout(toastTimer)
  toastTimer = window.setTimeout(() => {
    toast.value = ''
  }, 2600)
}

export async function loadFavorites() {
  if (!isLoggedIn.value) {
    favorites.value = new Set()
    return []
  }
  const records = await fetchFavorites()
  favorites.value = new Set(records.map((item) => item.id))
  return records
}

export async function toggleFavorite(id) {
  if (!isLoggedIn.value) {
    showToast('请先登录后再收藏')
    return false
  }
  const next = new Set(favorites.value)
  const wasFavorite = next.has(id)
  if (wasFavorite) {
    next.delete(id)
  } else {
    next.add(id)
  }
  favorites.value = next
  try {
    if (wasFavorite) await removeFavorite(id)
    else await addFavorite(id)
    showToast(wasFavorite ? '已取消收藏' : '已收藏，后续动态会提醒你')
    return true
  } catch (error) {
    favorites.value = new Set(wasFavorite ? [...next, id] : [...next].filter((value) => value !== id))
    showToast(error.message || '收藏操作失败，请稍后重试')
    return false
  }
}
