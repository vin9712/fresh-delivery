import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/Login.vue')
  },
  {
    path: '/',
    component: () => import('../views/Layout.vue'),
    redirect: '/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('../views/Dashboard.vue')
      },
      {
        path: 'user',
        name: 'User',
        component: () => import('../views/sys/User.vue')
      },
      {
        path: 'role',
        name: 'Role',
        component: () => import('../views/sys/Role.vue')
      },
      {
        path: 'permission',
        name: 'Permission',
        component: () => import('../views/sys/Permission.vue')
      },
      {
        path: 'log',
        name: 'Log',
        component: () => import('../views/sys/Log.vue')
      },
      {
        path: 'approval',
        name: 'Approval',
        component: () => import('../views/sys/Approval.vue')
      }
    ],
    meta: { requiresAuth: true }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  if (to.meta.requiresAuth && !token) {
    next('/login')
  } else if (to.path === '/login' && token) {
    next('/')
  } else {
    next()
  }
})

export default router