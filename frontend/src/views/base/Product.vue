<template>
  <div class="product-page">
    <el-card>
      <div class="toolbar">
        <el-form inline>
          <el-form-item>
            <el-input v-model="keyword" placeholder="品名/英文缩写" clearable style="width: 200px" @keyup.enter="load" />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="load">搜索</el-button>
          </el-form-item>
        </el-form>
        <el-button type="primary" @click="openCreate">新增商品</el-button>
      </div>

      <el-table :data="list" v-loading="loading" border>
        <el-table-column prop="id" label="ID" width="70" />
        <el-table-column prop="name" label="商品名称" />
        <el-table-column prop="enShort" label="英文缩写" />
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">
              {{ row.status === 1 ? '启用' : '停用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="创建时间" />
        <el-table-column label="操作" width="200">
          <template #default="{ row }">
            <el-button link type="primary" @click="openEdit(row)">编辑</el-button>
            <el-button link type="warning" @click="openAliases(row)">别名</el-button>
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

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑商品' : '新增商品'" width="420px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="90px">
        <el-form-item label="商品名称" prop="name">
          <el-input v-model="form.name" :disabled="isEdit" />
        </el-form-item>
        <el-form-item label="英文缩写" prop="enShort">
          <el-input v-model="form.enShort" />
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

    <el-dialog v-model="aliasDialogVisible" title="商品别名" width="500px">
      <el-button type="primary" size="small" @click="openAddAlias">添加别名</el-button>
      <el-table :data="aliases" border style="margin-top: 12px">
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="alias" label="别名" />
        <el-table-column prop="enShort" label="英文缩写" />
        <el-table-column label="操作" width="100">
          <template #default="{ row }">
            <el-button link type="danger" @click="removeAlias(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-dialog>

    <el-dialog v-model="aliasFormVisible" title="添加别名" width="400px">
      <el-form ref="aliasFormRef" :model="aliasForm" :rules="aliasRules" label-width="80px">
        <el-form-item label="别名" prop="alias">
          <el-input v-model="aliasForm.alias" />
        </el-form-item>
        <el-form-item label="英文缩写" prop="enShort">
          <el-input v-model="aliasForm.enShort" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="aliasFormVisible = false">取消</el-button>
        <el-button type="primary" :loading="aliasSubmitting" @click="submitAlias">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getProductPage, createProduct, updateProduct, deleteProduct, getProductAliases, createAlias, deleteAlias } from '../../api/product'

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
const form = reactive({ name: '', enShort: '', status: 1 })

const rules = {
  name: [{ required: true, message: '请输入商品名称', trigger: 'blur' }]
}

// 别名
const aliasDialogVisible = ref(false)
const aliasFormVisible = ref(false)
const aliasSubmitting = ref(false)
const aliases = ref([])
const currentProductId = ref(null)
const aliasFormRef = ref(null)
const aliasForm = reactive({ alias: '', enShort: '' })
const aliasRules = {
  alias: [{ required: true, message: '请输入别名', trigger: 'blur' }]
}

async function load() {
  loading.value = true
  try {
    const res = await getProductPage({ pageNum: pageNum.value, pageSize: pageSize.value, keyword: keyword.value })
    list.value = res.data.records
    total.value = res.data.total
  } finally {
    loading.value = false
  }
}

function openCreate() {
  isEdit.value = false
  editingId.value = null
  Object.assign(form, { name: '', enShort: '', status: 1 })
  dialogVisible.value = true
}

async function openEdit(row) {
  isEdit.value = true
  editingId.value = row.id
  Object.assign(form, { name: row.name, enShort: row.enShort || '', status: row.status })
  dialogVisible.value = true
}

async function submit() {
  await formRef.value.validate()
  submitting.value = true
  try {
    if (isEdit.value) {
      await updateProduct(editingId.value, { name: form.name, enShort: form.enShort, status: form.status })
      ElMessage.success('更新成功')
    } else {
      await createProduct({ name: form.name, enShort: form.enShort, status: form.status })
      ElMessage.success('创建成功')
    }
    dialogVisible.value = false
    await load()
  } finally {
    submitting.value = false
  }
}

async function remove(row) {
  await ElMessageBox.confirm(`确定删除商品"${row.name}"吗？`, '确认')
  await deleteProduct(row.id)
  ElMessage.success('删除成功')
  await load()
}

async function openAliases(row) {
  currentProductId.value = row.id
  const res = await getProductAliases(row.id)
  aliases.value = res.data
  aliasDialogVisible.value = true
}

function openAddAlias() {
  Object.assign(aliasForm, { alias: '', enShort: '' })
  aliasFormVisible.value = true
}

async function submitAlias() {
  await aliasFormRef.value.validate()
  aliasSubmitting.value = true
  try {
    await createAlias({ productId: currentProductId.value, alias: aliasForm.alias, enShort: aliasForm.enShort })
    ElMessage.success('添加成功')
    aliasFormVisible.value = false
    const res = await getProductAliases(currentProductId.value)
    aliases.value = res.data
  } finally {
    aliasSubmitting.value = false
  }
}

async function removeAlias(row) {
  await ElMessageBox.confirm(`确定删除该别名吗？`, '确认')
  await deleteAlias(row.id)
  ElMessage.success('删除成功')
  const res = await getProductAliases(currentProductId.value)
  aliases.value = res.data
}

onMounted(load)
</script>

<style scoped>
.product-page { padding: 16px }
.toolbar { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px }
.pagination { margin-top: 16px; text-align: right }
</style>