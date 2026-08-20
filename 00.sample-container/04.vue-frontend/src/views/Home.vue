<template>
  <div class="container">
    <div class="hero">
      <h1>상품부터 배송까지,<br><span class="gradient">주문 관리 시스템</span></h1>
      <p>주문 완료 → 결제 대기/완료 → 배송 대기/완료 흐름을 직접 실습해 보세요.</p>
      <p class="hero-note">
        이 화면(04.vue-frontend)은 Vue.js SPA로 만들어져 있으며,
        브라우저에서 <code>/api</code>를 통해 01.spring-backend-v1.0을 호출합니다.
      </p>
    </div>

    <div class="menu-grid">
      <RouterLink class="menu-card" to="/products">
        <div class="num">01</div>
        <h3>상품 관리</h3>
        <p>상품을 검색하고 등록·삭제합니다.</p>
        <span class="go">바로가기 &rarr;</span>
      </RouterLink>
      <RouterLink class="menu-card" to="/users">
        <div class="num">02</div>
        <h3>사용자 관리</h3>
        <p>사용자를 검색하고 등록·삭제합니다.</p>
        <span class="go">바로가기 &rarr;</span>
      </RouterLink>
      <RouterLink class="menu-card" to="/orders">
        <div class="num">03</div>
        <h3>주문 관리</h3>
        <p>상품을 주문하고 결제·배송 상태를 전환합니다.</p>
        <span class="go">바로가기 &rarr;</span>
      </RouterLink>
      <RouterLink class="menu-card" to="/system">
        <div class="num">04</div>
        <h3>시스템 정보</h3>
        <p>브라우저에서 02.fastapi-backend-v2.0을 직접 호출합니다.</p>
        <span class="go">바로가기 &rarr;</span>
      </RouterLink>
    </div>
  </div>
</template>
