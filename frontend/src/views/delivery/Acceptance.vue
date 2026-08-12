<template>
  <div class="acceptance-page">
    <el-card>
      <div class="toolbar">
        <el-form inline>
          <el-form-item>
            <el-input v-model="keyword" placeholder="验收单号" clearable style="width: 160px" @keyup.enter="load" />
          </el-form-item>
          <el-form-item>
            <el-select v-model="customerId" placeholder="客户" clearable style="width: 160px">
              <el-option v-for="c in customers" :key="c.id" :label="c.name" :value="c.id" />
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
        <el-button type="primary" @click="openCreate">新增验收</el-button>
      </div>

      <el-table :data="list" v-loading="loading" border>
        <el-table-column prop="orderNo" label="验收单号" />
        <el-table-column prop="deliveryOrderNo" label="送货单号" />
        <el-table-column prop="deliveryDate" label="送货日期" width="110" />
        <el-table-column prop="customerId" label="客户ID" width="80" />
        <el-table-column prop="pointId" label="配送点ID" width="90" />
        <el-table-column prop="totalAmount" label="实收金额" width="110" />
        <el-table-column prop="totalLossAmount" label="短损金额" width="110" />
        <el-table-column prop="createdAt" label="创建时间" />
        <el-table-column label="操作" width="80">
          <template #default="{ row }">
            <el-button link type="primary" @click="openItems(row)">明细</el-button>
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

    <el-dialog v-model="dialogVisible" title="新增验收单" width="680px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="90px">
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="送货单ID" prop="deliveryOrderId">
              <el-input-number v-model="form.deliveryOrderId" :min="1" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>

      <div class="item-section">
        <div class="item-header">
          <span>验收明细</span>
          <el-button type="primary" size="small" @click="addItemRow">添加明细</el-button>
        </div>
        <el-table :data="itemRows" border size="small">
          <el-table-column label="送货行ID" width="90">
            <template #default="{ row }">
              <el-input-number v-model="row.deliveryItemId" :min="1" size="small" style="width: 100%" />
            </template>
          </el-table-column>
          <el-table-column label="品名" width="120">
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
          <el-table-column label="配送数量" width="100">
            <template #default="{ row }">
              <el-input-number v-model="row.deliveredQuantity" :min="0" :precision="2" size="small" style="width: 100%" />
            </template>
          </el-table-column>
          <el-table-column label="实收数量" width="100">
            <template #default="{ row }">
              <el-input-number v-model="row.actualQuantity" :min="0" :precision="2" size="small" style="width: 100%" />
            </template>
          </el-table-column>
          <el-table-column label="短损数量" width="90">
            <template #default="{ row }">
              {{ (row.actualQuantity - row.deliveredQuantity).toFixed(2) }}
            </template>
          </el-table-column>
          <el-table-column label="单价" width="100">
            <template #default="{ row }">
              <el-input-number v-model="row.unitPrice" :min="0" :precision="2" size="small" style="width: 100%" />
            </template>
          </el-table-column>
          <el-table-column label="实收金额" width="100">
            <template #default="{ row }">
              {{ ((row.actualQuantity || 0) * (row.unitPrice || 0)).toFixed(2) }}
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
        <el-button type="primary" :loading="submitting" @click="submit">提交验收</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="itemsDialogVisible" :title="`验收明细 - ${currentAcceptance?.orderNo || ''}`" width="720px">
      <el-table :data="acceptanceItems" border>
        <el-table-column prop="deliveryItemId" label="送货行ID" width="80" />
        <el-table-column prop="itemName" label="品名" />
        <el-table-column prop="itemSpec" label="规格" />
        <el-table-column prop="itemUnit" label="单位" />
        <el-table-column prop="deliveredQuantity" label="配送数量" />
        <el-table-column prop="actualQuantity" label="实收数量" />
        <el-table-column prop="lossQuantity" label="短损数量" />
        <el-table-column prop="unitPrice" label="单价" />
        <el-table-column prop="actualAmount" label="实收金额" />
        <el-table-column prop="remark" label="备注" show-overflow-tooltip />
      </el-table>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getAcceptancePage, createAcceptance, getAcceptanceItems } from '../../api/acceptance'
import { getCustomerPage } from '../../api/customer'

const list = ref([])
const loading = ref(false)
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(10)
const keyword = ref('')
const customerId = ref(null)
const startDate = ref('')
const endDate = ref('')
const customers = ref([])

const dialogVisible = ref(false)
const submitting = ref(false)
const formRef = ref(null)
const form = reactive({ deliveryOrderId: null })
const itemRows = ref([])

const rules = {
  deliveryOrderId: [{ required: true, message: '请选择送货单', trigger: 'change' }]
}

const itemsDialogVisible = ref(false)
const currentAcceptance = ref(null)
const acceptanceItems = ref([])

async function load() {
  loading.value = true
  try {
    const res = await getAcceptancePage({ pageNum: pageNum.value, pageSize: pageSize.value, keyword: keyword.value, customerId: customerId.value, startDate: startDate.value, endDate: endDate.value })
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
  Object.assign(form, { deliveryOrderId: null })
  itemRows.value = []
  addItemRow()
  dialogVisible.value = true
}

function addItemRow() {
  itemRows.value.push({ deliveryItemId: null, itemName: '', itemSpec: '', itemUnit: '', deliveredQuantity: 0, actualQuantity: 0, unitPrice: 0 })
}

function removeItemRow(index) {
  itemRows.value.splice(index, 1)
}

async function submit() {
  await formRef.value.validate()
  const valid = itemRows.value.length > 0 && itemRows.value.every(r => r.deliveryItemId && r.itemName && r.deliveredQuantity >= 0 && r.actualQuantity >= 0)
  if (!valid) {
    ElMessage.warning('请确保所有明细项有效')
    return
  }
  submitting.value = true
  try {
    const data = { acceptance: { deliveryOrderId: form.deliveryOrderId }, items: itemRows.value }
    await createAcceptance(data)
    ElMessage.success('验收成功')
    dialogVisible.value = false
    await load()
  } finally {
    submitting.value = false
  }
}

async function openItems(row) {
  currentAcceptance.value = row
  const res = await getAcceptanceItems(row.id)
  acceptanceItems.value = res.data
  itemsDialogVisible.value = true
}

onMounted(() => { load(); loadCustomers() })
</script>

<style scoped>
.acceptance-page { padding: 16px }
.toolbar { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px }
.pagination { margin-top: 16px; text-align: right }
.item-section { margin-top: 16px }
.item-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 8px }
</style>