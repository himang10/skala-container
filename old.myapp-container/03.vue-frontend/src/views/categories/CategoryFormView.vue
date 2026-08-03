<template>
  <div>
    <div class="page-header">
      <nav aria-label="breadcrumb">
        <ol class="breadcrumb">
          <li class="breadcrumb-item"><RouterLink to="/categories">카테고리 관리</RouterLink></li>
          <li class="breadcrumb-item active">{{ isNew ? '신규 등록' : '수정' }}</li>
        </ol>
      </nav>
      <h2><i class="bi bi-grid me-2"></i>{{ isNew ? '카테고리 등록' : '카테고리 수정' }}</h2>
    </div>

    <div v-if="loadError" class="alert alert-danger">{{ loadError }}</div>

    <div v-else class="row justify-content-center">
      <div class="col-md-6">
        <div class="card">
          <div class="card-header bg-white py-3">
            <h5 class="mb-0">
              <i class="bi bi-tag-fill me-2 text-success"></i>
              {{ isNew ? '새 카테고리 정보 입력' : '카테고리 정보 수정' }}
            </h5>
          </div>
          <div class="card-body p-4">
            <form @submit.prevent="submit">
              <div class="mb-4">
                <label class="form-label fw-semibold">
                  카테고리명 <span class="text-danger">*</span>
                </label>
                <div class="input-group">
                  <span class="input-group-text bg-success text-white">
                    <i class="bi bi-tag"></i>
                  </span>
                  <input
                    v-model="form.name"
                    type="text"
                    class="form-control"
                    placeholder="예: 전자제품"
                    required
                    maxlength="50"
                  />
                </div>
                <div class="form-text">카테고리명은 중복될 수 없습니다.</div>
              </div>
              <div class="mb-4">
                <label class="form-label fw-semibold">설명</label>
                <textarea
                  v-model="form.description"
                  class="form-control"
                  rows="4"
                  placeholder="카테고리에 대한 간단한 설명을 입력하세요."
                ></textarea>
              </div>
              <div class="d-flex gap-2">
                <button type="submit" class="btn btn-success flex-grow-1" :disabled="saving">
                  <i class="bi bi-check-circle me-1"></i>{{ isNew ? '등록' : '저장' }}
                </button>
                <RouterLink to="/categories" class="btn btn-outline-secondary flex-grow-1">
                  <i class="bi bi-x-circle me-1"></i>취소
                </RouterLink>
              </div>
            </form>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, inject } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { categoryApi } from '@/api'

const route = useRoute()
const router = useRouter()
const toast = inject('toast')

const id = computed(() => route.params.id)
const isNew = computed(() => !id.value)

const form = ref({ name: '', description: '' })
const saving = ref(false)
const loadError = ref(null)

onMounted(async () => {
  if (!isNew.value) {
    try {
      const cat = await categoryApi.getById(id.value)
      form.value = { name: cat.name, description: cat.description || '' }
    } catch (e) {
      loadError.value = '카테고리 조회 실패: ' + e.message
    }
  }
})

async function submit() {
  saving.value = true
  try {
    if (isNew.value) {
      await categoryApi.create(form.value)
      toast('카테고리가 등록되었습니다.')
    } else {
      await categoryApi.update(id.value, form.value)
      toast('카테고리가 수정되었습니다.')
    }
    router.push('/categories')
  } catch (e) {
    toast('저장 실패: ' + e.message, 'danger')
  } finally {
    saving.value = false
  }
}
</script>
