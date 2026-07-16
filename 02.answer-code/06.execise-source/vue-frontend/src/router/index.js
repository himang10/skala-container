import { createRouter, createWebHashHistory } from 'vue-router'

import DashboardView from '@/views/DashboardView.vue'
import UserListView from '@/views/users/UserListView.vue'
import UserFormView from '@/views/users/UserFormView.vue'
import CategoryListView from '@/views/categories/CategoryListView.vue'
import CategoryFormView from '@/views/categories/CategoryFormView.vue'
import OrderListView from '@/views/orders/OrderListView.vue'
import OrderDetailView from '@/views/orders/OrderDetailView.vue'
import OrderFormView from '@/views/orders/OrderFormView.vue'

const routes = [
  { path: '/', redirect: '/dashboard' },
  { path: '/dashboard', name: 'dashboard', component: DashboardView },

  { path: '/users', name: 'users', component: UserListView },
  { path: '/users/new', name: 'user-new', component: UserFormView },
  { path: '/users/:id/edit', name: 'user-edit', component: UserFormView },

  { path: '/categories', name: 'categories', component: CategoryListView },
  { path: '/categories/new', name: 'category-new', component: CategoryFormView },
  { path: '/categories/:id/edit', name: 'category-edit', component: CategoryFormView },

  { path: '/orders', name: 'orders', component: OrderListView },
  { path: '/orders/new', name: 'order-new', component: OrderFormView },
  { path: '/orders/:id', name: 'order-detail', component: OrderDetailView }
]

const router = createRouter({
  history: createWebHashHistory(),
  routes
})

export default router
