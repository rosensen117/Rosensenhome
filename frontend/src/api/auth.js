const DEFAULT_API_BASE = import.meta.env.DEV ? 'http://localhost:8080/api' : '/api'
export const API_BASE = (import.meta.env.VITE_API_BASE_URL || DEFAULT_API_BASE).replace(/\/$/, '')
const SESSION_KEY = 'shiguang-auth'

export function readAuthSession() {
  for (const storage of [sessionStorage, localStorage]) {
    try {
      const value = storage.getItem(SESSION_KEY)
      if (value) return JSON.parse(value)
    } catch {
      storage.removeItem(SESSION_KEY)
    }
  }
  return null
}

export function saveAuthSession(session, remember = false) {
  localStorage.removeItem(SESSION_KEY)
  sessionStorage.removeItem(SESSION_KEY)
  ;(remember ? localStorage : sessionStorage).setItem(SESSION_KEY, JSON.stringify(session))
}

export function removeAuthSession() {
  localStorage.removeItem(SESSION_KEY)
  sessionStorage.removeItem(SESSION_KEY)
}

async function request(path, options = {}) {
  const session = readAuthSession()
  const response = await fetch(`${API_BASE}${path}`, {
    ...options,
    headers: {
      'Content-Type': 'application/json',
      ...(session?.token ? { Authorization: `Bearer ${session.token}` } : {}),
      ...options.headers,
    },
  })
  const data = await response.json().catch(() => ({}))
  if (!response.ok) throw new Error(data.message || '服务暂时不可用，请稍后重试')
  return data
}

export const registerAccount = (payload) => request('/auth/register', { method: 'POST', body: JSON.stringify(payload) })
export const loginAccount = (account, password) => request('/auth/login', { method: 'POST', body: JSON.stringify({ account, password }) })
export const loginAdmin = (account, password) => request('/auth/admin/login', { method: 'POST', body: JSON.stringify({ account, password }) })
export const fetchCurrentUser = () => request('/auth/me')
export const logoutAccount = () => request('/auth/logout', { method: 'POST' })
