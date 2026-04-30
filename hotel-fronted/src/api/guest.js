import request from './index'

export function queryGuests(params) {
  return request.get('/guests', { params })
}

export function getGuestDetail(id) {
  return request.get(`/guests/${id}`)
}

export function createGuest(data) {
  return request.post('/guests', data)
}

export function updateGuest(id, data) {
  return request.put(`/guests/${id}`, data)
}
