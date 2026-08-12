<template>
  <div class="template-page">
    <el-card>
      <div class="toolbar">
        <el-form inline>
          <el-form-item>
            <el-input v-model="keyword" placeholder="方案名称" clearable style="width: 200px" @keyup.enter="load" />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="load">搜索</el-button>
          </el-form-item>
        </el-form>
        <el-button type="primary" @click="openCreate">新增方案</el-button>
      </div>

      <el-table :data="list" v-loading="loading" border>
        <el-table-column prop="id" label="ID" width="70" />
        <el-table-column prop="name" label="方案名称" />
        <el-table-column prop="description" label="描述" show-overflow-tooltip />
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">
              {{ row.status === 1 ? '启用' : '停用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="创建时间" />
        <el-table-column label="操作" width="280">
          <template #default="{ row }">
            <el-button link type="primary" @click="openEdit(row)">编辑</el-button>
            <el-button link type="warning" @click="openSkus(row)">价格明细</el-button>
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

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑方案' : '新增方案'" width="460px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="90px">
        <el-form-item label="方案名称" prop="name">
          <el-input v-model="form.name" />
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input v-model="form.description" type="textarea" />
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="form.status">
            <el-radio :label="1">启用</el-radio>
            <el-radio :label="0">停用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="submit">确定</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="skusDialogVisible" :title="`价格明细 - ${currentTemplate?.name || ''}`" width="700px">
      <div class="sku-toolbar">
        <el-button type="primary" size="small" @click="addSkuRow">添加SKU</el-button>
        <el-button type="success" size="small" @click="saveSkus">保存明细</el-button>
      </div>
      <el-table :data="skuRows" border style="margin-top: 12px">
        <el-table-column label="SKU ID" width="100">
          <template #default="{ row }">
            <el-input-number v-model="row.skuId" :min="1" size="small" style="width: 100%" />
          </template>
        </el-table-column>
        <el-table-column label="价格" width="130">
          <template #default="{ row }">
            <el-input-number v-model="row.price" :min="0" :precision="2" size="small" style="width: 100%" />
          </template>
        </el-table-column>
        <el-table-column label="生效日期">
          <template #default="{ row }">
            <el-date-picker v-model="row.startDate" type="date" format="YYYY-MM-DD" value-format="YYYY-MM-DD" size="small" />
          </template>
        </el-table-column>
        <el-table-column label="到期日期">
          <template #default="{ row }">
            <el-date-picker v-model="row.endDate" type="date" format="YYYY-MM-DD" value-format="YYYY-MM-DD" size="small" />
          </template>
        </el-table-column>
        <el-table-column label="操作" width="80">
          <template #default="{ $index }">
            <el-button link type="danger" @click="removeSkuRow($index)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <template v-if="skuRows.length === 0">
        <el-empty description="暂无SKU" :image-size="80" />
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getTemplatePage, createTemplate, updateTemplate, deleteTemplate, getTemplateSkus, saveTemplateSkus } from '../../api/priceTemplate'

const list = ref([])
const loading = ref(false)
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(10)
const keyword = ref('')

const dialogVisible = ref(false)
const isEdit = ref(false)
const editingId = ref(null)
const submitting = ref(false)
const formRef = ref(null)
const form = reactive({ name: '', description: '', status: 1 })
const rules = {
  name: [{ required: true, message: '请输入方案名称', trigger: 'blur' }]
}

const skusDialogVisible = ref(false)
const currentTemplate = ref(null)
const skuRows = ref([])

async function load() {
  loading.value = true
  try {
    const res = await getTemplatePage({ pageNum: pageNum.value, pageSize: pageSize.value, keyword: keyword.value })
    list.value = res.data.records
    total.value = res.data.total
  } finally {
    loading.value = false
  }
}

function openCreate() {
  isEdit.value = false
  editingId.value = null
  Object.assign(form, { name: '', description: '', status: 1 })
  dialogVisible.value = true
}

function openEdit(row) {
  isEdit.value = true
  editingId.value = row.id
  Object.assign(form, { name: row.name, description: row.description || '', status: row.status })
  dialogVisible.value = true
}

async function submit() {
  await formRef.value.validate()
  submitting.value = true
  try {
    if (isEdit.value) {
      await updateTemplate(editingId.value, { name: form.name, description: form.description, status: form.status })
      ElMessage.success('更新成功')
    } else {
      await createTemplate({ name: form.name, description: form.description, status: form.status })
      ElMessage.success('创建成功')
    }
    dialogVisible.value = false
    await load()
  } finally {
    submitting.value = false
  }
}

async function remove(row) {
  await ElMessageBox.confirm(`确定删除方案"${row.name}"吗？`, '确认')
  await deleteTemplate(row.id)
  ElMessage.success('删除成功')
  await load()
}

async function openSkus(row) {
  currentTemplate.value = row
  const res = await getTemplateSkus(row.id)
  skuRows.value = res.data.map(s => ({ skuId: s.skuId, price: s.price, startDate: s.startDate, endDate: s.endDate }))
  skusDialogVisible.value = true
}

function addSkuRow() {
  skuRows.value.push({ skuId: 1, price: 0, startDate: '', endDate: '' })
}

function removeSkuRow(index) {
  skuRows.value.splice(index, 1)
}

async function saveSkus() {
  const valid = skuRows.value.every(r => r.skuId > 0 && r.price != null && r.price > 0)
  if (!valid) {
    ElMessage.warning('请确保所有SKU ID和价格有效')
    return
  }
  await saveTemplateSkus(currentTemplate.value.id, skuRows.value)
  ElMessage.success('保存成功')
}

onMounted(load)
</script>

<style scoped>
.template-page { padding: 16px }
.toolbar { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px }
.pagination { margin-top: 16px; text-align: right }
.sku-toolbar { display: flex; gap: 8px }
</style>