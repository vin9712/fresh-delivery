<template>
  <div class="log-page">
    <el-card>
      <div class="toolbar">
        <el-form inline>
          <el-form-item>
            <el-input v-model="keyword" placeholder="用户/模块/操作" clearable style="width: 220px" @keyup.enter="load" />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="load">搜索</el-button>
          </el-form-item>
        </el-form>
      </div>

      <el-table :data="list" v-loading="loading" border>
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="userName" label="操作用户" />
        <el-table-column prop="module" label="模块" />
        <el-table-column prop="action" label="操作" />
        <el-table-column prop="targetType" label="目标类型" />
        <el-table-column prop="operateTime" label="操作时间" />
        <el-table-column prop="ipAddress" label="IP" />
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
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getLogPage } from '../../api/log'

const list = ref([])
const loading = ref(false)
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(10)
const keyword = ref('')

async function load() {
  loading.value = true
  try {
    const res = await getLogPage({ pageNum: pageNum.value, pageSize: pageSize.value, keyword: keyword.value })
    list.value = res.data.records
    total.value = res.data.total
  } finally {
    loading.value = false
  }
}

onMounted(load)
</script>

<style scoped>
.log-page { padding: 16px }
.toolbar { margin-bottom: 16px }
.pagination { margin-top: 16px; text-align: right }
</style>