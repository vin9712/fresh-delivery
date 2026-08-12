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
        <el-sub-menu index="/base">
          <template #title>
            <el-icon><Memo /></el-icon>
            <span>基础数据</span>
          </template>
          <el-menu-item index="/product">
            <el-icon><Goods /></el-icon>
            <template #title>商品管理</template>
          </el-menu-item>
          <el-menu-item index="/sku">
            <el-icon><Box /></el-icon>
            <template #title>SKU管理</template>
          </el-menu-item>
          <el-menu-item index="/category">
            <el-icon><Fold /></el-icon>
            <template #title>客户分类</template>
          </el-menu-item>
          <el-menu-item index="/customer">
            <el-icon><UserFilled /></el-icon>
            <template #title>客户管理</template>
          </el-menu-item>
          <el-menu-item index="/delivery-point">
            <el-icon><Location /></el-icon>
            <template #title>配送点</template>
          </el-menu-item>
        </el-sub-menu>
        <el-sub-menu index="/price">
          <template #title>
            <el-icon><PriceTag /></el-icon>
            <span>报价管理</span>
          </template>
          <el-menu-item index="/template">
            <el-icon><Document /></el-icon>
            <template #title>报价方案</template>
          </el-menu-item>
          <el-menu-item index="/customer-price">
            <el-icon><UserFilled /></el-icon>
            <template #title>客户报价</template>
          </el-menu-item>
        </el-sub-menu>
        <el-sub-menu index="/order">
          <template #title>
            <el-icon><Document /></el-icon>
            <span>订单管理</span>
          </template>
          <el-menu-item index="/order">
            <el-icon><List /></el-icon>
            <template #title>订单列表</template>
          </el-menu-item>
          <el-menu-item index="/order-adjustment">
            <el-icon><Edit /></el-icon>
            <template #title>加退单</template>
          </el-menu-item>
        </el-sub-menu>
        <el-sub-menu index="/delivery">
          <template #title>
            <el-icon><Van /></el-icon>
            <span>配送验收</span>
          </template>
          <el-menu-item index="/delivery-order">
            <el-icon><List /></el-icon>
            <template #title>送货单</template>
          </el-menu-item>
          <el-menu-item index="/acceptance">
            <el-icon><DocumentChecked /></el-icon>
            <template #title>验收单</template>
          </el-menu-item>
        </el-sub-menu>
        <el-sub-menu index="/purchase">
          <template #title>
            <el-icon><ShoppingCart /></el-icon>
            <span>采购管理</span>
          </template>
          <el-menu-item index="/supplier">
            <el-icon><OfficeBuilding /></el-icon>
            <template #title>供应商</template>
          </el-menu-item>
          <el-menu-item index="/purchase-order">
            <el-icon><List /></el-icon>
            <template #title>采购单</template>
          </el-menu-item>
        </el-sub-menu>
        <el-menu-item index="/report-center">
          <el-icon><DataAnalysis /></el-icon>
          <template #title>报表中心</template>
        </el-menu-item>
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