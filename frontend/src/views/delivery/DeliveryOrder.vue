<template>
  <div class="delivery-order-page">
    <el-card>
      <div class="toolbar">
        <el-form inline>
          <el-form-item>
            <el-input v-model="keyword" placeholder="送货单号" clearable style="width: 160px" @keyup.enter="load" />
          </el-form-item>
          <el-form-item>
            <el-select v-model="customerId" placeholder="客户" clearable style="width: 160px">
              <el-option v-for="c in customers" :key="c.id" :label="c.name" :value="c.id" />
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-select v-model="status" placeholder="状态" clearable style="width: 120px">
              <el-option v-for="s in statusOptions" :key="s.value" :label="s.label" :value="s.value" />
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-date-picker v-model="startDate" type="date" format="YYYY-MM-DD" value-format="YYYY-MM-DD" placeholder="起送日期" style="width: 160px" />
          </el-form-item>
          <el-form-item>
            <el-date-picker v-model="endDate" type="date" format="YYYY-MM-DD" value-format="YYYY-MM-DD" placeholder="截止日期" style="width: 160px" />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="load">搜索</el-button>
          </el-form-item>
        </el-form>
        <el-button type="primary" @click="openCreate">新增送货单</el-button>
      </div>

      <el-table :data="list" v-loading="loading" border>
        <el-table-column prop="orderNo" label="送货单号" />
        <el-table-column prop="deliveryDate" label="送货日期" width="110" />
        <el-table-column prop="orderDate" label="订单日期" width="110" />
        <el-table-column prop="customerId" label="客户ID" width="80" />
        <el-table-column prop="pointId" label="配送点ID" width="90" />
        <el-table-column prop="totalQuantity" label="总数量" width="90" />
        <el-table-column prop="totalAmount" label="总金额" width="100" />
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="statusTag(row.status).type">{{ statusTag(row.status).text }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="printCount" label="打印次数" width="90" />
        <el-table-column prop="createdAt" label="创建时间" />
        <el-table-column label="操作" width="340">
          <template #default="{ row }">
            <el-button link type="primary" @click="openItems(row)">明细</el-button>
            <template v-if="row.status === 0">
              <el-button link type="success" @click="deliver(row)">送达</el-button>
            </template>
            <template v-elif="row.status === 1">
              <el-button link type="warning" @click="accept(row)">验收</el-button>
            </template>
            <el-button link type="info" @click="printRow(row)">打印</el-button>
            <template v-if="row.status < 2">
              <el-button link type="danger" @click="remove(row)">删除</el-button>
            </template>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination">
        <el-pagination
          v-model:current-page="pageNum"
          v-model:page-size="pageSize"
          :total="total"
          :page-sizes="[10, 20, 50]"
          layout="total, sizes, prev, pager, next"
          @size-change="load"
          @current-change="load"
        />
      </div>
    </el-card>

    <el-dialog v-model="dialogVisible" title="新增送货单" width="680px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="送货日期" prop="deliveryDate">
              <el-date-picker v-model="form.deliveryDate" type="date" format="YYYY-MM-DD" value-format="YYYY-MM-DD" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="订单日期" prop="orderDate">
              <el-date-picker v-model="form.orderDate" type="date" format="YYYY-MM-DD" value-format="YYYY-MM-DD" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="客户ID" prop="customerId">
              <el-input-number v-model="form.customerId" :min="1" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="配送点ID" prop="pointId">
              <el-input-number v-model="form.pointId" :min="1" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="备注" prop="remark">
              <el-input v-model="form.remark" type="textarea" />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>

      <div class="item-section">
        <div class="item-header">
          <span>送货明细</span>
          <el-button type="primary" size="small" @click="addItemRow">添加明细</el-button>
        </div>
        <el-table :data="itemRows" border size="small">
          <el-table-column label="原订单ID" width="90">
            <template #default="{ row }">
              <el-input-number v-model="row.originOrderId" :min="1" size="small" style="width: 100%" />
            </template>
          </el-table-column>
          <el-table-column label="原行ID" width="80">
            <template #default="{ row }">
              <el-input-number v-model="row.originItemId" :min="1" size="small" style="width: 100%" />
            </template>
          </el-table-column>
          <el-table-column label="品名" width="130">
            <template #default="{ row }">
              <el-input v-model="row.itemName" size="small" />
            </template>
          </el-table-column>
          <el-table-column label="规格" width="80">
            <template #default="{ row }">
              <el-input v-model="row.itemSpec" size="small" />
            </template>
          </el-table-column>
          <el-table-column label="单位" width="70">
            <template #default="{ row }">
              <el-input v-model="row.itemUnit" size="small" />
            </template>
          </el-table-column>
          <el-table-column label="数量" width="90">
            <template #default="{ row }">
              <el-input-number v-model="row.quantity" :min="0" :precision="2" size="small" style="width: 100%" />
            </template>
          </el-table-column>
          <el-table-column label="单价" width="100">
            <template #default="{ row }">
              <el-input-number v-model="row.unitPrice" :min="0" :precision="2" size="small" style="width: 100%" />
            </template>
          </el-table-column>
          <el-table-column label="小计" width="90">
            <template #default="{ row }">
              {{ ((row.quantity || 0) * (row.unitPrice || 0)).toFixed(2) }}
            </template>
          </el-table-column>
          <el-table-column label="操作" width="60">
            <template #default="{ $index }">
              <el-button link type="danger" @click="removeItemRow($index)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>

      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="submit">确定</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="itemsDialogVisible" :title="`送货明细 - ${currentDeliveryOrder?.orderNo || ''}`" width="720px">
      <el-table :data="deliveryItems" border>
        <el-table-column prop="originOrderId" label="原订单ID" width="80" />
        <el-table-column prop="originItemId" label="原行ID" width="70" />
        <el-table-column prop="itemName" label="品名" />
        <el-table-column prop="itemSpec" label="规格" />
        <el-table-column prop="itemUnit" label="单位" />
        <el-table-column prop="quantity" label="数量" />
        <el-table-column prop="unitPrice" label="单价" />
        <el-table-column prop="subtotal" label="小计" />
        <el-table-column prop="adjustStatus" label="调整状态" width="90">
          <template #default="{ row }">
            {{ adjustStatusLabel(row.adjustStatus) }}
          </template>
        </el-table-column>
        <el-table-column prop="remark" label="备注" show-overflow-tooltip />
      </el-table>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getDeliveryOrderPage, createDeliveryOrder, deleteDeliveryOrder, getDeliveryOrderItems, markDelivered, markAccepted, printDelivery } from '../../api/deliveryOrder'
