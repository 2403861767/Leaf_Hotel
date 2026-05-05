/**
 * Checkout API —— 对应后端 CheckoutController (/api/v1/checkouts)
 */
import request from './index'

/** 办理退房结账：POST /checkouts */
export function createCheckout(data) {
  return request.post('/checkouts', data)
}

/** 分页查询退房记录：GET /checkouts */
export function queryCheckouts(params) {
  return request.get('/checkouts', { params })
}

/** 预计算退款金额（只读，不写入）：POST /checkouts/calculate-refund */
export function calculateRefund(registrationId) {
  return request.post('/checkouts/calculate-refund', null, { params: { registrationId } })
}
