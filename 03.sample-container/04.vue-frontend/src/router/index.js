import { createRouter, createWebHistory } from 'vue-router';
import Home from '../views/Home.vue';
import Products from '../views/Products.vue';
import Users from '../views/Users.vue';
import Orders from '../views/Orders.vue';
import OrderNew from '../views/OrderNew.vue';
import System from '../views/System.vue';

// history 모드(주소창에 #이 붙지 않는 방식)를 쓰므로,
// 새로고침 시에도 라우트가 살아있으려면 nginx가 알 수 없는 경로를 index.html로 돌려줘야 한다.
// (default.conf의 "try_files $uri /index.html;" 참고)
const router = createRouter({
    history: createWebHistory(),
    linkActiveClass: 'active', // 03.frontend의 CSS(.topbar nav a.active)를 그대로 재사용하기 위한 설정
    routes: [
        { path: '/', name: 'home', component: Home },
        { path: '/products', name: 'products', component: Products },
        { path: '/users', name: 'users', component: Users },
        { path: '/orders', name: 'orders', component: Orders },
        { path: '/orders/new', name: 'order-new', component: OrderNew },
        { path: '/system', name: 'system', component: System }
    ]
});

export default router;
