package com.sk.skala.myapp.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.sk.skala.myapp.domain.Order;
import com.sk.skala.myapp.domain.OrderStatus;

public interface OrderRepository extends JpaRepository<Order, Long> {

    // 사용자별 전체 주문 목록 조회
    List<Order> findByUserId(Long userId);

    // 상태별 주문 목록 조회 (ShopHealthIndicator, OrderService 활용)
    List<Order> findByStatus(OrderStatus status);

    // 사용자 + 상태 복합 조회
    List<Order> findByUserIdAndStatus(Long userId, OrderStatus status);

    // 사용자별 주문 목록 페이징 조회 (실습 4)
    Page<Order> findByUserId(Long userId, Pageable pageable);

    // 사용자별 + 상태별 주문 목록 페이징 조회 (실습 4)
    Page<Order> findByUserIdAndStatus(Long userId, OrderStatus status, Pageable pageable);
}
