<template>
  <div class="purchase-order-page">
    <el-card>
      <div class="toolbar">
        <el-form inline>
          <el-form-item>
            <el-input v-model="keyword" placeholder="采购单号" clearable style="width: 160px" @keyup.enter="load" />
          </el-form-item>
          <el-form-item>
            <el-select v-model="supplierId" placeholder="供应商" clearable style="width: 160px">
              <el-option v-for="s in suppliers" :key="s.id" :label="s.name" :value="s.id" />
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
        <el-button-group>
            <el-button type="primary" @click="openCreate">新增采购单</el-button>
            <el-button type="success" @click="aggregateAndCreate">从订单生成</el-button>
          </el-button-group>
      </div>

      <el-table :data="list" v-loading="loading" border>
        <el-table-column prop="orderNo" label="采购单号" />
        <el-table-column prop="orderDate" label="订单日期" width="110" />
        <el-table-column prop="supplierId" label="供应商ID" width="90" />
        <el-table-column prop="sourceType" label="来源" width="70">
          <template #default="{ row }">
            {{ row.sourceType === 1 ? '自动生成' : '手动' }}
          </template>
        </el-table-column>
        <el-table-column prop="totalAmount" label="采购总额" width="110" />
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="statusTag(row.status).type">{{ statusTag(row.status).text }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="创建时间" />
        <el-table-column label="操作" width="300">
          <template #default="{ row }">
            <el-button link type="primary" @click="openItems(row)">明细</el-button>
            <template v-if="row.status === 0">
              <el-button link type="success" @click="confirm(row)">确认</el-button>
            </template>
            <template v-else-if="row.status === 1">
              <el-button link type="warning" @click="stockIn(row)">入库</el-button>
            </template>
            <template v-if="row.status < 1">
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

    <el-dialog v-model="dialogVisible" :title="isFromAggregate ? '从订单生成采购单' : '新增采购单'" width="720px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="90px">
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="订单日期" prop="orderDate">
              <el-date-picker v-model="form.orderDate" type="date" format="YYYY-MM-DD" value-format="YYYY-MM-DD" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="供应商ID" prop="supplierId">
              <el-select v-model="form.supplierId" placeholder="选择供应商" style="width: 100%" filterable>
                <el-option v-for="s in suppliers" :key="s.id" :label="s.name" :value="s.id" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>

      <div class="item-section">
        <div class="item-header">
          <span>采购明细</span>
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

    <el-dialog v-model="itemsDialogVisible" :title="`采购明细 - ${currentOrder?.orderNo || ''}`" width="720px">
      <el-table :data="orderItems" border>
        <el-table-column prop="skuId" label="SKU ID" width="80" />
        <el-table-column prop="itemName" label="品名" />
        <el-table-column prop="itemSpec" label="规格" />
        <el-table-column prop="itemUnit" label="单位" />
        <el-table-column prop="quantity" label="数量" />
        <el-table-column prop="unitPrice" label="单价" />
        <el-table-column prop="subtotal" label="小计" />
        <el-table-column prop="remark" label="备注" show-overflow-tooltip />
      </el-table>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getPurchaseOrderPage, createPurchaseOrder, deletePurchaseOrder, getPurchaseOrderItems, confirmPurchaseOrder, stockIn as stockInApi } from '../../api/purchaseOrder'
import { getSupplierPage } from '../../api/supplier'
import { getOrderPage } from '../../api/order'
import { aggregateFromOrders } from '../../api/purchaseOrder'

const list = ref([])
const loading = ref(false)
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(10)
const keyword = ref('')
const supplierId = ref(null)
const status = ref(null)
const startDate = ref('')
const endDate = ref('')
const suppliers = ref([])

const statusOptions = [
  { value: 0, label: '草稿' },
  { value: 1, label: '已确认' },
  { value: 2, label: '已入库' }
]

const dialogVisible = ref(false)
const isEdit = ref(false)
const isFromAggregate = ref(false)
const submitting = ref(false)
const formRef = ref(null)
const form = reactive({ orderDate: '', supplierId: null })
const itemRows = ref([])

