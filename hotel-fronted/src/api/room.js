import request from './index'

export function getStatusMap() {
  return request.get('/rooms/status-map')
}

export function getRoomDetail(id) {
  return request.get(`/rooms/${id}`)
}

export function getAvailableRooms(roomTypeId) {
  return request.get('/rooms/available', { params: { roomTypeId } })
}
