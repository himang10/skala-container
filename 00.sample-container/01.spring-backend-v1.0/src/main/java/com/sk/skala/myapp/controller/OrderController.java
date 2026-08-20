package com.sk.skala.myapp.controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sk.skala.myapp.domain.Order;
import com.sk.skala.myapp.dto.OrderRequest;
import com.sk.skala.myapp.dto.OrderResponse;
import com.sk.skala.myapp.service.OrderService;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    // Order 엔티티 → OrderResponse 변환
    private OrderResponse toResponse(Order order) {
        return new OrderResponse(
                order.getId(),
                order.getUser().getId(),
                order.getUser().getName(),
                order.getProduct().getId(),
                order.getProduct().getName(),
                order.getQuantity(),
                order.getPayment().getAmount(),
                order.getStatus(),
                order.getStatus().getLabel(),
                order.getPayment().getStatus().getLabel(),
                order.getDelivery().getStatus().getLabel(),
                order.getOrderedAt()
        );
    }

    // GET /api/orders — 전체 주문 조회
    @GetMapping
    public List<OrderResponse> getAllOrders() {
        return orderService.getAllOrders().stream()
                .map(this::toResponse)
                .toList();
    }

    // GET /api/orders/{id} — 주문 단건 조회
    @GetMapping("/{id}")
    public OrderResponse getOrderById(@PathVariable Long id) {
        return orderService.getOrderById(id)
                .map(this::toResponse)
                .orElse(null);
    }

    // GET /api/orders/user/{userId} — 사용자별 주문 목록 조회
    @GetMapping("/user/{userId}")
    public List<OrderResponse> getOrdersByUserId(@PathVariable Long userId) {
        return orderService.getOrdersByUserId(userId).stream()
                .map(this::toResponse)
                .toList();
    }

    // POST /api/orders — 주문 생성 (사용자가 상품을 선택해 주문)
    @PostMapping
    public OrderResponse createOrder(@Valid @RequestBody OrderRequest request) {
        Order saved = orderService.createOrder(
                request.getProductId(),
                request.getUserId(),
                request.getQuantity(),
                request.getAddress());
        log.info("주문 생성: id={}, 상태={}", saved.getId(), saved.getStatus().getLabel());
        return toResponse(saved);
    }

    // POST /api/orders/{id}/next-status — 주문 상태를 다음 단계로 전환
    @PostMapping("/{id}/next-status")
    public OrderResponse advanceStatus(@PathVariable Long id) {
        Order order = orderService.advanceStatus(id);
        log.info("주문 상태 전환: id={}, 상태={}", order.getId(), order.getStatus().getLabel());
        return toResponse(order);
    }

    // DELETE /api/orders/{id} — 주문 삭제
    @DeleteMapping("/{id}")
    public void deleteOrder(@PathVariable Long id) {
        orderService.deleteOrder(id);
    }
}
