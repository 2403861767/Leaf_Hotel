<template>
  <div class="checkout-mgmt">
    <div class="page-header">
      <h2>退房管理</h2>
    </div>

    <el-row :gutter="20">
      <el-col :span="10">
        <el-card shadow="hover">
          <template #header><span style="font-weight: 600;">在住客人查询</span></template>
          <el-input v-model="searchKeyword" placeholder="输入房号或客人姓名" clearable @keyup.enter="searchInHouse">
            <template #append>
              <el-button @click="searchInHouse">查询</el-button>
            </template>
          </el-input>
          <el-table :data="inHouseList" stripe height="400" v-loading="searchLoading" style="margin-top: 12px;" @row-click="selectRegistration">
            <el-table-column prop="id" label="单号" width="70" />
            <el-table-column prop="roomId" label="房号" width="70">
              <template #default="{ row }">{{ row.roomNumber || row.roomId }}</template>
            </el-table-column>
            <el-table-column label="客人" width="80">
              <template #default="{ row }">{{ row.guestName || '-' }}</template>
            </el-table-column>
            <el-table-column label="入住" width="100">
              <template #default="{ row }">{{ formatDate(row.checkInTime) }}</template>
            </el-table-column>
            <el-table-column label="押金" width="80">
              <template #default="{ row }">¥{{ row.depositAmount }}</template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>

      <el-col :span="14">
        <el-card shadow="hover" v-if="selected">
          <template #header><span style="font-weight: 600;">退房结算 - 房号 {{ selected.roomNumber || selected.roomId }}</span></template>

          <el-descriptions :column="2" border>
            <el-descriptions-item label="客人姓名">{{ selected.guestName || '-' }}</el-descriptions-item>
            <el-descriptions-item label="入住时间">{{ formatTime(selected.checkInTime) }}</el-descriptions-item>
            <el-descriptions-item label="预计离店">{{ formatTime(selected.expectedCheckOutTime) }}</el-descriptions-item>
            <el-descriptions-item label="押金总额">¥{{ selected.depositAmount }}</el-descriptions-item>
          </el-descriptions>

          <div style="margin-top: 16px;">
            <h4>杂费录入</h4>
            <div v-for="(item, idx) in additionalCharges" :key="idx" class="charge-row">
              <el-select v-model="item.item" placeholder="费用项目" style="width: 150px">
                <el-option label="迷你吧" value="mini_bar" />
                <el-option label="洗衣" value="laundry" />
                <el-option label="长途电话" value="phone" />
                <el-option label="客房送餐" value="room_service" />
                <el-option label="其他" value="other" />
              </el-select>
              <el-input-number v-model="item.amount" :min="0" :precision="2" :step="10" style="width: 150px;" />
              <el-button type="danger" :icon="Delete" circle size="small" @click="additionalCharges.splice(idx, 1)" v-if="additionalCharges.length > 1" />
            </div>
            <el-button type="primary" link @click="additionalCharges.push({ item: '', amount: 0 })" style="margin-top: 8px;">
              + 添加杂费
            </el-button>
          </div>

          <div v-if="preview" class="preview-section" style="margin-top: 16px; padding: 16px; background: #f5f7fa; border-radius: 8px;">
            <h4 style="margin: 0 0 12px;">费用预览</h4>
            <el-descriptions :column="2" border size="small">
              <el-descriptions-item label="房费">¥{{ preview.roomCharge }}</el-descriptions-item>
              <el-descriptions-item label="杂费">¥{{ preview.additionalCharges }}</el-descriptions-item>
              <el-descriptions-item label="费用合计">
                <span style="color: #C04848; font-weight: 700;">¥{{ preview.totalCharge }}</span>
              </el-descriptions-item>
              <el-descriptions-item label="押金总计">¥{{ preview.depositAmount }}</el-descriptions-item>
              <el-descriptions-item label="应退/补金额" :span="2">
                <span :style="{ color: preview.refundAmount >= 0 ? '#48762E' : '#C04848', fontWeight: 700, fontSize: '18px' }">
                  {{ preview.refundAmount >= 0 ? `退还 ¥${preview.refundAmount}` : `补收 ¥${Math.abs(preview.refundAmount)}` }}
                </span>
              </el-descriptions-item>
            </el-descriptions>
          </div>

          <div style="margin-top: 16px; display: flex; gap: 8px;">
            <el-button type="primary" @click="calculatePreview" :loading="previewLoading" :disabled="!selected">
              费用试算
            </el-button>
            <el-button type="danger" @click="confirmCheckout" :loading="checkoutLoading" :disabled="!preview">
              确认退房结账
            </el-button>
          </div>
        </el-card>

        <el-card v-else shadow="hover">
          <el-empty description="请先在左侧查询并选择在住客人" />
        </el-card>
      </el-col>
    </el-row>

    <el-card shadow="hover" style="margin-top: 20px;">
      <template #header><span style="font-weight: 600;">退房历史</span></template>
      <el-table :data="historyList" stripe v-loading="historyLoading" height="250">
        <el-table-column prop="id" label="单号" width="80" />
        <el-table-column prop="roomId" label="房号" width="70">
          <template #default="{ row }">{{ row.roomNumber || row.roomId }}</template>
        </el-table-column>
        <el-table-column label="客人" width="80">
          <template #default="{ row }">{{ row.guestName || '-' }}</template>
        </el-table-column>
        <el-table-column label="入住时间" width="140">
          <template #default="{ row }">{{ formatTime(row.checkInTime) }}</template>
        </el-table-column>
        <el-table-column label="离店时间" width="140">
          <template #default="{ row }">{{ formatTime(row.actualCheckOutTime) || '-' }}</template>
        </el-table-column>
        <el-table-column prop="depositAmount" label="押金" width="80">¥{{ row.depositAmount }}</el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { queryCheckins } from '../api/checkin'
