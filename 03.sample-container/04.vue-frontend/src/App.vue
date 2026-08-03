<script setup>
// 공통 상단 내비게이션 + 현재 라우트에 해당하는 화면(RouterView)을 렌더링한다.
// 03.frontend(정적 다중 페이지)와 달리, 여기서는 SPA 하나가 화면 전환을 담당한다.
</script>

<template>
  <header class="topbar">
    <div class="topbar-inner">
      <RouterLink class="logo" to="/">SK <span class="accent">AX</span> 주문 관리</RouterLink>
      <nav>
        <RouterLink to="/">홈</RouterLink>
        <RouterLink to="/products">상품 관리</RouterLink>
        <RouterLink to="/users">사용자 관리</RouterLink>
        <RouterLink to="/orders">주문 관리</RouterLink>
        <RouterLink to="/system">시스템 정보</RouterLink>
      </nav>
    </div>
  </header>

  <RouterView />
</template>
