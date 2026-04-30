import request from './index'

export function createDeposit(data) {
  return request.post('/deposits', data)
}

export function queryDeposits(registrationId) {
  return request.get('/deposits', { params: { registrationId } })
}

export function refundDeposit(id) {
  return request.post(`/deposits/${id}/refund`)
}
