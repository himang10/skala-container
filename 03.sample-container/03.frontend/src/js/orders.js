// 주문 관리 화면 로직 — 01.spring-backend-v1.0의 /api/products, /api/orders 호출

async function loadProductsForOrder() {
    const tbody = document.getElementById('productBody');
    try {
        const products = await apiGet('/products');
        if (products.length === 0) {
            tbody.innerHTML = '<tr class="empty-row"><td colspan="5">등록된 상품이 없습니다. 상품 관리에서 먼저 등록하세요.</td></tr>';
            return;
        }
        tbody.innerHTML = products.map(p => `
            <tr>
                <td>${p.id}</td>
                <td>${p.name}</td>
                <td>${formatWon(p.price)}</td>
                <td>${p.stockQuantity}</td>
                <td><a class="btn btn-primary btn-sm" href="/order-new.html?productId=${p.id}">주문</a></td>
            </tr>
        `).join('');
    } catch (e) {
        tbody.innerHTML = `<tr class="empty-row"><td colspan="5">조회 실패: ${e.message}</td></tr>`;
    }
}

async function loadOrders() {
    const tbody = document.getElementById('orderBody');
    try {
        const orders = await apiGet('/orders');
        if (orders.length === 0) {
            tbody.innerHTML = '<tr class="empty-row"><td colspan="7">주문이 없습니다.</td></tr>';
            return;
        }
        tbody.innerHTML = orders.map(o => `
            <tr>
                <td>${o.id}</td>
                <td>${o.userName}</td>
                <td>${o.productName}</td>
                <td>${o.quantity}</td>
                <td>${formatWon(o.totalAmount)}</td>
                <td>${formatDateTime(o.orderedAt)}</td>
                <td>
                    ${o.status === 'DELIVERY_COMPLETED'
                        ? `<span class="badge badge-done">${o.statusLabel}</span>`
                        : `<button class="btn btn-primary btn-sm" onclick="advanceStatus(${o.id})">${o.statusLabel}</button>`}
                </td>
            </tr>
        `).join('');
    } catch (e) {
        tbody.innerHTML = `<tr class="empty-row"><td colspan="7">조회 실패: ${e.message}</td></tr>`;
    }
}

async function advanceStatus(id) {
    await apiPost(`/orders/${id}/next-status`);
    loadOrders();
}

loadProductsForOrder();
loadOrders();
