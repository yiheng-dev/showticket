import { createRouter, createWebHistory } from 'vue-router'
import { auth } from '../store/auth'

const routes = [
  { path: '/', name: 'home', component: () => import('../views/HomeView.vue') },
  { path: '/login', name: 'login', component: () => import('../views/LoginView.vue') },
  { path: '/shows/:id', name: 'show-detail', component: () => import('../views/ShowDetailView.vue') },
  { path: '/selections', name: 'selections', component: () => import('../views/SelectionView.vue'), meta: { auth: true } },
  { path: '/orders', name: 'orders', component: () => import('../views/OrdersView.vue'), meta: { auth: true } },
  { path: '/orders/:id', name: 'order-detail', component: () => import('../views/OrderDetailView.vue'), meta: { auth: true } },
  { path: '/admin', name: 'admin', component: () => import('../views/AdminView.vue') },
  { path: '/:pathMatch(.*)*', redirect: '/' }
]

const router = createRouter({
  history: createWebHistory(),
  routes,
  scrollBehavior() {
    return { top: 0 }
  }
})

router.beforeEach((to) => {
  if (to.meta.auth && !auth.token) {
    return { path: '/login', query: { redirect: to.fullPath } }
  }
  return true
})

export default router
