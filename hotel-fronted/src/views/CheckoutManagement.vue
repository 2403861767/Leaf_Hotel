<template>
  <div class="checkout-mgmt">
    <div class="page-header">
      <h2>退房管理</h2>
    </div>

    <el-row :gutter="20">
      <el-col :span="10">
        <el-card shadow="hover" class="left-card">
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
        <el-card shadow="hover" v-if="selected" class="right-card">
          <template #header><span style="font-weight: 600;">退房结算 - 房号 {{ selected.roomNumber || selected.roomId }}</span></template>
          <div class="card-body-inner">

          <el-descriptions :column="2" border>
            <el-descriptions-item label="客人姓名">{{ selected.guestName || '-' }}</el-descriptions-item>
            <el-descriptions-item label="入住时间">{{ formatTime(selected.checkInTime) }}</el-descriptions-item>
            <el-descriptions-item label="预计离店">{{ formatTime(selected.expectedCheckOutTime) }}</el-descriptions-item>
            <el-descriptions-item label="押金总额">¥{{ selected.depositAmount }}</el-descriptions-item>
          </el-descriptions>

          <div class="charges-section">
            <h4>杂费录入</h4>
            <div class="charge-header-row">
              <span class="charge-col-item">费用项目</span>
              <span class="charge-col-amount">金额（元）</span>
              <span class="charge-col-action"></span>
            </div>
            <div v-for="(item, idx) in additionalCharges" :key="idx" class="charge-row">
              <el-select :model-value="item.item" @update:model-value="v => item.item = v" placeholder="选择项目" class="charge-select">
                <el-option label="迷你吧" value="mini_bar" />
                <el-option label="洗衣" value="laundry" />
                <el-option label="长途电话" value="phone" />
                <el-option label="客房送餐" value="room_service" />
                <el-option label="其他" value="other" />
              </el-select>
              <el-input-number :model-value="item.amount" @update:model-value="v => item.amount = v" :min="0" :precision="2" :step="10" class="charge-amount" />
              <span class="charge-col-action">
                <el-button type="danger" :icon="Delete" circle size="small" @click="additionalCharges.splice(idx, 1)" :disabled="additionalCharges.length <= 1" />
              </span>
            </div>
            <el-button type="primary" link class="add-charge-btn" @click="additionalCharges.push({ item: '', amount: 0 })">
              + 添加杂费
            </el-button>
          </div>

          <div v-if="preview" class="preview-section">
            <h4>费用预览</h4>
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

          <div class="checkout-actions">
            <el-button type="primary" class="action-btn" @click="calculatePreview" :loading="previewLoading" :disabled="!selected">
              费用试算
            </el-button>
            <el-button type="danger" class="action-btn" @click="confirmCheckout" :loading="checkoutLoading" :disabled="!preview">
              确认退房结账
            </el-button>
          </div>
          </div>
        </el-card>

        <el-card v-else shadow="hover" class="right-card">
          <template #header><span style="font-weight: 600;">退房结算</span></template>
          <div class="empty-placeholder">
            <el-empty description="请先在左侧查询并选择在住客人" />
          </div>
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
        <el-table-column label="押金" width="80">
          <template #default="{ row }">¥{{ row.depositAmount }}</template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
/**
 * 退房管理 —— 分左右两栏：左侧在住客人查询 + 右侧退房结算表单。
 *
 * 退房结算链（对应后端 CheckoutServiceImpl 的 9 步事务）：
 *   1. 左栏查询在住客人 → 点击选中一行
 *   2. 右栏录入杂费（迷你吧/洗衣/电话等）→ "费用试算" 调 calculateRefund 预览
 *   3. "确认退房结账" → ElMessageBox 二次确认 → createCheckout 提交
 *      后端自动：计算过夜天数 × 房价 → 叠加杂费 → 押金抵扣 → 退差额 → 房态变 dirty
 *
 * calculateRefund 是只读预计算接口，不写入数据库。
 */
import { ref, onMounted } from 'vue'
import { queryCheckins } from '../api/checkin'
import { calculateRefund, createCheckout } from '../api/checkout'
import { Delete } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'

const searchKeyword = ref('')
const searchLoading = ref(false)
const inHouseList = ref([])
const selected = ref(null)
const additionalCharges = ref([{ item: '', amount: 0 }])
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
    inHouseList.value = res.data?.list || []
  } catch (e) {
    ElMessage.error('查询失败')
  }
  searchLoading.value = false
}

/** 切换选择的入住记录时，清空预览和杂费列表 */
function selectRegistration(row) {
  selected.value = row
  preview.value = null
  additionalCharges.value.length = 0
  additionalCharges.value.push({ item: '', amount: 0 })
}

async function calculatePreview() {
  if (!selected.value) return
  previewLoading.value = true
  try {
    const charges = additionalCharges.value.filter(c => c.item && c.amount > 0)
    const res = await calculateRefund(selected.value.id)
    preview.value = res.data
  } catch (e) {
    ElMessage.error('试算失败')
  }
  previewLoading.value = false
}

async function confirmCheckout() {
  if (!selected.value || !preview.value) return
  // 二次确认防止误操作
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
    const charges = additionalCharges.value.filter(c => c.item && c.amount > 0)
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
    historyList.value = res.data?.list || []
  } catch (e) {}
  historyLoading.value = false
}

onMounted(() => {
  searchInHouse()
  loadHistory()
})
</script>

<style scoped>
.page-header { margin-bottom: 16px; }
.page-header h2 { margin: 0; font-size: 20px; }

/* 左右卡片等高 */
.left-card,
.right-card {
  height: 100%;
}

/* 退房结算卡片：flex 布局让按钮沉底 */
.right-card :deep(.el-card__body) {
  display: flex;
  flex-direction: column;
  flex: 1;
}
.card-body-inner {
  flex: 1;
  display: flex;
  flex-direction: column;
}

/* 杂费录入 */
.charges-section { margin-top: 20px; }
.charges-section h4 { margin: 0 0 12px; }

.charge-header-row {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 6px;
  padding-bottom: 6px;
  border-bottom: 1px solid #ebeef5;
  font-size: 12px;
  color: #909399;
}
.charge-col-item { width: 140px; flex-shrink: 0; }
.charge-col-amount { width: 160px; flex-shrink: 0; }
.charge-col-action { width: 32px; flex-shrink: 0; text-align: center; }

.charge-row {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 10px;
}
.charge-select { width: 140px; flex-shrink: 0; }
.charge-amount { width: 160px; flex-shrink: 0; }

.add-charge-btn { margin-top: 4px; padding-left: 0; }

/* 底部操作栏：沉底 */
.checkout-actions {
  margin-top: auto;
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  padding-top: 16px;
  border-top: 1px solid #ebeef5;
}
.action-btn {
  min-width: 130px;
}

/* 费用预览 */
.preview-section {
  margin-top: 20px;
  padding: 16px;
  background: #f5f7fa;
  border-radius: 8px;
}
.preview-section h4 { margin: 0 0 12px; }

/* 空状态居中 */
.empty-placeholder {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
}
</style>
