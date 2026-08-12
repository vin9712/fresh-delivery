<template>
  <div class="perm-page">
    <el-card>
      <el-table :data="list" v-loading="loading" border>
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="name" label="名称" />
        <el-table-column prop="permissionKey" label="权限标识" />
        <el-table-column prop="module" label="所属模块" />
        <el-table-column prop="description" label="描述" />
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getPermissionList } from '../../api/permission'

const list = ref([])
const loading = ref(false)

async function load() {
  loading.value = true
  try {
    const res = await getPermissionList()
    list.value = res.data
  } finally {
    loading.value = false
  }
}

onMounted(load)
</script>

<style scoped>
.perm-page { padding: 16px }
</style>