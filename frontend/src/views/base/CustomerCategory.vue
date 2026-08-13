<template>
  <div class="app-container">
    <el-form
      :model="queryParams"
      ref="queryFormRef"
      size="small"
      :inline="true"
      v-show="showSearch"
      label-width="80px"
    >
      <el-form-item label="分类名称" prop="name">
        <el-input
          v-model="queryParams.name"
          placeholder="请输入分类名称"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="请选择状态" clearable>
          <el-option label="启用" :value="1" />
          <el-option label="停用" :value="0" />
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
      <el-col :span="1.5" style="float: right">
        <el-button :icon="searchIcon" size="mini" link @click="showSearch = !showSearch">
          {{ showSearch ? '隐藏搜索' : '显示搜索' }}
        </el-button>
      </el-col>
    </el-row>

    <el-table
      v-loading="loading"
      :data="categoryList"
      @selection-change="handleSelectionChange"
    >
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="分类编号" align="center" prop="id" />
      <el-table-column label="分类名称" align="center" prop="name" />
      <el-table-column label="状态" align="center" prop="status">
        <template #default="scope">
          <el-tag :type="scope.row.status === 1 ? 'success' : 'danger'" size="small">
            {{ scope.row.status === 1 ? '启用' : '停用' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template #default="scope">
          <el-button size="mini" type="primary" link :icon="Edit" @click="handleUpdate(scope.row)">修改</el-button>
          <el-button size="mini" type="primary" link :icon="Delete" @click="handleDelete(scope.row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog :title="title" v-model="open" width="500px" append-to-body>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="分类名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入分类名称" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio :label="1">启用</el-radio>
            <el-radio :label="0">停用</el-radio>
          </el-radio-group>
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
import { ref, reactive } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh, Plus, Edit, Delete } from '@element-plus/icons-vue'
import { getCategoryList, createCategory, updateCategory, deleteCategory } from '../../api/customerCategory'

const loading = ref(true)
const ids = ref([])
const single = ref(true)
const multiple = ref(true)
const showSearch = ref(true)
const categoryList = ref([])
const title = ref('')
const open = ref(false)
const formRef = ref(null)
const queryFormRef = ref(null)
const searchIcon = ref(Search)

const queryParams = reactive({
  name: null,
  status: null
})

const form = reactive({
  id: null,
  name: null,
  status: 1
})

const rules = {
  name: [
    { required: true, message: '分类名称不能为空', trigger: 'blur' }
  ],
  status: [
    { required: true, message: '状态不能为空', trigger: 'change' }
  ]
}

async function getPageList() {
  loading.value = true
  try {
    const response = await getCategoryList(queryParams)
    categoryList.value = response.data || []
    // list() 只返回启用的分类；这里按查询条件再次过滤
    if (queryParams.name) {
      categoryList.value = categoryList.value.filter(
        item => item.name && item.name.includes(queryParams.name)
      )
    }
    if (queryParams.status != null) {
      categoryList.value = categoryList.value.filter(
        item => item.status === queryParams.status
      )
    }
  } finally {
    loading.value = false
  }
}

function cancel() {
  open.value = false
  reset()
}

function reset() {
  Object.assign(form, {
    id: null,
    name: null,
    status: 1
  })
  if (formRef.value) formRef.value.resetFields()
}

function handleQuery() {
  getPageList()
}

function resetQuery() {
  if (queryFormRef.value) queryFormRef.value.resetFields()
  queryParams.status = null
  queryParams.name = null
  getPageList()
}

function handleSelectionChange(selection) {
  ids.value = selection.map(item => item.id)
  single.value = selection.length !== 1
  multiple.value = !selection.length
}

function handleAdd() {
  reset()
  open.value = true
  title.value = '添加分类'
}

function handleUpdate(row) {
  reset()
  const id = row.id || ids.value[0]
  const found = categoryList.value.find(item => item.id === id)
  if (found) {
    Object.assign(form, found)
  }
  open.value = true
  title.value = '修改分类'
}

function submitForm() {
  formRef.value.validate(valid => {
    if (valid) {
      if (form.id != null) {
        updateCategory(form.id, form).then(() => {
          ElMessage.success('修改成功')
          open.value = false
          getPageList()
        })
      } else {
        createCategory(form).then(() => {
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
  const deleteNames = Array.isArray(deleteIds)
    ? ids.value.map(id => categoryList.value.find(item => item.id === id)?.name).filter(Boolean).join('、') || '所选'
    : row.name || '编号为' + deleteIds
  ElMessageBox.confirm('是否确认删除分类"' + deleteNames + '"的数据项？')
    .then(() => {
      if (Array.isArray(deleteIds)) {
        return Promise.all(deleteIds.map(id => deleteCategory(id)))
      } else {
        return deleteCategory(deleteIds)
      }
    })
    .then(() => {
      getPageList()
      ElMessage.success('删除成功')
    })
    .catch(() => {})
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
</style>