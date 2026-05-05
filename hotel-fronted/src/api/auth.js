/**
 * Auth API —— 对应后端 AuthController (/api/v1/auth)
 */
import request from './index'

/** 登录：POST /auth/login，返回 { token, tokenType, expiresIn, user } */
export function login(data) {
  return request.post('/auth/login', data)
}

/** 登出：POST /auth/logout，通知服务端清除 SecurityContext */
export function logout() {
  return request.post('/auth/logout')
}

/** 获取当前用户信息及权限列表：GET /auth/me */
export function getCurrentUser() {
  return request.get('/auth/me')
}
