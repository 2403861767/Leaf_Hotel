import axios from 'axios'
import router from '../router'

/**
 * Axios 实例 —— 统一的 HTTP 客户端配置。
 *
 * 拦截器链：
 *   请求 → 自动附加 Bearer Token（从 localStorage 读取）
 *   响应 → 统一解包 res.data；401 时清除本地状态并跳转登录页
 *
 * baseURL 由 vite.config.js 中 proxy 配置转发到后端 8080 端口。
 */
const request = axios.create({
  baseURL: '/api/v1',
  timeout: 30000
})

request.interceptors.request.use(config => {
  const token = localStorage.getItem('token')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

request.interceptors.response.use(
  response => {
    const res = response.data
    if (res.code === 200) {
      return res
    }
    if (res.code === 401) {
      localStorage.removeItem('token')
      localStorage.removeItem('user')
      router.push('/login')
      return Promise.reject(new Error(res.message || '未登录或登录已过期'))
    }
    return Promise.reject(new Error(res.message || '请求失败'))
  },
  error => {
    if (error.response?.status === 401) {
      localStorage.removeItem('token')
      localStorage.removeItem('user')
      router.push('/login')
    }
    return Promise.reject(error)
  }
)

export default request
