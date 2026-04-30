<template>
  <div class="checkin-mgmt">
    <div class="page-header">
      <h2>入住管理</h2>
      <el-button type="danger" @click="$router.push('/checkins/create')">
        <el-icon><Plus /></el-icon>办理入住
      </el-button>
    </div>

    <el-card shadow="hover" class="search-card">
      <el-form :inline="true" :model="query">
        <el-form-item label="关键词">
          <el-input v-model="query.keyword" placeholder="入住单号/房号" clearable />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="query.status" placeholder="全部" clearable style="width: 120px">
            <el-option label="在住" value="in_house" />
            <el-option label="已退房" value="checked_out" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="search">查询</el-button>
          <el-button @click="reset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card shadow="hover">
      <el-table :data="list" stripe v-loading="loading" @row-click="goDetail">
        <el-table-column prop="id" label="入住单号" width="110" />
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
        <el-table-column prop="guestCount" label="人数" width="60" />
        <el-table-column prop="depositAmount" label="押金" width="100">
          <template #default="{ row }">¥{{ row.depositAmount }}</template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="90">
          <template #default="{ row }">
            <el-tag :type="row.status === 'in_house' ? 'success' : 'info'" size="small">
              {{ row.status === 'in_house' ? '在住' : '已退房' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="120" fixed="right">
          <template #default="{ row }">
            <el-button text type="primary" size="small" @click.stop="$router.push(`/checkins/${row.id}`)">详情</el-button>
            <el-button v-if="row.status === 'in_house'" text type="warning" size="small" @click.stop="$router.push('/checkouts')">退房</el-button>
          </template>
        </el-table-column>
      </el-table>
      <div class="pagination-wrap">
        <el-pagination
          v-model:current-page="query.page"
          v-model:page-size="query.pageSize"
          :total="total"
          layout="total, prev, pager, next"
          @current-change="loadData"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { queryCheckins } from '../api/checkin'
import { Plus } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'

const router = useRouter()
const list = ref([])
const loading = ref(false)
const total = ref(0)

const query = reactive({
  page: 1,
  pageSize: 20,
  keyword: '',
  status: ''
})

function formatTime(time) {
  if (!time) return '-'
  return time.substring(0, 16).replace('T', ' ')
}

async function loadData() {
  loading.value = true
  try {
    const params = { ...query }
    if (!params.keyword) delete params.keyword
    if (!params.status) delete params.status
    const res = await queryCheckins(params)
    list.value = res.data?.records || []
    total.value = res.data?.total || 0
  } catch (e) {
    ElMessage.error('查询失败')
  }
  loading.value = false
}

function search() {
  query.page = 1
  loadData()
}

function reset() {
  query.keyword = ''
  query.status = ''
  search()
}

function goDetail(row) {
  router.push(`/checkins/${row.id}`)
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
.page-header h2 { margin: 0; font-size: 20px; }
.search-card { margin-bottom: 16px; }
.pagination-wrap {
  margin-top: 16px;
  display: flex;
  justify-content: flex-end;
}
</style>
