<template>
  <div class="role-page">
    <el-card>
      <div class="toolbar">
        <div></div>
        <el-button type="primary" @click="openCreate">新增角色</el-button>
      </div>

      <el-table :data="list" v-loading="loading" border>
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="roleName" label="角色名" />
        <el-table-column prop="roleKey" label="角色标识" />
        <el-table-column prop="description" label="描述" />
        <el-table-column label="操作" width="120">
          <template #default="{ row }">
            <el-button link type="primary" @click="openEdit(row)">编辑</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑角色' : '新增角色'" width="440px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="角色名" prop="roleName">
          <el-input v-model="form.roleName" />
        </el-form-item>
        <el-form-item label="角色标识" prop="roleKey">
          <el-input v-model="form.roleKey" :disabled="isEdit" />
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input v-model="form.description" type="textarea" />
        </el-form-item>
        <el-form-item label="权限" prop="permissionIds">
          <el-select v-model="form.permissionIds" multiple style="width: 100%" filterable>
            <el-option-group v-for="group in permGroups" :key="group.module" :label="group.module">
              <el-option v-for="p in group.items" :key="p.id" :label="p.name" :value="p.id" />
            </el-option-group>
          </el-select>
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
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getRoleList, createRole, updateRole, findRolePermissions } from '../../api/role'
import { getPermissionList } from '../../api/permission'

const list = ref([])
const loading = ref(false)
const allPerms = ref([])
const dialogVisible = ref(false)
const isEdit = ref(false)
const editingId = ref(null)
const submitting = ref(false)
const formRef = ref(null)

const form = reactive({ roleName: '', roleKey: '', description: '', permissionIds: [] })

const rules = {
  roleName: [{ required: true, message: '请输入角色名', trigger: 'blur' }],
  roleKey: [{ required: true, message: '请输入角色标识', trigger: 'blur' }]
}

const permGroups = computed(() => {
  const groups = {}
  for (const p of allPerms.value) {
    const mod = p.module || '通用'
    if (!groups[mod]) groups[mod] = { module: mod, items: [] }
    groups[mod].items.push(p)
  }
  return Object.values(groups)
})

async function load() {
  loading.value = true
  try {
    const res = await getRoleList()
    list.value = res.data
  } finally {
    loading.value = false
  }
}

async function loadPerms() {
  const res = await getPermissionList()
  allPerms.value = res.data
}

function openCreate() {
  isEdit.value = false
  editingId.value = null
  Object.assign(form, { roleName: '', roleKey: '', description: '', permissionIds: [] })
  dialogVisible.value = true
}

async function openEdit(row) {
  isEdit.value = true
  editingId.value = row.id
  Object.assign(form, { roleName: row.roleName, roleKey: row.roleKey, description: row.description, permissionIds: [] })

  try {
    const res = await findRolePermissions(row.id)
    form.permissionIds = res.data.map(p => p.id)
  } catch {}

  dialogVisible.value = true
}

async function submit() {
  await formRef.value.validate()
  submitting.value = true
  try {
    if (isEdit.value) {
      await updateRole(editingId.value, { roleName: form.roleName, roleKey: form.roleKey, description: form.description, permissionIds: form.permissionIds })
      ElMessage.success('更新成功')
    } else {
      await createRole({ roleName: form.roleName, roleKey: form.roleKey, description: form.description, permissionIds: form.permissionIds })
      ElMessage.success('创建成功')
    }
    dialogVisible.value = false
    await load()
  } finally {
    submitting.value = false
  }
}

onMounted(() => { load(); loadPerms() })
</script>

<style scoped>
.role-page { padding: 16px }
.toolbar { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px }
</style>