/**
 * Checkin API —— 对应后端 CheckinController (/api/v1/checkins)
 */
import request from './index'

/** 办理入住：POST /checkins */
export function createCheckin(data) {
  return request.post('/checkins', data)
}

/** 分页查询入住记录：GET /checkins */
export function queryCheckins(params) {
  return request.get('/checkins', { params })
}

/** 入住详情：GET /checkins/:id */
export function getCheckinDetail(id) {
  return request.get(`/checkins/${id}`)
}

/** 更新入住信息（含换房）：PUT /checkins/:id */
export function updateCheckin(id, data) {
  return request.put(`/checkins/${id}`, data)
}
