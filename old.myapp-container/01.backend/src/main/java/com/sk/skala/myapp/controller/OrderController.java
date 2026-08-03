package com.sk.skala.myapp.controller;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.sk.skala.myapp.common.ApiResponse;
import com.sk.skala.myapp.domain.Order;
import com.sk.skala.myapp.domain.OrderItem;
import com.sk.skala.myapp.domain.OrderStatus;
import com.sk.skala.myapp.dto.OrderItemResponse;
import com.sk.skala.myapp.dto.OrderRequest;
import com.sk.skala.myapp.dto.OrderResponse;
import com.sk.skala.myapp.service.OrderService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    // OrderItem 엔티티 → OrderItemResponse 변환
    private OrderItemResponse toItemResponse(OrderItem item) {
        return new OrderItemResponse(
                item.getId(),
                item.getProduct().getId(),
                item.getProduct().getName(),
                item.getQuantity(),
                item.getUnitPrice(),
                item.getSubtotal()
        );
    }

    // Order 엔티티 → OrderResponse 변환
    private OrderResponse toResponse(Order order) {
        List<OrderItemResponse> items = order.getOrderItems().stream()
                .map(this::toItemResponse)
                .toList();
        return new OrderResponse(
                order.getId(),
                order.getUser().getId(),
                order.getUser().getName(),
                order.getStatus(),
                order.getTotalPrice(),
                order.getOrderedAt(),
                items
        );
    }

    // POST /api/orders — 주문 생성
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<OrderResponse> createOrder(@Valid @RequestBody OrderRequest request) {
        Order saved = orderService.createOrder(request);
        log.info("주문 생성: orderId={}, userId={}, totalPrice={}", saved.getId(),
                saved.getUser().getId(), saved.getTotalPrice());
        return ApiResponse.of(toResponse(saved), "주문이 접수되었습니다");
    }

    // GET /api/orders/{id} — 주문 단건 조회
    @GetMapping("/{id}")
    public ApiResponse<OrderResponse> getOrderById(@PathVariable Long id) {
        return ApiResponse.of(toResponse(orderService.getOrderById(id)));
    }

    // GET /api/orders?userId={userId} — 사용자별 전체 주문 목록 조회
    @GetMapping
    public ApiResponse<List<OrderResponse>> getOrdersByUserId(@RequestParam Long userId) {
        List<OrderResponse> result = orderService.getOrdersByUserId(userId).stream()
                .map(this::toResponse)
                .toList();
        return ApiResponse.of(result);
    }

    // GET /api/orders/users/{userId} — 사용자별 주문 목록 페이징 조회 (실습 4)
    @GetMapping("/users/{userId}")
    public ApiResponse<Page<OrderResponse>> getOrdersByUserIdPaged(
            @PathVariable Long userId,
            @RequestParam(required = false) OrderStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        PageRequest pageable = PageRequest.of(page, size, Sort.by("orderedAt").descending());
        Page<OrderResponse> result = (status != null)
                ? orderService.getOrdersByUserIdAndStatus(userId, status, pageable).map(this::toResponse)
                : orderService.getOrdersByUserId(userId, pageable).map(this::toResponse);

        return ApiResponse.of(result);
    }

    // PUT /api/orders/{id}/cancel — 주문 취소 (PENDING 상태만 가능)
    @PutMapping("/{id}/cancel")
    public ApiResponse<OrderResponse> cancelOrder(@PathVariable Long id) {
        Order cancelled = orderService.cancelOrder(id);
        log.info("주문 취소: orderId={}", id);
        return ApiResponse.of(toResponse(cancelled), "주문이 취소되었습니다");
    }

    // PUT /api/orders/{id}/status — 주문 상태 변경
    // 요청 바디 예시: { "status": "CONFIRMED" }
    @PutMapping("/{id}/status")
    public ApiResponse<OrderResponse> updateOrderStatus(@PathVariable Long id,
                                                        @RequestBody Map<String, String> body) {
        OrderStatus newStatus = OrderStatus.valueOf(body.get("status"));
        Order updated = orderService.updateOrderStatus(id, newStatus);
        log.info("주문 상태 변경: orderId={}, status={}", id, newStatus);
        return ApiResponse.of(toResponse(updated), "주문 상태가 변경되었습니다");
    }
}
