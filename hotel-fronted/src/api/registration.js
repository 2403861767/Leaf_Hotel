import request from './index'

export function queryRegistrations(params) {
  return request.get('/checkins', { params })
}
