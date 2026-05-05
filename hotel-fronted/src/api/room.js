/**
 * Room API —— 对应后端 RoomController (/api/v1/rooms)
 */
import request from './index'

/** 房态地图：GET /rooms/status-map，返回 { available: [], occupied: [], dirty: [], maintenance: [] } */
export function getStatusMap() {
  return request.get('/rooms/status-map')
}

/** 房间详情（含房型信息）：GET /rooms/:id */
export function getRoomDetail(id) {
  return request.get(`/rooms/${id}`)
}

/** 可售房间列表，可选按房型过滤：GET /rooms/available?roomTypeId= */
export function getAvailableRooms(roomTypeId) {
  return request.get('/rooms/available', { params: { roomTypeId } })
}
