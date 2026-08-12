<template>
  <div class="adjustment-page">
    <el-card>
      <div class="toolbar">
        <el-form inline>
          <el-form-item>
            <el-input-number v-model="orderId" placeholder="订单ID" :min="1" style="width: 140px" />
          </el-form-item>
          <el-form-item>
            <el-select v-model="adjustType" placeholder="类型" clearable style="width: 120px">
              <el-option label="加单" :value="0" />
              <el-option label="退单" :value="1" />
              <el-option label="换货" :value="2" />
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-date-picker v-model="orderDate" type="date" format="YYYY-MM-DD" value-format="YYYY-MM-DD" placeholder="订单日期" style="width: 160px" />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="load">搜索</el-button>
          </el-form-item>
        </el-form>
        <el-button type="primary" @click="openCreate">新增调整</el-button>
      </div>

      <el-table :data="list" v-loading="loading" border>
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="orderId" label="订单ID" width="80" />
        <el-table-column prop="originItemId" label="原行ID" width="80" />
        <el-table-column prop="adjustType" label="类型" width="70">
          <template #default="{ row }">
            <el-tag :type="adjustTypeTag(row.adjustType).type">{{ adjustTypeTag(row.adjustType).text }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="adjustDate" label="操作日期" width="110" />
        <el-table-column prop="itemNo" label="品名" />
        <el-table-column prop="itemSpec" label="规格" width="100" />
        <el-table-column prop="itemUnit" label="单位" width="70" />
        <el-table-column prop="skuId" label="SKU ID" width="80" />
        <el-table-column prop="quantity" label="数量" />
        <el-table-column prop="unitPrice" label="单价" />
        <el-table-column prop="remark" label="备注" show-overflow-tooltip />
        <el-table-column label="操作" width="80">
          <template #default="{ row }">
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

    <el-dialog v-model="dialogVisible" title="新增调整记录" width="460px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="90px">
        <el-form-item label="订单ID" prop="orderId">
          <el-input-number v-model="form.orderId" :min="1" style="width: 100%" />
        </el-form-item>
        <el-form-item label="原行ID" prop="originItemId">
          <el-input-number v-model="form.originItemId" :min="1" style="width: 100%" />
        </el-form-item>
        <el-form-item label="类型" prop="adjustType">
          <el-radio-group v-model="form.adjustType">
            <el-radio :label="0">加单</el-radio>
            <el-radio :label="1">退单</el-radio>
            <el-radio :label="2">换货</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="品名" prop="itemName">
          <el-input v-model="form.itemName" />
        </el-form-item>
        <el-form-item label="规格">
          <el-input v-model="form.itemSpec" />
        </el-form-item>
        <el-form-item label="单位">
          <el-input v-model="form.itemUnit" />
        </el-form-item>
        <el-form-item label="SKU ID">
          <el-input-number v-model="form.skuId" :min="1" style="width: 100%" />
        </el-form-item>
        <el-form-item label="数量" prop="quantity">
          <el-input-number v-model="form.quantity" :min="0" :precision="2" style="width: 100%" />
        </el-form-item>
        <el-form-item label="单价">
          <el-input-number v-model="form.unitPrice" :min="0" :precision="2" style="width: 100%" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="form.remark" type="textarea" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="submit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getAdjustmentPage, createAdjustment, deleteAdjustment } from '../../api/orderAdjustment'

const list = ref([])
const loading = ref(false)
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(10)
const orderId = ref(null)
const adjustType = ref(null)
const orderDate = ref('')

const dialogVisible = ref(false)
const submitting = ref(false)
const formRef = ref(null)
const form = reactive({ orderId: null, originItemId: null, adjustType: 0, itemName: '', itemSpec: '', itemUnit: '', skuId: null, quantity: 0, unitPrice: 0, remark: '' })
const rules = {
  orderId: [{ required: true, message: '请输入订单ID', trigger: 'change' }],
  adjustType: [{ required: true, message: '请选择类型', trigger: 'change' }],
  quantity: [{ required: true, message: '请输入数量', trigger: 'blur' }]
}

function adjustTypeTag(t) {
  const map = { 0: ['加单', 'success'], 1: ['退单', 'danger'], 2: ['换货', 'warning'] }
  return map[t] || { text: '-', type: '' }
}

async function load() {
  loading.value = true
  try {
    const res = await getAdjustmentPage({ pageNum: pageNum.value, pageSize: pageSize.value, orderId: orderId.value, adjustType: adjustType.value, orderDate: orderDate.value })
    list.value = res.data.records
    total.value = res.data.total
  } finally {
    loading.value = false
  }
}

function openCreate() {
  Object.assign(form, { orderId: null, originItemId: null, adjustType: 0, itemName: '', itemSpec: '', itemUnit: '', skuId: null, quantity: 0, unitPrice: 0, remark: '' })
  dialogVisible.value = true
}

async function submit() {
  await formRef.value.validate()
  submitting.value = true
  try {
    await createAdjustment({ orderId: form.orderId, originItemId: form.originItemId, adjustType: form.adjustType, itemName: form.itemName, itemSpec: form.itemSpec, itemUnit: form.itemUnit, skuId: form.skuId, quantity: form.quantity, unitPrice: form.unitPrice, remark: form.remark })
    ElMessage.success('创建成功')
    dialogVisible.value = false
    await load()
  } finally {
    submitting.value = false
  }
}

async function remove(row) {
  await ElMessageBox.confirm('确定删除该调整记录吗？', '确认')
  await deleteAdjustment(row.id)
  ElMessage.success('删除成功')
  await load()
}

onMounted(load)
</script>

<style scoped>
.adjustment-page { padding: 16px }
.toolbar { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px }
.pagination { margin-top: 16px; text-align: right }
</style>