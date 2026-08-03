<template>
  <div>
    <div class="page-header d-flex justify-content-between align-items-center">
      <div>
        <h2><i class="bi bi-house me-2"></i>대시보드</h2>
        <p class="text-muted mb-0">SK Skala 쇼핑몰 관리 현황</p>
      </div>
    </div>

    <!-- 로딩 -->
    <div v-if="loading" class="text-center py-5">
      <div class="spinner-border text-primary" role="status"></div>
    </div>

    <!-- 에러 -->
    <div v-else-if="error" class="alert alert-danger">{{ error }}</div>

    <!-- 통계 카드 -->
    <template v-else>
      <div class="row g-4 mb-4">
        <div class="col-md-3">
          <div class="card stat-card stat-card-blue p-3">
            <div class="d-flex align-items-center">
              <div class="flex-grow-1">
                <div class="text-muted small">전체 사용자</div>
                <div class="fs-2 fw-bold text-primary">{{ stats.users }}</div>
              </div>
              <i class="bi bi-people fs-1 text-primary opacity-25"></i>
            </div>
            <RouterLink to="/users" class="text-decoration-none small text-muted mt-2 d-block">
              관리하기 <i class="bi bi-arrow-right"></i>
            </RouterLink>
          </div>
        </div>
        <div class="col-md-3">
          <div class="card stat-card stat-card-green p-3">
            <div class="d-flex align-items-center">
              <div class="flex-grow-1">
                <div class="text-muted small">전체 카테고리</div>
                <div class="fs-2 fw-bold text-success">{{ stats.categories }}</div>
              </div>
              <i class="bi bi-grid fs-1 text-success opacity-25"></i>
            </div>
            <RouterLink
              to="/categories"
              class="text-decoration-none small text-muted mt-2 d-block"
            >
              관리하기 <i class="bi bi-arrow-right"></i>
            </RouterLink>
          </div>
        </div>
        <div class="col-md-3">
          <div class="card stat-card stat-card-orange p-3">
            <div class="d-flex align-items-center">
              <div class="flex-grow-1">
                <div class="text-muted small">전체 주문</div>
                <div class="fs-2 fw-bold text-warning">{{ stats.orders }}</div>
              </div>
              <i class="bi bi-bag fs-1 text-warning opacity-25"></i>
            </div>
            <RouterLink to="/orders" class="text-decoration-none small text-muted mt-2 d-block">
              관리하기 <i class="bi bi-arrow-right"></i>
            </RouterLink>
          </div>
        </div>
        <div class="col-md-3">
          <div class="card stat-card stat-card-red p-3">
            <div class="d-flex align-items-center">
              <div class="flex-grow-1">
                <div class="text-muted small">대기 중 주문</div>
                <div class="fs-2 fw-bold text-danger">{{ stats.pending }}</div>
              </div>
              <i class="bi bi-clock-history fs-1 text-danger opacity-25"></i>
            </div>
            <RouterLink to="/orders" class="text-decoration-none small text-muted mt-2 d-block">
              확인하기 <i class="bi bi-arrow-right"></i>
            </RouterLink>
          </div>
        </div>
      </div>

      <!-- 최근 주문 목록 -->
      <div class="card">
        <div class="card-header bg-white d-flex justify-content-between align-items-center py-3">
          <h5 class="mb-0">
            <i class="bi bi-clock me-2 text-primary"></i>최근 주문 목록
          </h5>
          <RouterLink to="/orders" class="btn btn-sm btn-outline-primary">전체 보기</RouterLink>
        </div>
        <div class="card-body p-0">
          <div class="table-responsive">
            <table class="table table-hover mb-0">
              <thead>
                <tr>
                  <th>주문 ID</th>
                  <th>주문자</th>
                  <th>총 금액</th>
                  <th>상태</th>
                  <th>주문일시</th>
                  <th>상세</th>
                </tr>
              </thead>
              <tbody>
                <tr v-if="recentOrders.length === 0">
                  <td colspan="6" class="text-center text-muted py-4">주문 내역이 없습니다.</td>
                </tr>
                <tr v-for="o in recentOrders" :key="o.orderId">
                  <td><span class="fw-bold">#{{ o.orderId }}</span></td>
                  <td>{{ o.userName }}</td>
                  <td class="fw-bold text-warning">{{ fmt(o.totalPrice) }}원</td>
                  <td>
                    <span :class="`badge rounded-pill badge-status-${o.status}`">{{ o.status }}</span>
                  </td>
                  <td>
                    <small class="text-muted">{{ formatDate(o.orderedAt) }}</small>
                  </td>
                  <td>
                    <RouterLink
                      :to="`/orders/${o.orderId}`"
                      class="btn btn-sm btn-outline-warning"
                    >
                      <i class="bi bi-eye"></i> 상세
                    </RouterLink>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
      </div>
    </template>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { userApi, categoryApi, orderApi } from '@/api'

const loading = ref(true)
const error = ref(null)
const stats = ref({ users: 0, categories: 0, orders: 0, pending: 0 })
const recentOrders = ref([])

function fmt(n) {
  return Number(n).toLocaleString('ko-KR')
}

function formatDate(dt) {
  return dt ? dt.substring(0, 16).replace('T', ' ') : '-'
}

onMounted(async () => {
  try {
    const [users, categories] = await Promise.all([userApi.getAll(), categoryApi.getAll()])

    // 모든 사용자의 주문 수집
    const orderResults = await Promise.allSettled(
      users.map((u) => orderApi.getByUserId(u.id))
    )
    const allOrders = orderResults
      .filter((r) => r.status === 'fulfilled')
      .flatMap((r) => r.value || [])

    stats.value = {
      users: users.length,
      categories: categories.length,
      orders: allOrders.length,
      pending: allOrders.filter((o) => o.status === 'PENDING').length
    }

    recentOrders.value = [...allOrders]
      .sort((a, b) => new Date(b.orderedAt) - new Date(a.orderedAt))
      .slice(0, 10)
  } catch (e) {
    error.value = '데이터 로딩 실패: ' + e.message
  } finally {
    loading.value = false
  }
})
</script>
