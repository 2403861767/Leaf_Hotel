<template>
  <div class="deposit-mgmt">
    <div class="page-header">
      <h2>押金管理</h2>
      <el-button type="danger" @click="showCreate = true">
        <el-icon><Plus /></el-icon>收取押金
      </el-button>
    </div>

    <el-card shadow="hover" class="search-card">
      <el-form :inline="true">
        <el-form-item label="入住单号">
          <el-input v-model="query.registrationId" placeholder="输入入住单号" style="width: 200px" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="search">查询</el-button>
          <el-button @click="query.registrationId = ''; search()">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card shadow="hover">
      <el-table :data="list" stripe v-loading="loading">
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="registrationId" label="入住单号" width="100" />
        <el-table-column prop="paymentMethod" label="支付方式" width="120">
          <template #default="{ row }">{{ paymentLabel(row.paymentMethod) }}</template>
        </el-table-column>
        <el-table-column prop="amount" label="金额" width="100">
          <template #default="{ row }">¥{{ row.amount }}</template>
        </el-table-column>
        <el-table-column prop="slipNumber" label="押金单号" width="160" show-overflow-tooltip />
        <el-table-column prop="transactionNo" label="交易流水号" width="160" show-overflow-tooltip />
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
        <el-table-column label="操作" width="100" fixed="right">
          <template #default="{ row }">
            <el-button v-if="row.status === 'active'" text type="warning" size="small" @click="handleRefund(row)">退押金</el-button>
            <span v-else>-</span>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="showCreate" title="收取押金" width="500px">
      <el-form :model="createForm" label-width="120px">
        <el-form-item label="入住单号" required>
          <el-input-number v-model="createForm.registrationId" :min="1" style="width: 100%" />
        </el-form-item>
        <el-form-item label="支付方式" required>
          <el-select v-model="createForm.paymentMethod" style="width: 100%">
            <el-option label="现金" value="cash" />
            <el-option label="微信" value="wechat" />
            <el-option label="支付宝" value="alipay" />
            <el-option label="银行卡" value="bank_card" />
          </el-select>
        </el-form-item>
        <el-form-item label="押金金额" required>
          <el-input-number v-model="createForm.amount" :min="0.01" :precision="2" :step="100" style="width: 100%" />
        </el-form-item>
        <el-form-item label="押金单号" v-if="createForm.paymentMethod !== 'cash'">
          <el-input v-model="createForm.slipNumber" />
        </el-form-item>
        <el-form-item label="交易流水号" v-if="createForm.paymentMethod !== 'cash'">
          <el-input v-model="createForm.transactionNo" />
        </el-form-item>
        <el-form-item label="授权码" v-if="createForm.paymentMethod === 'bank_card'">
          <el-input v-model="createForm.authCode" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showCreate = false">取消</el-button>
        <el-button type="primary" :loading="creating" @click="submitDeposit">确认收取</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { queryDeposits, createDeposit, refundDeposit } from '../api/deposit'
import { Plus } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'

const list = ref([])
const loading = ref(false)
const showCreate = ref(false)
const creating = ref(false)

const query = reactive({ registrationId: '' })
const createForm = reactive({
  registrationId: null,
  paymentMethod: 'cash',
  amount: 100,
  slipNumber: '',
  transactionNo: '',
  authCode: ''
})

function formatTime(time) {
  if (!time) return '-'
  return time.substring(0, 16).replace('T', ' ')
}
function paymentLabel(s) {
  const map = { cash: '现金', wechat: '微信', alipay: '支付宝', bank_card: '银行卡' }
  return map[s] || s
}

async function loadData() {
  loading.value = true
  try {
    const params = {}
    if (query.registrationId) params.registrationId = parseInt(query.registrationId)
    const res = await queryDeposits(params.registrationId)
    list.value = res.data || []
  } catch (e) {
    ElMessage.error('查询失败')
  }
  loading.value = false
}

function search() { loadData() }

function resetCreateForm() {
  createForm.registrationId = null
  createForm.paymentMethod = 'cash'
  createForm.amount = 100
  createForm.slipNumber = ''
  createForm.transactionNo = ''
  createForm.authCode = ''
}

async function submitDeposit() {
  if (!createForm.registrationId || !createForm.amount) {
    ElMessage.warning('请填写完整信息')
    return
  }
  creating.value = true
  try {
    const data = { ...createForm }
    if (data.paymentMethod === 'cash') {
      delete data.slipNumber
      delete data.transactionNo
      delete data.authCode
    }
    if (data.paymentMethod !== 'bank_card') delete data.authCode
    await createDeposit(data)
    ElMessage.success('押金收取成功')
    showCreate.value = false
    resetCreateForm()
    loadData()
  } catch (e) {
    ElMessage.error(e.message || '操作失败')
  }
  creating.value = false
}

async function handleRefund(row) {
  try {
    await ElMessageBox.confirm(`确认退还 ¥${row.amount} 的押金？`, '提示', {
      confirmButtonText: '确认退还', cancelButtonText: '取消', type: 'warning'
    })
  } catch { return }
  try {
    await refundDeposit(row.id)
    ElMessage.success('押金退还成功')
    loadData()
  } catch (e) {
    ElMessage.error(e.message || '退还失败')
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
.page-header h2 { margin: 0; font-size: 20px; }
.search-card { margin-bottom: 16px; }
</style>
