// 사용자 관리 화면 로직 — 01.spring-backend-v1.0의 /api/users 호출

async function loadUsers(keyword) {
    const tbody = document.getElementById('userBody');
    tbody.innerHTML = '<tr class="empty-row"><td colspan="4">불러오는 중...</td></tr>';
    try {
        let users = await apiGet('/users');
        if (keyword) {
            users = users.filter(u => u.name.includes(keyword));
        }
        if (users.length === 0) {
            tbody.innerHTML = '<tr class="empty-row"><td colspan="4">등록된 사용자가 없습니다.</td></tr>';
            return;
        }
        tbody.innerHTML = users.map(u => `
            <tr>
                <td>${u.id}</td>
                <td>${u.name}</td>
                <td>${u.email}</td>
                <td><button class="btn-delete" onclick="deleteUser(${u.id})">삭제</button></td>
            </tr>
        `).join('');
    } catch (e) {
        tbody.innerHTML = `<tr class="empty-row"><td colspan="4">조회 실패: ${e.message}</td></tr>`;
    }
}

async function deleteUser(id) {
    if (!confirm('삭제하시겠습니까?')) return;
    await apiDelete(`/users/${id}`);
    loadUsers();
}

document.getElementById('searchForm').addEventListener('submit', (e) => {
    e.preventDefault();
    loadUsers(document.getElementById('searchKeyword').value.trim());
});

document.getElementById('resetBtn').addEventListener('click', () => {
    document.getElementById('searchKeyword').value = '';
    loadUsers();
});

document.getElementById('createForm').addEventListener('submit', async (e) => {
    e.preventDefault();
    const form = e.target;
    await apiPost('/users', {
        name: form.name.value,
        email: form.email.value
    });
    form.reset();
    loadUsers();
});

loadUsers();
