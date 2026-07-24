import { API_BASE, readAuthSession } from './auth'

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
  const data = response.status === 204 ? null : await response.json().catch(() => ({}))
  if (!response.ok) throw new Error(data.message || '失物信息服务暂时不可用，请稍后重试')
  return data
}

export const createItem = (payload) => request('/items', { method: 'POST', body: JSON.stringify(payload) })
export const fetchItems = () => request('/items')
export const fetchMyItems = () => request('/items/mine')
export const fetchItem = (id) => request(`/items/${id}`)
export const fetchFavorites = () => request('/favorites')
export const addFavorite = (id) => request(`/favorites/${id}`, { method: 'PUT' })
export const removeFavorite = (id) => request(`/favorites/${id}`, { method: 'DELETE' })
