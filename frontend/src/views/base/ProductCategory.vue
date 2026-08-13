<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryFormRef" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="分类名称" prop="name">
        <el-input v-model="queryParams.name" placeholder="请输入分类名称" clearable @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="分类编号" prop="code">
        <el-input v-model="queryParams.code" placeholder="请输入分类编号" clearable @keyup.enter="handleQuery" />
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
        <el-button type="danger" plain :icon="Delete" size="mini" :disabled="multiple" @click="handleDelete">删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain :icon="Download" size="mini" @click="handleExport">导出</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="info" plain :icon="Sort" size="mini" @click="toggleExpandAll">展开/折叠</el-button>
      </el-col>
      <el-col :span="1.5" style="float: right">
        <el-button :icon="searchIcon" size="mini" link @click="showSearch = !showSearch">
          {{ showSearch ? '隐藏搜索' : '显示搜索' }}
        </el-button>
      </el-col>
    </el-row>

    <el-table
      v-if="refreshTable"
      v-loading="loading"
      :data="categoryList"
      row-key="id"
      :default-expand-all="isExpandAll"
      :tree-props="{ children: 'children', hasChildren: 'hasChildren' }"
      border
    >
      <el-table-column label="分类名称" align="center" prop="name" :show-overflow-tooltip="true" width="160" />
      <el-table-column label="分类编号" align="center" prop="code" :show-overflow-tooltip="true" />
      <el-table-column label="备注" align="center" prop="remark" :show-overflow-tooltip="true" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template #default="scope">
          <el-button size="mini" type="primary" link :icon="Plus" @click="handleAdd(scope.row)">新增子类</el-button>
          <el-button size="mini" type="primary" link :icon="Edit" @click="handleUpdate(scope.row)">修改</el-button>
          <el-button size="mini" type="primary" link :icon="Delete" @click="handleDelete(scope.row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog :title="title" v-model="open" width="680px" append-to-body>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-row>
          <el-col :span="24">
            <el-form-item label="上级分类" prop="parentId">
              <treeselect
                v-model="form.parentId"
                :options="categoryOptions"
                :normalizer="normalizer"
                :show-count="true"
                placeholder="选择上级分类"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="分类名称" prop="name">
              <el-input v-model="form.name" placeholder="请输入分类名称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="分类排序" prop="sort">
              <el-input-number v-model="form.sort" controls-position="right" :min="0" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="备注" prop="remark">
              <el-input v-model="form.remark" type="textarea" placeholder="请输入内容" />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <template #footer>
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, nextTick } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh, Plus, Delete, Download, Sort, Edit } from '@element-plus/icons-vue'
import Treeselect from '@m-kusumgar/vue3-treeselect'
import '@m-kusumgar/vue3-treeselect/dist/vue3-treeselect.css'
import { listCategory, getCategory, getNewCategorySort, delCategory, addCategory, updateCategory } from '../../api/productCategory'
import { handleTree } from '../../utils/tree'
import { download } from '../../utils/download'

const loading = ref(false)
const ids = ref([])
const single = ref(true)
const multiple = ref(true)
const showSearch = ref(true)
const total = ref(0)
const categoryList = ref([])
const categoryOptions = ref([])
const title = ref('')
const open = ref(false)
const isExpandAll = ref(false)
const refreshTable = ref(true)
const formRef = ref(null)
const queryFormRef = ref(null)

const searchIcon = ref(Search)

const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  name: null,
  parentId: null,
  code: null,
  level: null,
  sort: null,
  isDeleted: null
})

const form = reactive({
  id: null,
  name: null,
  parentId: null,
  code: null,
  level: null,
  sort: null,
  isDeleted: null,
  createBy: null,
  createTime: null,
  updateBy: null,
  updateTime: null,
  remark: null
})

const rules = {
  name: [
    { required: true, message: '分类名称不能为空', trigger: 'blur' }
  ],
  sort: [
    { required: true, message: '分类排序不能为空', trigger: 'blur' }
  ]
}

function getList() {
  loading.value = true
  listCategory(queryParams).then(response => {
    categoryList.value = handleTree(response.data)
    loading.value = false
  }).catch(() => {
    loading.value = false
  })
}

function normalizer(node) {
  if (node.children && !node.children.length) {
    delete node.children
  }
  return {
    id: node.id,
    label: node.name,
    children: node.children
  }
}

function getTreeselect() {
  listCategory().then(response => {
    categoryOptions.value = []
    const category = { id: 0, name: '商品分类', children: [] }
    category.children = handleTree(response.data)
    categoryOptions.value.push(category)
  })
}

function getNextSortNum(id) {
  const categoryId = (id == null) ? 0 : id
  getNewCategorySort(categoryId).then(response => {
    form.sort = response.data || 0
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
    parentId: null,
    code: null,
    level: null,
    sort: null,
    isDeleted: null,
    createBy: null,
    createTime: null,
    updateBy: null,
    updateTime: null,
    remark: null
  })
  if (formRef.value) formRef.value.resetFields()
}

function handleQuery() {
  queryParams.pageNum = 1
  getList()
}

function resetQuery() {
  if (queryFormRef.value) queryFormRef.value.resetFields()
  handleQuery()
}

function handleAdd(row) {
  reset()
  getTreeselect()
  getNextSortNum()
  if (row != null && row.id) {
    form.parentId = row.id
  } else {
    form.parentId = 0
  }
  open.value = true
  title.value = '添加商品分类'
}

function toggleExpandAll() {
  refreshTable.value = false
  isExpandAll.value = !isExpandAll.value
  nextTick(() => {
    refreshTable.value = true
  })
}

function handleUpdate(row) {
  reset()
  getTreeselect()
  getCategory(row.id).then(response => {
    Object.assign(form, response.data)
    open.value = true
    title.value = '修改商品分类'
  })
}

function submitForm() {
  formRef.value.validate(valid => {
    if (valid) {
      if (form.id != null) {
        updateCategory(form).then(() => {
          ElMessage.success('修改成功')
          open.value = false
          getList()
        })
      } else {
        addCategory(form).then(() => {
          ElMessage.success('新增成功')
          open.value = false
          getList()
        })
      }
    }
  })
}

function handleDelete(row) {
  const ids = row.id
  const categoryName = row.name || null
  ElMessageBox.confirm('是否确认删除商品分类名为【' + categoryName + '】的数据项及其子项？')
    .then(() => {
      return delCategory(ids)
    })
    .then(() => {
      getList()
      ElMessage.success('删除成功')
    })
    .catch(() => {})
}

function handleExport() {
  download('product/category/export', {
    ...queryParams
  }, `category_${new Date().getTime()}.xlsx`)
}

function handleParentIdChanged(val) {
  if (val == null) return
  if (form.id == null) {
    getNextSortNum(val)
  }
}

// 监听 parentId 变化，新增时上级分类改变则重新获取排序号
import { watch } from 'vue'
watch(() => form.parentId, handleParentIdChanged)

getList()
</script>

<style scoped>
.app-container {
  padding: 16px;
}
.mb8 {
  margin-bottom: 8px;
}
</style>