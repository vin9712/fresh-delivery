<template>
  <div class="app-container">
    <el-form
      :model="queryParams"
      :rules="queryFormRules"
      ref="queryFormRef"
      size="small"
      :inline="true"
      v-show="showSearch"
      label-width="80px"
    >
      <el-form-item label="当前客户" prop="customerId">
        <el-select v-model="queryParams.customerId" filterable placeholder="请选择客户" @change="handleQuery">
          <el-option
            v-for="item in customerOptions"
            :key="item.id"
            :label="item.alias || item.name"
            :value="item.id"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="部门名称" prop="name">
        <el-input
          v-model="queryParams.name"
          placeholder="请输入部门名称"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="是否有效" prop="status">
        <el-select v-model="queryParams.status" placeholder="请选择是否有效" clearable>
          <el-option label="有效" :value="1" />
          <el-option label="无效" :value="0" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" :icon="Search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button :icon="Refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain :icon="Plus" size="mini" @click="handleAdd">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain :icon="Edit" size="mini" :disabled="single" @click="handleUpdate">修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain :icon="Delete" size="mini" :disabled="multiple" @click="handleDelete">删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain :icon="Download" size="mini" @click="handleExport">导出</el-button>
      </el-col>
      <el-col :span="1.5" style="float: right">
        <el-button :icon="searchIcon" size="mini" link @click="showSearch = !showSearch">
          {{ showSearch ? '隐藏搜索' : '显示搜索' }}
        </el-button>
      </el-col>
    </el-row>

    <el-table
      v-loading="loading"
      :data="deliveryPointList"
      @selection-change="handleSelectionChange"
    >
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="编号" align="center" prop="code" />
      <el-table-column label="客户部门" align="center" prop="name" />
      <el-table-column label="是否有效" align="center" prop="status">
        <template #default="scope">
          <span>{{ scope.row.status === 1 ? '有效' : '无效' }}</span>
        </template>
      </el-table-column>
      <el-table-column label="备注" align="center" prop="remark" :show-overflow-tooltip="true" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template #default="scope">
          <el-button size="mini" type="primary" link :icon="Edit" @click="handleUpdate(scope.row)">修改</el-button>
          <el-button size="mini" type="primary" link :icon="Delete" @click="handleDelete(scope.row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination
      v-show="total > 0"
      :total="total"
      :page-sizes="[10, 20, 50]"
      :current-page="queryParams.pageNum"
      :page-size="queryParams.pageSize"
      layout="total, sizes, prev, pager, next, jumper"
      @size-change="handleSizeChange"
      @current-change="handleCurrentChange"
    />

    <el-dialog :title="title" v-model="open" width="500px" append-to-body>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="当前客户" prop="customerId">
          <el-select v-model="form.customerId" disabled>
            <el-option
              v-for="item in customerOptions"
              :key="item.id"
              :label="item.alias || item.name"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="部门名称" prop="name">
          <el-input
            v-model="form.name"
            @input="handleUpdateMnemonicCode"
            placeholder="请输入部门名称"
          />
        </el-form-item>
        <el-form-item label="助记码" prop="mnemonicCode">
          <el-input
            v-model="form.mnemonicCode"
            placeholder="请输入助记码"
            :disabled="form.id != null"
          />
        </el-form-item>
        <el-form-item label="是否有效" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio :label="1">有效</el-radio>
            <el-radio :label="0">无效</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" placeholder="请输入内容" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh, Plus, Edit, Delete, Download } from '@element-plus/icons-vue'
import { listDeliveryPoint, getDeliveryPointDetail, createDeliveryPoint, updateDeliveryPoint, deleteDeliveryPoint } from '../../api/deliveryPoint'
import { getAllCustomers } from '../../api/customer'
import { download } from '../../utils/download'
import { pinyin } from 'pinyin-pro'

const route = useRoute()

const loading = ref(true)
const ids = ref([])
const deptCodes = ref([])
const single = ref(true)
const multiple = ref(true)
const showSearch = ref(true)
const total = ref(0)
const customerOptions = ref([])
const deliveryPointList = ref([])
const title = ref('')
const open = ref(false)
const formRef = ref(null)
const queryFormRef = ref(null)
const searchIcon = ref(Search)

const defaultCustomerId = ref(null)

