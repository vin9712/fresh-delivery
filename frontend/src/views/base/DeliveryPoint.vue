<template>
  <div class="delivery-point-page">
    <el-card>
      <div class="toolbar">
        <el-form inline>
          <el-form-item>
            <el-input v-model="keyword" placeholder="名称/联系人/电话" clearable style="width: 220px" @keyup.enter="load" />
          </el-form-item>
          <el-form-item>
            <el-select v-model="customerId" placeholder="选择客户" clearable style="width: 160px" @change="load">
              <el-option v-for="c in customers" :key="c.id" :label="c.name" :value="c.id" />
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="load">搜索</el-button>
          </el-form-item>
        </el-form>
        <el-button type="primary" @click="openCreate">新增配送点</el-button>
      </div>

      <el-table :data="list" v-loading="loading" border>
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="customerId" label="客户ID" width="90" />
        <el-table-column prop="name" label="配送点名称" />
        <el-table-column prop="address" label="配送地址" show-overflow-tooltip />
        <el-table-column prop="contactPerson" label="联系人" />
        <el-table-column prop="phone" label="联系电话" />
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

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑配送点' : '新增配送点'" width="480px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="客户" prop="customerId">
          <el-select v-model="form.customerId" style="width: 100%" filterable>
            <el-option v-for="c in customers" :key="c.id" :label="c.name" :value="c.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="名称" prop="name">
          <el-input v-model="form.name" />
        </el-form-item>
        <el-form-item label="地址" prop="address">
          <el-input v-model="form.address" type="textarea" />
        </el-form-item>
        <el-form-item label="联系人">
          <el-input v-model="form.contactPerson" />
        </el-form-item>
        <el-form-item label="联系电话">
          <el-input v-model="form.phone" />
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
import { getDeliveryPointPage, createDeliveryPoint, updateDeliveryPoint, deleteDeliveryPoint } from '../../api/deliveryPoint'
import { getCustomerPage } from '../../api/customer'

const list = ref([])
const loading = ref(false)
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(10)
const keyword = ref('')
const customerId = ref(null)
const customers = ref([])

const dialogVisible = ref(false)
const isEdit = ref(false)
const editingId = ref(null)
const submitting = ref(false)
const formRef = ref(null)
const form = reactive({ customerId: null, name: '', address: '', contactPerson: '', phone: '', status: 1 })

const rules = {
  customerId: [{ required: true, message: '请选择客户', trigger: 'change' }],
  name: [{ required: true, message: '请输入名称', trigger: 'blur' }]
}

async function load() {
  loading.value = true
  try {
    const res = await getDeliveryPointPage({ pageNum: pageNum.value, pageSize: pageSize.value, keyword: keyword.value, customerId: customerId.value })
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
  Object.assign(form, { customerId: null, name: '', address: '', contactPerson: '', phone: '', status: 1 })
  dialogVisible.value = true
}

async function openEdit(row) {
  isEdit.value = true
  editingId.value = row.id
  Object.assign(form, { customerId: row.customerId, name: row.name, address: row.address || '', contactPerson: row.contactPerson || '', phone: row.phone || '', status: row.status })
  dialogVisible.value = true
}

async function submit() {
  await formRef.value.validate()
  submitting.value = true
  try {
    const data = { customerId: form.customerId, name: form.name, address: form.address, contactPerson: form.contactPerson, phone: form.phone, status: form.status }
    if (isEdit.value) {
      await updateDeliveryPoint(editingId.value, data)
      ElMessage.success('更新成功')
    } else {
      await createDeliveryPoint(data)
      ElMessage.success('创建成功')
    }
    dialogVisible.value = false
    await load()
  } finally {
    submitting.value = false
  }
}

async function remove(row) {
  await ElMessageBox.confirm(`确定删除配送点"${row.name}"吗？`, '确认')
  await deleteDeliveryPoint(row.id)
  ElMessage.success('删除成功')
  await load()
}

onMounted(() => { load(); loadCustomers() })
</script>

<style scoped>
.delivery-point-page { padding: 16px }
.toolbar { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px }
.pagination { margin-top: 16px; text-align: right }
</style>