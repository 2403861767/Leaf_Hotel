<template>
  <div class="checkin-create">
    <div class="page-header">
      <h2>办理入住</h2>
    </div>

    <el-row :gutter="20">
      <el-col :span="14">
        <el-card shadow="hover">
          <template #header><span style="font-weight: 600;">客人信息</span></template>
          <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
            <el-form-item label="客人">
              <el-row :gutter="8" style="width: 100%;">
                <el-col :span="18">
                  <el-select v-model="form.guestId" filterable remote clearable
                    :remote-method="searchGuest" placeholder="搜索客人（姓名/电话）" style="width: 100%"
                    @change="onGuestSelected">
                    <el-option v-for="g in guestOptions" :key="g.id" :label="`${g.name} (${g.phone})`" :value="g.id" />
                  </el-select>
                </el-col>
                <el-col :span="6">
                  <el-button type="primary" @click="showCreateGuest = true">新建客人</el-button>
                </el-col>
              </el-row>
            </el-form-item>
            <el-form-item label="姓名" prop="guestName">
              <el-input v-model="form.guestName" disabled />
            </el-form-item>
            <el-form-item label="证件号" prop="idNumber">
              <el-input v-model="form.idNumber" disabled />
            </el-form-item>
            <el-form-item label="联系电话" prop="phone">
              <el-input v-model="form.phone" disabled />
            </el-form-item>
            <el-divider />
            <el-form-item label="入住人数" prop="guestCount">
              <el-input-number v-model="form.guestCount" :min="1" :max="10" />
            </el-form-item>
            <el-form-item label="预计离店" prop="expectedCheckOutDate">
              <el-date-picker v-model="form.expectedCheckOutDate" type="date" placeholder="选择日期" style="width: 100%" value-format="YYYY-MM-DD" />
            </el-form-item>
            <el-form-item label="入住来源" prop="source">
              <el-select v-model="form.source" style="width: 100%">
                <el-option label="散客-到店" value="walk_in" />
                <el-option label="电话预订" value="phone" />
                <el-option label="前台预订" value="front_desk" />
                <el-option label="经理预订" value="manager" />
                <el-option label="在线直连" value="online_direct" />
                <el-option label="OTA平台" value="online_ota" />
                <el-option label="合同单位" value="contract" />
              </el-select>
            </el-form-item>
            <el-form-item label="预订编号">
              <el-input v-model="form.bookingNumber" placeholder="如有预订请输入" />
            </el-form-item>
            <el-form-item label="预订单号">
              <el-input v-model="form.reservationId" placeholder="关联预订ID（可选）" />
            </el-form-item>
          </el-form>
        </el-card>
      </el-col>

      <el-col :span="10">
        <el-card shadow="hover">
          <template #header><span style="font-weight: 600;">选择房间</span></template>
          <el-form-item label="房型">
            <el-select v-model="form.roomTypeId" placeholder="选择房型" style="width: 100%" @change="loadAvailableRooms">
              <el-option v-for="t in roomTypes" :key="t.id" :label="t.typeName" :value="t.id" />
            </el-select>
          </el-form-item>
          <div class="room-select-grid" v-loading="roomLoading">
            <div
              v-for="room in availableRooms"
              :key="room.id"
              class="room-option"
              :class="{ selected: form.roomId === room.id }"
              @click="form.roomId = room.id"
            >
              <div class="room-no">{{ room.roomNumber }}</div>
              <div class="room-floor">{{ room.floor }}F</div>
            </div>
            <el-empty v-if="!roomLoading && availableRooms.length === 0" description="请选择房型" />
          </div>
        </el-card>

        <el-card shadow="hover" style="margin-top: 16px;">
          <template #header><span style="font-weight: 600;">押金</span></template>
          <el-form-item label="押金金额">
            <el-input-number v-model="form.depositAmount" :min="0" :precision="2" :step="100" style="width: 100%" />
          </el-form-item>
        </el-card>

        <div style="margin-top: 16px; display: flex; gap: 8px;">
          <el-button type="danger" size="large" style="flex: 1;" @click="submitCheckin" :loading="submitting">
            确认入住
          </el-button>
          <el-button size="large" @click="$router.back()">取消</el-button>
        </div>
      </el-col>
    </el-row>

    <el-dialog v-model="showCreateGuest" title="新建客人" width="500px">
      <el-form :model="newGuest" label-width="80px">
        <el-form-item label="姓名" required>
          <el-input v-model="newGuest.name" />
        </el-form-item>
        <el-form-item label="证件类型" required>
          <el-select v-model="newGuest.idType" style="width: 100%">
            <el-option label="身份证" value="identity" />
            <el-option label="护照" value="passport" />
            <el-option label="其他" value="other" />
          </el-select>
        </el-form-item>
        <el-form-item label="证件号" required>
          <el-input v-model="newGuest.idNumber" />
        </el-form-item>
        <el-form-item label="电话" required>
          <el-input v-model="newGuest.phone" />
        </el-form-item>
        <el-form-item label="地址">
          <el-input v-model="newGuest.address" type="textarea" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showCreateGuest = false">取消</el-button>
        <el-button type="primary" :loading="creatingGuest" @click="submitNewGuest">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { queryGuests, createGuest } from '../api/guest'
