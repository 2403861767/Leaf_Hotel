<template>
  <div class="room-status">
    <div class="page-header">
      <h2>房态图</h2>
      <div class="header-actions">
        <el-select v-model="floorFilter" placeholder="全部楼层" clearable style="width: 120px">
          <el-option v-for="f in floors" :key="f" :label="`${f}楼`" :value="f" />
        </el-select>
        <el-select v-model="typeFilter" placeholder="全部房型" clearable style="width: 150px">
          <el-option v-for="t in roomTypes" :key="t.id" :label="t.typeName" :value="t.id" />
        </el-select>
        <el-button type="danger" @click="loadData">
          <el-icon><Refresh /></el-icon>刷新
        </el-button>
      </div>
    </div>

    <div class="legend">
      <span v-for="item in legendList" :key="item.label" class="legend-item">
        <span class="legend-dot" :style="{ background: item.color }"></span>
        {{ item.label }}
      </span>
    </div>

    <div v-loading="loading" class="room-grid">
      <div
        v-for="room in filteredRooms"
        :key="room.id"
        class="room-card"
        :class="'room-' + room.status"
        @click="showDetail(room)"
      >
        <div class="room-number">{{ room.roomNumber }}</div>
        <div class="room-type">{{ room.typeName || '-' }}</div>
        <div class="room-status-tag">{{ statusLabel(room.status) }}</div>
      </div>
    </div>

    <el-dialog v-model="detailVisible" title="房间详情" width="500px">
      <div v-if="currentRoom" class="room-detail">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="房号">{{ currentRoom.roomNumber }}</el-descriptions-item>
          <el-descriptions-item label="楼层">{{ currentRoom.floor }}F</el-descriptions-item>
          <el-descriptions-item label="房型">{{ currentRoom.typeName || '-' }}</el-descriptions-item>
          <el-descriptions-item label="房态">
            <el-tag :type="statusTagType(currentRoom.status)">{{ statusLabel(currentRoom.status) }}</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="面积" v-if="currentRoom.area">{{ currentRoom.area }}m²</el-descriptions-item>
          <el-descriptions-item label="床型" v-if="currentRoom.bedType">{{ currentRoom.bedType }}</el-descriptions-item>
          <el-descriptions-item label="价格" v-if="currentRoom.basePrice">¥{{ currentRoom.basePrice }}</el-descriptions-item>
        </el-descriptions>
        <div v-if="currentRoom.occupiedInfo" class="occupied-info" style="margin-top: 16px;">
          <h4>当前住客信息</h4>
          <el-descriptions :column="2" border>
            <el-descriptions-item label="客人姓名">{{ currentRoom.occupiedInfo.guestName }}</el-descriptions-item>
            <el-descriptions-item label="入住时间">{{ currentRoom.occupiedInfo.checkInTime }}</el-descriptions-item>
            <el-descriptions-item label="预计离店">{{ currentRoom.occupiedInfo.expectedCheckOut }}</el-descriptions-item>
          </el-descriptions>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { getStatusMap, getRoomDetail } from '../api/room'
import { Refresh } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'

const loading = ref(false)
const rooms = ref([])
const roomTypes = ref([])
const floors = ref([])
const floorFilter = ref(null)
const typeFilter = ref(null)
const detailVisible = ref(false)
const currentRoom = ref(null)

const legendList = [
  { label: '可售', color: '#48762E' },
  { label: '入住中', color: '#C04848' },
  { label: '脏房', color: '#e6a23c' },
  { label: '维修', color: '#909399' }
]

const filteredRooms = computed(() => {
  return rooms.value.filter(r => {
    if (floorFilter.value && r.floor !== floorFilter.value) return false
    if (typeFilter.value && r.roomTypeId !== typeFilter.value) return false
    return true
  })
})

function statusLabel(s) {
  const map = { available: '可售', occupied: '入住中', dirty: '脏房', maintenance: '维修' }
  return map[s] || s
}

function statusTagType(s) {
  const map = { available: 'success', occupied: 'danger', dirty: 'warning', maintenance: 'info' }
  return map[s] || ''
}

async function loadData() {
  loading.value = true
  try {
    const res = await getStatusMap()
    const data = res.data || {}
    const all = []
    const typeSet = new Set()
    const floorSet = new Set()
    for (const [status, list] of Object.entries(data)) {
      for (const room of list) {
        all.push({ ...room, status })
        if (room.roomTypeId) typeSet.add(room.roomTypeId)
        if (room.floor) floorSet.add(room.floor)
      }
    }
    rooms.value = all
    roomTypes.value = Array.from(typeSet).map(id => ({ id, typeName: data[id]?.typeName || `房型${id}` }))
    floors.value = Array.from(floorSet).sort((a, b) => a - b)
  } catch (e) {
    ElMessage.error('加载房态数据失败')
  }
  loading.value = false
}

async function showDetail(room) {
  try {
    const res = await getRoomDetail(room.id)
    currentRoom.value = res.data
    detailVisible.value = true
  } catch (e) {
    ElMessage.error('获取房间详情失败')
  }
}

onMounted(loadData)
</script>

<style scoped>
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}
.page-header h2 {
  margin: 0;
  font-size: 20px;
}
.header-actions {
  display: flex;
  gap: 8px;
}
.legend {
  display: flex;
  gap: 20px;
  margin-bottom: 16px;
  padding: 12px 16px;
  background: #fff;
  border-radius: 8px;
}
.legend-item {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 13px;
  color: #606266;
}
.legend-dot {
  width: 10px;
  height: 10px;
  border-radius: 50%;
}
.room-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(140px, 1fr));
  gap: 12px;
}
.room-card {
  padding: 16px;
  border-radius: 10px;
  cursor: pointer;
  transition: transform 0.2s, box-shadow 0.2s;
  text-align: center;
  border: 2px solid transparent;
}
.room-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(0,0,0,0.1);
}
.room-available {
  background: #f0f9eb;
  border-color: #b3e19d;
}
.room-occupied {
  background: #fef0f0;
  border-color: #fab6b6;
}
.room-dirty {
  background: #fdf6ec;
  border-color: #f5dab1;
}
.room-maintenance {
  background: #f5f7fa;
  border-color: #dcdfe6;
}
.room-number {
  font-size: 20px;
  font-weight: 700;
  margin-bottom: 4px;
}
.room-type {
  font-size: 12px;
  color: #909399;
  margin-bottom: 6px;
}
.room-status-tag {
  font-size: 12px;
  padding: 2px 10px;
  border-radius: 10px;
  display: inline-block;
}
.room-available .room-status-tag { background: #b3e19d; color: #2a5c1a; }
.room-occupied .room-status-tag { background: #fab6b6; color: #8a3a3a; }
.room-dirty .room-status-tag { background: #f5dab1; color: #8a6a3a; }
.room-maintenance .room-status-tag { background: #dcdfe6; color: #606266; }
</style>
