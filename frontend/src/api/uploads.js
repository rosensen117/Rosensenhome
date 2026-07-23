import { API_BASE, readAuthSession } from './auth'

export async function uploadImage(file) {
  const session = readAuthSession()
  const body = new FormData()
  body.append('file', file)
  const response = await fetch(`${API_BASE}/files/images`, {
    method: 'POST',
    headers: session?.token ? { Authorization: `Bearer ${session.token}` } : {},
    body,
  })
  const data = await response.json().catch(() => ({}))
  if (!response.ok) throw new Error(data.message || '图片上传失败，请稍后重试')
  return data
}
