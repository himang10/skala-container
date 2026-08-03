<template>
  <div>
    <div class="page-header">
      <nav aria-label="breadcrumb">
        <ol class="breadcrumb">
          <li class="breadcrumb-item"><RouterLink to="/orders">주문 관리</RouterLink></li>
          <li class="breadcrumb-item active">새 주문</li>
        </ol>
      </nav>
      <h2><i class="bi bi-bag-plus me-2"></i>주문 생성</h2>
    </div>

    <div v-if="loading" class="text-center py-5">
      <div class="spinner-border text-warning" role="status"></div>
    </div>
    <div v-else-if="loadError" class="alert alert-danger">{{ loadError }}</div>

    <div v-else class="row g-4">
      <div class="col-lg-8">
        <div class="card">
          <div class="card-header bg-white py-3">
            <h5 class="mb-0"><i class="bi bi-cart me-2 text-warning"></i>주문 정보 입력</h5>
          </div>
          <div class="card-body p-4">
            <form @submit.prevent="submit">
              <!-- 주문자 선택 -->
              <div class="mb-4">
                <label class="form-label fw-semibold">주문자 <span class="text-danger">*</span></label>
                <select v-model="form.userId" class="form-select" required>
                  <option value="">-- 주문자를 선택하세요 --</option>
                  <option v-for="u in users" :key="u.id" :value="u.id">
                    {{ u.name }} ({{ u.email }})
                  </option>
                </select>
              </div>

              <!-- 주문 항목 -->
              <div class="mb-3">
                <div class="d-flex justify-content-between align-items-center mb-2">
                  <label class="form-label fw-semibold mb-0">
                    주문 항목 <span class="text-danger">*</span>
                  </label>
                  <button type="button" class="btn btn-sm btn-outline-warning" @click="addItem">
                    <i class="bi bi-plus-circle me-1"></i>항목 추가
                  </button>
                </div>
                <div
                  v-for="(item, idx) in form.items"
                  :key="idx"
                  class="border rounded p-3 mb-2 bg-light"
                >
                  <div class="row g-2 align-items-center">
                    <div class="col-7">
                      <select v-model="item.productId" class="form-select form-select-sm" required>
                        <option value="">-- 상품 선택 --</option>
                        <option v-for="p in products" :key="p.id" :value="p.id">
                          {{ p.name }} ({{ fmt(p.price) }}원, 재고:{{ p.stockQuantity }})
                        </option>
                      </select>
                    </div>
                    <div class="col-3">
                      <div class="input-group input-group-sm">
                        <input
                          v-model.number="item.quantity"
                          type="number"
                          class="form-control"
                          min="1"
                          required
                        />
                        <span class="input-group-text">개</span>
                      </div>
                    </div>
                    <div class="col-2">
                      <button
                        v-if="form.items.length > 1"
                        type="button"
                        class="btn btn-sm btn-outline-danger w-100"
                        @click="removeItem(idx)"
                      >
                        <i class="bi bi-trash"></i>
                      </button>
                    </div>
                  </div>
                </div>
              </div>

              <div class="d-flex gap-2 mt-4">
                <button type="submit" class="btn btn-warning text-dark flex-grow-1 fw-semibold" :disabled="saving">
                  <i class="bi bi-bag-check me-1"></i>주문 생성
                </button>
                <RouterLink to="/orders" class="btn btn-outline-secondary flex-grow-1">
                  <i class="bi bi-x-circle me-1"></i>취소
                </RouterLink>
              </div>
            </form>
          </div>
        </div>
      </div>

      <!-- 안내 카드 -->
      <div class="col-lg-4">
        <div class="card border-warning">
          <div class="card-header bg-warning bg-opacity-10">
            <h6 class="mb-0 fw-bold text-warning">
              <i class="bi bi-info-circle me-2"></i>주문 안내
            </h6>
          </div>
          <div class="card-body">
            <ul class="list-unstyled mb-0 small text-muted">
              <li class="mb-2"><i class="bi bi-dot text-warning"></i>주문자와 최소 1개 이상의 상품을 선택하세요.</li>
              <li class="mb-2"><i class="bi bi-dot text-warning"></i>재고가 충분한 상품만 주문 가능합니다.</li>
              <li class="mb-2"><i class="bi bi-dot text-warning"></i>주문 생성 후 <strong>PENDING</strong> 상태로 시작됩니다.</li>
              <li class="mb-2"><i class="bi bi-dot text-warning"></i>상태 전환: PENDING → CONFIRMED → SHIPPED → DELIVERED</li>
              <li><i class="bi bi-dot text-warning"></i>취소는 PENDING 상태에서만 가능합니다.</li>
            </ul>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, inject } from 'vue'
import { useRouter } from 'vue-router'
import { userApi, productApi, orderApi } from '@/api'

const router = useRouter()
const toast = inject('toast')

const users = ref([])
const products = ref([])
const loading = ref(true)
const loadError = ref(null)
const saving = ref(false)

const form = ref({
  userId: '',
  items: [{ productId: '', quantity: 1 }]
})

onMounted(async () => {
  try {
    const [u, p] = await Promise.all([userApi.getAll(), productApi.getAll()])
    users.value = u
    products.value = p
  } catch (e) {
    loadError.value = '로딩 실패: ' + e.message
  } finally {
    loading.value = false
  }
})

function addItem() {
  form.value.items.push({ productId: '', quantity: 1 })
}

function removeItem(idx) {
  form.value.items.splice(idx, 1)
}

async function submit() {
  if (!form.value.userId) {
    toast('주문자를 선택하세요.', 'warning')
    return
  }
  const validItems = form.value.items.filter((i) => i.productId && i.quantity > 0)
  if (validItems.length === 0) {
    toast('주문 항목을 추가하세요.', 'warning')
    return
  }
  saving.value = true
  try {
    await orderApi.create({
      userId: Number(form.value.userId),
      items: validItems.map((i) => ({ productId: Number(i.productId), quantity: i.quantity }))
    })
    toast('주문이 생성되었습니다.')
    router.push('/orders')
  } catch (e) {
    toast('주문 생성 실패: ' + e.message, 'danger')
  } finally {
    saving.value = false
  }
}

function fmt(n) {
  return Number(n).toLocaleString('ko-KR')
}
</script>
