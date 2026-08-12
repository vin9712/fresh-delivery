<template>
  <div class="order-page">
    <el-card>
      <div class="toolbar">
        <el-form inline>
          <el-form-item>
            <el-input v-model="keyword" placeholder="订单编号" clearable style="width: 160px" @keyup.enter="load" />
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
            <el-date-picker v-model="orderDate" type="date" format="YYYY-MM-DD" value-format="YYYY-MM-DD" placeholder="订单日期" style="width: 160px" />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="load">搜索</el-button>
          </el-form-item>
        </el-form>
        <el-button type="primary" @click="openCreate">新增订单</el-button>
      </div>

      <el-table :data="list" v-loading="loading" border>
        <el-table-column prop="orderNo" label="订单编号" />
        <el-table-column prop="orderDate" label="订单日期" width="110" />
        <el-table-column prop="customerId" label="客户ID" width="80" />
        <el-table-column prop="pointId" label="配送点ID" width="90" />
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="statusTag(row.status).type">{{ statusTag(row.status).text }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="remark" label="备注" show-overflow-tooltip />
        <el-table-column prop="createdAt" label="创建时间" />
        <el-table-column label="操作" width="300">
          <template #default="{ row }">
            <el-button link type="primary" @click="openEdit(row)">编辑</el-button>
            <el-button link type="info" @click="openItems(row)">明细</el-button>
            <template v-if="row.status < 4">
              <el-button link type="success" @click="advance(row)">{{ advanceLabel(row.status) }}</el-button>
            </template>
            <el-button link type="danger" @click="remove(row)">删除</el-button>
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

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑订单' : '新增订单'" width="680px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
        <el-row :gutter="16">
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
          <span>订单明细</span>
          <el-button type="primary" size="small" @click="addItemRow">添加明细</el-button>
        </div>
        <el-table :data="itemRows" border size="small">
          <el-table-column label="SKU ID" width="80">
            <template #default="{ row }">
              <el-input-number v-model="row.skuId" :min="1" size="small" style="width: 100%" />
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
            <template #default="{ row, $index }">
              <div class="price-cell">
                <el-input-number v-model="row.unitPrice" :min="0" :precision="2" size="small" style="width: 100%" />
                <el-button link type="primary" size="small" @click="autoPrice(row, $index)">自动</el-button>
              </div>
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

    <el-dialog v-model="itemsDialogVisible" :title="`订单明细 - ${currentOrder?.orderNo || ''}`" width="720px">
      <el-table :data="orderItems" border>
        <el-table-column prop="itemName" label="品名" />
        <el-table-column prop="itemSpec" label="规格" />
        <el-table-column prop="itemUnit" label="单位" />
        <el-table-column prop="quantity" label="数量" />
        <el-table-column prop="unitPrice" label="单价" />
        <el-table-column prop="subtotal" label="小计" />
        <el-table-column prop="itemStatus" label="状态" width="80">
          <template #default="{ row }">
            {{ itemStatusLabel(row.itemStatus) }}
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
import { getOrderPage, createOrder, updateOrder, deleteOrder, getOrderItems, confirmOrder, deliverOrder, acceptOrder, settleOrder } from '../../api/order'
import { lookupPrice } from '../../api/orderAdjustment'
import { getCustomerPage } from '../../api/customer'

const list = ref([])
const loading = ref(false)
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(10)
const keyword = ref('')
const customerId = ref(null)
const status = ref(null)
const orderDate = ref('')
const customers = ref([])

const statusOptions = [
  { value: 0, label: '草稿' },
  { value: 1, label: '已确认' },
  { value: 2, label: '已送货' },
  { value: 3, label: '已验收' },
  { value: 4, label: '已结算' }
]

const dialogVisible = ref(false)
const isEdit = ref(false)
const editingId = ref(null)
const submitting = ref(false)
const formRef = ref(null)
const form = reactive({ orderDate: '', customerId: null, pointId: null, remark: '' })
const itemRows = ref([])

const rules = {
  orderDate: [{ required: true, message: '请选择订单日期', trigger: 'change' }],
  customerId: [{ required: true, message: '请选择客户', trigger: 'change' }]
}

const itemsDialogVisible = ref(false)
const currentOrder = ref(null)
const orderItems = ref([])

function statusTag(s) {
  const map = { 0: ['草稿', 'info'], 1: ['已确认', 'primary'], 2: ['已送货', 'warning'], 3: ['已验收', 'success'], 4: ['已结算', 'success'] }
  return { text: map[s][0], type: map[s][1] }
}

function advanceLabel(s) {
  const map = { 0: '确认', 1: '送货', 2: '验收', 3: '结算' }
  return map[s] || ''
}

function itemStatusLabel(s) {
  const map = { 0: '正常', 1: '已退单', 2: '已换货', 3: '部分退单' }
  return map[s] || '正常'
}

async function load() {
  loading.value = true
  try {
    const res = await getOrderPage({ pageNum: pageNum.value, pageSize: pageSize.value, keyword: keyword.value, customerId: customerId.value, status: status.value, orderDate: orderDate.value })
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
  isEdit.value = false
  editingId.value = null
  Object.assign(form, { orderDate: '', customerId: null, pointId: null, remark: '' })
  itemRows.value = []
  addItemRow()
  dialogVisible.value = true
}

async function openEdit(row) {
  isEdit.value = true
  editingId.value = row.id
  Object.assign(form, { orderDate: row.orderDate, customerId: row.customerId, pointId: row.pointId, remark: row.remark || '' })
  const res = await getOrderItems(row.id)
  itemRows.value = res.data.map(i => ({ skuId: i.skuId, itemName: i.itemName, itemSpec: i.itemSpec, itemUnit: i.itemUnit, quantity: i.quantity, unitPrice: i.unitPrice, remark: i.remark }))
  dialogVisible.value = true
}

function addItemRow() {
  itemRows.value.push({ skuId: null, itemName: '', itemSpec: '', itemUnit: '', quantity: 0, unitPrice: 0 })
}

function removeItemRow(index) {
  itemRows.value.splice(index, 1)
}

async function autoPrice(row, index) {
  if (!row.skuId || !form.customerId) {
    ElMessage.warning('请先填写SKU ID和客户')
    return
  }
  try {
    const res = await lookupPrice(form.customerId, row.skuId, form.orderDate)
    if (res.data) {
      itemRows.value[index].unitPrice = res.data
      ElMessage.success('价格已自动填充')
    } else {
      ElMessage.info('未找到对应报价，请手动填写')
    }
  } catch (e) {
    ElMessage.error('获取价格失败')
  }
}

async function submit() {
  await formRef.value.validate()
  const valid = itemRows.value.length > 0 && itemRows.value.every(r => r.skuId && r.itemName && r.quantity > 0 && r.unitPrice >= 0)
  if (!valid) {
    ElMessage.warning('请确保所有明细项有效')
    return
  }
  submitting.value = true
  try {
    const data = { order: { orderDate: form.orderDate, customerId: form.customerId, pointId: form.pointId, remark: form.remark }, items: itemRows.value }
    if (isEdit.value) {
      await updateOrder(editingId.value, data.order)
      ElMessage.success('更新成功')
    } else {
      await createOrder(data)
      ElMessage.success('创建成功')
    }
    dialogVisible.value = false
    await load()
  } finally {
    submitting.value = false
  }
}

async function remove(row) {
  await ElMessageBox.confirm(`确定删除订单"${row.orderNo}"吗？`, '确认')
  await deleteOrder(row.id)
  ElMessage.success('删除成功')
  await load()
}

async function advance(row) {
  const fn = { 0: confirmOrder, 1: deliverOrder, 2: acceptOrder, 3: settleOrder }[row.status]
  await fn(row.id)
  ElMessage.success(advanceLabel(row.status) + '成功')
  await load()
}

async function openItems(row) {
  currentOrder.value = row
  const res = await getOrderItems(row.id)
  orderItems.value = res.data
  itemsDialogVisible.value = true
}

onMounted(() => { load(); loadCustomers() })
</script>

<style scoped>
.order-page { padding: 16px }
.toolbar { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px }
.pagination { margin-top: 16px; text-align: right }
.item-section { margin-top: 16px }
.item-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 8px }
.price-cell { display: flex; align-items: center; gap: 4px }
</style>