// 시스템 정보 화면 로직 — 브라우저가 02.fastapi-backend-v2.0을 "직접" 호출한다.
// (01과 달리 nginx 프록시를 거치지 않으므로 CORS 설정이 필요하다.)
//
// 실습 환경에 맞게 아래 주소만 바꾸면 된다.
const BACKEND_V2_URL = 'http://localhost:8082';

document.getElementById('backendV2UrlText').textContent = BACKEND_V2_URL;

document.getElementById('loadInfoBtn').addEventListener('click', async () => {
    try {
        const [hostnameRes, podIpRes, liveRes, readyRes] = await Promise.all([
            fetch(BACKEND_V2_URL + '/python/hostname').then(r => r.json()),
            fetch(BACKEND_V2_URL + '/python/pod-ip').then(r => r.json()),
            fetch(BACKEND_V2_URL + '/python/health/liveness').then(r => r.json()),
            fetch(BACKEND_V2_URL + '/python/health/readiness').then(r => r.json())
        ]);
        document.getElementById('infoHostname').textContent = hostnameRes.hostname;
        document.getElementById('infoPodIp').textContent = podIpRes.pod_ip;
        document.getElementById('infoLiveness').textContent = liveRes.status;
        document.getElementById('infoReadiness').textContent = readyRes.status;
    } catch (e) {
        alert('02 서버 호출 실패: ' + e.message + ' (CORS 설정 또는 02 서버 실행 여부를 확인하세요)');
    }
});

document.getElementById('loadUsersBtn').addEventListener('click', async () => {
    const tbody = document.getElementById('usersBody');
    try {
        const users = await fetch(BACKEND_V2_URL + '/api/users').then(r => r.json());
        tbody.innerHTML = users.map(u =>
            `<tr><td>${u.id}</td><td>${u.name}</td><td>${u.email}</td></tr>`
        ).join('');
    } catch (e) {
        tbody.innerHTML = '<tr class="empty-row"><td colspan="3">조회 실패: ' + e.message + '</td></tr>';
    }
});
