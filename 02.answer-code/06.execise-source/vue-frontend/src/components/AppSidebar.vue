<template>
  <nav class="sidebar d-flex flex-column p-3">
    <div class="sidebar-brand">
      <i class="bi bi-shop me-2"></i>SK Skala Shop
    </div>
    <ul class="nav flex-column gap-1">
      <li class="nav-item">
        <RouterLink
          class="nav-link"
          :class="{ active: currentPage === 'dashboard' }"
          to="/dashboard"
        >
          <i class="bi bi-house me-2"></i>대시보드
        </RouterLink>
      </li>
      <li class="nav-item mt-2">
        <small class="text-white-50 px-2 text-uppercase" style="font-size: 0.7rem">관리</small>
      </li>
      <li class="nav-item">
        <RouterLink
          class="nav-link"
          :class="{ active: currentPage === 'users' }"
          to="/users"
        >
          <i class="bi bi-people me-2"></i>사용자 관리
        </RouterLink>
      </li>
      <li class="nav-item">
        <RouterLink
          class="nav-link"
          :class="{ active: currentPage === 'categories' }"
          to="/categories"
        >
          <i class="bi bi-grid me-2"></i>카테고리 관리
        </RouterLink>
      </li>
      <li class="nav-item">
        <RouterLink
          class="nav-link"
          :class="{ active: currentPage === 'orders' }"
          to="/orders"
        >
          <i class="bi bi-bag me-2"></i>주문 관리
        </RouterLink>
      </li>
    </ul>
  </nav>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'

const route = useRoute()
const currentPage = computed(() => route.path.split('/')[1] || 'dashboard')
</script>
