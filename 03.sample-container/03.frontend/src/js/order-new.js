// 주문 - 사용자 선택 화면 로직 — /api/products/{id}, /api/users, /api/orders 호출

const productId = new URLSearchParams(window.location.search).get('productId');

async function loadProduct() {
    const tbody = document.getElementById('productBody');
    try {
        const product = await apiGet(`/products/${productId}`);
        tbody.innerHTML = `
            <tr>
                <td>${product.name}</td>
                <td>${formatWon(product.price)}</td>
                <td>${product.stockQuantity}</td>
            </tr>
        `;
    } catch (e) {
        tbody.innerHTML = `<tr class="empty-row"><td colspan="3">상품 조회 실패: ${e.message}</td></tr>`;
    }
}

async function loadUserOptions() {
    const select = document.getElementById('userId');
    try {
        const users = await apiGet('/users');
        users.forEach(u => {
            const option = document.createElement('option');
            option.value = u.id;
            option.textContent = `${u.name} (${u.email})`;
            select.appendChild(option);
        });
    } catch (e) {
        alert('사용자 목록 조회 실패: ' + e.message);
    }
}

document.getElementById('orderForm').addEventListener('submit', async (e) => {
    e.preventDefault();
    const form = e.target;
    try {
        await apiPost('/orders', {
            productId: Number(productId),
            userId: Number(form.userId.value),
            quantity: Number(form.quantity.value),
            address: form.address.value
        });
        window.location.href = '/orders.html';
    } catch (err) {
        alert('주문 생성 실패: ' + err.message);
    }
});

if (!productId) {
    alert('상품이 지정되지 않았습니다. 주문 관리 화면에서 다시 시도하세요.');
    window.location.href = '/orders.html';
} else {
    loadProduct();
    loadUserOptions();
}
