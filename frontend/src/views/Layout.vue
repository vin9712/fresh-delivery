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
          <el-menu-item index="/product-category">
            <el-icon><Folder /></el-icon>
            <template #title>商品分类</template>
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
          <el-breadcrumb separator="/" class="header-breadcrumb">
            <el-breadcrumb-item :to="{ path: '/dashboard' }">首页</el-breadcrumb-item>
            <el-breadcrumb-item v-for="item in breadcrumbList" :key="item.path">
              {{ item.title }}
            </el-breadcrumb-item>
          </el-breadcrumb>
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

// 菜单路径 → 标题 映射（对应左侧菜单结构）
const menuTitleMap = {
  '/sys': '系统管理',
  '/base': '基础数据',
  '/price': '报价管理',
  '/order': '订单管理',
  '/delivery': '配送验收',
  '/purchase': '采购管理',
  '/user': '用户管理',
  '/role': '角色管理',
  '/permission': '权限管理',
  '/approval': '审批中心',
  '/log': '操作日志',
  '/product': '商品管理',
  '/sku': 'SKU管理',
  '/product-category': '商品分类',
  '/category': '客户分类',
  '/customer': '客户管理',
  '/delivery-point': '配送点',
  '/template': '报价方案',
  '/customer-price': '客户报价',
  '/order-adjustment': '加退单',
  '/delivery-order': '送货单',
  '/acceptance': '验收单',
  '/supplier': '供应商',
  '/purchase-order': '采购单',
  '/report-center': '报表中心',
  '/dashboard': '首页'
}

const breadcrumbList = computed(() => {
  const path = route.path
  if (!path || path === '/dashboard') return []

  // 一级菜单分组路径
  const groupPaths = ['/sys', '/base', '/price', '/order', '/delivery', '/purchase']
  const group = groupPaths.find(g => path.startsWith(g))
  const items = []
  if (group) items.push({ path: group, title: menuTitleMap[group] })
  items.push({ path, title: menuTitleMap[path] || route.name || '' })
  return items
})
</script>

<style scoped>
.layout {
  height: 100vh;
}
.layout-aside {
  background: linear-gradient(180deg, #131d2e 0%, #0d1623 100%);
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
  color: #fff;
  background: rgba(0, 0, 0, 0.15);
  border-bottom: 1px solid rgba(255, 255, 255, 0.08);
}
.aside-logo .el-icon {
  color: #60a5fa;
  font-size: 26px;
}
.aside-menu {
  border-right: none;
  --el-menu-bg-color: transparent;
  --el-menu-text-color: #a9b8d0;
  --el-menu-active-color: #fff;
  --el-menu-hover-bg-color: rgba(96, 165, 250, 0.12);
  background: transparent;
}
.aside-menu .el-menu-item,
.aside-menu .el-sub-menu .el-sub-menu__title {
  background: transparent;
  color: #a9b8d0;
  margin: 4px 12px;
  border-radius: 6px;
  transition: all 0.2s ease;
}
.aside-menu .el-menu-item .el-icon,
.aside-menu .el-sub-menu .el-sub-menu__title .el-icon {
  color: #74a5d6;
}
.aside-menu .el-menu-item:hover,
.aside-menu .el-sub-menu .el-sub-menu__title:hover {
  background: rgba(96, 165, 250, 0.12) !important;
  color: #fff !important;
}
.aside-menu .el-menu-item:hover .el-icon,
.aside-menu .el-sub-menu .el-sub-menu__title:hover .el-icon {
  color: #60a5fa !important;
}
.aside-menu .el-menu-item.is-active,
.aside-menu .el-sub-menu.is-active > .el-sub-menu__title {
  color: #fff !important;
  background: linear-gradient(135deg, #3b82f6 0%, #2563eb 100%) !important;
  box-shadow: 0 4px 12px rgba(59, 130, 246, 0.45);
}
.aside-menu .el-menu-item.is-active .el-icon,
.aside-menu .el-sub-menu.is-active > .el-sub-menu__title .el-icon {
  color: #fff !important;
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
.header-breadcrumb :deep(.el-breadcrumb__item .el-breadcrumb__inner) {
  font-size: 14px;
  color: #606266;
  font-weight: 400;
}
.header-breadcrumb :deep(.el-breadcrumb__item:last-child .el-breadcrumb__inner) {
  color: #3b82f6;
  font-weight: 600;
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