<template>
  <div class="approval-page">
    <el-card>
      <el-tabs v-model="activeTab">
        <el-tab-pane label="待审批" name="pending" />
        <el-tab-pane label="全部记录" name="all" />
      </el-tabs>

      <el-table :data="list" v-loading="loading" border style="margin-top: 12px">
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="bizType" label="业务类型" />
        <el-table-column prop="bizId" label="业务ID" />
        <el-table-column prop="submitUser" label="提交人" />
        <el-table-column prop="approver" label="审批人" />
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="statusTagType(row.status)">{{ statusText(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="submitTime" label="提交时间" />
        <el-table-column prop="approveTime" label="审批时间" />
        <el-table-column prop="remark" label="备注" />
        <el-table-column label="操作" width="160" v-if="activeTab === 'pending'">
          <template #default="{ row }">
            <el-button link type="success" @click="approve(row)">通过</el-button>
            <el-button link type="danger" @click="reject(row)">拒绝</el-button>
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

    <el-dialog v-model="handleDialogVisible" :title="handleAction === 'approve' ? '审批通过' : '审批拒绝'" width="360px">
      <el-form label-width="60px">
        <el-form-item label="备注">
          <el-input v-model="handleRemark" type="textarea" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="handleDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmHandle">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { getPendingApprovals, getApprovalPage, handleApproval } from '../../api/approval'

const list = ref([])
const loading = ref(false)
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(10)
const activeTab = ref('pending')

const handleDialogVisible = ref(false)
const handleAction = ref('')
const handleRemark = ref('')
const handleRow = ref(null)

const statusMap = { 0: '待审批', 1: '已通过', 2: '已拒绝' }
const statusTag = { 0: 'info', 1: 'success', 2: 'danger' }

function statusText(s) { return statusMap[s] || '未知' }
function statusTagType(s) { return statusTag[s] || '' }

async function load() {
  loading.value = true
  try {
    if (activeTab.value === 'pending') {
      const res = await getPendingApprovals({ pageNum: pageNum.value, pageSize: pageSize.value })
      list.value = res.data.records
      total.value = res.data.total
    } else {
      const res = await getApprovalPage({ pageNum: pageNum.value, pageSize: pageSize.value })
      list.value = res.data.records
      total.value = res.data.total
    }
  } finally {
    loading.value = false
  }
}

function approve(row) {
  handleAction.value = 'approve'
  handleRow.value = row
  handleRemark.value = ''
  handleDialogVisible.value = true
}

function reject(row) {
  handleAction.value = 'reject'
  handleRow.value = row
  handleRemark.value = ''
  handleDialogVisible.value = true
}

async function confirmHandle() {
  const status = handleAction.value === 'approve' ? 1 : 2
  await handleApproval(handleRow.value.id, { status, remark: handleRemark.value })
  ElMessage.success('审批完成')
  handleDialogVisible.value = false
  await load()
}

watch(activeTab, () => { pageNum.value = 1; load() })
onMounted(load)
</script>

<style scoped>
.approval-page { padding: 16px }
.pagination { margin-top: 16px; text-align: right }
</style>