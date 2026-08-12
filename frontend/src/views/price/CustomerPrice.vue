<template>
  <div class="customer-price-page">
    <el-card>
      <div class="toolbar">
        <el-form inline>
          <el-form-item>
            <el-select v-model="customerId" placeholder="选择客户" clearable style="width: 180px">
              <el-option v-for="c in customers" :key="c.id" :label="c.name" :value="c.id" />
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-select v-model="status" placeholder="状态" clearable style="width: 120px">
              <el-option label="草稿" :value="0" />
              <el-option label="生效" :value="1" />
              <el-option label="已拒绝" :value="2" />
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="load">搜索</el-button>
          </el-form-item>
        </el-form>
        <div>
          <el-button type="warning" @click="openImport">模板导入</el-button>
          <el-button type="primary" @click="openCreate">新增报价</el-button>
        </div>
      </div>

      <el-table :data="list" v-loading="loading" border>
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="customerId" label="客户ID" width="90" />
        <el-table-column prop="skuId" label="SKU ID" width="90" />
        <el-table-column prop="price" label="价格" />
        <el-table-column prop="startDate" label="生效日期" width="110" />
        <el-table-column prop="endDate" label="到期日期" width="110" />
        <el-table-column prop="sourceType" label="来源" width="80">
          <template #default="{ row }">
            {{ row.sourceType === 1 ? '模板' : '手动' }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="statusTag(row.status).type">
              {{ statusTag(row.status).text }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="220">
          <template #default="{ row }">
            <template v-if="row.status === 0">
              <el-button link type="success" @click="activate(row)">生效</el-button>
              <el-button link type="info" @click="reject(row)">拒绝</el-button>
            </template>
            <el-button link type="primary" @click="openEdit(row)">编辑</el-button>
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

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑报价' : '新增报价'" width="460px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="90px">
        <el-form-item label="客户ID" prop="customerId">
          <el-input-number v-model="form.customerId" :min="1" style="width: 100%" />
        </el-form-item>
        <el-form-item label="SKU ID" prop="skuId">
          <el-input-number v-model="form.skuId" :min="1" style="width: 100%" />
        </el-form-item>
        <el-form-item label="价格" prop="price">
          <el-input-number v-model="form.price" :min="0" :precision="2" style="width: 100%" />
        </el-form-item>
        <el-form-item label="生效日期">
          <el-date-picker v-model="form.startDate" type="date" format="YYYY-MM-DD" value-format="YYYY-MM-DD" style="width: 100%" />
        </el-form-item>
        <el-form-item label="到期日期">
          <el-date-picker v-model="form.endDate" type="date" format="YYYY-MM-DD" value-format="YYYY-MM-DD" style="width: 100%" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="submit">确定</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="importDialogVisible" title="模板导入" width="460px">
      <el-form ref="importFormRef" :model="importForm" :rules="importRules" label-width="90px">
        <el-form-item label="客户" prop="customerId">
          <el-select v-model="importForm.customerId" style="width: 100%" filterable>
            <el-option v-for="c in customers" :key="c.id" :label="c.name" :value="c.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="模板" prop="templateId">
          <el-select v-model="importForm.templateId" style="width: 100%" filterable>
            <el-option v-for="t in templates" :key="t.id" :label="t.name" :value="t.id" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="importDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="importSubmitting" @click="doImport">导入</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getCustomerPricePage, createCustomerPrice, updateCustomerPrice, deleteCustomerPrice, importFromTemplate, activateCustomerPrice, rejectCustomerPrice } from '../../api/customerPrice'
import { getCustomerPage } from '../../api/customer'
import { getTemplatePage } from '../../api/priceTemplate'

const list = ref([])
const loading = ref(false)
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(10)
const customerId = ref(null)
const status = ref(null)
const customers = ref([])
const templates = ref([])

const dialogVisible = ref(false)
const isEdit = ref(false)
const editingId = ref(null)
const submitting = ref(false)
const formRef = ref(null)
const form = reactive({ customerId: null, skuId: null, price: 0, startDate: '', endDate: '' })
const rules = {
  customerId: [{ required: true, message: '请选择客户', trigger: 'change' }],
  skuId: [{ required: true, message: '请选择SKU', trigger: 'change' }],
  price: [{ required: true, message: '请输入价格', trigger: 'blur' }]
}

const importDialogVisible = ref(false)
const importSubmitting = ref(false)
const importFormRef = ref(null)
const importForm = reactive({ customerId: null, templateId: null })
const importRules = {
  customerId: [{ required: true, message: '请选择客户', trigger: 'change' }],
  templateId: [{ required: true, message: '请选择模板', trigger: 'change' }]
}

function statusTag(s) {
  if (s === 0) return { text: '草稿', type: 'info' }
  if (s === 1) return { text: '生效', type: 'success' }
  if (s === 2) return { text: '已拒绝', type: 'danger' }
  return { text: '-', type: '' }
}

async function load() {
  loading.value = true
  try {
    const res = await getCustomerPricePage({ pageNum: pageNum.value, pageSize: pageSize.value, customerId: customerId.value, status: status.value })
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

async function loadTemplates() {
  const res = await getTemplatePage({ pageNum: 1, pageSize: 500 })
  templates.value = res.data.records
}

function openCreate() {
  isEdit.value = false
  editingId.value = null
  Object.assign(form, { customerId: null, skuId: null, price: 0, startDate: '', endDate: '' })
  dialogVisible.value = true
}

function openEdit(row) {
  isEdit.value = true
  editingId.value = row.id
  Object.assign(form, { customerId: row.customerId, skuId: row.skuId, price: row.price, startDate: row.startDate, endDate: row.endDate })
  dialogVisible.value = true
}

async function submit() {
  await formRef.value.validate()
  submitting.value = true
  try {
    if (isEdit.value) {
      await updateCustomerPrice(editingId.value, { customerId: form.customerId, skuId: form.skuId, price: form.price, startDate: form.startDate, endDate: form.endDate })
      ElMessage.success('更新成功')
    } else {
      await createCustomerPrice({ customerId: form.customerId, skuId: form.skuId, price: form.price, startDate: form.startDate, endDate: form.endDate, status: 0 })
      ElMessage.success('创建成功')
    }
    dialogVisible.value = false
    await load()
  } finally {
    submitting.value = false
  }
}

async function remove(row) {
  await ElMessageBox.confirm(`确定删除该报价吗？`, '确认')
  await deleteCustomerPrice(row.id)
  ElMessage.success('删除成功')
  await load()
}

async function activate(row) {
  await activateCustomerPrice(row.id)
  ElMessage.success('已生效')
  await load()
}

async function reject(row) {
  await rejectCustomerPrice(row.id)
  ElMessage.success('已拒绝')
  await load()
}

function openImport() {
  Object.assign(importForm, { customerId: null, templateId: null })
  importDialogVisible.value = true
}

async function doImport() {
  await importFormRef.value.validate()
  importSubmitting.value = true
  try {
    await importFromTemplate(importForm.templateId, importForm.customerId)
    ElMessage.success('导入成功')
    importDialogVisible.value = false
    await load()
  } finally {
    importSubmitting.value = false
  }
}

onMounted(() => { load(); loadCustomers(); loadTemplates() })
</script>

<style scoped>
.customer-price-page { padding: 16px }
.toolbar { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px }
.pagination { margin-top: 16px; text-align: right }
</style>