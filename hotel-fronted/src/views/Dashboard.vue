<template>
  <div class="dashboard">
    <div class="welcome-card">
      <div class="welcome-text">
        <h2>欢迎回来，{{ authStore.userName }}</h2>
        <p>今天是 {{ currentDate }}，祝您工作愉快！</p>
      </div>
      <div class="welcome-icon">🍁</div>
    </div>

    <el-row :gutter="20" class="stats-row">
      <el-col :span="6" v-for="stat in stats" :key="stat.label">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-info">
              <p class="stat-label">{{ stat.label }}</p>
              <p class="stat-value">{{ stat.value }}</p>
            </div>
            <div class="stat-icon" :style="{ background: stat.bg }">
              <el-icon :size="28" color="#fff">
                <component :is="stat.icon" />
              </el-icon>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20">
      <el-col :span="16">
        <el-card shadow="hover">
          <template #header>
            <span style="font-weight: 600;">在住客人列表</span>
            <el-button text type="primary" style="float: right;" @click="$router.push('/checkins')">
              查看全部
            </el-button>
          </template>
          <el-table :data="inHouseList" stripe style="width: 100%" v-loading="tableLoading">
            <el-table-column prop="id" label="入住单号" width="100" />
            <el-table-column label="客人" width="100">
              <template #default="{ row }">{{ row.guestName || '-' }}</template>
            </el-table-column>
            <el-table-column prop="roomId" label="房号" width="80">
              <template #default="{ row }">{{ row.roomNumber || row.roomId }}</template>
            </el-table-column>
            <el-table-column label="入住时间" width="160">
              <template #default="{ row }">{{ formatTime(row.checkInTime) }}</template>
            </el-table-column>
            <el-table-column label="预计离店" width="160">
              <template #default="{ row }">{{ formatTime(row.expectedCheckOutTime) }}</template>
            </el-table-column>
            <el-table-column prop="status" label="状态" width="100">
              <template #default="{ row }">
                <el-tag type="success" size="small">在住</el-tag>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="hover">
          <template #header>
            <span style="font-weight: 600;">快捷操作</span>
          </template>
          <div class="quick-actions">
            <el-button type="danger" class="quick-btn" @click="$router.push('/checkins/create')">
              <el-icon><Edit /></el-icon>办理入住
            </el-button>
            <el-button type="warning" class="quick-btn" @click="$router.push('/checkouts')">
              <el-icon><Switch /></el-icon>办理退房
            </el-button>
            <el-button type="primary" class="quick-btn" @click="$router.push('/rooms')">
              <el-icon><HomeFilled /></el-icon>查看房态
            </el-button>
            <el-button type="success" class="quick-btn" @click="$router.push('/guests')">
              <el-icon><User /></el-icon>客人管理
            </el-button>
          </div>
        </el-card>
        <el-card shadow="hover" style="margin-top: 16px;">
          <template #header>
            <span style="font-weight: 600;">房态概览</span>
          </template>
          <div class="room-summary">
            <div v-for="item in roomSummary" :key="item.label" class="summary-item">
              <span class="dot" :style="{ background: item.color }"></span>
              <span class="summary-label">{{ item.label }}</span>
              <span class="summary-value">{{ item.count }}间</span>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { useAuthStore } from '../stores/auth'
import { queryCheckins } from '../api/checkin'
import { getStatusMap } from '../api/room'
import { Edit, Switch, HomeFilled, User } from '@element-plus/icons-vue'

const authStore = useAuthStore()
const inHouseList = ref([])
const tableLoading = ref(false)
const roomStats = reactive({ available: 0, occupied: 0, dirty: 0, maintenance: 0 })

const currentDate = computed(() => {
  const d = new Date()
  return `${d.getFullYear()}年${d.getMonth() + 1}月${d.getDate()}日`
})

const stats = computed(() => [
  { label: '在住房间', value: roomStats.occupied, icon: 'HomeFilled', bg: 'linear-gradient(135deg, #C04848, #a03838)' },
  { label: '可售房间', value: roomStats.available, icon: 'CircleCheck', bg: 'linear-gradient(135deg, #48762E, #356322)' },
  { label: '脏房待清洁', value: roomStats.dirty, icon: 'Warning', bg: 'linear-gradient(135deg, #e6a23c, #cf9236)' },
  { label: '今日入住', value: statsToday.value, icon: 'Edit', bg: 'linear-gradient(135deg, #409eff, #337ecc)' }
])

const statsToday = computed(() => inHouseList.value.length || 0)

const roomSummary = computed(() => [
  { label: '可售', count: roomStats.available, color: '#48762E' },
  { label: '入住中', count: roomStats.occupied, color: '#C04848' },
  { label: '脏房', count: roomStats.dirty, color: '#e6a23c' },
  { label: '维修', count: roomStats.maintenance, color: '#909399' }
])

function formatTime(time) {
  if (!time) return '-'
  return time.substring(0, 16).replace('T', ' ')
}

async function loadData() {
  tableLoading.value = true
  try {
    const res = await queryCheckins({ page: 1, pageSize: 50, status: 'in_house' })
    inHouseList.value = res.data?.records || []
  } catch (e) {
    console.error(e)
  }
  tableLoading.value = false

  try {
    const mapRes = await getStatusMap()
    const data = mapRes.data || {}
    roomStats.available = data.available?.length || 0
    roomStats.occupied = data.occupied?.length || 0
    roomStats.dirty = data.dirty?.length || 0
    roomStats.maintenance = data.maintenance?.length || 0
  } catch (e) {
    console.error(e)
  }
}

onMounted(loadData)
</script>

<style scoped>
.welcome-card {
  background: linear-gradient(135deg, #1e1e2d, #2d2d44);
  border-radius: 12px;
  padding: 28px 32px;
  margin-bottom: 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  color: #fff;
}
.welcome-text h2 {
  margin: 0 0 4px;
  font-size: 22px;
}
.welcome-text p {
  margin: 0;
  color: rgba(255,255,255,0.6);
  font-size: 14px;
}
.welcome-icon {
  font-size: 48px;
}
.stat-card {
  border-radius: 10px;
  margin-bottom: 20px;
}
.stat-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.stat-label {
  color: #909399;
  font-size: 14px;
  margin: 0 0 4px;
}
.stat-value {
  font-size: 28px;
  font-weight: 700;
  color: #303133;
  margin: 0;
}
.stat-icon {
  width: 56px;
  height: 56px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
}
.quick-actions {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 12px;
}
.quick-btn {
  width: 100%;
  height: 70px;
  display: flex;
  flex-direction: column;
  gap: 4px;
  font-size: 13px;
  border-radius: 8px;
}
.room-summary {
  display: flex;
  flex-direction: column;
  gap: 12px;
}
.summary-item {
  display: flex;
  align-items: center;
  gap: 8px;
}
.dot {
  width: 10px;
  height: 10px;
  border-radius: 50%;
}
.summary-label {
  flex: 1;
  font-size: 14px;
  color: #606266;
}
.summary-value {
  font-size: 16px;
  font-weight: 600;
}
</style>
