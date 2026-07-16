<template>
  <div>
    <div class="page-header">
      <nav aria-label="breadcrumb">
        <ol class="breadcrumb">
          <li class="breadcrumb-item"><RouterLink to="/users">사용자 관리</RouterLink></li>
          <li class="breadcrumb-item active">{{ isNew ? '신규 등록' : '정보 수정' }}</li>
        </ol>
      </nav>
      <h2>{{ isNew ? '사용자 등록' : '사용자 정보 수정' }}</h2>
    </div>

    <div v-if="loadError" class="alert alert-danger">{{ loadError }}</div>

    <div v-else class="row justify-content-center">
      <div class="col-md-6">
        <div class="card">
          <div class="card-header bg-white py-3">
            <h5 class="mb-0">
              <i class="bi bi-person-fill me-2 text-primary"></i>
              {{ isNew ? '새 사용자 정보 입력' : '사용자 정보 수정' }}
            </h5>
          </div>
          <div class="card-body p-4">
            <form @submit.prevent="submit">
              <div class="mb-4">
                <label class="form-label fw-semibold">이름 <span class="text-danger">*</span></label>
                <div class="input-group">
                  <span class="input-group-text"><i class="bi bi-person"></i></span>
                  <input
                    v-model="form.name"
                    type="text"
                    class="form-control"
                    placeholder="홍길동"
                    required
                    maxlength="50"
                  />
                </div>
              </div>
              <div class="mb-4">
                <label class="form-label fw-semibold">이메일 <span class="text-danger">*</span></label>
                <div class="input-group">
                  <span class="input-group-text"><i class="bi bi-envelope"></i></span>
                  <input
                    v-model="form.email"
                    type="email"
                    class="form-control"
                    placeholder="example@email.com"
                    required
                  />
                </div>
              </div>
              <div class="d-flex gap-2">
                <button type="submit" class="btn btn-primary flex-grow-1" :disabled="saving">
                  <i class="bi bi-check-circle me-1"></i>{{ isNew ? '등록' : '저장' }}
                </button>
                <RouterLink to="/users" class="btn btn-outline-secondary flex-grow-1">
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
import { userApi } from '@/api'

const route = useRoute()
const router = useRouter()
const toast = inject('toast')

const id = computed(() => route.params.id)
const isNew = computed(() => !id.value)

const form = ref({ name: '', email: '' })
const saving = ref(false)
const loadError = ref(null)

onMounted(async () => {
  if (!isNew.value) {
    try {
      const user = await userApi.getById(id.value)
      form.value = { name: user.name, email: user.email }
    } catch (e) {
      loadError.value = '사용자 조회 실패: ' + e.message
    }
  }
})

async function submit() {
  saving.value = true
  try {
    if (isNew.value) {
      await userApi.create(form.value)
      toast('사용자가 등록되었습니다.')
    } else {
      await userApi.update(id.value, form.value)
      toast('사용자 정보가 수정되었습니다.')
    }
    router.push('/users')
  } catch (e) {
    toast('저장 실패: ' + e.message, 'danger')
  } finally {
    saving.value = false
  }
}
</script>
