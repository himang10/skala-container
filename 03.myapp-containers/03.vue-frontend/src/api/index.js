import axios from 'axios'

const http = axios.create({
  baseURL: '/api',
  headers: { 'Content-Type': 'application/json' }
})

// 응답 인터셉터 — ApiResponse 래퍼 처리
http.interceptors.response.use(
  (response) => response,
  (error) => {
    const msg = error.response?.data?.message || `HTTP ${error.response?.status}`
    return Promise.reject(new Error(msg))
  }
)

// 백엔드 ApiResponse 래퍼에서 data 필드 추출
function unwrap(res) {
  return res.data?.data !== undefined ? res.data.data : res.data
}

export const userApi = {
  getAll: () => http.get('/users').then((r) => r.data),
  getById: (id) => http.get(`/users/${id}`).then((r) => r.data),
  create: (body) => http.post('/users', body).then((r) => r.data),
  update: (id, body) => http.put(`/users/${id}`, body).then((r) => r.data),
  remove: (id) => http.delete(`/users/${id}`)
}

export const categoryApi = {
  getAll: () => http.get('/categories').then(unwrap),
  getById: (id) => http.get(`/categories/${id}`).then(unwrap),
  create: (body) => http.post('/categories', body).then(unwrap),
  update: (id, body) => http.put(`/categories/${id}`, body).then(unwrap),
  remove: (id) => http.delete(`/categories/${id}`)
}

export const orderApi = {
  getByUserId: (userId) => http.get(`/orders?userId=${userId}`).then(unwrap),
  getById: (id) => http.get(`/orders/${id}`).then(unwrap),
  create: (body) => http.post('/orders', body).then(unwrap),
  updateStatus: (id, status) => http.put(`/orders/${id}/status`, { status }).then(unwrap)
}

export const productApi = {
  getAll: () => http.get('/products').then((r) => r.data)
}
