// 01.spring-backend-v1.0 REST API 호출 공통 헬퍼
// "/api"로 시작하는 요청은 nginx(default.conf) 또는 k8s Ingress가 backend로 그대로 전달(proxy)한다.
// 그래서 이 파일에서는 백엔드의 실제 주소를 몰라도 되고, 같은 오리진(origin)으로만 호출하면 된다.
const API_BASE = '/api';

export async function apiGet(path) {
    const res = await fetch(API_BASE + path);
    if (!res.ok) throw new Error('HTTP ' + res.status);
    return res.json();
}

export async function apiPost(path, body) {
    const res = await fetch(API_BASE + path, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(body)
    });
    if (!res.ok) throw new Error('HTTP ' + res.status);
    return res.status === 204 ? null : res.json();
}

export async function apiDelete(path) {
    const res = await fetch(API_BASE + path, { method: 'DELETE' });
    if (!res.ok) throw new Error('HTTP ' + res.status);
}

export function formatWon(n) {
    return Number(n).toLocaleString('ko-KR') + '원';
}

export function formatDateTime(iso) {
    return iso ? iso.substring(0, 16).replace('T', ' ') : '-';
}
