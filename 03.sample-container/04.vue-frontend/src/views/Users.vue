<script setup>
import { ref, onMounted } from 'vue';
import { apiGet, apiPost, apiDelete } from '../api.js';

const users = ref([]);
const loading = ref(true);
const errorMessage = ref('');
const keyword = ref('');

const form = ref({ name: '', email: '' });

async function loadUsers() {
    loading.value = true;
    errorMessage.value = '';
    try {
        let list = await apiGet('/users');
        if (keyword.value) {
            list = list.filter((u) => u.name.includes(keyword.value));
        }
        users.value = list;
    } catch (e) {
        errorMessage.value = '조회 실패: ' + e.message;
    } finally {
        loading.value = false;
    }
}

function resetSearch() {
    keyword.value = '';
    loadUsers();
}

async function createUser() {
    await apiPost('/users', { name: form.value.name, email: form.value.email });
    form.value = { name: '', email: '' };
    loadUsers();
}

async function deleteUser(id) {
    if (!confirm('삭제하시겠습니까?')) return;
    await apiDelete(`/users/${id}`);
    loadUsers();
}

onMounted(loadUsers);
</script>

<template>
  <div class="container">
    <h1 class="page-title">사용자 관리</h1>
    <p class="page-desc">사용자를 검색하고 등록·삭제할 수 있습니다.</p>

    <!-- 사용자 검색 -->
    <div class="card">
      <h2>사용자 검색</h2>
      <form class="form-row" @submit.prevent="loadUsers">
        <input type="text" v-model.trim="keyword" placeholder="이름 검색">
        <button type="submit" class="btn btn-primary">검색</button>
        <button type="button" class="btn btn-outline" @click="resetSearch">전체 보기</button>
      </form>
    </div>

    <!-- 사용자 등록 -->
    <div class="card">
      <h2>사용자 등록</h2>
      <form class="form-row" @submit.prevent="createUser">
        <input type="text" v-model="form.name" placeholder="이름" required>
        <input type="email" v-model="form.email" placeholder="이메일" required>
        <button type="submit" class="btn btn-primary">등록</button>
      </form>
    </div>

    <!-- 사용자 목록 -->
    <div class="card">
      <h2>사용자 목록</h2>
      <table>
        <thead>
          <tr><th>ID</th><th>이름</th><th>이메일</th><th>관리</th></tr>
        </thead>
        <tbody>
          <tr v-if="loading" class="empty-row"><td colspan="4">불러오는 중...</td></tr>
          <tr v-else-if="errorMessage" class="empty-row"><td colspan="4">{{ errorMessage }}</td></tr>
          <tr v-else-if="users.length === 0" class="empty-row"><td colspan="4">등록된 사용자가 없습니다.</td></tr>
          <template v-else>
            <tr v-for="u in users" :key="u.id">
              <td>{{ u.id }}</td>
              <td>{{ u.name }}</td>
              <td>{{ u.email }}</td>
              <td><button class="btn-delete" @click="deleteUser(u.id)">삭제</button></td>
            </tr>
          </template>
        </tbody>
      </table>
    </div>
  </div>
</template>