import { getCustomerPage } from '../../api/customer'

const list = ref([])
const loading = ref(false)
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(10)
const keyword = ref('')
const customerId = ref(null)
const status = ref(null)
const startDate = ref('')
const endDate = ref('')
const customers = ref([])

const statusOptions = [
  { value: 0, label: '已打印' },
  { value: 1, label: '已送达' },
  { value: 2, label: '已验收' }
]

const dialogVisible = ref(false)
const submitting = ref(false)
const formRef = ref(null)
const form = reactive({ deliveryDate: '', orderDate: '', customerId: null, pointId: null, remark: '' })
const itemRows = ref([])

const rules = {
  deliveryDate: [{ required: true, message: '请选择送货日期', trigger: 'change' }],
  orderDate: [{ required: true, message: '请选择订单日期', trigger: 'change' }],
  customerId: [{ required: true, message: '请选择客户', trigger: 'change' }]
}

const itemsDialogVisible = ref(false)
const currentDeliveryOrder = ref(null)
const deliveryItems = ref([])

function statusTag(s) {
  const map = { 0: ['已打印', 'info'], 1: ['已送达', 'warning'], 2: ['已验收', 'success'] }
  return { text: map[s][0], type: map[s][1] }
}

function adjustStatusLabel(s) {
  const map = { 0: '正常', 1: '部分退', 2: '全部退', 3: '换货', 4: '加单' }
  return map[s] || '正常'
}

async function load() {
  loading.value = true
  try {
    const res = await getDeliveryOrderPage({ pageNum: pageNum.value, pageSize: pageSize.value, keyword: keyword.value, customerId: customerId.value, status: status.value, startDate: startDate.value, endDate: endDate.value })
    list.value = res.data.records
    total.value = res.data.total
  } finally {
    loading.value = false
  }
}

async function loadCustomers() {
  const res = await getCustomerPage({ pageNum: 1, pageSize: 500 })
  customers.value = res.data.records
}

function openCreate() {
  Object.assign(form, { deliveryDate: '', orderDate: '', customerId: null, pointId: null, remark: '' })
  itemRows.value = []
  addItemRow()
  dialogVisible.value = true
}

function addItemRow() {
  itemRows.value.push({ originOrderId: null, originItemId: null, itemName: '', itemSpec: '', itemUnit: '', quantity: 0, unitPrice: 0 })
}

function removeItemRow(index) {
  itemRows.value.splice(index, 1)
}

async function submit() {
  await formRef.value.validate()
  const valid = itemRows.value.length > 0 && itemRows.value.every(r => r.itemName && r.quantity > 0)
  if (!valid) {
    ElMessage.warning('请确保所有明细项有效')
    return
  }
  submitting.value = true
  try {
    const data = { order: { deliveryDate: form.deliveryDate, orderDate: form.orderDate, customerId: form.customerId, pointId: form.pointId, remark: form.remark }, items: itemRows.value }
    await createDeliveryOrder(data)
    ElMessage.success('创建成功')
    dialogVisible.value = false
    await load()
  } finally {
    submitting.value = false
  }
}

async function remove(row) {
  await ElMessageBox.confirm(`确定删除送货单"${row.orderNo}"吗？`, '确认')
  await deleteDeliveryOrder(row.id)
  ElMessage.success('删除成功')
  await load()
}

async function deliver(row) {
  await markDelivered(row.id)
  ElMessage.success('送达成功')
  await load()
}

async function accept(row) {
  await markAccepted(row.id)
  ElMessage.success('验收成功')
  await load()
}

async function printRow(row) {
  const res = await printDelivery(row.id)
  row.printCount = res.data.printCount
  ElMessage.success('打印成功')
}

async function openItems(row) {
  currentDeliveryOrder.value = row
  const res = await getDeliveryOrderItems(row.id)
  deliveryItems.value = res.data
  itemsDialogVisible.value = true
}

onMounted(() => { load(); loadCustomers() })
</script>

<style scoped>
.delivery-order-page { padding: 16px }
.toolbar { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px }
.pagination { margin-top: 16px; text-align: right }
.item-section { margin-top: 16px }
.item-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 8px }
</style>