const rules = {
  orderDate: [{ required: true, message: '请选择订单日期', trigger: 'change' }],
  supplierId: [{ required: true, message: '请选择供应商', trigger: 'change' }]
}

const itemsDialogVisible = ref(false)
const currentOrder = ref(null)
const orderItems = ref([])

function statusTag(s) {
  const map = { 0: ['草稿', 'info'], 1: ['已确认', 'primary'], 2: ['已入库', 'success'] }
  return { text: map[s][0], type: map[s][1] }
}

async function load() {
  loading.value = true
  try {
    const res = await getPurchaseOrderPage({ pageNum: pageNum.value, pageSize: pageSize.value, keyword: keyword.value, supplierId: supplierId.value, status: status.value, startDate: startDate.value, endDate: endDate.value })
    list.value = res.data.records
    total.value = res.data.total
  } finally {
    loading.value = false
  }
}

async function loadSuppliers() {
  const res = await getSupplierPage({ pageNum: 1, pageSize: 500 })
  suppliers.value = res.data.records
}

function openCreate() {
  isEdit.value = false
  isFromAggregate.value = false
  Object.assign(form, { orderDate: '', supplierId: null })
  itemRows.value = []
  addItemRow()
  dialogVisible.value = true
}

function addItemRow() {
  itemRows.value.push({ skuId: null, itemName: '', itemSpec: '', itemUnit: '', quantity: 0, unitPrice: 0 })
}

function removeItemRow(index) {
  itemRows.value.splice(index, 1)
}

async function aggregateAndCreate() {
  isFromAggregate.value = true
  isEdit.value = false
  const res = await getOrderPage({ pageNum: 1, pageSize: 500, status: 1 })
  const orderIds = res.data.records.map(o => o.id)
  if (orderIds.length === 0) {
    ElMessage.warning('没有可汇总的订单')
    return
  }
  const aggRes = await aggregateFromOrders(orderIds)
  itemRows.value = aggRes.data.map(i => ({
    skuId: i.skuId,
    itemName: i.itemName,
    itemSpec: i.itemSpec,
    itemUnit: i.itemUnit,
    quantity: i.quantity,
    unitPrice: i.unitPrice
  }))
  Object.assign(form, { orderDate: '', supplierId: null })
  dialogVisible.value = true
}

async function submit() {
  await formRef.value.validate()
  const valid = itemRows.value.length > 0 && itemRows.value.every(r => r.skuId && r.itemName && r.quantity > 0)
  if (!valid) {
    ElMessage.warning('请确保所有明细项有效')
    return
  }
  submitting.value = true
  try {
    const data = { order: { orderDate: form.orderDate, supplierId: form.supplierId, sourceType: isFromAggregate.value ? 1 : 0 }, items: itemRows.value }
    await createPurchaseOrder(data)
    ElMessage.success('创建成功')
    dialogVisible.value = false
    await load()
  } finally {
    submitting.value = false
  }
}

async function remove(row) {
  await ElMessageBox.confirm(`确定删除采购单"${row.orderNo}"吗？`, '确认')
  await deletePurchaseOrder(row.id)
  ElMessage.success('删除成功')
  await load()
}

async function confirm(row) {
  await confirmPurchaseOrder(row.id)
  ElMessage.success('确认成功')
  await load()
}

async function stockIn(row) {
  await stockInApi(row.id)
  ElMessage.success('入库成功')
  await load()
}

async function openItems(row) {
  currentOrder.value = row
  const res = await getPurchaseOrderItems(row.id)
  orderItems.value = res.data
  itemsDialogVisible.value = true
}

onMounted(() => { load(); loadSuppliers() })
</script>

<style scoped>
.purchase-order-page { padding: 16px }
.toolbar { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px }
.pagination { margin-top: 16px; text-align: right }
.item-section { margin-top: 16px }
.item-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 8px }
</style>