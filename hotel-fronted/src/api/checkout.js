import request from './index'

export function createCheckout(data) {
  return request.post('/checkouts', data)
}

export function queryCheckouts(params) {
  return request.get('/checkouts', { params })
}

export function calculateRefund(registrationId) {
  return request.post('/checkouts/calculate-refund', null, { params: { registrationId } })
}
