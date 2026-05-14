const TOKEN_KEY = 'course-share-token'
const USER_KEY = 'course-share-user'

export function getStoredToken() {
  return localStorage.getItem(TOKEN_KEY) || ''
}

export function getStoredUser() {
  const raw = localStorage.getItem(USER_KEY)
  if (!raw) return null
  try {
    return JSON.parse(raw)
  } catch {
    return null
  }
}

export function storeSession(loginData) {
  localStorage.setItem(TOKEN_KEY, loginData.token)
  localStorage.setItem(USER_KEY, JSON.stringify(loginData))
}

export function clearSession() {
  localStorage.removeItem(TOKEN_KEY)
  localStorage.removeItem(USER_KEY)
}

export async function apiRequest(path, options = {}) {
  const headers = new Headers(options.headers || {})
  const token = getStoredToken()

  if (token) {
    headers.set('Authorization', `Bearer ${token}`)
  }

  if (options.body && !(options.body instanceof FormData)) {
    headers.set('Content-Type', 'application/json; charset=utf-8')
  }

  let response
  try {
    response = await fetch(path, {
      ...options,
      headers,
      body: options.body && !(options.body instanceof FormData)
        ? JSON.stringify(options.body)
        : options.body
    })
  } catch {
    throw new Error('后端服务未连接，请先启动后端或 Docker Compose')
  }

  const text = await response.text()
  if (!text) {
    throw new Error(formatHttpError(response.status))
  }

  let result
  try {
    result = JSON.parse(text)
  } catch {
    throw new Error(formatHttpError(response.status))
  }
  if (result.code !== 200) {
    throw new Error(normalizeErrorMessage(result.message))
  }
  return result.data
}

function formatHttpError(status) {
  if (status >= 500) return '后端服务异常，请查看后端日志'
  if (status === 404) return '接口不存在，请检查后端路由或 Nginx 代理'
  return `请求失败：HTTP ${status}`
}

function normalizeErrorMessage(message) {
  if (!message) return '请求失败'
  const technicalHints = [
    '### Error querying database',
    'CannotGetJdbcConnectionException',
    'Communications link failure',
    'Connection refused',
    '<!doctype html',
    '<html'
  ]
  if (technicalHints.some(hint => message.includes(hint))) {
    return '后端依赖未就绪，请确认 MySQL、Redis、MinIO 和后端服务已启动'
  }
  return message
}