const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  customerId: null,
  name: null,
  status: null
})

const queryFormRules = {
  customerId: [
    { required: true, message: '当前客户不能为空', trigger: 'change' }
  ]
}

const form = reactive({
  id: null,
  customerId: null,
  name: null,
  mnemonicCode: null,
  status: 1,
  remark: null
})

const rules = {
  customerId: [
    { required: true, message: '当前客户不能为空', trigger: 'change' }
  ],
  name: [
    { required: true, message: '部门名称不能为空', trigger: 'blur' }
  ],
  mnemonicCode: [
    { required: true, message: '助记码不能为空', trigger: 'blur' }
  ],
  status: [
    { required: true, message: '是否有效不能为空', trigger: 'change' }
  ]
}

function getPageList() {
  loading.value = true
  listDeliveryPoint(queryParams).then(response => {
    deliveryPointList.value = response.data.records || []
    total.value = response.data.total || 0
    loading.value = false
  }).catch(() => {
    loading.value = false
  })
}

function getCustomerList() {
  getAllCustomers().then(response => {
    customerOptions.value = response.data || []
  })
}

function cancel() {
  open.value = false
  reset()
}

function reset() {
  Object.assign(form, {
    id: null,
    customerId: defaultCustomerId.value,
    name: null,
    mnemonicCode: null,
    status: 1,
    remark: null
  })
  if (formRef.value) formRef.value.resetFields()
}

function handleQuery() {
  queryParams.pageNum = 1
  getPageList()
}

function resetQuery() {
  if (queryFormRef.value) queryFormRef.value.resetFields()
  queryParams.customerId = defaultCustomerId.value
  queryParams.status = null
  queryParams.name = null
  handleQuery()
}

function handleSelectionChange(selection) {
  ids.value = selection.map(item => item.id)
  deptCodes.value = selection.map(item => item.code)
  single.value = selection.length !== 1
  multiple.value = !selection.length
}

function handleSizeChange(val) {
  queryParams.pageSize = val
  queryParams.pageNum = 1
  getPageList()
}

function handleCurrentChange(val) {
  queryParams.pageNum = val
  getPageList()
}

function handleAdd() {
  reset()
  open.value = true
  title.value = '添加配送点'
}

function handleUpdate(row) {
  reset()
  const id = row.id || ids.value[0]
  getDeliveryPointDetail(id).then(response => {
    Object.assign(form, response.data)
    open.value = true
    title.value = '修改配送点'
  })
}

function submitForm() {
  formRef.value.validate(valid => {
    if (valid) {
      if (form.id != null) {
        updateDeliveryPoint(form.id, form).then(() => {
          ElMessage.success('修改成功')
          open.value = false
          getPageList()
        })
      } else {
        createDeliveryPoint(form).then(() => {
          ElMessage.success('新增成功')
          open.value = false
          getPageList()
        })
      }
    }
  })
}

function handleDelete(row) {
  const deleteIds = row.id || ids.value
  const codes = row.code || deptCodes.value
  ElMessageBox.confirm('是否确认删除配送点编号为"' + codes + '"的数据项？')
    .then(() => {
      if (Array.isArray(deleteIds)) {
        return Promise.all(deleteIds.map(id => deleteDeliveryPoint(id)))
      } else {
        return deleteDeliveryPoint(deleteIds)
      }
    })
    .then(() => {
      getPageList()
      ElMessage.success('删除成功')
    })
    .catch(() => {})
}

function handleExport() {
  download('base/delivery-point/export', {
    ...queryParams
  }, `deliveryPoint_${new Date().getTime()}.xlsx`)
}

// 拼音首字母助记码自动生成
function handleUpdateMnemonicCode() {
  const value = form.name
  if (!value) {
    form.mnemonicCode = ''
    return
  }
  form.mnemonicCode = pinyin(value, {
    pattern: 'first',
    toneType: 'none',
    type: 'array'
  }).join('').toUpperCase()
}

onMounted(() => {
  const cid = route.query.customerId
  if (cid) {
    defaultCustomerId.value = Number(cid)
    queryParams.customerId = Number(cid)
  }
  getCustomerList()
  getPageList()
})
</script>

<style scoped>
.app-container {
  padding: 16px;
}
.mb8 {
  margin-bottom: 8px;
}
</style>