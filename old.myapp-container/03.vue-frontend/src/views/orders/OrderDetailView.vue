<template>
  <div>
    <div v-if="loading" class="text-center py-5">
      <div class="spinner-border text-warning" role="status"></div>
    </div>
    <div v-else-if="error" class="alert alert-danger">{{ error }}</div>

    <template v-else-if="order">
      <div class="page-header d-flex justify-content-between align-items-center">
        <div>
          <nav aria-label="breadcrumb">
            <ol class="breadcrumb">
              <li class="breadcrumb-item"><RouterLink to="/orders">주문 관리</RouterLink></li>
              <li class="breadcrumb-item active">주문 #{{ order.orderId }}</li>
            </ol>
          </nav>
          <h2>
            <i class="bi bi-receipt me-2"></i>주문 상세
            <span class="fs-4 fw-normal text-muted">#{{ order.orderId }}</span>
          </h2>
        </div>
        <div class="d-flex gap-2">
          <button
            v-if="nextStatuses.includes('CONFIRMED')"
            class="btn btn-sm btn-primary"
            @click="changeStatus('CONFIRMED')"
          >
            <i class="bi bi-check2-circle me-1"></i>확정
          </button>
          <button
            v-if="nextStatuses.includes('SHIPPED')"
            class="btn btn-sm btn-info"
            @click="changeStatus('SHIPPED')"
          >
            <i class="bi bi-truck me-1"></i>배송
          </button>
          <button
            v-if="nextStatuses.includes('DELIVERED')"
            class="btn btn-sm btn-success"
            @click="changeStatus('DELIVERED')"
          >
            <i class="bi bi-check-all me-1"></i>완료
          </button>
          <button
            v-if="nextStatuses.includes('CANCELLED')"
            class="btn btn-sm btn-danger"
            @click="changeStatus('CANCELLED')"
          >
            <i class="bi bi-x-circle me-1"></i>취소
          </button>
        </div>
      </div>

      <div class="row g-4">
        <!-- 주문 정보 -->
        <div class="col-md-4">
          <div class="card h-100">
            <div class="card-header bg-white">
              <h6 class="mb-0 fw-bold">
                <i class="bi bi-info-circle me-2 text-primary"></i>주문 정보
              </h6>
            </div>
            <div class="card-body">
              <dl class="row mb-0">
                <dt class="col-5 text-muted">주문 ID</dt>
                <dd class="col-7 fw-semibold">#{{ order.orderId }}</dd>
                <dt class="col-5 text-muted">주문자</dt>
                <dd class="col-7 fw-semibold">{{ order.userName }}</dd>
                <dt class="col-5 text-muted">주문 상태</dt>
                <dd class="col-7">
                  <span :class="`badge rounded-pill badge-status-${order.status}`">
                    {{ order.status }}
                  </span>
                </dd>
                <dt class="col-5 text-muted">주문일시</dt>
                <dd class="col-7 small">{{ formatDate(order.orderedAt) }}</dd>
                <dt class="col-5 text-muted">총 금액</dt>
                <dd class="col-7 fw-bold text-warning fs-5">{{ fmt(order.totalPrice) }}원</dd>
              </dl>
            </div>
          </div>
        </div>

        <!-- 주문 항목 -->
        <div class="col-md-8">
          <div class="card">
            <div class="card-header bg-white">
              <h6 class="mb-0 fw-bold">
                <i class="bi bi-list-ul me-2 text-warning"></i>주문 항목
                <span class="badge bg-warning text-dark ms-1">{{ (order.items || []).length }}종류</span>
              </h6>
            </div>
            <div class="card-body p-0">
              <table class="table mb-0">
                <thead>
                  <tr>
                    <th>상품명</th>
                    <th class="text-end">단가</th>
                    <th class="text-center">수량</th>
                    <th class="text-end">소계</th>
                  </tr>
                </thead>
                <tbody>
                  <tr v-for="item in order.items" :key="item.id">
                    <td class="fw-semibold">{{ item.productName }}</td>
                    <td class="text-end text-muted">{{ fmt(item.unitPrice) }}원</td>
                    <td class="text-center">
                      <span class="badge bg-light text-dark border">{{ item.quantity }}개</span>
                    </td>
                    <td class="text-end fw-bold">{{ fmt(item.subtotal) }}원</td>
                  </tr>
                  <tr class="table-warning">
                    <td colspan="3" class="text-end fw-bold">합계</td>
                    <td class="text-end fw-bold">{{ fmt(order.totalPrice) }}원</td>
                  </tr>
                </tbody>
              </table>
            </div>
          </div>
        </div>
      </div>
    </template>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, inject } from 'vue'
import { useRoute } from 'vue-router'
import { orderApi } from '@/api'

const route = useRoute()
const toast = inject('toast')

const order = ref(null)
const loading = ref(true)
const error = ref(null)

const STATUS_FLOW = {
  PENDING: ['CONFIRMED', 'CANCELLED'],
  CONFIRMED: ['SHIPPED'],
  SHIPPED: ['DELIVERED']
}

const STATUS_LABELS = {
  CONFIRMED: '확정',
  SHIPPED: '배송 처리',
  DELIVERED: '배송 완료',
  CANCELLED: '취소'
}

const nextStatuses = computed(() => STATUS_FLOW[order.value?.status] || [])

onMounted(async () => {
  await loadOrder()
})

async function loadOrder() {
  loading.value = true
  error.value = null
  try {
    order.value = await orderApi.getById(route.params.id)
  } catch (e) {
    error.value = '주문 조회 실패: ' + e.message
  } finally {
    loading.value = false
  }
}

async function changeStatus(status) {
  const label = STATUS_LABELS[status] || status
  if (!confirm(`주문을 ${label} 처리하시겠습니까?`)) return
  try {
    await orderApi.updateStatus(route.params.id, status)
    toast(`주문 상태가 ${label}(으)로 변경되었습니다.`)
    await loadOrder()
  } catch (e) {
    toast('상태 변경 실패: ' + e.message, 'danger')
  }
}

function fmt(n) {
  return Number(n).toLocaleString('ko-KR')
}

function formatDate(dt) {
  return dt ? dt.substring(0, 16).replace('T', ' ') : '-'
}
</script>
