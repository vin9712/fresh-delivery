<template>
  <div class="app-container">
    <el-form
      :model="queryParams"
      ref="queryFormRef"
      size="small"
      :inline="true"
      v-show="showSearch"
      label-width="68px"
    >
      <el-form-item label="客户名称" prop="name">
        <el-input
          v-model="queryParams.name"
          placeholder="请输入客户名称"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="是否有效" prop="valid">
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
        <el-button type="info" plain :icon="Upload" size="mini" @click="handleImport">导入</el-button>
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
      :data="customerList"
      @selection-change="handleSelectionChange"
    >
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="客户编号" align="center" prop="id" />
      <el-table-column label="客户名称" align="center" prop="name">
        <template #default="scope">
          <router-link
            title="查看配送点列表"
            :to="{ path: '/delivery-point', query: { customerId: scope.row.id } }"
            class="link-type"
          >
            <span>{{ scope.row.name }}</span>
          </router-link>
        </template>
      </el-table-column>
      <el-table-column label="客户别名" align="center" prop="alias" />
      <el-table-column label="客户类型" align="center" prop="type">
        <template #default="scope">
          <span>{{ scope.row.type == null ? '-' : scope.row.type === 1 ? '批发' : scope.row.type === 2 ? '零售' : scope.row.type === 3 ? '团餐' : '其他' }}</span>
        </template>
      </el-table-column>
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
        <el-form-item label="客户类别" prop="type">
          <el-select v-model="form.type" placeholder="请选择客户类别" clearable style="width: 100%">
            <el-option label="批发" :value="1" />
            <el-option label="零售" :value="2" />
            <el-option label="团餐" :value="3" />
            <el-option label="其他" :value="4" />
          </el-select>
        </el-form-item>
        <el-form-item label="客户名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入客户名称" />
        </el-form-item>
        <el-form-item label="客户别名" prop="alias">
          <el-input v-model="form.alias" placeholder="请输入客户别名" />
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="form.phone" placeholder="请输入手机号" />
        </el-form-item>
        <el-form-item label="客户地址" prop="address">
          <el-input v-model="form.address" placeholder="请输入客户地址" />
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

    <el-dialog
      :title="upload.title"
      v-model="upload.open"
      width="400px"
      append-to-body
    >
      <el-upload
        ref="uploadRef"
        :limit="1"
        accept=".xlsx, .xls"
        :headers="upload.headers"
        :action="upload.url"
        :disabled="upload.isUploading"
        :auto-upload="false"
        drag
      >
        <div class="el-icon--upload">
          <el-icon><UploadFilled /></el-icon>
        </div>
        <div class="el-upload__text">将文件拖到此处，或<em>点击上传</em></div>
        <template #tip>
          <div class="el-upload__tip text-center">
            <span>仅允许导入xls、xlsx格式文件。</span>
          </div>
        </template>
      </el-upload>
      <template #footer>
        <el-button type="primary" @click="submitFileForm">确 定</el-button>
        <el-button @click="upload.open = false">取 消</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh, Plus, Edit, Delete, Upload, Download, UploadFilled } from '@element-plus/icons-vue'
import { listCustomer, getCustomerDetail, createCustomer, updateCustomer, deleteCustomer } from '../../api/customer'
import { getToken } from '../../utils/auth'
import { download } from '../../utils/download'

const loading = ref(true)
const ids = ref([])
const single = ref(true)
const multiple = ref(true)
const showSearch = ref(true)
const total = ref(0)
const customerList = ref([])
const title = ref('')
const open = ref(false)
const formRef = ref(null)
const queryFormRef = ref(null)
const uploadRef = ref(null)

const searchIcon = ref(Search)

const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  name: null,
  status: null
})

const form = reactive({
  id: null,
  name: null,
  alias: null,
  type: null,
  phone: null,
  address: null,
  status: 1,
  remark: null
})

const rules = {
  name: [
    { required: true, message: '客户名称不能为空', trigger: 'blur' }
  ],
  type: [
    { required: true, message: '客户类别不能为空', trigger: 'change' }
  ],
  status: [
    { required: true, message: '是否有效不能为空', trigger: 'change' }
  ]
}

const upload = reactive({
  open: false,
  title: '',
  isUploading: false,
  headers: { Authorization: 'Bearer ' + getToken() },
  url: import.meta.env.VITE_APP_BASE_API || '/api/base/customer/importData'
})

function getPageList() {
  loading.value = true
  listCustomer(queryParams).then(response => {
    customerList.value = response.data.records || []
    total.value = response.data.total || 0
    loading.value = false
  }).catch(() => {
    loading.value = false
  })
}

function cancel() {
  open.value = false
  reset()
}

function reset() {
  Object.assign(form, {
    id: null,
    name: null,
    alias: null,
    type: null,
    phone: null,
    address: null,
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
  queryParams.status = null
  handleQuery()
}

function handleSelectionChange(selection) {
  ids.value = selection.map(item => item.id)
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
  title.value = '添加客户'
}

function handleUpdate(row) {
  reset()
  const id = row.id || ids.value
  getCustomerDetail(id).then(response => {
    Object.assign(form, response.data)
    open.value = true
    title.value = '修改客户'
  })
}

function submitForm() {
  formRef.value.validate(valid => {
    if (valid) {
      if (form.id != null) {
        updateCustomer(form.id, form).then(() => {
          ElMessage.success('修改成功')
          open.value = false
          getPageList()
        })
      } else {
        createCustomer(form).then(() => {
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
  const name = row.name || ('编号为' + deleteIds)
  ElMessageBox.confirm('是否确认删除客户编号为"' + deleteIds + '"的数据项？')
    .then(() => {
      if (Array.isArray(deleteIds)) {
        return Promise.all(deleteIds.map(id => deleteCustomer(id)))
      } else {
        return deleteCustomer(deleteIds)
      }
    })
    .then(() => {
      getPageList()
      ElMessage.success('删除成功')
    })
    .catch(() => {})
}

function handleExport() {
  download('base/customer/export', {
    ...queryParams
  }, `customer_${new Date().getTime()}.xlsx`)
}

function handleImport() {
  upload.title = '客户导入'
  upload.open = true
}

function submitFileForm() {
  if (uploadRef.value) {
    upload.isUploading = true
    uploadRef.value.submit()
    setTimeout(() => { upload.isUploading = false }, 2000)
  }
}

getPageList()
</script>

<style scoped>
.app-container {
  padding: 16px;
}
.mb8 {
  margin-bottom: 8px;
}
.link-type {
  color: #409eff;
  text-decoration: none;
}
.link-type:hover {
  color: #66b1ff;
}
</style>