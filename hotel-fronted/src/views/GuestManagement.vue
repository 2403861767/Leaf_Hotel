<template>
  <div class="guest-mgmt">
    <div class="page-header">
      <h2>客人管理</h2>
      <el-button type="danger" @click="showCreate = true">
        <el-icon><Plus /></el-icon>新建客人
      </el-button>
    </div>

    <el-card shadow="hover" class="search-card">
      <el-form :inline="true" :model="query">
        <el-form-item label="关键词">
          <el-input v-model="query.keyword" placeholder="姓名/证件号/电话" clearable style="width: 300px" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="search">查询</el-button>
          <el-button @click="reset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card shadow="hover">
      <el-table :data="list" stripe v-loading="loading">
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="name" label="姓名" width="100" />
        <el-table-column prop="idType" label="证件类型" width="100">
          <template #default="{ row }">{{ idTypeLabel(row.idType) }}</template>
        </el-table-column>
        <el-table-column prop="idNumber" label="证件号" width="180" />
        <el-table-column prop="phone" label="联系电话" width="140" />
        <el-table-column prop="address" label="地址" min-width="200" show-overflow-tooltip />
        <el-table-column label="创建时间" width="160">
          <template #default="{ row }">{{ formatTime(row.createdAt) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="120" fixed="right">
          <template #default="{ row }">
            <el-button text type="primary" size="small" @click="editGuest(row)">编辑</el-button>
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

    <el-dialog v-model="showCreate" :title="editingId ? '编辑客人' : '新建客人'" width="500px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="姓名" required>
          <el-input v-model="form.name" />
        </el-form-item>
        <el-form-item label="证件类型" required>
          <el-select v-model="form.idType" style="width: 100%">
            <el-option label="身份证" value="identity" />
            <el-option label="护照" value="passport" />
            <el-option label="其他" value="other" />
          </el-select>
        </el-form-item>
        <el-form-item label="证件号" required>
          <el-input v-model="form.idNumber" />
        </el-form-item>
        <el-form-item label="电话" required>
          <el-input v-model="form.phone" />
        </el-form-item>
        <el-form-item label="地址">
          <el-input v-model="form.address" type="textarea" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showCreate = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="saveGuest">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { queryGuests, createGuest, updateGuest } from '../api/guest'
import { Plus } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'

const list = ref([])
const loading = ref(false)
const total = ref(0)
const showCreate = ref(false)
const saving = ref(false)
const editingId = ref(null)

const query = reactive({ page: 1, pageSize: 20, keyword: '' })
const form = reactive({ name: '', idType: 'identity', idNumber: '', phone: '', address: '' })

function formatTime(time) {
  if (!time) return '-'
  return time.substring(0, 16).replace('T', ' ')
}

function idTypeLabel(t) {
  const map = { identity: '身份证', passport: '护照', other: '其他' }
  return map[t] || t
}

async function loadData() {
  loading.value = true
  try {
    const params = { ...query }
    if (!params.keyword) delete params.keyword
    const res = await queryGuests(params)
    list.value = res.data?.records || []
    total.value = res.data?.total || 0
  } catch (e) {
    ElMessage.error('查询失败')
  }
  loading.value = false
}

function search() { query.page = 1; loadData() }
function reset() { query.keyword = ''; search() }

function editGuest(row) {
  editingId.value = row.id
  form.name = row.name
  form.idType = row.idType
  form.idNumber = row.idNumber
  form.phone = row.phone
  form.address = row.address || ''
  showCreate.value = true
}

function resetForm() {
  editingId.value = null
  form.name = ''
  form.idType = 'identity'
  form.idNumber = ''
  form.phone = ''
  form.address = ''
}

async function saveGuest() {
  if (!form.name || !form.idNumber || !form.phone) {
    ElMessage.warning('请填写完整信息')
    return
  }
  saving.value = true
  try {
    if (editingId.value) {
      await updateGuest(editingId.value, { ...form })
      ElMessage.success('更新成功')
    } else {
      await createGuest({ ...form })
      ElMessage.success('创建成功')
    }
    showCreate.value = false
    resetForm()
    loadData()
  } catch (e) {
    ElMessage.error(e.message || '操作失败')
  }
  saving.value = false
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
.pagination-wrap { margin-top: 16px; display: flex; justify-content: flex-end; }
</style>
