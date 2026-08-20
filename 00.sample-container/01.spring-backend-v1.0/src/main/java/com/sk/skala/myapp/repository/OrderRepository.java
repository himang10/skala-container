package com.sk.skala.myapp.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sk.skala.myapp.domain.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {

    // 사용자별 주문 목록 조회
    List<Order> findByUserId(Long userId);
}
