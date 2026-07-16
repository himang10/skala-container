<template>
  <div>
    <div class="page-header d-flex justify-content-between align-items-center">
      <div>
        <h2><i class="bi bi-people me-2"></i>사용자 관리</h2>
        <p class="text-muted mb-0">
          전체 <span class="fw-bold text-primary">{{ users.length }}</span>명의 사용자
        </p>
      </div>
      <RouterLink to="/users/new" class="btn btn-primary">
        <i class="bi bi-person-plus me-1"></i>사용자 등록
      </RouterLink>
    </div>

    <div v-if="loading" class="text-center py-5">
      <div class="spinner-border text-primary" role="status"></div>
    </div>
    <div v-else-if="error" class="alert alert-danger">{{ error }}</div>
    <div v-else class="card">
      <div class="card-body p-0">
        <div class="table-responsive">
          <table class="table table-hover mb-0">
            <thead>
              <tr>
                <th style="width: 80px">ID</th>
                <th>이름</th>
                <th>이메일</th>
                <th style="width: 160px" class="text-center">작업</th>
              </tr>
            </thead>
            <tbody>
              <tr v-if="users.length === 0">
                <td colspan="4" class="text-center text-muted py-5">
                  <i class="bi bi-inbox fs-1 d-block mb-2"></i>등록된 사용자가 없습니다.
                </td>
              </tr>
              <tr v-for="u in users" :key="u.id">
                <td><span class="badge bg-secondary">{{ u.id }}</span></td>
                <td>
                  <div class="d-flex align-items-center gap-2">
                    <div
                      class="rounded-circle bg-primary d-flex align-items-center justify-content-center text-white fw-bold"
                      style="width: 32px; height: 32px; font-size: 0.8rem"
                    >
                      {{ u.name.charAt(0) }}
                    </div>
                    <span class="fw-semibold">{{ u.name }}</span>
                  </div>
                </td>
                <td><span class="text-muted">{{ u.email }}</span></td>
                <td class="text-center">
                  <RouterLink
                    :to="`/users/${u.id}/edit`"
                    class="btn btn-sm btn-outline-primary me-1"
                  >
                    <i class="bi bi-pencil"></i> 수정
                  </RouterLink>
                  <button class="btn btn-sm btn-outline-danger" @click="deleteUser(u.id)">
                    <i class="bi bi-trash"></i> 삭제
                  </button>
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
import { ref, onMounted, inject } from 'vue'
import { userApi } from '@/api'

const toast = inject('toast')
const users = ref([])
const loading = ref(true)
const error = ref(null)

onMounted(async () => {
  await loadUsers()
})

async function loadUsers() {
  loading.value = true
  error.value = null
  try {
    users.value = await userApi.getAll()
  } catch (e) {
    error.value = '로딩 실패: ' + e.message
  } finally {
    loading.value = false
  }
}

async function deleteUser(id) {
  if (!confirm('정말 삭제하시겠습니까?')) return
  try {
    await userApi.remove(id)
    toast('사용자가 삭제되었습니다.')
    await loadUsers()
  } catch (e) {
    toast('삭제 실패: ' + e.message, 'danger')
  }
}
</script>
