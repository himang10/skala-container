<script setup>
import { ref, onMounted } from 'vue';
import { apiGet, apiPost, formatWon, formatDateTime } from '../api.js';

const products = ref([]);
const productsError = ref('');
const productsLoading = ref(true);

const orders = ref([]);
const ordersError = ref('');
const ordersLoading = ref(true);

async function loadProducts() {
    productsLoading.value = true;
    productsError.value = '';
    try {
        products.value = await apiGet('/products');
    } catch (e) {
        productsError.value = '조회 실패: ' + e.message;
    } finally {
        productsLoading.value = false;
    }
}

async function loadOrders() {
    ordersLoading.value = true;
    ordersError.value = '';
    try {
        orders.value = await apiGet('/orders');
    } catch (e) {
        ordersError.value = '조회 실패: ' + e.message;
    } finally {
        ordersLoading.value = false;
    }
}

async function advanceStatus(id) {
    await apiPost(`/orders/${id}/next-status`);
    loadOrders();
}

onMounted(() => {
    loadProducts();
    loadOrders();
});
</script>

<template>
  <div class="container">
    <h1 class="page-title">주문 관리</h1>
    <p class="page-desc">상품을 주문하고, 상태 버튼을 눌러 결제·배송 단계를 진행합니다.</p>

    <!-- 1단계: 상품 목록에서 주문 버튼 클릭 -->
    <div class="card">
      <h2>상품 목록</h2>
      <table>
        <thead>
          <tr><th>ID</th><th>상품명</th><th>가격</th><th>재고</th><th>주문</th></tr>
        </thead>
        <tbody>
          <tr v-if="productsLoading" class="empty-row"><td colspan="5">불러오는 중...</td></tr>
          <tr v-else-if="productsError" class="empty-row"><td colspan="5">{{ productsError }}</td></tr>
          <tr v-else-if="products.length === 0" class="empty-row">
            <td colspan="5">등록된 상품이 없습니다. 상품 관리에서 먼저 등록하세요.</td>
          </tr>
          <template v-else>
            <tr v-for="p in products" :key="p.id">
              <td>{{ p.id }}</td>
              <td>{{ p.name }}</td>
              <td>{{ formatWon(p.price) }}</td>
              <td>{{ p.stockQuantity }}</td>
              <td>
                <RouterLink class="btn btn-primary btn-sm" :to="{ path: '/orders/new', query: { productId: p.id } }">주문</RouterLink>
              </td>
            </tr>
          </template>
        </tbody>
      </table>
    </div>

    <!-- 2단계: 주문 목록에서 상태 버튼 클릭 → 다음 상태로 전환 -->
    <div class="card">
      <h2>주문 목록</h2>
      <p class="page-desc">상태 버튼을 클릭하면 다음 단계로 진행됩니다.
        (주문 완료 → 결제 대기 → 결제 완료 → 배송 대기 → 배송 완료)</p>
      <table>
        <thead>
          <tr>
            <th>ID</th><th>사용자</th><th>상품</th><th>수량</th>
            <th>결제 금액</th><th>주문 시각</th><th>현재 상태</th>
          </tr>
        </thead>
        <tbody>
          <tr v-if="ordersLoading" class="empty-row"><td colspan="7">불러오는 중...</td></tr>
          <tr v-else-if="ordersError" class="empty-row"><td colspan="7">{{ ordersError }}</td></tr>
          <tr v-else-if="orders.length === 0" class="empty-row"><td colspan="7">주문이 없습니다.</td></tr>
          <template v-else>
            <tr v-for="o in orders" :key="o.id">
              <td>{{ o.id }}</td>
              <td>{{ o.userName }}</td>
              <td>{{ o.productName }}</td>
              <td>{{ o.quantity }}</td>
              <td>{{ formatWon(o.totalAmount) }}</td>
              <td>{{ formatDateTime(o.orderedAt) }}</td>
              <td>
                <span v-if="o.status === 'DELIVERY_COMPLETED'" class="badge badge-done">{{ o.statusLabel }}</span>
                <button v-else class="btn btn-primary btn-sm" @click="advanceStatus(o.id)">{{ o.statusLabel }}</button>
              </td>
            </tr>
          </template>
        </tbody>
      </table>
    </div>
  </div>
</template>
