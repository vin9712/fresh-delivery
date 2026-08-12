<template>
  <div class="sku-page">
    <el-card>
      <div class="toolbar">
        <el-form inline>
          <el-form-item>
            <el-input v-model="keyword" placeholder="规格名称/规格值" clearable style="width: 200px" @keyup.enter="load" />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="load">搜索</el-button>
          </el-form-item>
        </el-form>
        <el-button type="primary" @click="openCreate">新增SKU</el-button>
      </div>

      <el-table :data="list" v-loading="loading" border>
        <el-table-column prop="id" label="ID" width="70" />
        <el-table-column prop="productId" label="商品ID" width="100" />
        <el-table-column prop="specName" label="规格名称" />
        <el-table-column prop="specValue" label="规格值" />
        <el-table-column prop="unit" label="单位" />
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">
              {{ row.status === 1 ? '启用' : '停用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="创建时间" />
        <el-table-column label="操作" width="180">
          <template #default="{ row }">
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

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑SKU' : '新增SKU'" width="420px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="90px">
        <el-form-item label="商品ID" prop="productId">
          <el-input-number v-model="form.productId" :min="1" style="width: 100%" />
        </el-form-item>
        <el-form-item label="规格名称" prop="specName">
          <el-input v-model="form.specName" />
        </el-form-item>
        <el-form-item label="规格值" prop="specValue">
          <el-input v-model="form.specValue" />
        </el-form-item>
        <el-form-item label="单位" prop="unit">
          <el-input v-model="form.unit" />
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
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getSkuPage, createSku, updateSku, deleteSku } from '../../api/sku'

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
const form = reactive({ productId: 1, specName: '', specValue: '', unit: '', status: 1 })

const rules = {
  productId: [{ required: true, message: '请输入商品ID', trigger: 'blur' }],
  specName: [{ required: true, message: '请输入规格名称', trigger: 'blur' }],
  unit: [{ required: true, message: '请输入单位', trigger: 'blur' }]
}

async function load() {
  loading.value = true
  try {
    const res = await getSkuPage({ pageNum: pageNum.value, pageSize: pageSize.value, keyword: keyword.value })
    list.value = res.data.records
    total.value = res.data.total
  } finally {
    loading.value = false
  }
}

function openCreate() {
  isEdit.value = false
  editingId.value = null
  Object.assign(form, { productId: 1, specName: '', specValue: '', unit: '', status: 1 })
  dialogVisible.value = true
}

async function openEdit(row) {
  isEdit.value = true
  editingId.value = row.id
  Object.assign(form, { productId: row.productId, specName: row.specName || '', specValue: row.specValue || '', unit: row.unit || '', status: row.status })
  dialogVisible.value = true
}

async function submit() {
  await formRef.value.validate()
  submitting.value = true
  try {
    if (isEdit.value) {
      await updateSku(editingId.value, { productId: form.productId, specName: form.specName, specValue: form.specValue, unit: form.unit, status: form.status })
      ElMessage.success('更新成功')
    } else {
      await createSku({ productId: form.productId, specName: form.specName, specValue: form.specValue, unit: form.unit, status: form.status })
      ElMessage.success('创建成功')
    }
    dialogVisible.value = false
    await load()
  } finally {
    submitting.value = false
  }
}

async function remove(row) {
  await ElMessageBox.confirm(`确定删除该SKU吗？`, '确认')
  await deleteSku(row.id)
  ElMessage.success('删除成功')
  await load()
}

onMounted(load)
</script>

<style scoped>
.sku-page { padding: 16px }
.toolbar { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px }
.pagination { margin-top: 16px; text-align: right }
</style>