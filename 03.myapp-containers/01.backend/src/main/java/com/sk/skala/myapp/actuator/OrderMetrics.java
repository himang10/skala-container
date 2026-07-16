package com.sk.skala.myapp.actuator;

import com.sk.skala.myapp.domain.OrderStatus;
import com.sk.skala.myapp.domain.ProductStatus;
import com.sk.skala.myapp.repository.ProductRepository;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 주문 관련 커스텀 Actuator 메트릭 등록
 *
 * - Counter "shop.orders.total": 상태별 누적 주문 건수
 * - Gauge "shop.products.on_sale.count": 현재 ON_SALE 상품 수 (실시간)
 *
 * 접근 방법:
 *   GET /actuator/metrics/shop.orders.total
 *   GET /actuator/metrics/shop.products.on_sale.count
 */
@Slf4j
@Component
public class OrderMetrics {

    private final MeterRegistry registry;
    private final ProductRepository productRepository;

    public OrderMetrics(MeterRegistry registry, ProductRepository productRepository) {
        this.registry = registry;
        this.productRepository = productRepository;
    }

    /**
     * 애플리케이션 시작 시 Gauge 메트릭 등록
     * Counter는 incrementOrder() 호출 시 태그에 맞게 자동 생성됨
     */
    @PostConstruct
    public void init() {
        // Gauge: ON_SALE 상품 수를 실시간으로 측정
        Gauge.builder("shop.products.on_sale.count",
                        productRepository,
                        repo -> repo.findByStatus(ProductStatus.ON_SALE).size())
                .description("현재 판매 중인 상품 수")
                .register(registry);

        log.info("[OrderMetrics] 커스텀 Actuator 메트릭 등록 완료");
    }

    /**
     * 주문 상태별 Counter 증가 - OrderService에서 호출
     * @param status 변경된 주문 상태
     */
    public void incrementOrder(OrderStatus status) {
        Counter.builder("shop.orders.total")
                .tag("status", status.name())
                .description("상태별 누적 주문 건수")
                .register(registry)
                .increment();
    }
}
