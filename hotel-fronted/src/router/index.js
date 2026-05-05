import { createRouter, createWebHistory } from 'vue-router'

/**
 * 路由配置。
 *
 * 两层结构：
 *   /login — 独立页面，meta.noAuth=true 跳过 Token 校验
 *   /       — MainLayout 壳，包含侧边栏 + 顶栏，子路由渲染在 <router-view /> 中
 *
 * 导航守卫逻辑：
 *   1. 设置页面标题
 *   2. meta.noAuth 标记的路由直接放行（登录页）
 *   3. 其余路由检查 localStorage.token 是否存在，不存在则重定向到 /login
 */
const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/Login.vue'),
    meta: { title: '登录', noAuth: true }
  },
  {
    path: '/',
    component: () => import('../layout/MainLayout.vue'),
    redirect: '/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('../views/Dashboard.vue'),
        meta: { title: '工作台', icon: 'Odometer' }
      },
      {
        path: 'rooms',
        name: 'Rooms',
        component: () => import('../views/RoomStatus.vue'),
        meta: { title: '房态图', icon: 'HomeFilled' }
      },
      {
        path: 'checkins',
        name: 'Checkins',
        component: () => import('../views/CheckinManagement.vue'),
        meta: { title: '入住管理', icon: 'Edit' }
      },
      {
        path: 'checkins/create',
        name: 'CreateCheckin',
        component: () => import('../views/CheckinCreate.vue'),
        meta: { title: '办理入住', icon: 'Edit' }
      },
      {
        path: 'checkins/:id',
        name: 'CheckinDetail',
        component: () => import('../views/CheckinDetail.vue'),
        meta: { title: '入住详情', icon: 'Edit' }
      },
      {
        path: 'checkouts',
        name: 'Checkouts',
        component: () => import('../views/CheckoutManagement.vue'),
        meta: { title: '退房管理', icon: 'Switch' }
      },
      {
        path: 'guests',
        name: 'Guests',
        component: () => import('../views/GuestManagement.vue'),
        meta: { title: '客人管理', icon: 'User' }
      },
      {
        path: 'deposits',
        name: 'Deposits',
        component: () => import('../views/DepositManagement.vue'),
        meta: { title: '押金管理', icon: 'Coin' }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  document.title = to.meta.title ? `${to.meta.title} - Leaf-Hotel` : 'Leaf-Hotel'
  if (to.meta.noAuth) {
    next()
  } else {
    const token = localStorage.getItem('token')
    if (!token) {
      next('/login')
    } else {
      next()
    }
  }
})

export default router