import { calculateRefund, createCheckout } from '../api/checkout'
import { Delete } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'

const searchKeyword = ref('')
const searchLoading = ref(false)
const inHouseList = ref([])
const selected = ref(null)
const additionalCharges = reactive([{ item: '', amount: 0 }])
const preview = ref(null)
const previewLoading = ref(false)
const checkoutLoading = ref(false)
const historyList = ref([])
const historyLoading = ref(false)

function formatTime(time) {
  if (!time) return '-'
  return time.substring(0, 16).replace('T', ' ')
}
function formatDate(time) {
  if (!time) return '-'
  return time.substring(0, 10)
}

async function searchInHouse() {
  searchLoading.value = true
  try {
    const params = { status: 'in_house', page: 1, pageSize: 50 }
    if (searchKeyword.value) params.keyword = searchKeyword.value
    const res = await queryCheckins(params)
    inHouseList.value = res.data?.records || []
  } catch (e) {
    ElMessage.error('查询失败')
  }
  searchLoading.value = false
}

function selectRegistration(row) {
  selected.value = row
  preview.value = null
  additionalCharges.length = 0
  additionalCharges.push({ item: '', amount: 0 })
}

async function calculatePreview() {
  if (!selected.value) return
  previewLoading.value = true
  try {
    const charges = additionalCharges.filter(c => c.item && c.amount > 0)
    const res = await calculateRefund(selected.value.id)
    preview.value = res.data
  } catch (e) {
    ElMessage.error('试算失败')
  }
  previewLoading.value = false
}

async function confirmCheckout() {
  if (!selected.value || !preview.value) return
  try {
    await ElMessageBox.confirm('确认办理退房结账？', '提示', {
      confirmButtonText: '确认退房',
      cancelButtonText: '取消',
      type: 'warning'
    })
  } catch {
    return
  }

  checkoutLoading.value = true
  try {
    const charges = additionalCharges.filter(c => c.item && c.amount > 0)
    const res = await createCheckout({
      registrationId: selected.value.id,
      additionalCharges: charges.length > 0 ? charges : undefined
    })
    ElMessage.success(`退房成功！退还金额：¥${res.data.refundAmount}`)
    selected.value = null
    preview.value = null
    searchInHouse()
    loadHistory()
  } catch (e) {
    ElMessage.error(e.message || '退房失败')
  }
  checkoutLoading.value = false
}

async function loadHistory() {
  historyLoading.value = true
  try {
    const res = await queryCheckins({ status: 'checked_out', page: 1, pageSize: 20 })
    historyList.value = res.data?.records || []
  } catch (e) {}
  historyLoading.value = false
}

searchInHouse()
loadHistory()
</script>

<style scoped>
.page-header { margin-bottom: 16px; }
.page-header h2 { margin: 0; font-size: 20px; }
.charge-row {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 8px;
}
</style>
