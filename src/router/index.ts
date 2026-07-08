import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/',
      name: 'Home',
      component: () => import('@/pages/HomePage.vue'),
    },
    {
      path: '/login',
      name: 'Login',
      component: () => import('@/pages/LoginPage.vue'),
    },
    {
      path: '/register',
      name: 'Register',
      component: () => import('@/pages/RegisterPage.vue'),
    },
    {
      path: '/order/confirm',
      name: 'OrderConfirm',
      component: () => import('@/pages/OrderConfirmPage.vue'),
      meta: { requiresAuth: true },
    },
    {
      path: '/orders',
      name: 'Orders',
      component: () => import('@/pages/OrderListPage.vue'),
      meta: { requiresAuth: true },
    },
    {
      path: '/pay/:orderNo',
      name: 'Pay',
      component: () => import('@/pages/PayPage.vue'),
      meta: { requiresAuth: true },
    },
    {
      path: '/passengers',
      name: 'Passengers',
      component: () => import('@/pages/PassengerPage.vue'),
      meta: { requiresAuth: true },
    },
  ],
})

router.beforeEach((to) => {
  if (to.meta.requiresAuth) {
    const token = localStorage.getItem('token')
    if (!token) {
      return '/login'
    }
  }
})

export default router