import { getAvailableRooms } from '../api/room'
import { getRoomDetail } from '../api/room'
import { createCheckin } from '../api/checkin'
import { ElMessage } from 'element-plus'

const router = useRouter()
const formRef = ref(null)
const submitting = ref(false)
const showCreateGuest = ref(false)
const creatingGuest = ref(false)
const guestOptions = ref([])
const availableRooms = ref([])
const roomLoading = ref(false)
const roomTypes = ref([])

const form = reactive({
  guestId: null,
  guestName: '',
  idNumber: '',
  phone: '',
  roomId: null,
  roomTypeId: null,
  guestCount: 1,
  expectedCheckOutDate: '',
  source: 'walk_in',
  depositAmount: 0,
  bookingNumber: '',
  reservationId: null
})

const rules = {
  guestCount: [{ required: true, message: '请输入入住人数' }],
  expectedCheckOutDate: [{ required: true, message: '请选择预计离店日期' }],
  source: [{ required: true, message: '请选择入住来源' }]
}

const newGuest = reactive({
  name: '',
  idType: 'identity',
  idNumber: '',
  phone: '',
  address: ''
})

async function searchGuest(keyword) {
  if (!keyword) return
  try {
    const res = await queryGuests({ keyword, page: 1, pageSize: 20 })
    guestOptions.value = res.data?.records || []
  } catch (e) {
    guestOptions.value = []
  }
}

function onGuestSelected(id) {
  const g = guestOptions.value.find(x => x.id === id)
  if (g) {
    form.guestName = g.name
    form.idNumber = g.idNumber
    form.phone = g.phone
  }
}

async function loadAvailableRooms(typeId) {
  if (!typeId) return
  roomLoading.value = true
  try {
    const res = await getAvailableRooms(typeId)
    availableRooms.value = res.data || []
  } catch (e) {
    availableRooms.value = []
  }
  roomLoading.value = false
}

async function submitNewGuest() {
  if (!newGuest.name || !newGuest.idNumber || !newGuest.phone) {
    ElMessage.warning('请填写完整信息')
    return
  }
  creatingGuest.value = true
  try {
    const res = await createGuest({ ...newGuest })
    guestOptions.value = [res.data]
    form.guestId = res.data.id
    onGuestSelected(res.data.id)
    showCreateGuest.value = false
    ElMessage.success('客人创建成功')
  } catch (e) {
    ElMessage.error(e.message || '创建失败')
  }
  creatingGuest.value = false
}

async function submitCheckin() {
  if (!form.guestId) { ElMessage.warning('请选择客人'); return }
  if (!form.roomId) { ElMessage.warning('请选择房间'); return }
  if (!form.expectedCheckOutDate) { ElMessage.warning('请选择预计离店日期'); return }

  submitting.value = true
  try {
    const data = {
      guestId: form.guestId,
      roomId: form.roomId,
      roomTypeId: form.roomTypeId,
      expectedCheckOutDate: form.expectedCheckOutDate,
      guestCount: form.guestCount,
      depositAmount: form.depositAmount || 0,
      source: form.source,
      bookingNumber: form.bookingNumber || undefined
    }
    if (form.reservationId) data.reservationId = parseInt(form.reservationId)
    const res = await createCheckin(data)
    ElMessage.success(`入住办理成功！房间号：${res.data.roomNumber}`)
    router.push('/checkins')
  } catch (e) {
    ElMessage.error(e.message || '入住办理失败')
  }
  submitting.value = false
}

onMounted(async () => {
  try {
    const res = await getAvailableRooms()
    const rooms = res.data || []
    const typeMap = {}
    rooms.forEach(r => {
      if (r.roomTypeId && !typeMap[r.roomTypeId]) {
        typeMap[r.roomTypeId] = { id: r.roomTypeId, typeName: r.typeName || `房型${r.roomTypeId}` }
      }
    })
    roomTypes.value = Object.values(typeMap)
  } catch (e) {}
})
</script>

<style scoped>
.page-header { margin-bottom: 16px; }
.page-header h2 { margin: 0; font-size: 20px; }
.room-select-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 8px;
  min-height: 100px;
}
.room-option {
  padding: 12px 8px;
  border: 2px solid #dcdfe6;
  border-radius: 8px;
  text-align: center;
  cursor: pointer;
  transition: all 0.2s;
}
.room-option:hover { border-color: #409eff; }
.room-option.selected {
  border-color: #C04848;
  background: #fef0f0;
}
.room-no { font-size: 18px; font-weight: 700; }
.room-floor { font-size: 12px; color: #909399; }
</style>
