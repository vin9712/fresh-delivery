<template>
  <div class="supplier-page">
    <el-card>
      <div class="toolbar">
        <el-form inline>
          <el-form-item>
            <el-input v-model="keyword" placeholder="供应商名称/联系人" clearable style="width: 180px" @keyup.enter="load" />
          </el-form-item>
          <el-form-item>
            <el-select v-model="status" placeholder="状态" clearable style="width: 120px">
              <el-option label="停用" :value="0" />
              <el-option label="启用" :value="1" />
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="load">搜索</el-button>
          </el-form-item>
        </el-form>
        <el-button type="primary" @click="openCreate">新增供应商</el-button>
      </div>

      <el-table :data="list" v-loading="loading" border>
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="name" label="供应商名称" />
        <el-table-column prop="contactPerson" label="联系人" />
        <el-table-column prop="phone" label="电话" />
        <el-table-column prop="address" label="地址" show-overflow-tooltip />
        <el-table-column prop="isDefault" label="默认" width="70">
          <template #default="{ row }">
            <el-tag :type="row.isDefault === 1 ? 'success' : ''">{{ row.isDefault === 1 ? '是' : '否' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="70">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'">{{ row.status === 1 ? '启用' : '停用' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="160">
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

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑供应商' : '新增供应商'" width="460px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="名称" prop="name">
          <el-input v-model="form.name" />
        </el-form-item>
        <el-form-item label="联系人">
          <el-input v-model="form.contactPerson" />
        </el-form-item>
        <el-form-item label="电话">
          <el-input v-model="form.phone" />
        </el-form-item>
        <el-form-item label="地址">
          <el-input v-model="form.address" type="textarea" />
        </el-form-item>
        <el-form-item label="默认">
          <el-switch v-model="form.isDefault" :active-value="1" :inactive-value="0" />
        </el-form-item>
        <el-form-item label="状态">
          <el-switch v-model="form.status" :active-value="1" :inactive-value="0" />
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
import { getSupplierPage, createSupplier, updateSupplier, deleteSupplier } from '../../api/supplier'

const list = ref([])
const loading = ref(false)
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(10)
const keyword = ref('')
const status = ref(null)

const dialogVisible = ref(false)
const isEdit = ref(false)
const editingId = ref(null)
const submitting = ref(false)
const formRef = ref(null)
const form = reactive({ name: '', contactPerson: '', phone: '', address: '', isDefault: 0, status: 1 })
const rules = {
  name: [{ required: true, message: '请输入供应商名称', trigger: 'blur' }]
}

async function load() {
  loading.value = true
  try {
    const res = await getSupplierPage({ pageNum: pageNum.value, pageSize: pageSize.value, keyword: keyword.value, status: status.value })
    list.value = res.data.records
    total.value = res.data.total
  } finally {
    loading.value = false
  }
}

function openCreate() {
  isEdit.value = false
  editingId.value = null
  Object.assign(form, { name: '', contactPerson: '', phone: '', address: '', isDefault: 0, status: 1 })
  dialogVisible.value = true
}

function openEdit(row) {
  isEdit.value = true
  editingId.value = row.id
  Object.assign(form, row)
  dialogVisible.value = true
}

async function submit() {
  await formRef.value.validate()
  submitting.value = true
  try {
    if (isEdit.value) {
      await updateSupplier(editingId.value, form)
      ElMessage.success('更新成功')
    } else {
      await createSupplier(form)
      ElMessage.success('创建成功')
    }
    dialogVisible.value = false
    await load()
  } finally {
    submitting.value = false
  }
}

async function remove(row) {
  await ElMessageBox.confirm(`确定删除供应商"${row.name}"吗？`, '确认')
  await deleteSupplier(row.id)
  ElMessage.success('删除成功')
  await load()
}

onMounted(load)
</script>

<style scoped>
.supplier-page { padding: 16px }
.toolbar { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px }
.pagination { margin-top: 16px; text-align: right }
</style>