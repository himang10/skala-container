package com.sk.skala.myapp.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sk.skala.myapp.actuator.OrderMetrics;
import com.sk.skala.myapp.domain.Order;
import com.sk.skala.myapp.domain.OrderItem;
import com.sk.skala.myapp.domain.OrderStatus;
import com.sk.skala.myapp.domain.Product;
import com.sk.skala.myapp.domain.ProductStatus;
import com.sk.skala.myapp.domain.User;
import com.sk.skala.myapp.dto.OrderItemRequest;
import com.sk.skala.myapp.dto.OrderRequest;
import com.sk.skala.myapp.event.OrderCompletedEvent;
import com.sk.skala.myapp.exception.BusinessException;
import com.sk.skala.myapp.exception.ErrorCode;
import com.sk.skala.myapp.repository.OrderRepository;
import com.sk.skala.myapp.repository.ProductRepository;
import com.sk.skala.myapp.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final ApplicationEventPublisher eventPublisher;  // 실습 5: 이벤트 발행
    private final OrderMetrics orderMetrics;                 // 실습 6: 커스텀 메트릭

    /**
     * 주문 생성
     * 1. 주문자 조회
     * 2. 각 항목별 상품 유효성 검증 + 재고 차감 (Product.@Version 낙관적 락 적용)
     * 3. Order, OrderItem 저장
     */
    @Transactional
    public Order createOrder(OrderRequest request) {
        // 주문자 조회
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND, "id=" + request.getUserId()));

        Order order = new Order();
        order.setUser(user);
        order.setStatus(OrderStatus.PENDING);
        order.setOrderedAt(LocalDateTime.now());

        int totalPrice = 0;

        for (OrderItemRequest itemRequest : request.getItems()) {
            // 상품 조회
            Product product = productRepository.findById(itemRequest.getProductId())
                    .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND,
                            "id=" + itemRequest.getProductId()));

            // 판매 중인 상품인지 확인
            if (product.getStatus() != ProductStatus.ON_SALE) {
                throw new BusinessException(ErrorCode.PRODUCT_NOT_ON_SALE, product.getName());
            }

            // 재고 확인
            if (product.getStockQuantity() < itemRequest.getQuantity()) {
                throw new BusinessException(ErrorCode.INSUFFICIENT_STOCK,
                        product.getName() + " 재고=" + product.getStockQuantity()
                        + ", 요청=" + itemRequest.getQuantity());
            }

            // 재고 차감 (@Version으로 낙관적 락 적용 - 동시 주문 시 충돌 감지)
            product.setStockQuantity(product.getStockQuantity() - itemRequest.getQuantity());
            productRepository.save(product);

            // 재고 소진 시 상태 자동 변경
            if (product.getStockQuantity() == 0) {
                product.setStatus(ProductStatus.SOLD_OUT);
            }

            // OrderItem 생성 - 주문 시점의 단가를 고정 저장
            int subtotal = product.getPrice() * itemRequest.getQuantity();
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(product);
            orderItem.setQuantity(itemRequest.getQuantity());
            orderItem.setUnitPrice(product.getPrice());
            orderItem.setSubtotal(subtotal);

            order.getOrderItems().add(orderItem);
            totalPrice += subtotal;
        }

        order.setTotalPrice(totalPrice);
        Order saved = orderRepository.save(order);

        // 실습 6: 주문 생성 메트릭 카운팅
        orderMetrics.incrementOrder(OrderStatus.PENDING);

        return saved;
    }

    // 주문 단건 조회
    public Order getOrderById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.ORDER_NOT_FOUND, "id=" + id));
    }

    // 전체 주문 목록 조회 (웹 UI용)
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    // 사용자별 전체 주문 목록 조회
    public List<Order> getOrdersByUserId(Long userId) {
        return orderRepository.findByUserId(userId);
    }

    // 사용자별 주문 목록 페이징 조회 (실습 4)
    public Page<Order> getOrdersByUserId(Long userId, Pageable pageable) {
        return orderRepository.findByUserId(userId, pageable);
    }

    // 사용자별 + 상태별 주문 목록 페이징 조회 (실습 4)
    public Page<Order> getOrdersByUserIdAndStatus(Long userId, OrderStatus status, Pageable pageable) {
        return orderRepository.findByUserIdAndStatus(userId, status, pageable);
    }

    /**
     * 주문 취소
     * - PENDING 상태에서만 취소 가능
     * - 각 항목의 재고를 원복한다
     */
    @Transactional
    public Order cancelOrder(Long orderId) {
        Order order = getOrderById(orderId);

        if (order.getStatus() != OrderStatus.PENDING) {
            throw new BusinessException(ErrorCode.ORDER_CANCEL_NOT_ALLOWED,
                    "현재 상태=" + order.getStatus());
        }

        // 재고 복원
        for (OrderItem item : order.getOrderItems()) {
            Product product = item.getProduct();
            product.setStockQuantity(product.getStockQuantity() + item.getQuantity());
            // 재고 복원 시 SOLD_OUT 상태였다면 ON_SALE로 되돌림
            if (product.getStatus() == ProductStatus.SOLD_OUT) {
                product.setStatus(ProductStatus.ON_SALE);
            }
            productRepository.save(product);
        }

        order.setStatus(OrderStatus.CANCELLED);
        Order saved = orderRepository.save(order);

        // 실습 6: 취소 메트릭 카운팅
        orderMetrics.incrementOrder(OrderStatus.CANCELLED);

        return saved;
    }

    /**
     * 주문 상태 변경
     * - 유효한 상태 전환만 허용: PENDING→CONFIRMED, CONFIRMED→SHIPPED, SHIPPED→DELIVERED
     * - CONFIRMED 전환 시 OrderCompletedEvent 발행 (실습 5)
     */
    @Transactional
    public Order updateOrderStatus(Long orderId, OrderStatus newStatus) {
        Order order = getOrderById(orderId);
        validateStatusTransition(order.getStatus(), newStatus);

        order.setStatus(newStatus);
        Order saved = orderRepository.save(order);

        // 실습 6: 상태 변경 메트릭 카운팅
        orderMetrics.incrementOrder(newStatus);

        // 실습 5: CONFIRMED 전환 시 이벤트 발행
        if (newStatus == OrderStatus.CONFIRMED) {
            eventPublisher.publishEvent(new OrderCompletedEvent(
                    saved.getId(),
                    saved.getUser().getId(),
                    saved.getUser().getName(),
                    saved.getTotalPrice(),
                    saved.getOrderedAt()
            ));
        }

        return saved;
    }

    // 유효한 상태 전환인지 검증
    private void validateStatusTransition(OrderStatus current, OrderStatus next) {
        boolean valid = switch (current) {
            case PENDING   -> next == OrderStatus.CONFIRMED;
            case CONFIRMED -> next == OrderStatus.SHIPPED;
            case SHIPPED   -> next == OrderStatus.DELIVERED;
            default        -> false;  // DELIVERED, CANCELLED 는 추가 전환 불가
        };
        if (!valid) {
            throw new BusinessException(ErrorCode.INVALID_ORDER_STATUS,
                    current + " → " + next + " 전환 불가");
        }
    }
}
