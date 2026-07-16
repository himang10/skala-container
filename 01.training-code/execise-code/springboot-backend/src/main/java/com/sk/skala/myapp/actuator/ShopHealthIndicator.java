package com.sk.skala.myapp.actuator;

import java.util.LinkedHashMap;
import java.util.Map;

import com.sk.skala.myapp.domain.OrderStatus;
import com.sk.skala.myapp.domain.ProductStatus;
import com.sk.skala.myapp.repository.OrderRepository;
import com.sk.skala.myapp.repository.ProductRepository;

import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.stereotype.Component;

/**
 * 쇼핑몰 운영 상태 커스텀 Actuator 엔드포인트
 *
 * Spring Boot 4.x에서는 HealthIndicator 대신 @Endpoint + @ReadOperation으로 구현
 * 접근: GET /actuator/shophealth
 *
 * 점검 항목:
 *   1. ON_SALE 상품이 1개 이상 존재하는지
 *   2. PENDING 주문이 임계값(100건) 이하인지
 */
@Component
@Endpoint(id = "shophealth")
public class ShopHealthIndicator {

    private static final int PENDING_ORDER_THRESHOLD = 100;

    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;

    public ShopHealthIndicator(ProductRepository productRepository, OrderRepository orderRepository) {
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
    }

    @ReadOperation
    public Map<String, Object> health() {
        long onSaleCount = productRepository.findByStatus(ProductStatus.ON_SALE).size();
        long pendingCount = orderRepository.findByStatus(OrderStatus.PENDING).size();

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("onSaleProducts", onSaleCount);
        result.put("pendingOrders", pendingCount);

        boolean healthy = (onSaleCount > 0) && (pendingCount < PENDING_ORDER_THRESHOLD);
        result.put("status", healthy ? "UP" : "DOWN");

        if (onSaleCount == 0) {
            result.put("reason", "판매 중인 상품이 없습니다");
        } else if (pendingCount >= PENDING_ORDER_THRESHOLD) {
            result.put("reason", "미처리 주문이 임계값(" + PENDING_ORDER_THRESHOLD + "건)을 초과했습니다");
        }

        return result;
    }
}

