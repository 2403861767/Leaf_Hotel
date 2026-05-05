/**
 * Deposit API —— 对应后端 DepositController (/api/v1/deposits)
 */
import request from './index'

/** 收取押金：POST /deposits，关联入住单号 */
export function createDeposit(data) {
  return request.post('/deposits', data)
}

/** 按入住单号查询押金记录：GET /deposits?registrationId= */
export function queryDeposits(registrationId) {
  return request.get('/deposits', { params: { registrationId } })
}

/** 退还押金：POST /deposits/:id/refund */
export function refundDeposit(id) {
  return request.post(`/deposits/${id}/refund`)
}
