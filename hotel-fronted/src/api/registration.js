/**
 * Registration API —— 入住登记查询的别名模块。
 *
 * 当前仅提供分页查询入住记录，等同于 queryCheckins(params)。
 * 与 checkin.js 的区别：本模块聚焦于"登记记录"语义，checkin.js 聚焦于"入住操作"语义。
 */
import request from './index'

/** 分页查询入住登记：GET /checkins */
export function queryRegistrations(params) {
  return request.get('/checkins', { params })
}
