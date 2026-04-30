import { defineStore } from 'pinia'
import { login, logout, getCurrentUser } from '../api/auth'

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
    async login(credentials) {
      const res = await login(credentials)
      const { token, tokenType, user } = res.data
      this.token = token
      this.user = user
      localStorage.setItem('token', token)
      localStorage.setItem('user', JSON.stringify(user))
      return res.data
    },
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
