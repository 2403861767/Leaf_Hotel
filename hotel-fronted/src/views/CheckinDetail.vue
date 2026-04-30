<template>
  <div class="checkin-detail">
    <div class="page-header">
      <h2>入住详情 #{{ id }}</h2>
      <div>
        <el-button @click="$router.push('/checkins')">返回列表</el-button>
        <el-button v-if="detail.status === 'in_house'" type="warning" @click="$router.push('/checkouts')">办理退房</el-button>
      </div>
    </div>

    <el-card shadow="hover" v-loading="loading">
      <el-descriptions :column="3" border>
        <el-descriptions-item label="入住单号">{{ detail.id }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="detail.status === 'in_house' ? 'success' : 'info'">
            {{ detail.status === 'in_house' ? '在住' : '已退房' }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="入住来源">{{ sourceLabel(detail.source) }}</el-descriptions-item>
        <el-descriptions-item label="客人姓名">{{ detail.guestName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="房号">{{ detail.roomNumber || detail.roomId }}</el-descriptions-item>
        <el-descriptions-item label="入住人数">{{ detail.guestCount }}人</el-descriptions-item>
        <el-descriptions-item label="入住时间">{{ formatTime(detail.checkInTime) }}</el-descriptions-item>
        <el-descriptions-item label="预计离店">{{ formatTime(detail.expectedCheckOutTime) }}</el-descriptions-item>
        <el-descriptions-item label="实际离店">{{ formatTime(detail.actualCheckOutTime) || '-' }}</el-descriptions-item>
        <el-descriptions-item label="押金金额">¥{{ detail.depositAmount }}</el-descriptions-item>
        <el-descriptions-item label="办理人">{{ detail.operatorName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="预订编号">{{ detail.bookingNumber || '-' }}</el-descriptions-item>
      </el-descriptions>
    </el-card>

    <el-card shadow="hover" style="margin-top: 16px;">
      <template #header><span style="font-weight: 600;">押金记录</span></template>
      <el-table :data="deposits" stripe v-loading="depositLoading">
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="paymentMethod" label="支付方式" width="120">
          <template #default="{ row }">{{ paymentLabel(row.paymentMethod) }}</template>
        </el-table-column>
        <el-table-column prop="amount" label="金额" width="100">
          <template #default="{ row }">¥{{ row.amount }}</template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="90">
          <template #default="{ row }">
            <el-tag :type="row.status === 'active' ? 'success' : 'info'" size="small">
              {{ row.status === 'active' ? '有效' : '已退' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="创建时间" width="160">
          <template #default="{ row }">{{ formatTime(row.createdAt) }}</template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { getCheckinDetail } from '../api/checkin'
import { queryDeposits } from '../api/deposit'
import { ElMessage } from 'element-plus'

const route = useRoute()
const id = route.params.id
const loading = ref(false)
const depositLoading = ref(false)
const detail = ref({})
const deposits = ref([])

function formatTime(time) {
  if (!time) return '-'
  return time.substring(0, 16).replace('T', ' ')
}

function sourceLabel(s) {
  const map = { walk_in: '散客到店', phone: '电话预订', front_desk: '前台预订', manager: '经理预订', online_direct: '在线直连', online_ota: 'OTA平台', contract: '合同单位' }
  return map[s] || s
}

function paymentLabel(s) {
  const map = { cash: '现金', wechat: '微信', alipay: '支付宝', bank_card: '银行卡' }
  return map[s] || s
}

onMounted(async () => {
  loading.value = true
  try {
    const res = await getCheckinDetail(id)
    detail.value = res.data || {}
  } catch (e) {
    ElMessage.error('获取入住详情失败')
  }
  loading.value = false

  depositLoading.value = true
  try {
    const dRes = await queryDeposits(id)
    deposits.value = dRes.data || []
  } catch (e) {}
  depositLoading.value = false
})
</script>

<style scoped>
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}
.page-header h2 { margin: 0; font-size: 20px; }
</style>
