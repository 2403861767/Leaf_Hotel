import request from './index'

export function createCheckin(data) {
  return request.post('/checkins', data)
}

export function queryCheckins(params) {
  return request.get('/checkins', { params })
}

export function getCheckinDetail(id) {
  return request.get(`/checkins/${id}`)
}

export function updateCheckin(id, data) {
  return request.put(`/checkins/${id}`, data)
}
