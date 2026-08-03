// 상품 관리 화면 로직 — 01.spring-backend-v1.0의 /api/products 호출

async function loadProducts(keyword) {
    const tbody = document.getElementById('productBody');
    tbody.innerHTML = '<tr class="empty-row"><td colspan="7">불러오는 중...</td></tr>';
    try {
        let products = await apiGet('/products');
        // 검색 API가 따로 없으므로, 전체 목록을 받아온 뒤 이름으로 걸러낸다.
        if (keyword) {
            products = products.filter(p => p.name.includes(keyword));
        }
        if (products.length === 0) {
            tbody.innerHTML = '<tr class="empty-row"><td colspan="7">등록된 상품이 없습니다.</td></tr>';
            return;
        }
        tbody.innerHTML = products.map(p => `
            <tr>
                <td>${p.id}</td>
                <td>${p.name}</td>
                <td>${formatWon(p.price)}</td>
                <td>${p.stockQuantity}</td>
                <td>${p.status}</td>
                <td>${p.description ?? ''}</td>
                <td><button class="btn-delete" onclick="deleteProduct(${p.id})">삭제</button></td>
            </tr>
        `).join('');
    } catch (e) {
        tbody.innerHTML = `<tr class="empty-row"><td colspan="7">조회 실패: ${e.message}</td></tr>`;
    }
}

async function deleteProduct(id) {
    if (!confirm('삭제하시겠습니까?')) return;
    await apiDelete(`/products/${id}`);
    loadProducts();
}

document.getElementById('searchForm').addEventListener('submit', (e) => {
    e.preventDefault();
    loadProducts(document.getElementById('searchKeyword').value.trim());
});

document.getElementById('resetBtn').addEventListener('click', () => {
    document.getElementById('searchKeyword').value = '';
    loadProducts();
});

document.getElementById('createForm').addEventListener('submit', async (e) => {
    e.preventDefault();
    const form = e.target;
    // 신규 상품은 판매중 상태로 등록한다.
    await apiPost('/products', {
        name: form.name.value,
        price: Number(form.price.value),
        stockQuantity: Number(form.stockQuantity.value),
        status: 'ON_SALE',
        description: form.description.value
    });
    form.reset();
    loadProducts();
});

loadProducts();
