<template>
  <div>
    <div class="page-header d-flex justify-content-between align-items-center">
      <div>
        <h2><i class="bi bi-bag me-2"></i>주문 관리</h2>
        <p class="text-muted mb-0">전체 주문 현황</p>
      </div>
      <RouterLink to="/orders/new" class="btn btn-warning text-dark">
        <i class="bi bi-plus-circle me-1"></i>주문 생성
      </RouterLink>
    </div>

    <!-- 사용자 필터 -->
    <div class="card mb-4">
      <div class="card-body py-2">
        <form class="row g-2 align-items-end" @submit.prevent="applyFilter">
          <div class="col-md-4">
            <label class="form-label small fw-semibold mb-1">사용자로 검색</label>
            <select v-model="selectedUserId" class="form-select form-select-sm">
              <option value="">-- 전체 사용자 --</option>
              <option v-for="u in users" :key="u.id" :value="u.id">
                {{ u.name }} ({{ u.email }})
              </option>
            </select>
          </div>
          <div class="col-md-2">
            <button type="submit" class="btn btn-sm btn-outline-primary">
              <i class="bi bi-search me-1"></i>조회
            </button>
            <button type="button" class="btn btn-sm btn-outline-secondary ms-1" @click="resetFilter">
              초기화
            </button>
          </div>
        </form>
      </div>
    </div>

    <div v-if="loading" class="text-center py-5">
      <div class="spinner-border text-warning" role="status"></div>
    </div>
    <div v-else-if="error" class="alert alert-danger">{{ error }}</div>
    <div v-else class="card">
      <div class="card-body p-0">
        <div class="table-responsive">
          <table class="table table-hover mb-0">
            <thead>
              <tr>
                <th style="width: 80px">주문 ID</th>
                <th>주문자</th>
                <th>주문 항목</th>
                <th>총 금액</th>
                <th>상태</th>
                <th>주문일시</th>
                <th style="width: 100px" class="text-center">상세</th>
              </tr>
            </thead>
            <tbody>
              <tr v-if="orders.length === 0">
                <td colspan="7" class="text-center text-muted py-5">
                  <i class="bi bi-inbox fs-1 d-block mb-2"></i>주문 내역이 없습니다.
                </td>
              </tr>
              <tr v-for="o in orders" :key="o.orderId">
                <td><span class="badge bg-secondary">#{{ o.orderId }}</span></td>
                <td><span class="fw-semibold">{{ o.userName }}</span></td>
                <td><span class="badge bg-light text-dark border">{{ (o.items || []).length }}종류</span></td>
                <td><span class="fw-bold text-warning">{{ fmt(o.totalPrice) }}원</span></td>
                <td>
                  <span :class="`badge rounded-pill badge-status-${o.status}`">{{ o.status }}</span>
                </td>
                <td><small class="text-muted">{{ formatDate(o.orderedAt) }}</small></td>
                <td class="text-center">
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
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { userApi, orderApi } from '@/api'

const route = useRoute()
const router = useRouter()

const users = ref([])
const orders = ref([])
const loading = ref(true)
const error = ref(null)
const selectedUserId = ref('')

onMounted(async () => {
  try {
    users.value = await userApi.getAll()
  } catch {
    // 사용자 목록 조회 실패 시 무시
  }
  // URL 쿼리 파라미터로 초기 필터 적용
  selectedUserId.value = route.query.userId || ''
  await loadOrders()
})

watch(() => route.query.userId, async (val) => {
  selectedUserId.value = val || ''
  await loadOrders()
})

async function loadOrders() {
  loading.value = true
  error.value = null
  try {
    if (selectedUserId.value) {
      orders.value = await orderApi.getByUserId(selectedUserId.value)
    } else {
      // 전체 주문 조회
      const results = await Promise.allSettled(
        users.value.map((u) => orderApi.getByUserId(u.id))
      )
      orders.value = results
        .filter((r) => r.status === 'fulfilled')
        .flatMap((r) => r.value || [])
        .sort((a, b) => new Date(b.orderedAt) - new Date(a.orderedAt))
    }
  } catch (e) {
    error.value = '로딩 실패: ' + e.message
  } finally {
    loading.value = false
  }
}

function applyFilter() {
  if (selectedUserId.value) {
    router.push({ query: { userId: selectedUserId.value } })
  } else {
    router.push('/orders')
  }
}

function resetFilter() {
  selectedUserId.value = ''
  router.push('/orders')
}

function fmt(n) {
  return Number(n).toLocaleString('ko-KR')
}

function formatDate(dt) {
  return dt ? dt.substring(0, 16).replace('T', ' ') : '-'
}
</script>
