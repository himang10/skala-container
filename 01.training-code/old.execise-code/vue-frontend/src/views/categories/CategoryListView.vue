<template>
  <div>
    <div class="page-header d-flex justify-content-between align-items-center">
      <div>
        <h2><i class="bi bi-grid me-2"></i>카테고리 관리</h2>
        <p class="text-muted mb-0">
          전체 <span class="fw-bold text-success">{{ categories.length }}</span>개의 카테고리
        </p>
      </div>
      <RouterLink to="/categories/new" class="btn btn-success">
        <i class="bi bi-plus-circle me-1"></i>카테고리 등록
      </RouterLink>
    </div>

    <div v-if="loading" class="text-center py-5">
      <div class="spinner-border text-success" role="status"></div>
    </div>
    <div v-else-if="error" class="alert alert-danger">{{ error }}</div>

    <div v-else class="row g-4">
      <div v-if="categories.length === 0" class="col-12">
        <div class="card text-center py-5">
          <div class="card-body">
            <i class="bi bi-inbox fs-1 text-muted d-block mb-3"></i>
            <p class="text-muted">등록된 카테고리가 없습니다.</p>
            <RouterLink to="/categories/new" class="btn btn-success mt-2">
              <i class="bi bi-plus-circle me-1"></i>첫 카테고리 등록
            </RouterLink>
          </div>
        </div>
      </div>
      <div v-for="c in categories" :key="c.id" class="col-md-4 col-lg-3">
        <div class="card h-100">
          <div class="card-body">
            <div class="d-flex justify-content-between align-items-start mb-2">
              <div
                class="rounded-circle bg-success d-flex align-items-center justify-content-center text-white"
                style="width: 40px; height: 40px"
              >
                <i class="bi bi-tag"></i>
              </div>
              <span class="badge bg-light text-dark border">{{ c.productCount || 0 }}개 상품</span>
            </div>
            <h5 class="card-title mt-2 fw-bold">{{ c.name }}</h5>
            <p class="card-text text-muted small">{{ c.description || '설명 없음' }}</p>
          </div>
          <div class="card-footer bg-white border-top-0 d-flex gap-2">
            <RouterLink
              :to="`/categories/${c.id}/edit`"
              class="btn btn-sm btn-outline-success flex-grow-1"
            >
              <i class="bi bi-pencil me-1"></i>수정
            </RouterLink>
            <button
              class="btn btn-sm btn-outline-danger flex-grow-1"
              @click="deleteCategory(c.id)"
            >
              <i class="bi bi-trash me-1"></i>삭제
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, inject } from 'vue'
import { categoryApi } from '@/api'

const toast = inject('toast')
const categories = ref([])
const loading = ref(true)
const error = ref(null)

onMounted(async () => {
  await loadCategories()
})

async function loadCategories() {
  loading.value = true
  error.value = null
  try {
    categories.value = await categoryApi.getAll()
  } catch (e) {
    error.value = '로딩 실패: ' + e.message
  } finally {
    loading.value = false
  }
}

async function deleteCategory(id) {
  if (!confirm('카테고리를 삭제하시겠습니까?\n(상품이 있으면 삭제할 수 없습니다)')) return
  try {
    await categoryApi.remove(id)
    toast('카테고리가 삭제되었습니다.')
    await loadCategories()
  } catch (e) {
    toast('삭제 실패: ' + e.message, 'danger')
  }
}
</script>
