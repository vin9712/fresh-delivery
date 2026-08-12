<template>
  <div class="customer-page">
    <el-card>
      <div class="toolbar">
        <el-form inline>
          <el-form-item>
            <el-input v-model="keyword" placeholder="客户名称/联系人/电话" clearable style="width: 220px" @keyup.enter="load" />
          </el-form-item>
          <el-form-item>
            <el-select v-model="categoryId" placeholder="分类" clearable style="width: 140px">
              <el-option v-for="c in categories" :key="c.id" :label="c.name" :value="c.id" />
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="load">搜索</el-button>
          </el-form-item>
        </el-form>
        <el-button type="primary" @click="openCreate">新增客户</el-button>
      </div>

      <el-table :data="list" v-loading="loading" border>
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="name" label="客户名称" />
        <el-table-column prop="contactPerson" label="联系人" />
        <el-table-column prop="phone" label="联系电话" />
        <el-table-column prop="address" label="地址" show-overflow-tooltip />
        <el-table-column prop="settlementCycle" label="结算周期" width="100">
          <template #default="{ row }">
            <span>{{ row.settlementCycle === 1 ? '周结' : row.settlementCycle === 2 ? '月结' : '-' }}</span>
          </template>
        </el-table-column>
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

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑客户' : '新增客户'" width="500px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="90px">
        <el-form-item label="客户名称" prop="name">
          <el-input v-model="form.name" />
        </el-form-item>
        <el-form-item label="分类" prop="categoryId">
          <el-select v-model="form.categoryId" style="width: 100%">
            <el-option v-for="c in categories" :key="c.id" :label="c.name" :value="c.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="联系人" prop="contactPerson">
          <el-input v-model="form.contactPerson" />
        </el-form-item>
        <el-form-item label="联系电话" prop="phone">
          <el-input v-model="form.phone" />
        </el-form-item>
        <el-form-item label="地址" prop="address">
          <el-input v-model="form.address" type="textarea" />
        </el-form-item>
        <el-form-item label="结算周期">
          <el-radio-group v-model="form.settlementCycle">
            <el-radio :label="1">周结</el-radio>
            <el-radio :label="2">月结</el-radio>
          </el-radio-group>
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
import { getCustomerPage, createCustomer, updateCustomer, deleteCustomer } from '../../api/customer'
import { getCategoryList } from '../../api/customerCategory'

const list = ref([])
const loading = ref(false)
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(10)
const keyword = ref('')
const categoryId = ref(null)
const categories = ref([])

const dialogVisible = ref(false)
const isEdit = ref(false)
const editingId = ref(null)
const submitting = ref(false)
const formRef = ref(null)
const form = reactive({ name: '', categoryId: null, contactPerson: '', phone: '', address: '', settlementCycle: null, status: 1 })

const rules = {
  name: [{ required: true, message: '请输入客户名称', trigger: 'blur' }]
}

async function load() {
  loading.value = true
  try {
    const res = await getCustomerPage({ pageNum: pageNum.value, pageSize: pageSize.value, keyword: keyword.value, categoryId: categoryId.value })
    list.value = res.data.records
    total.value = res.data.total
  } finally {
    loading.value = false
  }
}

async function loadCategories() {
  const res = await getCategoryList()
  categories.value = res.data
}

function openCreate() {
  isEdit.value = false
  editingId.value = null
  Object.assign(form, { name: '', categoryId: null, contactPerson: '', phone: '', address: '', settlementCycle: null, status: 1 })
  dialogVisible.value = true
}

async function openEdit(row) {
  isEdit.value = true
  editingId.value = row.id
  Object.assign(form, { name: row.name, categoryId: row.categoryId, contactPerson: row.contactPerson || '', phone: row.phone || '', address: row.address || '', settlementCycle: row.settlementCycle, status: row.status })
  dialogVisible.value = true
}

async function submit() {
  await formRef.value.validate()
  submitting.value = true
  try {
    const data = { name: form.name, categoryId: form.categoryId, contactPerson: form.contactPerson, phone: form.phone, address: form.address, settlementCycle: form.settlementCycle, status: form.status }
    if (isEdit.value) {
      await updateCustomer(editingId.value, data)
      ElMessage.success('更新成功')
    } else {
      await createCustomer(data)
      ElMessage.success('创建成功')
    }
    dialogVisible.value = false
    await load()
  } finally {
    submitting.value = false
  }
}

async function remove(row) {
  await ElMessageBox.confirm(`确定删除客户"${row.name}"吗？`, '确认')
  await deleteCustomer(row.id)
  ElMessage.success('删除成功')
  await load()
}

onMounted(() => { load(); loadCategories() })
</script>

<style scoped>
.customer-page { padding: 16px }
.toolbar { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px }
.pagination { margin-top: 16px; text-align: right }
</style>