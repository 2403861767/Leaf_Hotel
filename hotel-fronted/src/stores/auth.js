import { defineStore } from 'pinia'
import { login, logout, getCurrentUser } from '../api/auth'

/**
 * 认证 Store —— 管理 JWT Token、用户信息与权限的前端状态。
 *
 * 持久化策略：Token 与用户基本信息手动同步到 localStorage，
 * 页面刷新后从 localStorage 恢复状态，无需重新登录。
 * 权限列表（permissions）仅在内存中，刷新后需重新 fetchUserInfo 拉取。
 *
 * 路由守卫依赖 localStorage 中的 token 判断登录状态，
 * Axios 请求拦截器从该 Store 读取 token 附加到 Authorization 头。
 */
export const useAuthStore = defineStore('auth', {
  state: () => ({
    token: localStorage.getItem('token') || '',
    user: JSON.parse(localStorage.getItem('user') || 'null'),
    permissions: []
  }),
  getters: {
    isLoggedIn: (state) => !!state.token,
    userRole: (state) => state.user?.role || '',
    userName: (state) => state.user?.realName || '',
    hasPermission: (state) => (code) => state.permissions.includes(code)
  },
  actions: {
    /**
     * 登录：调用后端 /auth/login 获取 JWT Token，持久化到 localStorage。
     * 注意：登录后还需调 fetchUserInfo 拉取权限列表。
     */
    async login(credentials) {
      const res = await login(credentials)
      const { token, tokenType, user } = res.data
      this.token = token
      this.user = user
      localStorage.setItem('token', token)
      localStorage.setItem('user', JSON.stringify(user))
      return res.data
    },
    /** 拉取当前用户信息和权限列表，失败时自动登出 */
    async fetchUserInfo() {
      try {
        const res = await getCurrentUser()
        this.user = {
          id: res.data.id,
          username: res.data.username,
          realName: res.data.realName,
          role: res.data.role
        }
        this.permissions = res.data.permissions || []
        localStorage.setItem('user', JSON.stringify(this.user))
      } catch (e) {
        this.logout()
      }
    },
    /**
     * 登出：调后端 /auth/logout 通知服务端（非必须，无状态架构），
     * 清除前端所有状态和 localStorage，路由守卫会将用户重定向到登录页。
     */
    logout() {
      logout().catch(() => {})
      this.token = ''
      this.user = null
      this.permissions = []
      localStorage.removeItem('token')
      localStorage.removeItem('user')
    }
  }
})
