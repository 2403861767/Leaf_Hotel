/**
 * Guest API —— 对应后端 GuestController (/api/v1/guests)
 */
import request from './index'

/** 分页查询客人：GET /guests?keyword=&page=&pageSize=，keyword 按姓名/证件号/手机号三字段 OR 搜索 */
export function queryGuests(params) {
  return request.get('/guests', { params })
}

/** 客人详情：GET /guests/:id */
export function getGuestDetail(id) {
  return request.get(`/guests/${id}`)
}

/** 新建客人：POST /guests */
export function createGuest(data) {
  return request.post('/guests', data)
}

/** 更新客人信息：PUT /guests/:id */
export function updateGuest(id, data) {
  return request.put(`/guests/${id}`, data)
}
