<script setup>
import { ref, onMounted } from 'vue';
import { apiGet, apiPost, apiDelete, formatWon } from '../api.js';

const products = ref([]);
const loading = ref(true);
const errorMessage = ref('');
const keyword = ref('');

const form = ref({ name: '', price: '', stockQuantity: '', description: '' });

async function loadProducts() {
    loading.value = true;
    errorMessage.value = '';
    try {
        let list = await apiGet('/products');
        // 검색 API가 따로 없으므로, 전체 목록을 받아온 뒤 이름으로 걸러낸다.
        if (keyword.value) {
            list = list.filter((p) => p.name.includes(keyword.value));
        }
        products.value = list;
    } catch (e) {
        errorMessage.value = '조회 실패: ' + e.message;
    } finally {
        loading.value = false;
    }
}

function resetSearch() {
    keyword.value = '';
    loadProducts();
}

async function createProduct() {
    // 신규 상품은 판매중 상태로 등록한다.
    await apiPost('/products', {
        name: form.value.name,
        price: Number(form.value.price),
        stockQuantity: Number(form.value.stockQuantity),
        status: 'ON_SALE',
        description: form.value.description
    });
    form.value = { name: '', price: '', stockQuantity: '', description: '' };
    loadProducts();
}

async function deleteProduct(id) {
    if (!confirm('삭제하시겠습니까?')) return;
    await apiDelete(`/products/${id}`);
    loadProducts();
}

onMounted(loadProducts);
</script>

<template>
  <div class="container">
    <h1 class="page-title">상품 관리</h1>
    <p class="page-desc">상품을 검색하고 등록·삭제할 수 있습니다.</p>

    <!-- 상품 검색 -->
    <div class="card">
      <h2>상품 검색</h2>
      <form class="form-row" @submit.prevent="loadProducts">
        <input type="text" v-model.trim="keyword" placeholder="상품명 검색">
        <button type="submit" class="btn btn-primary">검색</button>
        <button type="button" class="btn btn-outline" @click="resetSearch">전체 보기</button>
      </form>
    </div>

    <!-- 상품 등록 -->
    <div class="card">
      <h2>상품 등록</h2>
      <form class="form-row" @submit.prevent="createProduct">
        <input type="text" v-model="form.name" placeholder="상품명" required>
        <input type="number" v-model="form.price" placeholder="가격" required min="0">
        <input type="number" v-model="form.stockQuantity" placeholder="재고 수량" required min="0">
        <input type="text" v-model="form.description" placeholder="상품 설명">
        <button type="submit" class="btn btn-primary">등록</button>
      </form>
    </div>

    <!-- 상품 목록 -->
    <div class="card">
      <h2>상품 목록</h2>
      <table>
        <thead>
          <tr>
            <th>ID</th><th>상품명</th><th>가격</th><th>재고</th><th>상태</th><th>설명</th><th>관리</th>
          </tr>
        </thead>
        <tbody>
          <tr v-if="loading" class="empty-row"><td colspan="7">불러오는 중...</td></tr>
          <tr v-else-if="errorMessage" class="empty-row"><td colspan="7">{{ errorMessage }}</td></tr>
          <tr v-else-if="products.length === 0" class="empty-row"><td colspan="7">등록된 상품이 없습니다.</td></tr>
          <template v-else>
            <tr v-for="p in products" :key="p.id">
              <td>{{ p.id }}</td>
              <td>{{ p.name }}</td>
              <td>{{ formatWon(p.price) }}</td>
              <td>{{ p.stockQuantity }}</td>
              <td>{{ p.status }}</td>
              <td>{{ p.description }}</td>
              <td><button class="btn-delete" @click="deleteProduct(p.id)">삭제</button></td>
            </tr>
          </template>
        </tbody>
      </table>
    </div>
  </div>
</template>
