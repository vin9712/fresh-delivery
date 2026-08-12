<template>
  <el-container class="layout">
    <el-aside width="210px" class="layout-aside">
      <div class="aside-logo">
        <el-icon><Van /></el-icon>
        <span>生鲜配送</span>
      </div>
      <el-menu
        :default-active="route.path"
        router
        class="aside-menu"
        :collapse="collapsed"
      >
        <el-menu-item index="/dashboard">
          <el-icon><DataAnalysis /></el-icon>
          <template #title>首页</template>
        </el-menu-item>
        <el-sub-menu index="/sys">
          <template #title>
            <el-icon><Setting /></el-icon>
            <span>系统管理</span>
          </template>
          <el-menu-item index="/user">
            <el-icon><User /></el-icon>
            <template #title>用户管理</template>
          </el-menu-item>
          <el-menu-item index="/role">
            <el-icon><UserFilled /></el-icon>
            <template #title>角色管理</template>
          </el-menu-item>
          <el-menu-item index="/permission">
            <el-icon><Key /></el-icon>
            <template #title>权限管理</template>
          </el-menu-item>
          <el-menu-item index="/approval">
            <el-icon><DocumentChecked /></el-icon>
            <template #title>审批中心</template>
          </el-menu-item>
          <el-menu-item index="/log">
            <el-icon><List /></el-icon>
            <template #title>操作日志</template>
          </el-menu-item>
        </el-sub-menu>
      </el-menu>
    </el-aside>

    <el-container>
      <el-header class="layout-header">
        <div class="header-left">
          <el-icon class="collapse-btn" @click="collapsed = !collapsed">
            <Fold v-if="!collapsed" />
            <Expand v-else />
          </el-icon>
          <span class="header-title">生鲜配送管理系统</span>
        </div>
        <div class="header-right">
          <el-dropdown>
            <span class="user-info">
              <el-avatar :size="28">
                {{ userStore.user.realName?.charAt(0) || 'U' }}
              </el-avatar>
              <span class="user-name">{{ userStore.user.realName || '用户' }}</span>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item @click="userStore.logout()">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>

      <el-main class="layout-main">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import { useUserStore } from '../stores/user'

const route = useRoute()
const userStore = useUserStore()
const collapsed = computed(() => false)
</script>

<style scoped>
.layout {
  height: 100vh;
}
.layout-aside {
  background: #304156;
  color: #fff;
}
.aside-logo {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  font-size: 18px;
  font-weight: bold;
  border-bottom: 1px solid #263445;
}
.aside-menu {
  border-right: none;
  background: transparent;
}
.aside-menu .el-menu-item {
  color: #bfcbd9;
}
.aside-menu .el-menu-item.is-active {
  color: #fff;
  background: #409eff;
}
.aside-menu .el-sub-menu__title {
  color: #bfcbd9;
}
.layout-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  background: #fff;
  border-bottom: 1px solid #e8e8e8;
}
.header-left {
  display: flex;
  align-items: center;
  gap: 12px;
}
.header-title {
  font-size: 14px;
  color: #333;
}
.collapse-btn {
  cursor: pointer;
  font-size: 18px;
}
.header-right {
  display: flex;
  align-items: center;
}
.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
}
.user-name {
  font-size: 14px;
  color: #333;
}
.layout-main {
  background: #f0f2f5;
  padding: 16px;
}
</style>