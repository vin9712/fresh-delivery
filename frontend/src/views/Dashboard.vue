<template>
  <div class="dashboard">
    <el-row :gutter="16">
      <el-col :span="8">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <el-icon><User /></el-icon>
              <span>当前用户</span>
            </div>
          </template>
          <div class="info-row">
            <span>姓名</span>
            <el-tag>{{ userStore.user.realName || '-' }}</el-tag>
          </div>
          <div class="info-row">
            <span>账号</span>
            <el-tag>{{ userStore.user.username || '-' }}</el-tag>
          </div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <el-icon><Timer /></el-icon>
              <span>项目版本</span>
            </div>
          </template>
          <div class="version">v1.0.0</div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <el-icon><Calendar /></el-icon>
              <span>今天</span>
            </div>
          </template>
          <div class="version">{{ today }}</div>
        </el-card>
      </el-col>
    </el-row>

    <el-card style="margin-top: 16px" shadow="hover">
      <template #header>
        <div class="card-header">
          <el-icon><Document /></el-icon>
          <span>系统说明</span>
        </div>
      </template>
      <p>欢迎使用生鲜配送管理系统。M1/M2/M3/M4 已完成，后续模块按里程碑逐步开发：</p>
      <el-steps :active="4" finish-status="success" align-center>
        <el-step title="系统管理" description="用户/角色/权限/日志/审批" />
        <el-step title="基础数据" description="商品/SKU/客户分类/客户/配送点" />
        <el-step title="报价管理" description="报价方案/客户报价/模板导入" />
        <el-step title="订单管理" description="订单列表/加退单/取价" />
        <el-step title="配送验收" />
        <el-step title="采购管理" />
        <el-step title="报表中心" />
      </el-steps>
    </el-card>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useUserStore } from '../stores/user'

const userStore = useUserStore()

const today = computed(() => {
  const d = new Date()
  const days = ['日', '一', '二', '三', '四', '五', '六']
  return `${d.getFullYear()}年${d.getMonth() + 1}月${d.getDate()}日 星期${days[d.getDay()]}`
})
</script>

<style scoped>
.card-header {
  display: flex;
  align-items: center;
  gap: 8px;
}
.info-row {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 8px 0;
}
.info-row span {
  color: #999;
  font-size: 13px;
  min-width: 40px;
}
.version {
  font-size: 24px;
  font-weight: bold;
  color: #409eff;
  text-align: center;
  padding: 20px 0;
}
</style>