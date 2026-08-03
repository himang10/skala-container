<script setup>
import { ref, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { apiGet, apiPost, formatWon } from '../api.js';

const route = useRoute();
const router = useRouter();
const productId = route.query.productId;

const product = ref(null);
const productError = ref('');
const users = ref([]);

const form = ref({ userId: '', quantity: 1, address: '' });

async function loadProduct() {
    try {
        product.value = await apiGet(`/products/${productId}`);
    } catch (e) {
        productError.value = '상품 조회 실패: ' + e.message;
    }
}

async function loadUsers() {
    try {
        users.value = await apiGet('/users');
    } catch (e) {
        alert('사용자 목록 조회 실패: ' + e.message);
    }
}

async function submitOrder() {
    try {
        await apiPost('/orders', {
            productId: Number(productId),
            userId: Number(form.value.userId),
            quantity: Number(form.value.quantity),
            address: form.value.address
        });
        router.push('/orders');
    } catch (e) {
        alert('주문 생성 실패: ' + e.message);
    }
}

onMounted(() => {
    if (!productId) {
        alert('상품이 지정되지 않았습니다. 주문 관리 화면에서 다시 시도하세요.');
        router.push('/orders');
        return;
    }
    loadProduct();
    loadUsers();
});
</script>

<template>
  <div class="container">
    <h1 class="page-title">주문 - 사용자 선택</h1>
    <p class="page-desc">주문할 사용자를 선택하고 수량·배송지를 입력한 뒤 주문을 확정하세요.</p>

    <!-- 주문할 상품 정보 -->
    <div class="card">
      <h2>주문 상품</h2>
      <table>
        <thead>
          <tr><th>상품명</th><th>가격</th><th>재고</th></tr>
        </thead>
        <tbody>
          <tr v-if="productError" class="empty-row"><td colspan="3">{{ productError }}</td></tr>
          <tr v-else-if="!product" class="empty-row"><td colspan="3">불러오는 중...</td></tr>
          <tr v-else>
            <td>{{ product.name }}</td>
            <td>{{ formatWon(product.price) }}</td>
            <td>{{ product.stockQuantity }}</td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- 사용자 선택 후 OK(주문 확정)하면 주문 완료 -->
    <div class="card">
      <h2>주문 정보 입력</h2>
      <form @submit.prevent="submitOrder">
        <div class="field">
          <label for="userId">사용자</label>
          <select id="userId" v-model="form.userId" required>
            <option value="" disabled>-- 사용자 선택 --</option>
            <option v-for="u in users" :key="u.id" :value="u.id">{{ u.name }} ({{ u.email }})</option>
          </select>
        </div>

        <div class="field">
          <label for="quantity">수량</label>
          <input type="number" id="quantity" v-model="form.quantity" min="1" required>
        </div>

        <div class="field">
          <label for="address">배송지</label>
          <input type="text" id="address" v-model="form.address" placeholder="배송지 주소" style="width: 320px;">
        </div>

        <button type="submit" class="btn btn-primary">주문 확정 (OK)</button>
        <RouterLink class="btn btn-outline" to="/orders">취소</RouterLink>
      </form>
    </div>
  </div>
</template>
